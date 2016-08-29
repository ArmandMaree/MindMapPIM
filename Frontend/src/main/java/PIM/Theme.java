package data;

import java.io.Serializable;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class Theme implements Serializable {
	private static final long serialVersionUID = 7146817827688675L;

	/*
	*	Array of strings which contain hexadecimal values
	*/
	private String userId= null;
	/*
	*	Array of strings which contain hexadecimal values
	*/
	private String[] theme= null;

	/**
	* Default empty constructor.
	*/
	public Theme()
	{

	}
	/*
	*	Constructor that sets the theme member variable
	* 	@param userId Contains the value for the userId
	*	@param theme The array that is the value for theme
	*/
	public Theme(String userId,String[] theme)
	{
		this.userId = userId;
		this.theme = theme;
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
	public String[] getTheme()
	{
		return this.theme;
	} 
	/**
	* String representation of AuthCode used for printing.
	* @return AuthCode in string form.
	*/
	@Override
	public String toString() {
		return "AuthCode {\n" +
			"\tnav: " + theme[0] + "\n" +
			"\tmap: " + theme[1] + "\n" +
			"\tsidepanel: " + theme[2] + "\n" +
		"}";
	}
}
