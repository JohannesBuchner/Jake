package com.jakeapp.gui.swing.callbacks;


/**
 * The generic Error Callback.
 * User: studpete
 * Date: Dec 30, 2008
 * Time: 12:21:02 PM
 */
public interface ErrorCallback {

    class JakeErrorEvent {
        private RuntimeException exception;

        public JakeErrorEvent(RuntimeException exception) {
            this.setException(exception);
        }

        public RuntimeException getException() {
            return exception;
        }

        public void setException(RuntimeException exception) {
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
