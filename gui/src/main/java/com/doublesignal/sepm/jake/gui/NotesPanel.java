package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumnModel;

import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

@SuppressWarnings("serial")
public class NotesPanel extends JPanel {
	JakeGui gui;

	public NotesPanel(JakeGui gui) {
		this.gui = gui;

		initComponents();
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

		// ======== notesPanel ========

		this.setLayout(new BorderLayout());

		// ======== notesScrollPane ========

		// ---- notesTable ----
		notesTable.setComponentPopupMenu(notesPopupMenu);
		notesTable.setColumnControlVisible(true);
		notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		notesTable.setModel(new DefaultTableModel(new Object[][] {
				{ "Update 1", "", "Today", "Peter" },
				{ "Aufgaben und Ziele", "!", "Yesterday, 11:00", "Simon" },
				{ "Testnotiz 1", "test", "April 12th", "Johannes" }, },
				new String[] { "Title", "Tags", "Last changed", "User" }) {
			boolean[] columnEditable = new boolean[] { true, true, true, false };

			@Override
			public boolean isCellEditable(int rowIndex, int columnIndex) {
				return columnEditable[columnIndex];
			}
		});
		{
			TableColumnModel cm = notesTable.getColumnModel();
			cm.getColumn(0).setPreferredWidth(265);
		}
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
