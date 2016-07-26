package data;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class AuthCode {
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

	public AuthCode() {

	}

	/**
	* Constructor that sets the authCode
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

	public void setPimSource(String pimSource) {
		this.pimSource = pimSource;
	}

	public void setAuthCode(String authCode) {
		this.authCode = authCode;
	}
	
	/**
	* Getter to return the authCode.
	* @return Returns the authCode.
	*/
	public String getAuthCode() {
		return authCode;
	}
}
