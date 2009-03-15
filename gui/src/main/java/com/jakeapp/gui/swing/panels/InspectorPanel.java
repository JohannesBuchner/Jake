package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.FileSelectionChangedCallback;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectViewChangedCallback;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.controls.SmallLabel;
import com.jakeapp.gui.swing.controls.SmallShortenedLabel;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.helpers.ConfigControlsHelper;
import com.jakeapp.gui.swing.helpers.ExceptionUtilities;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.helpers.ImageLoader;
import com.jakeapp.gui.swing.helpers.NotesHelper;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.StringUtilities;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.models.EventsTableModel;
import com.jakeapp.gui.swing.xcore.EventCore;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.EnumSet;

/**
 * Inspector Panel Shows extended File/Notes Info: For File: Icon, Name, , Size,
 * Tags, full path Common: Last access time+user, History Table
 *
 * @author studpete, simon
 */
public class InspectorPanel extends JXPanel
				implements ProjectChangedCallback, ContextChangedCallback, FileSelectionChangedCallback,
				ProjectViewChangedCallback, NoteSelectionChangedCallback {

	private static final long serialVersionUID = 7743765581263700424L;
	private static final Logger log = Logger.getLogger(InspectorPanel.class);
	public static final int INSPECTOR_SIZE = 250;
	private ResourceMap resourceMap;
	private Attributed<FileObject> attributedFileObject;
	private Attributed<NoteObject> attributedNoteObject;
	private JScrollPane eventsTableScrollPane;

	private enum Mode {
		FILE, NOTE, NONE
	}

	private Mode mode;
	private JXTable eventsTable;
	private JLabel icon;
	private JLabel nameValue;
	private JLabel sizeValue;
	private JLabel fullPathValue;
	private JLabel lastEditedLabel;
	private JLabel lastEditedValue;
	private JLabel lastEditorLabel;
	private JLabel lastEditorValue;
	private JLabel uuidLabel;
	private JLabel uuidValue;
	private JLabel lockedByLabel;
	private JLabel lockedByValue;
	private JLabel sharedLabel;
	private JLabel sharedValue;

	private EventsTableModel eventsTableModel;
	private JPanel headerPanel;
	private final Icon notesIcon = ImageLoader.getScaled(getClass(), "/icons/notes.png", 64);

	private JPanel metaPanel;
	private JPanel noteFileInspector;
	private JPanel emptyInspector;

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application
						.getInstance(JakeMainApp.class).getContext().getResourceMap(
						InspectorPanel.class));

		// register for events
		EventCore.get().addProjectChangedCallbackListener(this);
		EventCore.get().addContextChangedListener(this);
		EventCore.get().addFileSelectionListener(this);
		NotesPanel.getInstance().addNoteSelectionListener(this);
		JakeMainView.getMainView().addProjectViewChangedListener(this);

		this.mode = Mode.NONE;

		initComponents();
	}

	private void initComponents() {

		// header panel
		this.headerPanel = new JPanel(new MigLayout("fill"));
		this.headerPanel.setOpaque(false);
		
		this.icon = new JLabel();
		this.headerPanel.add(this.icon, "w 64!, h 64!, span 2");
		
		this.nameValue = new JLabel();
		this.headerPanel.add(this.nameValue, "top");
		
		this.sizeValue = new JLabel();
		this.headerPanel.add(this.sizeValue, "growx, wrap, top");
		
		this.fullPathValue = new SmallShortenedLabel();
		this.headerPanel.add(this.fullPathValue);
		

		// meta panel
		this.metaPanel = new JPanel(new MigLayout("fill, wrap 2"));
		this.metaPanel.setOpaque(false);

		this.lastEditedLabel = new SmallLabel(getResourceMap().getString("lastEditedLabel"));
		this.metaPanel.add(this.lastEditedLabel, "right");
		this.lastEditedValue = new SmallLabel();
		this.metaPanel.add(this.lastEditedValue, "growx");

		this.lastEditorLabel = new SmallLabel(getResourceMap().getString("lastEditorLabel"));
		this.metaPanel.add(this.lastEditorLabel, "right");
		this.lastEditorValue = new SmallShortenedLabel();
		this.metaPanel.add(this.lastEditorValue, "growx");

		this.sharedLabel = new SmallLabel(getResourceMap().getString("sharedLabel"));
		this.metaPanel.add(this.sharedLabel, "right");
		this.sharedValue = new SmallShortenedLabel();
		this.metaPanel.add(this.sharedValue, "growx");

		this.lockedByLabel = new SmallLabel(getResourceMap().getString("lockedByLabel"));
		this.metaPanel.add(this.lockedByLabel, "right");
		this.lockedByValue = new SmallShortenedLabel();
		this.metaPanel.add(this.lockedByValue, "growx");
		
		this.uuidLabel = new SmallLabel(getResourceMap().getString("uuidLabel"));
		this.metaPanel.add(this.uuidLabel, "right");
		this.uuidValue = new SmallShortenedLabel();
		this.metaPanel.add(this.uuidValue, "growx");

		// events table
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();
		this.eventsTableScrollPane = new JScrollPane(this.eventsTable);

		// assembly
		this.noteFileInspector = new JPanel(new MigLayout("wrap 1, fill, ins 0"));
		this.noteFileInspector.setOpaque(false);
		this.noteFileInspector.add(this.headerPanel, "growx");
		this.noteFileInspector.add(this.metaPanel, "growx");
		this.noteFileInspector.setVisible(false);

		this.emptyInspector = new JPanel();
		this.emptyInspector.setOpaque(false);
		this.emptyInspector.setLayout(new MigLayout("fill"));
		JLabel spoon = new JLabel("There is no spoon!");// FIXME: randomness, i18n
		this.emptyInspector.add(spoon, "center");


		this.setLayout(new MigLayout("wrap 1, fill, ins 0"));
		this.add(this.noteFileInspector, "hidemode 2");
		this.add(this.emptyInspector, "hidemode 2");


		this.add(this.eventsTableScrollPane, "dock south, grow");


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
		log.trace("mode: " + this.mode);

		// HACK: quick fix
		if ((mode == Mode.FILE && attributedFileObject == null) || (mode == Mode.NOTE && attributedNoteObject == null)) {
			mode = Mode.NONE;
		}

		switch (this.mode) {
			case FILE:
				this.fullPathValue.setVisible(true);
				this.sizeValue.setVisible(true);
				
				this.emptyInspector.setVisible(false);
				this.noteFileInspector.setVisible(true);
				break;
			case NOTE:
				this.sizeValue.setVisible(false);
				this.fullPathValue.setVisible(false);
								
				this.emptyInspector.setVisible(false);
				this.noteFileInspector.setVisible(true);
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
		if (attributedFileObject != null) {
			this.mode = Mode.FILE;
			this.getEventsTableModel().setJakeObject(attributedFileObject);
			
			//update fields...
			this.updateCommonFields(attributedFileObject);
			
			File file = null;
			try {
				file = JakeMainApp.getCore().getFile(getAttributedFileObject().getJakeObject());
			} catch (FileOperationFailedException e) {
				ExceptionUtilities.showError(e);
			}
			this.icon.setIcon(Platform.getToolkit().getFileIcon(file, 64));
			this.nameValue.setText(StringUtilities.htmlize(StringUtilities.bold(
							FileObjectHelper.getName(getAttributedFileObject()
											.getJakeObject().getRelPath()))));

			this.fullPathValue.setText(FileObjectHelper.getPath(getAttributedFileObject()
							.getJakeObject().getRelPath()));
		} else {
			this.mode = Mode.NONE;
		}

		updatePanel();
	}

	@Override
	public void projectChanged(ProjectChangedEvent ev) {
		updatePanel();
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		if (reason.contains(Reason.Project)) {
			this.getEventsTableModel().setProject(this.getProject());
			updatePanel();
		}
	}

	public Project getProject() {
		return JakeContext.getProject();
	}

	@Override
	public void fileSelectionChanged(FileSelectedEvent event) {
		log.debug("received a fileSelectedEvent from the EventCore");
		if (event.isSingleFileSelected()) {
			this.setFileObject(JakeMainApp.getCore().getAttributed(this.getProject(),
							event.getSingleFile()));
		} else {
			this.clear();
		}
	}


	private void clear() {
		this.mode = Mode.NONE;
	}

	public Attributed<NoteObject> getAttributedNoteObject() {
		return this.attributedNoteObject;
	}

	public void setNoteObject(Attributed<NoteObject> attributedNoteObject) {
		this.attributedNoteObject = attributedNoteObject;
		if (attributedNoteObject != null) {
			this.mode = Mode.NOTE;
			this.getEventsTableModel().setJakeObject(attributedNoteObject);
			
			// update fields
			this.updateCommonFields(attributedNoteObject);
			this.icon.setIcon(this.notesIcon);
			this.nameValue.setText(StringUtilities.htmlize(NotesHelper.getTitle(
							getAttributedNoteObject().getJakeObject())));
		} else {
			this.mode = Mode.NONE;
		}
	}
	
	private void updateCommonFields(Attributed<? extends JakeObject> attributedJakeObject) {
		if (this.getAttributedNoteObject().getLastVersionEditor() != null) {
			this.lastEditorValue.setText(attributedJakeObject.getLastVersionEditor().toString());
		} else {
			this.lastEditorValue.setText("local"); //FIXME: elaborate, i18n
		}
		
		this.lastEditedValue	.setText(TimeUtilities.getRelativeTime(attributedJakeObject
				.getLastModificationDate()));
		
		this.sharedValue.setText(Boolean.toString((attributedJakeObject.isOnlyLocal())));

		if (this.getAttributedNoteObject().isLocked()) {
			this.lockedByValue.setText(attributedJakeObject.getLockLogEntry()
							.getMember().toString());
		} else {
			this.lockedByValue.setText("-");
		}
		this.uuidValue.setText(attributedJakeObject.getJakeObject().getUuid().toString());
	}
	
	@Override
	public void setProjectViewPanel(JakeMainView.ProjectView projectViewPanel) {

		if (projectViewPanel == JakeMainView.ProjectView.Files)
			this.mode = Mode.FILE;
		else if (projectViewPanel == JakeMainView.ProjectView.Notes)
			this.mode = Mode.NOTE;
		else
			this.mode = Mode.NONE;

		updatePanel();
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
}
