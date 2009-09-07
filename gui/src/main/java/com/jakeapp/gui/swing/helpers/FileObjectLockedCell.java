package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

public class FileObjectLockedCell {
	private FileObject fileObject;

	public FileObjectLockedCell(FileObject fileObject) {
		this.fileObject = fileObject;
	}

	public FileObject getFileObject() {
		return fileObject;
	}
}