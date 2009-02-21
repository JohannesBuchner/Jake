package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.AttributedJakeObject;
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
 * @author: studpete
 */
public class InspectorPanel extends JXPanel implements
		  ProjectChanged, ProjectSelectionChanged, FileSelectionChanged, ProjectViewChanged, NoteSelectionChanged {

	private static final long serialVersionUID = 7743765581263700424L;
	private static final Logger log = Logger.getLogger(InspectorPanel.class);

	public static final int INSPECTOR_SIZE = 250;
	
	private Project project;
	private ResourceMap resourceMap;
	private JakeMainView.ProjectViewPanelEnum projectViewPanel;
	private AttributedJakeObject<FileObject> attributedFileObject;
	private AttributedJakeObject<NoteObject> attributedNoteObject;

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

		//FIXME: pass a jakeObject to the EventTableModel c'tor.
		this.setEventsTableModel(new EventsTableModel(getProject()));
		this.eventsTable = new ITunesTable();
		this.eventsTable.setModel(this.getEventsTableModel());
		this.eventsTable.setMinimumSize(new Dimension(50, INSPECTOR_SIZE));
		ConfigControlsHelper.configEventsTable(this.eventsTable);
		this.eventsTable.updateUI();
		this.add(this.eventsTable, "dock south, grow 150 150, gpy 150");

		JPanel headerPanel = new JPanel(new MigLayout("wrap 1, fill"));
		headerPanel.setOpaque(false);

		// add icon
		this.icoLabel = new JLabel();
		headerPanel.add(this.icoLabel, "dock west, hidemode 1, w 64!, h 64!");

		// add name
		this.nameLabel = new JLabel();
		headerPanel.add(this.nameLabel, "wrap, grow");

		// add size
		this.sizeLabel = new JLabel();
		headerPanel.add(this.sizeLabel, "wrap, grow");

		this.add(headerPanel, "span 2, wrap, grow");

		Font smallFont = UIManager.getFont("Label.font").deriveFont(12);

		// last time+user modified
		JLabel lastAccessTextLabel = new JLabel(getResourceMap().getString("modifiedLabel"));
		lastAccessTextLabel.setFont(smallFont);
		this.add(lastAccessTextLabel, "right");
		this.lastAccessTimeAndUser = new JLabel();
		this.lastAccessTimeAndUser.setFont(smallFont);
		this.add(this.lastAccessTimeAndUser, "growy, wrap");

		// add tags
		JLabel tagsHeaderLabel = new JLabel(getResourceMap().getString("tagsLabel"));
		this.add(tagsHeaderLabel, "right");
		this.tagsLabel = new JLabel("");
		this.add(this.tagsLabel, "growy, wrap");

		// add full path
		JLabel fullPathTextLabel = new JLabel(getResourceMap().getString("pathLabel"));
		fullPathTextLabel.setFont(smallFont);
		this.add(fullPathTextLabel, "right");
		this.fullPathLabel = new JLabel();
		this.fullPathLabel.setFont(smallFont);
		this.add(this.fullPathLabel, "growy, wrap");

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

			this.sizeLabel.setText(FileObjectHelper.getSizeHR(getAttributedFileObject().getJakeObject()));

			// TODO: @Chris: update tags

			this.fullPathLabel.setText(
					  FileObjectHelper.getPath(getAttributedFileObject().getJakeObject().getRelPath()));
			log.debug(this.fullPathLabel.getText());
			this.lastAccessTimeAndUser.setText(StringUtilities.htmlize(Translator.get(getResourceMap(), "byLabel",
					  FileObjectHelper.getTimeRel(getAttributedFileObject().getJakeObject()),
					  FileObjectHelper.getLastModifier(getAttributedFileObject().getJakeObject()))));

			this.getEventsTableModel().setJakeObject(getAttributedFileObject());
		} else if (getNoteObject() != null && isNoteContext()) {
			this.icoLabel.setIcon(this.notesIcon);
			this.nameLabel.setText(StringUtilities.htmlize(NoteObjectHelper.getTitle(getNoteObject().getJakeObject())));
			this.sizeLabel.setText("");
			this.fullPathLabel.setText("");

		  this.lastAccessTimeAndUser.setText(TimeUtilities.getRelativeTime(getNoteObject().getLastModificationDate()));


			// TODO: inspector for notes
			this.getEventsTableModel().setJakeObject(getAttributedFileObject());
		} else {
			this.icoLabel.setIcon(null);
			this.nameLabel.setText("");
			this.sizeLabel.setText("");
			this.fullPathLabel.setText("");
			this.lastAccessTimeAndUser.setText("");

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

	public AttributedJakeObject<FileObject> getAttributedFileObject() {
		return this.attributedFileObject;
	}

	public void setFileObject(AttributedJakeObject<FileObject> attributedFileObject) {
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

	public AttributedJakeObject<NoteObject> getNoteObject() {
		return this.attributedNoteObject;
	}

	public void setNoteObject(AttributedJakeObject<NoteObject> noteObject) {
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


