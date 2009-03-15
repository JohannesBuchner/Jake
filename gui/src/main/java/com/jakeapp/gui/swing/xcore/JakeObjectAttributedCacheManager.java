package com.jakeapp.gui.swing.xcore;

import com.jakeapp.core.domain.JakeObject;
import com.jakeapp.core.synchronization.attributes.Attributed;
import com.jakeapp.gui.swing.SpringCoreAccessImpl;
import com.jakeapp.gui.swing.callbacks.FilesChangedCallback;
import com.jakeapp.jake.fss.IModificationListener;

import java.util.HashMap;

public class JakeObjectAttributedCacheManager {
	private static boolean enabled = true;

	private final HashMap<JakeObject, Attributed<JakeObject>> cacheHash =
					new HashMap<JakeObject, Attributed<JakeObject>>();

	public JakeObjectAttributedCacheManager() {
		// register for changes - this listener work per project.
		//springCoreAccessImpl.addFilesChangedListener(new SyncCacheFileChangedListener(), null);
	}

	public HashMap<JakeObject, Attributed<JakeObject>> getCacheHash() {
		return cacheHash;
	}

	/**
	 * Clears the whole SyncStatus-Cache
	 */
	public void clearCache() {
		getCacheHash().clear();
	}

	public <T extends JakeObject> boolean isCached(T jakeObject) {
		return cacheHash.containsKey(jakeObject);
	}

	public <T extends JakeObject> Attributed<T> getCached(T jakeObject) {
		if(!enabled)
			return null;
		return (Attributed<T>) cacheHash.get(jakeObject);
	} 
	
	/**
	 * deletes the corresponding cached Atrributed to a given jakeObject from the cache 
	 * @param <T>
	 * @param jakeObject
	 */
	public <T extends JakeObject> void invalidateCache(T jakeObject) {
		cacheHash.remove(jakeObject);
	}

	/**
	 * Caches a JakeObject's Attributed
	 *
	 * @param jakeObject
	 * @param jakeObjectAttributed
	 * @param <T>
	 * @return
	 */
	public <T extends JakeObject> Attributed<T> cacheObject(T jakeObject,
					Attributed<T> jakeObjectAttributed) {
		if(enabled) 
			getCacheHash().put(jakeObject, (Attributed<JakeObject>) jakeObjectAttributed);
		return jakeObjectAttributed;
	}


	private class SyncCacheFileChangedListener implements FilesChangedCallback {
		@Override public void filesChanged(String relpath, IModificationListener.ModifyActions action) {

		}
	}
}