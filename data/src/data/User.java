package data;

import java.io.Serializable;

import org.springframework.data.annotation.Id;

/**
* A user template for NoSQL repositories.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class User implements Serializable {
	private static final long serialVersionUID = 7823655309489247L;

	/**
	* ID used in database.
	*/
	@Id
	private String userId;

	/**
	* First name of the user.
	*/
	private String firstName;

	/**
	* Last name of the user.
	*/
	private String lastName;

	/**
	* The email address of the user's Gmail account.
	*/
	private String gmailId = null;

	/**
	* An array that contains the theme for various sections of the website.
	*/
	private String[] theme = {"#0f4d71", "#ffffff","#0f4d71"};

	/**
	* The depth to which the graph should expand to when the mainpage loads.
	*/
	private int initialDepth = 2;

	/**
	* How many nodes should be retrieved when a node is expanded.
	*/
	private int branchingFactor = 4;

	/**
	* Specifies if the user has deregistered their account or not.
	*/
	private Boolean isActive = true;

	/**
	* Default empty constructor.
	*/
	public User() {
		super();
	}

	/**
	* Constructor user for updates for user settings.
	*/
	public User(String userId) {
		super();
		this.userId = userId;
	}

	/**
	* Copy constructor.
	*/
	public User(User other) {
		super();
		this.userId = other.userId;
		this.firstName = other.firstName;
		this.lastName = other.lastName;
		this.gmailId = other.gmailId;
		this.firstName = other.firstName;
		
		if (other.theme != null)
			for (int i = 0; i < theme.length; i++)
				this.theme[i] = other.theme[i];
		else
			theme = null;

		this.initialDepth = other.initialDepth;
		this.branchingFactor = other.branchingFactor;
		this.isActive = other.isActive;
	}

	/**
	* Default constructor.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	* @param gmailId The email address of the user's Gmail account.
	*/
	public User(String firstName, String lastName, String gmailId) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.gmailId = gmailId;
	}

	/**
	* Default constructor.
	* @param userId ID used in database.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	* @param gmailId The email address of the user's Gmail account.
	*/
	public User(String userId, String firstName, String lastName, String gmailId) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
		this.gmailId = gmailId;
	}

	/**
	* Returns value of userId.
	* @return ID used in database.
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Sets new value of userId.
	* @param userId ID used in database.
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns value of firstName.
	* @return First name of the user.
	*/
	public String getFirstName() {
		return firstName;
	}

	/**
	* Sets new value of firstName.
	* @param firstName First name of the user.
	*/
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	* Returns value of lastName.
	* @return Last name of the user.
	*/
	public String getLastName() {
		return lastName;
	}

	/**
	* Sets new value of lastName.
	* @param lastName Last name of the user.
	*/
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	* Returns value of gmailId.
	* @return The email address of the user's Gmail account.
	*/
	public String getGmailId() {
		return gmailId;
	}

	/**
	* Sets new value of gmailId.
	* @param gmailId The email address of the user's Gmail account.
	*/
	public void setGmailId(String gmailId) {
		this.gmailId = gmailId;
	}

	/**
	* Returns value of theme.
	* @return An array that contains the theme for various sections of the website.
	*/
	public String[] getTheme() {
		return theme;
	}

	/**
	* Sets new value of theme.
	* @param theme An array that contains the theme for various sections of the website.
	*/
	public void setTheme(String[] theme) {
		this.theme = theme;
	}

	/**
	* Returns value of initialDepth.
	* @return The depth to which the graph should expand to when the mainpage loads.
	*/
	public int getInitialDepth() {
		return initialDepth;
	}

	/**
	* Sets new value of initialDepth.
	* @param initialDepth The depth to which the graph should expand to when the mainpage loads.
	*/
	public void setInitialDepth(int initialDepth) {
		this.initialDepth = initialDepth;
	}

	/**
	* Returns value of branchingFactor.
	* @return How many nodes should be retrieved when a node is expanded.
	*/
	public int getBranchingFactor() {
		return branchingFactor;
	}

	/**
	* Sets new value of branchingFactor.
	* @param branchingFactor How many nodes should be retrieved when a node is expanded.
	*/
	public void setBranchingFactor(int branchingFactor) {
		this.branchingFactor = branchingFactor;
	}
	/**
	* Returns value of isActive.
	* @return True or False indicating if the user is active or not
	*/
	public Boolean getIsActive() {
		return isActive;
	}

	/**
	* Sets new value of isActive.
	* @param isActive True or False indicating is the user is active or not.
	*/
	public void setIsActive(Boolean isActive) {
		this.isActive = isActive;
	}

	/**
	* Returns a string representation of a user used for printing.
	* @return User as a string.
	*/
	@Override
	public String toString() {
		return "User {\n" +
			"\tid:" + userId + ",\n" +
			"\tfirstName:" + firstName  + ",\n" +
			"\tlastName:" + lastName + ",\n" +
			"\tgmailId:" + gmailId + "\n" +
			"\tinitialDepth:" + initialDepth + "\n" +
			"\tbranchingFactor:" + branchingFactor + "\n" +
			"\tisActive:" + isActive + "\n" +
		"}";
	}
}
