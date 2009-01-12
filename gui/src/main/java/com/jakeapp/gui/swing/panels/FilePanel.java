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

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.ETreeTable;
import com.jakeapp.gui.swing.controls.ProjectFilesTreeCellRenderer;
import com.jakeapp.gui.swing.exceptions.ProjectFolderMissingException;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.ProjectFilesTreeNode;
import com.jakeapp.gui.swing.models.FolderObjectsTreeTableModel;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.decorator.HighlighterFactory;
import org.jdesktop.swingx.treetable.TreeTableModel;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;

/**
 * @author studpete
 */
public class FilePanel extends javax.swing.JPanel implements ProjectSelectionChanged {
	private static final Logger log = Logger.getLogger(FilePanel.class);
	private static FilePanel instance;

	private PopupMenu fileMenu;

	private Project project;

	private java.util.List<FileSelectionChanged> fileSelectionListeners = new ArrayList<FileSelectionChanged>();

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
		instance = this;
		initComponents();

		JakeMainApp.getApp().addProjectSelectionChangedListener(this);

		infoPanel.setBackgroundPainter(Platform.getStyler().getContentPanelBackgroundPainter());

		// make the buttons more fancy
		Platform.getStyler().MakeWhiteRecessedButton(newFilesButton);
		Platform.getStyler().MakeWhiteRecessedButton(resolveButton);
		Platform.getStyler().MakeWhiteRecessedButton(illegalFilenamesButton);

		// TODO: Fix this. Essentially, the problem is that we need to set some sort
		// of model before we have a project, so this creates a new model of the root
		// point in the FS. Maybe replace this by a dummy model or find a nicer way of
		// injection.
		// TreeTableModel treeTableModel = new FileSystemModel();
		// fileTreeTable.setTreeTableModel(treeTableModel);

		fileTreeTable.setScrollsOnExpand(true);
		fileTreeTable.setSortable(true);
		fileTreeTable.setColumnControlVisible(true);

		// ETreeTable performs its own striping on the mac
		// TODO: make this more beautiful...
		if (!Platform.isMac()) {
			fileTreeTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		}

		fileTreeTable.setTreeCellRenderer(new ProjectFilesTreeCellRenderer());
		// fileTreeTable.setDefaultEditor(TagSet.class, new FilesTreeTableTagCellEditor());
		// WHY THE FUCK IS THIS NOT WORKING?!!!!!
		// fileTreeTable.setDefaultRenderer(TagSet.class, new TagSetRenderer());

		fileTreeTable.addMouseListener(new FileTreeTableMouseListener(this));
	}

	public static FilePanel getInstance() {
		return instance;
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
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
	// <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
	private void initComponents() {

		jScrollPane1 = new javax.swing.JScrollPane();
		fileTreeTable = new ETreeTable();
		infoPanel = new org.jdesktop.swingx.JXPanel();
		jLabel1 = new javax.swing.JLabel();
		newFilesButton = new javax.swing.JButton();
		resolveButton = new javax.swing.JButton();
		illegalFilenamesButton = new javax.swing.JButton();

		setName("Form"); // NOI18N
		setLayout(new java.awt.BorderLayout());

		jScrollPane1.setName("jScrollPane1"); // NOI18N

		fileTreeTable.setName("fileTreeTable"); // NOI18N
		jScrollPane1.setViewportView(fileTreeTable);

		add(jScrollPane1, java.awt.BorderLayout.CENTER);

		org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(FilePanel.class);
		infoPanel.setBackground(resourceMap.getColor("infoPanel.background")); // NOI18N
		infoPanel.setName("infoPanel"); // NOI18N

		jLabel1.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
		jLabel1.setForeground(resourceMap.getColor("jLabel1.foreground")); // NOI18N
		jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
		jLabel1.setName("jLabel1"); // NOI18N

		newFilesButton.setText(resourceMap.getString("newFilesButton.text")); // NOI18N
		newFilesButton.setName("newFilesButton"); // NOI18N
		newFilesButton.setSelected(true);

		resolveButton.setText(resourceMap.getString("resolveButton.text")); // NOI18N
		resolveButton.setName("resolveButton"); // NOI18N

		illegalFilenamesButton.setText(resourceMap.getString("illegalFilenamesButton.text")); // NOI18N
		illegalFilenamesButton.setName("illegalFilenamesButton"); // NOI18N

		javax.swing.GroupLayout infoPanelLayout = new javax.swing.GroupLayout(infoPanel);
		infoPanel.setLayout(infoPanelLayout);
		infoPanelLayout.setHorizontalGroup(
				  infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							 .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, infoPanelLayout.createSequentialGroup()
							 .addContainerGap()
							 .addComponent(jLabel1)
							 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 299, Short.MAX_VALUE)
							 .addComponent(resolveButton, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
							 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							 .addComponent(illegalFilenamesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
							 .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
							 .addComponent(newFilesButton, javax.swing.GroupLayout.PREFERRED_SIZE, 102, javax.swing.GroupLayout.PREFERRED_SIZE)
							 .addContainerGap())
		);
		infoPanelLayout.setVerticalGroup(
				  infoPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
							 .addGroup(infoPanelLayout.createSequentialGroup()
										.addGap(8, 8, 8)
										.addComponent(jLabel1))
							 .addComponent(newFilesButton)
							 .addComponent(illegalFilenamesButton)
							 .addComponent(resolveButton)
		);

		add(infoPanel, java.awt.BorderLayout.PAGE_START);
	}// </editor-fold>//GEN-END:initComponents

	// Variables declaration - do not modify//GEN-BEGIN:variables
	private org.jdesktop.swingx.JXTreeTable fileTreeTable;
	private javax.swing.JButton illegalFilenamesButton;
	private org.jdesktop.swingx.JXPanel infoPanel;
	private javax.swing.JLabel jLabel1;
	private javax.swing.JScrollPane jScrollPane1;
	private javax.swing.JButton newFilesButton;
	private javax.swing.JButton resolveButton;
	// End of variables declaration//GEN-END:variables

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;

		// we have to cope with no project selected state.
		if (project != null) {
			TreeTableModel treeTableModel = null;
			try {
				treeTableModel = new FolderObjectsTreeTableModel(new ProjectFilesTreeNode(JakeMainApp.getApp().getCore().getProjectRootFolder(JakeMainApp.getApp().getProject())));
			} catch (ProjectFolderMissingException e) {
				log.warn("Project folder missing!!");
			}
			fileTreeTable.setTreeTableModel(treeTableModel);
		}
	}
}
