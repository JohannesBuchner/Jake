package com.jakeapp.gui.swing.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.io.File;
import java.util.Date;
import java.util.ArrayList;

import com.jakeapp.gui.swing.helpers.*;
import com.jakeapp.gui.swing.JakeMainApp;
import com.jakeapp.core.domain.FileObject;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 6, 2009
 * Time: 2:02:37 PM
 * To change this template use File | Settings | File Templates.
 */
public class FolderObjectsTreeTableModel extends AbstractTreeTableModel {
   public FolderObjectsTreeTableModel(FolderObject folder) {
      super(folder);
   }

   @Override
   public int getColumnCount() {
      return 4;
   }

   @Override
   public Object getValueAt(Object node, int column) {
      if (node instanceof FileObject) {
         FileObject file = (FileObject) node;

         switch (column) {
            case 0:
               return new ProjectFilesTreeNode(file);
            case 1:
               return isLeaf(node) ? FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(file), 0, false, true) : "";
            case 2:
               return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(file));
            case 3:
               return isLeaf(node) ? new TagSet() : null;
         }
      } else if (node instanceof FolderObject) {
         FolderObject folder = (FolderObject) node;

         switch (column) {
            case 0:
               return new ProjectFilesTreeNode(folder);
            case 1:
               // Size of directory = 0
               return "";
            case 2:
               // No last modified date for folder
               return "";
            case 3:
               return null;
         }
      }

      return null;
   }

   @Override
   public Object getChild(Object parent, int index) {
      if (!(parent instanceof FolderObject)) return null;
      FolderObject fo = (FolderObject) parent;

      ArrayList<Object> children = new ArrayList<Object>();
      children.addAll(fo.getFolderChildren());
      children.addAll(fo.getFileChildren());

      return children.get(index);
   }

   @Override
   public int getChildCount(Object parent) {
      if (!(parent instanceof FolderObject)) return 0;

      FolderObject fo = (FolderObject) parent;
      return fo.getFileChildren().size() + fo.getFolderChildren().size();
   }

   @Override
   public int getIndexOfChild(Object parent, Object child) {
      if (!(parent instanceof FolderObject)) return -1;
      FolderObject fo = (FolderObject) parent;

      ArrayList<Object> children = new ArrayList<Object>();
      children.addAll(fo.getFolderChildren());
      children.addAll(fo.getFileChildren());

      return children.indexOf(child);
   }

   @Override
   public Class<?> getColumnClass(int column) {
      switch (column) {
         case 0:
            return ProjectFilesTreeNode.class;
         case 1:
            return String.class;
         case 2:
            return String.class;
         case 3:
            return TagSet.class;
         default:
            return super.getColumnClass(column);
      }
   }
}
