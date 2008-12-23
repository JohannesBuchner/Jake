package com.jakeapp.gui.swing.models;

/**
 * User: studpete
 * Date: Dec 9, 2008
 * Time: 2:01:12 AM
 */

// Create an interface that the TreeTableNode Object will use to interface
// with user Objects.
// This way multiple user Objects can be stored as Nodes in the table and
// the TreeTableNode
// implementation will know how to extract their values. There are of course
// plenty of other
// ways to do this.
public interface PeopleTreeTableNodeInterface {
    public Object getColumn1();

    public Object getColumn2();

    public Object getColumn3();

    public void setColumn1(Object myValue);

    public void setColumn2(Object myValue);

    public void setColumn3(Object myValue);
}