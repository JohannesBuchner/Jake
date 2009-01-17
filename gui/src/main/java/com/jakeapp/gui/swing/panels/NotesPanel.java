/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NotesPanel.java
 *
 * Created on Dec 3, 2008, 2:00:15 AM
 */

package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.ICoreAccess;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.CommitNoteAction;
import com.jakeapp.gui.swing.actions.DeleteNoteAction;
import com.jakeapp.gui.swing.actions.NewNoteAction;
import com.jakeapp.gui.swing.actions.SoftlockNoteAction;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.helpers.Colors;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.models.NotesTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.painter.CompoundPainter;
import org.jdesktop.swingx.painter.GlossPainter;
import org.jdesktop.swingx.painter.MattePainter;

import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * @author studpete, simon
 */
public class NotesPanel extends javax.swing.JPanel implements ProjectSelectionChanged, ProjectChanged, ListSelectionListener {

	private static final long serialVersionUID = -7703570005631651276L;
	private static NotesPanel instance;
	private static Logger log = Logger.getLogger(NotesPanel.class);
	private java.util.List<NoteSelectionChanged> noteSelectionListeners = new ArrayList<NoteSelectionChanged>();
	private NotesTableModel notesTableModel;
	private ResourceMap resourceMap;
	private JTextArea noteReader;
	private ICoreAccess core;
	private Project currentProject;

	private class NoteContainerMouseListener extends MouseAdapter {
		private NotesPanel panel;
		private JTable table;
		private NotesTableModel tableModel;
		private JPopupMenu popupMenu;

		{
			this.popupMenu = new JakePopupMenu();
			this.popupMenu.add(new JMenuItem(new NewNoteAction()));
			this.popupMenu.addSeparator();
			this.popupMenu.add(new JMenuItem(new DeleteNoteAction()));
			this.popupMenu.add(new JMenuItem(new CommitNoteAction()));
			this.popupMenu.addSeparator();
			// this.popupMenu.add(new JMenuItem(new SaveNoteAction()));
			this.popupMenu.add(new JMenuItem(new SoftlockNoteAction()));
		}

		public NoteContainerMouseListener(NotesPanel panel, JTable table, NotesTableModel tableModel) {
			super();
			this.panel = panel;
			this.table = table;
			this.tableModel = tableModel;
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			if (SwingUtilities.isRightMouseButton(me)) {
				// get the coordinates of the mouse click
				Point p = me.getPoint();

				// get the row index that contains that coordinate
				int rowNumber = this.table.rowAtPoint(p);

				// Get the ListSelectionModel of the JTable
				ListSelectionModel model = this.table.getSelectionModel();

				// ONLY select new item if we didn't select multiple items.
				if (this.table.getSelectedRowCount() <= 1 && rowNumber != -1) {
					model.setSelectionInterval(rowNumber, rowNumber);
					List<NoteObject> selectedRows = new ArrayList<NoteObject>();
					selectedRows.add(this.tableModel.getNoteAtRow(rowNumber));
					this.panel.notifyNoteSelectionListeners(selectedRows);
				} else if (rowNumber == -1) {
					this.panel.notifyNoteSelectionListeners(new ArrayList<NoteObject>());
				}
				showMenu(me);

			} else if (SwingUtilities.isLeftMouseButton(me)) {
				java.util.List<NoteObject> selectedNotes = new ArrayList<NoteObject>();
				for (int row : this.table.getSelectedRows()) {
					selectedNotes.add(this.tableModel.getNoteAtRow(row));
				}
				this.panel.notifyNoteSelectionListeners(selectedNotes);
			}
		}

		private void showMenu(MouseEvent me) {
			this.popupMenu.show(this.table, (int) me.getPoint().getX(), (int) me.getPoint().getY());
		}
	}

	/**
	 * Creates new form NotesPanel
	 */
	public NotesPanel() {
		instance = this;
		// init components
		initComponents();

		// set up table model
		this.notesTableModel = new NotesTableModel();
		this.notesTable.setModel(this.notesTableModel);

		// register the callbacks
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		this.notesTable.getSelectionModel().addListSelectionListener(this);

		// get resource map
		this.resourceMap = org.jdesktop.application.Application.getInstance(
			 com.jakeapp.gui.swing.JakeMainApp.class).getContext()
			 .getResourceMap(NotesPanel.class);

		//get core
		this.core = JakeMainApp.getCore();


		this.notesTable.setSortable(true);
		this.notesTable.setColumnControlVisible(true);

		// FIXME: set column with for soft lock and shared note
		this.notesTable.getColumnModel().getColumn(0).setResizable(false); // lock
		this.notesTable.getColumnModel().getColumn(0).setMaxWidth(20);
		this.notesTable.getColumnModel().getColumn(1).setResizable(false); // shared note
		this.notesTable.getColumnModel().getColumn(1).setMaxWidth(20);
		this.notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);


		// TODO: make this a styler property
		if (!Platform.isMac()) {
			this.notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}
		final JPopupMenu notesPopupMenu = new JakePopupMenu();

		this.notesTable.addMouseListener(new NoteContainerMouseListener(this, this.notesTable, this.notesTableModel));

		this.noteReader = new JTextArea();
		this.noteReader.setLineWrap(true);
		this.noteReader.setOpaque(false);
		this.noteReader.setText("Enter your Note here.\nChanges will be saved automatically.");
		this.noteReader.setMargin(new Insets(8, 8, 8, 8));

		JScrollPane noteReaderScrollPane = new JScrollPane(this.noteReader);
		noteReaderScrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		noteReaderScrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		noteReaderScrollPane.setOpaque(false);
		noteReaderScrollPane.getViewport().setOpaque(false);
		noteReaderScrollPane.setBorder(new LineBorder(Color.BLACK, 0));

