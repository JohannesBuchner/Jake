package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.NotesTableModel.NotesUpdaterObservable;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class NotesPanel extends JPanel {
	private static final Logger log = Logger.getLogger(NotesPanel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
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
	
    public void setFilters(FilterPipeline filterPipeline) {
        notesTable.setFilters(filterPipeline);
    }

	private void newNoteMenuItemActionPerformed(ActionEvent e) {
		log.info("create new Note.");
		NoteEditorDialog noteEditor = new NoteEditorDialog(gui.getMainFrame());
		noteEditor.setVisible(true);

		if (noteEditor.isSaved()) {
			jakeGuiAccess.createNote(noteEditor.getContent());
		}

		updateData();
	}

	private void editNoteMenuItemActionPerformed(ActionEvent e) {
		editNote(getSelectedNote());
	}

	private void removeNoteMenuItemActionPerformed(ActionEvent e) {
		JOptionPane.showConfirmDialog(this, "This operation cannot be undone.",
				"Really delete this node?", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

		jakeGuiAccess.removeNote(getSelectedNote());
		updateData();
	}

	private void editNote(NoteObject note) {
		log.info("Edit Note " + note);
		NoteEditorDialog noteEditor = new NoteEditorDialog(gui.getMainFrame(), note);
		noteEditor.setVisible(true);

		if (noteEditor.isSaved()) {
			jakeGuiAccess.editNote(noteEditor.getNote());
		}

		updateData();
	}

	public NotesUpdaterObservable getNotesUpdater() {
		return notesTableModel.getNotesUpdater();
	}

	public void updateData() {
		log.info("Updating Notes Panel...");
		notesTableModel.updateData();
	}

	public String getTitle() {
		return "Notes (" + notesTableModel.getNotes().size() + ")";
	}

	private boolean isNoteSelected() {
		return notesTable.getSelectedRow() >= 0;
	}

	private NoteObject getSelectedNote() {
		int selRow = notesTable.getSelectedRow();
		if (selRow >= 0) {
			log.info("getSelectedNode: (" + selRow + ") " + notesTableModel.getNotes().get(selRow));
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

		this.setLayout(new BorderLayout());
		notesTableModel = new NotesTableModel(jakeGuiAccess);
		notesTable.setComponentPopupMenu(notesPopupMenu);
		notesTable.setColumnControlVisible(true);
		notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		notesTable.setModel(notesTableModel);
		notesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)
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
		viewEditNoteMenuItem = new JMenuItem();
		newNoteMenuItem = new JMenuItem();
		removeNoteMenuItem = new JMenuItem();

		// ---- newNoteMenuItem ----
		newNoteMenuItem.setText("New Note...");
		newNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newNoteMenuItemActionPerformed(e);
			}
		});

		// ---- viewEditNoteMenuItem ----
		viewEditNoteMenuItem.setText("Open Note...");
		viewEditNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(viewEditNoteMenuItem);

		notesPopupMenu.add(newNoteMenuItem);

		// ---- removeNoteMenuItem ----
		removeNoteMenuItem.setText("Remove Note...");
		removeNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(removeNoteMenuItem);

		// check the data before drawing the popup menu
		notesPopupMenu.addPopupMenuListener(new PopupMenuListener() {
			public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
				boolean isNoteSelected = getSelectedNote() != null;
				viewEditNoteMenuItem.setEnabled(isNoteSelected);
				removeNoteMenuItem.setEnabled(isNoteSelected);
			}

			public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
			}

			public void popupMenuCanceled(PopupMenuEvent e) {
			}
		});
	}

	private JScrollPane notesScrollPane;
	private JXTable notesTable;
	private JPopupMenu notesPopupMenu;
	private JMenuItem viewEditNoteMenuItem;
	private JMenuItem newNoteMenuItem;
	private JMenuItem removeNoteMenuItem;

	public int getNameColPos() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getTagsColPos() {
		// TODO Auto-generated method stub
		return 1;
	}	
}
