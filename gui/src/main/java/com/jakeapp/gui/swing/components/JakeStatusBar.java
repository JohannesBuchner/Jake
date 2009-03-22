package com.jakeapp.gui.swing.components;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.EnumSet;
import java.util.concurrent.ExecutionException;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.apache.log4j.Logger;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.availablelater.AvailableLaterObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.ContextViewChangedCallback;
import com.jakeapp.gui.swing.callbacks.DataChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectViewChangedCallback;
import com.jakeapp.gui.swing.callbacks.TaskChangedCallback;
import com.jakeapp.gui.swing.components.componenthelper.JakeGuiComponent;
import com.jakeapp.gui.swing.controls.SpinningDial;
import com.jakeapp.gui.swing.controls.SpinningWheelComponent;
import com.jakeapp.gui.swing.exceptions.PeopleOperationFailedException;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import com.jakeapp.gui.swing.worker.JakeExecutor;
import com.jakeapp.gui.swing.worker.tasks.AbstractTask;
import com.jakeapp.gui.swing.worker.tasks.IJakeTask;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.jake.ics.status.ILoginStateListener;

/**
 * Statusbar-Controller
 * As you see, Statusbar is very curious about ALL the events going on...
 */
public class JakeStatusBar extends JakeGuiComponent
				implements ILoginStateListener, ProjectChangedCallback, ProjectViewChangedCallback,
				ContextViewChangedCallback, DataChangedCallback, ContextChangedCallback, TaskChangedCallback {
	private static final Logger log = Logger.getLogger(JakeStatusBar.class);

	private static JakeStatusBar instance;
	private JLabel statusLabel;
	private JButton connectionButton;
	private TriAreaComponent statusBar;
	private JakeMainView.ProjectView projectViewPanel;
	private JakeMainView.ContextPanelEnum contextViewPanel;

	private ConnectionState lastConnectionState = ConnectionState.LOGGED_OUT;
	private String lastConnectionMsg = "";
	private String projectFileCount = "";
	private String projectTotalSize = "";
	private SpinningWheelComponent progressDrawer;
	//	private JLabel progressMessage;

	Icon chooseUserIcon = ImageLoader.get(getClass(),"/icons/login.png");

	Icon spinningDial = new SpinningDial(16, 16);
	private JLabel progressMsg;
	private JPopupMenu connectionMenu;

	@Override public void contextChanged(EnumSet<ContextChangedCallback.Reason> reason,
					Object context) {
		updateConnectionDisplay();
	}

	@Override public void taskStarted(IJakeTask task) {
		updateTaskDisplay();
	}

	private void updateTaskDisplay() {
		if (JakeExecutor.hasTasksRunning()) {
			this.showProgressAnimation(true);
			String moreTasks = "";
			if (JakeExecutor.countTasksRunning() > 1) {
				moreTasks = String.format(" (%d)", JakeExecutor.countTasksRunning());
			}
			this.progressMsg.setText(
							JakeExecutor.getLatestTask().getClass().getSimpleName() + moreTasks);
		} else {
			this.showProgressAnimation(false);
			this.progressMsg.setText("");
		}
	}

	@Override public void taskUpdated(IJakeTask task) {
		updateTaskDisplay();
	}

	@Override public void taskFinished(IJakeTask task) {
		if(task.getException() != null) {
			log.warn("Error in a task" + task.getException().getMessage());
			//ExceptionUtilities.showError(task.getException());
		}

		updateTaskDisplay();
	}

	@Override public void connectionStateChanged(ConnectionState le, Exception ex) {
		lastConnectionState = le;
		lastConnectionMsg = ex != null ? ex.getMessage() : null;
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

	@Override public void dataChanged(EnumSet<DataReason> dataReason, Project p) {
		updateMessage();
	}


	protected class ProjectSizeTotalTask extends AbstractTask<Long> {
		@Override
		protected AvailableLaterObject<Long> calculateFunction() {
			return JakeMainApp.getCore().getProjectSizeTotal(getProject());
		}

		@Override
		protected void onDone() {
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
		protected void onDone() {
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
			return JakeMainApp.getCore().getNoteCount(JakeContext.getProject());
		}

		@Override
		protected void onDone() {
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

		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addContextChangedListener(this);
		EventCore.get().addTasksChangedListener(this);
		EventCore.get().addConnectionStatusCallbackListener(this);

		JakeMainView.getMainView().addProjectViewChangedListener(this);
		JakeMainView.getMainView().addContextViewChangedListener(this);

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

		if (JakeContext.getMsgService() != null) {
			String user = JakeContext.getMsgService().getUserId().getUserId();

			msg = getPrettyConnectionState() + " - " + user;

			if (lastConnectionState == ConnectionState.CONNECTING) {
				icon = IconEnum.LoggingIn;
			} else if (lastConnectionState == ConnectionState.LOGGED_IN) {
				icon = IconEnum.Online;
			}
			connectionButton.setVisible(true);
		} else {
			msg = getResourceMap().getString("statusLoginNotSignedIn");
			connectionButton.setVisible(false);
		}

		connectionButton.setText(msg);
		connectionButton.setIcon(getConnectionIcon(icon));

		connectionButton.setToolTipText(lastConnectionMsg);
	}

	private String getPrettyConnectionState() {
		switch (lastConnectionState) {
			case LOGGED_IN:
				return "Connected";
			case LOGGED_OUT:
				return "Offline";
			case CONNECTING:
				return "Connecting";
			case INVALID_CREDENTIALS:
				return "Invalid User/Pass";
			default:
				return "Unknown Error";
		}
	}

	/**
	 * Create status bar code
	 *
	 * @return TriAreaComponent of status bar.
	 */
	private TriAreaComponent createStatusBar() {
		log.trace("creating status bar...");

		// only draw the 'small' statusbar if we are in on win. mac and linux want it fat! ;o)
		BottomBarSize bottombarSize =
						Platform.isWin() ? BottomBarSize.SMALL : BottomBarSize.LARGE;

		TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
		statusLabel = MacWidgetFactory.createEmphasizedLabel("");

		// make status label 2 px smaller
		statusLabel.setFont(
						statusLabel.getFont().deriveFont(statusLabel.getFont().getSize() - 2f));

		bottomBar.addComponentToCenter(statusLabel);
		bottomBar.installWindowDraggerOnWindow(JakeMainView.getMainView().getFrame());

		progressDrawer = new SpinningWheelComponent();
		progressDrawer.setVisible(false);
		bottomBar.addComponentToLeft(progressDrawer, 3);

		// FIXME: remove after release
		progressMsg = MacWidgetFactory.createEmphasizedLabel("");
		progressMsg.setFont(
						progressMsg.getFont().deriveFont(progressMsg.getFont().getSize() - 2f));
		bottomBar.addComponentToLeft(progressMsg, 3);

		// connection info
		connectionButton = new JButton();
		connectionButton.setIcon(chooseUserIcon);
		connectionButton.setHorizontalTextPosition(SwingConstants.LEFT);
		connectionButton.putClientProperty("JComponent.sizeVariant", "small");
		connectionButton.putClientProperty("JButton.buttonType", "textured");
		if (!Platform.isMac()) {
			connectionButton.setFont(connectionButton.getFont().deriveFont(
							connectionButton.getFont().getSize() - 2f));
		}

		connectionButton.addActionListener(new ActionListener() {

			public void actionPerformed(ActionEvent event) {
				JPopupMenu menu = createConnectionMenu();

				// calculate contextmenu directly above signin-status button
				menu.show((JButton) event.getSource(), ((JButton) event.getSource()).getX(),
								((JButton) event.getSource()).getY() - 20);
			}
		});
		updateConnectionDisplay();
		bottomBar.addComponentToRight(connectionButton);

		return bottomBar;
	}

	/**
	 * One-Time-Init of the Connection Menu
	 *
	 * @return
	 */
	private JPopupMenu createConnectionMenu() {
		connectionMenu = new JakePopupMenu();
		JMenuItem signInOut = new JMenuItem(getResourceMap().getString(
						(JakeContext.getMsgService() != null) ? "menuSignOut" : "menuSignIn"));

		signInOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent actionEvent) {
				// fixme: login, logout!
			}
		});
		connectionMenu.add(signInOut);

		JMenuItem chooseUser = new JMenuItem("Choose User...");
		chooseUser.addActionListener(new ActionListener() {
			@Override public void actionPerformed(ActionEvent e) {
				try {
					JakeMainApp.logoutUser();
				} catch (Exception ex) {
					ExceptionUtilities.showError(ex);
				}

				JakeMainView.getMainView()
								.setContextViewPanel(JakeMainView.ContextPanelEnum.Login);
			}
		});

		connectionMenu.add(chooseUser);
		return connectionMenu;
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
			if (JakeContext.getInvitation() != null) {
				statusLabel.setText("Woohoo, that's an Invitation! You better join!");
			} else {

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
							peopleCount = JakeMainApp.getCore().getAllProjectMembers(getProject())
											.size();
						} catch (PeopleOperationFailedException e) {
							peopleCount = 0;
							ExceptionUtilities.showError(e);
						}

						// nobody there...
						if (peopleCount == 1) {
							String aloneStr =
											getResourceMap().getString("projectAddPeopleToStart");
							statusLabel.setText(aloneStr);
						} else {
							String peopleCountStr = getResourceMap().getString("projectPeople");
							statusLabel.setText(peopleCount + " " + peopleCountStr);
						}
					} else {
						statusLabel.setText("");
					}
				}
			}
		} else if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Invitation) {
			statusLabel.setText("");
		} else if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Login) {

			if (JakeContext.getMsgService() != null) {
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
				JakeStatusBar.getInstance().statusLabel.setText(StringUtilities.htmlize(msg));
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
