package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class NotesPanel extends JPanel {
	private static Logger log = Logger.getLogger(JakeGui.class);
	private final JakeGui gui;
	private final IJakeGuiAccess jakeGuiAccess;

	private NotesTableModel notesTableModel;
	private List<NoteObject> notes;

	public NotesPanel(JakeGui gui) {
		log.info("Initializing NotesPanel.");
		this.gui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();

		updateData();

		initComponents();
	}

	/**
	 * Updates the view for notes, get new notes from GuiAccess
	 */
	private void updateData() {
		notes = jakeGuiAccess.getNotes();
	}

	private void newNoteMenuItemActionPerformed(ActionEvent e) {
		new NoteEditorDialog(gui.getMainFrame()).setVisible(true);
	}

	public void initComponents() {
		notesTable = new JXTable();
		notesScrollPane = new JScrollPane();
		notesPopupMenu = new JPopupMenu();
		viewEditNoteMenuItem = new JMenuItem();
		newNoteMenuItem = new JMenuItem();
		removeNoteMenuItem = new JMenuItem();

		this.setLayout(new BorderLayout());
		notesTableModel = new NotesTableModel(notes);
		// ---- notesTable ----
		notesTable.setComponentPopupMenu(notesPopupMenu);
		notesTable.setColumnControlVisible(true);
		notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		notesTable.setModel(notesTableModel);

		TableColumnModel cm = notesTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(265);

		notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		notesScrollPane.setViewportView(notesTable);

		this.add(notesScrollPane, BorderLayout.NORTH);

		// ======== notesPopupMenu ========

		// ---- viewEditNoteMenuItem ----
		viewEditNoteMenuItem.setText("View/Edit Note");
		notesPopupMenu.add(viewEditNoteMenuItem);

		// ---- newNoteMenuItem ----
		newNoteMenuItem.setText("New Note...");
		newNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(newNoteMenuItem);

		// ---- removeNoteMenuItem ----
		removeNoteMenuItem.setText("Remove");
		notesPopupMenu.add(removeNoteMenuItem);

	}

	private JScrollPane notesScrollPane;
	private JXTable notesTable;
	private JPopupMenu notesPopupMenu;
	private JMenuItem viewEditNoteMenuItem;
	private JMenuItem newNoteMenuItem;
	private JMenuItem removeNoteMenuItem;
}
