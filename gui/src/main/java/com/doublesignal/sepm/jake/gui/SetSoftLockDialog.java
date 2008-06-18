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
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.LogAction;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

@SuppressWarnings("serial")
public class SetSoftLockDialog extends JDialog {

	private static final Logger log = Logger.getLogger(SendMessageDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

	private IJakeGuiAccess jakeGuiAccess;
	private JakeObject file;
	
	private JPanel dialogPanel;
	private JPanel contentPanel;
	private JScrollPane lockCommentPane;
	private JTextArea lockCommentTextArea;
	private JPanel topPanel;
	private JLabel lockOwnerLabel;
	private JCheckBox lockCheckBox;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;

	public SetSoftLockDialog(Frame owner, IJakeGuiAccess guiAccess, JakeObject file) {
		super(owner);
		this.jakeGuiAccess = guiAccess;
		this.file = file;
		setModal(true);
		initComponents();
	}

	private void overrideButtonActionPerformed(ActionEvent e) {
		if (lockCheckBox.isSelected()) {
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
	
	private void fillInData() {
		LogEntry log = jakeGuiAccess.getJakeObjectLockLogEntry(file);
		if (log != null) {
			lockCommentTextArea.setText(log.getComment());

			
			if (log.getUserId().equals(jakeGuiAccess.getLoginUserid())) {
				lockOwnerLabel.setText(translator.get("SetSoftLockDialogLockedByYouLabel"));
			} else {
				lockOwnerLabel.setText(translator.get("SetSoftLockDialogLockedByLabel") + log.getUserId());
			}

			lockCheckBox.setSelected(log.getAction() == LogAction.LOCK);
			if (log.getAction() == LogAction.LOCK) {
				okButton.setText(translator.get("SetSoftLockDialogOverrideLockButton"));
				return;
			}
		}
		okButton.setText(translator.get("ButtonConfirm"));
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
		okButton = new JButton();
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
					lockCommentTextArea.setLineWrap(true);
					lockCommentPane.setViewportView(lockCommentTextArea);
				}
				contentPanel.add(lockCommentPane, BorderLayout.CENTER);

				//======== topPanel ========
				{
					topPanel.setLayout(new BorderLayout());
					topPanel.add(lockOwnerLabel, BorderLayout.NORTH);

					//---- checkBox ----
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
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						overrideButtonActionPerformed(e);
					}
				});	
				buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPanel.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPanel, BorderLayout.CENTER);
		pack();
		fillInData();
		updateLockCommentPane();
		setLocationRelativeTo(getOwner());
	}
}
