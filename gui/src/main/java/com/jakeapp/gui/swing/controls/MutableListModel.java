package com.jakeapp.gui.swing.controls;

import javax.swing.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
public interface MutableListModel extends ListModel {
    public boolean isCellEditable(int index);

    public void setValueAt(Object value, int index);
} 