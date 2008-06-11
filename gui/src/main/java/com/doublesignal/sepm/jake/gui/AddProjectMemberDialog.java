package com.doublesignal.sepm.jake.gui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;


/**
 * @author tester tester
 */
@SuppressWarnings("serial")
public class AddProjectMemberDialog extends JDialog {
	private static final Logger log = Logger.getLogger(AddProjectMemberDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private ProjectMember projectMember = null;
	private boolean isSaved = false;
	
	/**
	 * Constructor for a new note
	 * 
	 * @param owner
	 */
	public AddProjectMemberDialog(Frame owner) {
		super(owner, true);
		initComponents();
		this.setTitle(translator.get("AddProjectMemberDialogTitle"));
	}

	public boolean isSaved() {
		return isSaved;
	}

	/**
	 * Get User Id
	 * 
	 * @return note text content.
	 */
	public String getContent() {
		return AddProjectMemberArea.getText();
	}

	/**
	 * Get project member
	 * 
	 * @return projectMember with the inserted projectMember user id
	 */
	public ProjectMember getProjectMember() {
		return projectMember;
	}

	private void okButtonActionPerformed(ActionEvent e) {
		isSaved = true;
		projectMember = new ProjectMember(getContent());
		
		this.setVisible(false);

	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		isSaved = false;
		this.setVisible(false);
	}

	private void initComponents() {
		dialogPane = new JPanel();
		contentPanel = new JPanel();
		ProjectMemberScrollPane = new JScrollPane();
		AddProjectMemberArea = new JTextArea();
		buttonBar = new JPanel();
		okButton = new JButton();
		cancelButton = new JButton();

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
		dialogPane.setLayout(new BorderLayout());

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

		AddProjectMemberArea.setText("");
		AddProjectMemberArea.setLineWrap(true);
		ProjectMemberScrollPane.setViewportView(AddProjectMemberArea);
		contentPanel.add(ProjectMemberScrollPane);
		dialogPane.add(contentPanel, BorderLayout.CENTER);

		buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
		buttonBar.setLayout(new GridBagLayout());
		((GridBagLayout) buttonBar.getLayout()).columnWidths = new int[] { 0,
				85, 80 };
		((GridBagLayout) buttonBar.getLayout()).columnWeights = new double[] {
				1.0, 0.0, 0.0 };

		// ---- cancelButton ----
		cancelButton.setText(translator.get("ButtonCancel"));
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cancelButtonActionPerformed(e);
			}
		});
		buttonBar.add(cancelButton, new GridBagConstraints(1, 0, 1, 1, 0.0,
				0.0, GridBagConstraints.CENTER, GridBagConstraints.BOTH,
				new Insets(0, 0, 0, 5), 0, 0));
		dialogPane.add(buttonBar, BorderLayout.SOUTH);

		// ---- okButton ----
		okButton.setText(translator.get("AddProjectMemberDialogSaveButton"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		
		contentPane.add(dialogPane, BorderLayout.CENTER);
		setMinimumSize(new Dimension(200, 110));
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPane;
	private JPanel contentPanel;
	private JScrollPane ProjectMemberScrollPane;
	private JTextArea AddProjectMemberArea;
	private JPanel buttonBar;
	private JButton okButton;
	private JButton cancelButton;
}
