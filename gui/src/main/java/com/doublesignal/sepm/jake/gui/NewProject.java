package com.doublesignal.sepm.jake.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.ExistingProjectException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidDatabaseException;
import com.doublesignal.sepm.jake.core.services.exceptions.InvalidRootPathException;
import com.doublesignal.sepm.jake.core.services.exceptions.NonExistantDatabaseException;
import com.doublesignal.sepm.jake.fss.exceptions.NotADirectoryException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * @author johannes, peter
 */
@SuppressWarnings("serial")
public class NewProject extends JDialog {
	
	private static final Logger log = Logger.getLogger(NewProject.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

	IJakeGuiAccess jga = null;

	public NewProject(String foldersuggestion) {
		super();
		log.debug("NewProject dialog starts");
		initComponents();
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE); 
		log.debug("NewProject:setVisible");
		setVisible(true);
		if (foldersuggestion != null) {
			folderTextField.setText(foldersuggestion);
			if (checkFolderSelection())
				okButtonActionPerformed(null);
		}
	}

	private void folderSelectActionPerformed(ActionEvent event) {
		JFileChooser fileChooser = new JFileChooser(folderTextField.getText());
		fileChooser.setMultiSelectionEnabled(false);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		int returnCode = fileChooser.showOpenDialog(null);
		if (returnCode == JFileChooser.APPROVE_OPTION) {
			String rootPath = fileChooser.getSelectedFile().getAbsolutePath();
			folderTextField.setText(rootPath);
			checkFolderSelection();
		}
	}

	private boolean checkFolderSelection() {
		String rootPath = folderTextField.getText();
		projectNameTextField.setEditable(true);
		useridTextField.setEditable(true);
		okButton.setEnabled(false);
		folderTextField.setBackground(Color.WHITE);
		
		// try to close connection
		if(jga != null) {
			try {
				jga.close();
			} catch (SQLException e) {
				log.warn("Database cannot be closed");
			}
		}
		jga = null;
		
		try {
			jga = JakeGuiAccess.openProjectByRootpath(rootPath);
			okButton.setEnabled(true);
			folderTextField.setBackground(Color.GREEN);
			
			projectNameTextField.setText(jga.getProject().getName());
			projectNameTextField.setEditable(false);
			useridTextField.setText(jga.getLoginUserid());
			useridTextField.setEditable(false);
			okButton.setText(translator.get("NewProjectDialogOpenProject"));
			okButton.setEnabled(true);
			return true;
		} catch (NonExistantDatabaseException e) {
			okButton.setText(translator.get("NewProjectDialogCreateProject"));
			projectNameTextField.setEditable(true);
			projectNameTextField.setText(new File(rootPath).getName());
			okButton.setEnabled(true);
		} catch (InvalidDatabaseException e) {
			folderTextField.setBackground(Color.RED);
			UserDialogHelper.translatedError(this, "NewProjectDialogInvalidDatabase");
		} catch (InvalidRootPathException e) {
			UserDialogHelper.translatedError(this, "NewProjectDialogInvalidRootPath");
			folderTextField.setBackground(Color.RED);
		}
		return false;
	}

