import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
/*
 * Created by JFormDesigner on Wed May 07 16:25:01 CEST 2008
 */



/**
 * @author tester tester
 */
public class InfoDlg extends JDialog {
	public InfoDlg(Frame owner) {
		super(owner);
		initComponents();
	}

	public InfoDlg(Dialog owner) {
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
		buttonBar2 = new JPanel();
		okButton2 = new JButton();
		cancelButton2 = new JButton();

		//======== this ========
		setTitle("Info for Simon");
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
				contentPanel2.setLayout(new BoxLayout(contentPanel2, BoxLayout.X_AXIS));

				//======== scrollPane1 ========
				{

					//---- textArea1 ----
					textArea1.setText("Student, Technical University of Vienna\n\nTel: 012344567\nMail: simon@jake-project.com");
					textArea1.setLineWrap(true);
					scrollPane1.setViewportView(textArea1);
				}
				contentPanel2.add(scrollPane1);
			}
			dialogPane2.add(contentPanel2, BorderLayout.CENTER);

			//======== buttonBar2 ========
			{
				buttonBar2.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar2.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar2.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar2.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton2 ----
				okButton2.setText("Save");
				buttonBar2.add(okButton2, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

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
	private JPanel buttonBar2;
	private JButton okButton2;
	private JButton cancelButton2;
	// JFormDesigner - End of variables declaration  //GEN-END:variables
}
