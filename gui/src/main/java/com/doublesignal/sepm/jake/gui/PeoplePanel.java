package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.table.TableColumnModel;


import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeoplePanel extends JPanel {
	private static Logger log = Logger.getLogger(PeoplePanel.class);
	private final JakeGui jakeGui;
	private final IJakeGuiAccess jakeGuiAccess;

	private PeopleTableModel peopleTableModel;

	public PeoplePanel(JakeGui gui) {
		log.info("Initializing PeoplePanel.");
		this.jakeGui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();
		initPopupMenu();
		initComponents();
		updateData();
	
		jakeGui.getMainTabbedPane()
		.addTab("peopletab", new ImageIcon(
				getClass().getResource("/icons/people.png")),
				this);
	}

	

	public void updateData() {
		log.info("Updating people Panel...");
		peopleTableModel.updateData();
	}


	
	

	public void initComponents() {
		PeopleTable = new JXTable();
		PeopleScrollPane = new JScrollPane();
		
		

		this.setLayout(new BorderLayout());
		peopleTableModel = new PeopleTableModel(jakeGuiAccess);
		PeopleTable.setComponentPopupMenu(peoplePopupMenu);
		PeopleTable.setColumnControlVisible(true);
		PeopleTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		PeopleTable.setModel(peopleTableModel);
	

		TableColumnModel cm = PeopleTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(265);

		PeopleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		PeopleScrollPane.setViewportView(PeopleTable);

		this.add(PeopleScrollPane, BorderLayout.NORTH);
	}
	
	private void initPopupMenu() {
	
		
		peoplePopupMenu = new JPopupMenu();
		sendMessageMenuItem = new JMenuItem();
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
	    }
	
	

	private JScrollPane PeopleScrollPane;
	private JXTable PeopleTable;
	private JPopupMenu peoplePopupMenu;
	private JMenuItem sendMessageMenuItem;
	private JMenuItem showInfoPeopleMenuItem;
	private JMenuItem renamePeopleMenuItem;
	private JMenuItem changeUserIdMenuItem;
	private JMenuItem removePeopleMenuItem;

	
}
