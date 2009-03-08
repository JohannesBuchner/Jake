package com.jakeapp.gui.swing.panels;

import com.explodingpixels.widgets.WindowUtils;
import com.jakeapp.core.domain.FileObject;
import com.jakeapp.gui.swing.JakeContext;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.file.*;
import com.jakeapp.gui.swing.callbacks.ContextChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.controls.cmacwidgets.GreenHudButtonUI;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTable;
import com.jakeapp.gui.swing.controls.cmacwidgets.ITunesTreeTable;
import com.jakeapp.gui.swing.controls.cmacwidgets.JakeHudButtonUI;
import com.jakeapp.gui.swing.controls.cmacwidgets.RedHudButtonUI;
import com.jakeapp.gui.swing.exceptions.FileOperationFailedException;
import com.jakeapp.gui.swing.filters.FileObjectConflictStatusFilter;
import com.jakeapp.gui.swing.filters.FileObjectDateFilter;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.models.FileTableModel;
import com.jakeapp.gui.swing.renderer.files.FileLockedTreeCellRenderer;
import com.jakeapp.gui.swing.renderer.files.FileStatusTreeCellRenderer;
import com.jakeapp.gui.swing.renderer.files.ProjectFilesTableCellRenderer;
import com.jakeapp.gui.swing.renderer.files.ProjectFilesTreeCellRenderer;
import com.jakeapp.gui.swing.xcore.EventCore;
import com.jakeapp.gui.swing.xcore.ObjectCache;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.Filter;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import javax.swing.table.TableColumn;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * @author studpete, csutter
 */
