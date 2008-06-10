package com.doublesignal.sepm.jake.gui;



import java.util.ArrayList;
import java.util.List;

import javax.swing.table.*;

import org.apache.log4j.Logger;
import com.doublesignal.sepm.jake.core.domain.LogEntry;



/**
 * @author philipp
 */
@SuppressWarnings("serial")
public class ViewLogDialogTableModel extends AbstractTableModel {
    private static Logger log = Logger.getLogger(ViewLogDialogTableModel.class);
    private List<LogEntry> logEntries = new ArrayList<LogEntry>();
    

    ViewLogDialogTableModel()	{
    	log.info("Initializing ViewLogDialogTableModel.");
		
		updateData();
    	
    }
    
    private void updateData() {
		log.info("Updating Log data...");
			
	}

	String[] colNames = new String[] { "Action", "User", "Time" };
    
    enum LogColumns {
		Action, User, Time
		}
    
    public int getColumnCount() {
		return colNames.length;
	}
    
    public int getRowCount() {
    	return logEntries.size();
    }
    
    public Object getValueAt(int rowIndex, int columnIndex) {
    	LogEntry logEntry = logEntries.get(rowIndex);
    	
    	LogColumns col = LogColumns.values()[columnIndex];
    	switch(col)	{
    	case Action:
    		return logEntry.getAction();
    		
    	case User:
    		return logEntry.getUserId();
    		
    	case Time:
    		return logEntry.getTimestamp();
    		
    	default:
    		throw new IllegalArgumentException(
    				"Cannot get Inoformation for column "+ columnIndex);
    	
    	
    	}
    }
    	
	@Override
	public String getColumnName(int columnIndex)	{
		return colNames[columnIndex];
	}
    
    public List<LogEntry> getLogEntries()	{
    	return logEntries;
    }


}
