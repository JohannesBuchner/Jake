package com.doublesignal.sepm.jake.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

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
    
	private static String join(Collection<String> s, String delimiter) {
        StringBuffer buffer = new StringBuffer();
        Iterator<String> iter = s.iterator();
        while (iter.hasNext()) {
            buffer.append(iter.next());
            if (iter.hasNext()) {
                buffer.append(delimiter);
            }
        }
        return buffer.toString();
    }
	
    public static String getHumanReadableFileStatus(int status) {
    	ArrayList<String> state = new ArrayList<String>();
    	if((status & IJakeGuiAccess.SYNC_IN_CONFLICT) != 0)
    		state.add(translator.get("HumanReadeableFileStatusConflict"));
    	if((status & IJakeGuiAccess.SYNC_LOCAL_IS_LATEST) != 0)
    		state.add(translator.get("HumanReadeableFileStatusIsNewestVersion"));
    	if((status & IJakeGuiAccess.SYNC_REMOTE_IS_NEWER) != 0)
    		state.add(translator.get("HumanReadeableFileStatusNewerVersionAvailable"));
    	if((status & IJakeGuiAccess.SYNC_LOCALLY_CHANGED) != 0)
    		state.add(translator.get("HumanReadeableFileStatusLocallyChanged"));
    	if((status & IJakeGuiAccess.SYNC_EXISTS_LOCALLY) != 0)
    		state.add(translator.get("HumanReadeableFileStatusLocalCopyExists"));
    	if((status & IJakeGuiAccess.SYNC_EXISTS_REMOTELY) != 0)
    		state.add(translator.get("HumanReadeableFileStatusRemoteVersionExists"));
    	if((status & IJakeGuiAccess.SYNC_HAS_LOGENTRIES) == 0)
       		state.add(translator.get("HumanReadeableFileStatusNotInProject"));
    	if((status & IJakeGuiAccess.SYNC_NO_VALID_STATE) != 0)
       		state.add(translator.get("HumanReadeableFileStatusInvalid"));
    	return join(state, ", ");
    }

}
