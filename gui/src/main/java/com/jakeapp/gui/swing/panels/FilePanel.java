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
import com.jakeapp.gui.swing.callbacks.NodeSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.cmacwidgets.GreenHudButtonUI;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTreeTable;
import com.jakeapp.gui.swing.controls.cmacwidgets.JakeHudButtonUI;
import com.jakeapp.gui.swing.controls.cmacwidgets.RedHudButtonUI;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.filters.FileObjectConflictStatusFilter;
import com.jakeapp.gui.swing.filters.FileObjectDateFilter;
import com.jakeapp.gui.swing.helpers.DebugHelper;
import com.jakeapp.gui.swing.helpers.FileObjectLockedCell;
import com.jakeapp.gui.swing.helpers.FileObjectStatusCell;
import com.jakeapp.gui.swing.helpers.JakeExecutor;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.models.FileObjectsTableModel;
import com.jakeapp.gui.swing.models.FolderObjectsTreeTableModel;
import com.jakeapp.gui.swing.renderer.FileLockedTreeCellRenderer;
import com.jakeapp.gui.swing.renderer.FileStatusTreeCellRenderer;
import com.jakeapp.gui.swing.renderer.ProjectFilesTableCellRenderer;
import com.jakeapp.gui.swing.renderer.ProjectFilesTreeCellRenderer;
import com.jakeapp.gui.swing.worker.GetAllProjectFilesWorker;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author studpete, csutter
 */
public class FilePanel extends javax.swing.JPanel implements ProjectSelectionChanged, ProjectChanged {
	private static final Logger log = Logger.getLogger(FilePanel.class);
	private static FilePanel instance;

	public static final int FILETREETABLE_NODECOLUMN = 2;
	public static final int FILETABLE_NODECOLUMN = 2;

	private Project project;

	private java.util.List<FileSelectionChanged> fileSelectionListeners = new ArrayList<FileSelectionChanged>();
	private java.util.List<NodeSelectionChanged> nodeSelectionListeners = new ArrayList<NodeSelectionChanged>();

	private ResourceMap resourceMap;
	private JToggleButton treeBtn;
	private JToggleButton flatBtn;
	private JToggleButton allBtn;
	private JToggleButton newBtn;
	private JToggleButton conflictsBtn;

	// use tree (true) or flat (false)
	// TODO: set to false for debug only
	private boolean treeViewActive = false;

	private final JPopupMenu popupMenu = new JakePopupMenu();

	/**
	 * Displays files as a file/folder tree or list of relative paths (classic Jake ;-)
	 */
	public void switchFileDisplay() {
		if (flatBtn.isSelected()) {
			fileTreeTableScrollPane.setViewportView(fileTable);
			treeViewActive = false;
			resetFilter();
		} else {
			// We don't need this anymore because resetFilter() does it for us 
			// fileTreeTableScrollPane.setViewportView(fileTreeTable);
			treeViewActive = true;
			resetFilter();
		}
	}

	public void filterFileDisplay() {
		if (conflictsBtn.isSelected()) {
			Filter filter = new FileObjectConflictStatusFilter();
			switchToFlatAndFilter(new FilterPipeline(filter));
		} else if (newBtn.isSelected()) {
			Filter filter = new FileObjectDateFilter();
			switchToFlatAndFilter(new FilterPipeline(filter));
		} else {
			resetFilter();
		}
	}

	/**
	 * Switches to flat view and applies a filter pipeline
	 *
	 * @param pipeline The filter pipeline to apply
	 */
	public void switchToFlatAndFilter(FilterPipeline pipeline) {
		fileTreeTableScrollPane.setViewportView(fileTable);
		fileTable.setFilters(pipeline);
		flatBtn.setSelected(true);
	}

	/**
	 * Resets all applied filters
	 */
	public void resetFilter() {
		fileTable.setFilters(null);
		if (treeViewActive) {
			fileTreeTableScrollPane.setViewportView(fileTreeTable);
			treeBtn.setSelected(true);
		}
	}

	private void initPopupMenu(JPopupMenu pm) {
		pm.add(new JMenuItem(new OpenFileAction()));
		// TODO: show always? dynamically? (alwasy for now...while dev)
		pm.add(new JMenuItem(new ResolveConflictFileAction()));
		pm.add(new JSeparator());
		pm.add(new JMenuItem(new AnnounceFileAction()));
		pm.add(new JMenuItem(new PullFileAction()));
		pm.add(new JSeparator());
		pm.add(new JMenuItem(new DeleteFileAction()));
		pm.add(new JMenuItem(new RenameFileAction()));
		pm.add(new JSeparator());
		pm.add(new JMenuItem(new InspectorFileAction()));
		pm.add(new JSeparator());
		pm.add(new JMenuItem(new ImportFileAction()));
		pm.add(new JMenuItem(new CreateFolderFileAction()));
		pm.add(new JSeparator());
		pm.add(new JMenuItem(new LockFileAction()));
		pm.add(new JMenuItem(new LockWithMessageFileAction()));
	}

