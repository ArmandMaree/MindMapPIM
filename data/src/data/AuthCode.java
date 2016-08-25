package data;

import java.io.Serializable;

/**
* Contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class AuthCode implements Serializable {
	private static final long serialVersionUID = 7146817820488675L;

	/**
	* Id used by the PIM source to identify the user.
	*/
	private String id;

	/**
	* Name of the PIM.
	*/
	private String pimSource;

	/**
	* Code used by the poller to start up the process.
	*/
	private String authCode = "";

	/**
	* Default constructor.
	*/
	public AuthCode() {

	}

	/**
	* Constructor that sets the authCode
	* @param id Id used by the PIM source to identify the user.
	* @param pimSource Name of the PIM.
	* @param authCode Code used by the poller to start up the process.
	*/
	public AuthCode(String id, String pimSource, String authCode) {
		this.id = id;
		this.pimSource = pimSource;
		this.authCode = authCode;
	}

	/*
	* Returns the value of id.
	* @return Id used by the PIM source to identify the user.
	*/
	public String getId() {
		return id;
	}

	/**
	* Set the value of id.
	* @param id Id used by the PIM source to identify the user.
	*/
	public void setId(String id) {
		this.id = id;
	}

	/*
	* Return the value pimSource.
	* @return Name of the PIM.
	*/
	public String getPimSource() {
		return pimSource;
	}

	/**
	* Return the value of pimSource.
	* @param pimSource Name of the PIM.
	*/
	public void setPimSource(String pimSource) {
		this.pimSource = pimSource;
	}

	/**
	* Getter to return the authCode.
	* @return Returns the authCode.
	*/
	public String getAuthCode() {
		return authCode;
	}

	/**
	* Set the value of AuthCode.
	* @param authCode The auth code.
	*/
	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}

	/**
	* String representation of AuthCode used for printing.
	* @return AuthCode in string form.
	*/
	@Override
	public String toString() {
		return "AuthCode {\n" +
			"\tid: " + id + "\n" +
			"\tpimSource: " + pimSource + "\n" +
			"\tauthCode: " + authCode + "\n" +
		"}";
	}
}
