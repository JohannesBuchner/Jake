package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.rmi.NoSuchObjectException;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.NotesTableModel.NotesUpdaterObservable;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

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
		createNewNote();
	}

	public void createNewNote() {
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
		if (JOptionPane.showConfirmDialog(this, translator.get("NotesPanelDialogCannotBeUndone"),
				translator.get("NotesPanelDialogReallyDeleteNode"), JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE) == 0) {
			jakeGuiAccess.removeNote(getSelectedNote());
			updateData();
		}
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
		return translator.get("NotesPanelDialogTitle", String.valueOf(notesTableModel.getNotes()
				.size()));
	}

	private boolean isNoteSelected() {
		return notesTable.getSelectedRow() >= 0;
	}

	private NoteObject getSelectedNote() {
		int selRow = notesTable.getSelectedRow();
		if (selRow >= 0 && selRow < notesTableModel.getNotes().size()) {
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
		notesTable.setColumnControlVisible(true);
		notesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		notesTable.setModel(notesTableModel);
		notesTable.setRolloverEnabled(false);
		notesTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2 && SwingUtilities.isLeftMouseButton(e)
						&& isNoteSelected()) {
					editNote(getSelectedNote());
				}
			}
		});
		
		notesTable.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				// Right mouse click
				if (SwingUtilities.isRightMouseButton(e)) {
					// get the coordinates of the mouse click
					Point p = e.getPoint();

					// get the row index that contains that coordinate
					int rowNumber = notesTable.rowAtPoint(p);

					// Get the ListSelectionModel of the JTable
					ListSelectionModel model = notesTable.getSelectionModel();

					// set the selected interval of rows. Using the "rowNumber"
					// variable for the beginning and end selects only that one
					// row.
					model.setSelectionInterval(rowNumber, rowNumber);

					// Show the table popup
					notesPopupMenu.show(notesTable, (int) e.getPoint().getX(), (int) e.getPoint()
							.getY());
				}
			}
		});

		TableColumnModel cm = notesTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(265);

		notesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		notesScrollPane.setViewportView(notesTable);

		this.add(notesScrollPane, BorderLayout.CENTER);
	}

	private void initPopupMenu() {
		viewEditNoteMenuItem = new JMenuItem();
		newNoteMenuItem = new JMenuItem();
		removeNoteMenuItem = new JMenuItem();

		// ---- newNoteMenuItem ----
		newNoteMenuItem.setText(translator.get("NotesPanelDialogMenuNewNote"));
		newNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newNoteMenuItemActionPerformed(e);
			}
		});

		// ---- viewEditNoteMenuItem ----
		viewEditNoteMenuItem.setText(translator.get("NotesPanelDialogMenuOpenNote"));
		viewEditNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(viewEditNoteMenuItem);

		notesPopupMenu.add(newNoteMenuItem);

		// ---- removeNoteMenuItem ----
		removeNoteMenuItem.setText(translator.get("NotesPanelDialogMenuRemoveNote"));
		removeNoteMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removeNoteMenuItemActionPerformed(e);
			}
		});
		notesPopupMenu.add(removeNoteMenuItem);
		
		

        viewLogForNoteMenuItem = new JMenuItem();
        pushNoteMenuItem = new JMenuItem();
        pullNoteMenuItem = new JMenuItem();
        lockNoteMenuItem = new JMenuItem();
        
        lockNoteMenuItem.setText(translator.get("NotesDialogContextMenuItemSetLock"));
        lockNoteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lockNoteMenuItemActionPerformed(event);
            }
        });

        viewLogForNoteMenuItem.setText(translator.get("FilesDialogContextMenuItemViewLog"));
        viewLogForNoteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                viewLogForNoteMenuItemActionPerformed(event);
            }
        });
        
        pushNoteMenuItem.setText(translator.get("FilesDialogContextMenuItemPushFile"));
        pushNoteMenuItem.setToolTipText("Push locally changed file");
        pushNoteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pushNoteMenuItemActionPerformed(event);
            }
        }
        );
        pullNoteMenuItem.setText(translator.get("FilesDialogContextMenuItemPullFile"));
        pullNoteMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pullNoteMenuItemActionPerformed(event);
            }
        });
        
        notesPopupMenu.addSeparator();
        notesPopupMenu.add(pushNoteMenuItem);
        notesPopupMenu.add(pullNoteMenuItem);
        notesPopupMenu.addSeparator();
        notesPopupMenu.add(lockNoteMenuItem);
        notesPopupMenu.add(viewLogForNoteMenuItem);
		

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

	private void pullNoteMenuItemActionPerformed(ActionEvent event) {
		log.info("pullNoteMenuItemActionPerformed");
		JakeObject noteObject = getSelectedNote();
		if (noteObject != null) {
			try {
				jakeGuiAccess.pullJakeObject(noteObject);
			} catch (NotLoggedInException e) {
				UserDialogHelper.translatedError(this, "NotLoggedInException");
			} catch (OtherUserOfflineException e) {
				UserDialogHelper.translatedError(this, "OtherUserOfflineException");
			} catch (NoSuchObjectException e) {
				UserDialogHelper.translatedError(this, "ObjectNotInProject");
			} catch (NoSuchLogEntryException e) {
				UserDialogHelper.translatedError(this, "ObjectNotInProject");
			}
		}
		updateUI();	
	}

	private void pushNoteMenuItemActionPerformed(ActionEvent event) {
		log.info("pushNoteMenuItemActionPerformed");

		JakeObject noteObject = getSelectedNote();
		if (noteObject != null) {
			String commitmsg = UserDialogHelper.showTextInputDialog(this, 
					translator.get("CommitMessage"), ""); 
			
			if(commitmsg != null)
				try {
					jakeGuiAccess.pushJakeObject(noteObject, commitmsg);
				} catch (NotLoggedInException e) {
					UserDialogHelper.translatedError(this, "NotLoggedInException");
				} catch (SyncException e) {
//					e.getInnerException().printStackTrace();
					UserDialogHelper.translatedError(this, "SyncException");
				}
		}
		updateUI();
	}

	private void viewLogForNoteMenuItemActionPerformed(ActionEvent event) {
		log.info("viewLogForNoteMenuItemActionPerformed");
        JakeObject noteObject = getSelectedNote();
        if (noteObject != null) {
        	log.info("view log for a single object");
        	log.info(noteObject.getName());
            new ViewLogDialog(gui.getMainFrame() , this.jakeGuiAccess , noteObject).setVisible(true);
        }
	}

	private void lockNoteMenuItemActionPerformed(ActionEvent event) {
        log.info("lockNoteMenuItemActionPerformed");
        JakeObject noteObject = getSelectedNote();
        if (noteObject == null || noteObject.getClass() != NoteObject.class)
            return;
        new SetSoftLockDialog(gui.getMainFrame(), jakeGuiAccess, (NoteObject)noteObject).setVisible(true);
	}

	private JScrollPane notesScrollPane;
	private JXTable notesTable;
	private JPopupMenu notesPopupMenu;
	private JMenuItem viewEditNoteMenuItem;
	private JMenuItem newNoteMenuItem;
	private JMenuItem removeNoteMenuItem;
	private JMenuItem viewLogForNoteMenuItem;
	private JMenuItem pushNoteMenuItem;
	private JMenuItem pullNoteMenuItem;
	private JMenuItem lockNoteMenuItem;

	public int getNameColPos() {
		return 0;
	}

	public int getTagsColPos() {
		return 1;
	}
}
