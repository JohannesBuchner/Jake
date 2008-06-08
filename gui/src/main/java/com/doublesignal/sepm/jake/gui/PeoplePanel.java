package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.TableColumnModel;


import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;

@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeoplePanel extends JPanel {
	private static Logger log = Logger.getLogger(PeoplePanel.class);
	private final JakeGui jakeGui;
	private PeopleTableModel peopleTableModel;

	int tabindex = 0;
	
	public PeoplePanel(JakeGui gui) {
		log.info("Initializing PeoplePanel.");
		this.jakeGui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();
		initComponents();	
		initPopupMenu();
		
		
		jakeGui.getMainTabbedPane()
		.addTab("peopletab", new ImageIcon(
				getClass().getResource("/icons/people.png")),
				this);
	}

	public String getTitle() {
		return "People (" + peopleTableModel.getOnlineMembersCount()+"/"+peopleTableModel.getMembersCount()  + ")";
	}

	public void updateUi() {
		log.info("Updating people Panel...");
		peopleTableModel.updateData();
		if (tabindex >= 0)
			jakeGui.getMainTabbedPane().setTitleAt(tabindex,
					"People (" + peopleTableModel.getMembersCount()
                    + "/" + FilesLib.getHumanReadableFileSize(peopleTableModel.getOnlineMembersCount()) + ")"	
			);
	}

	public void initComponents() {
		peopleTable = new JXTable();
		peopleScrollPane = new JScrollPane();
		peoplePopupMenu = new JPopupMenu();
		

		this.setLayout(new BorderLayout());
		peopleTableModel = new PeopleTableModel(jakeGuiAccess);
		peopleTable.setComponentPopupMenu(peoplePopupMenu);
		peopleTable.setColumnControlVisible(true);
		peopleTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		peopleTable.setModel(peopleTableModel);
		peopleTable.setRolloverEnabled(false);
		
		peopleTable.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent event) {
                if (event.getClickCount() == 2
                        && SwingUtilities.isLeftMouseButton(event)
                        && isPersonSelected()) {
                    openExecutFileMenuItemActionEvent(null);
                }
            }
        }
        );
		
		TableColumnModel cm = peopleTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(70);
	    cm.getColumn(1).setPreferredWidth(70);
	    cm.getColumn(2).setPreferredWidth(50);
	    
	    peopleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        peopleTable.setPreferredScrollableViewportSize(new Dimension(450, 379));
        peopleScrollPane.setViewportView(peopleTable);
        
        this.add(peopleScrollPane, BorderLayout.CENTER);
        
	}
	
	private void initPopupMenu() {
	
		
		sendMessageMenuItem = new JMenuItem();
		showInfoPeopleMenuItem = new JMenuItem();
		renamePeopleMenuItem = new JMenuItem();
		changeUserIdMenuItem = new JMenuItem();
		removePeopleMenuItem = new JMenuItem();

		// ---- sendMessageMenuItem ----
		sendMessageMenuItem.setText("Send Message...");
		sendMessageMenuItem.setIcon(new ImageIcon(getClass().getResource(
				"/icons/message-new.png")));
		sendMessageMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessageMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(sendMessageMenuItem);
		
		// ---- showInfoPeopleMenuItem ----
		showInfoPeopleMenuItem.setText("Show Info/Comments...");
		showInfoPeopleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfoPeopleMenuItemActionPerformed(e);
			}
		});
		
		peoplePopupMenu.add(showInfoPeopleMenuItem);

		// ---- renamePeopleMenuItem ----
		renamePeopleMenuItem.setText("Change Nickname...");
		renamePeopleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				renamePeopleMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(renamePeopleMenuItem);

		// ---- changeUserIdMenuItem ----
		changeUserIdMenuItem.setText("Change User ID...");
		changeUserIdMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				changeUserIdMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(changeUserIdMenuItem);

		// ---- removePeopleMenuItem ----
		removePeopleMenuItem.setText("Remove Member...");
		removePeopleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				removePeopleMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(removePeopleMenuItem);
	}
		
	private void sendMessageMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("sendMessageMenuItemActionPerformed");
	    } 
	
	private void showInfoPeopleMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("showInfoPeopleMenuItemActionPerformed");
	    }
	
	private void renamePeopleMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("renamePeopleMenuItemActionPerformed");
	    }
	
	private void changeUserIdMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("changeUserIdMenuItemActionPerformed");
	    }
	
	private void removePeopleMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("removePeopleMenuItemActionPerformed");
	       //jakeGuiAccess.removeProjectMember(getSelectedMember());
			updateUi();
	    }
	
	private void openExecutFileMenuItemActionEvent(ActionEvent event) {
        log.info("openExecutFileMenuItemActionEvent");
        
    }
	
	private boolean isPersonSelected() {
		return peopleTable.getSelectedRow() >= 0;
	}

	private ProjectMember getSelectedMember() {
		int selRow = peopleTable.getSelectedRow();
		if (selRow >= 0) {
			
			log.info("getSelectedNode: (" + selRow + ") " + peopleTableModel.getMembers().get(selRow));
			return (peopleTableModel.getMembers().get(selRow));
		} else {
			log.info("getSelctedNode: null");
			return null;
		}
	}
	
	private JScrollPane peopleScrollPane;
	private JXTable peopleTable;
	private final IJakeGuiAccess jakeGuiAccess;
	private JPopupMenu peoplePopupMenu;
	private JMenuItem sendMessageMenuItem;
	private JMenuItem showInfoPeopleMenuItem;
	private JMenuItem renamePeopleMenuItem;
	private JMenuItem changeUserIdMenuItem;
	private JMenuItem removePeopleMenuItem;

	
}
