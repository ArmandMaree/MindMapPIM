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

	public getPollerName() {
		return pollerName;
	}

	public setPollerName(String pollerName) {
		this.pollerName = pollerName;
	}

	public getAuthCodeListener() {
		return authCodeListener;
	}

	public setAuthCodeListener(Object authCodeListener) {
		this.authCodeListener = authCodeListener;
	}

	public getAuthCodeMethod() {
		return authCodeMethod;
	}

	public setAuthCodeMethod(String authCodeMethod) {
		this.authCodeMethod = authCodeMethod;
	}

	public getItemListener() {
		return itemListener;
	}

	public setItemListener(Object itemListener) {
		this.itemListener = itemListener;
	}

	public getItemMethod() {
		return itemMethod;
	}

	public setItemMethod(String itemMethod) {
		this.itemMethod = itemMethod;
	}

}
