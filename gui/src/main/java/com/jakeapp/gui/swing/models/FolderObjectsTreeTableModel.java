package com.jakeapp.gui.swing.models;

import org.jdesktop.swingx.treetable.AbstractTreeTableModel;

import java.io.File;
import java.util.Date;

import com.jakeapp.gui.swing.helpers.TagSet;
import com.jakeapp.gui.swing.helpers.FileUtilities;
import com.jakeapp.gui.swing.helpers.TimeUtilities;
import com.jakeapp.gui.swing.helpers.FolderObject;
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
               return file;
            case 1:
               return isLeaf(node) ? FileUtilities.getSize(JakeMainApp.getApp().getCore().getFileSize(file), 0, false, true) : "";
            case 2:
               return TimeUtilities.getRelativeTime(JakeMainApp.getApp().getCore().getFileLastModified(file));
            case 3:
               return isLeaf(node) ? new TagSet() : null;
         }
      } else if (node instanceof FolderObject) {
         FolderObject file = (FolderObject) node;

         switch (column) {
            case 0:
               return file;
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
      return null;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public int getChildCount(Object parent) {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public int getIndexOfChild(Object parent, Object child) {
      return 0;  //To change body of implemented methods use File | Settings | File Templates.
   }

   @Override
   public Class<?> getColumnClass(int column) {
      switch (column) {
         case 0:
            return File.class;
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
