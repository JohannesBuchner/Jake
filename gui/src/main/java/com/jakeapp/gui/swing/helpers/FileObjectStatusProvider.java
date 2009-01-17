package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.FileObject;

import javax.swing.*;
import java.awt.*;

public class FileObjectStatusProvider {
	public static Component getStatusRendererComponent(FileObject obj) {
		return new JLabel("!!!");
	}

	public static Component getLockedRendererComponent(FileObject obj) {
		return new JLabel("!!!");
	}
}
