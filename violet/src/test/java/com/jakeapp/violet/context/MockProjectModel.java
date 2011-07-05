package com.jakeapp.violet.context;

import com.jakeapp.jake.fss.IFSService;
import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.violet.model.Log;
import com.jakeapp.violet.model.ProjectPreferences;


public class MockProjectModel extends ProjectModel {

	public MockProjectModel(IFSService fss, Log log,
			ProjectPreferences preferences, ICService ics,
			IFileTransferService transfer) {
		super(fss, log, preferences, ics, transfer);
	}

}
