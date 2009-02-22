package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.Attributed;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
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
 * Inspector Panel
 * Shows extended File/Notes Info:
 * For File: Icon, Name, , Size, Tags, full path
 * Common: Last access time+user, History Table
 *
 * @author: studpete, simon
 */
public class InspectorPanel extends JXPanel implements
		  ProjectChanged, ProjectSelectionChanged, FileSelectionChanged, ProjectViewChanged, NoteSelectionChanged {

	private static final long serialVersionUID = 7743765581263700424L;
	private static final Logger log = Logger.getLogger(InspectorPanel.class);

	public static final int INSPECTOR_SIZE = 250;
	
	private final Font smallFont = UIManager.getFont("Label.font").deriveFont(12);
	
	private Project project;
	private ResourceMap resourceMap;
	private JakeMainView.ProjectViewPanelEnum projectViewPanel;
	private Attributed<FileObject> attributedFileObject;
	private Attributed<NoteObject> attributedNoteObject;

	private JXTable eventsTable;
	private JLabel icoLabel;
	private JLabel nameLabel;
	private JLabel sizeLabel;
	private JLabel lastEditTimeAndUser;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private EventsTableModel eventsTableModel;
	private JPanel headerPanel;

	private Icon notesIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
			  getClass().getResource("/icons/notes.png")).getScaledInstance(64, 64, Image.SCALE_SMOOTH));
	private JPanel metaPanel;
	private JLabel lastEditTextLabel;
	private JLabel tagsHeaderLabel;
	private JLabel fullPathTextLabel;

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
		
		//header panel
		this.headerPanel = new JPanel(new MigLayout("fillx"));
		this.headerPanel.setOpaque(false);
		this.icoLabel = new JLabel();
		this.headerPanel.add(this.icoLabel, "hidemode 1, w 64!, h 64!");
		this.nameLabel = new JLabel();
		this.headerPanel.add(this.nameLabel);
		this.sizeLabel = new JLabel();
		this.headerPanel.add(this.sizeLabel, "wrap, growx");

		//meta panel
		this.metaPanel = new JPanel(new MigLayout("fillx"));
		this.metaPanel.setOpaque(false);
		
		this.lastEditTextLabel = new JLabel(getResourceMap().getString("modifiedLabel"));
		this.lastEditTextLabel.setFont(this.smallFont);
		this.metaPanel.add(this.lastEditTextLabel, "right");
		
		this.lastEditTimeAndUser = new JLabel();
		this.lastEditTimeAndUser.setFont(this.smallFont);
		this.metaPanel.add(this.lastEditTimeAndUser, "wrap, growx");

		this.tagsHeaderLabel = new JLabel(getResourceMap().getString("tagsLabel"));
		this.metaPanel.add(this.tagsHeaderLabel, "right");
		
		this.tagsLabel = new JLabel("");
		this.metaPanel.add(this.tagsLabel, "wrap, growx");

		// add full path
		this.fullPathTextLabel = new JLabel(getResourceMap().getString("pathLabel"));
		this.fullPathTextLabel.setFont(this.smallFont);
		this.metaPanel.add(this.fullPathTextLabel, "right");
		
		this.fullPathLabel = new JLabel();
		this.fullPathLabel.setFont(this.smallFont);
		this.metaPanel.add(this.fullPathLabel, "wrap, growx");

		// events table
		//FIXME: pass a jakeObject to the EventTableModel c'tor.
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();
		
		
		//assembly
		this.setLayout(new MigLayout("debug, wrap 1, fillx"));
		this.add(this.headerPanel, "growx");
		this.add(this.metaPanel, "growx");
		this.add(this.eventsTable, "dock south, growy");
		
		// not resizeable, fixed width
		Dimension absSize = new Dimension(INSPECTOR_SIZE, INSPECTOR_SIZE);
		this.setMinimumSize(new Dimension(100, 100));
		//this.setMaximumSize(new Dimension((int) absSize.getWidth(), Integer.MAX_VALUE));
		//this.setPreferredSize(absSize);

		this.setBackground(Platform.getStyler().getWindowBackground());
		
		updatePanel();
	}

	/**
	 * Updates the panel with current data
	 */
	public void updatePanel() {

		// always update the table
		// HACK: disable for DEMO
		this.getEventsTableModel().setProject(null);

		if (getAttributedFileObject() != null && isFileContext()) {
			File file = null;
			try {
				file = JakeMainApp.getCore().getFile(getAttributedFileObject().getJakeObject());
			} catch (FileOperationFailedException e) {
				ExceptionUtilities.showError(e);
			}
			this.icoLabel.setIcon(Platform.getToolkit().getFileIcon(file, 64));

			this.nameLabel.setText(StringUtilities.htmlize(StringUtilities.bold(
					  FileObjectHelper.getName(getAttributedFileObject().getJakeObject().getRelPath()))));

			// TODO: fix!
	//		this.sizeLabel.setText(FileObjectHelper.getSizeHR(getAttributedFileObject().getJakeObject()));

			// TODO: @Chris: update tags
/*
			this.fullPathLabel.setText(
					  FileObjectHelper.getPath(getAttributedFileObject().getJakeObject().getRelPath()));
			log.debug(this.fullPathLabel.getText());
			this.lastAccessTimeAndUser.setText(StringUtilities.htmlize(Translator.get(getResourceMap(), "byLabel",
					  FileObjectHelper.getTimeRel(getAttributedFileObject().getJakeObject()),
					  FileObjectHelper.getLastModifier(getAttributedFileObject().getJakeObject()))));
*/
			this.getEventsTableModel().setJakeObject(getAttributedFileObject());
		} else if (getNoteObject() != null && isNoteContext()) {
			this.icoLabel.setIcon(this.notesIcon);
			this.nameLabel.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getNoteObject().getJakeObject())));
			this.sizeLabel.setText("");
			this.fullPathLabel.setText("");

		  this.lastEditTimeAndUser.setText(TimeUtilities.getRelativeTime(getNoteObject().getLastModificationDate()));


			// TODO: inspector for notes
			this.getEventsTableModel().setJakeObject(getAttributedFileObject());
		} else {
			this.icoLabel.setIcon(null);
			this.nameLabel.setText("");
			this.sizeLabel.setText("");
			this.fullPathLabel.setText("");
			this.lastEditTimeAndUser.setText("");

			// hide table contents
			this.getEventsTableModel().setProject(null);
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
		this.getEventsTableModel().setJakeObject(noteObject);
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
	
	
}


