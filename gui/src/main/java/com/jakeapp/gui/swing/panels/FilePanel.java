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
import com.jakeapp.gui.swing.controls.cmacwidgets.*;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.models.FileObjectsTableModel;
import com.jakeapp.gui.swing.models.FolderObjectsTreeTableModel;
import com.jakeapp.gui.swing.renderer.FileStatusTreeCellRenderer;
import com.jakeapp.gui.swing.renderer.ProjectFilesTableCellRenderer;
import com.jakeapp.gui.swing.renderer.ProjectFilesTreeCellRenderer;
import com.jakeapp.gui.swing.worker.GetAllProjectFilesWorker;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.TimerTask;

/**
 * @author studpete
 */
public class FilePanel extends javax.swing.JPanel implements ProjectSelectionChanged {
	private static final Logger log = Logger.getLogger(FilePanel.class);
	private static FilePanel instance;

	private static final int FILETREETABLE_NODECOLUMN = 2;
	private static final int FILETABLE_NODECOLUMN = 0;

	private Project project;

	private java.util.List<FileSelectionChanged> fileSelectionListeners = new ArrayList<FileSelectionChanged>();
	private ResourceMap resourceMap;
	private JToggleButton treeBtn;
	private JToggleButton flatBtn;
	private JToggleButton allBtn;
	private JToggleButton newBtn;
	private JToggleButton conflictsBtn;

	private boolean treeViewActive = true;

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

	public void addFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.add(listener);
	}

	public void removeFileSelectionListener(FileSelectionChanged listener) {
		fileSelectionListeners.remove(listener);
	}

	public void notifyFileSelectionListeners(java.util.List<FileObject> objs) {
		log.debug("notify selection listeners");
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
		fileTreeTable.setDefaultRenderer(ProjectFilesStatusCell.class, new FileStatusTreeCellRenderer());
		fileTable.setDefaultRenderer(ProjectFilesTreeNode.class, new ProjectFilesTableCellRenderer());

		fileTreeTable.addMouseListener(new FileContainerMouseListener(this, fileTreeTable, FILETREETABLE_NODECOLUMN));
		fileTable.addMouseListener(new FileContainerMouseListener(this, fileTable, FILETABLE_NODECOLUMN));

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

	private class FileTreeTableKeyListener extends KeyAdapter {
		private FilePanel panel;

		public FileTreeTableKeyListener(FilePanel p) {
			this.panel = p;
		}

		@Override
		public void keyReleased(KeyEvent e) {
			java.util.List<FileObject> list = new ArrayList<FileObject>();
			for (int row : fileTreeTable.getSelectedRows()) {
				ProjectFilesTreeNode node = (ProjectFilesTreeNode) fileTreeTable.getValueAt(row, (treeViewActive ? FILETREETABLE_NODECOLUMN : FILETABLE_NODECOLUMN));
				if (node.isFile()) {
					list.add(node.getFileObject());
				}
			}
			panel.notifyFileSelectionListeners(list);
		}
	}

	private class FileContainerMouseListener extends MouseAdapter {
		private FilePanel panel;
		private JTable container;
		private int nodeColumn;

		public FileContainerMouseListener(FilePanel p, JTable fileContainer, int nodeColumn) {
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
				if (container.getSelectedRowCount() <= 1) {
					model.setSelectionInterval(rowNumber, rowNumber);
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) container.getValueAt(rowNumber, nodeColumn);
					if (node != null && node.isFile()) {
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
				for (int row : container.getSelectedRows()) {
					System.err.println("NODECOLUMN: " + nodeColumn);
					ProjectFilesTreeNode node = (ProjectFilesTreeNode) container.getValueAt(row, nodeColumn);
					if (node.isFile()) {
						list.add(node.getFileObject());
					}
				}
				panel.notifyFileSelectionListeners(list);
			}
		}

		/**
		 * Global Menu for FilesPanel.
		 * Should show anywhere on right click, supporting multiselect
		 *
		 * @param me
		 */
		private void showMenu(MouseEvent me) {
			JPopupMenu pm = new JakePopupMenu();

			ArrayList<ProjectFilesTreeNode> nodes = new ArrayList<ProjectFilesTreeNode>();

			for (int i : container.getSelectedRows()) {
				nodes.add((ProjectFilesTreeNode) container.getValueAt(i, nodeColumn));
			}

			pm.add(new JMenuItem(new OpenFileAction(nodes)));
			// TODO: show always? dynamically? (alwasy for now...while dev)
			pm.add(new JMenuItem(new ResolveConflictFileAction(nodes)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new AnnounceFileAction(nodes)));
			pm.add(new JMenuItem(new PullFileAction(nodes)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new DeleteFileAction(nodes)));
			pm.add(new JMenuItem(new RenameFileAction(nodes)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new InspectorFileAction(nodes)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new ImportFileAction(nodes)));
			pm.add(new JMenuItem(new NewFolderFileAction(nodes)));
			pm.add(new JSeparator());
			pm.add(new JMenuItem(new LockFileAction(nodes)));
			pm.add(new JMenuItem(new LockWithMessageFileAction(nodes)));


			pm.show(container, (int) me.getPoint().getX(), (int) me.getPoint()
				 .getY());
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
				switchFileDisplay();
				log.debug("updating view in filepanel...");
			}
		};

		flatBtn.addActionListener(updateViewAction);
		treeBtn.addActionListener(updateViewAction);

		ButtonGroup showGrp = new ButtonGroup();
		showGrp.add(flatBtn);
		showGrp.add(treeBtn);

		allBtn = new JToggleButton(getResourceMap().getString("filterAllButton"));
		allBtn.setUI(new HudButtonUI());
		controlPanel.add(allBtn, "split 3, right");
		allBtn.setSelected(true);

		newBtn = new JToggleButton(getResourceMap().getString("filterNewButton"));
		newBtn.setUI(new GreenHudButtonUI());
		controlPanel.add(newBtn, "right");

		conflictsBtn = new JToggleButton(getResourceMap().getString("filterConflictsButton"));
		conflictsBtn.setUI(new RedHudButtonUI());
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
				treeTableModel = new FolderObjectsTreeTableModel(new ProjectFilesTreeNode(JakeMainApp.getApp().getCore().getProjectRootFolder(JakeMainApp.getApp().getProject())));
				fileTreeTable.setTreeTableModel(treeTableModel);
			} catch (ProjectFolderMissingException e) {
				e.printStackTrace();
			}
			tableModel = new FileObjectsTableModel(new ArrayList<FileObject>());

			fileTable.setModel(tableModel);

			// start get all files from project, async
			JakeExecutor.exec(new GetAllProjectFilesWorker(JakeMainApp.getProject()));


		}
	}

	public void setProjectFiles(java.util.List<FileObject> files) {
		log.info("setting project files...");
		fileTable.setModel(new FileObjectsTableModel(files));
		fileTable.updateUI();
	}
}
