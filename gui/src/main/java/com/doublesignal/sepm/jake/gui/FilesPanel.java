package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.rmi.NoSuchObjectException;
import java.util.Date;

import javax.swing.*;
import javax.swing.event.PopupMenuListener;
import javax.swing.event.PopupMenuEvent;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.IStateChangeListener;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.fss.exceptions.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TextTranslationProvider;
import com.doublesignal.sepm.jake.ics.exceptions.NotLoggedInException;
import com.doublesignal.sepm.jake.ics.exceptions.OtherUserOfflineException;
import com.doublesignal.sepm.jake.sync.exceptions.SyncException;

/**
 * SEPM SS08 Gruppe: 3950 Projekt: Jake - a collaborative Environment User:
 *
 * @author domdorn, peter
 */
@SuppressWarnings("serial")
public class FilesPanel extends JPanel  implements IStateChangeListener{
	private static final Logger log = Logger.getLogger(FilesPanel.class);

	private final JakeGui jakeGui;
    private FilesTableModel filesTableModel;
    private static ITranslationProvider translator = new TextTranslationProvider();
    private Date lastUpdate;

    {
        lastUpdate = new Date();
    }

    int tabindex = 0;

    private JakeObject getSelectedFile() {
        int selectedRow = filesTable.getSelectedRow();
        if(selectedRow < 0)
            return null;
        return filesTableModel.getFiles().get(filesTable.getSelectedRow());
    }


