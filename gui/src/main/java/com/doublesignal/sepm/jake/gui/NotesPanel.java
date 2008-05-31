package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.NotesTableModel.NotesUpdaterObservable;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class NotesPanel extends JPanel {
	private static Logger log = Logger.getLogger(NotesPanel.class);
	private final JakeGui gui;
	private final IJakeGuiAccess jakeGuiAccess;

	private NotesTableModel notesTableModel;

	public NotesPanel(JakeGui gui) {
		log.info("Initializing NotesPanel.");
		this.gui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();

		initComponents();
		initPopupMenu();
		updateData();
	}

	private void newNoteMenuItemActionPerformed(ActionEvent e) {
		new NoteEditorDialog(gui.getMainFrame()).setVisible(true);
	}

	private void editNoteMenuItemActionPerformed(ActionEvent e) {
		editNote(getSelectedNote());
	}

	private void removeNoteMenuItemActionPerformed(ActionEvent e) {
		jakeGuiAccess.removeNote(getSelectedNote());
	}

	private void editNote(NoteObject note) {
		log.info("Edit Note " + note);
		new NoteEditorDialog(gui.getMainFrame(), note).setVisible(true);
	}

	public NotesUpdaterObservable getNotesUpdater() {
		return notesTableModel.getNotesUpdater();
	}

	public void updateData() {
		log.info("Updating Notes Panel...");
		notesTableModel.updateData();
	}

	public String getTitle() {
		return "Notes (" + notesTableModel.getNotes() + ")";
	}

	private boolean isNoteSelected() {
		return notesTable.getSelectedRow() >= 0;
	}

	private NoteObject getSelectedNote() {
		int selRow = notesTable.getSelectedRow();
		if (selRow >= 0) {
			log.info("getSelectedNode: (" + selRow + ") "
					+ notesTableModel.getNotes().get(selRow));
			return (notesTableModel.getNotes().get(selRow));
		} else {
			log.info("getSelctedNode: null");
			return null;
		}
	}

	public void initComponents() {
		notesTable = new JXTable();
		notesScrollPane = new JScrollPane();
		notesPopupMenu = new JPopupMenu();
		viewEditNoteMenuItem = new JMenuItem();
		newNoteMenuItem = new JMenuItem();
		removeNoteMenuItem = new JMenuItem();

		this.setLayout(new BorderLayout());
		notesTableModel = new NotesTableModel(jakeGuiAccess);
		notesTable.setComponentPopupMenu(notesPopupMenu);
		notesTable.setColumnControlVisible(true);
		notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		notesTable.setModel(notesTableModel);
		notesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2
						&& SwingUtilities.isLeftMouseButton(e)
						&& isNoteSelected()) {
					editNote(getSelectedNote());
				}
			}
		});

		TableColumnModel cm = notesTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(265);

		notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		notesScrollPane.setViewportView(notesTable);

		this.add(notesScrollPane, BorderLayout.NORTH);
	}

	private void initPopupMenu() {
		// ---- viewEditNoteMenuItem ----
		viewEditNoteMenuItem.setText("View/Edit Note");
		viewEditNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editNoteMenuItemActionPerformed(e);
			}
		});
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
		removeNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(removeNoteMenuItem);
	}

	private JScrollPane notesScrollPane;
	private JXTable notesTable;
	private JPopupMenu notesPopupMenu;
	private JMenuItem viewEditNoteMenuItem;
	private JMenuItem newNoteMenuItem;
	private JMenuItem removeNoteMenuItem;
}