	public void addFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.add(listener);
	}

	public void removeFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.remove(listener);
	}

	public void addNodeSelectionListener(NodeSelectionChanged listener) {
		nodeSelectionListeners.add(listener);
	}

	public void removeNodeSelectionListener(NodeSelectionChanged listener) {
		nodeSelectionListeners.remove(listener);
	}

	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners");
		for (FileSelectionChanged c : fileSelectionListeners) {
			//c.fileSelectionChanged(new FileSelectionChanged.FileSelectedEvent(objs));
		}
	}

	public void notifyNodeSelectionListeners(
					java.util.List<ProjectFilesTreeNode> objs) {
		log.debug("notify selection listeners");
		for (NodeSelectionChanged c : nodeSelectionListeners) {
			c.nodeSelectionChanged(new NodeSelectionChanged.NodeSelectedEvent(objs));
		}
	}

	/**
	 * Creates new form FilePanel
	 */
	public FilePanel() {

		// save for instance access
		instance = this;

		// init resource map
		setResourceMap(
						org.jdesktop.application.Application.getInstance(JakeMainApp.class)
										.getContext().getResourceMap(FilePanel.class));


		initComponents();

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		fileTreeTable.setScrollsOnExpand(true);
		fileTreeTable.setSortable(true);
		fileTreeTable.setColumnControlVisible(true);

		// ETreeTable performs its own striping on the mac
		if (!Platform.isMac()) {
			fileTreeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
			fileTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}

		fileTreeTable.setTreeCellRenderer(new ProjectFilesTreeCellRenderer());
		fileTable.setDefaultRenderer(ProjectFilesTreeNode.class,
						new ProjectFilesTableCellRenderer());

		fileTreeTable.setDefaultRenderer(FileObjectStatusCell.class,
						new FileStatusTreeCellRenderer());
		fileTreeTable.setDefaultRenderer(FileObjectLockedCell.class,
						new FileLockedTreeCellRenderer());

		fileTable.setDefaultRenderer(FileObjectStatusCell.class,
						new FileStatusTreeCellRenderer());
		fileTable.setDefaultRenderer(FileObjectLockedCell.class,
						new FileLockedTreeCellRenderer());

		fileTreeTable.addMouseListener(
						new FileContainerMouseListener(this, fileTreeTable,
										FILETREETABLE_NODECOLUMN));
		fileTable.addMouseListener(
						new FileContainerMouseListener(this, fileTable, FILETABLE_NODECOLUMN));

		fileTreeTable.addKeyListener(new FileTreeTableKeyListener(this));

		// Initialize popup menu
		initPopupMenu(popupMenu);
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

	public List<ProjectFilesTreeNode> getSelectedNodes() {
		java.util.List<ProjectFilesTreeNode> nodeObjs = new ArrayList<ProjectFilesTreeNode>();
		for (int row : fileTreeTable.getSelectedRows()) {
			ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable
							.getValueAt(row,
											(treeViewActive ? FILETREETABLE_NODECOLUMN : FILETABLE_NODECOLUMN));
			nodeObjs.add(node);
		}

		return nodeObjs;
	}

	@Override
	public void projectChanged(ProjectChangedEvent ev) {

	}

	private class FileTreeTableKeyListener extends KeyAdapter {
		private FilePanel panel;

		public FileTreeTableKeyListener(FilePanel p) {
			this.panel = p;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();
			java.util.List<ProjectFilesTreeNode> nodeObjs = new ArrayList<ProjectFilesTreeNode>();
			for (int row : fileTreeTable.getSelectedRows()) {
				ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable
								.getValueAt(row,
												(treeViewActive ? FILETREETABLE_NODECOLUMN : FILETABLE_NODECOLUMN));
				if (node.isFile()) {
					fileObjs.add(node.getFileObject());
				}
				nodeObjs.add(node);
			}

			panel.notifyFileSelectionListeners(fileObjs);
			panel.notifyNodeSelectionListeners(nodeObjs);
		}
	}

	private class FileContainerMouseListener extends MouseAdapter {
		private FilePanel panel;
		private JTable container;
		private int nodeColumn;

		public FileContainerMouseListener(FilePanel p, JTable fileContainer,
						int nodeColumn) {
			super();
			this.panel = p;
			this.container = fileContainer;
			this.nodeColumn = nodeColumn;
		}

		@Override
		public void mouseClicked(MouseEvent me) {
			log.info("mouseClicked: " + me);
			if (SwingUtilities.isRightMouseButton(me)) {
				// get the coordinates of the mouse click
				Point p = me.getPoint();

				// get the row index that contains that coordinate
				int rowNumber = container.rowAtPoint(p);

				// Get the ListSelectionModel of the JTable
				ListSelectionModel model = container.getSelectionModel();

				// set the selected interval of rows. Using the "rowNumber"
				// variable for the beginning and end selects only that one
				// row.
				// ONLY select new item if we didn't select multiple items.


				java.util.List<ProjectFilesTreeNode> nodeObjs = new ArrayList<ProjectFilesTreeNode>();

				log.debug("Selected rows: " + DebugHelper
								.arrayToString(container.getSelectedRows()));

				if (container.getSelectedRowCount() <= 1) {
					model.setSelectionInterval(rowNumber, rowNumber);
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) container
									.getValueAt(rowNumber, nodeColumn);

					if (node != null && node.isFile()) {
						java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();

						fileObjs.add(node.getFileObject());

						panel.notifyFileSelectionListeners(fileObjs);
					} else {
						panel.notifyFileSelectionListeners(null);
					}

				}

				for (int currRow : container.getSelectedRows()) {
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) container
									.getValueAt(currRow, nodeColumn);
					nodeObjs.add(node);
				}

				panel.notifyNodeSelectionListeners(nodeObjs);

				log.debug("UGA UGA " + DebugHelper.arrayToString(nodeObjs));

				showMenu(me);
			} else if (SwingUtilities.isLeftMouseButton(me)) {
				java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();
				java.util.List<ProjectFilesTreeNode> nodeObjs = new ArrayList<ProjectFilesTreeNode>();

				for (int row : container.getSelectedRows()) {
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) container
									.getValueAt(row, nodeColumn);
					if (node.isFile()) {
						fileObjs.add(node.getFileObject());
					}
					nodeObjs.add(node);
				}
				panel.notifyFileSelectionListeners(fileObjs);
				panel.notifyNodeSelectionListeners(nodeObjs);
			}
		}

		/**
		 * Global Menu for FilesPanel.
		 * Should show anywhere on right click, supporting multiselect
		 *
		 * @param me
		 */
		private void showMenu(MouseEvent me) {
			popupMenu.show(container, (int) me.getPoint().getX(),
							(int) me.getPoint().getY());
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
		final JPanel controlPanel = new JPanel(
						new MigLayout("wrap 2, ins 2, fill, gap 0! 0!"));
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
		treeBtn.setUI(new JakeHudButtonUI());
		controlPanel.add(treeBtn, "split 2");
		treeBtn.setSelected(true);

		flatBtn = new JToggleButton(getResourceMap().getString("flatButton"));
		flatBtn.setUI(new JakeHudButtonUI());
		controlPanel.add(flatBtn);

		ActionListener updateViewAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				switchFileDisplay();
				log.debug("updating view in filepanel...");
			}
		};

		ActionListener filterViewAction = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				filterFileDisplay();
				log.debug("filtering view in filepanel...");
			}
		};

		flatBtn.addActionListener(updateViewAction);
		treeBtn.addActionListener(updateViewAction);

		ButtonGroup showGrp = new ButtonGroup();
		showGrp.add(flatBtn);
		showGrp.add(treeBtn);

		allBtn = new JToggleButton(getResourceMap().getString("filterAllButton"));
		allBtn.setUI(new JakeHudButtonUI());
		controlPanel.add(allBtn, "split 3, right");
		allBtn.setSelected(true);

		newBtn = new JToggleButton(getResourceMap().getString("filterNewButton"));
		newBtn.setUI(new GreenHudButtonUI());
		controlPanel.add(newBtn, "right");

		conflictsBtn = new JToggleButton(
						getResourceMap().getString("filterConflictsButton"));
		conflictsBtn.setUI(new RedHudButtonUI());
		controlPanel.add(conflictsBtn, "right, wrap");

		this.add(controlPanel, "growx");

		allBtn.addActionListener(filterViewAction);
		newBtn.addActionListener(filterViewAction);
		conflictsBtn.addActionListener(filterViewAction);

		ButtonGroup filterGrp = new ButtonGroup();
		filterGrp.add(allBtn);
		filterGrp.add(newBtn);
		filterGrp.add(conflictsBtn);

		// add file treetable
		fileTreeTableScrollPane = new javax.swing.JScrollPane();
		fileTreeTable = new ITunesTreeTable();

		// add file table
		fileTable = new ITunesTable();

		// Default display (first): TreeTable
		fileTreeTableScrollPane.setViewportView(fileTreeTable);

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
			TreeTableModel treeTableModel;
			TableModel tableModel;

			// TODO: lazy loading !!!
			try {
				treeTableModel = new FolderObjectsTreeTableModel(new ProjectFilesTreeNode(
								JakeMainApp.getCore().getFolder(JakeMainApp.getProject(), null)));
				fileTreeTable.setTreeTableModel(treeTableModel);
			} catch (ProjectFolderMissingException e) {
				e.printStackTrace();
			}

			tableModel = new FileObjectsTableModel(new ArrayList<FileObject>());

			fileTable.setModel(tableModel);

			// start get all files from project, async
			JakeExecutor.exec(new GetAllProjectFilesWorker(JakeMainApp.getProject()));

			// FIXME
			//setProjectFiles(JakeMainApp.getCore().getFilesDEBUG(project));
		}
	}

	public void setProjectFiles(java.util.List<FileObject> files) {
		log.info("setting project files...");
		fileTable.setModel(new FileObjectsTableModel(files));
		fileTable.updateUI();
	}
}