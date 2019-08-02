package com.dataexo.zblog.vo.mail;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Campaign {

	@SerializedName("id")
	@Expose
	private String id;

	@SerializedName("delivered_count")
	@Expose
	private int deliveredCount;

	@SerializedName("name")
	@Expose
	private String name;

	@SerializedName("created_at")
	@Expose
	private String createdAt;

	@SerializedName("clicked_count")
	@Expose
	private int clickedCount;

	@SerializedName("opened_count")
	@Expose
	private int openedCount;

	@SerializedName("submitted_count")
	@Expose
	private int submittedCount;

	@SerializedName("unsubscribed_count")
	@Expose
	private int unsubscribedCount;

	@SerializedName("bounced_count")
	@Expose
	private int bouncedCount;

	@SerializedName("complained_count")
	@Expose
	private int complainedCount;

	@SerializedName("dropped_count")
	@Expose
	private int droppedCount;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getDeliveredCount() {
		return deliveredCount;
	}

	public void setDeliveredCount(int deliveredCount) {
		this.deliveredCount = deliveredCount;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(String createdAt) {
		this.createdAt = createdAt;
	}

	public int getClickedCount() {
		return clickedCount;
	}

	public void setClickedCount(int clickedCount) {
		this.clickedCount = clickedCount;
	}

	public int getOpenedCount() {
		return openedCount;
	}

	public void setOpenedCount(int openedCount) {
		this.openedCount = openedCount;
	}

	public int getSubmittedCount() {
		return submittedCount;
	}

	public void setSubmittedCount(int submittedCount) {
		this.submittedCount = submittedCount;
	}

	public int getUnsubscribedCount() {
		return unsubscribedCount;
	}

	public void setUnsubscribedCount(int unsubscribedCount) {
		this.unsubscribedCount = unsubscribedCount;
	}

	public int getBouncedCount() {
		return bouncedCount;
	}

	public void setBouncedCount(int bouncedCount) {
		this.bouncedCount = bouncedCount;
	}

	public int getComplainedCount() {
		return complainedCount;
	}

	public void setComplainedCount(int complainedCount) {
		this.complainedCount = complainedCount;
	}

	public int getDroppedCount() {
		return droppedCount;
	}

	public void setDroppedCount(int droppedCount) {
		this.droppedCount = droppedCount;
	}

}
