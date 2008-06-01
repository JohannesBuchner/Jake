package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

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

    int tabindex = 0;
    
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
        if(tabindex >= 0)
            jakeGui.getMainTabbedPane().setTitleAt(tabindex,
                "Files ("+ filesTableModel.getFilesCount()
                        + "/"+ FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize())+")"
           );
    }

    public void updateUI() {
        super.updateUI();
        if(filesTableModel!=null)
        {
            filesTableModel.updateData();
            if(tabindex >= 0)
                jakeGui.getMainTabbedPane().setTitleAt(tabindex,
                    "Files ("+ filesTableModel.getFilesCount()
                            + "/"+ FilesLib.getHumanReadableFileSize(filesTableModel.getSummedFilesize())+")");
        }
    }

    /**
	 * ** Files Context Menu ****
	 */
	private void resolveFileConflictMenuItemActionPerformed(ActionEvent e) {
		new ResolveConflictDialog(jakeGui.getMainFrame()).setVisible(true);
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

		TableColumnModel cm = filesTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(245);
		cm.getColumn(1).setPreferredWidth(50);
		cm.getColumn(2).setPreferredWidth(75);

		filesTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		filesTable.setPreferredScrollableViewportSize(new Dimension(450, 379));
		filesScrollPane.setViewportView(filesTable);

		this.add(filesScrollPane, BorderLayout.CENTER);
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

		openExecuteFileMenuItem.setText("Open");
		filesPopupMenu.add(openExecuteFileMenuItem);

		lockFileMenuItem.setText("Lock File...");
		filesPopupMenu.add(lockFileMenuItem);

		deleteFileMenuItem.setText("Delete File...");
		filesPopupMenu.add(deleteFileMenuItem);

		viewLogForFileMenuItem.setText("View Log...");
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
		filesPopupMenu.add(propagateFileMenuItem);

		pullFileMenuItem.setText("Pull File");
		filesPopupMenu.add(pullFileMenuItem);



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
}
