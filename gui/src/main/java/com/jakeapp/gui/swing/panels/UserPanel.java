package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.ServiceCredentials;
import com.jakeapp.core.domain.exceptions.InvalidCredentialsException;
import com.jakeapp.core.domain.exceptions.NotLoggedInException;
import com.jakeapp.core.services.exceptions.ProtocolNotSupportedException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.RegistrationStatus;
import com.jakeapp.gui.swing.controls.JAsynchronousProgressIndicator;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.renderer.IconComboBoxRenderer;
import com.jakeapp.gui.swing.worker.SwingWorkerWithAvailableLaterObject;
import com.jakeapp.jake.ics.exceptions.NetworkException;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXHyperlink;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.hyperlink.LinkAction;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * The Userpanel creates accouts for Jake.
 *
 * @author: studpete
 */
public class UserPanel extends JXPanel implements RegistrationStatus, ConnectionStatus {
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
	private JAsynchronousProgressIndicator workingAnimation;

	private enum SupportedServices {
		Google, Jabber
	}

	/**
	 * Create the User Panel.
	 */
	public UserPanel() {

		setResourceMap(org.jdesktop.application.Application.getInstance(
				  com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(UserPanel.class));

		initComponents();

		// register the connection & reg status callback!
		JakeMainApp.getApp().getCore().addConnectionStatusCallbackListener(this);
		JakeMainApp.getApp().getCore().addRegistrationStatusCallbackListener(this);
	}

	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}


