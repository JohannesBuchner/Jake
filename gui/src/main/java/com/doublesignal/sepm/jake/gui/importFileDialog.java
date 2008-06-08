package com.doublesignal.sepm.jake.gui;

import org.apache.log4j.Logger;
import org.springframework.core.io.ClassPathResource;

import javax.swing.*;
import javax.swing.event.CaretListener;
import javax.swing.event.CaretEvent;
import java.awt.event.*;
import java.awt.*;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.io.IOException;
import java.io.File;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

public class importFileDialog extends JDialog {
    private JPanel mainPanel;
    private JButton importButton;
    private JButton cancelButton;
    private JPanel topPanel;
    private JPanel buttonPanel;
    private JButton browseFileButton;
    private JLabel jakeIconLabel;
    private JTextField destinationFolderTextField;
    private JButton destinationFolderButton;
    private JTextField fileTextField;
    private JButton fileButton;
    private JLabel destinationFolderLabel;
    private JLabel fileLabel;
    private JPanel importFilePanel;
    private JPanel destinationFolderPanel;
    private JPanel middlePanel;
    private JFrame parent;
    private IJakeGuiAccess jakeGuiAccess;

    public IJakeGuiAccess getJakeGuiAccess() {
        return jakeGuiAccess;
    }

    public importFileDialog setJakeGuiAccess(IJakeGuiAccess jakeGuiAccess) {
        this.jakeGuiAccess = jakeGuiAccess;
        return this;
    }

    public importFileDialog(Dialog dialog) {
        super(dialog);
        setModal(true);
        setResizable(false);
        getRootPane().setDefaultButton(importButton);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private Logger log = Logger.getLogger(importFileDialog.class);

    public importFileDialog(JFrame owner) {
        super(owner);
        $$$setupUI$$$();
        createUIComponents();
        addListeners();


        setContentPane(mainPanel);
        setModal(true);
        setResizable(false);
        getRootPane().setDefaultButton(importButton);
        pack();
        setLocationRelativeTo(getOwner());
    }

    private void onCancel() {
        dispose();
    }

    private void createUIComponents() {
        try {
            jakeIconLabel.setIcon(new ImageIcon(new ClassPathResource(
                    "jake.gif").getURL()));
        } catch (IOException e1) {
            log.warn("image icon not found.");
        }
        jakeIconLabel.setBackground(Color.white);
        jakeIconLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        jakeIconLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
        jakeIconLabel.setAlignmentX(5.0F);
        jakeIconLabel.setAlignmentY(5.5F);
        topPanel.add(jakeIconLabel);
    }

    private void addListeners() {
        browseFileButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                browseFileButtonActionPerformed(event);
            }
        });
        fileTextField.addCaretListener(new CaretListener() {
            public void caretUpdate(CaretEvent event) {
                checkFields();
            }
        });
        destinationFolderTextField.addPropertyChangeListener(new PropertyChangeListener() {
            public void propertyChange(PropertyChangeEvent event) {
                //checkFields();
            }
        });
        destinationFolderButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                destinationFolderButtonActionPerformed(event);
            }
        });
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                cancelButtonActionPerfomed(event);
            }
        });
        importButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                importButtonActionPerformed(event);
            }
        });

// call onCancel() when cross is clicked
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                onCancel();
            }
        });

