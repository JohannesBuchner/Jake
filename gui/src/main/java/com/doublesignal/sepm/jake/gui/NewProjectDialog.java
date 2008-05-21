package com.doublesignal.sepm.jake.gui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import info.clearthought.layout.*;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class NewProjectDialog extends JDialog {
	public NewProjectDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public NewProjectDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		textField1 = new JTextField();
		label2 = new JLabel();
		textField2 = new JTextField();
		button2 = new JButton();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

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
				contentPanel.setLayout(new TableLayout(new double[][] {
					{TableLayout.PREFERRED, 256, TableLayout.PREFERRED},
					{TableLayout.FILL, TableLayout.PREFERRED}}));

				//---- label1 ----
				label1.setText("Name:");
				contentPanel.add(label1, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(textField1, new TableLayoutConstraints(1, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- label2 ----
				label2.setText("Folder:");
				contentPanel.add(label2, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(textField2, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- button2 ----
				button2.setText("...");
				contentPanel.add(button2, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
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
	private JLabel label1;
	private JTextField textField1;
	private JLabel label2;
	private JTextField textField2;
	private JButton button2;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
