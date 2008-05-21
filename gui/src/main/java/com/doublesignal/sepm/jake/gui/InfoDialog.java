package com.doublesignal.sepm.jake.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class InfoDialog extends JDialog {
	public InfoDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public InfoDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}	

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		infoScrollPane = new JScrollPane();
		infotextArea = new JTextArea();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Info for Simon");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

				//======== infoScrollPane ========
				{

					//---- textArea1 ----
					infotextArea.setText("Student, Technical University of Vienna\n\nTel: 012344567\nMail: simon@jake-project.com");
					infotextArea.setLineWrap(true);
					infoScrollPane.setViewportView(infotextArea);
				}
				contentPanel.add(infoScrollPane);
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
				cancelButton.setText("Close");
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
	private JScrollPane infoScrollPane;
	private JTextArea infotextArea;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
