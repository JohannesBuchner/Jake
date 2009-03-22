package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.Account;
import com.jakeapp.core.domain.User;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.CoreChangedCallback;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.controls.SpinningWheelComponent;
import com.jakeapp.gui.swing.dialogs.AdvancedAccountSettingsDialog;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.SheetHelper;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import com.jakeapp.gui.swing.helpers.dragdrop.ProjectDropHandler;
import com.jakeapp.gui.swing.renderer.IconComboBoxRenderer;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.tasks.AbstractTask;
import com.jakeapp.gui.swing.worker.tasks.LoginAccountTask;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.animation.timing.Animator;
import org.jdesktop.animation.timing.interpolation.PropertySetter;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.jxlayer.JXLayer;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.hyperlink.LinkAction;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The Userpanel creates accouts for Jake.
 *
 * @author studpete
 */
public class UserPanel extends JXPanel
				implements RegistrationStatus, CoreChangedCallback, ContextChangedCallback {
	private ResourceMap resourceMap;
	private static final Logger log = Logger.getLogger(UserPanel.class);

	private javax.swing.JRadioButton registerRadioButton;
	private javax.swing.JRadioButton loginRadioButton;
	private javax.swing.ButtonGroup loginRegisterButtonGroup;

	private JPanel loginSuccessPanel;
	private javax.swing.JPanel registrationInfoPanel;
	private JButton signInRegisterButton;
	private UserDataPanel loginUserDataPanel;
	private UserDataPanel registerUserDataPanel;
	private JComboBox loginServiceCheckBox;
	private SpinningWheelComponent workingAnimation;
	private Map<SupportedServices, Account> creds =
					new HashMap<SupportedServices, Account>();
	private JPanel addUserPanel;
	private JPanel loginUserPanel;
	private JPanel loadingAppPanel;

	private ImageIcon jakeWelcomeIcon = new ImageIcon(
					JakeMainView.getMainView().getLargeAppImage().getScaledInstance(90, 90,
									Image.SCALE_SMOOTH));
	private JLabel userLabelLoginSuccess;
	private JPanel userListPanel;
	private JButton signInRegisterBackBtn;


	@Override public void coreChanged() {
		// called after statup, when core init is done.

		// one-time-init of predefined credentials
		if (JakeContext.isCoreInitialized() && creds.size() == 0) {
			initPredefinedCredentials();
		}

		updateView();
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		if (reason.contains(Reason.MsgService)) {
			updateView();
		}
	}

	/**
	 * SupportedServices; we add some default support for common services.
	 */
	private enum SupportedServices {
		Google, Jabber, UnitedInternet
	}

	/**
	 * The three user panels supported.
	 */
	private enum UserPanels {
		AddUser, ManageUsers, LoggedIn, LoadingApplication
	}

	/**
	 * Create the User Panel.
	 */
	public UserPanel() {
		setResourceMap(org.jdesktop.application.Application
						.getInstance(com.jakeapp.gui.swing.JakeMainApp.class)
						.getContext().getResourceMap(UserPanel.class));

		initComponents();

		JakeMainApp.getInstance().addCoreChangedListener(this);

		// device which panel to show!
		updateView();
	}

	private void initPredefinedCredentials() {
		for (SupportedServices service : SupportedServices.values()) {
			creds.put(service, JakeMainApp.getCore().getPredefinedServiceCredential(
							service.toString()));
		}
	}

	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}


	private void initComponents() {
		this.setLayout(new MigLayout("wrap 1, fill, center, ins 15"));

		// initialize various panels
		addUserPanel = createAddUserPanel();
		loginUserPanel = createChooseUserPanel();
		loginSuccessPanel = createSignInSuccessPanel();
		loadingAppPanel = createLoadingAppPanel();

		// set the background painter
		this.setBackgroundPainter(Platform.getStyler().getLoginBackgroundPainter());

		EventCore.get().addContextChangedListener(this);
	}

	private JPanel createLoadingAppPanel() {
		JPanel loader = new JXPanel(new MigLayout("wrap 1, fill, center, ins 0"));
		loader.setOpaque(false);

		// the say hello heading
		JXPanel jakeLogoContainer =
						new JXPanel(new MigLayout("wrap 1, fill, center, ins 0"));
		JLabel jakeLogo = new JLabel();
		ImageIcon jakeLogoImage =
						ImageLoader.get(getClass(), "/icons/jakeapp-large.png");
		jakeLogo.setIcon(jakeLogoImage);
		jakeLogoContainer.setOpaque(false);
		jakeLogoContainer.add(jakeLogo, "center");
		loader.add(jakeLogoContainer, "center");
		jakeLogoContainer.setAlpha(1f);

		PropertySetter modifier = new PropertySetter(jakeLogoContainer, "alpha", 1f, 0f);
		Animator timer = new Animator(10000, modifier);
		timer.start();

		SpinningWheelComponent inidcator = new SpinningWheelComponent();
		inidcator.startAnimation();
		loader.add(inidcator, "center");
		return loader;
	}

	/**
	 * Creates the Login User Panel
	 *
	 * @return
	 */
	private JPanel createChooseUserPanel() {
		// create the user login panel
		JPanel loginUserPanel = new JPanel(new MigLayout("wrap 1, fill, center, ins 0"));
		loginUserPanel.setOpaque(false);
		loginUserPanel.setBorder(null);

		// the say hello heading
		JPanel titlePanel =
						createJakeTitle(getResourceMap().getString("selectUserLabel"));
		loginUserPanel.add(titlePanel, "wrap, gapbottom 20, top, growx, h 80!");

		// add link to create new user
		JButton createAccountBtn = new JButton(getResourceMap().getString("addUserBtn"));
		createAccountBtn.putClientProperty("JButton.buttonType", "textured");
		createAccountBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				showPanel(UserPanels.AddUser);
			}
		});

		// TODO: show how many projects a user has
		// create the user list
		userListPanel = new JPanel(new MigLayout("wrap 1, filly, center, ins 0"));
		userListPanel.setOpaque(false);
		userListPanel.setBorder(null);
		JScrollPane usersScrollPanel = new JScrollPane(userListPanel);
		usersScrollPanel.setOpaque(false);
		usersScrollPanel.getViewport().setOpaque(false);
		usersScrollPanel.setBorder(null);

		updateChooseUserPanel();

		loginUserPanel.add(usersScrollPanel, "grow");

		loginUserPanel.add(createAccountBtn, "wrap, center");

		return loginUserPanel;
	}

	private JPanel createJakeTitle(String message) {
		JPanel titlePanel = new JPanel(new MigLayout("nogrid, fillx, top, ins 0"));
		titlePanel.setOpaque(false);
		JLabel selectUserLabel = new JLabel(message);
		selectUserLabel.setIcon(jakeWelcomeIcon);
		selectUserLabel.setVerticalTextPosition(JLabel.TOP);
		titlePanel.add(selectUserLabel, "top, center, h 80!");
		return titlePanel;
	}


	private void updateChooseUserPanel() {
		userListPanel.removeAll();
		log.debug("updateChooseUserPanel removed all!");

		if (JakeContext.isCoreInitialized()) {
			try {
				List<MsgService<User>> msgs = JakeMainApp.getCore().getMsgServices();

				if (msgs != null) {
					log.debug("updateChooseUserPanel will display n MsgServices, n=" + msgs
									.size());
					for (MsgService<User> msg : msgs) {
						UserControlPanel userPanel = new UserControlPanel(msg);
						JXLayer<UserControlPanel> userLayer =
										new JXLayer<UserControlPanel>(userPanel);
						userListPanel.add(userLayer);
					}
				}
			} catch (FrontendNotLoggedInException e) {
				ExceptionUtilities.showError(e);
			}
		}
	}

	/**
	 * Creates the Add User Panel
	 *
	 * @return
	 */
	private JPanel createAddUserPanel() {
		log.trace("creating add user panel...");

		// create the add user panel
		JPanel addUserPanel = new JPanel(new MigLayout("wrap 1, filly, center, ins 0"));
		addUserPanel.setOpaque(false);

		// the say hello heading
		JPanel titlePanel = new JPanel(new MigLayout("nogrid, fill, top, ins 0"));
		titlePanel.setOpaque(false);
		JLabel createAccountLabel =
						new JLabel(getResourceMap().getString("loginMessageLabel"));
		createAccountLabel.setIcon(jakeWelcomeIcon);
		createAccountLabel.setVerticalTextPosition(JLabel.TOP);
		titlePanel.add(createAccountLabel, "top, center, h 80!");
		addUserPanel.add(titlePanel, "wrap, gapbottom 20, top, grow, h 80:300");

		// login existing with service
		loginRadioButton =
						new JRadioButton(getResourceMap().getString("loginRadioButton"));
		loginRadioButton.setOpaque(false);
		loginRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSignInRegisterMode();
			}
		});
		loginRadioButton.setSelected(true);

		// register new
		registerRadioButton =
						new JRadioButton(getResourceMap().getString("registerRadioButton"));
		registerRadioButton.setOpaque(false);
		registerRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSignInRegisterMode();
			}
		});

		loginRegisterButtonGroup = new ButtonGroup();
		loginRegisterButtonGroup.add(registerRadioButton);
		loginRegisterButtonGroup.add(loginRadioButton);

		// login service
		String[] loginServices =
						new String[]{"Google Talk", "Jabber", "United Internet (GMX, Web.de)"};
		Integer[] indexes = new Integer[]{0, 1, 2};
		ImageIcon[] images = new ImageIcon[3];
		images[0] = ImageLoader.getScaled(getClass(), "/icons/service-google.png", 16);
		images[1] = ImageLoader.getScaled(getClass(), "/icons/service-jabber.png", 16);
		images[2] = ImageLoader
						.getScaled(getClass(), "/icons/service-unitedinternet.png", 16);
		loginServiceCheckBox = new JComboBox();
		loginServiceCheckBox.setModel(new DefaultComboBoxModel(indexes));
		IconComboBoxRenderer renderer = new IconComboBoxRenderer(images, loginServices);
		loginServiceCheckBox.setRenderer(renderer);
		loginServiceCheckBox.setEditable(false);
		loginServiceCheckBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateLoginUsernameLabel();
			}
		});

		// add to user panel
		addUserPanel.add(loginRadioButton, "split");
		addUserPanel.add(loginServiceCheckBox, "wrap");
		loginUserDataPanel = new UserDataPanel(false);
		updateLoginUsernameLabel();
		addUserPanel.add(loginUserDataPanel, "hidemode 1");

		// add the register radio button
		registerUserDataPanel = new UserDataPanel(true);
		addUserPanel.add(registerRadioButton, "");

		addUserPanel.add(registerUserDataPanel, "hidemode 1");

		JPanel addUserButtonPanel = new JPanel(new MigLayout("wrap 2, fill, ins 0"));
		addUserButtonPanel.setOpaque(false);

		workingAnimation = new SpinningWheelComponent();
		addUserButtonPanel.add(workingAnimation, "hidemode 1, left");

		signInRegisterButton = new JButton();
		signInRegisterButton.putClientProperty("JButton.buttonType", "textured");
		signInRegisterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signInRegisterButtonPressed();
			}
		});

		// add back button if there are users
		try {
			signInRegisterBackBtn = new JButton(getResourceMap().getString("backBtn"));
			signInRegisterBackBtn.putClientProperty("JButton.buttonType", "textured");
			signInRegisterBackBtn.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					showPanel(UserPanels.ManageUsers);
				}
			});
			addUserButtonPanel.add(signInRegisterBackBtn, "left, bottom, split");
		} catch (FrontendNotLoggedInException e) {
			ExceptionUtilities.showError(e);
		}

		addUserButtonPanel.add(signInRegisterButton, "right, bottom, wrap");


		addUserPanel.add(addUserButtonPanel, "width 370!");
		return addUserPanel;
	}


	/**
	 * Updates internal saved ServiceCredentials and return object.
	 *
	 * @return
	 */
	private Account getCredentials() {
		Account cred;
		String user, password;

		if (!isModeSignIn()) {
			// return the default set
			cred = creds.get(SupportedServices.Jabber);
			cred.setServerAddress(registerUserDataPanel.getServer());
			user = registerUserDataPanel.getUserName() + '@' + registerUserDataPanel
							.getServer();
			password = registerUserDataPanel.getPassword();
		} else {
			cred = creds.get(
							SupportedServices.values()[loginServiceCheckBox.getSelectedIndex()]);
			user = loginUserDataPanel.getUserName();
			password = loginUserDataPanel.getPassword();
		}

		cred.setUserId(user);
		cred.setPlainTextPassword(password);

		return cred;
	}

	/**
	 * Action that is called when Button Sign In / Register is pressed.
	 */
	public void signInRegisterButtonPressed() {
		log.info("Sign In / Registering (isSignIn=" + isModeSignIn());

		if (isSignInRegisterButtonEnabled()) {

			if (isModeSignIn()) {
				signInRegisterButton.setEnabled(false);
				try {
					// sync call
					MsgService msg = JakeMainApp.getCore().addAccount(getCredentials());
					JakeContext.setMsgService(msg);

					// prepare the servicecredentials (prefilled?)
					Account creds = getCredentials();
					creds.setAutologin(loginUserDataPanel.isSetRememberPassword());
					//creds.setPlainTextPassword(loginUserDataPanel.getPassword());

					JakeExecutor.exec(new LoginAccountTask(msg, creds,
									EventCore.get().getLoginStateListener()));

				} catch (Exception e) {
					log.warn(e);
					ExceptionUtilities.showError(e);
				} finally {
					updateView();
				}
			} else {
				JakeExecutor.exec(new RegisterAccountTask(getCredentials()));
				updateView();
				// fixme: what do do? events?
			}
		} else {
			log.warn("Sign In tried while button was not enabled!");
		}
	}


	/**
	 * Private inner worker for account registration.
	 */
	private class RegisterAccountTask extends AbstractTask<Void> {
		private Account cred;

		private RegisterAccountTask(Account cred) {
			this.cred = cred;
		}

		@Override
		protected AvailableLaterObject<Void> calculateFunction() {
			workingAnimation.startAnimation();

			try {
				return JakeMainApp.getCore().createAccount(cred);
			} catch (FrontendNotLoggedInException e) {
				log.warn(e);
				ExceptionUtilities.showError(e);
			} catch (InvalidCredentialsException e) {
				log.warn(e);
				ExceptionUtilities.showError(e);
			} catch (ProtocolNotSupportedException e) {
				log.warn(e);
				ExceptionUtilities.showError(e);
			} catch (NetworkException e) {
				log.warn(e);
				ExceptionUtilities.showError(e);
			}

			workingAnimation.stopAnimation();
			return null;
		}

		@Override
		protected void onDone() {
			workingAnimation.stopAnimation();
			updateView();
		}
	}


	/**
	 * Updates the login username in representation to selected service.
	 */
	private void updateLoginUsernameLabel() {
		if (loginServiceCheckBox.getSelectedIndex() == SupportedServices.Google
						.ordinal()) {
			loginUserDataPanel.setUserLabel("usernameGoogle");
		} else if (loginServiceCheckBox.getSelectedIndex() == SupportedServices.Jabber
						.ordinal()) {
			loginUserDataPanel.setUserLabel("usernameJabber");
		} else {
			loginUserDataPanel.setUserLabel("usernameUInternet");
		}
	}


	/**
	 * Creates User/Password Field for entering credentials
	 *
	 * @return
	 */
	private class UserDataPanel extends JPanel {
		private JTextField userName;
		private JTextField passName;
		private JComboBox serverComboBox;
		private JCheckBox rememberPassCheckBox;
		private JLabel userLabel;

		public UserDataPanel(boolean addServer) {
			this.setLayout(new MigLayout("wrap 1, fill"));
			this.setOpaque(false);

			// add server
			if (addServer) {
				// fill the registraton info panel
				registrationInfoPanel = new JPanel(new MigLayout("wrap 2, ins 0"));
				JLabel registrationLabel1 =
								new JLabel(getResourceMap().getString("registrationLabel1"));
				registrationLabel1.setForeground(Color.DARK_GRAY);
				registrationInfoPanel.add(registrationLabel1, "span 2, wrap");
				JLabel registrationLabel2 =
								new JLabel(getResourceMap().getString("registrationLabel2"));
				registrationLabel2.setForeground(Color.DARK_GRAY);
				registrationInfoPanel.add(registrationLabel2);
				LinkAction linkAction =
								new LinkAction(getResourceMap().getString("registrationLabel3")) {
									public void actionPerformed(ActionEvent e) {
										try {
											Desktop.getDesktop().browse(new URI(getResourceMap().getString(
															"registrationLabelHyperlink")));
										} catch (IOException e1) {
											e1.printStackTrace();
										} catch (URISyntaxException e1) {
											e1.printStackTrace();
										}
										setVisited(true);
									}
								};
				registrationInfoPanel.add(new JXHyperlink(linkAction), "gapbottom 10");
				registrationInfoPanel.setOpaque(false);
				this.add(registrationInfoPanel);

				JLabel serverLabel = new JLabel(getResourceMap().getString("serverLabel"));
				serverLabel.setForeground(Color.DARK_GRAY);
				serverComboBox = new JComboBox();
				serverComboBox.setModel(new DefaultComboBoxModel(
								new String[]{"jabber.fsinf.at", "jabber.org", "jabber.ccc.de",
												"macjabber.de", "swissjabber.ch", "binaryfreedom.info"}));
				serverComboBox.setEditable(true);

				this.add(serverLabel, "");
				this.add(serverComboBox, "width 350!");
			}
			userLabel = new JLabel(getResourceMap().getString("usernameLabel"));
			userLabel.setForeground(Color.DARK_GRAY);
			this.add(userLabel);

			userName = new JTextField();
			this.add(userName, "width 350!");

			JLabel passLabel = new JLabel(getResourceMap().getString("passwordLabel"));
			passLabel.setForeground(Color.DARK_GRAY);
			this.add(passLabel);

			passName = new JPasswordField();
			passName.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					signInRegisterButtonPressed();
				}
			});
			this.add(passName, "width 350!");

			rememberPassCheckBox =
							new JCheckBox(getResourceMap().getString("rememberPasswordCheckBox"));
			rememberPassCheckBox.setSelected(true);
			rememberPassCheckBox.setOpaque(false);
			this.add(rememberPassCheckBox, addServer ? "" : "split");

			DocumentListener dl = new DocumentListener() {
				public void insertUpdate(DocumentEvent documentEvent) {
					updateSignInRegisterModeButtons();
				}

				public void removeUpdate(DocumentEvent documentEvent) {
					updateSignInRegisterModeButtons();
				}

				public void changedUpdate(DocumentEvent documentEvent) {
					updateSignInRegisterModeButtons();
				}
			};

			// instlal event listener for password text field
			userName.getDocument().addDocumentListener(dl);
			passName.getDocument().addDocumentListener(dl);

			if (!addServer) {
				// Advanced Settings
				JButton loginAdvancedBtn =
								new JButton(getResourceMap().getString("advancedServerButton"));
				loginAdvancedBtn.putClientProperty("JButton.buttonType", "textured");
				loginAdvancedBtn.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						AdvancedAccountSettingsDialog.showDialog(getCredentials());
						log.debug("credentials now: " + getCredentials()
										.getServerAddress() + getCredentials().getServerPort());
					}
				});
				this.add(loginAdvancedBtn, "wrap");
			}
		}

		/**
		 * Get Username from internal TextField.
		 *
		 * @return
		 */
		public String getUserName() {
			return userName.getText();
		}

		/**
		 * Get Password from internal TextField.
		 *
		 * @return
		 */
		public String getPassword() {
			return passName.getText();
		}

		/**
		 * Get selected server string from JComboBox.
		 *
		 * @return
		 */
		public String getServer() {
			return serverComboBox != null ? serverComboBox.getSelectedItem().toString() :
							null;
		}

		/**
		 * Save password?
		 *
		 * @return
		 */
		public boolean isSetRememberPassword() {
			return rememberPassCheckBox.isSelected();
		}

		/**
		 * Set the user label text.
		 * translates the string.
		 *
		 * @param str
		 */
		public void setUserLabel(String str) {
			userLabel.setText(getResourceMap().getString(str));
		}
	}

	/**
	 * Create the success panel for correct user adding.
	 * This Panel has Drag&Drop-Abilities.
	 *
	 * @return
	 */
	private JPanel createSignInSuccessPanel() {

		// create the drag & drop hint
		JPanel loginSuccessPanel = new JPanel();
		loginSuccessPanel.setTransferHandler(new ProjectDropHandler());
		loginSuccessPanel.setOpaque(false);
		loginSuccessPanel.setLayout(new MigLayout("nogrid, al center, fill"));

		JLabel headerLoginSuccess =
						new JLabel(getResourceMap().getString("signInSuccessHeader"));
		headerLoginSuccess.setFont(Platform.getStyler().getH1Font());
		headerLoginSuccess.setForeground(Color.WHITE);
		loginSuccessPanel.add(headerLoginSuccess, "top, center, wrap");

		userLabelLoginSuccess = new JLabel();
		userLabelLoginSuccess.setFont(Platform.getStyler().getH1Font());
		loginSuccessPanel.add(userLabelLoginSuccess, "top, center, wrap");

		// the sign out button
		JButton signOutButton =
						new JButton(getResourceMap().getString("signInSuccessSignOut"));
		signOutButton.putClientProperty("JButton.buttonType", "textured");
		signOutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {

				// TODO: more control over login state
				//if (MsgServiceHelper.isCurrentUserLoggedIn()) {
				try {
					JakeMainApp.logoutUser();
					updateView();
				} catch (Exception e) {
					log.warn(e);
					ExceptionUtilities.showError(e);
				}
			}
			//else {
			//ExceptionUtilities.showError("No user is logged in!");
			//}
		});

		loginSuccessPanel.add(signOutButton, "wrap, top, center, gapbottom 25");

		JLabel iconSuccess = new JLabel();
		iconSuccess.setIcon(ImageLoader.get(getClass(), "/icons/dropfolder.png"));

		loginSuccessPanel.add(iconSuccess, "wrap, al center");

		JLabel messageSuccess1 = new JLabel(getResourceMap().getString("dragDropHint1"));
		messageSuccess1.setFont(Platform.getStyler().getH1Font());
		messageSuccess1.setForeground(Color.DARK_GRAY);
		loginSuccessPanel.add(messageSuccess1, "wrap, al center");

		JLabel messageSuccess2 = new JLabel(getResourceMap().getString("dragDropHint2"));
		messageSuccess2.setFont(Platform.getStyler().getH1Font());
		messageSuccess2.setForeground(Color.DARK_GRAY);
		loginSuccessPanel.add(messageSuccess2, "al center");

		updateSignInSuccessPanel();

		return loginSuccessPanel;
	}

	private void updateSignInSuccessPanel() {
		if (JakeContext.getMsgService() != null) {
			userLabelLoginSuccess
							.setText(JakeContext.getMsgService().getUserId().getUserId());
		}
	}

	private boolean isModeSignIn() {
		return loginRadioButton.isSelected();
	}

	private boolean isSignInRegisterButtonEnabled() {
		if (isModeSignIn()) {
			return (loginUserDataPanel.getUserName().length() > 0 && loginUserDataPanel
							.getPassword().length() > 0);
		} else {
			return (registerUserDataPanel.getUserName()
							.length() > 0 && registerUserDataPanel.getPassword().length() > 0);

		}
	}

	private void updateSignInRegisterMode() {
		signInRegisterBackBtn.setVisible(
						JakeContext.isCoreInitialized() && JakeMainApp.getCore().getMsgServices()
										.size() > 0);

		loginUserDataPanel.setVisible(isModeSignIn());
		registerUserDataPanel.setVisible(!isModeSignIn());
		loginServiceCheckBox.setEnabled(isModeSignIn());


		if (isModeSignIn()) {
			signInRegisterButton.setText(getResourceMap().getString("loginSignIn"));
		} else {
			signInRegisterButton.setText(getResourceMap().getString("loginRegister"));
		}
		updateSignInRegisterModeButtons();
	}

	private void updateSignInRegisterModeButtons() {// disable the button as long as no credidentals are entered
		signInRegisterButton.setEnabled(isSignInRegisterButtonEnabled());
	}

	/**
	 * Updates the main view.
	 * If there is a user registered, show dragdrop screen.
	 * If not, show the add user screen.
	 */
	private void updateView() {
		log.trace("updating login view. selected user: " + JakeContext.getMsgService());

		// always update everything
		updateSignInSuccessPanel();
		updateChooseUserPanel();
		updateSignInRegisterMode();

		// update the view (maybe already logged in)
		if (JakeContext.isCoreInitialized()) {
			if (JakeContext.getMsgService() != null) {
				showPanel(UserPanels.LoggedIn);
			} else {
				if (JakeMainApp.getCore().getMsgServices().size() > 0) {
					showPanel(UserPanels.ManageUsers);
				} else {
					showPanel(UserPanels.AddUser);
				}
			}
		} else {
			showPanel(UserPanels.LoadingApplication);
		}
	}

	/**
	 * Set which panel will be shown
	 *
	 * @param panel
	 */

	private void showPanel(UserPanels panel) {
		log.trace("show panel: " + panel);
		showContentPanel(addUserPanel, panel == UserPanels.AddUser);
		showContentPanel(loginUserPanel, panel == UserPanels.ManageUsers);
		showContentPanel(loginSuccessPanel, panel == UserPanels.LoggedIn);
		showContentPanel(loadingAppPanel, panel == UserPanels.LoadingApplication);
	}


	/**
	 * Helper for showPanel; add or remove certain panel at runtime.
	 *
	 * @param panel
	 * @param show
	 */
	private void showContentPanel(JPanel panel, boolean show) {
		if (show) {
			this.add(panel, "grow");
		} else {
			this.remove(panel);
		}
		this.updateUI();
	}


	public void setRegistrationStatus(final RegisterStati status, final String msg) {
		log.info("got registration status update: " + status);

		Runnable runner = new Runnable() {
			public void run() {

				updateView();

				// animation is controlled via swingworker

				if (status == RegisterStati.RegistrationActive) {
					signInRegisterButton
									.setText(getResourceMap().getString("loginRegisterProceed"));
					signInRegisterButton.setEnabled(false);
				}
			}
		};

		SwingUtilities.invokeLater(runner);
	}


	/*
	public void setConnectionStatus(final ConnectionStati status, final String msg) {
		log.info("got connection status update: " + status);

		Runnable runner = new Runnable() {
			public void run() {

				// always update view
				updateView();

				if (status == ConnectionStati.SigningIn) {
					signInRegisterButton
									.setText(getResourceMap().getString("loginSignInProceed"));
					signInRegisterButton.setEnabled(false);
				}
			}
		};

		SwingUtilities.invokeLater(runner);
	}*/

	/**
	 * Create User Panel
	 */
	private class UserControlPanel extends JXPanel {
		private final MsgService<User> msg;
		private JPasswordField passField;
		private JCheckBox rememberPassCheckBox;
		private final static String MagicPassToken = "%MAGIC%";
		private JButton signInBtn;

		public UserControlPanel(final MsgService<User> msg) {
			log.info("creating UserControlPanel with " + msg + ", userID: " + msg
							.getUserId());
			this.msg = msg;

			ActionListener loginAction = new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					try {
						log.info("Sign In with " + msg
										.getUserId() + " useSavedPassword: " + isMagicToken());
						signInBtn.setEnabled(false);
						JakeContext.setMsgService(msg);

						// prepare the servicecredentials (prefilled?)
						Account creds = getCredentials();
						creds.setAutologin(isRememberPassword());
						if (isMagicToken()) {
							creds.setPlainTextPassword(null);
						} else {
							creds.setPlainTextPassword(getPassword());
						}

						JakeExecutor.exec(new LoginAccountTask(msg, creds,
										EventCore.get().getLoginStateListener()));

						updateView();
					}

					catch (Exception e1){
						ExceptionUtilities.showError(e1);
					}
				}
			};

			this.setBackgroundPainter(Platform.getStyler().getUserBackgroundPainter());

			this.setLayout(new MigLayout("wrap 2, fill"));
			this.setOpaque(false);

			String msgUserId = msg.getUserId().getUserId();

			JLabel userLabel =
							new JLabel(StringUtilities.htmlize("<b>" + msgUserId + "</b>"));
			this.add(userLabel, "span 2, gapbottom 8");

			JLabel passLabel =
							new JLabel(getResourceMap().getString("passwordLabel") + ":");
			this.add(passLabel, "left");

			passField = new JPasswordField();

			passField.addActionListener(loginAction);
			this.add(passField, "w 200!");

			rememberPassCheckBox =
							new JCheckBox(getResourceMap().getString("rememberPasswordCheckBox")

							);
			rememberPassCheckBox.setSelected(true);
			rememberPassCheckBox.setOpaque(false);
			rememberPassCheckBox.addActionListener(new

							ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									updateUserPanel();
								}
							}

			);
			this.add(rememberPassCheckBox, "span 2");

			JButton deleteUserBtn = new JButton(getResourceMap().getString("deleteUser"));
			deleteUserBtn.putClientProperty("JButton.buttonType", "textured");
			deleteUserBtn.addActionListener(new

							ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									if (SheetHelper.showConfirm(
													"Really delete your Account? All your Projects will be deleted. " + "\n(But don't worry, we won't delete your Files)",
													"Delete Account")) {
										try {
											JakeMainApp.getCore().removeAccount(msg);
											updateView();
										} catch (Exception ex) {
											ExceptionUtilities.showError(ex);
										}
									}
								}
							});
			this.add(deleteUserBtn, "left, bottom");

			signInBtn = new JButton(getResourceMap().getString("loginSignInOnly"));
			signInBtn.putClientProperty("JButton.buttonType", "textured");
			signInBtn.addActionListener(loginAction);
			this.add(signInBtn, "right, bottom");

			// if a password is set, write a magic token into password field
			// to represent the "not changed" state
			log.info("msg.isPasswordSaved: " + msg.isPasswordSaved() + " for " + msg
							.getUserId());
			if (msg.isPasswordSaved()) {
				passField.setText(MagicPassToken);
			} else {
				// else clear field
				passField.setText("");
			}

			rememberPassCheckBox.setSelected(msg.isPasswordSaved());
		}


		/**
		 * Disables the Password Field if Password is saved.
		 */

		private void updateUserPanel() {
			if (isMagicToken() && isRememberPassword()) {
				passField.setEditable(false);
			} else {
				passField.setEditable(true);
			}

			// remove magic token if checkbox is removed
			if (isMagicToken() && !isRememberPassword()) {
				passField.setText("");
			}
		}

		private boolean isMagicToken() {
			return getPassword().compareTo(MagicPassToken) == 0;
		}

		/**
		 * En/Disables the controls.
		 * (Should be disabled, when logging in)
		 *
		 * @param enable
		 */
		public void enableControls(boolean enable) {
			passField.setEnabled(enable);
			updateUserPanel();
		}

		public boolean isRememberPassword() {
			return rememberPassCheckBox.isSelected();
		}

		public String getPassword() {
			return passField.getText();
		}
	}
}
