package com.doublesignal.sepm.jake.gui;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Font;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TextTranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;


/**
 * @author Peter Steinberger, Simon
 */
@SuppressWarnings("serial")
public class ResolveConflictDialog extends JDialog {
	private static final Logger log = Logger.getLogger(ResolveConflictDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

    private FileObject localFile, remoteFile;
    private IJakeGuiAccess jakeGuiAccess;


    /**
     * Setup the dialog and load all necessary data, i.e. the local and remote file.
     * @param owner
     * @param localObject
     * @param jakeGuiAccess
     */
    public ResolveConflictDialog(Frame owner, FileObject localObject, IJakeGuiAccess jakeGuiAccess) {
		super(owner);
        this.localFile = localObject;
        this.jakeGuiAccess = jakeGuiAccess;
        
        //load the remote file...
        remoteFile = this.jakeGuiAccess.pullRemoteFile(localFile);
        
		initComponents();
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		if (useRemoteFileRadio.isSelected()) {
			//keep remote file
		} else {
			//keep local file
		}
	}


	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		mainPanel = new JPanel();
		localLabel = new JLabel();
		remoteLabel = new JLabel();
		lastEditorLabel = new JLabel();
		localEditorLabel = new JLabel();
		remoteEditorLabel = new JLabel();
		lastEditLabel = new JLabel();
		localFileEditTimeLabel = new JLabel();
		remoteFileEditTimeLabel = new JLabel();
		fileSizeLabel = new JLabel();
		localFileSizeLabel = new JLabel();
		remoteFileSizeLabel = new JLabel();
		openLocalFileButton = new JButton();
		openRemoteFileButton = new JButton();
		useLocalFileRadio = new JRadioButton();
		useRemoteFileRadio = new JRadioButton();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		headerPanel = new JPanel();
		headerLabel = new JLabel();
		filenameLabel = new JLabel();

		//======== this ========
		setTitle("Resolve Conflict");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout(0, 10));

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

				//======== main panel ========
				{
					mainPanel.setLayout(new TableLayout(new double[][] {
						{132, 121, 134},
						{26, TableLayout.PREFERRED, 19, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
					((TableLayout)mainPanel.getLayout()).setHGap(5);
					((TableLayout)mainPanel.getLayout()).setVGap(5);

					//---- local ----
					localLabel.setText(translator.get("ConflictDialogLocal"));
					localLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					mainPanel.add(localLabel, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- remote ----
					remoteLabel.setText(translator.get("ConflictDialogRemote"));
					remoteLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					mainPanel.add(remoteLabel, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- last edit ----
					lastEditorLabel.setText(translator.get("ConflictDialogLastEditor"));
					mainPanel.add(lastEditorLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					try {
						//---- local editor ----
						localEditorLabel.setText(jakeGuiAccess.getLastModifier(localFile).getUserId());
						mainPanel.add(localEditorLabel, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- remote editor ----
						remoteEditorLabel.setText(jakeGuiAccess.getLastModifier(remoteFile).getUserId());
						mainPanel.add(remoteEditorLabel, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

						//---- last edited ----
						lastEditLabel.setText(translator.get("ConflictDialogLastEdit"));
						mainPanel.add(lastEditLabel, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

						//---- local last edited ----
						localFileEditTimeLabel.setText(jakeGuiAccess.getLastModified(localFile).toString());
						mainPanel.add(localFileEditTimeLabel, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

						//---- remote last edited ----
						remoteFileEditTimeLabel.setText(jakeGuiAccess.getLastModified(remoteFile).toString());
						mainPanel.add(remoteFileEditTimeLabel, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
					} catch (NoSuchLogEntryException e2) {
						log.warn("no such logentry: " + e2.getMessage());
						log.info("closing window");
						this.setVisible(false);
					}

					//---- file size ----
					fileSizeLabel.setText(translator.get("ConflictDialogFileSize"));
					mainPanel.add(fileSizeLabel, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- local file size ----
					localFileSizeLabel.setText(FilesLib.getHumanReadableFileSize(jakeGuiAccess.getFileSize(localFile)));
					mainPanel.add(localFileSizeLabel, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- remote file size ----
					remoteFileSizeLabel.setText(FilesLib.getHumanReadableFileSize(jakeGuiAccess.getFileSize(remoteFile)));
					mainPanel.add(remoteFileSizeLabel, new TableLayoutConstraints(2, 3, 2, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- open local ----
					openLocalFileButton.setText(translator.get("ConflictDialogOpen"));
					openLocalFileButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								jakeGuiAccess.launchFile(localFile.getName());
							} catch (Exception e1) {
								log.warn("Failed to launch file: " + e1.getMessage());
							}
						}
					});
					mainPanel.add(openLocalFileButton, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- open remote ----
					openRemoteFileButton.setText(translator.get("ConflictDialogOpen"));
					openRemoteFileButton.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							try {
								jakeGuiAccess.launchFile(remoteFile.getName());
							} catch (Exception e1) {
								log.warn("Failed to launch file: " + e1.getMessage());
							}
						}
					});
					mainPanel.add(openRemoteFileButton, new TableLayoutConstraints(2, 4, 2, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- use local ----
					useLocalFileRadio.setText(translator.get("ConflictDialogUseLocalFile"));
					useLocalFileRadio.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							okButton.setEnabled(true);
						}
					});
					mainPanel.add(useLocalFileRadio, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- use remote ----
					useRemoteFileRadio.setText(translator.get("ConflictDialogUseRemoteFile"));
					useRemoteFileRadio.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							okButton.setEnabled(true);
						}
					});
					mainPanel.add(useRemoteFileRadio, new TableLayoutConstraints(2, 5, 2, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
					
					//---- group
					ButtonGroup group = new ButtonGroup();
					group.add(useLocalFileRadio);
					group.add(useRemoteFileRadio);
				}
				contentPanel.add(mainPanel);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- cancelButton ----
				cancelButton.setText(translator.get("ConflictDialogCancel"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});	
				buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				//---- okButton ----
				okButton.setText(translator.get("ConflictDialogOK"));
				okButton.setEnabled(false);
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});
				buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));

			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//======== header ========
			{
				headerPanel.setLayout(new BorderLayout());

				//---- header ----
				headerLabel.setText(translator.get("ConflictDialogHeader"));
				headerPanel.add(headerLabel, BorderLayout.WEST);

				//---- filename ----
				filenameLabel.setText(localFile.getName());
				filenameLabel.setFont(new Font("Lucida Grande", Font.BOLD, 13));
				headerPanel.add(filenameLabel, BorderLayout.CENTER);
			}
			dialogPane.add(headerPanel, BorderLayout.NORTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - tester tester
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JPanel mainPanel;
	private JLabel localLabel;
	private JLabel remoteLabel;
	private JLabel lastEditorLabel;
	private JLabel localEditorLabel;
	private JLabel remoteEditorLabel;
	private JLabel lastEditLabel;
	private JLabel localFileEditTimeLabel;
	private JLabel remoteFileEditTimeLabel;
	private JLabel fileSizeLabel;
	private JLabel localFileSizeLabel;
	private JLabel remoteFileSizeLabel;
	private JButton openLocalFileButton;
	private JButton openRemoteFileButton;
	private JRadioButton useLocalFileRadio;
	private JRadioButton useRemoteFileRadio;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel headerPanel;
	private JLabel headerLabel;
	private JLabel filenameLabel;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
