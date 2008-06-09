package com.doublesignal.sepm.jake.gui;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class ReceiveMessageDialog extends JDialog {
	private JakeMessage message;
	private Date receivedAt;

	public ReceiveMessageDialog(Frame owner, JakeMessage jakeMessage) {
		super(owner);
		this.message = jakeMessage;
		initComponents();
	}

	public ReceiveMessageDialog(Dialog owner, JakeMessage jakeMessage) {
		super(owner);
		this.message = jakeMessage;
		initComponents();
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}		

	private void initComponents() {
		dialogPane2 = new JPanel();
		contentPanel2 = new JPanel();
		messageScrollPane = new JScrollPane();
		messageTextArea = new JTextArea();
		panel1 = new JPanel();
		label2 = new JLabel();
		label1 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		buttonBar = new JPanel();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Message from " + message.getSender().getUserId());
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane2 ========
		{
			dialogPane2.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane2.setLayout(new BorderLayout());

			//======== contentPanel2 ========
			{
				contentPanel2.setLayout(new BorderLayout(0, 3));

				//======== messageScrollPane ========
				{
					//---- messageTextArea ----
					messageTextArea.setText(message.getContent());
					messageTextArea.setLineWrap(true);
					messageTextArea.setEditable(false);
					messageScrollPane.setViewportView(messageTextArea);
				}
				contentPanel2.add(messageScrollPane, BorderLayout.CENTER);

				//======== panel1 ========
				{
					panel1.setLayout(new TableLayout(new double[][] {
						{TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED},
						{TableLayout.PREFERRED, TableLayout.FILL}}));
					((TableLayout)panel1.getLayout()).setVGap(3);

					//---- label2 ----
					label2.setText("From: ");
					panel1.add(label2, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label1 ----
					label1.setText(message.getSender().getUserId());
					panel1.add(label1, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label3 ----
					label3.setText("When: ");
					panel1.add(label3, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label4 ----
					label4.setText(this.message.getTime().toString());
					panel1.add(label4, new TableLayoutConstraints(1, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				contentPanel2.add(panel1, BorderLayout.NORTH);
			}
			dialogPane2.add(contentPanel2, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- cancelButton ----
				cancelButton.setText("Close");
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});	
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane2.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane2, BorderLayout.CENTER);
		setMinimumSize(new Dimension(300,250));
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane2;
	private JPanel contentPanel2;
	private JScrollPane messageScrollPane;
	private JTextArea messageTextArea;
	private JPanel panel1;
	private JLabel label2;
	private JLabel label1;
	private JLabel label3;
	private JLabel label4;
	private JPanel buttonBar;
	private JButton cancelButton;
}
