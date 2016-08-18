package data;

/**
* Used by the RESTController to map JSON objects to. Contains all the information needed to create a User object to be persisted.
*
* @author  Armand Maree
* @since   2016-07-26
*/
public class UserRegistrationIdentified extends UserRegistration {
	private static final long serialVersionUID = 4123426460166654L;

	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Default empty UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified() {
		super();
	}

	/**
	* Default empty UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified(String returnId, UserRegistration userRegistration) {
		super(userRegistration.getFirstName(), userRegistration.getLastName(), userRegistration.getAuthCodes());
		this.returnId = returnId;
	}

	/**
	* Default UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified(String returnId, String firstName, String lastName, AuthCode[] authCodes) {
		super(firstName, lastName, authCodes);
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
	* Sets new value of returnId
	* @param returnId ID used to identify the original request.
	*/
	public void setReturnId(String returnId) {
		this.returnId = returnId;
	}

	/**
	* Create string representation of UserRegistrationIdentified for printing
	* @return String version of UserRegistrationIdentified.
	*/
	@Override
	public String toString() {
		return "UserRegistrationIdentified {\n" +
			"\treturnId: " + returnId + "\n" +
			"\tfirstName: " + getFirstName() + "\n" +
			"\tlastName: " + getLastName() + "\n" +
			"\tauthCodes size: " + ((getAuthCodes() == null) ? "null" : getAuthCodes().length) + "\n" +
		"}";
	}
}
