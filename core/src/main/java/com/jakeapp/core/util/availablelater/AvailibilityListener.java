package com.jakeapp.core.util.availablelater;

public interface AvailibilityListener {

	public void statusUpdate(double progress, String status);

	public void finished();

	public void error(Exception t);

	public void error(String reason);

}
