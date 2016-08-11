package data;

/**
* Used by the RESTController to map JSON objects to. Contains all the information needed to create a User object to be persisted.
*
* @author  Armand Maree
* @since   2016-07-26
*/
public class UserRegistrationIdentified extends UserRegistration {
	/**
	* ID used to identify the original request.
	*/
	private String id;

	/**
	* Default empty UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified() {
		super();
	}

	/**
	* Default empty UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified(String id, UserRegistration userRegistration) {
		super(userRegistration.getFirstName(), userRegistration.getLastName(), userRegistration.getAuthCodes());
		this.id = id;
	}

	/**
	* Default UserRegistrationIdentified constructor
	*/
	public UserRegistrationIdentified(String id, String firstName, String lastName, AuthCode[] authCodes) {
		super(firstName, lastName, authCodes);
		this.id = id;
	}

	/**
	* Returns value of id
	* @return ID used to identify the original request.
	*/
	public String getId() {
		return id;
	}

	/**
	* Sets new value of id
	* @param id ID used to identify the original request.
	*/
	public void setId(String id) {
		this.id = id;
	}

	/**
	* Create string representation of UserRegistrationIdentified for printing
	* @return String version of UserRegistrationIdentified.
	*/
	@Override
	public String toString() {
		return "UserRegistrationIdentified {\n" +
			"\tid: " + id + "\n" +
			"\tfirstName: " + getFirstName() + "\n" +
			"\tlastName: " + getLastName() + "\n" +
			"\tauthCodes size: " + ((getAuthCodes() == null) ? "null" : getAuthCodes().length) + "\n" +
		"}";
	}
}
