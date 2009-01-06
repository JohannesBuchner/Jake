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

   @Deprecated
   public String getRelPath() {
      if (this.file) {
         return fileObj.getRelPath();
      } else if (this.folder) {
         return folderObj.getRelPath();
      } else {
         return "NOT_A_FILE_OR_FOLDER";
      }
   }

   public ProjectFilesTreeNode(Object o) {
      if (o instanceof FileObject) {
         this.fileObj = (FileObject) o;
         this.file = true;
      } else if (o instanceof FolderObject) {
         this.folderObj = (FolderObject) o;
         this.folder = true;
      } else {
         throw new IllegalArgumentException();
      }
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
