package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.services.exceptions.NoSuchJakeObjectException;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * @author peter, dominik
 * 
 */
@SuppressWarnings("serial")
public class FilesTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(FilesTableModel.class);
	private final IJakeGuiAccess jakeGuiAccess;
	private List<JakeObject> files;

	public List<JakeObject> getFiles()
	{
		log.debug("getting files");
		if(files == null)
			log.debug("files is null");
		return files;
	}

	String[] colNames = new String[] { "Name", "Size", "Tags", "Sync Status",
			"Last Changed", "User" };

	boolean[] columnEditable = new boolean[] { false, false, true, false,
			false, false };

	enum FilesColumns {
		Name, Size, Tags, SyncStatus, LastChanged, User
	}

	FilesTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing FilesTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;

		updateData();
	}

	/**
	 * Update whole table data.
	 */
	private void updateData() {
		log.info("calling updateData");
		try {
			this.files = jakeGuiAccess.getJakeObjectsByPath("/");
		} catch (NoSuchJakeObjectException e) {
			log.warn("Got NoSuchJakeObjectException: " + e.toString());
			e.printStackTrace();
		}
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return files.size();
	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		JakeObject file = files.get(rowIndex);

		FilesColumns col = FilesColumns.values()[columnIndex];
		switch (col) {
		case Name:
			return file.getName();

		case Size:
			return FilesLib.getHumanReadableFileSize(jakeGuiAccess
					.getFileSize((FileObject) file));

		case Tags:
			return JakeObjLib.getTagString(file.getTags());

		case SyncStatus:
			return jakeGuiAccess.getJakeObjectSyncStatus(file);

		case LastChanged:
			return jakeGuiAccess.getLastModified(file).toString();

		case User:
			ProjectMember pmLastModifier = jakeGuiAccess.getLastModifier(file);
			return (pmLastModifier.getNickname().isEmpty()) ? pmLastModifier
					.getUserId() : pmLastModifier.getNickname();

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}

	public void setValueAt(Object o, int rowIndex, int columnIndex)
	{
		if(columnIndex == FilesColumns.Tags.ordinal())
		{
			JakeObject foundJakeObject = files.get(rowIndex);
			log.debug("handling a tag-change event");
			String tags = (String) o;

			if (foundJakeObject != null)
			{

				log.debug("adding tags to jakeObject");
				String[] tagsArray = tags.split("[,\\s]");
				for (String sTag : tagsArray)
				{
					if (sTag.equals(",") || sTag.equals(" "))
					{
						continue;
					}

					Tag tTag = null;
					try
					{
						tTag = new Tag(sTag);
						if (!foundJakeObject.getTags().contains(tTag))
						{
							jakeGuiAccess.addTag(foundJakeObject, tTag);
						}
					}
					catch (InvalidTagNameException e)
					{
						log.debug("cought an InvalidTagNameException but ignoring "
								+ "it, because it will simply not show up in the gui" +
								" tagname is: " + sTag);
					}
				}

				// remove the non existend tags from the jakeObject
				List<String> tagsFromArray = Arrays.asList(tagsArray);
				log.debug("removing tags from jakeObject");
				Tag[] foundTags = foundJakeObject.getTags().toArray(new Tag[foundJakeObject.getTags().size()]);
				for (Tag tag : foundTags)
				{
					if (!tagsFromArray.contains(tag.toString()))
					{
						jakeGuiAccess.removeTag(foundJakeObject, tag);
					}
				}

				log.debug("creating new tag string");
				String sTags = "";

				Set<Tag> objTags = foundJakeObject.getTags();
				sTags = JakeObjLib.getTagString(objTags);
				super.setValueAt(sTags, rowIndex, columnIndex);
			}
		}
		// possible other columns go here
	}
	@Override
	public boolean isCellEditable(int rowIndex, int columnIndex) {
		return columnEditable[columnIndex];
	}

	@Override
	public String getColumnName(int columnIndex) {
		return colNames[columnIndex];
	}
	
}