public class FilePanel extends javax.swing.JPanel
				implements ContextChanged, ProjectChanged {
	private static final long serialVersionUID = -3419475619689818265L;
	private static final Logger log = Logger.getLogger(FilePanel.class);

	private static FilePanel instance;

	// fixme: deprecated!
	public static final int FILETREETABLE_NODECOLUMN = 1;

	private ResourceMap resourceMap;
	private JToggleButton treeBtn;
	private JToggleButton flatBtn;
	private JToggleButton allBtn;
	private JToggleButton newBtn;
	private JToggleButton conflictsBtn;

	private org.jdesktop.swingx.JXTreeTable fileTreeTable;
	private org.jdesktop.swingx.JXTable fileTable;
	private javax.swing.JScrollPane fileTreeTableScrollPane;	

	// use tree (true) or flat (false)
	// TODO: set to false for debug only
	private boolean treeViewActive = false;

	private final JPopupMenu popupMenu = new JakePopupMenu();

	/**
	 * Creates new form FilePanel
	 */
	public FilePanel() {
		// save for instance access
		instance = this;

		// init resource map
		setResourceMap(org.jdesktop.application.Application
						.getInstance(JakeMainApp.class)
						.getContext().getResourceMap(FilePanel.class));

		initComponents();

		EventCore.get().addContextChangedListener(this);

		this.fileTreeTable.setScrollsOnExpand(true);
		this.fileTreeTable.setSortable(true);
		this.fileTreeTable.setColumnControlVisible(true);

		// ETreeTable performs its own striping on the mac
		if (!Platform.isMac()) {
			this.fileTreeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
			this.fileTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}

		this.fileTreeTable.setTreeCellRenderer(new ProjectFilesTreeCellRenderer());
		this.fileTable.setDefaultRenderer(ProjectFilesTreeNode.class,
						new ProjectFilesTableCellRenderer());

		this.fileTreeTable.setDefaultRenderer(FileObjectStatusCell.class,
						new FileStatusTreeCellRenderer());
		this.fileTreeTable.setDefaultRenderer(FileObjectLockedCell.class,
						new FileLockedTreeCellRenderer());

		this.fileTable.setDefaultRenderer(FileObjectStatusCell.class,
						new FileStatusTreeCellRenderer());
		this.fileTable.setDefaultRenderer(FileObjectLockedCell.class,
						new FileLockedTreeCellRenderer());

		this.fileTreeTable.addMouseListener(new FileContainerMouseListener(this,
						fileTreeTable,
						FILETREETABLE_NODECOLUMN));
		this.fileTable.addMouseListener(new FileContainerMouseListener(this,
						fileTable,
						FileTableModel.Columns.Name.ordinal()));

		this.fileTreeTable.addKeyListener(new FileTreeTableKeyListener(this));

		// Initialize popup menu
		initPopupMenu(this.popupMenu);

		// TODO: remove later (hack to disable tree)
		this.fileTreeTableScrollPane.setViewportView(this.fileTable);
	}

	public static FilePanel getInstance() {
		return instance;
	}

	public ResourceMap getResourceMap() {
		return this.resourceMap;
	}

	public void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	/**
	 * Displays files as a file/folder tree or list of relative paths (classic Jake ;-)
	 */
	public void switchFileDisplay() {
		if (this.flatBtn.isSelected()) {
			this.fileTreeTableScrollPane.setViewportView(this.fileTable);
			this.treeViewActive = false;
			resetFilter();
		} else {
			// We don't need this anymore because resetFilter() does it for us
			// fileTreeTableScrollPane.setViewportView(fileTreeTable);
			this.treeViewActive = true;
			resetFilter();
		}
	}

	public void filterFileDisplay() {
		if (this.conflictsBtn.isSelected()) {
			Filter filter = new FileObjectConflictStatusFilter();
			switchToFlatAndFilter(new FilterPipeline(filter));
		} else if (this.newBtn.isSelected()) {
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
		this.fileTreeTableScrollPane.setViewportView(this.fileTable);
		this.fileTable.setFilters(pipeline);
		this.flatBtn.setSelected(true);
	}

	/**
	 * Resets all applied filters
	 */
	public void resetFilter() {
		this.fileTable.setFilters(null);

		// TODO: reenable later
		/*
		if (treeViewActive) {
			fileTreeTableScrollPane.setViewportView(fileTreeTable);
			treeBtn.setSelected(true);
		}
		*/
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


	public List<ProjectFilesTreeNode> getSelectedNodes() {
		java.util.List<ProjectFilesTreeNode> nodeObjs =
						new ArrayList<ProjectFilesTreeNode>();
		for (int row : this.fileTreeTable.getSelectedRows()) {
			ProjectFilesTreeNode node = (ProjectFilesTreeNode) this.fileTreeTable
							.getValueAt(row,
											(this.treeViewActive ? FILETREETABLE_NODECOLUMN :
															FileTableModel.Columns.FState.ordinal()));
			nodeObjs.add(node);
		}

		return nodeObjs;
	}

	@Override
	public void projectChanged(ProjectChangedEvent ev) {
		this.updatePanel();
	}

	@Override public void contextChanged(EnumSet<Reason> reason, Object context) {
		updatePanel();
	}


	private class FileTreeTableKeyListener extends KeyAdapter {
		private FilePanel panel;

		public FileTreeTableKeyListener(FilePanel p) {
			this.panel = p;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();
			java.util.List<ProjectFilesTreeNode> nodeObjs =
							new ArrayList<ProjectFilesTreeNode>();
			for (int row : fileTreeTable.getSelectedRows()) {
				ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable.getValueAt(
								row,
								(treeViewActive ? FILETREETABLE_NODECOLUMN : FileTableModel.Columns.FState.ordinal()));
				if (node.isFile()) {
					fileObjs.add(node.getFileObject());
				}
				nodeObjs.add(node);
			}

			EventCore.get().notifyFileSelectionListeners(fileObjs);
			EventCore.get().notifyNodeSelectionListeners(nodeObjs);
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


				java.util.List<ProjectFilesTreeNode> nodeObjs =
								new ArrayList<ProjectFilesTreeNode>();

				log.debug("Selected rows: " + DebugHelper
								.arrayToString(container.getSelectedRows()));

				if (container.getSelectedRowCount() <= 1) {
					model.setSelectionInterval(rowNumber, rowNumber);
					ProjectFilesTreeNode node =
									(ProjectFilesTreeNode) container.getValueAt(rowNumber, nodeColumn);

					if (node != null && node.isFile()) {
						java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();

						fileObjs.add(node.getFileObject());

						EventCore.get().notifyFileSelectionListeners(fileObjs);
					} else {
						EventCore.get().notifyFileSelectionListeners(null);
					}

				}

				for (int currRow : container.getSelectedRows()) {
					ProjectFilesTreeNode node =
									(ProjectFilesTreeNode) container.getValueAt(currRow, nodeColumn);
					nodeObjs.add(node);
				}

				EventCore.get().notifyNodeSelectionListeners(nodeObjs);

				log.debug("UGA UGA " + DebugHelper.arrayToString(nodeObjs));

				showMenu(me);
			} else if (SwingUtilities.isLeftMouseButton(me)) {
				java.util.List<FileObject> fileObjs = new ArrayList<FileObject>();
				java.util.List<ProjectFilesTreeNode> nodeObjs =
								new ArrayList<ProjectFilesTreeNode>();

				for (int row : container.getSelectedRows()) {
					ProjectFilesTreeNode node =
									(ProjectFilesTreeNode) container.getValueAt(row, nodeColumn);
					if (node.isFile()) {
						fileObjs.add(node.getFileObject());
					}
					nodeObjs.add(node);
				}
				EventCore.get().notifyFileSelectionListeners(fileObjs);
				EventCore.get().notifyNodeSelectionListeners(nodeObjs);

				if (me.getClickCount() == 2 && fileObjs.size() == 1) {
					try {
						FileUtilities.launchFile(JakeMainApp.getCore().getFile(fileObjs.get(0)));
					} catch (FileOperationFailedException e) {
						ExceptionUtilities.showError("Unable to open File", e);
					}
				}
			}
		}

		/**
		 * Global Menu for FilesPanel.
		 * Should show anywhere on right click, supporting multiselect
		 *
		 * @param me
		 */
		private void showMenu(MouseEvent me) {
			popupMenu.show(container,
							(int) me.getPoint().getX(),
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
		final JPanel controlPanel =
						new JPanel(new MigLayout("wrap 2, ins 2, fill, gap 0! 0!"));
		controlPanel.setBackground(Platform.getStyler().getFilterPaneColor(true));
		controlPanel.setSize(400, 25);

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


		// TODO: remove hack
		this.treeBtn = new JToggleButton(getResourceMap().getString("treeButton"));
		this.treeBtn.setUI(new JakeHudButtonUI());
		//controlPanel.add(treeBtn, "split 2");
		//treeBtn.setSelected(true);

		this.flatBtn = new JToggleButton(getResourceMap().getString("flatButton"));
		this.flatBtn.setUI(new JakeHudButtonUI());
		//controlPanel.add(this.flatBtn);
		this.flatBtn.setSelected(true);

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

		this.flatBtn.addActionListener(updateViewAction);
		this.treeBtn.addActionListener(updateViewAction);

		ButtonGroup showGrp = new ButtonGroup();
		showGrp.add(this.flatBtn);
		showGrp.add(this.treeBtn);

		this.allBtn = new JToggleButton(getResourceMap().getString("filterAllButton"));
		this.allBtn.setUI(new JakeHudButtonUI());
		controlPanel.add(this.allBtn, "split 3, right");
		this.allBtn.setSelected(true);

		this.newBtn = new JToggleButton(getResourceMap().getString("filterNewButton"));
		this.newBtn.setUI(new GreenHudButtonUI());
		controlPanel.add(this.newBtn, "right");

		this.conflictsBtn =
						new JToggleButton(getResourceMap().getString("filterConflictsButton"));
		this.conflictsBtn.setUI(new RedHudButtonUI());
		controlPanel.add(this.conflictsBtn, "right, wrap");

		this.add(controlPanel, "growx");

		this.allBtn.addActionListener(filterViewAction);
		this.newBtn.addActionListener(filterViewAction);
		this.conflictsBtn.addActionListener(filterViewAction);

		ButtonGroup filterGrp = new ButtonGroup();
		filterGrp.add(this.allBtn);
		filterGrp.add(this.newBtn);
		filterGrp.add(this.conflictsBtn);

		// add file treetable
		this.fileTreeTableScrollPane = new javax.swing.JScrollPane();
		this.fileTreeTable = new ITunesTreeTable();

		// add file table
		this.fileTable = new ITunesTable();
		this.fileTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		this.fileTable.setModel(new FileTableModel());

		int vColIndex = 2;
		TableColumn col = this.fileTable.getColumnModel().getColumn(vColIndex);
		int width = 100;
		col.setPreferredWidth(width);

		// Default display (first): TreeTable
		this.fileTreeTableScrollPane.setViewportView(this.fileTreeTable);

		this.add(this.fileTreeTableScrollPane, "grow, push");
	}

	private void updatePanel() {
		// don't update if project is null OR an invitation.
		if (JakeContext.getProject() == null) {
			return;
		}

		if (JakeContext.getProject() != null) {
			ObjectCache.get().updateFiles(JakeContext.getProject());
		}
	}
}