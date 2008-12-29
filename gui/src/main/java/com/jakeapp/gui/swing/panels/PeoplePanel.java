/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PeoplePanel.java
 *
 * Created on Dec 3, 2008, 2:00:21 AM
 */

package com.jakeapp.gui.swing.panels;

import com.jakeapp.gui.swing.models.PeopleMutableTreeTableNode;
import com.jakeapp.gui.swing.models.PeopleTreeTableNodeInterface;
import com.jakeapp.gui.swing.models.PeopleUserObject;
import org.jdesktop.swingx.JXTreeTable;
import org.jdesktop.swingx.table.ColumnFactory;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jdesktop.swingx.treetable.DefaultTreeTableModel;
import org.jdesktop.swingx.treetable.MutableTreeTableNode;

import javax.swing.*;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Arrays;
import java.util.List;

/**
 * @author studpete
 */
public class PeoplePanel extends javax.swing.JPanel {

    private static final List<String> theColumnNamesList = Arrays.asList(
            "Name", "User ID", "Status");

    /**
     * Creates new form PeoplePanel
     */
    public PeoplePanel() {
        initComponents();

        JXTreeTable peopleTreeTable = initTreeTable();

        this.add(new JScrollPane(peopleTreeTable), BorderLayout.CENTER);


        // Icon onlineIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResourceMap("resources/icons/user-online.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));
        //  Icon offlineIcon = new ImageIcon(Toolkit.getDefaultToolkit().getImage(JakeMainApp.class.getResourceMap("resources//icons/user-offline.png")).getScaledInstance(16, 16, Image.SCALE_SMOOTH));

        // TableColumn someColumn = jTable1.getColumnModel().getColumn(0);
        // someColumn.setCellRenderer(new IconifiedRenderer());
    }


    private JXTreeTable initTreeTable() {


        // Create a dummy user data object used by the TreeTableNode to return
        // data.
        PeopleTreeTableNodeInterface PeopleUserDataObject = new PeopleUserObject();

        // Create some AbstractMutableTreeTableNodes.
        MutableTreeTableNode myRootTreeTableNode = new PeopleMutableTreeTableNode();

        // Set the UserObject for the root node. This could be done in the
        // Constructor as well.
        myRootTreeTableNode.setUserObject(PeopleUserDataObject);

        // Add a couple more nodes to the tree to test sorting
        PeopleMutableTreeTableNode myNewMutableTreeTableNode = new PeopleMutableTreeTableNode();
        myNewMutableTreeTableNode.setUserObject(new PeopleUserObject());
        myRootTreeTableNode.insert(myNewMutableTreeTableNode, 0);
        myNewMutableTreeTableNode = new PeopleMutableTreeTableNode();
        myNewMutableTreeTableNode.setUserObject(new PeopleUserObject());
        myRootTreeTableNode.insert(myNewMutableTreeTableNode, 0);


        // ColumnFactory subclass.
        // The preferred hook for setting up your columns is the
        // configureTableColumn() method,
        // which is called automatically by the factory after the TableColumnExt
        // has been created.
        //
        // TBD--how to configure column sizes...
        //
        // This is being done via anonymous subclassing as an easy demo...better
        // to create
        // a real class in application.
        ColumnFactory myColumnFactory = new ColumnFactory() {

            @Override
            public void configureTableColumn(TableModel myModel,
                                             TableColumnExt myColumnExt) {
                super.configureTableColumn(myModel, myColumnExt);

                // Figure out which column it is and set it up appropriately.
                // Note this is not a particularily elegant or efficient way to
                // figure out which
                // column is being created but it is not called often.
                final String myColumnTitle = myColumnExt.getTitle();
                if (myColumnTitle.equals(theColumnNamesList.get(0))) {
                    myColumnExt.setEditable(false);
                    myColumnExt.setSortable(false);
                } else if (myColumnTitle.equals(theColumnNamesList.get(1))) {
                    myColumnExt.setEditable(true);
                    myColumnExt.setSortable(false);
                } else if (myColumnTitle.equals(theColumnNamesList.get(2))) {
                    myColumnExt.setEditable(false);
                    myColumnExt.setSortable(true);
                } else if (myColumnTitle.equals(theColumnNamesList.get(3))) {
                    myColumnExt.setEditable(true);
                    myColumnExt.setSortable(true);
                }
            }

        };


        // Initialize and setup the DefaultTreeTableModel.
        // This is where Column Names and Column Count is set, and the root
        // TreeTableNode.

        DefaultTreeTableModel myDefaultTreeTableModel = new DefaultTreeTableModel();

        // Here we pass in a List of Strings, the length of the list defines the
        // number of
        // columns and each String defines the title of that column. These could
        // be any
        // object with an appropriate toString method.
        myDefaultTreeTableModel.setColumnIdentifiers(theColumnNamesList);

        // Here we set the root node of the tree. Note this should happen after
        // setColumnIdentifiers() has been called.
        myDefaultTreeTableModel.setRoot(myRootTreeTableNode);

        // Initialize and setup the JXTreeTable
        JXTreeTable myJXTreeTable = new JXTreeTable();
        // This is false by default; we wouldn't see anything without adding
        // nodes :)
        myJXTreeTable.setRootVisible(true);
        // Whatever other properties you need to set...

        // Setup Highlighters...
        //Highlighter myHighlighter = new ColorHighlighter(
        //        HighlightPredicate.ROLLOVER_ROW, Color.BLUE, Color.CYAN);
        //myJXTreeTable.setHighlighters(myHighlighter);

        // Set the ColumnFactory; note that generally this is a singleton, I'm
        // just creating
        // an anonymous subclass here for convenience
        // This must be done before setting the TreeTableModel or the
        // ColumnFactory won't be
        // used.
        myJXTreeTable.setColumnFactory(myColumnFactory);

        // Set the TreeTableModel.
        // Must be done after ColumnFactory is set or it won't use the
        // ColumnFactory.
        myJXTreeTable.setTreeTableModel(myDefaultTreeTableModel);

        //show(new JScrollPane(myJXTreeTable));


        //TreeTableModel treeTableModel = new PeopleTreeTableModel(); // any TreeTableModel
        //JXTreeTable peopleTreeTable = new JXTreeTable();
        //peopleTreeTable.setTreeTableModel(treeTableModel);
        //peopleTreeTable.setScrollsOnExpand(true);
        return myJXTreeTable;
    }


    /**
     * This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setName("Form"); // NOI18N
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

}
