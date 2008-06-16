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
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.AbstractDocument;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.StyledDocument;
import javax.swing.text.AbstractDocument.Content;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.gui.helper.DocumentSizeFilter;
import com.doublesignal.sepm.jake.gui.helper.DocumentSizeAsciiFilter;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

/**
 * @author tester tester
 */
@SuppressWarnings("serial")
public class NoteEditorDialog extends JDialog {
	private static final Logger log = Logger.getLogger(NoteEditorDialog.class);
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private static final int MAX_NOTESLENGTH = 10000;
	
	private NoteObject note = null;
	private boolean isSaved = false;

	/**
	 * Constructor for a new note
	 * 
	 * @param owner
	 */
	public NoteEditorDialog(Frame owner) {
		super(owner, true);
		log.info("Init Note Editor Dialog");
		initComponents();
		this.setTitle(translator.get("NoteEditorDialogNewNoteTitle"));
	}

	/**
	 * Constructor for edit note. (modal)
	 * 
	 * @param owner
	 * @param note
	 */
	public NoteEditorDialog(Frame owner, NoteObject note) {
		super(owner, true); // make it modal!
		this.note = note;

		initComponents();
		this.setTitle(translator.get("NoteEditorDialogEditNoteTitle"));
		loadNote();
	}

	public boolean isSaved() {
		return isSaved;
	}

	/**
	 * Get note text content
	 * 
	 * @return note text content.
	 */
	public String getContent() {
		return noteTextArea.getText();
	}

	public NoteObject getNote() {
		return note;
	}

	private void loadNote() {
		noteTextArea.setText(note.getContent());
	}

	private void okButtonActionPerformed(ActionEvent e) {
		isSaved = true;

		// update current note object or create new one
		if (note == null) {
			// TODO: interface doesn't need this
			note = NoteObject.createNoteObject("0", getContent());
		} else {
			note.setContent(getContent());
		}

		this.setVisible(false);

	}

	private void cancelButtonActionPerformed(ActionEvent e) {
		isSaved = false;
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

		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		dialogPane.setBorder(new EmptyBorder(12, 12, 12, 12));
		dialogPane.setLayout(new BorderLayout());

		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.X_AXIS));

		noteTextArea.setText("");
		noteTextArea.setLineWrap(true);
		
		AbstractDocument doc;
		Document textareaDoc = noteTextArea.getDocument();
		if (textareaDoc instanceof AbstractDocument) {
		    doc = (AbstractDocument)textareaDoc;
		    doc.setDocumentFilter(new DocumentSizeAsciiFilter(MAX_NOTESLENGTH));
		}
		
		noteScrollPane.setViewportView(noteTextArea);
		contentPanel.add(noteScrollPane);
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
		okButton.setText(translator.get("NoteEditorDialogOkButton"));
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				okButtonActionPerformed(e);
			}
		});
		buttonBar.add(okButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
				GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(
						0, 0, 0, 0), 0, 0));
		
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
