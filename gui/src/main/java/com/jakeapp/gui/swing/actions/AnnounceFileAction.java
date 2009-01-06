package com.jakeapp.gui.swing.actions;

import com.jakeapp.gui.swing.actions.abstracts.FileAction;
import com.jakeapp.gui.swing.JakeMainView;

import java.awt.event.ActionEvent;

import org.jdesktop.swingx.JXTreeTable;

import javax.swing.*;

public class AnnounceFileAction extends FileAction {
   public AnnounceFileAction(JXTreeTable fileTable) {
      super(fileTable);

      String actionStr = JakeMainView.getMainView().getResourceMap().
            getString("announceMenuItem.text");

      putValue(Action.NAME, actionStr);
   }

   @Override
   public void actionPerformed(ActionEvent e) {
      // TODO: Implement me!
   }
}