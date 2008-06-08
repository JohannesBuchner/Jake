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

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TextTranslationProvider;

/**
 * SEPM SS08 Gruppe: 3950 Projekt: Jake - a collaborative Environment User:
 *
 * @author domdorn, peter
 */
@SuppressWarnings("serial")
public class FilesPanel extends JPanel {
    private static Logger log = Logger.getLogger(FilesPanel.class);
    private final JakeGui jakeGui;
    private FilesTableModel filesTableModel;
    private static ITranslationProvider translator = new TextTranslationProvider();

    int tabindex = 0;

    private JakeObject getSelectedFile() {
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
        filesTable.setComponentPopupMenu(filesPopupMenu);
        filesTable.setColumnControlVisible(true);
        filesTable.setHighlighters(HighlighterFactory.createSimpleStriping());
        filesTable.setModel(filesTableModel);
        filesTable.setRolloverEnabled(false);
        filesTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(event)
                        && isFileSelected()) {
                    openExecutFileMenuItemActionEvent(null);
                }
            }
        }
        );

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
        filesPopupMenu = new JPopupMenu();
        openExecuteFileMenuItem = new JMenuItem();
        lockFileMenuItem = new JMenuItem();
        deleteFileMenuItem = new JMenuItem();
        viewLogForFileMenuItem = new JMenuItem();
        resolveFileConflictMenuItem = new JMenuItem();
        propagateFileMenuItem = new JMenuItem();
        pullFileMenuItem = new JMenuItem();
        importLocalFileMenuItem = new JMenuItem();

        openExecuteFileMenuItem.setText("Open");
        openExecuteFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                openExecutFileMenuItemActionEvent(event);
            }
        }
        );

        filesPopupMenu.add(openExecuteFileMenuItem);

        lockFileMenuItem.setText("Lock File...");
        lockFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                lockFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(lockFileMenuItem);

        deleteFileMenuItem.setText("Delete File...");
        deleteFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                deleteFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(deleteFileMenuItem);

        viewLogForFileMenuItem.setText("View Log...");
        viewLogForFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                viewLogForFileMenuItemActionPerfomed(event);
            }
        });
        filesPopupMenu.add(viewLogForFileMenuItem);

        resolveFileConflictMenuItem.setText("Resolve Conflict...");
        resolveFileConflictMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                resolveFileConflictMenuItemActionPerformed(e);
            }
        });
        filesPopupMenu.add(resolveFileConflictMenuItem);
        filesPopupMenu.addSeparator();

        propagateFileMenuItem.setText("Propagate File");
        propagateFileMenuItem.setToolTipText("Propagate locally changed file");
        propagateFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                propagateFileMenuItemActionPerfomed(event);
            }
        }
        );
        filesPopupMenu.add(propagateFileMenuItem);

        pullFileMenuItem.setText("Pull File");
        pullFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                pullFileMenuItemActionPerformed(event);
            }
        });
        filesPopupMenu.add(pullFileMenuItem);

        importLocalFileMenuItem.setText("Import into Project");
        importLocalFileMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                importLocalFileMenuItemActionPerformed(event);
            }
        }
        );

        filesPopupMenu.add(importLocalFileMenuItem);

    }


    private void resolveFileConflictMenuItemActionPerformed(ActionEvent e) {
        JakeObject fileObject = getSelectedFile();
        if (fileObject != null)
            new ResolveConflictDialog(jakeGui.getMainFrame()).setJakeObject(fileObject).setVisible(true);
    }


    private void lockFileMenuItemActionPerformed(ActionEvent event) {
        log.info("lockFileMenuItemActionPerformed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject == null)
            return;
        jakeGuiAccess.setJakeObjectLock(!jakeGuiAccess.getJakeObjectLock(fileObject), fileObject);
// TODO @Peter: insert softlock actions here!


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
        // no else part, because if the user cancels deletion, nothing should happen!       
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
            UserDialogHelper.error(this, "Couldn't open file", "Sorry, the object \n\"" + filename +
                    "\"\n could not be opend because it seems to be no valid file.\n" +
                    "Please contact the Jake team.");

            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        } catch (LaunchException e) {
            UserDialogHelper.error(this, "Couldn't open file",
                    "Sorry, the file \n\"" + filename + "\"\n could not be opend. \n\n" +
                            "Please check your operating systems settings for default file actions. ");

        } catch (IOException e) {
            UserDialogHelper.error(this, "Couldn't open file", "The file \n\"" + filename + "\"\n" +
                    " could not be opened because of an input/output error.\n" +
                    "Please manually check if the file is accessible through \n" +
                    "the tools provided by your operating system (Windows Explorer, Konqueror, Finder, etc.)\n" +
                    "Maybe you should also check your filesystem for failures (scandisk, fsck, etc.)");

        } catch (NoProjectLoadedException e) {
            UserDialogHelper.inform(this, "Couldn't open file",
                    "The file \n\"" + filename + "\"\n" +
                            " could not be opened because no project is loaded. \n THIS SHOULD NOT HAPPEN \n" +
                            "Please report to the Jake Team.");
        }
    }


    private void propagateFileMenuItemActionPerfomed(ActionEvent event) {
        log.info("propagateFileMenuItemActionPerfomed");

        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            jakeGuiAccess.propagateJakeObject(fileObject);
            UserDialogHelper.inform(this, "Propagation sheduled",
                    "The propagation of the file \n\"" + fileObject.getName() + "\"\n was sheduled. \n\n" +
                            "It could take some time to propagate it to other project members, \n" +
                            "depending on the availability of other project members.");
        }
    }

    private void pullFileMenuItemActionPerformed(ActionEvent event) {
        log.info("pullFileMenuItemActionPerformed");
        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            jakeGuiAccess.pullJakeObject(fileObject);
            UserDialogHelper.inform(this, "Pulling sheduled",
                    "The pulling of the file \n\"" + fileObject.getName() + "\"\n" +
                            " was sheduled. \n\n" +
                            "It could take some time to get it from other project members,\n" +
                            "due to the availability of the other project members.");
        }
    }

    private void importLocalFileMenuItemActionPerformed(ActionEvent event) {
        log.info("importLocalFileMenuItemActionPerformed");

        JakeObject fileObject = getSelectedFile();
        if (fileObject != null) {
            Integer nstatusnr = jakeGuiAccess.getFileObjectSyncStatus(fileObject);

            if (nstatusnr != 102) {
                UserDialogHelper.warning(this, "Import failed",
                        "The file \n\"" + fileObject.getName() + "\"\n " +
                                "could not be imported into the project because it is either already \n" +
                                "a file in the projects repository or has not a valid filename ");
            } else {
                if (jakeGuiAccess.importLocalFileIntoProject(fileObject.getName())) {
                    UserDialogHelper.inform(this, "Import succeeded",
                            "The file \n\"" + fileObject.getName() + "\"\n" +
                                    "was succcessfully imported into this JakeProject."
                    );
                    filesTable.updateUI();
                } else {
                    UserDialogHelper.error(this, "Import failed",
                            "The file \n\"" + fileObject.getName() + "\"\n " +
                                    "could not be imported into the project because it is either already \n" +
                                    "a file in the projects repository or has not a valid filename "
                    );
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
    private JMenuItem openExecuteFileMenuItem;
    private JMenuItem lockFileMenuItem;
    private JMenuItem deleteFileMenuItem;
    private JMenuItem viewLogForFileMenuItem;
    private JMenuItem resolveFileConflictMenuItem;
    private JMenuItem propagateFileMenuItem;
    private JMenuItem pullFileMenuItem;
    private JMenuItem importLocalFileMenuItem;
}
