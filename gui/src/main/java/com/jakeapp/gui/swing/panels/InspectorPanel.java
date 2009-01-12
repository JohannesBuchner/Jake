package com.jakeapp.gui.swing.panels;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.domain.Project;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.gui.swing.callbacks.FileSelectionChanged;
import com.jakeapp.gui.swing.callbacks.ProjectChanged;
import com.jakeapp.gui.swing.callbacks.ProjectSelectionChanged;
import com.jakeapp.gui.swing.controls.ETable;
import com.jakeapp.gui.swing.helpers.ConfigControlsHelper;
import com.jakeapp.gui.swing.helpers.FileObjectHelper;
import com.jakeapp.gui.swing.helpers.Platform;
import com.jakeapp.gui.swing.helpers.Translator;
import com.jakeapp.gui.swing.models.EventsTableModel;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.swingx.JXPanel;
import org.jdesktop.swingx.JXTable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;

/**
 * Inspector Panel
 * Shows extended File Info:
 * Icon, Name, Last access time+user, Size, History Table
 * Tags, full path
 *
 * @author: studpete
 */
public class InspectorPanel extends JXPanel implements ProjectChanged, ProjectSelectionChanged, FileSelectionChanged {

	private Project project;
	private ResourceMap resourceMap;

	private JXTable eventsTable;
	private JLabel icoLabel;
	private FileObject fileObject;
	private JLabel nameLabel;
	private JLabel sizeLabel;
	private JLabel lastAccessTimeAndUser;
	private JLabel tagsLabel;
	private JLabel fullPathLabel;
	private EventsTableModel eventsTableModel;

	public InspectorPanel() {

		// load the resource map
		setResourceMap(org.jdesktop.application.Application.getInstance(
				  JakeMainApp.class).getContext()
				  .getResourceMap(InspectorPanel.class));

		// register for events
		JakeMainApp.getApp().getCore().addProjectChangedCallbackListener(this);
		JakeMainApp.getApp().addProjectSelectionChangedListener(this);
		FilePanel.getInstance().addFileSelectionListener(this);


		this.setLayout(new MigLayout("wrap 2, fill, debug"));

		initComponents();
	}

	private void initComponents() {

		this.setMinimumSize(new Dimension(250, 250));
		// TODO: styler?
		this.setBackground(SystemColor.window);


		// config the history table
		this.add(new JLabel(getResourceMap().getString("historyLabel")));

		eventsTableModel = new EventsTableModel();
		eventsTable = new ETable();
		eventsTable.setModel(eventsTableModel);
		ConfigControlsHelper.configEventsTable(eventsTable);
		this.add(eventsTable, "dock south, grow");

		// TODO: make a proper layout!

		// add icon
		icoLabel = new JLabel();
		this.add(icoLabel);

		// add name
		nameLabel = new JLabel();
		this.add(nameLabel, "wrap");

		// add size
		sizeLabel = new JLabel();
		this.add(sizeLabel, "wrap");

		// last time+user modified
		this.add(new JLabel(getResourceMap().getString("modifiedLabel")));
		lastAccessTimeAndUser = new JLabel();
		this.add(lastAccessTimeAndUser, "wrap");

		// add tags
		this.add(new JLabel(getResourceMap().getString("tagsLabel")));
		tagsLabel = new JLabel("TODO");
		this.add(tagsLabel, "wrap");

		// add full path
		this.add(new JLabel(getResourceMap().getString("pathLabel")));
		fullPathLabel = new JLabel();
		this.add(fullPathLabel, "wrap");
	}

	/**
	 * Updates the panel with current data
	 */
	public void updatePanel() {

		if (getFileObject() != null) {
			File file = getFileObject().getAbsolutePath();
			icoLabel.setIcon(Platform.getToolkit().getFileIcon(file, 32));

			nameLabel.setText(FileObjectHelper.getName(getFileObject().getAbsolutePath()));
			sizeLabel.setText(FileObjectHelper.getSizeHR(getFileObject()));

			// TODO: @Chris: update tags

			fullPathLabel.setText(FileObjectHelper.getPath(getFileObject().getAbsolutePath()));

			lastAccessTimeAndUser.setText(Translator.get(getResourceMap(), "byLabel",
					  FileObjectHelper.getTimeRel(getFileObject()),
					  FileObjectHelper.getLastModifier(getFileObject())));

			eventsTableModel.setProject(getProject());
			eventsTableModel.setJakeObject(getFileObject());
		} else {
			icoLabel.setIcon(null);
			nameLabel.setText("");
			sizeLabel.setText("");
			fullPathLabel.setText("");
			lastAccessTimeAndUser.setText("");

			// null model
			eventsTable.setModel(new DefaultTableModel());
		}
	}

	protected ResourceMap getResourceMap() {
		return resourceMap;
	}

	protected void setResourceMap(ResourceMap resourceMap) {
		this.resourceMap = resourceMap;
	}

	public FileObject getFileObject() {
		return fileObject;
	}

	public void setFileObject(FileObject fileObject) {
		this.fileObject = fileObject;

		updatePanel();
	}


	@Override
	public void projectChanged(ProjectChangedEvent ev) {
		updatePanel();
	}

	@Override
	public void setProject(Project pr) {
		this.project = pr;

		updatePanel();
	}

	public Project getProject() {
		return project;
	}

	@Override
	public void fileSelectionChanged(FileSelectedEvent event) {
		setFileObject(event.getSingleFile());
	}
}


