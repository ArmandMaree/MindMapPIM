package data;

import java.io.Serializable;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
* Contains all the information needed to update a User object that was previously persisted with an attached returnId.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserUpdateRequestIdentified extends UserUpdateRequest implements Serializable {
	private static final long serialVersionUID = 4129067728810262L;

	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Default empty constructor.
	*/
	public UserUpdateRequestIdentified() {
		super();
	}

	/**
	* Constructor that initializes some member variables used for updating.
	* @param returnId ID used to identify the original request.
	* @param userId ID used in database.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public UserUpdateRequestIdentified(String returnId, String userId, AuthCode[] authCodes) {
		super(userId, authCodes);
		this.returnId = returnId;
	}

	/**
	* Returns value of returnId
	* @return ID used to identify the original request.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Sets new value of id
	* @param returnId ID used to identify the original request.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* String representation of a UserUpdateRequestIdentified used for printing.
	* @return String representation of a UserUpdateRequestIdentified.
	*/
	public String toString() {
		String u = "";
		String a = "";
		String t = "";

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

		if (getAuthCodes() != null) {
			for (AuthCode authCode : getAuthCodes())
				a += "\t\t" + authCode.getPimSource() + ": " + authCode.getId() + " - " + authCode.getAuthCode() + "\n";
		}
		else 
			a = "NULL\n";

		return "UserUpdateRequestIdentified {\n" +
			"\treturn: " + returnId + ",\n" +
			"\tid: " + getUserId() + ",\n" +
			"\tfirstName: " + getFirstName()  + ",\n" +
			"\tlastName: " + getLastName() + ",\n" +
			"\tids: [\n" + u + "\t]\n" +
			"\ttheme: [\n" + t + "\t]\n" +
			"\tinitialDepth:" + getInitialDepth() + "\n" +
			"\tbranchingFactor:" + getBranchingFactor() + "\n" +
			"\tisActive:" + getIsActive() + "\n" +
			"\tpersistMap:" + getPersistMap() + "\n" +
			"\tauthCodes: [\n" + a + "\t]\n" +
		"}";
	}
}
