package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.AttributedJakeObject;

import java.util.List;

/**
 * This event is fired when a file is selected in the FilePanel.
 * <p/>
 * Consumers include, for example, the inspector.
 */
public interface FileSelectionChanged {
	/**
	 * Inner class that saves files and provides convenience methods
	 * <p/>
	 * I think this is ugly, but Peter > me.
	 */
	public class FileSelectedEvent {
		private List<AttributedJakeObject<FileObject>> attributedFiles;

		public FileSelectedEvent(List<AttributedJakeObject<FileObject>> files) {
			this.attributedFiles = files;
		}

		public int size() {
			return this.attributedFiles.size();
		}

		public boolean isSingleFileSelected() {
			return (this.attributedFiles != null) && this.attributedFiles.size() == 1;
		}

		public boolean isNoFileSelected() {
			return (this.attributedFiles == null) || this.attributedFiles.size() == 0;
		}

		public boolean isMultipleFilesSelected() {
			return (this.attributedFiles != null) && this.attributedFiles.size() > 1;
		}

		public AttributedJakeObject<FileObject> getSingleFile() {
			return (this.attributedFiles != null && this.attributedFiles.size() == 1) ? this.attributedFiles.get(0) : null;
		}

		public List<AttributedJakeObject<FileObject>> getFiles() {
			return this.attributedFiles;
		}
	}

	public void fileSelectionChanged(FileSelectedEvent event);
}
