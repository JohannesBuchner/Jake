package com.jakeapp.core.domain;

import java.io.Serializable;
import java.util.UUID;

/**
 * This interface is used within logEntrys.
 * Every object which should be able to be put into &quot;belongsTo&quot; has
 * to implement this interface.
 * @see LogEntry#getBelongsTo()
 */
public interface ILogable extends Serializable {
    
}
