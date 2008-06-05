package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.ExistingProjectException;
import com.doublesignal.sepm.jake.core.services.exceptions.GuiCoreInteractionException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidDatabaseException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.services.exceptions.NonExistantDatabaseException;
import com.doublesignal.sepm.jake.fss.NotADirectoryException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;

import info.clearthought.layout.*;
/*
 * Created by JFormDesigner on Thu Jun 05 00:33:51 CEST 2008
 */



/**
 * @author johannes
 */
@SuppressWarnings("serial")
public class NewProject extends JDialog {
	ITranslationProvider translator = null;
	JakeGuiAccess jga = null;
	private static Logger log = Logger.getLogger(NewProject.class);

	public NewProject() {
		super();
		log.debug("NewProject dialog starts");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		translator = (ITranslationProvider) factory.getBean("translationProvider");
		log.debug("NewProject:initComponents");
		initComponents();
		log.debug("NewProject:setVisible");
		setVisible(true);
	}
	
	private void folderSelectActionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(folderTextField.getText());
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnCode = fileChooser.showOpenDialog(null);
		if (returnCode == JFileChooser.APPROVE_OPTION) {
			String rootPath = fileChooser.getSelectedFile().getAbsolutePath();
			folderTextField.setText(rootPath);
			
			projectNameTextField.setEditable(true);
			okButton.setEnabled(false);
			folderTextField.setBackground(Color.WHITE);
			jga = null;
			try {
				jga = JakeGuiAccess.openProjectByRootpath(rootPath);
				okButton.setEnabled(true);
				folderTextField.setBackground(Color.GREEN);
				
				projectNameTextField.setEditable(false);
				projectNameTextField.setText(jga.getProject().getName());
				
				okButton.setText(translator.get("Open Project"));
				okButton.setEnabled(true);
			} catch (NonExistantDatabaseException e) {
				okButton.setText(translator.get("Create Project"));
				projectNameTextField.setEditable(true);
				projectNameTextField.setText( new File(rootPath).getName() );
				okButton.setEnabled(true);
			} catch (InvalidDatabaseException e) {
				folderTextField.setBackground(Color.RED);
				UserDialogHelper.error(this, "Invalid Database");
			} catch (InvalidRootPathException e) {
				UserDialogHelper.error(this, "Invalid root path");
				folderTextField.setBackground(Color.RED);
			}
		}
	}
	
	private void okButtonActionPerformed(ActionEvent event) {
		if(jga!=null){
			log.info("starting main window with opened database ...");
			new JakeGui(jga);
			setVisible(false);
		}else{
			try {
				log.info("creating database ...");
				jga = JakeGuiAccess.createNewProjectByRootpath(
						folderTextField.getText(), projectNameTextField.getText());
				setVisible(false);
				log.info("created Database, starting main window");
				new JakeGui(jga);
			} catch (ExistingProjectException e) {
				log.error("Project already exists");
				UserDialogHelper.error(this, translator.get("Project already exists"));
			} catch (InvalidDatabaseException e) {
				log.error("Invalid Database");
				UserDialogHelper.error(this, translator.get("Invalid Database"));
			} catch (SQLException e) {
				log.error("Invalid Database (SQL Exception)");
				UserDialogHelper.error(this, translator.get("Invalid Database (SQL Exception)"));
			} catch (IOException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator.get("Invalid Project Directory"));
			} catch (NotADirectoryException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator.get("Invalid Project Directory"));
			}
		}
	}
	
	private void cancelButtonActionPerformed(ActionEvent event) {
		System.exit(0);
	}
	
	
	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		panel1 = new JPanel();
		label7 = new JLabel();
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label8 = new JLabel();
		label6 = new JLabel();
		label2 = new JLabel();
		folderTextField = new JTextField();
		folderSelectButton = new JButton();
		label5 = new JLabel();
		label1 = new JLabel();
		projectNameTextField = new JTextField();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Welcome to Jake - the collaborative file share client.");
		setResizable(false);
		setModal(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== panel1 ========
		{
			panel1.setBackground(Color.white);

			// JFormDesigner evaluation mark
			panel1.setLayout(new FlowLayout());

			//---- label7 ----
			try {
				label7.setIcon(new ImageIcon(new ClassPathResource("jake.gif").getURL()));
			} catch (IOException e1) {
				log.warn("image icon not found.");
			}
			label7.setBackground(Color.white);
			label7.setText("Welcome to Jake!");
			label7.setHorizontalAlignment(SwingConstants.RIGHT);
			label7.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
			label7.setAlignmentX(5.0F);
			label7.setAlignmentY(5.5F);
			panel1.add(label7);
		}
		contentPane.add(panel1, BorderLayout.NORTH);

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new TableLayout(new double[][] {
					{TableLayout.PREFERRED, 246, 64},
					{TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
				((TableLayout)contentPanel.getLayout()).setHGap(2);
				((TableLayout)contentPanel.getLayout()).setVGap(15);

				//---- label8 ----
				label8.setText("<html><body>Jake is a collaborative file sharing client.<br>Begin sharing your files with two simple steps!</body></html>");
				contentPanel.add(label8, new TableLayoutConstraints(0, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- label6 ----
				label6.setText("1. Select the folder you want to share:");
				contentPanel.add(label6, new TableLayoutConstraints(0, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- label2 ----
				label2.setText("Folder:");
				label2.setIcon(UIManager.getIcon("FileChooser.newFolderIcon"));
				contentPanel.add(label2, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				folderTextField.setEditable(false);
				contentPanel.add(folderTextField, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				
				//---- button2 ----
				folderSelectButton.setText("...");
				folderSelectButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						folderSelectActionPerformed(event);
					}
				});
				contentPanel.add(folderSelectButton, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- label5 ----
				label5.setText("2. Give your project a name! (or use folder name as default)");
				contentPanel.add(label5, new TableLayoutConstraints(0, 3, 2, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- label1 ----
				label1.setText("Project Name:");
				contentPanel.add(label1, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(projectNameTextField, new TableLayoutConstraints(1, 4, 2, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
			}
			dialogPane.add(contentPanel, BorderLayout.NORTH);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton ----
				okButton.setText("Load/Generate New Project");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						okButtonActionPerformed(event);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
						GridBagConstraints.CENTER, GridBagConstraints.BOTH,
						new Insets(0, 0, 0, 5), 0, 0));
				
				//---- cancelButton ----
				cancelButton.setText("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						cancelButtonActionPerformed(event);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	private JPanel panel1;
	private JLabel label7;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label8;
	private JLabel label6;
	private JLabel label2;
	private JTextField folderTextField;
	private JButton folderSelectButton;
	private JLabel label5;
	private JLabel label1;
	private JTextField projectNameTextField;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
