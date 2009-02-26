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
	private Project project;
	private ResourceMap resourceMap;
	private Attributed<FileObject> attributedFileObject;
	private Attributed<NoteObject> attributedNoteObject;

	private enum Mode {
		FILE, NOTE, NONE
	}

	private Mode mode;
	private JXTable eventsTable;
	private JLabel iconLabel;
	private JLabel nameValue;
	private JLabel sizeValue;
	private JLabel lastEditedValue;
	private JLabel tagsValue;
	private JLabel fullPathValue;
	private EventsTableModel eventsTableModel;
	private JPanel headerPanel;
	private final Icon notesIcon = new ImageIcon(Toolkit.getDefaultToolkit()
			.getImage(getClass().getResource("/icons/notes.png"))
			.getScaledInstance(64, 64, Image.SCALE_SMOOTH));

	private JPanel metaPanel;
	private JLabel lastEditedLabel;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private JPanel noteFileInspector;
	private JPanel emptyInspector;
	private ProjectViewPanelEnum projectViewPanel;
	private JLabel lastEditorLabel;
	private JLabel lastEditorValue;
	private SmallLabel lockedByLabel;
	private SmallLabel lockedByValue;
	private SmallLabel sharedLabel;
	private JCheckBox sharedValue;

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
		this.iconLabel = new JLabel();
		this.headerPanel.add(this.iconLabel, "w 64!, h 64!");
		this.nameValue = new JLabel();
		this.headerPanel.add(this.nameValue);
		this.sizeValue = new JLabel();
		this.headerPanel.add(this.sizeValue, "growx");

		// meta panel
		this.metaPanel = new JPanel(new MigLayout("fill, wrap 2"));
		this.metaPanel.setOpaque(false);

		this.lastEditedLabel = new SmallLabel(getResourceMap().getString("lastEditedLabel"));
		this.metaPanel.add(this.lastEditedLabel, "right");
		this.lastEditedValue = new SmallLabel();
		this.metaPanel.add(this.lastEditedValue, "growx");
		
		this.lastEditorLabel = new SmallLabel(getResourceMap().getString("lastEditorLabel"));
		this.metaPanel.add(this.lastEditorLabel, "right");
		this.lastEditorValue = new SmallLabel();
		this.metaPanel.add(this.lastEditorValue, "growx"	);
		
		this.tagsLabel = new SmallLabel(getResourceMap().getString("tagsLabel"));
		this.metaPanel.add(this.tagsLabel, "right, hidemode 2");
		this.tagsValue = new SmallLabel();
		this.metaPanel.add(this.tagsValue, "growx, hidemode 2");
		
		this.sharedLabel = new SmallLabel(getResourceMap().getString("sharedLabel"));
		this.metaPanel.add(this.sharedLabel, "right");
		this.sharedValue = new JCheckBox();
		this.metaPanel.add(this.sharedValue, "growx");
		
		this.lockedByLabel = new SmallLabel(getResourceMap().getString("lockedByLabel"));
		this.metaPanel.add(this.lockedByLabel, "right");
		this.lockedByValue = new SmallLabel();
		this.metaPanel.add(this.lockedByValue, "growx");

		this.fullPathLabel = new SmallLabel(getResourceMap().getString("pathLabel"));
		this.metaPanel.add(this.fullPathLabel, "right, hidemode 2");
		this.fullPathValue = new SmallLabel();
		this.metaPanel.add(this.fullPathValue, "grow, hidemode 2");

		// events table
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();


		// assembly
		this.noteFileInspector = new JPanel(new MigLayout("wrap 1, fill"));
		this.noteFileInspector.setOpaque(false);
		this.noteFileInspector.add(this.headerPanel, "growx");
		this.noteFileInspector.add(this.metaPanel, "growx");
		this.noteFileInspector.add(this.eventsTable, "dock south, grow");
		this.noteFileInspector.setVisible(false);

		this.emptyInspector = new JPanel();
		this.emptyInspector.setOpaque(false);
		this.emptyInspector.setLayout(new MigLayout("fill"));
		JLabel spoon = new JLabel("There is no spoon!");// FIXME: randomness, i18n
		this.emptyInspector.add(spoon, "center");

		this.setLayout(new MigLayout("wrap 1, fill"));
		this.add(this.noteFileInspector, "hidemode 2");
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
					this.iconLabel.setIcon(Platform.getToolkit().getFileIcon(file, 64));
					this.nameValue.setText(StringUtilities.htmlize(StringUtilities.bold(
							  FileObjectHelper.getName(getAttributedFileObject().getJakeObject().getRelPath()))));
	
				this.fullPathLabel.setText(
						  FileObjectHelper.getPath(getAttributedFileObject().getJakeObject().getRelPath()));
				log.debug(this.fullPathLabel.getText());
				this.lastEditedValue.setText(TimeUtilities.getRelativeTime(this.getAttributedFileObject().getLastModificationDate()));
				this.getEventsTableModel().setJakeObject(getAttributedFileObject());
				this.emptyInspector.setVisible(false);
				this.noteFileInspector.setVisible(true);
				break;
			case NOTE:
				this.iconLabel.setIcon(this.notesIcon);
				this.nameValue.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getAttributedNoteObject().getJakeObject())));
				this.sizeValue.setVisible(false);
				if (this.getAttributedNoteObject().getLastVersionEditor() != null) {
					this.lastEditorValue.setText(this.getAttributedNoteObject().getLastVersionEditor().toString());	
				} else {
					this.lastEditorValue.setText("local"); //FIXME: elaborate, i18n
				}
				
				this.fullPathLabel.setVisible(false);
				this.fullPathValue.setVisible(false);
				this.tagsLabel.setVisible(false);
				this.tagsValue.setVisible(false);
				this.lastEditedValue.setText(TimeUtilities.getRelativeTime(getAttributedNoteObject().getLastModificationDate()));
				this.sharedValue.setEnabled(getAttributedNoteObject().isOnlyLocal());
				if (this.getAttributedNoteObject().isLocked()) {
					this.lockedByValue.setText(this.getAttributedNoteObject().getLockLogEntry().getMember().toString());
				} else {
					this.lockedByValue.setVisible(false);
					this.lockedByLabel.setVisible(false);
				}
				
				this.noteFileInspector.setVisible(true);
				this.emptyInspector.setVisible(false);
					
				break;
			case NONE:
			default:
				this.noteFileInspector.setVisible(false);
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

	public Attributed<NoteObject> getAttributedNoteObject() {
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
