package com.unclutter.poller;

/**
* Contains the setting used to setup all the beans used by the pollers.
*
* @author Armand Maree
* @since 1.0.0
*/
public class PollingConfiguration {
	/**
	* Name of the platform that the poller will be polling for.
	*/
	public String pollerName;

	/**
	* The object that will be receiving authCodes.
	*/
	public Object authCodeListener;

	/**
	* The method in the authCodeListener object that will receive the authCode.
	*/
	public String authCodeMethod;

	/**
	* The object that will be receiving itemRequests.
	*/
	public Object itemRequestListener;

	/**
	* The method in the itemRequestListener object that will receive the itemRequests.
	*/
	public String itemRequestMethod;


	/**
	* Constructor.
	* @param pollerName Name of the platform that the poller will be polling for.
	* @param authCodeListener The object that will be receiving authCodes.
	* @param authCodeMethod The method in the authCodeListener object that will receive the authCode.
	* @param itemRequestListener The object that will be receiving itemRequests.
	* @param itemRequestMethod The method in the itemRequestListener object that will receive the itemRequests.
	*/
	public PollingConfiguration(String pollerName, Object authCodeListener, String authCodeMethod, Object itemRequestListener, String itemRequestMethod) {
		this.pollerName = pollerName;
		this.authCodeListener = authCodeListener;
		this.authCodeMethod = authCodeMethod;
		this.itemRequestListener = itemRequestListener;
		this.itemRequestMethod = itemRequestMethod;
	}

	/**
	* Get the value of pollerName.
	* @return Name of the platform that the poller will be polling for.
	*/
	public String getPollerName() {
		return pollerName;
	}

	/**
	* Set the value of pollerName.
	* @param pollerName Name of the platform that the poller will be polling for.
	*/
	public void setPollerName(String pollerName) {
		this.pollerName = pollerName;
	}

	/**
	* Get the value of authCodeListener.
	* @return The object that will be receiving authCodes.
	*/
	public Object getAuthCodeListener() {
		return authCodeListener;
	}

	/**
	* Set the value of authCodeListener.
	* @param authCodeListener The object that will be receiving authCodes.
	*/
	public void setAuthCodeListener(Object authCodeListener) {
		this.authCodeListener = authCodeListener;
	}

	/**
	* Get the value of authCodeMethod.
	* @return The method in the authCodeListener object that will receive the authCode.
	*/
	public String getAuthCodeMethod() {
		return authCodeMethod;
	}

	/**
	* Set the value of authCodeMethod.
	* @param authCodeMethod The method in the authCodeListener object that will receive the authCode.
	*/
	public void setAuthCodeMethod(String authCodeMethod) {
		this.authCodeMethod = authCodeMethod;
	}

	/**
	* Get the value of itemRequestListener.
	* @return The object that will be receiving itemRequests.
	*/
	public Object getItemRequestListener() {
		return itemRequestListener;
	}

	/**
	* Set the value of itemRequestListener.
	* @param itemRequestListener The object that will be receiving itemRequests.
	*/
	public void getItemRequestListener(Object itemRequestListener) {
		this.itemRequestListener = itemRequestListener;
	}

	/**
	* Get the value of itemRequestMethod.
	* @return The method in the itemRequestListener object that will receive the itemRequests.
	*/
	public String getItemRequestMethod() {
		return itemRequestMethod;
	}

	/**
	* Set the value of itemRequestMethod.
	* @param itemRequestMethod The method in the itemRequestListener object that will receive the itemRequests.
	*/
	public void setItemRequestMethod(String itemRequestMethod) {
		this.itemRequestMethod = itemRequestMethod;
	}

	/**
	* String representation of a pollerConfiguration object used for printing.
	* @return A string representation of a pollerConfiguration object.
	*/
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
