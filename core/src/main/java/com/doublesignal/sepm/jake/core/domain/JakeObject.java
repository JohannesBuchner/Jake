package com.doublesignal.sepm.jake.core.domain;

import java.util.List;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: johannes, domdorn
 * Date: May 9, 2008
 * Time: 00:25:03 AM
 */
public class JakeObject {

    private String name;


    public List<Tag> getTags() {
        return null;
    }

    public JakeObject addTag(String tag) {
        return this;
    }

    public JakeObject addTag(Tag tag) {
        return this;
    }

    public JakeObject removeTag(Tag tag) {
        return this;
    }

    public void delete() {
        /* delete this jake object */
    }

    public boolean equals(Object o) {
        /* TODO */
        return super.equals(o);
    }
}
