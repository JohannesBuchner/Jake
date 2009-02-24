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
	private JLabel lastEditedValue;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private EventsTableModel eventsTableModel;
	private JPanel headerPanel;
	private final Icon notesIcon = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("/icons/notes.png"))
			.getScaledInstance(64, 64, Image.SCALE_SMOOTH));

	private JPanel noteMetaPanel;
	private JLabel lastEditedTextLabel;
	private JLabel tagsHeaderLabel;
	private JLabel fullPathTextLabel;
	private JPanel noteInspector;
	private JPanel fileInspector;
	private JPanel emptyInspector;
	private ProjectViewPanelEnum projectViewPanel;
	private JLabel lastEditorTextLabel;
	private JLabel lastEditorValue;

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(JakeMainApp.class).getContext()
				.getResourceMap(InspectorPanel.class));

		// register for events
		JakeMainApp.getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		FilePanel.getInstance().addFileSelectionListener(this);
		NotesPanel.getInstance().addNoteSelectionListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);

		this.mode = Mode.NONE;

		initComponents();
	}

	private void initComponents() {

		// header panel
		this.headerPanel = new JPanel(new MigLayout("fill"));
		this.headerPanel.setOpaque(false);
		this.icoLabel = new JLabel();
		this.headerPanel.add(this.icoLabel, "w 64!, h 64!");
		this.nameLabel = new JLabel();
		this.headerPanel.add(this.nameLabel);
		this.sizeLabel = new JLabel();
		this.headerPanel.add(this.sizeLabel, "wrap, growx");

		// note meta panel
		this.noteMetaPanel = new JPanel(new MigLayout("fill"));
		this.noteMetaPanel.setOpaque(false);

		this.lastEditedTextLabel = new JLabel(getResourceMap().getString("lastEditedLabel"));
		this.lastEditedTextLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditedTextLabel, "right");
		this.lastEditedValue = new JLabel();
		this.lastEditedValue.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditedValue, "wrap, growx");
		
		this.lastEditorTextLabel = new JLabel(getResourceMap().getString("lastEditorLabel"));
		this.lastEditorTextLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditorTextLabel, "right");
		this.lastEditorValue = new JLabel();
		this.lastEditorValue.setFont(this.smallFont);
		this.noteMetaPanel.add(this.lastEditorValue);


		this.fullPathLabel = new JLabel();
		this.fullPathLabel.setFont(this.smallFont);
		this.noteMetaPanel.add(this.fullPathLabel, "wrap, growx");

		// events table
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();


		// assembly
		this.noteInspector = new JPanel(new MigLayout("wrap 1, fill"));
		this.noteInspector.setOpaque(false);
		this.noteInspector.add(this.headerPanel, "growx");
		this.noteInspector.add(this.noteMetaPanel, "growx");
		this.noteInspector.add(this.eventsTable, "dock south, grow");
		this.noteInspector.setVisible(false);

		this.fileInspector = new JPanel(new MigLayout("wrap 1, fill"));
		this.fileInspector.setOpaque(false);
		this.fileInspector.add(this.headerPanel, "growx");
		this.fileInspector.add(this.eventsTable, "dock south, grow");
		this.fileInspector.setVisible(false);

		this.emptyInspector = new JPanel();
		this.emptyInspector.setOpaque(false);
		this.emptyInspector.setLayout(new MigLayout("fill"));
		JLabel spoon = new JLabel("There is no spoon!");// FIXME: randomness, i18n
		this.emptyInspector.add(spoon, "center");

		this.setLayout(new MigLayout("debug, wrap 1, fillx"));
		this.add(this.noteInspector, "hidemode 2");
		this.add(this.fileInspector, "hidemode 2");
		this.add(this.emptyInspector, "hidemode 2");


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
				this.emptyInspector.setVisible(false);
				this.fileInspector.setVisible(true);
				this.noteInspector.setVisible(false);
				break;
			case NOTE:
				this.icoLabel.setIcon(this.notesIcon);
				this.nameLabel.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getNoteObject().getJakeObject())));
				this.sizeLabel.setText("");
				this.fullPathLabel.setText("");
				this.lastEditedValue.setText(TimeUtilities.getRelativeTime(getNoteObject().getLastModificationDate()));

				this.noteInspector.setVisible(true);
				this.fileInspector.setVisible(true);
				this.emptyInspector.setVisible(true);
					
				break;
			case NONE:
			default:
				this.noteInspector.setVisible(false);
				this.fileInspector.setVisible(false);
				this.emptyInspector.setVisible(true);
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
