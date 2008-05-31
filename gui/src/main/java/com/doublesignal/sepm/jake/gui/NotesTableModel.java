package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.NoteObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.Tag;
import com.doublesignal.sepm.jake.core.domain.exceptions.InvalidTagNameException;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.List;
import java.util.Arrays;
import java.util.Set;

@SuppressWarnings("serial")
/**
 * @author peter
 */
public class NotesTableModel extends AbstractTableModel {
	private static Logger log = Logger.getLogger(NotesTableModel.class);
	private List<NoteObject> notes;
	private final IJakeGuiAccess jakeGuiAccess;

	NotesTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing NoteTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;

		updateData();
	}

	String[] colNames = new String[] { "Title", "Tags", "Last changed", "User" };
	boolean[] columnEditable = new boolean[] { false, true, true, false };

	enum NotesColumns {
		Title, Tags, LastChanged, User
	}

	public int getColumnCount() {
		return colNames.length;
	}

	public int getRowCount() {
		return notes.size();
	}

	/**
	 * Updates the view for notes, get new notes from GuiAccess
	 */
	private void updateData() {
		log.info("Updating Notes data...");
		notes = jakeGuiAccess.getNotes();

	}

	public Object getValueAt(int rowIndex, int columnIndex) {
		NoteObject note = notes.get(rowIndex);

		NotesColumns col = NotesColumns.values()[columnIndex];
		switch (col) {
		case Title:
			return note.getName();

		case Tags:
			return JakeObjLib.getTagString(note.getTags());

		case LastChanged:
			return jakeGuiAccess.getLastModified(note).toString();

		case User:
			return jakeGuiAccess.getLastModifier(note).getNickname();

		default:
			throw new IllegalArgumentException(
					"Cannot get Information for column " + columnIndex);
		}
	}


	public void setValueAt(Object o, int rowIndex, int columnIndex)
	{
		if(columnIndex == NotesColumns.Tags.ordinal())
		{
			JakeObject foundJakeObject = notes.get(rowIndex);
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
