package repositories.user;

import org.springframework.data.annotation.Id;

/**
* A user template for NoSQL repositories.
*
* @author  Armand Maree
* @since   2016-07-16
*/
public class User {
    @Id
    private String userId;
    private String firstName;
    private String lastName;
	private String gmailId;

    public User() {

	}

    public User(String firstName, String lastName, String gmailId) {
        this.firstName = firstName;
        this.lastName = lastName;
		this.gmailId = gmailId;
    }

    @Override
    public String toString() {
        return "User{\n" +
			"\tid=" + userId + ",\n" +
			"\tfirstName=" + firstName  + ",\n" +
			"\tlastName=" + lastName + ",\n" +
			"\tgmailId=" + gmailId + "\n" +
		"}";
    }

	/**
	* Returns value of userId
	* @return
	*/
	public String getUserId() {
		return userId;
	}

	/**
	* Sets new value of userId
	* @param
	*/
	public void setUserId(String userId) {
		this.userId = userId;
	}

	/**
	* Returns value of firstName
	* @return
	*/
	public String getFirstName() {
		return firstName;
	}

	/**
	* Sets new value of firstName
	* @param
	*/
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	/**
	* Returns value of lastName
	* @return
	*/
	public String getLastName() {
		return lastName;
	}

	/**
	* Sets new value of lastName
	* @param
	*/
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	/**
	* Returns value of gmailId
	* @return
	*/
	public String getGmailId() {
		return gmailId;
	}

	/**
	* Sets new value of gmailId
	* @param
	*/
	public void setGmailId(String gmailId) {
		this.gmailId = gmailId;
	}
}
