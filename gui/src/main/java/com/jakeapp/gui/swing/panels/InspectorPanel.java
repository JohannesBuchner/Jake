package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectViewChanged;
import com.jakeapp.gui.swing.controls.ETable;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.models.EventsTableModel;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Inspector Panel
 * Shows extended File/Notes Info:
 * For File: Icon, Name, , Size, Tags, full path
 * Common: Last access time+user, History Table
 *
 * @author: studpete
 */
public class InspectorPanel extends JXPanel implements
		  ProjectChanged, ProjectSelectionChanged, FileSelectionChanged, ProjectViewChanged {
	private static final Logger log = Logger.getLogger(InspectorPanel.class);
	private Project project;
	private ResourceMap resourceMap;
	private JakeMainView.ProjectViewPanelEnum projectViewPanel;
	private FileObject fileObject;
	private NoteObject noteObject;

	private JXTable eventsTable;
	private JLabel icoLabel;
	private JLabel nameLabel;
	private JLabel sizeLabel;
	private JLabel lastAccessTimeAndUser;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private EventsTableModel eventsTableModel;

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(InspectorPanel.class));

		// register for events
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		FilePanel.getInstance().addFileSelectionListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);

		this.setLayout(new MigLayout("wrap 2, fill"));

		initComponents();
	}

	private void initComponents() {

		// not resizeable, fixed width
		Dimension absSize = new Dimension(250, 250);
		this.setMinimumSize(absSize);
		this.setMaximumSize(new Dimension((int) absSize.getWidth(), Integer.MAX_VALUE));
		this.setPreferredSize(absSize);

		this.setBackground(Platform.getStyler().getWindowBackground());

		eventsTableModel = new EventsTableModel();
		eventsTable = new ETable();
		eventsTable.setModel(eventsTableModel);
		eventsTable.setMinimumSize(new Dimension(200, 200));
		ConfigControlsHelper.configEventsTable(eventsTable);
		this.add(eventsTable, "dock south, grow 200 200, gpy 200");

		// TODO: make this a proper layout!

		// add icon
		icoLabel = new JLabel();
		this.add(icoLabel, "span 2, wrap, growx");

		// add name
		nameLabel = new JLabel();
		this.add(nameLabel, "span 2, wrap");

		// add size
		sizeLabel = new JLabel();
		this.add(sizeLabel, "span 2, wrap");

		// last time+user modified
		this.add(new JLabel(getResourceMap().getString("modifiedLabel")));
		lastAccessTimeAndUser = new JLabel();
		this.add(lastAccessTimeAndUser, "growy, wrap");

		// add tags
		this.add(new JLabel(getResourceMap().getString("tagsLabel")));
		tagsLabel = new JLabel("TODO");
		this.add(tagsLabel, "growy, wrap");

		// add full path
		this.add(new JLabel(getResourceMap().getString("pathLabel")));
		fullPathLabel = new JLabel();
		this.add(fullPathLabel, "growy, wrap");

		// config the history table
		this.add(new JLabel(getResourceMap().getString("historyLabel")), "span 2, wrap");

		updatePanel();
	}

	/**
	 * Updates the panel with current data
	 */
	public void updatePanel() {

		if (getFileObject() != null && isFileContext()) {
			File file = getFileObject().getAbsolutePath();
			icoLabel.setIcon(Platform.getToolkit().getFileIcon(file, 64));

			nameLabel.setText(FileObjectHelper.getName(getFileObject().getAbsolutePath()));
			sizeLabel.setText(FileObjectHelper.getSizeHR(getFileObject()));

			// TODO: @Chris: update tags

			fullPathLabel.setText(
					  FileObjectHelper.getPath(getFileObject().getAbsolutePath()));
			log.debug(fullPathLabel.getText());
			lastAccessTimeAndUser.setText(StringUtilities.htmlize(Translator.get(getResourceMap(), "byLabel",
					  FileObjectHelper.getTimeRel(getFileObject()),
					  FileObjectHelper.getLastModifier(getFileObject()))));

			eventsTableModel.setProject(getProject());
			eventsTableModel.setJakeObject(getFileObject());
		} else if (getNoteObject() != null && isNoteContext()) {

			// TODO: inspector for notes
			eventsTableModel.setJakeObject(getFileObject());
		} else {
			icoLabel.setIcon(null);
			nameLabel.setText("");
			sizeLabel.setText("");
			fullPathLabel.setText("");
			lastAccessTimeAndUser.setText("");

			// hide table contents
			eventsTableModel.setProject(null);
		}
	}

	protected ResourceMap getResourceMap() {
		return resourceMap;
	}

	protected void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public FileObject getFileObject() {
		return fileObject;
	}

	public void setFileObject(FileObject fileObject) {
		this.fileObject = fileObject;

		updatePanel();
	}


	@Override
	public void projectChanged(ProjectChangedEvent ev) {
		updatePanel();
	}

	@Override
	public void setProject(Project pr) {
		this.project = pr;

		updatePanel();
	}

	public Project getProject() {
		return project;
	}

	@Override
	public void fileSelectionChanged(FileSelectedEvent event) {
		setFileObject(event.getSingleFile());
	}

	public NoteObject getNoteObject() {
		return noteObject;
	}

	public void setNoteObject(NoteObject noteObject) {
		this.noteObject = noteObject;
	}

	/**
	 * True if file panel is shown.
	 *
	 * @return
	 */
	private boolean isFileContext() {
		return getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Files;
	}

	/**
	 * True if notes panel is shown.
	 *
	 * @return
	 */
	private boolean isNoteContext() {
		return getProjectViewPanel() == JakeMainView.ProjectViewPanelEnum.Notes;
	}

	@Override
	public void setProjectViewPanel(JakeMainView.ProjectViewPanelEnum projectViewPanel) {
		this.projectViewPanel = projectViewPanel;

		updatePanel();
	}

	private JakeMainView.ProjectViewPanelEnum getProjectViewPanel() {
		return projectViewPanel;
	}
}