	private void okButtonActionPerformed(ActionEvent event) {
		if (projectNameTextField.getText().length() == 0) {
			UserDialogHelper.translatedError(this, "NewProjectDialogProjectNameTooShort");
			return;
		}
		if (!JakeGuiAccess.isOfCorrectUseridFormat(useridTextField.getText())){
			UserDialogHelper.translatedError(this, "NewProjectDialogInvalidUserIdFormat");
			return;
		}
		
		if (jga != null) {
			log.info("starting main window with opened database ...");
			new JakeGui(jga, false);
			setVisible(false);
		} else {
			try {
				log.info("creating database ...");
				jga = JakeGuiAccess.createNewProjectByRootpath(folderTextField
						.getText(), projectNameTextField.getText(),
						useridTextField.getText());
				jga.addProjectMember(useridTextField.getText());
				setVisible(false);
				log.info("created Database, starting main window");
				new JakeGui(jga, true);
			} catch (ExistingProjectException e) {
				log.error("Project already exists");
				UserDialogHelper.error(this, translator
						.get("NewProjectDialogProjectAlreadyExists"));
			} catch (InvalidDatabaseException e) {
				log.error("Invalid Database");
				UserDialogHelper
						.error(this, translator.get("NewProjectDialogInvalidDatabase"));
			} catch (NotADirectoryException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator
						.get("NewProjectDialogInvalidProjectDirectory"));
			} catch (InvalidRootPathException e) {
				log.error("Invalid Project Directory");
				UserDialogHelper.error(this, translator
						.get("NewProjectDialogInvalidProjectDirectory"));
			}
		}
	}

	private void cancelButtonActionPerformed(ActionEvent event) {
		System.exit(0);
	}

	private void initComponents() {
		headerPanel = new JPanel();
		jakeIconLabel = new JLabel();
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		infoLabel = new JLabel();
		selectFolderLabel = new JLabel();
		folderLabel = new JLabel();
		folderTextField = new JTextField();
		folderSelectButton = new JButton();
		adviseNameProjectLabel = new JLabel();
		adviseProjectNameLabel = new JLabel();
		adviseUseridLabel = new JLabel();
		useridLabel = new JLabel();
		useridTextField = new JTextField();
		projectNameTextField = new JTextField();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		// ======== this ========
		setTitle(translator.get("NewProjectDialogTitle"));
		setResizable(false);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		headerPanel.setBackground(Color.white);
		headerPanel.setLayout(new FlowLayout());

		try {
			jakeIconLabel.setIcon(new ImageIcon(new ClassPathResource(
					"jake.gif").getURL()));
		} catch (IOException e1) {
			log.warn("image icon not found.");
		}
		jakeIconLabel.setBackground(Color.white);
		jakeIconLabel.setText(translator.get("Welcome to Jake!"));
		jakeIconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
		jakeIconLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		jakeIconLabel.setAlignmentX(5.0F);
		jakeIconLabel.setAlignmentY(5.5F);
		headerPanel.add(jakeIconLabel);

		contentPane.add(headerPanel, BorderLayout.NORTH);

		// ======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			// ======== contentPanel ========
			{
				contentPanel.setLayout(new TableLayout(
					new double[][] {
						{ TableLayout.PREFERRED, 246, 64 },
						{ TableLayout.PREFERRED, TableLayout.PREFERRED, 
						  TableLayout.PREFERRED, TableLayout.PREFERRED,
						  TableLayout.PREFERRED, TableLayout.PREFERRED,
						  TableLayout.PREFERRED, TableLayout.PREFERRED 
						} 
					}
				));
				((TableLayout) contentPanel.getLayout()).setHGap(2);
				((TableLayout) contentPanel.getLayout()).setVGap(15);

				// ---- infoLabel ----
				infoLabel.setText(translator.get("NewProjectDialogInfo"));
				contentPanel.add(infoLabel, new TableLayoutConstraints(0, 0, 2,
						0, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- selectFolderLabel ----
				selectFolderLabel.setText(translator
						.get("NewProjectDialogAdviseSelectFolder"));
				contentPanel.add(selectFolderLabel, new TableLayoutConstraints(
						0, 1, 1, 1, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- folderLabel ----
				folderLabel.setText(translator.get("NewProjectDialogFolderLabel"));
				folderLabel.setIcon(UIManager
						.getIcon("FileChooser.newFolderIcon"));
				contentPanel.add(folderLabel, new TableLayoutConstraints(0, 2,
						0, 2, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				folderTextField.setEditable(false);
				contentPanel.add(folderTextField, new TableLayoutConstraints(1,
						2, 1, 2, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- folderSelectButton ----
				folderSelectButton.setText(translator.get("NewProjectDialogFolderSelectButton"));
				folderSelectButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						folderSelectActionPerformed(event);
					}
				});
				contentPanel.add(folderSelectButton,
						new TableLayoutConstraints(2, 2, 2, 2,
								TableLayoutConstraints.FULL,
								TableLayoutConstraints.FULL));

				// ---- nameProjectLabel ----
				adviseNameProjectLabel.setText(translator
						.get("NewProjectDialogAdviseNameProject"));
				contentPanel.add(adviseNameProjectLabel, new TableLayoutConstraints(
						0, 3, 2, 3, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));

				// ---- adviseProjectNameLabel ----
				adviseProjectNameLabel.setText(translator
						.get("NewProjectDialogProjectName"));
				contentPanel.add(adviseProjectNameLabel, new TableLayoutConstraints(
						0, 4, 0, 4, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				contentPanel.add(projectNameTextField,
						new TableLayoutConstraints(1, 4, 2, 4,
								TableLayoutConstraints.FULL,
								TableLayoutConstraints.FULL));
				// ---- adviseUseridLabel ----
				adviseUseridLabel.setText(translator
						.get("NewProjectDialogAdviseUserid", 
						JakeGuiAccess.getICSName()));
				contentPanel.add(adviseUseridLabel, new TableLayoutConstraints(
						0, 5, 2, 5, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				
				// ---- useridLabel ----
				useridLabel.setText(translator
						.get("NewProjectDialogUserid"));
				contentPanel.add(useridLabel, new TableLayoutConstraints(
						0, 6, 0, 6, TableLayoutConstraints.FULL,
						TableLayoutConstraints.FULL));
				contentPanel.add(useridTextField,
						new TableLayoutConstraints(1, 6, 2, 6,
								TableLayoutConstraints.FULL,
								TableLayoutConstraints.FULL));
			}
			dialogPane.add(contentPanel, BorderLayout.NORTH);

			// ======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] {
						0, 85, 80 };
				((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {
						1.0, 0.0, 0.0 };

				// ---- cancelButton ----
				cancelButton.setText(translator.get("ButtonClose"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						cancelButtonActionPerformed(event);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1,
						0.0, 0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 0), 0, 0));

				// ---- okButton ----
				okButton.setText(translator
						.get("NewProjectDialogCreateOpenProject"));
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent event) {
						okButtonActionPerformed(event);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0,
						0.0, GridBagConstraints.CENTER,
						GridBagConstraints.BOTH, new Insets(0, 0, 0, 5), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel headerPanel;
	private JLabel jakeIconLabel;
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel infoLabel;
	private JLabel selectFolderLabel;
	private JLabel folderLabel;
    private JLabel useridLabel;
	private JTextField folderTextField;
	private JTextField useridTextField;
	private JButton folderSelectButton;
	private JLabel adviseNameProjectLabel;
	private JLabel adviseProjectNameLabel;
	private JLabel adviseUseridLabel;
	private JTextField projectNameTextField;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
