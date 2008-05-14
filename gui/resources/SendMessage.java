import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Mon May 05 19:28:53 CEST 2008
 */



/**
 * @author tester tester
 */
public class SendMessage extends JDialog {
	public SendMessage(Frame owner) {
		super(owner);
		initComponents();
	}

	public SendMessage(Dialog owner) {
		super(owner);
		initComponents();
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

			// JFormDesigner evaluation mark
			dialogPane.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane.getBorder())); dialogPane.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BorderLayout(0, 3));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					textArea1.setText("Hi Johannes! File XY sollte \u00fcberarbeitet werden.");
					textArea1.setLineWrap(true);
					scrollPane1.setViewportView(textArea1);
				}
				contentPanel.add(scrollPane1, BorderLayout.CENTER);

				//======== panel1 ========
				{
					panel1.setLayout(new BorderLayout());

					//---- comboBox1 ----
					comboBox1.setModel(new DefaultComboBoxModel(new String[] {
						"Johannes",
						"Chris",
						"Peter",
						"Simon"
					}));
					panel1.add(comboBox1, BorderLayout.CENTER);

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

				//---- okButton ----
				okButton.setText("Send");
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
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JPanel panel1;
	private JComboBox comboBox1;
	private JLabel label1;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
