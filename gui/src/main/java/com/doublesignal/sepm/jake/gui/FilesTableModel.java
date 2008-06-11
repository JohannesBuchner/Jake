package com.doublesignal.sepm.jake.gui;

import com.doublesignal.sepm.jake.core.domain.FileObject;
import com.doublesignal.sepm.jake.core.domain.JakeObject;
import com.doublesignal.sepm.jake.core.domain.ProjectMember;
import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.core.dao.exceptions.NoSuchLogEntryException;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

import org.apache.log4j.Logger;

import javax.swing.table.AbstractTableModel;
import java.util.List;

/**
 * @author peter, dominik
 * 
 */
@SuppressWarnings("serial")
public class FilesTableModel extends AbstractTableModel {
	private static final Logger log = Logger.getLogger(FilesTableModel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
	private final IJakeGuiAccess jakeGuiAccess;
	private List<JakeObject> files;


    private long summedFilesize = 0;

    public long getSummedFilesize() {
        summedFilesize = 0;
        for(JakeObject file : files)
        {
            summedFilesize += jakeGuiAccess.getFileSize((FileObject) file);
        }
        return summedFilesize;
    }

    public int getFilesCount() {
        return files.size();
    }

    public List<JakeObject> getFiles()
	{
		log.debug("getting files");
		if(files == null)
			log.debug("files is null");
		return files;
	}

	private String[] colNames = new String[] {translator.get("FilesTableModelColumnName"),
			                          translator.get("FilesTableModelColumnSize"),
			                          translator.get("FilesTableModelColumnTags"),
			                          translator.get("FilesTableModelColumnSyncStatus"),
			                          translator.get("FilesTableModelColumnLastChanged"),
			                          translator.get("FilesTableModelColumnUser") };

	private boolean[] columnEditable = new boolean[] { false, false, true, false,
			false, false };

	private enum FilesColumns {
		Name, Size, Tags, SyncStatus, LastChanged, User
	}
	
	/**
	 * Construct a new table model, init data.
	 * @param jakeGuiAccess
	 */
	public FilesTableModel(IJakeGuiAccess jakeGuiAccess) {
		log.info("Initializing FilesTableModel.");
		this.jakeGuiAccess = jakeGuiAccess;
        updateData();
	}

	/**
	 * Update whole table data.
	 */
	public void updateData() {
		log.info("calling updateData");
        this.files = jakeGuiAccess.getFileObjects("/");
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
			return FilesLib.getHumanReadableFileStatus(jakeGuiAccess.getJakeObjectSyncStatus(file));

		case LastChanged:
			try {
				return jakeGuiAccess.getLastModified(file).toString();
			} catch (NoSuchLogEntryException e) {
				return "<File not in Project>";
			}

		case User:
			try {
				ProjectMember pmLastModifier = jakeGuiAccess.getLastModifier(file);
				return (pmLastModifier.getNickname().isEmpty()) ? pmLastModifier
						.getUserId() : pmLastModifier.getNickname();
			} catch (NoSuchLogEntryException e) {
				return translator.get("FilesTableModelFileNotInProject");
			}

		default:
			throw new IllegalArgumentException("Cannot get Information for column " + columnIndex);
		}
	}

	public void setValueAt(Object columnValue, int rowIndex, int columnIndex)
	{
		if(columnIndex == FilesColumns.Tags.ordinal())
		{
			JakeObject foundJakeObject = files.get(rowIndex);
			log.debug("handling a tag-change event");
			if (foundJakeObject != null)
			{
				String sTags = JakeObjLib.generateNewTagString(jakeGuiAccess, foundJakeObject, (String) columnValue);
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
