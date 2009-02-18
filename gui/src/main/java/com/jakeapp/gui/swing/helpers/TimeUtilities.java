package com.jakeapp.gui.swing.helpers;

import org.apache.log4j.Logger;

import java.util.Date;

/**
 * @author: studpete
 */
public class TimeUtilities {
	private static final Logger log = Logger.getLogger(TimeUtilities.class);

	// Date & Time constants - just in case they change anytime soon ;)
	public static int SECOND = 1;
	public static int MINUTE = 60 * SECOND;
	public static int HOUR = 60 * MINUTE;
	public static int DAY = 24 * HOUR;
	public static int MONTH = 30 * DAY;
	public static int YEAR = 365 * MONTH;

	/**
	 * Makes a fancy relative time description from a date object (e.g.
	 * "2 minutes ago", "5 days ago", "2 years ago", ...)
	 *
	 * @param date Any date
	 * @return A string containing a relative description of the date
	 */
	public static String getRelativeTime(Date date) {
		if (date == null) {
			log.warn("tried to get relative time with NULL date");
			return "<DATE IS NULL>";
		}

		long now = System.currentTimeMillis();
		long then = date.getTime();

		long delta = (now - then) / 1000;

		if (delta < 1 * MINUTE) {
			return "less than a minute ago";
		}
		if (delta < 2 * MINUTE) {
			return "a minute ago";
		}
		if (delta < 45 * MINUTE) {
			return (delta / MINUTE) + " minutes ago";
		}
		if (delta < 90 * MINUTE) {
			return "an hour ago";
		}
		if (delta < 24 * HOUR) {
			return (delta / HOUR) + " hours ago";
		}
		if (delta < 48 * HOUR) {
			return "yesterday";
		}
		if (delta < 30 * DAY) {
			return (delta / DAY) + " days ago";
		}
		if (delta < 12 * MONTH) {
			long months = delta / MONTH;
			return months <= 1 ? "one month ago" : months + " months ago";
		} else {
			long years = delta / YEAR;
			return years <= 1 ? "one year ago" : years + " years ago";
		}
	}

	public static String getRelativeTime(long date) {
		return getRelativeTime(new Date(date));
	}
}
