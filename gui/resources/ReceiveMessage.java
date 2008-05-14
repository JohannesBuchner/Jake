import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import info.clearthought.layout.*;
/*
 * Created by JFormDesigner on Mon May 05 19:31:06 CEST 2008
 */



/**
 * @author tester tester
 */
public class ReceiveMessage extends JDialog {
	public ReceiveMessage(Frame owner) {
		super(owner);
		initComponents();
	}

	public ReceiveMessage(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		// JFormDesigner - Component initialization - DO NOT MODIFY  //GEN-BEGIN:initComponents
		// Generated using JFormDesigner Evaluation license - tester tester
		dialogPane2 = new JPanel();
		contentPanel2 = new JPanel();
		scrollPane1 = new JScrollPane();
		textArea1 = new JTextArea();
		panel1 = new JPanel();
		label2 = new JLabel();
		label1 = new JLabel();
		label3 = new JLabel();
		label4 = new JLabel();
		buttonBar2 = new JPanel();
		cancelButton2 = new JButton();

		//======== this ========
		setTitle("Message from Johannes");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane2 ========
		{
			dialogPane2.setBorder(new EmptyBorder(12, 12, 12, 12));

			// JFormDesigner evaluation mark
			dialogPane2.setBorder(new javax.swing.border.CompoundBorder(
				new javax.swing.border.TitledBorder(new javax.swing.border.EmptyBorder(0, 0, 0, 0),
					"JFormDesigner Evaluation", javax.swing.border.TitledBorder.CENTER,
					javax.swing.border.TitledBorder.BOTTOM, new java.awt.Font("Dialog", java.awt.Font.BOLD, 12),
					java.awt.Color.red), dialogPane2.getBorder())); dialogPane2.addPropertyChangeListener(new java.beans.PropertyChangeListener(){public void propertyChange(java.beans.PropertyChangeEvent e){if("border".equals(e.getPropertyName()))throw new RuntimeException();}});

			dialogPane2.setLayout(new BorderLayout());

			//======== contentPanel2 ========
			{
				contentPanel2.setLayout(new BorderLayout(0, 3));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					textArea1.setText("Erledigt.");
					textArea1.setLineWrap(true);
					textArea1.setEditable(false);
					scrollPane1.setViewportView(textArea1);
				}
				contentPanel2.add(scrollPane1, BorderLayout.CENTER);

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
					label1.setText("Johannes");
					panel1.add(label1, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label3 ----
					label3.setText("When: ");
					panel1.add(label3, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- label4 ----
					label4.setText("Yesterday, 11:00");
					panel1.add(label4, new TableLayoutConstraints(1, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				contentPanel2.add(panel1, BorderLayout.NORTH);
			}
			dialogPane2.add(contentPanel2, BorderLayout.CENTER);

			//======== buttonBar2 ========
			{
				buttonBar2.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar2.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar2.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar2.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- cancelButton2 ----
				cancelButton2.setText("Close");
				buttonBar2.add(cancelButton2, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane2.add(buttonBar2, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane2, BorderLayout.CENTER);
		pack();
		setLocationRelativeTo(getOwner());
		// JFormDesigner - End of component initialization  //GEN-END:initComponents
	}

	// JFormDesigner - Variables declaration - DO NOT MODIFY  //GEN-BEGIN:variables
	// Generated using JFormDesigner Evaluation license - tester tester
	private JPanel dialogPane2;
	private JPanel contentPanel2;
	private JScrollPane scrollPane1;
	private JTextArea textArea1;
	private JPanel panel1;
	private JLabel label2;
	private JLabel label1;
	private JLabel label3;
	private JLabel label4;
	private JPanel buttonBar2;
	private JButton cancelButton2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
