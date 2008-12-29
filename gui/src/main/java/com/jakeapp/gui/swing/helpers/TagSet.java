package com.jakeapp.gui.swing.helpers;

import com.jakeapp.core.domain.Tag;

import java.util.Set;

/**
 * Wrapper for a set of tags so it can be used by a TagSetRenderer in file tables.
 */
public class TagSet {
    private Set<Tag> tags;

    public Set<Tag> getTags() {
        return tags;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }
}
