package com.doublesignal.sepm.jake.core.domain;

import java.io.Serializable;

/**
 * SEPM SS08
 * Gruppe: 3950
 * Projekt: Jake - a collaborative Environment
 * User: domdorn
 * Date: May 8, 2008
 * Time: 11:05:08 PM
 */
public class NoteObject extends JakeObject implements Serializable {

    private String content;

    public NoteObject(String content) {
        this.content = content;


    }


    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int hashCode() {
        return (content != null ? content.hashCode() : 0);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        NoteObject that = (NoteObject) o;

        if (content != null ? !content.equals(that.content) : that.content != null) return false;

        return true;
    }
}
