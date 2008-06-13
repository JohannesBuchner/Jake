package com.doublesignal.sepm.jake.gui;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchProjectMemberException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;
import com.doublesignal.sepm.jake.ics.exceptions.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.*;
import javax.swing.border.*;

import org.apache.log4j.Logger;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class SendMessageDialog extends JDialog {
	private static final Logger log = Logger.getLogger(SendMessageDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

	private IJakeGuiAccess jakeGuiAccess;
	private String recipient;
	
	public SendMessageDialog(Frame owner, String recipient, IJakeGuiAccess guiAccess) {
		super(owner);
		this.jakeGuiAccess = guiAccess;
		this.recipient = recipient;
		initComponents();
	}

	public SendMessageDialog(Dialog owner, String recipient, IJakeGuiAccess guiAccess) {
		super(owner);
		this.jakeGuiAccess = guiAccess;
		this.recipient = recipient;
		initComponents();
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		try {
			if("".equals(this.messageTextArea.getText())) {
				JOptionPane.showMessageDialog(this,
						translator.get("SendMessageDialogMessageEmptyMessageText"),
						translator.get("SendMessageDialogMessageEmptyMessageTitle"),
						JOptionPane.ERROR_MESSAGE);
				return;
			}
			ProjectMember jmRecipient = jakeGuiAccess.getProjectMember(recipient);
			// TODO: Is this REALLY the value we want?
			ProjectMember jmSender = jakeGuiAccess.getProjectMember(jakeGuiAccess.getLoginUserid());

			JakeMessage jm = new JakeMessage(jmRecipient, jmSender, this.messageTextArea.getText());

		   jakeGuiAccess.sendMessage(jm);
		} catch (NoSuchProjectMemberException e1) {
			log.warn("Recipient does not exist in project");
			JOptionPane.showMessageDialog(this, 
					translator.get("SendMessageDialogMessageRecipientDoesNotExistInProject"),
					translator.get("SendMessageDialogMesaageError"),
					JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (OtherUserOfflineException e1) {
			JOptionPane.showMessageDialog(this, 
					translator.get("SendMessageDialogMessageRecipientOffline"),
					translator.get("SendMessageDialogMesaageError"),
					JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NoSuchUseridException e1) {
			JOptionPane.showMessageDialog(this,
					translator.get("SendMessageDialogMeaageRecipientDoesNotExistInNetwork"),
					translator.get("SendMessageDialogMesaageError"),
					JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NotLoggedInException e1) {
			JOptionPane.showMessageDialog(this, 
					translator.get("SendMessageDialogMessageNotLoggedIn"),
					translator.get("SendMessageDialogMesaageError"),
					JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NetworkException e1) {
			JOptionPane.showMessageDialog(this,
					translator.get("SendMessageDialogMessageGeneralNetworkError"),
					translator.get("SendMessageDialogMesaageError"),
					JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		}

		JOptionPane.showMessageDialog(this, 
				translator.get("SendMessageDialogMessageSuccessText", recipient), 
				translator.get("SendMessageDialogMessageSuccessTitle"),
				JOptionPane.INFORMATION_MESSAGE);

		this.setVisible(false);
	}	

	private void initComponents() {

		dialogPane = new JPanel();
		contentPanel = new JPanel();
		messageScrollPane = new JScrollPane();
		messageTextArea = new JTextArea();
		mainPanel = new JPanel();
		toLabel = new JLabel();
		recipientLabel = new JLabel();
		buttonBar = new JPanel();
		sendButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle(translator.get("SendMessageDialogTitle"));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setPreferredSize(new Dimension(320, 240));

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(0, 3));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					messageTextArea.setText("");
					messageTextArea.setLineWrap(true);
					messageScrollPane.setViewportView(messageTextArea);
				}
				contentPanel.add(messageScrollPane, BorderLayout.CENTER);

				//======== panel1 ========
				{
					mainPanel.setLayout(new BorderLayout());

					//---- comboBox1 ----
					recipientLabel.setText(recipient);
					mainPanel.add(recipientLabel, BorderLayout.CENTER);

					//---- label1 ----
					toLabel.setText(translator.get("SendMessageDialogToLabel"));
					mainPanel.add(toLabel, BorderLayout.WEST);
				}
				contentPanel.add(mainPanel, BorderLayout.NORTH);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

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
				
				//---- sendButton ----
				sendButton.setText(translator.get("SendMessageDialogSendButton"));
				sendButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});	
				buttonBar.add(sendButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane messageScrollPane;
	private JTextArea messageTextArea;
	private JPanel mainPanel;
	private JLabel toLabel;
	private JLabel recipientLabel;
	private JPanel buttonBar;
	private JButton sendButton;
	private JButton cancelButton;

}
