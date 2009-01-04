package com.jakeapp.gui.swing.controls;

import com.jakeapp.core.domain.ProjectMember;

import javax.swing.*;
import java.awt.*;

// @author Santhosh Kumar T - santhosh@in.fiorano.com
public class PeopleListCellEditor extends DefaultCellEditor implements ListCellEditor {
    public PeopleListCellEditor(final JCheckBox checkBox) {
        super(checkBox);
    }

    public PeopleListCellEditor(final JComboBox comboBox) {
        super(comboBox);
    }

    public PeopleListCellEditor(final JTextField textField) {
        super(textField);
    }

    /**
     * Filters the value, only shows the Nickname.
     *
     * @param list
     * @param value
     * @param isSelected
     * @param index
     * @return
     */
    public Component getListCellEditorComponent(JList list, Object value, boolean isSelected, int index) {
        delegate.setValue(((ProjectMember) value).getUserId().getNickname());
        return editorComponent;
    }
}