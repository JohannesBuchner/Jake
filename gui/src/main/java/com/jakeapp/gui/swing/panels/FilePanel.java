/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FilePanel.java
 *
 * Created on Dec 2, 2008, 10:28:37 PM
 */
package com.jakeapp.gui.swing.panels;

import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.ETreeTable;
import com.jakeapp.gui.swing.controls.ProjectFilesTreeCellRenderer;
import com.jakeapp.gui.swing.controls.ETable;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.HudButtonUI;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.models.FolderObjectsTreeTableModel;
import com.jakeapp.gui.swing.models.FileObjectsTableModel;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Enumeration;

/**
 * @author studpete
 */
public class FilePanel extends javax.swing.JPanel implements ProjectSelectionChanged {
	private static final Logger log = Logger.getLogger(FilePanel.class);
	private static FilePanel instance;

	private PopupMenu fileMenu;

	private Project project;

	private java.util.List<FileSelectionChanged> fileSelectionListeners = new ArrayList<FileSelectionChanged>();
	private ResourceMap resourceMap;
	private JToggleButton treeBtn;
	private JToggleButton flatBtn;
	private JToggleButton allBtn;
	private JToggleButton newBtn;
	private JToggleButton conflictsBtn;


	/**
	 * Displays files as a file/folder tree or list of relative paths (classic Jake ;-)
	 */
	public void switchFileDisplay() {
		if (flatBtn.isSelected()) {
			fileTreeTableScrollPane.setViewportView(fileTable);
		} else {
			fileTreeTableScrollPane.setViewportView(fileTreeTable);
		}
	}

