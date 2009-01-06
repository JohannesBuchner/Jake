package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

public class ProjectFilesTreeNode {
   private boolean folder = false;
   private boolean file = false;
   private FileObject fileObj = null;
   private FolderObject folderObj = null;

   public ProjectFilesTreeNode(FileObject fo) {
      this.fileObj = fo;
      this.file = true;
   }

   public ProjectFilesTreeNode(FolderObject fo) {
      this.folderObj = fo;
      this.folder = true;
   }

   public FileObject getFileObject() {
      return fileObj;
   }

   public FolderObject getFolderObject() {
      return folderObj;
   }

   public boolean isFolder() {
      return folder;
   }

   public boolean isFile() {
      return file;
   }
}
