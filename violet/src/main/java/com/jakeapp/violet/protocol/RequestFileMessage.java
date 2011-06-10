package com.jakeapp.violet.protocol;

import com.jakeapp.violet.model.JakeObject;

public class RequestFileMessage extends Message {
	private JakeObject jakeObject;

	public JakeObject getJakeObject() {
		return jakeObject;
	}

	public void setJakeObject(JakeObject jakeObject) {
		this.jakeObject = jakeObject;
	}
}
