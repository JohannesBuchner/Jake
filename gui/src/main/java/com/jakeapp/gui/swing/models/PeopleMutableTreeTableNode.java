package com.jakeapp.gui.swing.models;    // AbstractMutableTreeTableNode subclass.
// We must implement getColumnCount() here as well because of lots of
// checking
// being done by SwingX. Just refer to the same list that the
// DefaultTreeTableModel
// is using, or whatever mechanism you choose to keep them in sync.

import org.jdesktop.swingx.treetable.AbstractMutableTreeTableNode;

public class PeopleMutableTreeTableNode extends AbstractMutableTreeTableNode {
    @Override
    public boolean isEditable(int myColumn) {
        // Hard coded to true, will control editing via the ColumnFactory.
        // Implement appropriately as you need your editing granularity, but
        // it you
        // want to control via the ColumnFactory make sure you hard code to
        // true here.
        return true;
    }

    @Override
    public int getColumnCount() {
        // return the same thing that the DefaultTreeTableModel uses to
        // determine size
        return 3;
    }

    @Override
    public Object getValueAt(int myColumn) {
        // Use the column index to determine what to return here.
        // Note that this is using the interface I defined to cast the
        // objects; could
        // be a class too, or whatever you want to do.
        switch (myColumn) {
            case 0:
                return ((PeopleTreeTableNodeInterface) getUserObject())
                        .getColumn1();
            case 1:
                return ((PeopleTreeTableNodeInterface) getUserObject())
                        .getColumn2();
            case 2:
                return ((PeopleTreeTableNodeInterface) getUserObject())
                        .getColumn3();

            default:
                throw new ArrayIndexOutOfBoundsException(
                        "TreeTableNode--getValueAt called for bad column index");
        }
    }

    @Override
    public void setValueAt(Object myValue, int myColumn) {
        super.setValueAt(myValue, myColumn);
        switch (myColumn) {
            case 0:
                ((PeopleTreeTableNodeInterface) getUserObject())
                        .setColumn1(myValue);
                break;
            case 1:
                ((PeopleTreeTableNodeInterface) getUserObject())
                        .setColumn2(myValue);
                break;
            case 2:
                ((PeopleTreeTableNodeInterface) getUserObject())
                        .setColumn3(myValue);
                break;
            default:
                throw new ArrayIndexOutOfBoundsException(
                        "TreeTableNode--setValueAt called for bad column index");
        }
    }
}