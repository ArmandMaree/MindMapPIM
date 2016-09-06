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
	* Used by the pollers to know how long an access token is valid.
	*/
	private long expireTime = -1;

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

	/**
	* Constructor that sets the authCode
	* @param id Id used by the PIM source to identify the user.
	* @param pimSource Name of the PIM.
	* @param authCode Code used by the poller to start up the process.
	* @param expireTime Used by the pollers to know how long an access token is valid.
	*/
	public AuthCode(String id, String pimSource, String authCode, long expireTime) {
		this.id = id;
		this.pimSource = pimSource;
		this.authCode = authCode;
		this.expireTime = expireTime;
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
	* Getter to return the expireTime.
	* @return Used by the pollers to know how long an access token is valid.
	*/
	public long getExpireTime() {
		return expireTime;
	}

	/**
	* Set the value of expireTime.
	* @param expireTime Used by the pollers to know how long an access token is valid.
	*/
	public void setExpireTime(long expireTime) {
		this.expireTime = expireTime;
	}

	/**
	* String representation of AuthCode used for printing.
	* @return AuthCode in string form.
	*/
	@Override
	public String toString() {
		return "AuthCode {\n" +
			"\tpimSource: " + pimSource + "\n" +
			"\tid: " + id + "\n" +
			"\tauthCode: " + authCode + "\n" +
			"\texpireTime: " + expireTime + "\n" +
		"}";
	}
}
