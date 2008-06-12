package com.doublesignal.sepm.jake.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.LinkedList;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchConfigOptionException;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * The application preferences dialog.
 * @author Peter Steinberger, Simon
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends JDialog {
	private static final Logger log = Logger.getLogger(PreferencesDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

	private JakeGui gui;
	private IJakeGuiAccess guiAccess;
	private List<ActionListener> updateListeners;

	
	public PreferencesDialog(Frame owner, JakeGui gui) {
		super(owner);
		this.gui = gui;
		guiAccess = gui.getJakeGuiAccess();
		initComponents();
	}

	public PreferencesDialog(Dialog owner) {
		super(owner);
		initComponents();
	}
	
	public void addUpdateListener(ActionListener l) {
		updateListeners.add(l);		
	}
	
	private void notifyUpdateListeners()	{
		for (ActionListener listener : updateListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "update"));
		}
	}
	private void okButtonActionPerformed(ActionEvent e) {
		if (!JakeGuiAccess.isOfCorrectUseridFormat(userTextfield.getText())) {
			UserDialogHelper.translatedError(this, "PreferencesInvalidUserIdError");
			userTextfield.setBackground(Color.RED);
			return;
		} else {
			userTextfield.setBackground(Color.WHITE);
		}
		try {
			int logsyncInterval = Integer.parseInt(logSyncTextField.getText());
			if (logsyncInterval < 0)
				throw new NumberFormatException("logsyncInterval must be geater 0");
			
			guiAccess.setConfigOption("showOfflineProjectMembers", String.valueOf(showOfflineProjectMemebrsCheckBox.isSelected()));
			guiAccess.setConfigOption("autoRefresh", String.valueOf(autoRefreshCheckBox.isSelected()));
			guiAccess.setConfigOption("autoPush", String.valueOf(autoPushCheckBox.isSelected()));
			guiAccess.setConfigOption("autoPull", String.valueOf(autoPullCheckBox.isSelected()));
			guiAccess.setConfigOption("logsyncInterval", String.valueOf(logsyncInterval));
			guiAccess.setConfigOption("userid", userTextfield.getText());
			guiAccess.setConfigOption("password", String.valueOf(passwordTextfield.getPassword()));
			notifyUpdateListeners();
			this.setVisible(false);
		} catch (NumberFormatException ex) {
			log.info("could not parse logsyncInterval int" + ex.getMessage());
			logSyncTextField.setBackground(Color.RED);
		}
	}	
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		notifyUpdateListeners();
		this.setVisible(false);
	}

	private void initComponents() {
		log.info("initializing Preferences Dialog");
		updateListeners = new LinkedList<ActionListener>();
		
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		showOfflineProjectMembersLabel = new JLabel();
		showOfflineProjectMemebrsCheckBox = new JCheckBox();
		autoRefreshLabel = new JLabel();
		autoRefreshCheckBox = new JCheckBox();
		autoPushLabel = new JLabel();
		autoPushCheckBox = new JCheckBox();
		autoPullLabel = new JLabel();
		autoPullCheckBox = new JCheckBox();
		autoLogSyncLabel = new JLabel();
		logSyncTextField = new JTextField();
		userLabel = new JLabel();
		userTextfield = new JTextField();
		passwordLabel = new JLabel();
		passwordTextfield = new JPasswordField();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		//======== this ========
		setTitle(translator.get("PreferencesWindowTitle"));
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
					{250, 176},
					{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

				//---- offline members ----
				showOfflineProjectMembersLabel.setText(translator.get("PreferencesLabelShowOfflineProjectMembers"));
				contentPanel.add(showOfflineProjectMembersLabel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(showOfflineProjectMemebrsCheckBox, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- auto refresh ----
				autoRefreshLabel.setText(translator.get("PreferencesLabelAutoRefresh"));
				contentPanel.add(autoRefreshLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(autoRefreshCheckBox, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- auto push ----
				autoPushLabel.setText(translator.get("PreferencesLabelAutoPush"));
				contentPanel.add(autoPushLabel, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(autoPushCheckBox, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- autp pull ----
				autoPullLabel.setText(translator.get("PreferencesLabelAutoPull"));
				contentPanel.add(autoPullLabel, new TableLayoutConstraints(0, 3, 0, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(autoPullCheckBox, new TableLayoutConstraints(1, 3, 1, 3, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- log sync ----
				autoLogSyncLabel.setText(translator.get("PreferencesLabelAutoLogSync"));
				contentPanel.add(autoLogSyncLabel, new TableLayoutConstraints(0, 4, 0, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(logSyncTextField, new TableLayoutConstraints(1, 4, 1, 4, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				
				// username
				userLabel.setText(translator.get("PreferencesLabelUserName"));
				contentPanel.add(userLabel, new TableLayoutConstraints(0, 5, 0, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(userTextfield, new TableLayoutConstraints(1, 5, 1, 5, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				
				// password
				passwordLabel.setText(translator.get("PreferencesLabelPassword"));
				contentPanel.add(passwordLabel, new TableLayoutConstraints(0, 6, 0, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(passwordTextfield, new TableLayoutConstraints(1, 6, 1, 6, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				
			}
			dialogPane.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- okButton ----
				okButton.setText(translator.get("ButtonConfirm"));
				okButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						okButtonActionPerformed(e);
					}
				});	
				buttonBar.add(okButton, new GridBagConstraints(1, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 5), 0, 0));

				//---- cancelButton ----
				cancelButton.setText(translator.get("ButtonCancel"));
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
		contentPane.add(dialogPane, BorderLayout.SOUTH);

		//===== try to fill in data =====
		try {
			showOfflineProjectMemebrsCheckBox.setSelected(Boolean.parseBoolean(guiAccess.getConfigOption("showOfflineProjectMembers")));
			autoRefreshCheckBox.setSelected(Boolean.parseBoolean(guiAccess.getConfigOption("autoRefresh")));
			autoPushCheckBox.setSelected(Boolean.parseBoolean(guiAccess.getConfigOption("autoPush")));
			autoPullCheckBox.setSelected(Boolean.parseBoolean(guiAccess.getConfigOption("autoPull")));
			logSyncTextField.setText(guiAccess.getConfigOption("logsyncInterval"));
		} catch (NoSuchConfigOptionException e) {
			log.warn("could not fill in configuration values; unknown configuration option!");
		}
		try {
			userTextfield.setText(guiAccess.getConfigOption("userid"));
			passwordTextfield.setText(guiAccess.getConfigOption("password"));
		} catch (NoSuchConfigOptionException e) {
			log.warn("could not fill in username/password; unknown configuration option. Values may not yet be set");
		}
		
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel showOfflineProjectMembersLabel;
	private JCheckBox showOfflineProjectMemebrsCheckBox;
	private JLabel autoRefreshLabel;
	private JCheckBox autoRefreshCheckBox;
	private JLabel autoPushLabel;
	private JCheckBox autoPushCheckBox;
	private JLabel autoPullLabel;
	private JCheckBox autoPullCheckBox;
	private JLabel autoLogSyncLabel;
	private JTextField logSyncTextField;
	private JLabel userLabel;
	private JTextField userTextfield;
	private JLabel passwordLabel;
	private JPasswordField passwordTextfield;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
