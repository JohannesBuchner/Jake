package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.exceptions.NoteOperationFailedException;
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
		  ProjectChanged, ProjectSelectionChanged, FileSelectionChanged, ProjectViewChanged, NoteSelectionChanged {
	private static final Logger log = Logger.getLogger(InspectorPanel.class);
	public static final int INSPECTOR_SIZE = 250;
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

	private Icon notesIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  getClass().getResource("/icons/notes.png")).getScaledInstance(64, 64, Image.SCALE_SMOOTH));

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(InspectorPanel.class));

		// register for events
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		FilePanel.getInstance().addFileSelectionListener(this);
		NotesPanel.getInstance().addNoteSelectionListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);

		initComponents();
	}

	private void initComponents() {
		this.setLayout(new MigLayout("wrap 2, fill"));

		// not resizeable, fixed width
		Dimension absSize = new Dimension(INSPECTOR_SIZE, INSPECTOR_SIZE);
		this.setMinimumSize(new Dimension(100, 100));
		//this.setMaximumSize(new Dimension((int) absSize.getWidth(), Integer.MAX_VALUE));
		//this.setPreferredSize(absSize);

		this.setBackground(Platform.getStyler().getWindowBackground());

		eventsTableModel = new EventsTableModel(getProject());
		eventsTable = new ITunesTable();
		eventsTable.setModel(eventsTableModel);
		eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(eventsTable);
		eventsTable.updateUI();
		this.add(eventsTable, "dock south, grow 150 150, gpy 150");

		JPanel headerPanel = new JPanel(new MigLayout("wrap 1, fill"));
		headerPanel.setOpaque(false);

		// add icon
		icoLabel = new JLabel();
		headerPanel.add(icoLabel, "dock west, hidemode 1, w 64!, h 64!");

		// add name
		nameLabel = new JLabel();
		headerPanel.add(nameLabel, "span 2, wrap, grow");

		// add size
		sizeLabel = new JLabel();
		headerPanel.add(sizeLabel, "wrap, grow");

		this.add(headerPanel, "wrap, grow");

		Font smallFont = UIManager.getFont("Label.font").deriveFont(12);

		// last time+user modified
		JLabel lastAccessTextLabel = new JLabel(getResourceMap().getString("modifiedLabel"));
		lastAccessTextLabel.setFont(smallFont);
		this.add(lastAccessTextLabel, "right");
		lastAccessTimeAndUser = new JLabel();
		lastAccessTimeAndUser.setFont(smallFont);
		this.add(lastAccessTimeAndUser, "growy, wrap");

		// add tags
		JLabel tagsHeaderLabel = new JLabel(getResourceMap().getString("tagsLabel"));
		this.add(tagsHeaderLabel, "right");
		tagsLabel = new JLabel("");
		this.add(tagsLabel, "growy, wrap");

		// add full path
		JLabel fullPathTextLabel = new JLabel(getResourceMap().getString("pathLabel"));
		fullPathTextLabel.setFont(smallFont);
		this.add(fullPathTextLabel, "right");
		fullPathLabel = new JLabel();
		fullPathLabel.setFont(smallFont);
		this.add(fullPathLabel, "growy, wrap");

		// config the history table
		this.add(new JLabel(getResourceMap().getString("historyLabel")), "span 2, wrap");

		updatePanel();
	}

	/**
	 * Updates the panel with current data
	 */
	public void updatePanel() {

		// always update the table
		// HACK: disable for DEMO
		eventsTableModel.setProject(null);

		if (getFileObject() != null && isFileContext()) {
			File file = getFileObject().getAbsolutePath();
			icoLabel.setIcon(Platform.getToolkit().getFileIcon(file, 64));

			nameLabel.setText(StringUtilities.htmlize(StringUtilities.bold(
					  FileObjectHelper.getName(getFileObject().getAbsolutePath()))));

			sizeLabel.setText(FileObjectHelper.getSizeHR(getFileObject()));

			// TODO: @Chris: update tags

			fullPathLabel.setText(
					  FileObjectHelper.getPath(getFileObject().getAbsolutePath()));
			log.debug(fullPathLabel.getText());
			lastAccessTimeAndUser.setText(StringUtilities.htmlize(Translator.get(getResourceMap(), "byLabel",
					  FileObjectHelper.getTimeRel(getFileObject()),
					  FileObjectHelper.getLastModifier(getFileObject()))));

			eventsTableModel.setJakeObject(getFileObject());
		} else if (getNoteObject() != null && isNoteContext()) {
			icoLabel.setIcon(notesIcon);
			nameLabel.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getNoteObject())));
			sizeLabel.setText("");
			fullPathLabel.setText("");
			try {
				lastAccessTimeAndUser.setText(TimeUtilities.getRelativeTime(JakeMainApp.getCore().getLastEdit(getNoteObject())));
			} catch (NoteOperationFailedException e) {
				log.warn("error getting time for noteobject", e);
			}

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

		eventsTable.updateUI();
		;
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

	@Override
	public void noteSelectionChanged(NoteSelectedEvent event) {
		log.info("Inspector: Note Selection Changed: " + event);

		setNoteObject(event.getSingleNote());
		updatePanel();
	}
}


