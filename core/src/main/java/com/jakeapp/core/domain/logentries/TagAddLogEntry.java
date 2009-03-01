package com.jakeapp.core.domain.logentries;

import com.jakeapp.core.domain.Tag;
import com.jakeapp.core.domain.UserId;
import com.jakeapp.core.domain.LogAction;

import javax.persistence.Entity;
import javax.persistence.DiscriminatorValue;

@Entity
@DiscriminatorValue(value = "TAG_ADD")
public class TagAddLogEntry extends TagLogEntry {

    public TagAddLogEntry(Tag belongsTo, UserId member) {
        super(LogAction.TAG_ADD, belongsTo, member);
    }

    public TagAddLogEntry() {
        setLogAction(LogAction.TAG_ADD);
    }
}
