package com.doublesignal.sepm.jake.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import info.clearthought.layout.*;
import com.doublesignal.sepm.jake.core.domain.JakeObject;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class ResolveConflictDialog extends JDialog {

    private JakeObject jakeObject;

    public ResolveConflictDialog setJakeObject(JakeObject jakeObject) {
        this.jakeObject = jakeObject;
        return this;
    }

    public ResolveConflictDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public ResolveConflictDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}	

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		panel1 = new JPanel();
		label12 = new JLabel();
		label13 = new JLabel();
		label1 = new JLabel();
		label5 = new JLabel();
		label6 = new JLabel();
		label2 = new JLabel();
		label7 = new JLabel();
		label8 = new JLabel();
		label4 = new JLabel();
		label9 = new JLabel();
		label10 = new JLabel();
		button1 = new JButton();
		button2 = new JButton();
		radioButton1 = new JRadioButton();
		radioButton2 = new JRadioButton();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();
		panel2 = new JPanel();
		label3 = new JLabel();
		label11 = new JLabel();

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

				//======== panel1 ========
				{
					panel1.setLayout(new TableLayout(new double[][] {
						{132, 121, 134},
						{26, TableLayout.PREFERRED, 19, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));
					((TableLayout)panel1.getLayout()).setHGap(5);
					((TableLayout)panel1.getLayout()).setVGap(5);

					//---- label12 ----
					label12.setText("Local");
					label12.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					panel1.add(label12, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label13 ----
					label13.setText("Remote");
					label13.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					panel1.add(label13, new TableLayoutConstraints(2, 0, 2, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label1 ----
					label1.setText("Last editor:");
					panel1.add(label1, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label5 ----
					label5.setText("Simon");
					panel1.add(label5, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label6 ----
					label6.setText("Chris");
					panel1.add(label6, new TableLayoutConstraints(2, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label2 ----
					label2.setText("Last edit time:");
					panel1.add(label2, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

					//---- label7 ----
					label7.setText("Yesterday, 11:00");
					panel1.add(label7, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

					//---- label8 ----
					label8.setText("Today, 12:00");
					label8.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					panel1.add(label8, new TableLayoutConstraints(2, 2, 2, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

					//---- label4 ----
					label4.setText("File size:");
					panel1.add(label4, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label9 ----
					label9.setText("600 KB");
					label9.setFont(new Font("Lucida Grande", Font.BOLD, 13));
					panel1.add(label9, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label10 ----
					label10.setText("400 KB");
					panel1.add(label10, new TableLayoutConstraints(2, 3, 2, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- button1 ----
					button1.setText("Open");
					panel1.add(button1, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- button2 ----
					button2.setText("Open");
					panel1.add(button2, new TableLayoutConstraints(2, 4, 2, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- radioButton1 ----
					radioButton1.setText("Use local file");
					panel1.add(radioButton1, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- radioButton2 ----
					radioButton2.setText("Use remote file");
					panel1.add(radioButton2, new TableLayoutConstraints(2, 5, 2, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				contentPanel.add(panel1);
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
				okButton.setText("OK");
				okButton.setEnabled(false);
				buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));

			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);

			//======== panel2 ========
			{
				panel2.setLayout(new BorderLayout());

				//---- label3 ----
				label3.setText("There is a conflict for ");
				panel2.add(label3, BorderLayout.WEST);

				//---- label11 ----
				label11.setText("Docs/SEPM_Architekt.txt");
				label11.setFont(new Font("Lucida Grande", Font.BOLD, 13));
				panel2.add(label11, BorderLayout.CENTER);
			}
			dialogPane.add(panel2, BorderLayout.NORTH);
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
	private JPanel panel1;
	private JLabel label12;
	private JLabel label13;
	private JLabel label1;
	private JLabel label5;
	private JLabel label6;
	private JLabel label2;
	private JLabel label7;
	private JLabel label8;
	private JLabel label4;
	private JLabel label9;
	private JLabel label10;
	private JButton button1;
	private JButton button2;
	private JRadioButton radioButton1;
	private JRadioButton radioButton2;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	private JPanel panel2;
	private JLabel label3;
	private JLabel label11;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
