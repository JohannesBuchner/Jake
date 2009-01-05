package com.jakeapp.gui.swing.controls;

import javax.swing.*;
import javax.swing.table.TableCellEditor;
import java.awt.*;

/**
 * Created by IntelliJ IDEA.
 * User: Chris
 * Date: Jan 5, 2009
 * Time: 10:17:26 PM
 * To change this template use File | Settings | File Templates.
 */
public class FilesTreeTableTagCellEditor extends AbstractCellEditor implements TableCellEditor {
   @Override
   public Object getCellEditorValue() {
      return "blubb";
   }

   @Override
   public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
      return new TagSetEditor();
   }
}
