package com.jakeapp.jake.ics.filetransfer.negotiate;

import com.jakeapp.jake.ics.UserId;
import com.jakeapp.jake.ics.filetransfer.AdditionalFileTransferData;


public class FileRequest {

	private AdditionalFileTransferData data;

	private UserId peer;

	private String fileName;

	private long fileSize;

	private boolean incoming;

	public FileRequest(String fileName, boolean incoming, UserId peer,
			AdditionalFileTransferData data) {
		this(fileName, incoming, peer);
		this.data = data;
	}

	public FileRequest(String fileName, boolean incoming, UserId peer) {
		super();
		this.fileName = fileName;
		this.incoming = incoming;
		this.peer = peer;
	}

	public UserId getPeer() {
		return this.peer;
	}

	public AdditionalFileTransferData getData() {
		return this.data;
	}

	public String getFileName() {
		return this.fileName;
	}

	public Boolean isIncoming() {
		return this.incoming;
	}

	public void setData(AdditionalFileTransferData data) {
		this.data = data;
	}

	public long getFileSize() {
		return fileSize;
	}


	public void setFileSize(long fileSize) {
		this.fileSize = fileSize;
	}

	public String toString() {
		return getFileName() + " " + (isIncoming() ? "from" : "to") + " "
				+ getPeer();
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((fileName == null) ? 0 : fileName.hashCode());
		result = prime * result + ((peer == null) ? 0 : peer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		FileRequest other = (FileRequest) obj;
		if (fileName == null) {
			if (other.fileName != null)
				return false;
		} else if (!fileName.equals(other.fileName))
			return false;
		if (peer == null) {
			if (other.peer != null)
				return false;
		} else if (!peer.equals(other.peer))
			return false;
		return true;
	}
}
