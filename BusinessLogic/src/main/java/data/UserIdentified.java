package data;

public class UserIdentified extends User {
	/**
	* ID used to identify the original request.
	*/
	private String returnId;

	/**
	* Default empty UserIdentified constructor
	*/
	public UserIdentified() {
		super();
	}

	/**
	* Constructor to build from given user.
	*/
	public UserIdentified(String returnId, User user) {
		super(user.getUserId(), user.getFirstName(), user.getLastName(), user.getGmailId());
		this.returnId = returnId;
	}

	/**
	* Default UserIdentified constructor
	*/
	public UserIdentified(String returnId, String firstName, String lastName, String gmailId) {
		super(firstName, lastName, gmailId);
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
	* Get the parent user.
	*/
	public User getUser(boolean getId) {
		if (getId)
			return new User(getUserId(), getFirstName(), getLastName(), getGmailId());
		else
			return new User(getFirstName(), getLastName(), getGmailId());
	}

	/**
	* Create string representation of UserIdentified for printing
	* @return String version of UserIdentified.
	*/
	@Override
	public String toString() {
		return "UserIdentified {\n" +
		"\treturnId=" + returnId + ",\n" +
		"\tid=" + getUserId() + ",\n" +
		"\tfirstName=" + getFirstName()  + ",\n" +
		"\tlastName=" + getLastName() + ",\n" +
		"\tgmailId=" + getGmailId() + "\n" +
		"}";
	}
}
