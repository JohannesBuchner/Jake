package com.jakeapp.gui.swing.callbacks;

import com.jakeapp.core.domain.FileObject;
import com.jakeapp.core.synchronization.attributes.Attributed;

import java.util.List;

/**
 * This event is fired when a file is selected in the FilePanel.
 * <p/>
 * Consumers include, for example, the inspector.
 */
public interface FileSelectionChangedCallback {
	/**
	 * Inner class that saves files and provides convenience methods
	 * <p/>
	 * I think this is ugly, but Peter > me.
	 */
	public class FileSelectedEvent {
		private List<FileObject> files;

		public FileSelectedEvent(List<FileObject> files) {
			this.files = files;
		}

		public int size() {
			return this.files.size();
		}

		public boolean isSingleFileSelected() {
			return (this.files != null) && this.files.size() == 1;
		}

		public boolean isNoFileSelected() {
			return (this.files == null) || this.files.size() == 0;
		}

		public boolean isMultipleFilesSelected() {
			return (this.files != null) && this.files.size() > 1;
		}

		public FileObject getSingleFile() {
			return (this.files != null && this.files.size() == 1) ? this.files.get(0) : null;
		}

		public List<FileObject> getFiles() {
			return this.files;
		}
	}

	public void fileSelectionChanged(FileSelectedEvent event);
}
