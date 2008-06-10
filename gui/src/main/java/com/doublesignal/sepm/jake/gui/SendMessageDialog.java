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
			if("".equals(this.textArea1.getText())) {
				JOptionPane.showMessageDialog(this, "You cannot send an empty message. Please enter some text and try again.", "Error sending message", JOptionPane.ERROR_MESSAGE);
				return;
			}
			ProjectMember jmRecipient = jakeGuiAccess.getProjectMember(recipient);
			// TODO: Is this REALLY the value we want?
			ProjectMember jmSender = jakeGuiAccess.getProjectMember(jakeGuiAccess.getLoginUserid());

			JakeMessage jm = new JakeMessage(jmRecipient, jmSender, this.textArea1.getText());

		   jakeGuiAccess.sendMessage(jm);
		} catch (NoSuchProjectMemberException e1) {
			log.warn("Recipient does not exist in project");
			JOptionPane.showMessageDialog(this, "The recipient project member does not exist in this project.", "Error sending message", JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (OtherUserOfflineException e1) {
			JOptionPane.showMessageDialog(this, "The recipient project member is currently offline. Try again later.", "Error sending message", JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NoSuchUseridException e1) {
			JOptionPane.showMessageDialog(this, "The recipient project member does not exist on this network.", "Error sending message", JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NotLoggedInException e1) {
			JOptionPane.showMessageDialog(this, "The message could not be sent because you are not logged in.", "Error sending message", JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		} catch (NetworkException e1) {
			JOptionPane.showMessageDialog(this, "The message could not be sent because of a general network error. Please try again later.", "Error sending message", JOptionPane.ERROR_MESSAGE);
			this.setVisible(false);
			return;
		}

		JOptionPane.showMessageDialog(this, "Your message to \""+ recipient +"\" has been sent successfully.", "Message sent", JOptionPane.INFORMATION_MESSAGE);

		this.setVisible(false);
	}	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		panel1 = new JPanel();
		comboBox1 = new JComboBox();
		label1 = new JLabel();
		label2 = new JLabel();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Send Message");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setMinimumSize(new Dimension(320, 240));

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(0, 3));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					textArea1.setText("");
					textArea1.setLineWrap(true);
					scrollPane1.setViewportView(textArea1);
				}
				contentPanel.add(scrollPane1, BorderLayout.CENTER);

				//======== panel1 ========
				{
					panel1.setLayout(new BorderLayout());

					//---- comboBox1 ----
					label2.setText(recipient);
					panel1.add(label2, BorderLayout.CENTER);

					//---- label1 ----
					label1.setText("To:");
					panel1.add(label1, BorderLayout.WEST);
				}
				contentPanel.add(panel1, BorderLayout.NORTH);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- cancelButton ----
				cancelButton.setText("Cancel");
			   cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});
				buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));
				
				//---- okButton ----
				okButton.setText("Send");
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
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - tester tester
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JPanel panel1;
	private JComboBox comboBox1;
	private JLabel label1;
	private JLabel label2;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
