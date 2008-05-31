package com.doublesignal.sepm.jake.gui;

public class FilesLib {
	public static String getHumanReadableFileSize(long lFileSize) {
		String sFileSizeUnity;

		sFileSizeUnity = "Bytes";
		if (lFileSize > 1024) {
			lFileSize /= 1024;
			sFileSizeUnity = "KB";
		}
		if (lFileSize > 1024) {
			lFileSize /= 1024;
			sFileSizeUnity = "MB";
		}
		if (lFileSize > 1024) {
			lFileSize /= 1024;
			sFileSizeUnity = "GB";
		}

		return lFileSize + " " + sFileSizeUnity;
	}
}
