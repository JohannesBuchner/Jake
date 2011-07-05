package com.jakeapp.violet.di;

import java.util.UUID;

import com.jakeapp.jake.ics.ICService;
import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.IFileTransferService;
import com.jakeapp.jake.ics.msgservice.IMsgService;


public interface ICSFactory {

	ICService getICS();

	ICService getICS(UUID project);

	IFileTransferService getFileTransferService(IMsgService msg, UserId user,
			ICService ics);
}
