package com.dataexo.zblog.vo.resources.lists;

import java.util.List;

import javax.ws.rs.core.MultivaluedMap;


import com.dataexo.zblog.util.Endpoints;
import com.dataexo.zblog.vo.auth.MailgunClient;
import com.dataexo.zblog.vo.mail.Bounce;
import com.dataexo.zblog.vo.resources.ListResource;
import com.google.gson.annotations.SerializedName;

public class Bounces extends ListResource<Bounce> {

	@SerializedName("total_count")
	private Integer totalCount;

	@SerializedName("items")
	private List<Bounce> items;

	public Bounces(MailgunClient client) {
		super(client);
	}

	public Bounces(MailgunClient client, MultivaluedMap<String, String> filters) {
		super(client, filters);
	}

	@Override
	protected Integer getTotalCount() {
		return this.totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public void setItems(List<Bounce> items) {
		this.items = items;
	}

	@Override
	protected String getResourceLocation() {
		return Endpoints.BOUNCES;
	}

	@Override
	protected List<Bounce> getItems() {
		return this.items;
	}

	@Override
	protected com.dataexo.zblog.vo.mail.Paging getPaging() {
		return null;
	}
}
