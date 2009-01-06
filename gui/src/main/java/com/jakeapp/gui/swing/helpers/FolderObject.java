package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

import java.util.List;
import java.util.ArrayList;

/**
 * Represents a folder in the file system
 * <p/>
 * TODO: This should eventually be moved to the domain objects
 */
public class FolderObject {
   private String relPath;
   private String name;
   private List<FileObject> fileChildren = new ArrayList<FileObject>();
   private List<FolderObject> folderChildren = new ArrayList<FolderObject>();

   public FolderObject(String relPath, String name) {
      this.relPath = relPath;
      this.name = name;
   }

   public String getRelPath() {
      return relPath;
   }

   public String getName() {
      return name;
   }

   public List<FileObject> getFileChildren() {
      return fileChildren;
   }

   public List<FolderObject> getFolderChildren() {
      return folderChildren;
   }

   public void addFolder(FolderObject folder) {
      folderChildren.add(folder);
   }

   public void addFile(FileObject file) {
      fileChildren.add(file);
   }

   public void addFolders(List<FolderObject> folders) {
      folderChildren.addAll(folders);
   }

   public void addFiles(List<FileObject> files) {
      fileChildren.addAll(files);
   }
}