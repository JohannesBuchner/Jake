package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;

import javax.swing.*;
import javax.swing.table.TableColumnModel;


import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.FilterPipeline;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoProjectLoadedException;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.LaunchException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

@SuppressWarnings("serial")
/**
 * @author philipp
 */
public class PeoplePanel extends JPanel {
	private static final Logger log = Logger.getLogger(PeoplePanel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	private final JakeGui jakeGui;
	private PeopleTableModel peopleTableModel;
	private Frame owner;

	int tabindex = 1;
	
    public void setFilters(FilterPipeline filterPipeline) {
        peopleTable.setFilters(filterPipeline);
    }	
	
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

		this.owner = jakeGui.getMainFrame();
	}

	public String getTitle() {
		return translator.get("PeoplePanelTitle", String.valueOf(peopleTableModel.getOnlineMembersCount()), String.valueOf(peopleTableModel.getMembersCount()));
	}

	public void updatePeopleUi() {
		log.info("Updating people Panel...");
		peopleTableModel.updateData();
		if (tabindex >= 1)
			jakeGui.getMainTabbedPane().setTitleAt(tabindex,getTitle());
	}

	public void initComponents() {
		peopleTable = new JXTable();
		peopleScrollPane = new JScrollPane();
		peoplePopupMenu = new JPopupMenu();
		

		this.setLayout(new BorderLayout());
		peopleTableModel = new PeopleTableModel(jakeGuiAccess);
		// peopleTable.setComponentPopupMenu(peoplePopupMenu);
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

		peopleTable.addMouseListener( new MouseAdapter() {
			public void mouseClicked( MouseEvent e ) {
				// Right mouse click
				if ( SwingUtilities.isRightMouseButton( e ) ) {
					// get the coordinates of the mouse click
					Point p = e.getPoint();
	
					// get the row index that contains that coordinate
					int rowNumber = peopleTable.rowAtPoint( p );
	
					// Get the ListSelectionModel of the JTable
					ListSelectionModel model = peopleTable.getSelectionModel();
	
					// set the selected interval of rows. Using the "rowNumber"
					// variable for the beginning and end selects only that one row.
					model.setSelectionInterval( rowNumber, rowNumber );
	
					// Show the table popup
					peoplePopupMenu.show(peopleTable, (int)e.getPoint().getX(), (int)e.getPoint().getY());
				}
			}
		});
		
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
		addProjectMemberMenuItem = new JMenuItem();

		// ---- sendMessageMenuItem ----
		sendMessageMenuItem.setText(translator.get("PeoplePanelMenuItemSendMessage"));
		sendMessageMenuItem.setIcon(new ImageIcon(getClass().getResource(
				"/icons/message-new.png")));
		sendMessageMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				sendMessageMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(sendMessageMenuItem);
		
		// ---- showInfoPeopleMenuItem ----
		showInfoPeopleMenuItem.setText(translator.get("PeoplePanelMenuItemShowInfo"));
		showInfoPeopleMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				showInfoPeopleMenuItemActionPerformed(e);
			}
		});
		
		peoplePopupMenu.add(showInfoPeopleMenuItem);

		/*// ---- renamePeopleMenuItem ----
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
		peoplePopupMenu.add(changeUserIdMenuItem);*/
		
		// ---- addProjectMemberMenuItem ----
		addProjectMemberMenuItem.setText(translator.get("PeoplePanelMenuItemAddProjectMember"));
		addProjectMemberMenuItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				addProjectMemberMenuItemActionPerformed(e);
			}
		});
		peoplePopupMenu.add(addProjectMemberMenuItem);

		// ---- removePeopleMenuItem ----
		removePeopleMenuItem.setText(translator.get("PeoplePanelMenuItemRemoveProjectMember"));
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
		    new SendMessageDialog(owner, peopleTableModel.getProjectMemberAt(peopleTable.getSelectedRow()).getUserId(), this.jakeGuiAccess).setVisible(true);
	    } 
	
	private void showInfoPeopleMenuItemActionPerformed(ActionEvent event)
	    {
	        log.info("showInfoPeopleMenuItemActionPerformed");
	        
	      //  new ViewProjectMemberCommentDialog(owner ,getSelectedMember(), this.jakeGuiAccess ).setVisible(true);
	        
	        ViewProjectMemberCommentDialog viewProjectMemberCommentDialog = new ViewProjectMemberCommentDialog(owner ,getSelectedMember(), this.jakeGuiAccess );
	        viewProjectMemberCommentDialog.setVisible(true);
	        if(viewProjectMemberCommentDialog.isSaved())
	        	updatePeopleUi();
	        	
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
	        log.info("removePeopleMenuItemActionPerformed"+getSelectedMember().getUserId());
	      
	        //log.info(getSelectedMember().getUserId());
	        
	        jakeGuiAccess.removeProjectMember(getSelectedMember());
	        updatePeopleUi();
			
	       //updatePeopleUi();
	    }
	
	private void openExecutFileMenuItemActionEvent(ActionEvent event) {
        log.info("openExecutFileMenuItemActionEvent");
        
    }
	
	private void addProjectMemberMenuItemActionPerformed(ActionEvent e) {
		log.info("add Project Member.");
		AddProjectMemberDialog addProjectMemberDialog = new AddProjectMemberDialog(jakeGui.getMainFrame());
		addProjectMemberDialog.setVisible(true);

		if (addProjectMemberDialog.isSaved()) {
			jakeGuiAccess.addProjectMember(addProjectMemberDialog.getContent());
		}

		updatePeopleUi();
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
	
	public int getNameColPos() {
		// TODO: make dynamic lookup
		return 0;
	}
	
	private JScrollPane peopleScrollPane;
	private JXTable peopleTable;
	private final IJakeGuiAccess jakeGuiAccess;
	private JPopupMenu peoplePopupMenu;
	private JMenuItem sendMessageMenuItem;
	private JMenuItem showInfoPeopleMenuItem;
	private JMenuItem renamePeopleMenuItem;
	private JMenuItem changeUserIdMenuItem;
	private JMenuItem addProjectMemberMenuItem;
	private JMenuItem removePeopleMenuItem;
}
