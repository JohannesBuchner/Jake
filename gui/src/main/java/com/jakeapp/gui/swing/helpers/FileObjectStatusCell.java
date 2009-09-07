package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

public class FileObjectStatusCell {
	private FileObject fileObject;

	public FileObjectStatusCell(FileObject fileObject) {
		this.fileObject = fileObject;
	}

	public FileObject getFileObject() {
		return fileObject;
	}
}