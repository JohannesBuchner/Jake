package com.doublesignal.sepm.jake.gui;
import com.doublesignal.sepm.jake.core.domain.JakeObject;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;

import org.apache.log4j.Logger;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class ViewLogDialog extends JDialog {
    private static Logger log = Logger.getLogger(ViewLogDialog.class);
    private JakeObject jakeObject;

    public ViewLogDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public ViewLogDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

    public ViewLogDialog setJakeObject(JakeObject jakeObject)
    {
        log.info("Set jakeObject to  "+ jakeObject.getName());
        this.jakeObject = jakeObject;
        return this;
    }


    private void okButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}
	

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		logTableScrollPane = new JScrollPane();
		logTable = new JTable();
		buttonBar = new JPanel();
		okButton = new JButton();

		//======== this ========
		setTitle("View Log - SEPM_Artefakt.pdf");
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

				//======== scrollPane1 ========
				{

					//---- table1 ----
					logTable.setModel(new DefaultTableModel(
						new Object[][] {
							{"CREATED", "Johannes", "April 1st, 11:59"},
							{"UPDATED", "Peter", "April 4th, 19:33"},
						},
						new String[] {
							"Action", "User", "Time"
						}
					));
					logTableScrollPane.setViewportView(logTable);
				}
				contentPanel.add(logTableScrollPane);
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0};

				//---- okButton ----
				okButton.setText("OK");
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});	
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
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
	private JScrollPane logTableScrollPane;
	private JTable logTable;
	private JPanel buttonBar;
	private JButton okButton;
}
