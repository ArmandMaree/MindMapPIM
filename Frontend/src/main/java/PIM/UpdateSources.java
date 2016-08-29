package data;

import java.io.Serializable;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class UpdateSources implements Serializable {
	private static final long serialVersionUID = 7142347827688675L;

	/*
	*	Array of strings which contain hexadecimal values
	*/
	private String userId= null;
	/*
	*	The default value for the initial depth of the map
	*/
	private AuthCode[] authcodes;
	/*
	*	The default value for the initial depth of the map
	*/

	/**
	* Default empty constructor.
	*/
	public UpdateSources()
	{

	}
	/*
	*	Constructor that sets the theme member variable
	* 	@param userId Contains the value for the userId
	*	@param theme The array that is the value for theme
	*/
	public UpdateSources(String userId,AuthCode[] authcodes)
	{
		this.userId = userId;
		this.authcodes = authcodes;
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
	*	Returns the initial depth
	*	@return The initialDepth
	*/
	public AuthCode[] getAuthcodes()
	{
		return this.authcodes;
	} 
	
	/**
	* String representation of AuthCode used for printing.
	* @return Mapsettings in string form.
	*/
	@Override
	public String toString() {
		String n= "UpdateSources {\n" +
			"\tuserId: " +userId + "\n" +
			"\tauthcodes: [" ;

			for(int i=0;i< authcodes.length;i++)
			{
				n+="{ " + authcodes[i] + "\n" +" }";
			}
		n+="]}";
		return n;
	}
}
