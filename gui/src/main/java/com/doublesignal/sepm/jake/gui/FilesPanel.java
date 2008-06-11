package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TextTranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * SEPM SS08 Gruppe: 3950 Projekt: Jake - a collaborative Environment User:
 *
 * @author domdorn, peter
 */
@SuppressWarnings("serial")
public class FilesPanel extends JPanel {
	private static final Logger log = Logger.getLogger(FilesPanel.class);
	
	private static final ITranslationProvider translatorProvider = TranslatorFactory.getTranslator();
	
	private final JakeGui jakeGui;
    private FilesTableModel filesTableModel;
    private static ITranslationProvider translator = new TextTranslationProvider();

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


        tabindex = jakeGui.getMainTabbedPane().indexOfTab("filestab");
        if (tabindex >= 0)
            jakeGui.getMainTabbedPane().setTitleAt(tabindex,
                    "Files (" + filesTableModel.getFilesCount()
                            + "/" + FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize()) + ")"
            );
    }

    public void updateUI() {
        super.updateUI();
        if (filesTableModel != null) {
            filesTableModel.updateData();
            if (tabindex >= 0)
                jakeGui.getMainTabbedPane().setTitleAt(tabindex,
                        "Files (" + filesTableModel.getFilesCount()
                                + "/" + FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize()) + ")");
        }
    }


    public FilterPipeline getFilters() {
        return filesTable.getFilters();
    }

    public void setFilters(FilterPipeline filterPipeline) {
        filesTable.setFilters(filterPipeline);
    }

    private void initComponents() {
        filesScrollPane = new JScrollPane();
        filesTable = new JXTable();
        this.setLayout(new BorderLayout());
        filesTableModel = new FilesTableModel(jakeGuiAccess);
        //filesTable.setComponentPopupMenu(filesPopupMenu);
        filesTable.setColumnControlVisible(true);
        filesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        filesTable.setModel(filesTableModel);
        filesTable.setRolloverEnabled(false);
    // sorry, we don't need a mouseadapter
      filesTable.addMouseListener(new MouseAdapter() {
    		public void mouseClicked( MouseEvent e ) {
    			log.debug("click reveived");
                if (e.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(e)
                        && isFileSelected()) {
                    openExecutFileMenuItemActionEvent(null);
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
        resolveFileConflictMenuItem = new JMenuItem();
        propagateFileMenuItem = new JMenuItem();
        pullFileMenuItem = new JMenuItem();
        importLocalFileMenuItem = new JMenuItem();

        launchFileMenuItem.setText(translator.get("FilesDialogContextMenuItemOpen"));
        launchFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                openExecutFileMenuItemActionEvent(event);
            }
        }
        );

        filesPopupMenu.add(launchFileMenuItem);

        lockFileMenuItem.setText(translator.get("FilesDialogContextMenuItemSetLock"));
        lockFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lockFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(lockFileMenuItem);

        deleteFileMenuItem.setText(translator.get("FilesDialogContextMenuItemDeleteFile"));
        deleteFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                deleteFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(deleteFileMenuItem);

        viewLogForFileMenuItem.setText(translator.get("FilesDialogContextMenuItemViewLog"));
        viewLogForFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                viewLogForFileMenuItemActionPerfomed(event);
            }
        });
        filesPopupMenu.add(viewLogForFileMenuItem);

        resolveFileConflictMenuItem.setText(translator.get("FilesDialogContextMenuItemResolveConflict"));
        resolveFileConflictMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resolveFileConflictMenuItemActionPerformed(e);
            }
        });
        filesPopupMenu.add(resolveFileConflictMenuItem);
        filesPopupMenu.addSeparator();

        propagateFileMenuItem.setText(translator.get("FilesDialogContextMenuItemPropagateFile"));
        propagateFileMenuItem.setToolTipText("Propagate locally changed file");
        propagateFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                propagateFileMenuItemActionPerfomed(event);
            }
        }
        );
        filesPopupMenu.add(propagateFileMenuItem);

        pullFileMenuItem.setText(translator.get("FilesDialogContextMenuItemPullFile"));
        pullFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pullFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(pullFileMenuItem);

        importLocalFileMenuItem.setText(translator.get("FilesDialogContextMenuItemImportFile"));
        importLocalFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                importLocalFileMenuItemActionPerformed(event);
            }
        }
        );
        filesPopupMenu.add(importLocalFileMenuItem);
    }


    private void resolveFileConflictMenuItemActionPerformed(ActionEvent e) {
        JakeObject localFile = getSelectedFile();
        if (localFile != null && localFile.getClass().equals(FileObject.class))
            new ResolveConflictDialog(jakeGui.getMainFrame(), (FileObject)localFile, jakeGuiAccess).setVisible(true);
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
                UserDialogHelper.inform(this, translator.get("FilesPanelDialogFileDeletedTitle"), translator.get("FilesPanleDialogFileDeletedText", filename),
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                UserDialogHelper.inform(this, translator.get("FilesPanelDialogFileNotDeletedTitle"), translator.get("FilesPanelDialogFileNotDeletedText", filename),
                        JOptionPane.ERROR_MESSAGE);
            }

        }
        updateUI();
    }

    private void viewLogForFileMenuItemActionPerfomed(ActionEvent event) {
        log.info("viewLogForFileMenuItemActionPerfomed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            new ViewLogDialog(jakeGui.getMainFrame()).setJakeObject(fileObject).setVisible(true);
        }
    }

    private void openExecutFileMenuItemActionEvent(ActionEvent event) {
        log.info("openExecutFileMenuItemActionEvent");
        log.info("got file " + getSelectedFile().getName());
        String filename = getSelectedFile().getName();
        try {
            jakeGuiAccess.launchFile(filename);
        } catch (InvalidFilenameException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"), translator.get("FilesPanelDialogCouldNotLaunchFileInvalidText"));
        } catch (LaunchException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"), translator.get("FilesPanelDialogCouldNotLaunchFileOpenText"));
        } catch (IOException e) {
            UserDialogHelper.error(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"), translator.get("FilesPanelDialogCouldNotLaunchFileIOText"));
        } catch (NoProjectLoadedException e) {
            UserDialogHelper.inform(this, translator.get("FilesPanelDialogCouldNotLaunchFileTitle"), translator.get("FilesPanelDialogCouldNotLaunchFileNoProjectText"));
        }
    }


    private void propagateFileMenuItemActionPerfomed(ActionEvent event) {
        log.info("propagateFileMenuItemActionPerfomed");

        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            jakeGuiAccess.propagateJakeObject(fileObject);
            UserDialogHelper.inform(this, translator.get("FilesPanelDialogPropagationMessageTitle"), translator.get("FilesPanelDialogPropagationMessageText", fileObject.getName()));
        }
    }

    private void pullFileMenuItemActionPerformed(ActionEvent event) {
        log.info("pullFileMenuItemActionPerformed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            jakeGuiAccess.pullJakeObject(fileObject);
            UserDialogHelper.inform(this, translator.get("FilesPanelDialogPullMessageTitle"), translator.get("FilesPanelDialogPullMessageText", fileObject.getName()));
        }
    }

    private void importLocalFileMenuItemActionPerformed(ActionEvent event) {
        log.info("importLocalFileMenuItemActionPerformed");

        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
        	Integer nstatusnr = jakeGuiAccess.getFileObjectSyncStatus(fileObject);

        	if (nstatusnr != 102) {
        		UserDialogHelper.warning(this, translator.get(""), translator.get("", fileObject.getName()));
        	} else {
        		if (jakeGuiAccess.importLocalFileIntoProject(fileObject.getName())) {
        			UserDialogHelper.inform(this, translator.get(""), translator.get("", fileObject.getName()));
        			filesTable.updateUI();
        		} else {
        			UserDialogHelper.error(this, translator.get(""), translator.get("", fileObject.getName()));
        		}
        	}
        }
    }
    
	public int getNameColPos() {
		// TODO Auto-generated method stub
		return 0;
	}
	
	public int getTagsColPos() {
		// TODO Auto-generated method stub
		return 2;
	}	 


    private JScrollPane filesScrollPane;
    private JXTable filesTable;
    private final IJakeGuiAccess jakeGuiAccess;
    private JPopupMenu filesPopupMenu;
    private JMenuItem launchFileMenuItem;
    private JMenuItem lockFileMenuItem;
    private JMenuItem deleteFileMenuItem;
    private JMenuItem viewLogForFileMenuItem;
    private JMenuItem resolveFileConflictMenuItem;
    private JMenuItem propagateFileMenuItem;
    private JMenuItem pullFileMenuItem;
    private JMenuItem importLocalFileMenuItem;
}
