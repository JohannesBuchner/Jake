package com.doublesignal.sepm.jake.gui;
import com.doublesignal.sepm.jake.core.domain.JakeObject;


import com.doublesignal.sepm.jake.sync.ISyncService;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import javax.swing.*;
import javax.swing.border.*;


import org.apache.log4j.Logger;


/**
 * @author Peter Steinberger, philipp
 */
@SuppressWarnings("serial")
public class ViewLogDialog extends JDialog {
	private static final Logger log = Logger.getLogger(ViewLogDialog.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();

    
    
    ISyncService ss = null;
    IJakeGuiAccess jakeGuiAccess;
   
    Date d = new Date();
    
     
   
   
    public ViewLogDialog(Frame owner,IJakeGuiAccess jakeGuiAccess) {
		super(owner);
		this.jakeGuiAccess = jakeGuiAccess;
		initComponents();
    }
    
    public ViewLogDialog(Frame owner) {
		super(owner);
		
		initComponents();
		log.info("DONE");
    }

	public ViewLogDialog(Dialog owner) {
		super(owner);
		initComponents();
	}

    public ViewLogDialog setJakeObject(JakeObject jakeObject)
    {
        log.info("Set jakeObject to  "+ jakeObject.getName());
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
		viewlogDialogTableModel = new ViewLogDialogTableModel(jakeGuiAccess);
		//======== this ========
		setTitle("View Log");
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
					logTable.setModel(viewlogDialogTableModel);
			
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
	private ViewLogDialogTableModel viewlogDialogTableModel;
}
