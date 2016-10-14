package data;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

/**
* A user that will be saved in a database plus an attached returnId.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserIdentified extends User {
	private static final long serialVersionUID = 3180116540442618L;

	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Indicates whether the user is registered already.
	*/
	private boolean isRegistered;

	/**
	* Default empty constructor.
	*/
	public UserIdentified() {
		super();
	}

	/**
	* Constructor used when updating the user settings.
	* @param returnId ID used to identify the original request.
	* @param id ID used in database.
	*/
	public UserIdentified(String returnId, String id) {
		super(id);
		this.returnId = returnId;
	}

	/**
	* Constructor to build from given user.
	* @param returnId ID used to identify the original request.
	* @param isRegistered Indicates whether the user is registered already.
	* @param user The user this class must extend (do a deep copy).
	*/
	public UserIdentified(String returnId, boolean isRegistered, User user) {
		super(user);
		this.returnId = returnId;
		this.isRegistered = isRegistered;
	}

	/**
	* Constructor that initializes some member variables.
	* @param returnId ID used to identify the original request.
	* @param isRegistered Indicates whether the user is registered already.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	*/
	public UserIdentified(String returnId, boolean isRegistered, String firstName, String lastName) {
		super(firstName, lastName);
		this.returnId = returnId;
		this.isRegistered = isRegistered;
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
	* Returns value of isRegistered
	* @return Indicates whether the user is registered already.
	*/
	public boolean getIsRegistered() {
		return isRegistered;
	}

	/**
	* Sets new value of isRegistered.
	* @param isRegistered Indicates whether the user is registered already.
	*/
	public void setIsRegistered(boolean isRegistered) {
		this.isRegistered = isRegistered;
	}

	/**
	* Create string representation of UserIdentified for printing
	* @return String version of UserIdentified.
	*/
	@Override
	public String toString() {
		String u = "";

		for (PimId pimId : getPimIds())
			u += "\t\t" + pimId.pim + ": " + pimId.uId + "\n";

		return "UserIdentified {\n" +
		"\treturnId: " + returnId + ",\n" +
		"\tisRegistered: " + isRegistered + ",\n" +
		"\tid: " + getUserId() + ",\n" +
		"\tfirstName: " + getFirstName()  + ",\n" +
		"\tlastName: " + getLastName() + ",\n" +
		"\tids: {\n" + u + "\t}\n" +
		"\tinitialDepth:" + getInitialDepth() + "\n" +
		"\tbranchingFactor:" + getBranchingFactor() + "\n" +
		"\tisActive:" + getIsActive() + "\n" +
		"}";
	}
}
