/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * NewsPanel.java
 *
 * Created on Dec 3, 2008, 2:00:25 AM
 */

package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.actions.*;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.ETable;
import com.jakeapp.gui.swing.controls.JListMutable;
import com.jakeapp.gui.swing.controls.PeopleListCellEditor;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.JakeMainHelper;
import com.jakeapp.gui.swing.helpers.JakePopupMenu;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.models.EventsTableModel;
import com.jakeapp.gui.swing.models.PeopleListModel;
import com.jakeapp.gui.swing.renderer.EventCellRenderer;
import com.jakeapp.gui.swing.renderer.PeopleListCellRenderer;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.IOException;

/**
 * @author studpete
 */
public class NewsPanel extends javax.swing.JPanel implements ProjectSelectionChanged, ProjectChanged {
    private static final Logger log = Logger.getLogger(NewsPanel.class);
    private Project project;
    private ResourceMap resourceMap;
    private Icon startIcon;
    private Icon stopIcon;
    private Icon invalidIcon;
    private StartStopProjectAction startStopProjectAction = new StartStopProjectAction();

    /**
     * Creates new form NewsPanel
     */
    public NewsPanel() {
        initComponents();
        setResourceMap(org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NewsPanel.class));

        // register the callbacks
        JakeMainApp.getApp().addProjectSelectionChangedListener(this);
        JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);

        // init actions!
        projectRunningButton.setAction(startStopProjectAction);

        // ensure opaque(=draw background) is false (default on mac, not default on win/lin)
        autoUploadCB.setOpaque(false);
        autoDownloadCB.setOpaque(false);

        // set the background painter
        newsContentPanel.setBackgroundPainter(Platform.getStyler().getContentPanelBackgroundPainter());

        startIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/folder-open.png")));
        stopIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/folder.png")));
        invalidIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(
                getClass().getResource("/icons/folder_invalid.png")));

        autoDownloadCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                getProject().setAutoPullEnabled(autoDownloadCB.isSelected());
            }
        });

        autoUploadCB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent actionEvent) {
                getProject().setAutoAnnounceEnabled(autoUploadCB.isSelected());
            }
        });

        // configure the people list
        peopleList.setHighlighters(HighlighterFactory.createSimpleStriping());
        peopleList.setModel(new PeopleListModel());
        peopleList.setCellRenderer(new PeopleListCellRenderer());
        ((JListMutable) peopleList).setListCellEditor(new PeopleListCellEditor(new JTextField()));

        peopleList.addMouseListener(new PeopleListMouseListener());

        // config the recent events table
        eventsTable.setModel(new EventsTableModel());
        eventsTable.getColumn(0).setCellRenderer(new EventCellRenderer());
        eventsTable.setSortable(false);
        eventsTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        //eventsTable.setBorder(BorderFactory.createEtchedBorder());
        eventsTable.setColumnControlVisible(false);
        eventsTable.setEditable(false);
        eventsTable.setDoubleBuffered(true);
        eventsTable.setRolloverEnabled(true);
        eventsTable.addMouseListener(new EventsTableMouseListener());
    }

    /**
     * private inner mouselistener for events table.
     */
    private class EventsTableMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent mouseEvent) {
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

    private class PeopleListMouseListener implements MouseListener {

        @Override
        public void mouseClicked(MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me)) {
                log.debug("right clicked");
                // get the coordinates of the mouse click
                Point p = me.getPoint();

                // get the row index that contains that coordinate
                int rowNumber = peopleList.locationToIndex(p);

                // Get the ListSelectionModel of the JTable
                ListSelectionModel model = peopleList.getSelectionModel();

                // set the selected interval of rows. Using the "rowNumber"
                // variable for the beginning and end selects only that one
                // row.
                // ONLY select new item if we didn't select multiple items.
                if (peopleList.getSelectedValues().length <= 1) {
                    model.setSelectionInterval(rowNumber, rowNumber);
                }

                showMenu(me);
            }
        }

        private void showMenu(MouseEvent me) {
            log.info("triggered popup event");

            JPopupMenu pm = new JakePopupMenu();

            pm.add(new JMenuItem(new InvitePeopleAction(true)));
            pm.add(new JSeparator());
            pm.add(new JMenuItem(new RenamePeopleAction((JListMutable) peopleList)));
            pm.add(new JSeparator());
            pm.add(new JCheckBoxMenuItem(new TrustFullPeopleAction(peopleList)));
            pm.add(new JCheckBoxMenuItem(new TrustPeopleAction(peopleList)));
            pm.add(new JCheckBoxMenuItem(new TrustNoPeopleAction(peopleList)));

            pm.show(peopleList, (int) me.getPoint().getX(), (int) me.getPoint()
                    .getY());
        }

        @Override
        public void mousePressed(MouseEvent mouseEvent) {
            log.info("mousePressed");
            //showMenu(mouseEvent);
        }

        @Override
        public void mouseReleased(MouseEvent mouseEvent) {
            log.info("mouseReleased");
            //showMenu(mouseEvent);
        }

        @Override
        public void mouseEntered(MouseEvent mouseEvent) {
        }

        @Override
        public void mouseExited(MouseEvent mouseEvent) {
        }
    }


    /**
     * Update the news panel.
     */
    private void updatePanel() {
        log.info("updating panel with " + getProject());

        // TODO: remove hack
        if (getProject() == null) {
            return;
        }

        // TODO: Get rid of this ugly hack and everything related to it
        if (!FileUtilities.hasValidRootPath(getProject())) {
            log.warn("Project root path " + getProject().getRootPath() + " is invalid.");
            projectStatusLabel.setText("ERROR: Project folder does not exist");
            projectStatusLabel.setForeground(Color.RED);
            projectIconLabel.setIcon(invalidIcon);

            return;
        } else {
            projectStatusLabel.setForeground(Color.BLACK);
        }

        // update all text in panel
        projectLabel.setText(getProject().getName());
        projectFolderHyperlink.setText(getProject().getRootPath());
        projectStatusLabel.setText(JakeMainHelper.printProjectStatus(getProject()));
        autoDownloadCB.setSelected(getProject().isAutoPullEnabled());
        autoUploadCB.setSelected(getProject().isAutoAnnounceEnabled());

        // update the checkboxes
        autoDownloadCB.setSelected(getProject().isAutoPullEnabled());
        autoUploadCB.setSelected(getProject().isAutoAnnounceEnabled());

        // update the icon (start/stop-state)
        projectIconLabel.setIcon(getProject().isStarted() ? startIcon : stopIcon);

        // update the event table

        // update the people table
        //peopleList.add
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

        newsContentPanel = new org.jdesktop.swingx.JXPanel();
        titlePanel = new javax.swing.JPanel();
        projectFolderHyperlink = new org.jdesktop.swingx.JXHyperlink();
        projectStatusLabel = new javax.swing.JLabel();
        projectRunningButton = new javax.swing.JButton();
        projectIconLabel = new javax.swing.JLabel();
        projectTitlePanel = new javax.swing.JPanel();
        projectLabel = new javax.swing.JLabel();
        actionPanel = new javax.swing.JPanel();
        eventsLabel = new javax.swing.JLabel();
        eventsScrollPanel = new javax.swing.JScrollPane();
        eventsTable = new ETable();
        peopleScrollPanel = new javax.swing.JScrollPane();
        peopleList = new JListMutable();
        peopleLabel = new javax.swing.JLabel();
        optionsPanel = new javax.swing.JPanel();
        autoUploadCB = new javax.swing.JCheckBox();
        optionsLabel = new javax.swing.JLabel();
        autoDownloadCB = new javax.swing.JCheckBox();

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(com.jakeapp.gui.swing.JakeMainApp.class).getContext().getResourceMap(NewsPanel.class);
        setBackground(resourceMap.getColor("Form.background")); // NOI18N
        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());

        newsContentPanel.setBackground(resourceMap.getColor("newsContentPanel.background")); // NOI18N
        newsContentPanel.setName("newsContentPanel"); // NOI18N
        newsContentPanel.setLayout(new javax.swing.BoxLayout(newsContentPanel, javax.swing.BoxLayout.Y_AXIS));

        titlePanel.setMaximumSize(new java.awt.Dimension(32767, 120));
        titlePanel.setMinimumSize(new java.awt.Dimension(389, 120));
        titlePanel.setName("titlePanel"); // NOI18N
        titlePanel.setOpaque(false);
        titlePanel.setPreferredSize(new java.awt.Dimension(389, 120));

        projectFolderHyperlink.setText(resourceMap.getString("projectFolderHyperlink.text")); // NOI18N
        projectFolderHyperlink.setFocusable(false);
        projectFolderHyperlink.setName("projectFolderHyperlink"); // NOI18N
        projectFolderHyperlink.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                projectFolderHyperlinkActionPerformed(evt);
            }
        });

        projectStatusLabel.setText(resourceMap.getString("projectStatusLabel.text")); // NOI18N
        projectStatusLabel.setName("projectStatusLabel"); // NOI18N

        projectRunningButton.setText(resourceMap.getString("projectRunningButton.text")); // NOI18N
        projectRunningButton.setName("projectRunningButton"); // NOI18N

        projectIconLabel.setIcon(resourceMap.getIcon("projectIconLabel.icon")); // NOI18N
        projectIconLabel.setName("projectIconLabel"); // NOI18N

        projectTitlePanel.setName("projectTitlePanel"); // NOI18N
        projectTitlePanel.setOpaque(false);
        projectTitlePanel.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.LEFT, 5, 0));

        projectLabel.setFont(resourceMap.getFont("projectLabel.font")); // NOI18N
        projectLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        projectLabel.setText(resourceMap.getString("projectLabel.text")); // NOI18N
        projectLabel.setName("projectLabel"); // NOI18N

        javax.swing.GroupLayout titlePanelLayout = new javax.swing.GroupLayout(titlePanel);
        titlePanel.setLayout(titlePanelLayout);
        titlePanelLayout.setHorizontalGroup(
                titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(titlePanelLayout.createSequentialGroup()
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(titlePanelLayout.createSequentialGroup()
                                        .addContainerGap()
                                        .addComponent(projectRunningButton))
                                .addGroup(titlePanelLayout.createSequentialGroup()
                                .addGap(28, 28, 28)
                                .addComponent(projectIconLabel)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(projectTitlePanel, javax.swing.GroupLayout.DEFAULT_SIZE, 515, Short.MAX_VALUE)
                                .addComponent(projectFolderHyperlink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(projectLabel)
                                .addComponent(projectStatusLabel))
                        .addContainerGap())
        );
        titlePanelLayout.setVerticalGroup(
                titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(titlePanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addGroup(titlePanelLayout.createSequentialGroup()
                                        .addComponent(projectIconLabel)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                .addGroup(titlePanelLayout.createSequentialGroup()
                                .addComponent(projectLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(projectTitlePanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(projectFolderHyperlink, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(11, 11, 11)))
                        .addGroup(titlePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(projectRunningButton)
                                .addComponent(projectStatusLabel))
                        .addGap(30, 30, 30))
        );

        newsContentPanel.add(titlePanel);

        actionPanel.setName("actionPanel"); // NOI18N
        actionPanel.setOpaque(false);

        eventsLabel.setFont(resourceMap.getFont("eventsLabel.font")); // NOI18N
        eventsLabel.setForeground(resourceMap.getColor("eventsLabel.foreground")); // NOI18N
        eventsLabel.setText(resourceMap.getString("eventsLabel.text")); // NOI18N
        eventsLabel.setName("eventsLabel"); // NOI18N

        eventsScrollPanel.setName("eventsScrollPanel"); // NOI18N

        eventsTable.setModel(new javax.swing.table.DefaultTableModel(
                new Object[][]{
                        {"Uebersicht.odt was created", "Simon", "Yesterday"},
                        {null, null, null},
                        {null, null, null},
                        {null, null, null}
                },
                new String[]{
                        "Event", "Person", "Date"
                }
        ) {
            boolean[] canEdit = new boolean[]{
                    false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit[columnIndex];
            }
        });
        eventsTable.setName("eventsTable"); // NOI18N
        eventsScrollPanel.setViewportView(eventsTable);

        peopleScrollPanel.setName("peopleScrollPanel"); // NOI18N

        peopleList.setModel(new javax.swing.AbstractListModel() {
            String[] strings = {"<html><b><font color=green>Peter Steinberger</font></b><br>&nbsp;&nbsp;Online<br></html>", "<html><b><font color=blue>Dominik</font></b><br>&nbsp;&nbsp;Offline<br></html>"};

            public int getSize() {
                return strings.length;
            }

            public Object getElementAt(int i) {
                return strings[i];
            }
        });
        peopleList.setDoubleBuffered(true);
        peopleList.setDragEnabled(true);
        peopleList.setName("peopleList"); // NOI18N
        peopleList.setRolloverEnabled(true);
        peopleScrollPanel.setViewportView(peopleList);

        peopleLabel.setFont(resourceMap.getFont("peopleLabel.font")); // NOI18N
        peopleLabel.setForeground(resourceMap.getColor("peopleLabel.foreground")); // NOI18N
        peopleLabel.setText(resourceMap.getString("peopleLabel.text")); // NOI18N
        peopleLabel.setName("peopleLabel"); // NOI18N

        javax.swing.GroupLayout actionPanelLayout = new javax.swing.GroupLayout(actionPanel);
        actionPanel.setLayout(actionPanelLayout);
        actionPanelLayout.setHorizontalGroup(
                actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(actionPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(eventsLabel)
                                .addComponent(eventsScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 401, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(peopleLabel)
                                .addComponent(peopleScrollPanel, javax.swing.GroupLayout.PREFERRED_SIZE, 184, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addContainerGap())
        );
        actionPanelLayout.setVerticalGroup(
                actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, actionPanelLayout.createSequentialGroup()
                        .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(eventsLabel)
                                .addComponent(peopleLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(actionPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(eventsScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)
                        .addComponent(peopleScrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 262, Short.MAX_VALUE)))
        );

        newsContentPanel.add(actionPanel);

        optionsPanel.setMaximumSize(new java.awt.Dimension(32767, 100));
        optionsPanel.setMinimumSize(new java.awt.Dimension(0, 100));
        optionsPanel.setName("optionsPanel"); // NOI18N
        optionsPanel.setOpaque(false);
        optionsPanel.setPreferredSize(new java.awt.Dimension(697, 100));

        autoUploadCB.setSelected(true);
        autoUploadCB.setText(resourceMap.getString("autoUploadCB.text")); // NOI18N
        autoUploadCB.setName("autoUploadCB"); // NOI18N

        optionsLabel.setFont(resourceMap.getFont("optionsLabel.font")); // NOI18N
        optionsLabel.setForeground(resourceMap.getColor("optionsLabel.foreground")); // NOI18N
        optionsLabel.setText(resourceMap.getString("optionsLabel.text")); // NOI18N
        optionsLabel.setName("optionsLabel"); // NOI18N

        autoDownloadCB.setSelected(true);
        autoDownloadCB.setText(resourceMap.getString("autoDownloadCB.text")); // NOI18N
        autoDownloadCB.setName("autoDownloadCB"); // NOI18N

        javax.swing.GroupLayout optionsPanelLayout = new javax.swing.GroupLayout(optionsPanel);
        optionsPanel.setLayout(optionsPanelLayout);
        optionsPanelLayout.setHorizontalGroup(
                optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(optionsPanelLayout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(optionsPanelLayout.createSequentialGroup()
                                        .addComponent(optionsLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 263, Short.MAX_VALUE)
                                        .addGap(168, 168, 168))
                                .addGroup(optionsPanelLayout.createSequentialGroup()
                                .addGap(21, 21, 21)
                                .addGroup(optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(autoUploadCB, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(autoDownloadCB, javax.swing.GroupLayout.Alignment.LEADING))))
                        .addGap(441, 441, 441))
        );
        optionsPanelLayout.setVerticalGroup(
                optionsPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, optionsPanelLayout.createSequentialGroup()
                        .addContainerGap(20, Short.MAX_VALUE)
                        .addComponent(optionsLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoDownloadCB)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoUploadCB)
                        .addContainerGap())
        );

        newsContentPanel.add(optionsPanel);

        add(newsContentPanel, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void projectFolderHyperlinkActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_projectFolderHyperlinkActionPerformed
        log.info("Opening the Folder: " + getProject().getRootPath());
        try {
            Desktop.getDesktop().open(new File(getProject().getRootPath()));
        } catch (IOException e) {
            log.warn("Unable to open folder: " + getProject().getRootPath(), e);
        }
    }//GEN-LAST:event_projectFolderHyperlinkActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel actionPanel;
    private javax.swing.JCheckBox autoDownloadCB;
    private javax.swing.JCheckBox autoUploadCB;
    private javax.swing.JLabel eventsLabel;
    private javax.swing.JScrollPane eventsScrollPanel;
    private org.jdesktop.swingx.JXTable eventsTable;
    private org.jdesktop.swingx.JXPanel newsContentPanel;
    private javax.swing.JLabel optionsLabel;
    private javax.swing.JPanel optionsPanel;
    private javax.swing.JLabel peopleLabel;
    private org.jdesktop.swingx.JXList peopleList;
    private javax.swing.JScrollPane peopleScrollPanel;
    private org.jdesktop.swingx.JXHyperlink projectFolderHyperlink;
    private javax.swing.JLabel projectIconLabel;
    private javax.swing.JLabel projectLabel;
    private javax.swing.JButton projectRunningButton;
    private javax.swing.JLabel projectStatusLabel;
    private javax.swing.JPanel projectTitlePanel;
    private javax.swing.JPanel titlePanel;
    // End of variables declaration//GEN-END:variables

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;

        // relay to actions
        startStopProjectAction.setProject(getProject());

        updatePanel();
    }

    public ResourceMap getResourceMap() {
        return resourceMap;
    }

    public void setResourceMap(ResourceMap resourceMap) {
        this.resourceMap = resourceMap;
    }

    public void projectChanged(ProjectChangedEvent ev) {
        updatePanel();
    }

}
