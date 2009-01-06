package com.jakeapp.gui.swing.exceptions;

/**
 * Gets thrown when a project folder no longer exists
 * <p/>
 * TODO: This does not belong here, but rather in the core. Move it later.
 */
public class ProjectFolderMissingException extends Exception {
   public ProjectFolderMissingException(String s) {
      super(s);
   }
}
