package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.JakeMainView.ProjectViewPanelEnum;
import com.jakeapp.gui.swing.callbacks.*;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
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
 * Inspector Panel Shows extended File/Notes Info: For File: Icon, Name, , Size,
 * Tags, full path Common: Last access time+user, History Table
 * 
 * @author: studpete, simon
 */
public class InspectorPanel extends JXPanel implements ProjectChanged, ProjectSelectionChanged,
		FileSelectionChanged, ProjectViewChanged, NoteSelectionChanged {

	private static final long serialVersionUID = 7743765581263700424L;
	private static final Logger log = Logger.getLogger(InspectorPanel.class);
	public static final int INSPECTOR_SIZE = 250;
	private final Font smallFont = UIManager.getFont("Label.font").deriveFont(12);
	private Project project;
	private ResourceMap resourceMap;
	private Attributed<FileObject> attributedFileObject;
	private Attributed<NoteObject> attributedNoteObject;
	private enum Mode {
		FILE, NOTE, NONE;
	}

	private Mode mode;
	private JXTable eventsTable;
	private JLabel icoLabel;
	private JLabel nameLabel;
	private JLabel sizeLabel;
	private JLabel lastEditTimeAndUser;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private EventsTableModel eventsTableModel;
	private JPanel headerPanel;
	private final Icon notesIcon = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("/icons/notes.png"))
			.getScaledInstance(64, 64, Image.SCALE_SMOOTH));

	private JPanel noteMetaPanel;
	private JLabel lastEditTextLabel;
	private JLabel tagsHeaderLabel;
	private JLabel fullPathTextLabel;
	private JPanel NoteInspector;
	private JPanel FileInspector;
	private JPanel NoneInspector;
	private ProjectViewPanelEnum projectViewPanel;

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(JakeMainApp.class).getContext()
				.getResourceMap(InspectorPanel.class));

		// register for events
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		FilePanel.getInstance().addFileSelectionListener(this);
		NotesPanel.getInstance().addNoteSelectionListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);

		this.mode = Mode.NONE;

		initComponents();
	}

	private void initComponents() {

		// header panel
		this.headerPanel = new JPanel(new MigLayout("fillx"));
		this.headerPanel.setOpaque(false);
		this.icoLabel = new JLabel();
		this.headerPanel.add(this.icoLabel, "w 64!, h 64!");
		this.nameLabel = new JLabel();
		this.headerPanel.add(this.nameLabel);
		this.sizeLabel = new JLabel();
		this.headerPanel.add(this.sizeLabel, "wrap, growx");

		// meta panel
		this.noteMetaPanel = new JPanel(new MigLayout("fillx"));
		this.noteMetaPanel.setOpaque(false);

		this.lastEditTextLabel = new JLabel(getResourceMap().getString("modifiedLabel"));
		this.lastEditTextLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditTextLabel, "right");

		this.lastEditTimeAndUser = new JLabel();
		this.lastEditTimeAndUser.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditTimeAndUser, "wrap, growx");

		this.tagsHeaderLabel = new JLabel(getResourceMap().getString("tagsLabel"));
		this.noteMetaPanel.add(this.tagsHeaderLabel, "right");

		this.tagsLabel = new JLabel("");
		this.noteMetaPanel.add(this.tagsLabel, "wrap, growx");

		// add full path
		this.fullPathTextLabel = new JLabel(getResourceMap().getString("pathLabel"));
		this.fullPathTextLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.fullPathTextLabel, "right");

		this.fullPathLabel = new JLabel();
		this.fullPathLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.fullPathLabel, "wrap, growx");

		// events table
		// FIXME: pass a jakeObject to the EventTableModel c'tor.
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();


		// assembly
		this.NoteInspector = new JPanel();
		this.NoteInspector.setOpaque(false);
		this.NoteInspector.setLayout(new MigLayout("debug, wrap 1, fill"));
		this.NoteInspector.add(this.headerPanel, "growx");
		this.NoteInspector.add(this.noteMetaPanel, "growx");
		this.NoteInspector.add(this.eventsTable, "dock south, growy");
		this.NoteInspector.setVisible(false);

		this.FileInspector = new JPanel();
		this.FileInspector.setOpaque(false);
		this.FileInspector.setLayout(new MigLayout("debug, wrap 1, fill"));
		this.FileInspector.add(this.headerPanel, "growx");
		this.FileInspector.add(this.eventsTable, "dock south, growy");
		this.FileInspector.setVisible(false);

		this.NoneInspector = new JPanel();
		this.NoneInspector.setOpaque(false);
		this.NoneInspector.setLayout(new MigLayout("fill"));
		// FIXME: randomness, i18n
		JLabel spoon = new JLabel();
		spoon.setText("There is no spoon!");
		this.NoneInspector.add(spoon, "center");

		this.setLayout(new MigLayout("debug, wrap 1, fillx"));
		this.add(this.NoteInspector, "hidemode 2");
		this.add(this.FileInspector, "hidemode 2");
		this.add(this.NoneInspector, "hidemode 2");


		// not resizeable, fixed width
		Dimension absSize = new Dimension(INSPECTOR_SIZE, INSPECTOR_SIZE);
		this.setMinimumSize(new Dimension(100, 100));
		// this.setMaximumSize(new Dimension((int) absSize.getWidth(),
		// Integer.MAX_VALUE));
		// this.setPreferredSize(absSize);

		this.setBackground(Platform.getStyler().getWindowBackground());

		updatePanel();
	}

	/**
	 * Updates the panel with current data
	 */
	public void updatePanel() {
		this.updateMode();
		log.debug("mode: " + this.mode);
		switch(this.mode) {
			case FILE:
					File file = null;
					try {
						file = JakeMainApp.getCore().getFile(getAttributedFileObject().getJakeObject());
					} catch (FileOperationFailedException e) {
						ExceptionUtilities.showError(e);
					}
					this.icoLabel.setIcon(Platform.getToolkit().getFileIcon(file, 64));
					this.nameLabel.setText(StringUtilities.htmlize(StringUtilities.bold(
							  FileObjectHelper.getName(getAttributedFileObject().getJakeObject().getRelPath()))));
	/*
				this.fullPathLabel.setText(
						  FileObjectHelper.getPath(getAttributedFileObject().getJakeObject().getRelPath()));
				log.debug(this.fullPathLabel.getText());
				this.lastAccessTimeAndUser.setText(StringUtilities.htmlize(Translator.get(getResourceMap(), "byLabel",
						  FileObjectHelper.getTimeRel(getAttributedFileObject().getJakeObject()),
						  FileObjectHelper.getLastModifier(getAttributedFileObject().getJakeObject()))));
	*/
				this.getEventsTableModel().setJakeObject(getAttributedFileObject());
				this.NoneInspector.setVisible(false);
				this.NoteInspector.setVisible(false);
				this.FileInspector.setVisible(true);
				break;
			case NOTE:
				this.icoLabel.setIcon(this.notesIcon);
				this.nameLabel.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getNoteObject().getJakeObject())));
				this.sizeLabel.setText("");
				this.fullPathLabel.setText("");
				this.lastEditTimeAndUser.setText(TimeUtilities.getRelativeTime(getNoteObject().getLastModificationDate()));

				this.NoteInspector.setVisible(true);
				this.FileInspector.setVisible(false);
				this.NoneInspector.setVisible(false);
					
				break;
			case NONE:
			default:
				this.NoteInspector.setVisible(false);
				this.FileInspector.setVisible(false);
				this.NoneInspector.setVisible(true);
			}

		this.eventsTable.updateUI();
	}

	protected ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	protected void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public Attributed<FileObject> getAttributedFileObject() {
		return this.attributedFileObject;
	}

	public void setFileObject(Attributed<FileObject> attributedFileObject) {
		this.attributedFileObject = attributedFileObject;
		this.mode = Mode.FILE;

		updatePanel();
	}

	@Override
	public void projectChanged(ProjectChangedEvent ev) {
		updatePanel();
	}

	@Override
	public void setProject(Project project) {
		this.project = project;
		this.getEventsTableModel().setProject(project);

		updatePanel();
	}

	public Project getProject() {
		return this.project;
	}

	@Override
	public void fileSelectionChanged(FileSelectedEvent event) {
		this.setFileObject(event.getSingleFile());
	}

	public Attributed<NoteObject> getNoteObject() {
		return this.attributedNoteObject;
	}

	public void setNoteObject(Attributed<NoteObject> noteObject) {
		this.attributedNoteObject = noteObject;
		this.mode = Mode.NOTE;
		this.getEventsTableModel().setJakeObject(noteObject);
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
		return this.projectViewPanel;
	}

	@Override
	public void noteSelectionChanged(NoteSelectedEvent event) {
		log.info("Inspector: Note Selection Changed: " + event);

		setNoteObject(event.getSingleNote());
		updatePanel();
	}

	private EventsTableModel getEventsTableModel() {
		return this.eventsTableModel;
	}

	private void setEventsTableModel(EventsTableModel eventsTableModel) {
		this.eventsTableModel = eventsTableModel;
	}
	
	//FIXME: DRY, merge this.mode and this.projectViewPanel to make things simpler. Altogether, this
	// solution for determining the current 'mode' of the inspector sucks.
	// see updateMode(), setProjectViewPanel(), this.mode, this.projectViewPanel, updatePanel()
	private void updateMode() {
		//FIXME: damn initialization, evade this hack with proper initialization.
		if (this.projectViewPanel != null) {
			switch(this.projectViewPanel) {
				case Files:
					if (this.attributedFileObject != null) {
						this.mode = Mode.FILE;
					} else {
						this.mode = Mode.NONE;
					}
					break;
				case Notes:
					if (this.attributedNoteObject != null) {
						this.mode = Mode.NOTE;
					}
					else {
						this.mode = Mode.NONE;
					}
					break;
				case News:
				default:
					this.mode = Mode.NONE;
			}
		}
		log.debug("projectViewPanel: " + this.projectViewPanel);
		log.debug("attributedFileObject: " + this.attributedFileObject + ", attributedNoteObject: " + this.attributedNoteObject);
		log.debug("mode has been set to: " + this.mode);
	}
}
