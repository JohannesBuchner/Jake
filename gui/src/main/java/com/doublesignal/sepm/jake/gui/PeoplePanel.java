package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTable;
import org.jdesktop.swingx.decorator.HighlighterFactory;

import com.doublesignal.sepm.jake.core.domain.Project;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.NotesTableModel.NotesUpdaterObservable;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class PeoplePanel extends JPanel {
	private static Logger log = Logger.getLogger(PeoplePanel.class);
	private final JakeGui gui;
	private final IJakeGuiAccess jakeGuiAccess;

	private NotesTableModel notesTableModel;

	public PeoplePanel(JakeGui gui) {
		log.info("Initializing PeoplePanel.");
		this.gui = gui;
		this.jakeGuiAccess = gui.getJakeGuiAccess();

		initComponents();
		updateData();
	}

	

	public void updateData() {
		log.info("Updating Notes Panel...");
		notesTableModel.updateData();
	}


	
	

	public void initComponents() {
		PeopleTable = new JXTable();
		PeopleScrollPane = new JScrollPane();
		PeoplePopupMenu = new JPopupMenu();
		

		this.setLayout(new BorderLayout());
		notesTableModel = new NotesTableModel(jakeGuiAccess);
		PeopleTable.setComponentPopupMenu(PeoplePopupMenu);
		PeopleTable.setColumnControlVisible(true);
		PeopleTable.setHighlighters(HighlighterFactory.createSimpleStriping());
		PeopleTable.setModel(notesTableModel);
	

		TableColumnModel cm = PeopleTable.getColumnModel();
		cm.getColumn(0).setPreferredWidth(265);

		PeopleTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
		PeopleScrollPane.setViewportView(PeopleTable);

		this.add(PeopleScrollPane, BorderLayout.NORTH);
	}

	
		
	

	private JScrollPane PeopleScrollPane;
	private JXTable PeopleTable;
	private JPopupMenu PeoplePopupMenu;
	
}
