package data;

import org.springframework.data.annotation.Id;

/**
* A user template for NoSQL repositories.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class User {
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
	private String gmailId;

    public User() {

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
	* Returns a string representation of a user used for printing.
	* @return User as a string.
	*/
    @Override
    public String toString() {
        return "User{\n" +
			"\tid=" + userId + ",\n" +
			"\tfirstName=" + firstName  + ",\n" +
			"\tlastName=" + lastName + ",\n" +
			"\tgmailId=" + gmailId + "\n" +
		"}";
    }
}
