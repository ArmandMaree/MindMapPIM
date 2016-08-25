package data;

/**
* Contains all the information needed to create a User object to be persisted with an attached returnId.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class UserRegistrationIdentified extends UserRegistration {
	private static final long serialVersionUID = 4123426460166654L;

	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Default constructor
	*/
	public UserRegistrationIdentified() {
		super();
	}

	/**
	* Default constructor that initializes some member variables.
	* @param returnId ID used to identify the original request.
	* @param userRegistration Contains all the information needed to create a User object to be persisted.
	*/
	public UserRegistrationIdentified(String returnId, UserRegistration userRegistration) {
		super(userRegistration.getFirstName(), userRegistration.getLastName(), userRegistration.getAuthCodes());
		this.returnId = returnId;
	}

	/**
	* Default constructor that initializes some member variables.
	* @param returnId ID used to identify the original request.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	* @param authCodes Array of authentication codes for the pollers.
	*/
	public UserRegistrationIdentified(String returnId, String firstName, String lastName, AuthCode[] authCodes) {
		super(firstName, lastName, authCodes);
		this.returnId = returnId;
	}

	/**
	* Returns value of returnId.
	* @return ID used to identify the original request.
	*/
	public String getReturnId() {
		return returnId;
	}

	/**
	* Sets new value of returnId.
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
