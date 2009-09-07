package com.jakeapp.core.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Column;

/**
 * Simple key/value representation of a config option.
 */
@Entity(name = "configuration")
public class Configuration {

	private String key;
	private String value;
	
	/**
	 * Default constructor, needed for persistence frameworks.
	 */
	public Configuration() {
	}

    public Configuration(String name, String value) {
        this.key = name;
        this.value = value;
    }


	/**
	 * Get the key of this config option
	 * @return the key of the config option
	 */
    @Id
    @Column(name = "key")
	public String getKey() {
		return this.key;
	}
	
	/**
	 * Get the value.
	 * @return the value of this config option.
	 */
    @Column(name = "value")
	public String getValue() {
		return this.value;
	}
	
	/**
	 * Set the key of this config option.
	 * @param key the key of this config option.
	 */
	public void setKey(final String key) {
		this.key = key;
	}

	/**
	 * Set the value of this config option
	 * @param value the value of this config option
	 */
	public void setValue(final String value) {
		this.value = value;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.key == null) ? 0
				: this.key.hashCode());
		result = prime * result + ((this.value == null) ? 0
				: this.value.hashCode());
		return result;
	}

	/**
	 * Test if two configurations are equal.
	 * @return <code>true</code> iff both <code>key</code>
	 * and <code>value</code> are equal.
	 * @param obj The other object to compare this object to.
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (this.getClass() != obj.getClass())
			return false;
		final Configuration other = (Configuration) obj;
		if (this.key == null) {
			if (other.key != null)
				return false;
		} else if (!this.key.equals(other.key))
			return false;
		if (this.value == null) {
			if (other.value != null)
				return false;
		} else if (!this.value.equals(other.value))
			return false;
		return true;
	}
}
