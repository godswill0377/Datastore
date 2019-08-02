package com.dataexo.zblog.vo.mail;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class IncomingAttachment {

	@SerializedName("attachment-id")
	@Expose
	private String id;

	@Expose
	private String url;

	@SerializedName("content-type")
	@Expose
	private String contentType;

	@Expose
	private String name;

	@Expose
	private long size;

	/**
	 * 
	 * @return The url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * 
	 * @param url
	 *            The url
	 */
	public void setUrl(String url) {
		this.url = url;
	}

	public IncomingAttachment withUrl(String url) {
		this.url = url;
		return this;
	}

	/**
	 * 
	 * @return The contentType
	 */
	public String getContentType() {
		return contentType;
	}

	/**
	 * 
	 * @param contentType
	 *            The content-type
	 */
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public IncomingAttachment withContentType(String contentType) {
		this.contentType = contentType;
		return this;
	}

	/**
	 * 
	 * @return The name
	 */
	public String getName() {
		return name;
	}

	/**
	 * 
	 * @param name
	 *            The name
	 */
	public void setName(String name) {
		this.name = name;
	}

	public IncomingAttachment withName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 
	 * @return The size
	 */
	public long getSize() {
		return size;
	}

	/**
	 * 
	 * @param size
	 *            The size
	 */
	public void setSize(long size) {
		this.size = size;
	}

	public IncomingAttachment withSize(long size) {
		this.size = size;
		return this;
	}

	/**
	 * Getter for id
	 * 
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * Setter for id
	 * 
	 * @param id
	 *            the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	public IncomingAttachment withId(String id) {
		this.id = id;
		return this;
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this);
	}

	@Override
	public int hashCode() {
		return new HashCodeBuilder().append(id).append(url).append(contentType).append(name).append(size).toHashCode();
	}

	@Override
	public boolean equals(Object other) {
		if (other == this) {
			return true;
		}
		if ((other instanceof IncomingAttachment) == false) {
			return false;
		}
		IncomingAttachment rhs = ((IncomingAttachment) other);
		return new EqualsBuilder().append(id, rhs.id).append(url, rhs.url).append(contentType, rhs.contentType).append(name, rhs.name).append(size, rhs.size).isEquals();
	}

}
