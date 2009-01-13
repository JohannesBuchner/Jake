package com.jakeapp.gui.swing.callbacks;

import java.io.FileNotFoundException;


/**
 * The generic Error Callback.
 * User: studpete
 * Date: Dec 30, 2008
 * Time: 12:21:02 PM
 */
public interface ErrorCallback {

    class JakeErrorEvent {
        private Exception exception;

        public JakeErrorEvent(Exception e) {
            this.setException(e);
        }

        public Exception getException() {
            return exception;
        }

        public void setException(Exception exception) {
            this.exception = exception;
        }
    }

    /**
     * Gets called when an error from the core occures.
     *
     * @param ee
     */
    void reportError(JakeErrorEvent ee);
}
