package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.ContextViewChanged;
import com.jakeapp.gui.swing.callbacks.DataChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectViewChanged;
import com.jakeapp.gui.swing.callbacks.PropertyChanged;
import com.jakeapp.gui.swing.callbacks.TaskChanged;
import com.jakeapp.gui.swing.controls.SpinningDial;
import com.jakeapp.gui.swing.controls.SpinningWheelComponent;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.worker.AbstractTask;
import com.jakeapp.gui.swing.worker.IJakeTask;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.ics.status.ILoginStateListener;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

/**
 * Statusbar-Controller
 * As you see, Statusbar is very curious about ALL the events going on...
 */
public class JakeStatusBar extends JakeGuiComponent
				implements ILoginStateListener, ProjectSelectionChanged, ProjectChanged,
				ProjectViewChanged, ContextViewChanged, DataChanged, PropertyChanged,
				TaskChanged {
	private static final Logger log = Logger.getLogger(JakeStatusBar.class);

	private static JakeStatusBar instance;
	private JLabel statusLabel;
	private JButton connectionButton;
	private TriAreaComponent statusBar;
	private JakeMainView.ProjectView projectViewPanel;
	private JakeMainView.ContextPanelEnum contextViewPanel;

	private ConnectionState lastConnectionState = ConnectionState.LOGGED_OUT;

	private String projectFileCount = "";
	private String projectTotalSize = "";
	private SpinningWheelComponent progressDrawer;
	//	private JLabel progressMessage;

	Icon chooseUserIcon =
					new ImageIcon(Toolkit.getDefaultToolkit().getImage(getClass().getResource(
									"/icons/login.png")));

	Icon spinningDial = new SpinningDial(12, 12);
	private JLabel progressMsg;

	@Override public void propertyChanged(EnumSet<PropertyChanged.Reason> reason,
					Project p, Object data) {
		updateConnectionDisplay();
	}

	@Override public void taskStarted(IJakeTask task) {
		updateTaskDisplay();
	}

	private void updateTaskDisplay() {
		if(JakeExecutor.hasTasksRunning()) {
		this.showProgressAnimation(true);
			String moreTasks = "";
			if(JakeExecutor.countTasksRunning() > 1) {
				moreTasks = String.format(" (%d)", JakeExecutor.countTasksRunning());
			}
		this.progressMsg.setText(JakeExecutor.getLatestTask().getClass().getSimpleName() + moreTasks);
		}else {
			this.showProgressAnimation(false);
			this.progressMsg.setText("");
		}
	}

	@Override public void taskUpdated(IJakeTask task) {
		updateTaskDisplay();
	}

	@Override public void taskFinished(IJakeTask task) {
		updateTaskDisplay();
	}

	@Override public void connectionStateChanged(ConnectionState le) {
		lastConnectionState = le;
		updateConnectionDisplay();
	}

	enum IconEnum {
		Offline, LoggingIn, Online
	}

	public static JakeStatusBar getInstance() {
		return instance;
	}

	/**
	 * Update everything of the StatusBar
	 */
	public void updateAll() {
		updateMessage();
		updateConnectionDisplay();
	}

	/**
	 * Get the Icon corresponding to the Connection State.
	 *
	 * @param icon
	 * @return
	 */
	private Icon getConnectionIcon(IconEnum icon) {
		switch (icon) {
			case Offline:
				return chooseUserIcon;
			case LoggingIn:
				return spinningDial;
			case Online:
				return chooseUserIcon;
			default:
				return null;
		}
	}

	@Override public void dataChanged(EnumSet<DataChanged.Reason> reason, Project p) {
		updateMessage();
	}


	protected class ProjectSizeTotalTask extends AbstractTask<Long> {
		@Override
		protected AvailableLaterObject<Long> calculateFunction() {
			return JakeMainApp.getCore().getProjectSizeTotal(getProject());
		}

		@Override
		protected void done() {
			super.done();
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
	protected class ProjectFileCountTask extends AbstractTask<Integer> {
		@Override
		protected AvailableLaterObject<Integer> calculateFunction() {
			log.info("calculating total file count...");
			AvailableLaterObject<Integer> alo =
							JakeMainApp.getCore().getProjectFileCount(getProject());
			alo.setListener(this);
			return alo;
		}

		@Override
		protected void done() {
			super.done();
			// update the status bar label
			int projectFileCount = 0;

			try {
				projectFileCount = this.get();
			} catch (InterruptedException e) {
				this.handleInterruption(e);
			} catch (ExecutionException e) {
				this.handleExecutionError(e);
			}
			String filesStr = getResourceMap()
							.getString(projectFileCount == 1 ? "projectFile" : "projectFiles");

			// update project statistics
			setProjectFileCount(projectFileCount + " " + filesStr);
		}
	}

	protected class NoteCountTask extends AbstractTask<Integer> {
		@Override
		protected AvailableLaterObject<Integer> calculateFunction() {
			return JakeMainApp.getCore().getNoteCount(JakeMainApp.getProject());
		}

		@Override
		public void error(Exception e) {
			log.warn(e);
			this.finished(0);
		}

		@Override
		protected void done() {
			super.done();
			Integer objNoteCount = 0;
			int notesCount = 0;

			try {
				objNoteCount = this.get();
				notesCount = (objNoteCount == null) ? 0 : objNoteCount;
			} catch (InterruptedException e) {
				this.handleInterruption(e);
			} catch (ExecutionException e) {
				this.handleExecutionError(e);
			}

			String notesCountStr = getResourceMap()
							.getString(notesCount == 1 ? "projectNote" : "projectNotes");
			statusLabel.setText(notesCount + " " + notesCountStr);
		}
	}

	public JakeStatusBar() {
		super();
		instance = this;

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		EventCore.get().addProjectChangedCallbackListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);
		JakeMainView.getMainView().addContextViewChangedListener(this);
		EventCore.get().addPropertyListener(this);
		EventCore.get().addTasksChangedListener(this);

		// registering the connection status callback
		EventCore.get().addConnectionStatusCallbackListener(this);

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

	/**
	 * Updates the connection Button with new credentals informations.
	 */
	private void updateConnectionDisplay() {
		String msg;
		IconEnum icon = IconEnum.Offline;

		if (JakeMainApp.getMsgService() != null) {
			String user = JakeMainApp.getMsgService().getUserId().getUserId();

			msg = lastConnectionState + " - " + user;
			icon = IconEnum.LoggingIn;

			if (lastConnectionState == ConnectionState.LOGGED_IN) {
				icon = IconEnum.Online;
			}
			connectionButton.setVisible(true);
		} else {
			msg = getResourceMap().getString("statusLoginNotSignedIn");
			connectionButton.setVisible(false);
		}

		connectionButton.setText(msg);
		connectionButton.setIcon(getConnectionIcon(icon));
	}

	/**
	 * Create status bar code
	 *
	 * @return TriAreaComponent of status bar.
	 */
	private TriAreaComponent createStatusBar() {
		log.trace("creating status bar...");

		// only draw the 'fat' statusbar if we are in a mac. does not look good on win/linux
		BottomBarSize bottombarSize =
						Platform.isMac() ? BottomBarSize.LARGE : BottomBarSize.SMALL;

		TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
		statusLabel = MacWidgetFactory.createEmphasizedLabel("");

		// make status label 2 px smaller
		statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont()
						.getSize() - 2f));

		bottomBar.addComponentToCenter(statusLabel);
		bottomBar.installWindowDraggerOnWindow(JakeMainView.getMainView().getFrame());

		progressDrawer = new SpinningWheelComponent();
		progressDrawer.setVisible(false);
		bottomBar.addComponentToLeft(progressDrawer, 3);

		// FIXME: remove after release
		progressMsg = MacWidgetFactory.createEmphasizedLabel("");
		progressMsg.setFont(progressMsg.getFont().deriveFont(progressMsg.getFont()
						.getSize() - 2f));
		bottomBar.addComponentToLeft(progressMsg, 3);

		// connection info
		connectionButton = new JButton();
		connectionButton.setIcon(chooseUserIcon);
		connectionButton.setHorizontalTextPosition(SwingConstants.LEFT);

		connectionButton.putClientProperty("JButton.buttonType", "textured");
		connectionButton.putClientProperty("JComponent.sizeVariant", "small");
		if (!Platform.isMac()) {
			connectionButton.setFont(connectionButton.getFont().deriveFont(connectionButton
							.getFont().getSize() - 2f));
		}

		connectionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JPopupMenu menu = new JakePopupMenu();
				JMenuItem signInOut = new JMenuItem(getResourceMap().getString(
								(JakeMainApp.getMsgService() != null) ? "menuSignOut" :
												"menuSignIn"));

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

						JakeMainView.getMainView()
										.setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
					}
				});

				menu.add(signInOut);

				// calculate contextmenu directly above signin-status button
				menu.show((JButton) event.getSource(),
								((JButton) event.getSource()).getX(),
								((JButton) event.getSource()).getY() - 20);
			}
		});
		updateConnectionDisplay();
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
			if (getProjectViewPanel() == JakeMainView.ProjectView.Files) {
				// update the status bar label
				JakeExecutor.exec(new ProjectFileCountTask());
				JakeExecutor.exec(new ProjectSizeTotalTask());
			} else if (getProjectViewPanel() == JakeMainView.ProjectView.Notes) {
				JakeExecutor.exec(new NoteCountTask());
			} else {
				// project view
				if (getProject() != null) {
					int peopleCount;
					try {
						peopleCount = JakeMainApp.getCore().getAllProjectMembers(getProject()).size();
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

				/*
				if (MsgServiceHelper.isCurrentUserLoggedIn()) {
					statusLabel.setText(getResourceMap().getString("showLoggedInSuccess"));
				} else {
					statusLabel.setText(getResourceMap().getString("showLoggedInFailed"));
				}
				*/
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
		this.setStatusLabelText(this.getProjectFileCount() + (
						(this.getProjectFileCount().length() == 0 || this.getProjectTotalSize()
										.length() == 0) ? "" : ", ") + this.getProjectTotalSize());
	}

	private void setStatusLabelText(String text) {
		statusLabel.setText(text);
	}


	public void projectChanged(ProjectChangedEvent ev) {
		projectUpdated();
	}

	public void setProjectViewPanel(JakeMainView.ProjectView panel) {
		this.projectViewPanel = panel;

		projectUpdated();
	}

	public JakeMainView.ProjectView getProjectViewPanel() {
		return projectViewPanel;
	}

	public void setContextViewPanel(JakeMainView.ContextPanelEnum contextViewPanel) {
		this.contextViewPanel = contextViewPanel;
		projectUpdated();
	}

	public JakeMainView.ContextPanelEnum getContextViewPanel() {
		return contextViewPanel;
	}


	private void showProgressAnimation(final boolean show) {
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
		//showProgressAnimation(progress > 0 && progress < 100);
	}


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
