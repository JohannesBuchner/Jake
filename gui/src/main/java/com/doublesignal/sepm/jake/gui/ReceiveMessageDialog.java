package com.doublesignal.sepm.jake.gui;
import com.doublesignal.sepm.jake.core.domain.JakeMessage;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

import info.clearthought.layout.TableLayout;
import info.clearthought.layout.TableLayoutConstraints;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;


/**
 * @author Peter Steinberger
 */
@SuppressWarnings("serial")
public class ReceiveMessageDialog extends JDialog {
	private static final Logger log = Logger.getLogger(ReceiveMessageDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private JakeMessage message;
	private Date receivedAt;

	public ReceiveMessageDialog(Frame owner, JakeMessage jakeMessage) {
		super(owner);
		this.message = jakeMessage;
		initComponents();
	}

	public ReceiveMessageDialog(Dialog owner, JakeMessage jakeMessage) {
		super(owner);
		this.message = jakeMessage;
		initComponents();
	}
	
	private void cancelButtonActionPerformed(ActionEvent e) {
		this.setVisible(false);
	}		

	private void initComponents() {
		dialogPanel = new JPanel();
		contentPanel = new JPanel();
		messageScrollPane = new JScrollPane();
		messageTextArea = new JTextArea();
		mainPanel = new JPanel();
		fromLabel = new JLabel();
		fromUserLabel = new JLabel();
		whenLabel = new JLabel();
		whenDateLabel = new JLabel();
		buttonBar = new JPanel();
		closeButton = new JButton();

		//======== this ========
		setTitle(translator.get("ReceiveMessageDialogTitle", message.getSender().getUserId()));
		Container contentPane = getContentPane();
		contentPane.setLayout(new BorderLayout());

		//======== dialogPane2 ========
		{
			dialogPanel.setBorder(new EmptyBorder(12, 12, 12, 12));
			dialogPanel.setLayout(new BorderLayout());

			//======== contentPanel2 ========
			{
				contentPanel.setLayout(new BorderLayout(0, 3));

				//======== messageScrollPane ========
				{
					//---- messageTextArea ----
					messageTextArea.setText(message.getContent());
					messageTextArea.setLineWrap(true);
					messageTextArea.setEditable(false);
					messageScrollPane.setViewportView(messageTextArea);
				}
				contentPanel.add(messageScrollPane, BorderLayout.CENTER);

				//======== panel1 ========
				{
					mainPanel.setLayout(new TableLayout(new double[][] {
						{TableLayout.PREFERRED, TableLayout.FILL, TableLayout.PREFERRED},
						{TableLayout.PREFERRED, TableLayout.FILL}}));
					((TableLayout)mainPanel.getLayout()).setVGap(3);

					//---- from: ----
					fromLabel.setText(translator.get("ReceiveMessageDialogFromLabel"));
					mainPanel.add(fromLabel, new TableLayoutConstraints(0, 0, 0, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- from user ----
					fromUserLabel.setText(message.getSender().getUserId());
					mainPanel.add(fromUserLabel, new TableLayoutConstraints(1, 0, 1, 0, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- when: ----
					whenLabel.setText(translator.get("ReceiveMessageDialogWhenLabel"));
					mainPanel.add(whenLabel, new TableLayoutConstraints(0, 1, 0, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));

					//---- when date ----
					whenDateLabel.setText(this.message.getTime().toString());
					mainPanel.add(whenDateLabel, new TableLayoutConstraints(1, 1, 2, 1, TableLayoutConstraints.FULL, TableLayoutConstraints.FULL));
				}
				contentPanel.add(mainPanel, BorderLayout.NORTH);
			}
			dialogPanel.add(contentPanel, BorderLayout.CENTER);

			//======== buttonBar ========
			{
				buttonBar.setBorder(new EmptyBorder(12, 0, 0, 0));
				buttonBar.setLayout(new GridBagLayout());
				((GridBagLayout)buttonBar.getLayout()).columnWidths = new int[] {0, 85, 80};
				((GridBagLayout)buttonBar.getLayout()).columnWeights = new double[] {1.0, 0.0, 0.0};

				//---- close Button ----
				closeButton.setText(translator.get("ButtonClose"));
				closeButton.addActionListener(new ActionListener() {
					public void actionPerformed(ActionEvent e) {
						cancelButtonActionPerformed(e);
					}
				});	
				buttonBar.add(closeButton, new GridBagConstraints(2, 0, 1, 1, 0.0, 0.0,
					GridBagConstraints.CENTER, GridBagConstraints.BOTH,
					new Insets(0, 0, 0, 0), 0, 0));
			}
			dialogPanel.add(buttonBar, BorderLayout.SOUTH);
		}
		contentPane.add(dialogPanel, BorderLayout.CENTER);
		setMinimumSize(new Dimension(300,250));
		pack();
		setLocationRelativeTo(getOwner());
	}

	private JPanel dialogPanel;
	private JPanel contentPanel;
	private JScrollPane messageScrollPane;
	private JTextArea messageTextArea;
	private JPanel mainPanel;
	private JLabel fromLabel;
	private JLabel fromUserLabel;
	private JLabel whenLabel;
	private JLabel whenDateLabel;
	private JPanel buttonBar;
	private JButton closeButton;
}
