import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.tree.*;
import com.jgoodies.uif_lite.component.*;
import org.jdesktop.swingx.*;
/*
 * Created by JFormDesigner on Mon May 05 17:07:29 CEST 2008
 */



/**
 * @author tester tester
 */
public class JakeGuiMockup1 extends JPanel {
	public JakeGuiMockup1() {
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		frame1 = new JFrame();
		panel3 = new JPanel();
		label1 = new JLabel();
		label2 = new JLabel();
		label3 = new JLabel();
		panel1 = new JPanel();
		tabbedPane1 = new JTabbedPane();
		panel2 = new JPanel();
		scrollPane1 = new JScrollPane();
		table3 = new JTable();
		filesPanel = new JPanel();
		scrollPane2 = new JScrollPane();
		table1 = new JTable();
		notesPanel = new JPanel();
		scrollPane3 = new JScrollPane();
		table2 = new JTable();
		toolBar1 = new JToolBar();
		button10 = new JButton();
		button11 = new JButton();
		button8 = new JButton();
		button4 = new JButton();
		button5 = new JButton();
		button7 = new JButton();
		hSpacer1 = new JPanel(null);
		label4 = new JLabel();
		scrollPane4 = new JScrollPane();
		textArea1 = new JTextArea();
		menuBar1 = new JMenuBar();
		menu1 = new JMenu();
		menuItem6 = new JMenuItem();
		menuItem1 = new JMenuItem();
		menuItem2 = new JMenuItem();
		menuItem3 = new JMenuItem();
		menuItem4 = new JMenuItem();
		menuItem5 = new JMenuItem();
		menu3 = new JMenu();
		menuItem19 = new JMenuItem();
		menuItem14 = new JMenuItem();
		menuItem15 = new JMenuItem();
		menuItem30 = new JMenuItem();
		menu2 = new JMenu();
		menuItem22 = new JMenuItem();
		menuItem23 = new JMenuItem();
		checkBoxMenuItem1 = new JCheckBoxMenuItem();
		menuItem24 = new JMenuItem();
		menuItem25 = new JMenuItem();
		menu8 = new JMenu();
		menuItem9 = new JMenuItem();
		menuItem31 = new JMenuItem();
		menuItem26 = new JMenuItem();
		menuItem8 = new JMenuItem();
		menuItem7 = new JMenuItem();
		popupMenuTree = new JPopupMenu();
		menuItem10 = new JMenuItem();
		menuItem17 = new JMenuItem();
		menuItem27 = new JMenuItem();
		menuItem11 = new JMenuItem();
		popupMenuFiles = new JPopupMenu();
		menuItem12 = new JMenuItem();
		menuItem21 = new JMenuItem();
		checkBoxMenuItem2 = new JCheckBoxMenuItem();
		menuItem16 = new JMenuItem();
		menuItem18 = new JMenuItem();
		menuItem13 = new JMenuItem();
		popupMenuNotes = new JPopupMenu();
		menuItem20 = new JMenuItem();
		menuItem28 = new JMenuItem();
		menuItem29 = new JMenuItem();

		//======== frame1 ========
		{
			frame1.setTitle("Jake - \u00dcbersetzerbau (6/6)");
			Container frame1ContentPane = frame1.getContentPane();
			frame1ContentPane.setLayout(new BorderLayout());

			//======== this ========
			{

				// JFormDesigner evaluation mark
				this.setBorder(new javax.swing.border.CompoundBorder(
					new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
						"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
						javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
						java.awt.Color.red), this.getBorder())); this.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

				this.setLayout(new BorderLayout());

				//======== panel3 ========
				{
					panel3.setBorder(new SoftBevelBorder(SoftBevelBorder.LOWERED));
					panel3.setLayout(new BorderLayout(4, 4));

					//---- label1 ----
					label1.setText("Pulling File xy...");
					label1.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
					panel3.add(label1, BorderLayout.WEST);

					//---- label2 ----
					label2.setText("Connected as pstein@jabber.fsinf.at");
					label2.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
					panel3.add(label2, BorderLayout.EAST);

					//---- label3 ----
					label3.setText("4 Files, 1 Conflict");
					label3.setHorizontalAlignment(SwingConstants.CENTER);
					label3.setFont(new Font("Lucida Grande", Font.PLAIN, 12));
					panel3.add(label3, BorderLayout.CENTER);
				}
				this.add(panel3, BorderLayout.SOUTH);

				//======== panel1 ========
				{
					panel1.setLayout(new BorderLayout());

					//======== tabbedPane1 ========
					{

						//======== panel2 ========
						{
							panel2.setLayout(new BoxLayout(panel2, BoxLayout.X_AXIS));

							//======== scrollPane1 ========
							{

								//---- table3 ----
								table3.setModel(new DefaultTableModel(
									new Object[][] {
										{"Simon", "simon@jabber.fsinf.at", "Online", "OK", ""},
										{"Dominik", "d@jabber.fsinf.at", "Online", "1 File in Conflict", null},
										{"Chris", "c@jabber.fsinf.at", "Online", "OK", null},
										{"Peter", "peter@jabber.fsinf.at", "Offline", "OK", null},
									},
									new String[] {
										"User", "User ID", "Connected", "State", "Notes"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										true, true, false, false, true
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								scrollPane1.setViewportView(table3);
							}
							panel2.add(scrollPane1);
						}
						tabbedPane1.addTab("People", panel2);


						//======== filesPanel ========
						{
							filesPanel.setLayout(new BorderLayout());

							//======== scrollPane2 ========
							{
								scrollPane2.setComponentPopupMenu(popupMenuFiles);

								//---- table1 ----
								table1.setModel(new DefaultTableModel(
									new Object[][] {
										{"SEPM_SS08_Artefaktenbeschreibung.pdf", "Latest", "! released", "Yesterday", "1 KB"},
										{"SEPM_VO_Block_1.pdf", "Conflict", "obsolete", "March 18", "5 MB"},
										{"SEPM_SS08_Artefaktenliste.pdf", "Modified", "!", "Today, 12:00", "10 MB"},
										{"ToDos.txt", "Locked by Johannes", "todo", "Today 12:00", "5 KB"},
									},
									new String[] {
										"Name", "State", "Tags", "Date modified", "Size"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										false, false, true, false, false
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								{
									TableColumnModel cm = table1.getColumnModel();
									cm.getColumn(0).setPreferredWidth(245);
									cm.getColumn(2).setPreferredWidth(75);
									cm.getColumn(4).setPreferredWidth(50);
								}
								table1.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								table1.setPreferredScrollableViewportSize(new Dimension(450, 379));
								table1.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
								scrollPane2.setViewportView(table1);
							}
							filesPanel.add(scrollPane2, BorderLayout.CENTER);
						}
						tabbedPane1.addTab("Files", filesPanel);


						//======== notesPanel ========
						{
							notesPanel.setLayout(new BorderLayout());

							//======== scrollPane3 ========
							{
								scrollPane3.setComponentPopupMenu(popupMenuNotes);

								//---- table2 ----
								table2.setModel(new DefaultTableModel(
									new Object[][] {
										{"Update 1", "", "Peter", "Today"},
										{"Aufgaben und Ziele", "!", "Simon", "Yesterday, 11:00"},
										{"Testnotiz 1", "test", "Johannes", "April 12th"},
									},
									new String[] {
										"Title", "Tags", "Creator", "Time"
									}
								) {
									boolean[] columnEditable = new boolean[] {
										true, true, false, true
									};
									@Override
									public boolean isCellEditable(int rowIndex, int columnIndex) {
										return columnEditable[columnIndex];
									}
								});
								{
									TableColumnModel cm = table2.getColumnModel();
									cm.getColumn(0).setPreferredWidth(265);
								}
								table2.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
								scrollPane3.setViewportView(table2);
							}
							notesPanel.add(scrollPane3, BorderLayout.CENTER);
						}
						tabbedPane1.addTab("Notes", notesPanel);

					}
					panel1.add(tabbedPane1, BorderLayout.CENTER);

					//======== toolBar1 ========
					{
						toolBar1.setBorderPainted(false);
						toolBar1.setFont(new Font("Lucida Grande", Font.PLAIN, 12));

						//---- button10 ----
						button10.setText("Open Project Folder");
						button10.setHorizontalAlignment(SwingConstants.RIGHT);
						toolBar1.add(button10);

						//---- button11 ----
						button11.setText("Refresh Datapool");
						button11.setHorizontalAlignment(SwingConstants.RIGHT);
						toolBar1.add(button11);
						toolBar1.addSeparator();

						//---- button8 ----
						button8.setText("Sync");
						button8.setSelected(true);
						toolBar1.add(button8);

						//---- button4 ----
						button4.setText("Push");
						toolBar1.add(button4);

						//---- button5 ----
						button5.setText("Pull");
						toolBar1.add(button5);
						toolBar1.addSeparator();

						//---- button7 ----
						button7.setText("Log");
						toolBar1.add(button7);
						toolBar1.add(hSpacer1);

						//---- label4 ----
						label4.setText("Search ");
						toolBar1.add(label4);

						//======== scrollPane4 ========
						{

							//---- textArea1 ----
							textArea1.setMaximumSize(new Dimension(50, 2147483647));
							scrollPane4.setViewportView(textArea1);
						}
						toolBar1.add(scrollPane4);
					}
					panel1.add(toolBar1, BorderLayout.NORTH);
				}
				this.add(panel1, BorderLayout.CENTER);

				//======== menuBar1 ========
				{

					//======== menu1 ========
					{
						menu1.setText("File");

						//---- menuItem6 ----
						menuItem6.setText("New");
						menu1.add(menuItem6);

						//---- menuItem1 ----
						menuItem1.setText("Open");
						menu1.add(menuItem1);

						//---- menuItem2 ----
						menuItem2.setText("Save");
						menu1.add(menuItem2);

						//---- menuItem3 ----
						menuItem3.setText("Save As...");
						menu1.add(menuItem3);

						//---- menuItem4 ----
						menuItem4.setText("Close");
						menu1.add(menuItem4);

						//---- menuItem5 ----
						menuItem5.setText("Exit");
						menu1.add(menuItem5);
					}
					menuBar1.add(menu1);

					//======== menu3 ========
					{
						menu3.setText("View");

						//---- menuItem19 ----
						menuItem19.setText("People");
						menu3.add(menuItem19);

						//---- menuItem14 ----
						menuItem14.setText("Files");
						menu3.add(menuItem14);

						//---- menuItem15 ----
						menuItem15.setText("Notes");
						menu3.add(menuItem15);
						menu3.addSeparator();

						//---- menuItem30 ----
						menuItem30.setText("Log");
						menu3.add(menuItem30);
					}
					menuBar1.add(menu3);

					//======== menu2 ========
					{
						menu2.setText("Network");

						//---- menuItem22 ----
						menuItem22.setText("LogIn As...");
						menu2.add(menuItem22);

						//---- menuItem23 ----
						menuItem23.setText("Logout");
						menu2.add(menuItem23);
						menu2.addSeparator();

						//---- checkBoxMenuItem1 ----
						checkBoxMenuItem1.setText("Auto Sync");
						checkBoxMenuItem1.setSelected(true);
						menu2.add(checkBoxMenuItem1);

						//---- menuItem24 ----
						menuItem24.setText("Push");
						menu2.add(menuItem24);

						//---- menuItem25 ----
						menuItem25.setText("Pull");
						menu2.add(menuItem25);
					}
					menuBar1.add(menu2);

					//======== menu8 ========
					{
						menu8.setText("Project");

						//---- menuItem9 ----
						menuItem9.setText("Open Project Folder");
						menu8.add(menuItem9);

						//---- menuItem31 ----
						menuItem31.setText("Refresh Datapool");
						menu8.add(menuItem31);

						//---- menuItem26 ----
						menuItem26.setText("Add File...");
						menu8.add(menuItem26);

						//---- menuItem8 ----
						menuItem8.setText("Add Folder...");
						menu8.add(menuItem8);
						menu8.addSeparator();

						//---- menuItem7 ----
						menuItem7.setText("Add User");
						menu8.add(menuItem7);
					}
					menuBar1.add(menu8);
				}
				this.add(menuBar1, BorderLayout.NORTH);
			}
			frame1ContentPane.add(this, BorderLayout.CENTER);
			frame1.pack();
			frame1.setLocationRelativeTo(frame1.getOwner());
		}

		//======== popupMenuTree ========
		{

			//---- menuItem10 ----
			menuItem10.setText("Send Message...");
			menuItem10.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			popupMenuTree.add(menuItem10);

			//---- menuItem17 ----
			menuItem17.setText("Show Info...");
			popupMenuTree.add(menuItem17);

			//---- menuItem27 ----
			menuItem27.setText("Rename");
			popupMenuTree.add(menuItem27);

			//---- menuItem11 ----
			menuItem11.setText("Remove");
			popupMenuTree.add(menuItem11);
		}

		//======== popupMenuFiles ========
		{

			//---- menuItem12 ----
			menuItem12.setText("Open");
			menuItem12.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			popupMenuFiles.add(menuItem12);

			//---- menuItem21 ----
			menuItem21.setText("Lock File");
			popupMenuFiles.add(menuItem21);

			//---- checkBoxMenuItem2 ----
			checkBoxMenuItem2.setText("Auto Sync");
			checkBoxMenuItem2.setSelected(true);
			popupMenuFiles.add(checkBoxMenuItem2);

			//---- menuItem16 ----
			menuItem16.setText("View Log...");
			popupMenuFiles.add(menuItem16);

			//---- menuItem18 ----
			menuItem18.setText("Resolve Conflict...");
			popupMenuFiles.add(menuItem18);

			//---- menuItem13 ----
			menuItem13.setText("Delete File");
			popupMenuFiles.add(menuItem13);
		}

		//======== popupMenuNotes ========
		{

			//---- menuItem20 ----
			menuItem20.setText("View/Edit");
			menuItem20.setFont(new Font("Lucida Grande", Font.BOLD, 14));
			popupMenuNotes.add(menuItem20);

			//---- menuItem28 ----
			menuItem28.setText("New Note...");
			popupMenuNotes.add(menuItem28);

			//---- menuItem29 ----
			menuItem29.setText("Remove");
			popupMenuNotes.add(menuItem29);
		}
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - tester tester
	private JFrame frame1;
	private JPanel panel3;
	private JLabel label1;
	private JLabel label2;
	private JLabel label3;
	private JPanel panel1;
	private JTabbedPane tabbedPane1;
	private JPanel panel2;
	private JScrollPane scrollPane1;
	private JTable table3;
	private JPanel filesPanel;
	private JScrollPane scrollPane2;
	private JTable table1;
	private JPanel notesPanel;
	private JScrollPane scrollPane3;
	private JTable table2;
	private JToolBar toolBar1;
	private JButton button10;
	private JButton button11;
	private JButton button8;
	private JButton button4;
	private JButton button5;
	private JButton button7;
	private JPanel hSpacer1;
	private JLabel label4;
	private JScrollPane scrollPane4;
	private JTextArea textArea1;
	private JMenuBar menuBar1;
	private JMenu menu1;
	private JMenuItem menuItem6;
	private JMenuItem menuItem1;
	private JMenuItem menuItem2;
	private JMenuItem menuItem3;
	private JMenuItem menuItem4;
	private JMenuItem menuItem5;
	private JMenu menu3;
	private JMenuItem menuItem19;
	private JMenuItem menuItem14;
	private JMenuItem menuItem15;
	private JMenuItem menuItem30;
	private JMenu menu2;
	private JMenuItem menuItem22;
	private JMenuItem menuItem23;
	private JCheckBoxMenuItem checkBoxMenuItem1;
	private JMenuItem menuItem24;
	private JMenuItem menuItem25;
	private JMenu menu8;
	private JMenuItem menuItem9;
	private JMenuItem menuItem31;
	private JMenuItem menuItem26;
	private JMenuItem menuItem8;
	private JMenuItem menuItem7;
	private JPopupMenu popupMenuTree;
	private JMenuItem menuItem10;
	private JMenuItem menuItem17;
	private JMenuItem menuItem27;
	private JMenuItem menuItem11;
	private JPopupMenu popupMenuFiles;
	private JMenuItem menuItem12;
	private JMenuItem menuItem21;
	private JCheckBoxMenuItem checkBoxMenuItem2;
	private JMenuItem menuItem16;
	private JMenuItem menuItem18;
	private JMenuItem menuItem13;
	private JPopupMenu popupMenuNotes;
	private JMenuItem menuItem20;
	private JMenuItem menuItem28;
	private JMenuItem menuItem29;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