	public void addFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.add(listener);
	}

	public void removeFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.remove(listener);
	}

	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners: " + objs.toArray());
		for (FileSelectionChanged c : fileSelectionListeners) {
			c.fileSelectionChanged(new FileSelectionChanged.FileSelectedEvent(objs));
		}
	}

	/**
	 * Creates new form FilePanel
	 */
	public FilePanel() {

		// save for instance access
		instance = this;

		// init resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
			 JakeMainApp.class).getContext().getResourceMap(FilePanel.class));


		initComponents();

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		//infoPanel.setBackgroundPainter(Platform.getStyler().getContentPanelBackgroundPainter());

		// make the buttons more fancy
		//Platform.getStyler().MakeWhiteRecessedButton(newFilesButton);
		//Platform.getStyler().MakeWhiteRecessedButton(resolveButton);
		//Platform.getStyler().MakeWhiteRecessedButton(illegalFilenamesButton);

		fileTreeTable.setScrollsOnExpand(true);
		fileTreeTable.setSortable(true);
		fileTreeTable.setColumnControlVisible(true);

		// ETreeTable performs its own striping on the mac
		// TODO: make this more beautiful...
		if (!Platform.isMac()) {
			fileTreeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
			fileTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}

		fileTreeTable.setTreeCellRenderer(new ProjectFilesTreeCellRenderer());

		fileTreeTable.addMouseListener(new FileTreeTableMouseListener(this));
		fileTreeTable.addKeyListener(new FileTreeTableKeyListener(this));
	}

	public static FilePanel getInstance() {
		return instance;
	}

	public ResourceMap getResourceMap() {
		return resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	private class FileTreeTableKeyListener implements KeyListener {
		private FilePanel panel;

		public FileTreeTableKeyListener(FilePanel p) {
			this.panel = p;
		}

		@Override
		public void keyTyped(KeyEvent e) {
			// Do nothing
		}

		@Override
		public void keyPressed(KeyEvent e) {
			// Do nothing
		}

		@Override
		public void keyReleased(KeyEvent e) {
			java.util.List<FileObject> list = new ArrayList<FileObject>();
			for (int row : fileTreeTable.getSelectedRows()) {
				ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable.getValueAt(row, 0);
				if (node.isFile()) {
					list.add(node.getFileObject());
				}
			}
			panel.notifyFileSelectionListeners(list);
		}
	}

	private class FileTreeTableMouseListener implements MouseListener {
		private FilePanel panel;

		public FileTreeTableMouseListener(FilePanel p) {
			super();
			this.panel = p;
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			if (SwingUtilities.isRightMouseButton(me)) {
				// get the coordinates of the mouse click
				Point p = me.getPoint();

				// get the row index that contains that coordinate
				int rowNumber = fileTreeTable.rowAtPoint(p);

				// Get the ListSelectionModel of the JTable
				ListSelectionModel model = fileTreeTable.getSelectionModel();

				// set the selected interval of rows. Using the "rowNumber"
				// variable for the beginning and end selects only that one
				// row.
				// ONLY select new item if we didn't select multiple items.
				if (fileTreeTable.getSelectedRowCount() <= 1) {
					model.setSelectionInterval(rowNumber, rowNumber);
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable.getValueAt(rowNumber, 0);
					if (node.isFile()) {
						java.util.List<FileObject> list = new ArrayList<FileObject>();
						list.add(node.getFileObject());
						panel.notifyFileSelectionListeners(list);
					} else {
						panel.notifyFileSelectionListeners(null);
					}
				}

				showMenu(me);
			} else if (SwingUtilities.isLeftMouseButton(me)) {
				java.util.List<FileObject> list = new ArrayList<FileObject>();
				for (int row : fileTreeTable.getSelectedRows()) {
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable.getValueAt(row, 0);
					if (node.isFile()) {
						list.add(node.getFileObject());
					}
				}
				panel.notifyFileSelectionListeners(list);
			}
		}

		private void showMenu(MouseEvent me) {
			JPopupMenu pm = new JakePopupMenu();

			pm.add(new JMenuItem(new OpenFileAction(fileTreeTable, getProject())));
			// TODO: show always? dynamically? (alwasy for now...while dev)
			pm.add(new JMenuItem(new ResolveConflictFileAction(fileTreeTable)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new AnnounceFileAction(fileTreeTable)));
			pm.add(new JMenuItem(new PullFileAction(fileTreeTable)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new DeleteFileAction(fileTreeTable)));
			pm.add(new JMenuItem(new RenameFileAction(fileTreeTable)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new InspectorFileAction(fileTreeTable)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new ImportFileAction(fileTreeTable)));
			pm.add(new JMenuItem(new NewFolderFileAction(fileTreeTable)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new LockFileAction(fileTreeTable)));
			pm.add(new JMenuItem(new LockWithMessageFileAction(fileTreeTable)));


			pm.show(fileTreeTable, (int) me.getPoint().getX(), (int) me.getPoint()
				 .getY());
		}

		@Override
		public void mousePressed(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseReleased(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseEntered(MouseEvent mouseEvent) {
		}

		@Override
		public void mouseExited(MouseEvent mouseEvent) {
		}
	}

	/**
	 * This method is called from within the constructor to
	 * initialize the form.
	 */
	private void initComponents() {

		this.setLayout(new MigLayout("wrap 1, ins 0, fill, gap 0! 0!"));
		this.setBackground(Color.WHITE);

		// add control bar
		final JPanel controlPanel = new JPanel(new MigLayout("wrap 2, ins 2, fill, gap 0! 0!"));
		controlPanel.setBackground(Platform.getStyler().getFilterPaneColor(true));

		// change color on window loose/gain focus
		WindowUtils.installWindowFocusListener(new WindowFocusListener() {
			@Override
			public void windowGainedFocus(WindowEvent e) {
				controlPanel.setBackground(Platform.getStyler().getFilterPaneColor(true));
			}

			@Override
			public void windowLostFocus(WindowEvent e) {
				controlPanel.setBackground(Platform.getStyler().getFilterPaneColor(false));
			}
		}, controlPanel);

		treeBtn = new JToggleButton(getResourceMap().getString("treeButton"));
		treeBtn.setUI(new HudButtonUI());
		controlPanel.add(treeBtn, "split 2");
		treeBtn.setSelected(true);

		flatBtn = new JToggleButton(getResourceMap().getString("flatButton"));
		flatBtn.setUI(new HudButtonUI());
		controlPanel.add(flatBtn);

		ActionListener updateViewAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO: update view
				log.debug("updating view in filepanel...");
			}
		};

		flatBtn.addActionListener(updateViewAction);
		treeBtn.addActionListener(updateViewAction);

		ButtonGroup showGrp = new ButtonGroup();
		showGrp.add(flatBtn);
		showGrp.add(treeBtn);

		// I can't believe Java STILL doesn't have foreach support for Enumerations - pathetic!
		// Add a mouse listener to each view changing button
		for (Enumeration e = showGrp.getElements(); e.hasMoreElements();) {
			AbstractButton b = (AbstractButton) e.nextElement();
			b.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					switchFileDisplay();
				}
			});
		}

		allBtn = new JToggleButton(getResourceMap().getString("filterAllButton"));
		allBtn.setUI(new HudButtonUI());
		controlPanel.add(allBtn, "split 3, right");
		allBtn.setSelected(true);

		newBtn = new JToggleButton(getResourceMap().getString("filterNewButton"));
		newBtn.setUI(new HudButtonUI());
		controlPanel.add(newBtn, "right");

		conflictsBtn = new JToggleButton(getResourceMap().getString("filterConflictsButton"));
		conflictsBtn.setUI(new HudButtonUI());
		controlPanel.add(conflictsBtn, "right, wrap");

		this.add(controlPanel, "growx");

		allBtn.addActionListener(updateViewAction);
		newBtn.addActionListener(updateViewAction);
		conflictsBtn.addActionListener(updateViewAction);

		ButtonGroup filterGrp = new ButtonGroup();
		filterGrp.add(allBtn);
		filterGrp.add(newBtn);
		filterGrp.add(conflictsBtn);

		// add file treetable
		fileTreeTableScrollPane = new javax.swing.JScrollPane();
		fileTreeTable = new ETreeTable();

		// add file table
		fileTable = new ETable();

		// fileTreeTableScrollPane.setViewportView(fileTreeTable);
		fileTreeTableScrollPane.setViewportView(fileTable);

		this.add(fileTreeTableScrollPane, "grow");
	}

	private org.jdesktop.swingx.JXTreeTable fileTreeTable;
	private org.jdesktop.swingx.JXTable fileTable;
	private javax.swing.JScrollPane fileTreeTableScrollPane;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;

		// we have to cope with no project selected state.
		if (project != null) {
			TreeTableModel treeTableModel = null;
			TableModel tableModel = null;

			try {
				treeTableModel = new FolderObjectsTreeTableModel(new ProjectFilesTreeNode(JakeMainApp.getApp().getCore().getProjectRootFolder(JakeMainApp.getApp().getProject())));
				tableModel = new FileObjectsTableModel(JakeMainApp.getApp().getCore().getAllProjectFiles(JakeMainApp.getApp().getProject()));
			} catch (ProjectFolderMissingException e) {
				log.warn("Project folder missing!!");
			}

			fileTreeTable.setTreeTableModel(treeTableModel);
			fileTable.setModel(tableModel);
		}
	}
}