		this.noteReadPanel.add(noteReaderScrollPane);

		// set the background painter
		MattePainter mp = new MattePainter(Colors.Yellow.alpha(0.5f));
		GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),
			 GlossPainter.GlossPosition.TOP);
		this.noteReadPanel.setBackgroundPainter(new CompoundPainter(mp, gp));
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (this.notesTable.getSelectedRow() == -1) {
			this.noteReader.setText("");
			//TODO: en/disable context menu entries for delete, commit, etc...
		} else {
			String text;
			text = this.notesTableModel.getNoteAtRow(this.notesTable.getSelectedRow()).getContent();
			this.noteReader.setText(text);
		}
	}


	public ResourceMap getResourceMap() {
		return this.resourceMap;
	}


	public Project getCurrentProject() {
		return this.currentProject;
	}

	/**
	 * Get the current Project.
	 *
	 * @param currentProject
	 */
	private void setCurrentProject(Project currentProject) {
		this.currentProject = currentProject;
	}


	@Override
	public void projectChanged(ProjectChangedEvent ignored) {
		log.info("received projectChangedEvent: " + ignored.toString());
		this.notesTableModel.update();
	}

	@Override
	public void setProject(Project pr) {
		this.setCurrentProject(pr);
		this.notesTableModel.update(this.getCurrentProject());
	}

	private boolean isNoteSelected() {
		return this.notesTable.getSelectedRow() >= 0;
	}

	/**
	 * Create a new note both in the persistence and in the notes table.
	 */
	private void newNote() {
		this.core.newNote(new NoteObject(UUID.randomUUID(), this.getCurrentProject(), "new note"));
	}

	/**
	 * Commit the currently selected note.
	 */
	private void shareSelectedNote() {
		//TODO
	}

	/**
	 * Save the currently selected note. If it is a local note, it is only saved, if it is a shared note
	 * it is automatically commited.
	 */
	private void saveSelectedNote() {
		//TODO
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jSplitPane1 = new javax.swing.JSplitPane();
		jScrollPane2 = new javax.swing.JScrollPane();
		notesTable = new ITunesTable();
		noteReadPanel = new org.jdesktop.swingx.JXPanel();

		setName("Form"); // NOI18N
		setLayout(new java.awt.BorderLayout());

		jSplitPane1.setBorder(null);
		jSplitPane1.setDividerSize(2);
		jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		jSplitPane1.setName("jSplitPane1"); // NOI18N

		jScrollPane2.setName("jScrollPane2"); // NOI18N

		notesTable.setModel(new javax.swing.table.DefaultTableModel(
			 new Object[][]{
				  {"Gui redesign nicht vergessen...", "8.12.2008 18:00", "Peter"},
				  {"Blabla", "9.12.2008", "Simon"},
				  {"xxx", "10.12", "a"},
				  {"bbb", "11.12", "b"}
			 },
			 new String[]{
				  "Note", "Date", "User"
			 }
		) {
			boolean[] canEdit = new boolean[]{
				 false, false, false
			};

			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return canEdit[columnIndex];
			}
		});
		notesTable.setName("notesTable"); // NOI18N
		jScrollPane2.setViewportView(notesTable);

		jSplitPane1.setLeftComponent(jScrollPane2);

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NotesPanel.class);
		noteReadPanel.setBackground(resourceMap.getColor("noteReadPanel.background")); // NOI18N
		noteReadPanel.setName("noteReadPanel"); // NOI18N
		noteReadPanel.setLayout(new java.awt.BorderLayout());
		jSplitPane1.setRightComponent(noteReadPanel);

		add(jSplitPane1, java.awt.BorderLayout.CENTER);
	}// </editor-fold>//GEN-END:initComponents


	// Variables declaration - do not modify//GEN-BEGIN:variables
	private javax.swing.JScrollPane jScrollPane2;
	private javax.swing.JSplitPane jSplitPane1;
	private org.jdesktop.swingx.JXPanel noteReadPanel;
	private org.jdesktop.swingx.JXTable notesTable;
	// End of variables declaration//GEN-END:variables

	public static NotesPanel getInstance() {
		return instance;
	}

	public void addNoteSelectionListener(NoteSelectionChanged listener) {
		this.noteSelectionListeners.add(listener);
	}

	public void removeNoteSelectionListener(NoteSelectionChanged listener) {
		this.noteSelectionListeners.remove(listener);
	}

	public void notifyNoteSelectionListeners(java.util.List<NoteObject> selectedNotes) {
		log.debug("notify selection listeners: " + selectedNotes.toArray());
		for (NoteSelectionChanged listener : this.noteSelectionListeners) {
			listener.noteSelectionChanged(new NoteSelectionChanged.NoteSelectedEvent(selectedNotes));
		}
	}

	/**
	 * Get a <code>List</code> of selected notes.
	 *
	 * @return the list of currently selected notes. If nothing is selected, an empty list is returned.
	 */
	public List<NoteObject> getSelectedNotes() {
		log.debug("get selected notes...");
		List<NoteObject> selectedNotes = new ArrayList<NoteObject>();

		if (this.notesTable.getSelectedRow() == -1) {
			return selectedNotes;
		}

		 log.debug("selcted notes count: " + notesTable.getSelectedRowCount());
		for (int row : this.notesTable.getSelectedRows()) {
			selectedNotes.add(this.notesTableModel.getNoteAtRow(row));
		}
		return selectedNotes;
	}

	public void resetFilter() {
		log.debug("resetting filter...");
		this.notesTable.setFilters(null);
	}

	public void setFilter(FilterPipeline filterPipeline) {
		this.notesTable.setFilters(filterPipeline);
	}
}
