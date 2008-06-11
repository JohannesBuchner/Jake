package com.doublesignal.sepm.jake.gui;

import org.apache.log4j.Logger;

import com.doublesignal.sepm.jake.core.services.IJakeGuiAccess;
import com.doublesignal.sepm.jake.gui.i18n.ITranslationProvider;
import com.doublesignal.sepm.jake.gui.i18n.TranslatorFactory;

public class FilesLib {
	private static final Logger log = Logger.getLogger(FilesLib.class);
	
	private static final ITranslationProvider translator = TranslatorFactory.getTranslator();
	
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
            case IJakeGuiAccess.SYNC_NO_VALID_STATE:
                return translator.get("HumanReadeableFileStatusInvalid") + status;

            case IJakeGuiAccess.SYNC_FILE_IS_REMOTE:
                return translator.get("HumanReadeableFileStatusRemote");
            case IJakeGuiAccess.SYNC_LOCAL_FILE_NOT_IN_PROJECT:
                return translator.get("HumanReadeableFileStatusNotInProject");
            case IJakeGuiAccess.SYNC_FILE_IN_SYNC:
                return translator.get("HumanReadeableFileStatusInSync");
            case IJakeGuiAccess.SYNC_FILE_REMOTELY_CHANGED:
                return translator.get("HumanReadeableFileStatusRemotelyChanged");
            case IJakeGuiAccess.SYNC_FILE_LOCALLY_CHANGED:
                return translator.get("HumanReadeableFileStatusLocallyChanged");
            case IJakeGuiAccess.SYNC_FILE_IN_CONFLICT:
                return translator.get("HumanReadeableFileStatusConflict");
            case IJakeGuiAccess.SYNC_FILE_DELETED_LOCALLY:
                return translator.get("HumanReadeableFileStatusMissing");
        }
    }

}
