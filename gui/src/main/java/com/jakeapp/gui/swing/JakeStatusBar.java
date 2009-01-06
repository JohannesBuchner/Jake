package com.jakeapp.gui.swing;

import com.explodingpixels.macwidgets.BottomBarSize;
import com.explodingpixels.macwidgets.MacWidgetFactory;
import com.explodingpixels.macwidgets.TriAreaComponent;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
	private JakeMainView.ProjectViewPanels projectViewPanel;
	private JakeMainView.ContextPanels contextViewPanel;


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
	 * Updates the connection Button with new credentals informations
	 */
	private void updateConnectionButton() {
		String msg;

		if (getCore().isSignedIn()) {
			msg = getCore().getCurrentProjectMember().getUserId().getUserId();
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
						  getCore().isSignedIn() ? "menuSignOut" : "menuSignIn"));

				signInOut.addActionListener(new ActionListener() {

					public void actionPerformed(ActionEvent actionEvent) {
						if (!JakeMainApp.getApp().getCore().isSignedIn()) {
						} else {
							JakeMainApp.getApp().getCore().signOut();
						}

						JakeMainView.getMainView().setContextViewPanel(JakeMainView.ContextPanels.Login);
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

		if (getContextViewPanel() == JakeMainView.ContextPanels.Project) {
			if (getProjectViewPanel() == JakeMainView.ProjectViewPanels.Files) {
				// update the status bar label
				int projectFileCount = getCore().getProjectFileCount(getProject());
				String filesStr = getResourceMap().getString(projectFileCount == 1 ? "projectFile" : "projectFiles");

				long projectSizeTotal = getCore().getProjectSizeTotal(getProject());
				String projectSize = FileUtilities.getSize(projectSizeTotal);

				// update project statistics
				statusLabel.setText(projectFileCount + " " + filesStr + ", " + projectSize);
			} else if (getProjectViewPanel() == JakeMainView.ProjectViewPanels.Notes) {
				int notesCount = getCore().getNotes(getProject()).size();
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
		} else if (getContextViewPanel() == JakeMainView.ContextPanels.Invitation) {
			statusLabel.setText("");
		} else {
			statusLabel.setText("");
			// login
		}
	}


	public void projectChanged(ProjectChangedEvent ev) {
		projectUpdated();
	}

	public void setProjectViewPanel(JakeMainView.ProjectViewPanels panel) {
		this.projectViewPanel = panel;

		projectUpdated();
	}

	public JakeMainView.ProjectViewPanels getProjectViewPanel() {
		return projectViewPanel;
	}

	public void setContextViewPanel(JakeMainView.ContextPanels contextViewPanel) {
		this.contextViewPanel = contextViewPanel;
		projectUpdated();
	}

	public JakeMainView.ContextPanels getContextViewPanel() {
		return contextViewPanel;
	}
}