    public FilesPanel(JakeGui jakeGui) {
        this.jakeGui = jakeGui;
        this.jakeGuiAccess = jakeGui.getJakeGuiAccess();
        initPopupMenu();
        initComponents();

        jakeGui.getMainTabbedPane()
                .addTab("filestab", new ImageIcon(
                        getClass().getResource("/icons/files.png")),
                        this);
        
        jakeGuiAccess.addJakeObjectStateChangeListener(this);
        
        tabindex = jakeGui.getMainTabbedPane().indexOfTab("filestab");
        if (tabindex >= 0)
            jakeGui.getMainTabbedPane().setTitleAt(tabindex,
            		translator.get("FilesTabTitle", Integer.toString(filesTableModel.getFilesCount()),
                            FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize()))
            );
    }

	public void updateUI() {
		log.debug("files panel _real_ update");
		super.updateUI();
		if (filesTableModel != null) {
			filesTableModel.updateData();
			if (tabindex >= 0)
				jakeGui.getMainTabbedPane().setTitleAt(tabindex, "Files ("
					+ filesTableModel.getFilesCount() + "/" + 
					FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize())
					+ ")");
		}
		super.updateUI();
		this.repaint();
		lastUpdate = new Date();
	}

	public void stateChanged(JakeObject jo) {
		if(jo == null)
			log.debug("everything changed");
		else 
			log.debug(jo.getName() + "changed");
		updateUI();
	}
	
    public FilterPipeline getFilters() {
        return filesTable.getFilters();
    }

    public void setFilters(FilterPipeline filterPipeline) {
        filesTable.setFilters(filterPipeline);
    }

    private void initComponents() {
        filesScrollPane = new JScrollPane();
        filesTable = new JXTable()
        {
            private Date lastUpdated;

            {
                lastUpdated = new Date();
            }

            public void updateUI() {
                Date now = new Date();
                if(lastUpdated == null || this.lastUpdated.getTime()+3 < now.getTime())
                {
                    super.updateUI();
                    lastUpdated = now;
                }
            }
        };

        this.setLayout(new BorderLayout());
        filesTableModel = new FilesTableModel(jakeGuiAccess);
        filesTable.setColumnControlVisible(true);
        filesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        filesTable.setModel(filesTableModel);
        filesTable.setRolloverEnabled(false);

        filesTable.addMouseListener(new MouseAdapter() {
    		public void mouseClicked( MouseEvent e ) {
    			log.debug("click reveived");
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)
                        && isFileSelected()) {
                    openExecuteFileMenuItemActionEvent(null);
                }
    			// Right mouse click
    			if ( SwingUtilities.isRightMouseButton( e ) ) {
        			log.debug("a right click.");
    				// get the coordinates of the mouse click
    				Point p = e.getPoint();

    				// get the row index that contains that coordinate
    				int rowNumber = filesTable.rowAtPoint( p );

    				// Get the ListSelectionModel of the JTable
    				ListSelectionModel model = filesTable.getSelectionModel();

    				// set the selected interval of rows. Using the "rowNumber"
    				// variable for the beginning and end selects only that one row.
    				model.setSelectionInterval( rowNumber, rowNumber );

    				// Show the table popup
                    filesPopupMenu.show(filesTable, (int)e.getPoint().getX(), (int)e.getPoint().getY());
    			}
    		}
        });

        TableColumnModel cm = filesTable.getColumnModel();
        cm.getColumn(0).setPreferredWidth(245);
        cm.getColumn(1).setPreferredWidth(50);
        cm.getColumn(2).setPreferredWidth(75);

        filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        filesTable.setPreferredScrollableViewportSize(new Dimension(450, 379));
        filesScrollPane.setViewportView(filesTable);
        
        this.add(filesScrollPane, BorderLayout.CENTER);
    }

    private boolean isFileSelected() {
        return getSelectedFile() != null;
    }

    private void initPopupMenu() {
        filesPopupMenu = new JPopupMenu() {
        	@Override
            public void show(Component component, int i, int i1) {
                if(getSelectedFile() != null)
                    super.show(component, i, i1);
            }
        };
        launchFileMenuItem = new JMenuItem();
        lockFileMenuItem = new JMenuItem();
        deleteFileMenuItem = new JMenuItem();
        viewLogForFileMenuItem = new JMenuItem();
        announceFileMenuItem = new JMenuItem();
        pullFileMenuItem = new JMenuItem();
        importLocalFileMenuItem = new JMenuItem();
        
        launchFileMenuItem.setText(translator.get("FilesDialogContextMenuItemOpen"));
        launchFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                openExecuteFileMenuItemActionEvent(event);
            }
        }
        );

        lockFileMenuItem.setText(translator.get("FilesDialogContextMenuItemSetLock"));
        lockFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lockFileMenuItemActionPerformed(event);
            }
        });
        deleteFileMenuItem.setText(translator.get("FilesDialogContextMenuItemDeleteFile"));
        deleteFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                deleteFileMenuItemActionPerformed(event);
            }
        });

        viewLogForFileMenuItem.setText(translator.get("FilesDialogContextMenuItemViewLog"));
        viewLogForFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                viewLogForFileMenuItemActionPerformed(event);
            }
        });
        
        announceFileMenuItem.setText(translator.get("FilesDialogContextMenuItemPushFile"));
        announceFileMenuItem.setToolTipText("Push locally changed file");
        announceFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pushFileMenuItemActionPerformed(event);
            }
        }
        );
        pullFileMenuItem.setText(translator.get("FilesDialogContextMenuItemPullFile"));
        pullFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pullFileMenuItemActionPerformed(event);
            }
        });

        importLocalFileMenuItem.setText(translator.get("FilesDialogContextMenuItemImportFile"));
        importLocalFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                importLocalFileMenuItemActionPerformed(event);
            }
        }
        );


        
        filesPopupMenu.add(launchFileMenuItem);
        filesPopupMenu.addSeparator();
        filesPopupMenu.add(announceFileMenuItem);
        filesPopupMenu.add(pullFileMenuItem);
        filesPopupMenu.addSeparator();
        filesPopupMenu.add(lockFileMenuItem);
        filesPopupMenu.add(deleteFileMenuItem);
        filesPopupMenu.add(viewLogForFileMenuItem);
        filesPopupMenu.add(importLocalFileMenuItem);

        filesPopupMenu.addPopupMenuListener(new PopupMenuListener()
        {
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                JakeObject file = getSelectedFile();
                if (file == null)
                    return;
                //disable by default
                boolean setall = false;
                launchFileMenuItem.setEnabled(setall);
                announceFileMenuItem.setEnabled(setall);
                pullFileMenuItem.setEnabled(setall);
                lockFileMenuItem.setEnabled(setall);
                deleteFileMenuItem.setEnabled(setall);
                viewLogForFileMenuItem.setEnabled(setall);
                importLocalFileMenuItem.setEnabled(setall);
                int syncstatus = jakeGuiAccess.getJakeObjectSyncStatus(file);

                if ( (syncstatus & IJakeGuiAccess.SYNC_EXISTS_LOCALLY ) != 0)
                    launchFileMenuItem.setEnabled(true);

                if( (syncstatus & IJakeGuiAccess.SYNC_EXISTS_LOCALLY ) != 0  &&
                        (syncstatus & IJakeGuiAccess.SYNC_IS_IN_PROJECT) != 0)
                    announceFileMenuItem.setEnabled(true);

                if( (syncstatus & IJakeGuiAccess.SYNC_IS_IN_PROJECT) != 0 &&
                        (syncstatus & IJakeGuiAccess.SYNC_REMOTE_IS_NEWER) != 0)
                    pullFileMenuItem.setEnabled(true);

                if( (syncstatus & IJakeGuiAccess.SYNC_IS_IN_PROJECT) != 0 &&
                        (syncstatus & IJakeGuiAccess.SYNC_EXISTS_LOCALLY) != 0)
                    lockFileMenuItem.setEnabled(true);

                if(((syncstatus & IJakeGuiAccess.SYNC_EXISTS_LOCALLY) != 0) &&
                        ((syncstatus & IJakeGuiAccess.SYNC_REMOTE_IS_NEWER) == 0)
                        )
                    deleteFileMenuItem.setEnabled(true);

                if( (syncstatus & IJakeGuiAccess.SYNC_IS_IN_PROJECT) != 0)
                    viewLogForFileMenuItem.setEnabled(true);

                if( (syncstatus & IJakeGuiAccess.SYNC_IS_IN_PROJECT) == 0)
                    importLocalFileMenuItem.setEnabled(true);

            }

            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
            }

            public void popupMenuCanceled(PopupMenuEvent e) {
            }

        });

    }

    private void lockFileMenuItemActionPerformed(ActionEvent event) {
        log.info("lockFileMenuItemActionPerformed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject == null || fileObject.getClass() != FileObject.class)
            return;
        new SetSoftLockDialog(jakeGui.getMainFrame(), jakeGuiAccess, (FileObject)fileObject).setVisible(true);
    }

    private void deleteFileMenuItemActionPerformed(ActionEvent event) {
        log.info("deleteFileMenuItemActionPerformed");

        JakeObject fileObject = getSelectedFile();
        if (fileObject == null) 
            return;

        String filename = fileObject.getName();

        if (UserDialogHelper.askForConfirmation(this, translator.get("FilesPanelDialogConfirmDeleteDialogTitle"),
                translator.get("FilesPanelDialogConfirmDeleteDialogText", filename))) {
            if (jakeGuiAccess.deleteJakeObject(fileObject)) {
                UserDialogHelper.inform(this, translator.get("FilesPanelDialogFileDeletedTitle"),
                        translator.get("FilesPanelDialogFileDeletedText", filename),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                UserDialogHelper.inform(this, translator.get("FilesPanelDialogFileNotDeletedTitle"),
                        translator.get("FilesPanelDialogFileNotDeletedText", filename),
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        updateUI();
    }

    private void viewLogForFileMenuItemActionPerformed(ActionEvent event) {
        log.info("viewLogForFileMenuItemActionPerformed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
        	log.info("view log for a single object");
        	log.info(fileObject.getName());
            new ViewLogDialog(jakeGui.getMainFrame() , this.jakeGuiAccess , fileObject).setVisible(true);
        }
    }

    private void openExecuteFileMenuItemActionEvent(ActionEvent event) {
        log.info("openExecutFileMenuItemActionEvent");
        log.info("got file " + getSelectedFile().getName());
        String filename = getSelectedFile().getName();
        try {
            jakeGuiAccess.launchFile(filename);
        } catch (InvalidFilenameException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"),
                    translator.get("FilesPanelDialogCouldNotLaunchFileInvalidText"));
        } catch (LaunchException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"),
                    translator.get("FilesPanelDialogCouldNotLaunchFileOpenText"));
        } catch (IOException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"),
                    translator.get("FilesPanelDialogCouldNotLaunchFileIOText"));
        } catch (NoProjectLoadedException e) {
            UserDialogHelper.inform(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"),
                    translator.get("FilesPanelDialogCouldNotLaunchFileNoProjectText"));
        }
    }


    private void pushFileMenuItemActionPerformed(ActionEvent event) {
		log.info("pushFileMenuItemActionPerformed");

		JakeObject fileObject = getSelectedFile();
		if (fileObject != null) {
			String commitmsg = UserDialogHelper.showTextInputDialog(this, 
					translator.get("CommitMessage"), ""); 
			
			if(commitmsg != null)
				try {
					jakeGuiAccess.pushJakeObject(fileObject, commitmsg);
				} catch (NotLoggedInException e) {
					UserDialogHelper.translatedError(this, "NotLoggedInException");
				} catch (SyncException e) {
					UserDialogHelper.translatedError(this, "SyncException");
				}
		}
		updateUI();
	}
    
    private void pullFileMenuItemActionPerformed(ActionEvent event) {
		log.info("pullFileMenuItemActionPerformed");
		JakeObject fileObject = getSelectedFile();
		if (fileObject != null) {
			try {
				jakeGuiAccess.pullJakeObject(fileObject);
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
    
	public int getNameColPos() {
		return 0;
	}
	
	public int getTagsColPos() {
		return 2;
	}	 


        private void importLocalFileMenuItemActionPerformed(ActionEvent event) {
        log.info("importLocalFileMenuItemActionPerformed");
            JakeObject fileObject = getSelectedFile();
            if (fileObject != null) {
                if( jakeGuiAccess.importLocalFileIntoProject(fileObject.getName()) )
                {
                    // possible to add some notification here, but skipped because of usability (avoid dialogs)
                }
                else
                {
                    UserDialogHelper.error(this, translator.get("FilesPanelDialogImportFailedTitle"),
                            translator.get("FilesPanelDialogImportFailedText", fileObject.getName()) );
                    log.debug("show some error dialog");

                }
        }

    }



    private JScrollPane filesScrollPane;
    private JXTable filesTable;
    private final IJakeGuiAccess jakeGuiAccess;
    private JPopupMenu filesPopupMenu;
    private JMenuItem launchFileMenuItem;
    private JMenuItem lockFileMenuItem;
    private JMenuItem deleteFileMenuItem;
    private JMenuItem viewLogForFileMenuItem;
    private JMenuItem announceFileMenuItem;
    private JMenuItem pullFileMenuItem;
    private JMenuItem importLocalFileMenuItem;
}
