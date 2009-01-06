package com.jakeapp.gui.swing.actions.abstracts;

import com.jakeapp.core.domain.FileObject;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTreeTable;

public abstract class FileAction extends ProjectAction {
   private static final Logger log = Logger.getLogger(FileAction.class);

   private JXTreeTable fileTable;

   public FileAction(JXTreeTable fileTable) {
      super();
      this.fileTable = fileTable;
   }

   public JXTreeTable getFileTable() {
      return fileTable;
   }

   public void setFile(JXTreeTable fileTable) {
      this.fileTable = fileTable;
   }
}
