package com.unclutter.poller;

public class PollingConfiguration {
	public String pollerName;
	public Object authCodeListener;
	public String authCodeMethod;
	public Object itemRequestListener;
	public String itemRequestMethod;

	public PollingConfiguration(String pollerName, Object authCodeListener, String authCodeMethod, Object itemRequestListener, String itemRequestMethod) {
		this.pollerName = pollerName;
		this.authCodeListener = authCodeListener;
		this.authCodeMethod = authCodeMethod;
		this.itemRequestListener = itemRequestListener;
		this.itemRequestMethod = itemRequestMethod;
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

	public Object getItemRequestListener() {
		return itemRequestListener;
	}

	public void getItemRequestListener(Object itemRequestListener) {
		this.itemRequestListener = itemRequestListener;
	}

	public String getItemRequestMethod() {
		return itemRequestMethod;
	}

	public void setItemRequestMethod(String itemRequestMethod) {
		this.itemRequestMethod = itemRequestMethod;
	}

	@Override
	public String toString() {
		return "PollingConfiguration {\n" +
			"\tpollerName: " + pollerName + "\n" +
			"\tauthCodeListener: " + authCodeListener + "\n" +
			"\tauthCodeMethod: " + authCodeMethod + "\n" +
			"\titemRequestListener: " + itemRequestListener + "\n" +
			"\titemRequestMethod: " + itemRequestMethod + "\n" +
		"}\n";
	}
}
