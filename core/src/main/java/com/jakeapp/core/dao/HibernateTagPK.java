package com.jakeapp.core.dao;

import com.jakeapp.core.domain.JakeObject;

import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Column;
import java.io.Serializable;

@SuppressWarnings({"SerializableClassInSecureContext"})
@Embeddable
public class HibernateTagPK implements Serializable {
    private String name;
    private JakeObject object;
    private static final long serialVersionUID = 5319678626593794908L;

    @Column(name = "text", nullable = false)
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @ManyToOne
    @JoinColumn(name = "objectid", columnDefinition = "char(36)", insertable = false, updatable = false, nullable = false)    
    public JakeObject getObject() {
        return object;
    }


    public void setObject(JakeObject object) {
        this.object = object;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        HibernateTagPK that = (HibernateTagPK) o;

        if (!name.equals(that.name)) return false;
        if (!object.getUuid().equals(that.object.getUuid())) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + object.hashCode();
        return result;
    }
}
