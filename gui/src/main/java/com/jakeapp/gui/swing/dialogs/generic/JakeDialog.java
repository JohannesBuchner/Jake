package com.jakeapp.gui.swing.dialogs.generic;

import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.JakeMainView;
import com.jakeapp.gui.swing.helpers.Platform;
import net.miginfocom.swing.MigLayout;
import org.apache.log4j.Logger;
import org.jdesktop.application.ResourceMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * A JakeDialog is a standartized sheet/dialog for jake.
 * It consists of a Title, a Icon and a custom area.
 *
 * @author: studpete
 */
public abstract class JakeDialog extends EscapeDialog {
	private static final Logger log = Logger.getLogger(JakeDialog.class);
	private ResourceMap resourceMap;
	private ResourceMap commonResourceMap;
	private JLabel dialogTitleLabel;
	private JLabel pictureLabel;
	private JLabel explanationLabel;
	private JPanel buttonsPanel;

	public Project getProject() {
		return project;
	}

	public void setProject(Project project) {
		this.project = project;
	}

	private Project project;

	/**
	 * Constructs a Jake Dialog/Sheet.
	 * Uses JakeMainView as parent Frame.
	 *
	 * @param project
	 */
	public JakeDialog(Project project) {
		super(JakeMainView.getMainView().getFrame());
		this.project = project;
	}

	/**
	 * Constructs a Jake Dialog/Sheet.
	 *
	 * @param project
	 * @param owner
	 */
	public JakeDialog(Project project, Frame owner) {
		super(owner);
		this.project = project;
	}

	/**
	 * Initializes the common dialog.
	 * Calls initComponents from child.
	 */
	protected void initDialog() {
		// load the common resource map
		commonResourceMap = org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(JakeDialog.class);

		setTitle(commonResourceMap.getString("jakeDialogTitle"));

		initCommonComponents();
	}

	private void initCommonComponents() {
		this.setLayout(new MigLayout("wrap 1, insets dialog, fill"));

		dialogTitleLabel = new JLabel(getTitle());
		dialogTitleLabel.setFont(Platform.getStyler().getSheetLargeFont());
		this.add(dialogTitleLabel, "span 2, gapbottom 10");

		pictureLabel = new JLabel();
		this.add(pictureLabel, "dock west, gap 10 10");

		explanationLabel = new JLabel();
		this.add(explanationLabel, "growy, gapbottom 10");

		buttonsPanel = new JPanel(new MigLayout("nogrid, fill, ins 0"));

		// call overloaded initComponents and add default button.
		// There has to be at least one button!
		JButton defaultBtn = initComponents();
		getButtonsPanel().add(defaultBtn, "tag ok, aligny bottom");

		// add button panel as last
		this.add(buttonsPanel, "grow");

		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		this.getRootPane().setDefaultButton(defaultBtn);
	}

	/**
	 * Calls initComponents, returns the default selected button
	 *
	 * @return
	 */
	protected abstract JButton initComponents();

	/**
	 * Adds a default "Cancel" Button to the App.
	 */
	protected void addCancelBtn() {
		JButton closeBtn = new JButton(commonResourceMap.getString("cancelButton"));
		closeBtn.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent actionEvent) {
				setVisible(false);
			}
		});
		buttonsPanel.add(closeBtn, "tag cancel, aligny bottom, gaptop 10");
	}


	/**
	 * Set the dialog title label.
	 * This is not the dialog window title!
	 * The window title is always "Jake" (sheets don't have a title at all)
	 * The Title has a larger font, which is customizeable per platform in styler package.
	 *
	 * @param str: string of title
	 */
	protected void setDialogTitle(String str) {
		log.debug("Dialog Title: " + str);
		dialogTitleLabel.setText(str);
	}

	/**
	 * Set the Picture.
	 * Empty per default. Accepts Icon Path.
	 *
	 * @param path: icon path ("/icons/icon.png")
	 */
	protected void setPicture(String path) {
		pictureLabel.setIcon(new ImageIcon(Toolkit.getDefaultToolkit().getImage(
				  getClass().getResource(path))));
	}

	/**
	 * Set the main message for the dialog.
	 * Use html within msg to use multiple lines.
	 *
	 * @param locmsg: message to show.
	 */
	protected void setMessage(String locmsg) {
		explanationLabel.setText(getResourceMap().getString(locmsg));
	}


	/**
	 * Return the Button Panel.
	 * Add your own buttons here.
	 * The default button will be added automatically.
	 *
	 * @return button panel
	 */
	protected JPanel getButtonsPanel() {
		return buttonsPanel;
	}


	protected ResourceMap getResourceMap() {
		return resourceMap;
	}

	protected void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	/**
	 * Shows the Dialog. Static, configures modality and size, shows dialog.
	 *
	 * @param width:  dialog width
	 * @param height: dialog height
	 */
	protected void showDialogSized(int width, int height) {

		// pack and add sanity checks for size
		this.pack();
		if (this.getSize().getWidth() < width || this.getSize().getWidth() > 800) {
			this.setSize(width, height);
		} else {
			this.setSize((int) this.getSize().getWidth(), height);
		}
		// set minimum size slightly smaller, so that the dialog does not enlarge,
		// when the user tries to resize (some jdk-bug, i think)
		this.setMinimumSize(new Dimension(width - 10, height - 10));
		this.setResizable(true);

		// animation only works with mac os & sheets
		this.setAnimated(true);

		this.setVisible(true);
	}

	/**
	 * Closes the Dialog/Sheet.
	 * Pretty much sets visibility to false.
	 */
	protected void closeDialog() {
		this.setVisible(false);
	}
}
