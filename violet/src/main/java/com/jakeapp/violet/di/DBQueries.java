package com.jakeapp.violet.di;


public interface DBQueries {

	String DB_CREATELOGTABLE = "CREATE TABLE IF NOT EXISTS log ("
			+ " UUID id, " + " Timestamp when, " + " String who, "
			+ " String what, " + " String why, " + " String how, "
			+ " Boolean known" + ")";

	String DB_CREATELOGINDEXWHEN = "CREATE INDEX IF NOT EXISTS whenindex ON log (when)";

	String DB_CREATELOGINDEXWHAT = "CREATE INDEX IF NOT EXISTS whatindex ON log (what)";

	String DB_INSERTLOG = "INSERT INTO log (id, when, who, what, why, how, known) VALUES (?, ?, ?, ?, ?, ?, ?)";

	String DB_GETLOGBYID = "SELECT id, when, who, what, why, how, known FROM log WHERE id=?";

	String DB_GETRELPATHSPROCESSED = "SELECT what FROM log WHERE known=true";

	String DB_GETRELPATHS = "SELECT what FROM log";

	String DB_SETPROCESSEDFORWHAT = "UPDATE log SET known = true WHERE what = ?";

	String DB_SETPROCESSEDBYID = "UPDATE log SET known = true WHERE id = ?";

	String DB_GETALL = "SELECT id, when, who, what, why, how, known FROM log ";

	String DB_GETPROCESSED = DB_GETALL + " WHERE known = true";

	String DB_GETUNPROCESSED = DB_GETALL + " WHERE known = false";

	String DB_GETALLFORWHAT = DB_GETALL + " WHERE what = ?";

	String DB_GETPROCESSEDFORWHAT = DB_GETALLFORWHAT + " AND known = true";

	String DB_GETUNPROCESSEDFORWHAT = DB_GETALLFORWHAT + " AND known = false";
}
