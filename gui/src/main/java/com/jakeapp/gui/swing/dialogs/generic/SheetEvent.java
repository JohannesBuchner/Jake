/*
 * @(#)SheetEvent.java  1.0  26. September 2005
 *
 * Copyright (c) 1996-2006 by the original authors of JHotDraw
 * and all its contributors.
 * All rights reserved.
 *
 * The copyright of this software is owned by the authors and
 * contributors of the JHotDraw project ("the copyright holders").
 * You may not use, copy or modify this software, except in
 * accordance with the license agreement you entered into with
 * the copyright holders. For details see accompanying license terms.
 */

package com.jakeapp.gui.swing.dialogs.generic;

import javax.swing.*;
import java.util.EventObject;

/**
 * SheetEvent.
 *
 * @author Werner Randelshofer
 * @version 1.0 26. September 2005 Created.
 */
public class SheetEvent extends EventObject {
	private JComponent pane;
	private int option;
	private Object value;
	private Object inputValue;

	/**
	 * Creates a new instance.
	 * @param source
	 */
	public SheetEvent(JSheet source) {
		super(source);
	}

	/**
	 * Creates a new instance.
	 * @param source
	 * @param fileChooser
	 * @param option
	 * @param value
	 */
	public SheetEvent(JSheet source, JFileChooser fileChooser, int option, Object value) {
		super(source);
		this.pane = fileChooser;
		this.option = option;
		this.value = value;
	}

	/**
	 * Creates a new instance.
	 * @param source
	 * @param optionPane
	 * @param option
	 * @param value
	 * @param inputValue
	 */
	public SheetEvent(JSheet source, JOptionPane optionPane, int option, Object value, Object inputValue) {
		super(source);
		this.pane = optionPane;
		this.option = option;
		this.value = value;
		this.inputValue = inputValue;
	}

	/**
	 * Returns the pane on the sheet. This is either a JFileChooser or a
	 * JOptionPane.
	 * @return
	 */
	public JComponent getPane() {
		return pane;
	}

	/**
	 * Returns the JFileChooser pane on the sheet.
	 * @return
	 */
	public JFileChooser getFileChooser() {
		return (JFileChooser) pane;
	}

	/**
	 * Returns the JOptionPane pane on the sheet.
	 * @return
	 */
	public JOptionPane getOptionPane() {
		return (JOptionPane) pane;
	}

	/**
	 * Returns the option that the JFileChooser or JOptionPane returned.
	 * @return
	 */
	public int getOption() {
		return option;
	}

	/**
	 * Returns the value that the JFileChooser or JOptionPane returned.
	 * @return
	 */
	public Object getValue() {
		return value;
	}

	/**
	 * Returns the input value that the JOptionPane returned, if it wants input.
	 * @return
	 */
	public Object getInputValue() {
		return inputValue;
	}
}