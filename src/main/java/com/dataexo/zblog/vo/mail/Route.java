package com.dataexo.zblog.vo.mail;

import java.util.List;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Route {

	@SerializedName("id")
	@Expose
	private String id;
	
	@SerializedName("priority")
	@Expose
	private int priority;
	
	@SerializedName("description")
	@Expose
	private String description;
	
	@SerializedName("expression")
	@Expose
	private String expression;
	
	@SerializedName("actions")
	@Expose
	private List<String> actions;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public int getPriority() {
		return priority;
	}

	public void setPriority(int priority) {
		this.priority = priority;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getExpression() {
		return expression;
	}

	public void setExpression(String expression) {
		this.expression = expression;
	}

	public List<String> getActions() {
		return actions;
	}

	public void setActions(List<String> actions) {
		this.actions = actions;
	}

}
