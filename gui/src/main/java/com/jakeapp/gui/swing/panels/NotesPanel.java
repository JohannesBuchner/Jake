package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.NoteObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.globals.JakeContext;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.notes.CommitNoteAction;
import com.jakeapp.gui.swing.actions.notes.SoftlockNoteAction;
import com.jakeapp.gui.swing.actions.notes.CreateNoteAction;
import com.jakeapp.gui.swing.actions.notes.DeleteNoteAction;
import com.jakeapp.gui.swing.actions.notes.SaveNoteAction;
import com.jakeapp.gui.swing.callbacks.ContextChangedCallback;
import com.jakeapp.gui.swing.callbacks.NoteSelectionChangedCallback;
import com.jakeapp.gui.swing.callbacks.ProjectChangedCallback;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.helpers.Colors;
import com.jakeapp.gui.swing.helpers.JakeHelper;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.models.NotesTableModel;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;
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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * The NotesPanel
 *
 * @author studpete, simon
 */
public class NotesPanel extends javax.swing.JPanel
				implements ContextChangedCallback, ProjectChangedCallback, ListSelectionListener {

	private static final long serialVersionUID = -7703570005631651276L;
	private static NotesPanel instance;
	private static Logger log = Logger.getLogger(NotesPanel.class);
	private final static int TableUpdateDelay = 20000;
					// 20 sec 	//FIXME magic number, make property
	private Timer tableUpdateTimer;
	private List<NoteSelectionChangedCallback> noteSelectionListeners =
					new ArrayList<NoteSelectionChangedCallback>();
	private NotesTableModel notesTableModel;
	private JScrollPane notesTableScrollPane;
	private JSplitPane mainSplitPane;
	private JXPanel noteReaderPanel;
	private JXTable notesTable;
	private ResourceMap resourceMap;
	private JTextArea noteReader;
	private JButton createBtn;
	private JButton announceBtn;
	private JButton softLockBtn;
	private JButton deleteBtn;
	private JButton saveBtn;


	private class NoteContainerMouseListener extends MouseAdapter {
		private NotesPanel panel;
		private JTable table;
		private JPopupMenu popupMenu;

		{
			this.popupMenu = new JakePopupMenu();
			this.popupMenu.add(new JMenuItem(new CreateNoteAction()));
			this.popupMenu.addSeparator();
			this.popupMenu.add(new JMenuItem(new DeleteNoteAction()));
			this.popupMenu.add(new JMenuItem(new CommitNoteAction()));
			this.popupMenu.addSeparator();
			this.popupMenu.add(new JMenuItem(new SoftlockNoteAction()));
		}

		public NoteContainerMouseListener(NotesPanel panel, JTable table) {
			super();
			this.panel = panel;
			this.table = table;
		}

		@Override
		public void mouseClicked(MouseEvent mouseEvent) {
			if (SwingUtilities.isRightMouseButton(mouseEvent)) {

				Point p = mouseEvent.getPoint();
				int rowNumber = this.table.rowAtPoint(p);

				if (rowNumber == -1) { // click in empty area
					this.panel.notifyNoteSelectionListeners();
					this.table.clearSelection();
				} else { //click hit something
					boolean found = false;
					for (int row : this.table.getSelectedRows()) {
						if (row == rowNumber) {
							found = true;
							break;
						}
					}
					if (!found) {
						this.table.changeSelection(rowNumber, 0, false, false);
					}
				}
				showMenu(mouseEvent);
			} else if (SwingUtilities.isLeftMouseButton(mouseEvent)) {
				if (this.table.rowAtPoint(mouseEvent.getPoint()) == -1) {
					this.table.clearSelection();
				}
			}
		}

		private void showMenu(MouseEvent me) {
			this.popupMenu.show(this.table,
							(int) me.getPoint().getX(),
							(int) me.getPoint().getY());
		}
	}

	private void notifyNoteSelectionListeners() {
		notifyNoteSelectionListeners(new ArrayList<Attributed<NoteObject>>());
	}

	/**
	 * Creates new form NotesPanel
	 */
	public NotesPanel() {
		instance = this;

		// get resource map
		this.setResourceMap(org.jdesktop.application.Application
						.getInstance(JakeMainApp.class)
						.getContext().getResourceMap(NotesPanel.class));

		// init components
		initComponents();

		// register the callbacks
		EventCore.get().addContextChangedListener(this);
		EventCore.get().addProjectChangedCallbackListener(this);
		this.notesTable.getSelectionModel().addListSelectionListener(this);

		// TODO: make this a styler property
		if (!Platform.isMac()) {
			this.notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}
		//final JPopupMenu notesPopupMenu = new JakePopupMenu();

		this.notesTable
						.addMouseListener(new NoteContainerMouseListener(this, this.notesTable));

		// install event table update timer
		this.tableUpdateTimer = new Timer(TableUpdateDelay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				//log.debug("Updating notesTable");
				notesTable.updateUI();
			}
		});
		this.tableUpdateTimer.start();
	}


	@Override
	public void valueChanged(ListSelectionEvent e) {
		if (this.notesTable.getSelectedRow() == -1) {
			this.noteReader.setText("");
			this.noteReader.setEditable(false);
			this.notifyNoteSelectionListeners(new ArrayList<Attributed<NoteObject>>());
		} else {
			String text;

			//store old selected rows
			//FIXME not needed, since there is a (user-unfriendly) SAVE-Button.
			//to be fixed in a later version
			/*
			for (int i = e.getFirstIndex();i<=e.getLastIndex();i++) {
				if (i!=this.getNotesTable().getSelectedRow()) {
					//this row was just de-selected
					this.notesTableModel.getNoteAtRow(i).getJakeObject().setContent(this.getNoteReaderText());
					
				}
			}*/
			//handle new selection
			text = this.notesTableModel
							.getNoteAtRow(this.notesTable.convertRowIndexToModel(this.notesTable.getSelectedRow()))
							.getJakeObject().getContent();
			this.noteReader.setText(text);

			this.noteReader.setEditable(JakeHelper.isEditable(getSelectedNote()));
		}

		List<Attributed<NoteObject>> selectedNotes =
						new ArrayList<Attributed<NoteObject>>();
		for (int row : this.notesTable.getSelectedRows()) {
			selectedNotes
							.add(this.notesTableModel.getNoteAtRow(this.notesTable.convertRowIndexToModel(
											row)));
		}
		this.notifyNoteSelectionListeners(selectedNotes);
	}

	/**
	 * Get the first of the selected notes. if no notes are selected, <code>null</code> is returned
	 * instead.
	 *
	 * @return the first note of the selectedNotes, if notes are selected, <code>null</code>instead.
	 */
	private Attributed<NoteObject> getSelectedNote() {
		if (getSelectedNotes().size() > 0) {
			return getSelectedNotes().get(0);
		}
		return null;
	}

	public ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public Project getProject() {
		return JakeContext.getProject();
	}


	/**
	 * When we receive a ProjectChanged-Event, go and get new notes.
	 *
	 * @param e
	 */
	@Override
	public void projectChanged(ProjectChangedEvent e) {
		// don't update if project is null
		if (e.getProject() == null) {
			return;
		}

		ObjectCache.get().updateNotes(e.getProject());
	}


	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		this.notesTableModel.update(this.getProject());		
	}


	private void initComponents() {
		this.setLayout(new MigLayout("wrap 1, fill, ins 0"));

		this.mainSplitPane = new JSplitPane();
		this.notesTableScrollPane = new JScrollPane();
		this.notesTable = new ITunesTable();

		this.mainSplitPane.setBorder(null);
		this.mainSplitPane.setDividerSize(2);
		this.mainSplitPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);
		this.notesTableScrollPane.setViewportView(this.notesTable);
		this.mainSplitPane.setLeftComponent(this.notesTableScrollPane);

		this.add(this.mainSplitPane, "grow");

		// set up table model
		this.notesTableModel = new NotesTableModel();
		this.notesTable.setModel(this.notesTableModel);
		this.notesTable.setSortable(true);
		this.notesTable.setColumnControlVisible(false);

		this.notesTable.getColumnModel().getColumn(0).setResizable(false); // lock
		this.notesTable.getColumnModel().getColumn(0).setMaxWidth(20);
		this.notesTable.getColumnModel().getColumn(1).setResizable(false); // shared note
		this.notesTable.getColumnModel().getColumn(1).setMaxWidth(20);
		this.notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_LAST_COLUMN);

		this.notesTable.getSelectionModel().addListSelectionListener(this);

		this.notesTable.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				if (e.getKeyCode() == KeyEvent.VK_DELETE) {
					new DeleteNoteAction().execute();
				}
			}
		});

		this.noteReaderPanel = new JXPanel(new MigLayout("wrap 1, ins 0, fill"));
		this.noteReaderPanel
						.setBackground(getResourceMap().getColor("noteReadPanel.background"));

		JPanel noteControlPanel = new JPanel(new MigLayout("nogrid, ins 0"));
		noteControlPanel.setBackground(Color.WHITE);

		this.createBtn = new JButton(new CreateNoteAction());
		this.createBtn.putClientProperty("JButton.buttonType", "textured");
		noteControlPanel.add(this.createBtn);

		this.announceBtn = new JButton(new CommitNoteAction());
		this.announceBtn.putClientProperty("JButton.buttonType", "textured");
		noteControlPanel.add(this.announceBtn);

		this.softLockBtn = new JButton(new SoftlockNoteAction());
		this.softLockBtn.putClientProperty("JButton.buttonType", "textured");
		noteControlPanel.add(this.softLockBtn);

		this.deleteBtn = new JButton(new DeleteNoteAction());
		this.deleteBtn.putClientProperty("JButton.buttonType", "textured");
		noteControlPanel.add(this.deleteBtn);

		this.saveBtn = new JButton(new SaveNoteAction());
		this.saveBtn.putClientProperty("JButton.buttonType", "textured");
		noteControlPanel.add(this.saveBtn);

		this.noteReaderPanel.add(noteControlPanel, "growx");

		this.noteReader = new JTextArea();
		this.noteReader.setLineWrap(true);
		this.noteReader.setOpaque(false);
		this.noteReader.setText("");
		this.noteReader.setEditable(false);
		this.noteReader.setMargin(new Insets(8, 8, 8, 8));

		JScrollPane noteReaderScrollPane = new JScrollPane(this.noteReader);
		noteReaderScrollPane
						.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		noteReaderScrollPane
						.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		noteReaderScrollPane.setOpaque(false);
		noteReaderScrollPane.getViewport().setOpaque(false);
		noteReaderScrollPane.setBorder(new LineBorder(Color.BLACK, 0));

		this.noteReaderPanel.add(noteReaderScrollPane, "grow, push");

		// set the background painter
		MattePainter mp = new MattePainter(Colors.Yellow.alpha(0.5f));
		GlossPainter gp = new GlossPainter(Colors.White.alpha(0.3f),
						GlossPainter.GlossPosition.TOP);
		this.noteReaderPanel.setBackgroundPainter(new CompoundPainter(mp, gp));

		this.mainSplitPane.setRightComponent(this.noteReaderPanel);

	}

	public static NotesPanel getInstance() {
		return instance;
	}

	public void addNoteSelectionListener(NoteSelectionChangedCallback listener) {
		this.noteSelectionListeners.add(listener);
	}

	public void removeNoteSelectionListener(NoteSelectionChangedCallback listener) {
		this.noteSelectionListeners.remove(listener);
	}

	public void notifyNoteSelectionListeners(
					List<Attributed<NoteObject>> selectedNotes) {

		log.debug("notify note selection listeners");

		for (NoteSelectionChangedCallback listener : this.noteSelectionListeners) {
			listener.noteSelectionChanged(new NoteSelectionChangedCallback.NoteSelectedEvent(
							selectedNotes));
		}
	}

	/**
	 * Get a <code>List</code> of selected notes.
	 *
	 * @return the list of currently selected notes. If nothing is selected, an empty list is returned.
	 */
	public List<Attributed<NoteObject>> getSelectedNotes() {
		//log.debug("get selected notes...");
		List<Attributed<NoteObject>> selectedNotes =
						new ArrayList<Attributed<NoteObject>>();

		if (this.notesTable.getSelectedRow() == -1) {
			return selectedNotes;
		}

		//log.debug("selected notes count: " + notesTable.getSelectedRowCount());
		for (int row : this.notesTable.getSelectedRows()) {
			selectedNotes
							.add(this.notesTableModel.getNoteAtRow(this.notesTable.convertRowIndexToModel(
											row)));
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

	/**
	 * Get The current text of the noteReader.
	 *
	 * @return the text of the noteReader.
	 */
	public String getNoteReaderText() {
		return this.noteReader.getText();
	}

	public NotesTableModel getNotesTableModel() {
		return this.notesTableModel;
	}

	public JTable getNotesTable() {
		return this.notesTable;
	}
}