// call onCancel() on ESCAPE
        mainPanel.registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                onCancel();
            }
        }, KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
    }

    public importFileDialog() {
        $$$setupUI$$$();
        createUIComponents();
        addListeners();
    }

    public importFileDialog(Frame parent) {
        $$$setupUI$$$();
        createUIComponents();
        addListeners();
    }

    private void importButtonActionPerformed(ActionEvent event) {
        if (jakeGuiAccess.importLocalFileIntoProject(fileTextField.getText(), destinationFolderTextField.getText())) {
            UserDialogHelper.inform(this, "File successfully imported",
                    "Your file was successfully imported into your project!"
            );
            onCancel();
        } else {
            UserDialogHelper.error(this, "File not imported!",
                    "An Error occured during importing of the file into your project!\n" +
                            "Please check your permissions on the file and the project folder and \n" +
                            "the there is enough available disk space to copy it into the project folder!"
            );
        }
    }

    private void cancelButtonActionPerfomed(ActionEvent event) {
        onCancel();
    }

    private void destinationFolderButtonActionPerformed(ActionEvent event) {
        log.debug("calling destinationFolderButtonActionPerformed");
        final String projectRootPath = jakeGuiAccess.getProject().getRootPath().getAbsolutePath();
        JFileChooser fileChooser = new JFileChooser(projectRootPath + File.separator + ".") {

            public void setCurrentDirectory(File file) {
                if (file.getAbsolutePath().startsWith(projectRootPath)) {
                    super.setCurrentDirectory(file);
                } else {
                    File selectedFile = getSelectedFile();
                    if (selectedFile != null && selectedFile.getAbsolutePath().equals(projectRootPath)) {
                        super.setCurrentDirectory(file);
                    } else {
                        log.debug("a: " + file.getAbsolutePath());
                        log.debug("b: " + projectRootPath);
                        UserDialogHelper.warning(this, "No valid directory",
                                "Sorry, you can only select a folder within the current project!");
                    }
                }
            }
        };
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        int returncode = fileChooser.showDialog(this, "Copy file to this directory");
        if (returncode == JFileChooser.APPROVE_OPTION) {
            String rootPath = fileChooser.getSelectedFile().getAbsolutePath();
            destinationFolderTextField.setText(rootPath);
            checkFields();
        }
    }

    private void checkFields() {
        if (jakeGuiAccess == null)
            return;

        String projectRootPath = jakeGuiAccess.getProject().getRootPath().getAbsolutePath();
        String fileString = fileTextField.getText();
        String targetDirectoryString = destinationFolderTextField.getText();
        boolean fileOk = false;
        boolean folderOk = false;

        // initialize
        fileTextField.setBackground(Color.white);
        destinationFolderTextField.setBackground(Color.white);

        if (!fileString.isEmpty()) {
            File importFile = new File(fileString);
            if (importFile.exists() && importFile.isFile() && !importFile.getAbsolutePath().startsWith(projectRootPath))
                fileOk = true;
            if (!fileOk)
                fileTextField.setBackground(Color.red);
        }

        if (!targetDirectoryString.isEmpty()) {
            File targetDirectoryFile = new File(targetDirectoryString);
            if (targetDirectoryFile.exists() && targetDirectoryFile.isDirectory()
                    &&
                    (
                            targetDirectoryFile.getAbsolutePath().startsWith(projectRootPath) ||
                                    targetDirectoryFile.getAbsolutePath().equals(projectRootPath)
                    )
                    )
                folderOk = true;

            if (!folderOk)
                destinationFolderTextField.setBackground(Color.red);
        }

        importButton.setEnabled(fileOk && folderOk);
    }

    private void browseFileButtonActionPerformed(ActionEvent event) {
        log.debug("calling browseFileButtonActionPerformed");
        final String projectRootPath = jakeGuiAccess.getProject().getRootPath().getAbsolutePath();

        JFileChooser fileChooser = new JFileChooser() {
            public void setCurrentDirectory(File file) {

                if (file != null && file.getAbsolutePath().startsWith(projectRootPath)) {
                    UserDialogHelper.warning(this, "File must be outside the project folder",
                            "Sorry, the file you want to import must be outside of the project folder.\n" +
                                    "If the file is already in the project folder, use the popup menu in\n" +
                                    "the filelist to import it into the project!");
                } else {
                    super.setCurrentDirectory(file);
                }
            }
        };
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returncode = fileChooser.showDialog(this, "Import this file");
        if (returncode == JFileChooser.APPROVE_OPTION) {
            String rootPath = fileChooser.getSelectedFile().getAbsolutePath();
            fileTextField.setText(rootPath);
            checkFields();
        }
    }

    /**
     * Method generated by IntelliJ IDEA GUI Designer
     * >>> IMPORTANT!! <<<
     * DO NOT edit this method OR call it in your code!
     *
     * @noinspection ALL
     */
    private void $$$setupUI$$$() {
        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(0, 0));
        mainPanel.setMaximumSize(new Dimension(500, 250));
        mainPanel.setMinimumSize(new Dimension(500, 250));
        mainPanel.setOpaque(true);
        mainPanel.setPreferredSize(new Dimension(500, 250));
        mainPanel.setVerifyInputWhenFocusTarget(true);
        mainPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0), null));
        buttonPanel = new JPanel();
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setMinimumSize(new Dimension(180, 45));
        buttonPanel.setPreferredSize(new Dimension(180, 45));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        importButton = new JButton();
        importButton.setEnabled(false);
        importButton.setMaximumSize(new Dimension(90, 35));
        importButton.setMinimumSize(new Dimension(90, 35));
        importButton.setPreferredSize(new Dimension(90, 35));
        importButton.setText("import");
        importButton.setMnemonic('I');
        importButton.setDisplayedMnemonicIndex(0);
        GridBagConstraints gbc;
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(importButton, gbc);
        final JPanel spacer1 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(spacer1, gbc);
        final JPanel spacer2 = new JPanel();
        gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.fill = GridBagConstraints.VERTICAL;
        buttonPanel.add(spacer2, gbc);
        cancelButton = new JButton();
        cancelButton.setMaximumSize(new Dimension(90, 35));
        cancelButton.setMinimumSize(new Dimension(90, 35));
        cancelButton.setPreferredSize(new Dimension(90, 35));
        cancelButton.setText("Cancel");
        cancelButton.setMnemonic('C');
        cancelButton.setDisplayedMnemonicIndex(0);
        gbc = new GridBagConstraints();
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        buttonPanel.add(cancelButton, gbc);
        topPanel = new JPanel();
        topPanel.setLayout(new BorderLayout(0, 0));
        topPanel.setBackground(Color.white);
        topPanel.setPreferredSize(new Dimension(143, 90));
        mainPanel.add(topPanel, BorderLayout.NORTH);
        jakeIconLabel = new JLabel();
        jakeIconLabel.setFont(new Font(jakeIconLabel.getFont().getName(), Font.BOLD, 20));
        jakeIconLabel.setHorizontalAlignment(2);
        jakeIconLabel.setHorizontalTextPosition(11);
        jakeIconLabel.setText("Import a file");
        topPanel.add(jakeIconLabel, BorderLayout.WEST);
        middlePanel = new JPanel();
        middlePanel.setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
        middlePanel.setMaximumSize(new Dimension(530, 50));
        middlePanel.setMinimumSize(new Dimension(530, 50));
        middlePanel.setPreferredSize(new Dimension(530, 50));
        mainPanel.add(middlePanel, BorderLayout.CENTER);
        importFilePanel = new JPanel();
        importFilePanel.setLayout(new BorderLayout(0, 0));
        importFilePanel.setMaximumSize(new Dimension(2147483647, 40));
        importFilePanel.setMinimumSize(new Dimension(480, 40));
        importFilePanel.setPreferredSize(new Dimension(480, 40));
        middlePanel.add(importFilePanel);
        fileLabel = new JLabel();
        fileLabel.setPreferredSize(new Dimension(150, 17));
        fileLabel.setText("File to import");
        fileLabel.setDisplayedMnemonic('F');
        fileLabel.setDisplayedMnemonicIndex(0);
        importFilePanel.add(fileLabel, BorderLayout.WEST);
        fileTextField = new JTextField();
        fileTextField.setMinimumSize(new Dimension(100, 23));
        fileTextField.setPreferredSize(new Dimension(100, 23));
        importFilePanel.add(fileTextField, BorderLayout.CENTER);
        browseFileButton = new JButton();
        browseFileButton.setPreferredSize(new Dimension(100, 27));
        browseFileButton.setText("Browse");
        browseFileButton.setMnemonic('B');
        browseFileButton.setDisplayedMnemonicIndex(0);
        importFilePanel.add(browseFileButton, BorderLayout.EAST);
        destinationFolderPanel = new JPanel();
        destinationFolderPanel.setLayout(new BorderLayout(0, 0));
        destinationFolderPanel.setMaximumSize(new Dimension(480, 40));
        destinationFolderPanel.setMinimumSize(new Dimension(480, 40));
        destinationFolderPanel.setPreferredSize(new Dimension(480, 40));
        middlePanel.add(destinationFolderPanel);
        destinationFolderLabel = new JLabel();
        destinationFolderLabel.setPreferredSize(new Dimension(150, 17));
        destinationFolderLabel.setText("Destination folder");
        destinationFolderLabel.setDisplayedMnemonic('D');
        destinationFolderLabel.setDisplayedMnemonicIndex(0);
        destinationFolderPanel.add(destinationFolderLabel, BorderLayout.WEST);
        destinationFolderTextField = new JTextField();
        destinationFolderTextField.setMinimumSize(new Dimension(150, 23));
        destinationFolderTextField.setPreferredSize(new Dimension(150, 23));
        destinationFolderTextField.setText("");
        destinationFolderPanel.add(destinationFolderTextField, BorderLayout.CENTER);
        destinationFolderButton = new JButton();
        destinationFolderButton.setPreferredSize(new Dimension(100, 27));
        destinationFolderButton.setText("Select");
        destinationFolderButton.setMnemonic('S');
        destinationFolderButton.setDisplayedMnemonicIndex(0);
        destinationFolderPanel.add(destinationFolderButton, BorderLayout.EAST);
        fileLabel.setLabelFor(fileTextField);
        destinationFolderLabel.setLabelFor(destinationFolderTextField);
    }

    /**
     * @noinspection ALL
     */
    public JComponent $$$getRootComponent$$$() {
        return mainPanel;
    }
}
