package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.swing.*;
import javax.swing.event.DocumentListener;
import javax.swing.event.DocumentEvent;
import javax.swing.border.*;
import info.clearthought.layout.*;
import org.apache.log4j.Logger;
import com.doublesignal.sepm.jake.fss.InvalidFilenameException;
import com.doublesignal.sepm.jake.fss.NotADirectoryException;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class NewProjectDialog extends JDialog {
	private static Logger log = Logger.getLogger(NewProjectDialog.class);


    private JakeGui jakeGui;
    private String projectFolderString;
    private String projectNameString;
    private boolean projectNameOk;
    private boolean projectFolderOk;


    public NewProjectDialog(Frame owner,JakeGui jakeGui) {
        super(owner);
        this.jakeGui = jakeGui;
        initComponents();
        checkSaveButtonAvailable();
    }

    public NewProjectDialog(Dialog owner) {
		super(owner);
		initComponents();
	}


    private void reset()
    {
        projectFolderString = "invalid";
        projectFolderOk = false;
        projectNameOk = false;
        jakeProjectFolderTextField.setText("<Please select Folder for JakeProject>");
    }


    private void okButtonActionPerformed(ActionEvent e) {
        try {
            jakeGui.createProject(projectNameString,projectFolderString);
        } catch (InvalidFilenameException e1) {
            projectFolderOk = false;
            jakeProjectFolderTextField.setText("<The path you specified is invalid>");
        } catch (NotADirectoryException e1) {
            projectFolderOk = false;
            jakeProjectFolderTextField.setText("<The path you specified is not a folder>");
        } catch (IOException e1) {
            projectFolderOk = false;
            jakeProjectFolderTextField.setText("<Couldn't read the specified path. Are the permissions ok?>");
        }
        checkSaveButtonAvailable();
        if(projectFolderOk)
        {
            this.setVisible(false);
        }

    }

    private void cancelButtionActionPerformed(ActionEvent e)
    {
        this.setVisible(false);
    }


    private void selectorButtonActionPerformed(ActionEvent e)
    {
        //log.debug("calling selectorButtonActionPerformed ");
        String sUserWorkDir = System.getProperty("user.dir");


        JFileChooser fileChooser = new JFileChooser(sUserWorkDir);
        fileChooser.setMultiSelectionEnabled(false);
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        int returnCode = fileChooser.showOpenDialog(null);
        if(returnCode == JFileChooser.APPROVE_OPTION)
        {
            this.jakeProjectFolderTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
        }

    }

    private void projectFolderTextFieldActionPerformed(DocumentEvent e)
    {
        //log.debug("inside projectFolderTextFieldActionPerformed");
        File file = new File(jakeProjectFolderTextField.getText());
        if(file.isDirectory())
        {
            projectFolderString = jakeProjectFolderTextField.getText();
            jakeProjectFolderTextField.setForeground(Color.DARK_GRAY);
            projectFolderOk = true;
        }
        else
        {
            projectFolderString = "invalid";
            projectFolderOk = false;
            jakeProjectFolderTextField.setForeground(Color.RED);
        }
        checkSaveButtonAvailable();
    }


    private void checkSaveButtonAvailable()
    {
        //log.debug("inside checkSaveButtonAvailable");
        if (projectFolderOk && projectNameOk)
            okButton.setEnabled(true);
        else
            okButton.setEnabled(false);
    }


    private void projectNameTextFieldActionPerformed(DocumentEvent e)
    {
        //log.debug("calling projectNameTextFieldActionPerformed");
        projectNameString = jakeProjectNameTextField.getText();
        if(projectNameString.length() < 2 || projectNameString.length() > 50) // according to [projectName]
        {
            projectNameOk = false;
            jakeProjectNameTextField.setForeground(Color.RED);
        }
        else
        {
            projectNameOk = true;
            jakeProjectNameTextField.setForeground(Color.DARK_GRAY);
        }
        checkSaveButtonAvailable();
    }


    private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		jakeProjectNameLabel = new JLabel();
		jakeProjectNameTextField = new JTextField();
		jakeProjectFolderLabel = new JLabel();
		jakeProjectFolderTextField = new JTextField();
		jakeProjectFolderSelectorButton = new JButton();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();


        reset();


        //======== this ========
		setTitle("Start a new Project");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
                jakeProjectFolderTextField.getDocument().addDocumentListener(new DocumentListener()
                {
                    public void changedUpdate(DocumentEvent event) {
                        projectFolderTextFieldActionPerformed(event);
                    }

                    public void insertUpdate(DocumentEvent event) {
                        projectFolderTextFieldActionPerformed(event);
                    }

                    public void removeUpdate(DocumentEvent event) {
                        projectFolderTextFieldActionPerformed(event);
                    }
                });

                contentPanel.setLayout(new TableLayout(new double[][] {
					{TableLayout.PREFERRED, 256, TableLayout.PREFERRED},
					{TableLayout.FILL, TableLayout.PREFERRED}}));

				//---- jakeProjectNameLabel ----
				jakeProjectNameLabel.setText("Name:");
                jakeProjectNameTextField.getDocument().addDocumentListener(new DocumentListener()
                {
                    public void insertUpdate(DocumentEvent event) {
                        projectNameTextFieldActionPerformed(event);
                    }

                    public void removeUpdate(DocumentEvent event) {
                        projectNameTextFieldActionPerformed(event);
                    }

                    public void changedUpdate(DocumentEvent event) {
                        projectNameTextFieldActionPerformed(event);
                    }
                }
                );
                contentPanel.add(jakeProjectNameLabel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(jakeProjectNameTextField, new TableLayoutConstraints(1, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- jakeProjectFolderLabel ----
				jakeProjectFolderLabel.setText("Folder:");
				contentPanel.add(jakeProjectFolderLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(jakeProjectFolderTextField, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));



                //---- jakeProjectFolderSelectorButton ----
				jakeProjectFolderSelectorButton.setText("...");
				contentPanel.add(jakeProjectFolderSelectorButton, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
                jakeProjectFolderSelectorButton.addActionListener( new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        selectorButtonActionPerformed(event);                    
                    }
                }
                );

            }
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton ----
				okButton.setText("Save");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});	
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- cancelButton ----
				cancelButton.setText("Cancel");
				cancelButton.addActionListener(new ActionListener()
                {
                    public void actionPerformed(ActionEvent event)
                    {
                        cancelButtionActionPerformed(event);
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
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel jakeProjectNameLabel;
	private JTextField jakeProjectNameTextField;
	private JLabel jakeProjectFolderLabel;
	private JTextField jakeProjectFolderTextField;
	private JButton jakeProjectFolderSelectorButton;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
