package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TextTranslationProvider;

public class SetSoftLockDialog extends JDialog {

	private IJakeGuiAccess jakeGuiAccess;
	private FileObject file;

	private static Logger log = Logger.getLogger(SendMessageDialog.class);
	private static ITranslationProvider translator = new TextTranslationProvider();
	
	private JPanel dialogPanel;
	private JPanel contentPanel;
	private JScrollPane lockCommentPane;
	private JTextArea lockCommentTextArea;
	private JPanel topPanel;
	private JLabel lockOwnerLabel;
	private JCheckBox lockCheckBox;
	private JPanel buttonBar;
	private JButton overrideButton;
	private JButton cancelButton;

	public SetSoftLockDialog(Frame owner, IJakeGuiAccess guiAccess, FileObject file) {
		super(owner);
		this.jakeGuiAccess = guiAccess;
		this.file = file;
		initComponents();
	}

	private void overrideButtonActionPerformed(ActionEvent e) {
		if (lockCheckBox.isSelected()) {
			jakeGuiAccess.setJakeObjectLock(file, true);
			jakeGuiAccess.setJakeObjectLockComment(file, lockCommentTextArea.getText());
		} else {
			jakeGuiAccess.setJakeObjectLock(file, false);
		}
		this.setVisible(false);
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	private void updateLockCommentPane() {
		lockCommentTextArea.setEnabled(lockCheckBox.isSelected());
	}
	
	private void lockCheckBoxActionPerformed(ActionEvent e) {
		updateLockCommentPane();
	}
	
	private void initComponents() {
		
		dialogPanel = new JPanel();
		contentPanel = new JPanel();
		lockCommentPane = new JScrollPane();
		lockCommentTextArea = new JTextArea();
		topPanel = new JPanel();
		lockOwnerLabel = new JLabel();
		lockCheckBox = new JCheckBox();
		buttonBar = new JPanel();
		overrideButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle(translator.get("SetSoftLockDialogTitle"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPanel ========
		{
			dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPanel.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(0, 3));
				contentPanel.setPreferredSize(new Dimension(320, 240));

				//======== lockCommentPane ========
				{
					//---- lockCommentTextArea ----
					lockCommentTextArea.setText(jakeGuiAccess.getJakeObjectLockComment(file));
					lockCommentTextArea.setLineWrap(true);
					lockCommentPane.setViewportView(lockCommentTextArea);
				}
				contentPanel.add(lockCommentPane, BorderLayout.CENTER);

				//======== topPanel ========
				{
					topPanel.setLayout(new BorderLayout());
					String lockOwner = jakeGuiAccess.getJakeObjectLockedBy(file).getUserId();
					if (lockOwner.equals(jakeGuiAccess.getLoginUserid())) {
						lockOwnerLabel.setText(translator.get("SetSoftLockDialogLockedByYouLabel"));
					} else {
						lockOwnerLabel.setText(translator.get("SetSoftLockDialogLockedByLabel") + lockOwner);
					}
					topPanel.add(lockOwnerLabel, BorderLayout.NORTH);

					//---- checkBox ----
					lockCheckBox.setSelected(jakeGuiAccess.getJakeObjectLock(file));
					lockCheckBox.setText(translator.get("SetSoftLockDialogActivateLabel"));
					lockCheckBox.addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							lockCheckBoxActionPerformed(e);
						}
					});
					topPanel.add(lockCheckBox, BorderLayout.WEST);
				}
				contentPanel.add(topPanel, BorderLayout.NORTH);
			}
			dialogPanel.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- cancelButton ----
				cancelButton.setText(translator.get("ButtonCancel"));
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				//---- overrideButton ----
				overrideButton.setText(translator.get("SetSoftLockDialogOverrideLockButton"));
				overrideButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						overrideButtonActionPerformed(e);
					}
				});	
				buttonBar.add(overrideButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPanel.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPanel, BorderLayout.CENTER);
		pack();
		updateLockCommentPane();
		setLocationRelativeTo(getOwner());
	}
}
