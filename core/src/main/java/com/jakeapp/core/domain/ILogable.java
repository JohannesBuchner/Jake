package com.jakeapp.core.domain;

import java.io.Serializable;

/**
 * This interface is used within logEntrys.
 * Every object which should be able to be put into &quot;belongsTo&quot; has
 * to implement this interface.
 * @see com.jakeapp.core.domain.logentries.LogEntry#getBelongsTo()
 */
public interface ILogable extends Serializable {
    
}
