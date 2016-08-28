package data;

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
	*/
	public UserIdentified(String returnId, String id) {
		super(id);
		this.returnId = returnId;
	}

	/**
	* Constructor to build from given user.
	*/
	public UserIdentified(String returnId, boolean isRegistered, User user) {
		super(user);
		this.returnId = returnId;
		this.isRegistered = isRegistered;
	}

	/**
	* Default UserIdentified constructor
	*/
	public UserIdentified(String returnId, boolean isRegistered, String firstName, String lastName, String gmailId) {
		super(firstName, lastName, gmailId);
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
	* @return  Indicates whether the user is registered already.
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
	* Get the parent user.
	*/
	public User getUser(boolean getId) {
		if (getId)
			return new User(getUserId(), getFirstName(), getLastName(), getGmailId());
		else
			return new User(getFirstName(), getLastName(), getGmailId());
	}

	/**
	* Get the parent user.
	*/
	public User getUser() {
		return getUser(true);
	}

	/**
	* Create string representation of UserIdentified for printing
	* @return String version of UserIdentified.
	*/
	@Override
	public String toString() {
		return "UserIdentified {\n" +
		"\treturnId: " + returnId + ",\n" +
		"\tisRegistered: " + isRegistered + ",\n" +
		"\tid: " + getUserId() + ",\n" +
		"\tfirstName: " + getFirstName()  + ",\n" +
		"\tlastName: " + getLastName() + ",\n" +
		"\tgmailId: " + getGmailId() + "\n" +
		"\tinitialDepth:" + getInitialDepth() + "\n" +
		"\tbranchingFactor:" + getBranchingFactor() + "\n" +
		"\tisActive:" + getIsActive() + "\n" +
		"}";
	}
}
