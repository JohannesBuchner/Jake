package com.doublesignal.sepm.jake.fss;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class FileLauncher {
	private Desktop desktop;
	public FileLauncher() throws NoSuchAlgorithmException{
		if (!Desktop.isDesktopSupported())
			throw new NoSuchAlgorithmException("Desktop not supported");
		
		desktop = Desktop.getDesktop();
		
		if (!desktop.isSupported(Desktop.Action.OPEN)) 
			throw new NoSuchAlgorithmException("Open not supported in Desktop");

	}
	public void launchFile(File f) 
		throws InvalidFilenameException, LaunchException 
	{
		try {
			desktop.open(f);
		} catch (IOException e) {
			throw new LaunchException(e);
		}
	}

}