	private void initComponents() {
		this.setLayout(new MigLayout("wrap 2, fill, center"));

		// create the add user panel
		JPanel addUserPanel = new JPanel(new MigLayout("wrap 1, filly, center"));
		addUserPanel.setOpaque(false);

		// the say hello heading
		JLabel createAccountLabel = new JLabel(getResourceMap().getString("loginMessageLabel"));
		createAccountLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/jakewelcome.png"))));
		addUserPanel.add(createAccountLabel, "wrap, gapbottom 30, top, grow, h 60:300");
		createAccountLabel.setVerticalTextPosition(JLabel.TOP);

		// login existing with service
		loginRadioButton = new JRadioButton(getResourceMap().getString("loginRadioButton"));
		loginRadioButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				updateSignInRegisterMode();
			}
		});
		loginRadioButton.setSelected(true);

		// register new
		registerRadioButton = new JRadioButton(getResourceMap().getString("registerRadioButton"));
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
		String[] loginServices = new String[]{"Google Talk", "Jabber"};
		Integer[] indexes = new Integer[]{new Integer(0), new Integer(1)};
		ImageIcon[] images = new ImageIcon[2];
		images[0] = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/service-google.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
		images[1] = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/service-jabber.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
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

		JPanel buttonPanel = new JPanel(new MigLayout("wrap 2, fill, ins 0"));
		buttonPanel.setOpaque(false);

		workingAnimation = new JAsynchronousProgressIndicator();
		//workingAnimation.startAnimation();
		buttonPanel.add(workingAnimation, "hidemode 1, left");

		signInRegisterButton = new JButton();
		signInRegisterButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				signInRegisterButtonPressed();
			}
		});
		updateSignInRegisterMode();
		buttonPanel.add(signInRegisterButton, "right, bottom");


		addUserPanel.add(buttonPanel, "width 320!");

		// add the add user panel
		this.add(addUserPanel, "grow, center");

		// set the background painter
		this.setBackgroundPainter(Platform.getStyler().getContentPanelBackgroundPainter());

		// TODO
		loginSuccessPanel = createSignInSuccessPanel();
	}

	public void signInRegisterButtonPressed() {
		if (isSignInRegisterButtonEnabled()) {

			if (isModeSignIn()) {
				ServiceCredentials cred = new ServiceCredentials(
						  loginUserDataPanel.getUserName(), loginUserDataPanel.getPassword());

				try {
					JakeMainApp.getCore().addAccount(cred);
				} catch (NotLoggedInException e) {
					e.printStackTrace();
				} catch (InvalidCredentialsException e) {
					e.printStackTrace();
				} catch (ProtocolNotSupportedException e) {
					e.printStackTrace();
				} catch (NetworkException e) {
					e.printStackTrace();
				}

				/*
									 JakeMainApp.getApp().getCore().signIn(
												usernameComboBox.getSelectedItem().toString(),
												passwordField.getPassword().toString());*/
			} else {


				ServiceCredentials cred = new ServiceCredentials(
						  registerUserDataPanel.getUserName(), registerUserDataPanel.getPassword());

				JakeExecutor.exec(new RegisterAccountWorker(cred));
			}
		}
	}

	private class RegisterAccountWorker extends SwingWorkerWithAvailableLaterObject<Void> {
		private ServiceCredentials cred;

		private RegisterAccountWorker(ServiceCredentials cred) {
			this.cred = cred;
		}

		@Override
		protected AvailableLaterObject<Void> calculateFunction() {
			workingAnimation.startAnimation();

			try {
				return JakeMainApp.getCore().createAccount(cred);
			} catch (NotLoggedInException e) {
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
		protected void done() {
			workingAnimation.stopAnimation();

			showRegistrationSuccessPanel();
		}
	}


	private void showRegistrationSuccessPanel() {
		log.debug("show register success");

		// TODO
	}

	/**
	 * Updates the login username in representation to selected service.
	 */
	private void updateLoginUsernameLabel() {
		if (loginServiceCheckBox.getSelectedIndex() ==
				  SupportedServices.Google.ordinal()) {
			loginUserDataPanel.setUserLabel("usernameGoogle");
		} else {
			loginUserDataPanel.setUserLabel("usernameJabber");
		}
	}


	/**
	 * Creates User/Password Field for entering credientals
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
				registrationInfoPanel = new JPanel(new MigLayout("wrap 2"));
				JLabel registrationLabel1 = new JLabel(getResourceMap().getString("registrationLabel1"));
				registrationLabel1.setForeground(Color.DARK_GRAY);
				registrationInfoPanel.add(registrationLabel1, "span 2, wrap");
				JLabel registrationLabel2 = new JLabel(getResourceMap().getString("registrationLabel2"));
				registrationLabel2.setForeground(Color.DARK_GRAY);
				registrationInfoPanel.add(registrationLabel2);
				LinkAction linkAction = new LinkAction(getResourceMap().getString("registrationLabel3")) {
					public void actionPerformed(ActionEvent e) {
						try {
							Desktop.getDesktop().browse(new URI(getResourceMap().getString("registrationLabelHyperlink")));
						} catch (IOException e1) {
							e1.printStackTrace();
						} catch (URISyntaxException e1) {
							e1.printStackTrace();
						}
						setVisited(true);
					}
				};
				registrationInfoPanel.add(new JXHyperlink(linkAction));
				registrationInfoPanel.setOpaque(false);
				this.add(registrationInfoPanel);

				JLabel serverLabel = new JLabel(getResourceMap().getString("serverLabel"));
				serverLabel.setForeground(Color.DARK_GRAY);
				serverComboBox = new JComboBox();
				serverComboBox.setModel(new DefaultComboBoxModel(new String[]{"jabber.fsinf.at", "jabber.org",
						  "jabber.ccc.de", "macjabber.de", "swissjabber.ch", "binaryfreedom.info"}));
				serverComboBox.setEditable(true);

				this.add(serverLabel, "");
				this.add(serverComboBox, "width 300!");
			}
			userLabel = new JLabel(getResourceMap().getString("usernameLabel"));
			userLabel.setForeground(Color.DARK_GRAY);
			this.add(userLabel);

			userName = new JTextField();
			this.add(userName, "width 300!");

			JLabel passLabel = new JLabel(getResourceMap().getString("passwordLabel"));
			passLabel.setForeground(Color.DARK_GRAY);
			this.add(passLabel);

			passName = new JPasswordField();
			this.add(passName, "width 300!");

			rememberPassCheckBox = new JCheckBox(getResourceMap().getString("rememberPasswordCheckBox"));
			rememberPassCheckBox.setSelected(true);
			this.add(rememberPassCheckBox);
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
			return serverComboBox != null ? serverComboBox.getSelectedItem().toString() : null;
		}

		/**
		 * Save password?
		 *
		 * @return
		 */
		public boolean isRememberPassword() {
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
	 *
	 * @return
	 */
	private JPanel createSignInSuccessPanel() {
		// create the drag & drop hint
		JPanel loginSuccessPanel = new JPanel();
		loginSuccessPanel.setOpaque(false);
		loginSuccessPanel.setLayout(new MigLayout("nogrid, al center, fill"));

		// the sign out button
		JButton signOutButton = new JButton(getResourceMap().getString("signInSuccessSignOut"));
		signOutButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent actionEvent) {
				JakeMainApp.getApp().getCore().signOut();
			}
		});
		loginSuccessPanel.add(signOutButton, "wrap, al center, growx");

		JLabel iconSuccess = new JLabel();
		iconSuccess.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/dropfolder.png"))));

		loginSuccessPanel.add(iconSuccess, "wrap, al center");

		JLabel messageSuccess1 = new JLabel(getResourceMap().getString("dragDropHint1"));
		messageSuccess1.setFont(Platform.getStyler().getH1Font());
		messageSuccess1.setForeground(Color.DARK_GRAY);
		loginSuccessPanel.add(messageSuccess1, "wrap, al center");

		JLabel messageSuccess2 = new JLabel(getResourceMap().getString("dragDropHint2"));
		messageSuccess2.setFont(Platform.getStyler().getH1Font());
		messageSuccess2.setForeground(Color.DARK_GRAY);
		loginSuccessPanel.add(messageSuccess2, "al center");
		return loginSuccessPanel;
	}

	private boolean isModeSignIn() {
		return loginRadioButton.isSelected();
	}

	private boolean isSignInRegisterButtonEnabled() {
		return true;
		//return !(usernameComboBox.getSelectedItem().toString().isEmpty() || passwordField.getPassword().length == 0);
	}

	private void updateSignInRegisterMode() {
		log.info("updating signin/register mode.");

		loginUserDataPanel.setVisible(isModeSignIn());
		registerUserDataPanel.setVisible(!isModeSignIn());
		loginServiceCheckBox.setEnabled(isModeSignIn());


		if (isModeSignIn()) {
			signInRegisterButton.setText(getResourceMap().getString("loginSignIn"));
		} else {
			signInRegisterButton.setText(getResourceMap().getString("loginRegister"));
		}

		// disable the button as long as no credidentals are entered
		signInRegisterButton.setEnabled(isSignInRegisterButtonEnabled());
	}

	@Override
	public void setRegistrationStatus(RegisterStati status, String msg) {
	}

	@Override
	public void setConnectionStatus(ConnectionStati status, String msg) {
	}
}
