package com.jakeapp.jake.fss.exceptions;

/**
 * The operation that should create missing directories recursively, failed.
 * This can happen if a directory that would need to be created, already exists 
 * and is not a directory. 
 * @author johannes
 *
 */
@SuppressWarnings("serial")
public class CreatingSubDirectoriesFailedException extends Exception {

}
