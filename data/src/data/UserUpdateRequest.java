package data;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
* Contains all the information needed to update a User object that was previously persisted.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserUpdateRequest extends User implements Serializable {
	private static final long serialVersionUID = 369603720519056L;

	/**
	* Array of authentication codes for the pollers.
	*/
	private AuthCode[] authCodes = null;

	/**
	* Default empty conctructor.
	*/
	public UserUpdateRequest() {
		super();
	}

	/**
	* Constructor that initializes some member variables used for updating.
	* @param userId ID used in database.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public UserUpdateRequest(String userId, AuthCode[] authCodes) {
		super(userId);
		this.authCodes = authCodes;
	}

	/**
	* Returns the value of authCodes.
	* @return Array of authentication codes for the pollers.
	*/
	public AuthCode[] getAuthCodes() {
		return authCodes;
	}

	/**
	* Set the value of authCodes.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public void setAuthCodes(AuthCode[] authCodes) {
		this.authCodes = authCodes;
	}

	/**
	* String representation of a UserUpdateRequest used for printing.
	* @return String representation of a UserUpdateRequest.
	*/
	@Override
	public String toString() {
		String u = "";
		String t = "";
		String a = "";

		if (getPimIds() == null)
			u = "NULL\n";
		else
			for (PimId pimId : getPimIds())
				u += "\t\t" + pimId.pim + ": " + pimId.uId + "\n";

		if (getTheme() == null)
			t = "NULL\n";
		else
			for (String themeS : getTheme())
				t += "\t\t" + themeS + "\n";

		if (authCodes != null) {
			for (AuthCode authCode : getAuthCodes())
				a += "\t\t" + authCode.getPimSource() + ": " + authCode.getId() + " - " + authCode.getAuthCode() + "\n";
		}
		else 
			a = "NULL\n";

		return "UserUpdateRequest {\n" +
			"\tid: " + getUserId() + ",\n" +
			"\tfirstName: " + getFirstName()  + ",\n" +
			"\tlastName: " + getLastName() + ",\n" +
			"\tids: [\n" + u + "\t]\n" +
			"\ttheme: [\n" + t + "\t]\n" +
			"\tauthCodes: [\n" + a + "\t]\n" +
			"\tisActive:" + getIsActive() + "\n" +
			"\tpersistMap:" + getPersistMap() + "\n" +
		"}";
	}
}
