package com.unclutter.poller;

public class PollingConfiguration {
	public String pollerName;
	public Object authCodeListener;
	public String authCodeMethod;
	public Object itemListener;
	public String itemMethod;

	public PollingConfiguration(String pollerName, Object authCodeListener, String authCodeMethod, Object itemListener, String itemMethod) {
		this.pollerName = pollerName;
		this.authCodeListener = authCodeListener;
		this.authCodeMethod = authCodeMethod;
		this.itemListener = itemListener;
		this.itemMethod = itemMethod;
	}

	public String getPollerName() {
		return pollerName;
	}

	public void setPollerName(String pollerName) {
		this.pollerName = pollerName;
	}

	public Object getAuthCodeListener() {
		return authCodeListener;
	}

	public void setAuthCodeListener(Object authCodeListener) {
		this.authCodeListener = authCodeListener;
	}

	public String getAuthCodeMethod() {
		return authCodeMethod;
	}

	public void setAuthCodeMethod(String authCodeMethod) {
		this.authCodeMethod = authCodeMethod;
	}

	public Object getItemListener() {
		return itemListener;
	}

	public void setItemListener(Object itemListener) {
		this.itemListener = itemListener;
	}

	public String getItemMethod() {
		return itemMethod;
	}

	public void setItemMethod(String itemMethod) {
		this.itemMethod = itemMethod;
	}
}
