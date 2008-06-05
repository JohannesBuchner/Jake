package com.doublesignal.sepm.jake.gui;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.xml.XmlBeanFactory;
import org.springframework.core.io.ClassPathResource;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.JakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;

/**
 * The application preferences dialog.
 * @author Peter Steinberger, Simon
 */
@SuppressWarnings("serial")
public class PreferencesDialog extends JDialog {
	
	private static Logger log = Logger.getLogger(PreferencesDialog.class);
	private ITranslationProvider translator;
	private JakeGui gui;
	private IJakeGuiAccess guiAccess;

	
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
	
	private void okButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}	
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}

	private void initComponents() {
		log.info("initializing Preferences Dialog");
		BeanFactory factory = new XmlBeanFactory(new ClassPathResource("beans.xml"));
		translator = (ITranslationProvider) factory.getBean("translationProvider");
		
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		autoPushLabel = new JLabel();
		autoPushCheckBox = new JCheckBox();
		autoPullLabel = new JLabel();
		autoPullCheckBox = new JCheckBox();
		autoLogSyncLabel = new JLabel();
		logSyncTextField = new JTextField();
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
					{189, 176},
					{TableLayout.FILL, TableLayout.PREFERRED, TableLayout.PREFERRED}}));

				//---- auto push ----
				autoPushLabel.setText(translator.get("PreferencesLableAutoPush"));
				contentPanel.add(autoPushLabel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));
				contentPanel.add(autoPushCheckBox, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.CENTER));

				//---- autp pull ----
				autoPullLabel.setText(translator.get("PreferencesLableAutoPull"));
				contentPanel.add(autoPullLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(autoPullCheckBox, new TableLayoutConstraints(1, 1, 1, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

				//---- log sync ----
				autoLogSyncLabel.setText(translator.get("PreferencesLableAutoLogSync"));
				contentPanel.add(autoLogSyncLabel, new TableLayoutConstraints(0, 2, 0, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				contentPanel.add(logSyncTextField, new TableLayoutConstraints(1, 2, 1, 2, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
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

		//===== fill in data =====
//		autoPushCheckBox.setEnabled(guiAccess.getConfigOption(configKey));
//		autoPullCheckBox.setEnabled(guiAccess.getConfigOption(configKey));
//		logSyncTextField.setText(guiAccess.getConfigOption(configKey));
		
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JLabel autoPushLabel;
	private JCheckBox autoPushCheckBox;
	private JLabel autoPullLabel;
	private JCheckBox autoPullCheckBox;
	private JLabel autoLogSyncLabel;
	private JTextField logSyncTextField;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
