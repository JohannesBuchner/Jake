package com.doublesignal.sepm.jake.gui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;


/**
 * @author tester tester
 */
@SuppressWarnings("serial")
public class NoteEditorDialog extends JDialog {
	public NoteEditorDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public NoteEditorDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}		

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		noteScrollPane = new JScrollPane();
		noteTextArea = new JTextArea();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("New/Edit Note  - This is a Note");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

				//======== noteScrollPane ========
				{
					//---- noteTextArea ----
					noteTextArea.setText("This is a Note\n\nIt has an implicit title... bla");
					noteTextArea.setLineWrap(true);
					noteScrollPane.setViewportView(noteTextArea);
				}
				contentPanel.add(noteScrollPane);
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
				cancelButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});	
				buttonBar.add(cancelButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPane.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setMinimumSize(new Dimension(300, 250));
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane noteScrollPane;
	private JTextArea noteTextArea;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
