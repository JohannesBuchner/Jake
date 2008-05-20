package com.doublesignal.sepm.jake.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;

/**
 * @author Peter Steinberger
 */
public class PreferencesDialog extends JDialog {
	public PreferencesDialog(Frame owner) {
		super(owner);
		initComponents();
	}

	public PreferencesDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		autoPushLabel = new JLabel();
		textField1 = new JTextField();
		autoPullLabel = new JLabel();
		textField2 = new JTextField();
		autoLogSyncLabel = new JLabel();
		textField3 = new JTextField();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle("Preferences");
		setResizable(false);
		setModal(true);
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane ========
		{
			dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPane.setLayout(new BorderLayout());

			//======== contentPanel ========
			{
				contentPanel.setLayout(new TableLayout(new double[][] {
					{189, 176},
					{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

				//---- label1 ----
				autoPushLabel.setText("Auto-Push [sec]");
				contentPanel.add(autoPushLabel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(textField1, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- label2 ----
				autoPullLabel.setText("Auto-Pull [sec]");
				contentPanel.add(autoPullLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(textField2, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- label3 ----
				autoLogSyncLabel.setText("Auto Log Sync [sec]");
				contentPanel.add(autoLogSyncLabel, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(textField3, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton ----
				okButton.setText("OK");
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
		contentPane.add(dialogPane, BorderLayout.SOUTH);
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel autoPushLabel;
	private JTextField textField1;
	private JLabel autoPullLabel;
	private JTextField textField2;
	private JLabel autoLogSyncLabel;
	private JTextField textField3;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
