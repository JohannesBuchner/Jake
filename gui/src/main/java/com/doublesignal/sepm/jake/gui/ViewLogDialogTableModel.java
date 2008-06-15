package com.doublesignal.sepm.jake.gui;



import java.util.ArrayList;
import java.util.List;

import javax.swing.table.*;

import org.apache.log4j.Logger;
import com.doublesignal.sepm.jake.core.domain.LogEntry;
import com.doublesignal.sepm.jake.core.domain.JakeObject;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;

import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;




/**
 * @author philipp
 */
@SuppressWarnings("serial")
public class ViewLogDialogTableModel extends AbstractTableModel {
	
	private static final Logger log = Logger.getLogger(ViewLogDialogTableModel.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
    private List<LogEntry> logEntries = new ArrayList<LogEntry>();
    private final IJakeGuiAccess jakeGuiAccess;
    private JakeObject jakeObject = null;

    
    
    
    ViewLogDialogTableModel(IJakeGuiAccess jakeGuiAccess , JakeObject jakeObject)	{
    	log.info("Initializing ViewLogDialogTableModel...");
    	
    	if(jakeObject != null)
    	this.jakeObject = jakeObject;
    	this.jakeGuiAccess = jakeGuiAccess;
		updateData();
    	
    }
    
    public void updateData() {
		//test if current Jake object is null, if so all log entries will be shown
    	log.info("Updating Log data...");
		if (jakeObject!=null)
			this.logEntries = jakeGuiAccess.getLog(jakeObject);
		else
		this.logEntries = jakeGuiAccess.getLog();
		
	}

	String[] colNames = new String[] { translator.get("ViewLogDialogTableModelColumnAction"),
			translator.get("ViewLogDialogTableModelColumnUser"),
			translator.get("ViewLogDialogTableModelColumnComment"),
			translator.get("ViewLogDialogTableModelColumnTime") };
    
    enum LogColumns {
		Action, User, Comment, Time
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
    	
    	case Comment:
    		return logEntry.getComment();
    		
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
