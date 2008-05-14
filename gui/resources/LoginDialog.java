import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import info.clearthought.layout.*;
/*
 * Created by JFormDesigner on Mon May 05 19:37:49 CEST 2008
 */



/**
 * @author tester tester
 */
public class LoginDialog extends JDialog {
	public LoginDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public LoginDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		label1 = new JLabel();
		comboBox1 = new JComboBox();
		label2 = new JLabel();
		textField2 = new JTextField();
		checkBox1 = new JCheckBox();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Login");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));

			// JFormDesigner evaluation mark
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setPreferredSize(new Dimension(200, 200));
				contentPanel.setLayout(new TableLayout(new double[][] {
					{76, 276},
					{39, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

				//---- label1 ----
				label1.setText("User:");
				contentPanel.add(label1, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- comboBox1 ----
				comboBox1.setEditable(true);
				comboBox1.setModel(new DefaultComboBoxModel(new String[] {
					"pstein@jabber.fsinf.at"
				}));
				contentPanel.add(comboBox1, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- label2 ----
				label2.setText("Password:");
				contentPanel.add(label2, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(textField2, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- checkBox1 ----
				checkBox1.setText("Remember Password");
				contentPanel.add(checkBox1, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton ----
				okButton.setText("Connect");
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
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - tester tester
	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel label1;
	private JComboBox comboBox1;
	private JLabel label2;
	private JTextField textField2;
	private JCheckBox checkBox1;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
