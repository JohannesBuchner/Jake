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

    public static String getHumanReadableFileStatus(int status) {
        switch (status) {
            default:
            case 100:
                return "no valid status: "+status;

            case 101:
                return "Remote File -> pull";
            case 102:
                return "local file, not in project";
            case 103:
                return "File in Sync";
            case 104:
                return "File remotely changed";
            case 105:
                return "File locally changed";
            case 106:
                return "File in conflict";
            case 107:
                return "Local file missing";
        }
    }

}
