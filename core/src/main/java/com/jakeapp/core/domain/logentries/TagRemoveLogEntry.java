package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "TAG_REMOVE")
public class TagRemoveLogEntry extends TagLogEntry {

    public TagRemoveLogEntry(Tag belongsTo, UserId member) {
        super(LogAction.TAG_REMOVE, belongsTo, member);
    }

    public TagRemoveLogEntry() {
        setLogAction(LogAction.TAG_REMOVE);
    }
}
