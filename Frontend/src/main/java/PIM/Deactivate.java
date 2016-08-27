package data;

import java.io.Serializable;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class Deactivate implements Serializable {
	private static final long serialVersionUID = 7144417827688675L;

	/*
	*	Array of strings which contain hexadecimal values
	*/
	private String userId= null;
	/*
	*	Array of strings which contain hexadecimal values
	*/
	private Boolean isActive= true;

	/**
	* Default empty constructor.
	*/
	public Deactivate()
	{

	}
	/*
	*	Constructor that sets the isActive member variable
	* 	@param userId Contains the value for the userId
	*	@param isActive The value for isActive
	*/
	public Deactivate(String userId,Boolean isActive)
	{
		this.userId = userId;
		this.isActive = isActive;
	} 
	/*
	*	Returns the userId
	*	@return The user id
	*/
	public String getUserId()
	{
		return this.userId;
	}
	/*
	*	Returns the theme
	*	@return The theme
	*/
	public Boolean getIsActive()
	{
		return this.isActive;
	} 
	/**
	* String representation of Deactivate used for printing.
	* @return Deactivate in string form.
	*/
	@Override
	public String toString() {
		return "Deactivate {\n" +
			"\tuserId: " + userId + "\n" +
			"\tisActive: " + isActive + "\n" +
		"}";
	}
}
