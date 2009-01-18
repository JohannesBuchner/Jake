package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.core.domain.exceptions.FrontendNotLoggedInException;
import com.jakeapp.core.domain.exceptions.ProjectNotLoadedException;
import com.jakeapp.core.util.availablelater.AvailableLaterObject;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.helpers.*;
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
		  ConnectionStatus, ProjectSelectionChanged, ProjectChanged, ProjectViewChanged, ContextViewChanged {
	private static final Logger log = Logger.getLogger(JakeStatusBar.class);

	private JLabel statusLabel;
	private JButton connectionButton;
	private TriAreaComponent statusBar;
	private JakeMainView.ProjectViewPanelEnum projectViewPanel;
	private JakeMainView.ContextPanelEnum contextViewPanel;

	private String projectFileCount = "";
	private String projectTotalSize = "";


	protected class ProjectSizeTotalWorker extends SwingWorkerWithAvailableLaterObject<Long> {
		@Override
		protected AvailableLaterObject<Long> calculateFunction() {
			return JakeMainApp.getCore().getProjectSizeTotal(getProject(), this);
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

	protected class ProjectFileCountWorker extends SwingWorkerWithAvailableLaterObject<Integer> {
		@Override
		protected AvailableLaterObject<Integer> calculateFunction() {
			return JakeMainApp.getCore().getProjectFileCount(getProject(), this);
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

	public JakeStatusBar(ICoreAccess core) {
		super(core);

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);
		JakeMainView.getMainView().addContextViewChangedListener(this);

		// registering the connection status callback
		getCore().addConnectionStatusCallbackListener(this);

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

		if (MsgServiceHelper.isUserLoggedIn()) {
			msg = MsgServiceHelper.getLoggedInMsgService().getUserId().toString();
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
		log.info("creating status bar...");

		// only draw the 'fat' statusbar if we are in a mac. does not look good on win/linux
		BottomBarSize bottombarSize = Platform.isMac() ? BottomBarSize.LARGE : BottomBarSize.SMALL;

		TriAreaComponent bottomBar = MacWidgetFactory.createBottomBar(bottombarSize);
		statusLabel = MacWidgetFactory.createEmphasizedLabel("");

		// make status label 2 px smaller
		statusLabel.setFont(statusLabel.getFont().deriveFont(statusLabel.getFont().getSize() - 2f));

		bottomBar.addComponentToCenter(statusLabel);
		bottomBar.installWindowDraggerOnWindow(JakeMainView.getMainView().getFrame());

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
						  MsgServiceHelper.isUserLoggedIn() ? "menuSignOut" : "menuSignIn"));

				signInOut.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionEvent) {
						if (!MsgServiceHelper.isUserLoggedIn()) {
						} else {
							try {
								MsgServiceHelper.getLoggedInMsgService().logout();
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
		updateProjectLabel();
	}


	/**
	 * Updates the project label.
	 * This is context specific.
	 */
	public void updateProjectLabel() {

		if (getContextViewPanel() == JakeMainView.ContextPanelEnum.Project) {
			if (getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Files) {
				// update the status bar label
				JakeExecutor.exec(new ProjectFileCountWorker());
				JakeExecutor.exec(new ProjectSizeTotalWorker());
			} else if (getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Notes) {
				int notesCount = 0;
				try {
					/**
					 * TODO 4 Peter:
					 * achtung.. das hollt jedes mal
					 * die ganzen Notes aus der Datenbank... bitte irgendwo zwischenspeichern.
					 */
					notesCount = getCore().getNotes(getProject()).size();
				} catch (FrontendNotLoggedInException e) {
					// TODO 4 peter
					e.printStackTrace();
				} catch (ProjectNotLoadedException e) {
					// TODO 4 Peter
					e.printStackTrace();
				}
				String notesCountStr = getResourceMap().getString(notesCount == 1 ? "projectNote" : "projectNotes");

				statusLabel.setText(notesCount + " " + notesCountStr);

			} else {
				// project view
				if (getProject() != null) {
					int peopleCount = getCore().getPeople(getProject()).size();

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
		} else {
			statusLabel.setText("");
			// login
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
}
