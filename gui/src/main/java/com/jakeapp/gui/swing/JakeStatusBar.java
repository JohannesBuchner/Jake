package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.services.MsgService;
import com.jakeapp.core.util.availablelater.AvailableErrorObject;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.ConnectionStatus;
import com.jakeapp.gui.swing.callbacks.ContextViewChanged;
import com.jakeapp.gui.swing.callbacks.MsgServiceChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectViewChanged;
import com.jakeapp.gui.swing.controls.JAsynchronousProgressIndicator;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.MsgServiceHelper;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.worker.SwingWorkerWithAvailableLaterObject;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutionException;

/**
 * User: studpete
 * Date: Dec 29, 2008
 * Time: 10:59:04 AM
 */
public class JakeStatusBar extends JakeGuiComponent implements
		  ConnectionStatus, ProjectSelectionChanged, ProjectChanged, ProjectViewChanged, ContextViewChanged, MsgServiceChanged {
	private static final Logger log = Logger.getLogger(JakeStatusBar.class);

	private static JakeStatusBar instance;
	private JLabel statusLabel;
	private JButton connectionButton;
	private TriAreaComponent statusBar;
	private JakeMainView.ProjectViewPanelEnum projectViewPanel;
	private JakeMainView.ContextPanelEnum contextViewPanel;

	private String projectFileCount = "";
	private String projectTotalSize = "";
	private JAsynchronousProgressIndicator progressDrawer;
	private JLabel progressMessage;

	private static JakeStatusBar getInstance() {
		return instance;
	}


	protected class ProjectSizeTotalWorker extends SwingWorkerWithAvailableLaterObject<Long> {
		@Override
		protected AvailableLaterObject<Long> calculateFunction() {
			return JakeMainApp.getCore().getProjectSizeTotal(getProject());
		}

		@Override
		protected void done() {
			long projectSizeTotal = 0;
			try {
				projectSizeTotal = this.get();
			} catch (InterruptedException e) {
				this.handleInterruption(e);
			} catch (ExecutionException e) {
				this.handleExecutionError(e);
			}
			String projectSize = FileUtilities.getSize(projectSizeTotal);

			// update project statistics
			setProjectTotalSize(projectSize);
		}
	}

	/**
	 * Worker to count all project files
	 */
	protected class ProjectFileCountWorker extends SwingWorkerWithAvailableLaterObject<Integer> {
		@Override
		protected AvailableLaterObject<Integer> calculateFunction() {
			log.info("calculating total file count...");
			AvailableLaterObject<Integer> alo = JakeMainApp.getCore().getProjectFileCount(getProject());
			alo.setListener(this);
			return alo;
		}

		@Override
		protected void done() {
			// update the status bar label
			int projectFileCount = 0;

			try {
				projectFileCount = this.get();
			} catch (InterruptedException e) {
				this.handleInterruption(e);
			} catch (ExecutionException e) {
				this.handleExecutionError(e);
			}
			String filesStr = getResourceMap().getString(projectFileCount == 1 ? "projectFile" : "projectFiles");

			// update project statistics
			setProjectFileCount(projectFileCount + " " + filesStr);
		}
	}
	
	protected class NoteCountWorker extends SwingWorkerWithAvailableLaterObject<Integer> {
		@Override
		protected AvailableLaterObject<Integer> calculateFunction() {
			return JakeMainApp.getCore().getNoteCount(JakeMainApp.getProject());
		}

		@Override
		public void error(Exception e) {
			log.warn(e);
			this.finished(new Integer(0));
		}
		
		@Override
		protected void done() {
			Integer objNoteCount = 0;
			int notesCount = 0;
			
			try {
				objNoteCount = this.get();
				notesCount = (objNoteCount==null)?0:objNoteCount.intValue();
			} catch (InterruptedException e) {
				this.handleInterruption(e);
			} catch (ExecutionException e) {
				this.handleExecutionError(e);
			}
			
			String notesCountStr = getResourceMap().getString(notesCount == 1 ? "projectNote" : "projectNotes");
			statusLabel.setText(notesCount + " " + notesCountStr);
		}
	}

	public JakeStatusBar(ICoreAccess core) {
		super(core);
		instance = this;

		// TODO reenable
		/*
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		JakeMainApp.getCore().addProjectChangedCallbackListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);
		JakeMainView.getMainView().addContextViewChangedListener(this);
		JakeMainApp.getApp().addMsgServiceChangedListener(this);

		// registering the connection status callback
		getCore().addConnectionStatusCallbackListener(this);
*/
		statusBar = createStatusBar();
	}

	/**
	 * Returns the Status Bar component.
	 *
	 * @return the status bar component.
	 */
	public Component getComponent() {
		return statusBar.getComponent();
	}


	public void setConnectionStatus(ConnectionStati status, String msg) {
		updateConnectionButton();
	}

	/**
	 * Updates the connection Button with new credentals informations.
	 */
	private void updateConnectionButton() {
		String msg;

		// TODO: neet online/offline info!
		if (JakeMainApp.getMsgService() != null) {
			msg = JakeMainApp.getMsgService().getUserId().getUserId();
		} else {
			msg = getResourceMap().getString("statusLoginNotSignedIn");
		}
		connectionButton.setText(msg);
	}

	/**
	 * Create status bar code
	 *
	 * @return TriAreaComponent of status bar.
	 */
	private TriAreaComponent createStatusBar() {
		//log.info("creating status bar...");

		// only draw the 'fat' statusbar if we are in a mac. does not look good on win/linux
		BottomBarSize bottombarSize = Platform.isMac() ? BottomBarSize.LARGE : BottomBarSize.SMALL;

		TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
		statusLabel = MacWidgetFactory.createEmphasizedLabel("");

		// make status label 2 px smaller
		statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize() - 2f));

		bottomBar.addComponentToCenter(statusLabel);
		bottomBar.installWindowDraggerOnWindow(JakeMainView.getMainView().getFrame());

		progressDrawer = new JAsynchronousProgressIndicator();
		progressDrawer.setVisible(false);
		bottomBar.addComponentToLeft(progressDrawer, 3);

		//progressMessage = new JLabel();
		//progressMessage.setText("");
		//bottomBar.addComponentToLeft(progressMessage);


		//Font statusButtonFont = statusLabel.getFont().deriveFont(statusLabel.getFont().getSize()-2f)

		// control button code
		/*
				  Icon plusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
							 getClass().getResourceMap("/icons/plus.png")));

				  JButton addProjectButton = new JButton();
				  addProjectButton.setIcon(plusIcon);
				  addProjectButton.setToolTipText("Add Project...");

				  addProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
				  addProjectButton.putClientProperty("JButton.segmentPosition", "first");

				  if (Platform.isWin()) {
						addProjectButton.setFocusPainted(false);
				  }

				  Icon minusIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
							 getClass().getResourceMap("/icons/minus.png")));
				  JButton removeProjectButton = new JButton();
				  removeProjectButton.setIcon(minusIcon);
				  removeProjectButton.setToolTipText("Remove Project...");

				  removeProjectButton.putClientProperty("JButton.buttonType", "segmentedTextured");
				  removeProjectButton.putClientProperty("JButton.segmentPosition", "last");

				  if (Platform.isWin()) {
						addProjectButton.setFocusPainted(false);
				  }

				  ButtonGroup group = new ButtonGroup();
				  group.add(addProjectButton);
				  group.add(removeProjectButton);


				  /*
				  bottomBar.addComponentToLeft(addProjectButton, 0);
				  bottomBar.addComponentToLeft(removeProjectButton);
				  */

		/*
				 JButton playPauseProjectButton = new JButton(">/||");
				 if(!Platform.isMac()) playPauseProjectButton.setFont(statusButtonFont);
				 playPauseProjectButton.putClientProperty("JButton.buttonType", "textured");
				 bottomBar.addComponentToLeft(playPauseProjectButton, 0);


				 playPauseProjectButton.addActionListener(new ActionListener() {

				 public void actionPerformed(ActionEvent event) {
				 new SheetTest();
				 }
				 });
				  */

		// connection info
		Icon loginIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource("/icons/login.png")));
		connectionButton = new JButton();
		connectionButton.setIcon(loginIcon);
		connectionButton.setHorizontalTextPosition(SwingConstants.LEFT);

		connectionButton.putClientProperty("JButton.buttonType", "textured");
		connectionButton.putClientProperty("JComponent.sizeVariant", "small");
		if (!Platform.isMac()) {
			connectionButton.setFont(connectionButton.getFont().deriveFont(connectionButton.getFont().getSize() - 2f));
		}

		connectionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JPopupMenu menu = new JakePopupMenu();
				JMenuItem signInOut = new JMenuItem(getResourceMap().getString(
						  (JakeMainApp.getMsgService() != null) ? "menuSignOut" : "menuSignIn"));

				signInOut.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionEvent) {
						if (JakeMainApp.getMsgService() == null) {
						} else {
							try {
								JakeMainApp.logoutUser();
							} catch (Exception e) {
								log.warn(e);
								ExceptionUtilities.showError(e);
							}
						}

						JakeMainView.getMainView().setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
					}
				});

				menu.add(signInOut);

				// calculate contextmenu directly above signin-status button
				menu.show((JButton) event.getSource(), ((JButton) event.getSource()).getX(),
						  ((JButton) event.getSource()).getY() - 20);
			}
		});
		updateConnectionButton();
		bottomBar.addComponentToRight(connectionButton);

		return bottomBar;
	}


	@Override
	protected void projectUpdated() {
		updateMessageInt();
	}

	/**
	 * Updates the status bar message
	 */
	public static void updateMessage() {
		getInstance().updateMessageInt();
	}
	
	/**
	 * Updates the project label.
	 * This is context specific.
	 */
	public void updateMessageInt() {

		if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Project) {
			if (getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Files) {
				// update the status bar label
				JakeExecutor.exec(new ProjectFileCountWorker());
				JakeExecutor.exec(new ProjectSizeTotalWorker());
			} else if (getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Notes) {
				JakeExecutor.exec(new NoteCountWorker());
			} else {
				// project view
				if (getProject() != null) {
					int peopleCount;
					try {
						peopleCount = getCore().getProjectUser(getProject()).size();
					} catch (PeopleOperationFailedException e) {
						peopleCount = 0;
						ExceptionUtilities.showError(e);
					}

					// nobody there...
					if (peopleCount == 0) {
						String aloneStr = getResourceMap().getString("projectAddPeopleToStart");
						statusLabel.setText(aloneStr);
					} else {
						String peopleCountStr = getResourceMap().getString("projectPeople");
						statusLabel.setText(peopleCount + " " + peopleCountStr);
					}
				} else {
					statusLabel.setText("");
				}
			}
		} else if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Invitation) {
			statusLabel.setText("");
		} else if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Login) {

			if (JakeMainApp.getMsgService() != null) {
				// user chosen

				if (MsgServiceHelper.isCurrentUserLoggedIn()) {
					statusLabel.setText(getResourceMap().getString("showLoggedInSuccess"));
				} else {
					statusLabel.setText(getResourceMap().getString("showLoggedInFailed"));
				}
			} else {
				statusLabel.setText("");
			}
			// login
		} else {
			log.warn("Unknown Context: " + getContextViewPanel());
		}
	}

	private String getProjectFileCount() {
		return this.projectFileCount;
	}

	private String getProjectTotalSize() {
		return this.projectTotalSize;
	}

	private void setProjectFileCount(String filecount) {
		this.projectFileCount = filecount;
		this.setStatusLabelProjectStatistics();
	}

	private void setProjectTotalSize(String totalSize) {
		this.projectTotalSize = totalSize;
		this.setStatusLabelProjectStatistics();
	}

	private void setStatusLabelProjectStatistics() {
		this.setStatusLabelText(this.getProjectFileCount() + ((this.getProjectFileCount().length() == 0 || this.getProjectTotalSize().length() == 0) ? "" : ", ") + this.getProjectTotalSize());
	}

	private void setStatusLabelText(String text) {
		statusLabel.setText(text);
	}


	public void projectChanged(ProjectChangedEvent ev) {
		projectUpdated();
	}

	public void setProjectViewPanel(JakeMainView.ProjectViewPanelEnum panel) {
		this.projectViewPanel = panel;

		projectUpdated();
	}

	public JakeMainView.ProjectViewPanelEnum getProjectViewPanel() {
		return projectViewPanel;
	}

	public void setContextViewPanel(JakeMainView.ContextPanelEnum contextViewPanel) {
		this.contextViewPanel = contextViewPanel;
		projectUpdated();
	}

	public JakeMainView.ContextPanelEnum getContextViewPanel() {
		return contextViewPanel;
	}

	@Override
	public void msgServiceChanged(MsgService msg) {
		updateConnectionButton();
	}

	// HACK: should be called via listener, implicit only...
	public static void showProgressAnimation(final boolean show) {

		Runnable runner = new Runnable() {
			@Override
			public void run() {
				if (show) {
					getInstance().progressDrawer.setVisible(true);
					getInstance().progressDrawer.startAnimation();
				} else {
					getInstance().progressDrawer.setVisible(false);
					getInstance().progressDrawer.stopAnimation();
				}
			}
		};

		// allow thread save calls
		invokeIfNeeded(runner);
	}

	public static void showMessage(final String msg, int progress) {
		showMessage(msg);
		// TODO: pie!
		showProgressAnimation(progress > 0 && progress < 100);
	}

	// TODO: should use tasks framework
	public static void showMessage(final String msg) {
		Runnable runner = new Runnable() {
			@Override
			public void run() {
				JakeStatusBar.getInstance().statusLabel.setText(msg);
			}
		};

		// allow thread save calls
		invokeIfNeeded(runner);
	}

	/**
	 * Invoke if not called from evnet dispatch thread
	 *
	 * @param runner
	 */
	private static void invokeIfNeeded(Runnable runner) {
		if (!EventQueue.isDispatchThread()) {
			SwingUtilities.invokeLater(runner);
		} else {
			runner.run();
		}
	}
}
