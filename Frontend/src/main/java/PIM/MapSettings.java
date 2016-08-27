package data;

import java.io.Serializable;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account for a specific PIM.
*
* @author  Armand Maree
* @since   2016-07-25
*/
public class MapSettings implements Serializable {
	private static final long serialVersionUID = 7142347827688675L;

	/*
	*	Array of strings which contain hexadecimal values
	*/
	private String userId= null;
	/*
	*	The default value for the initial depth of the map
	*/
	private int initialDepth;
	/*
	*	The default value for the initial depth of the map
	*/
	private int initialBranchFactor;
	/**
	* Default empty constructor.
	*/
	public MapSettings()
	{

	}
	/*
	*	Constructor that sets the theme member variable
	* 	@param userId Contains the value for the userId
	*	@param theme The array that is the value for theme
	*/
	public MapSettings(String userId,int initialDepth, int initialBranchFactor)
	{
		this.userId = userId;
		this.initialDepth = initialDepth;
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
	public int getInitialDepth()
	{
		return this.initialDepth;
	} 
	/*
	*	Returns the initial branch factor
	*	@return The initialBranchFactor
	*/
	public int getInitialBranchFactor()
	{
		return this.initialBranchFactor;
	} 
	/**
	* String representation of AuthCode used for printing.
	* @return Mapsettings in string form.
	*/
	@Override
	public String toString() {
		return "MapSettings {\n" +
			"\tinitialDepth: " +initialDepth + "\n" +
			"\tinitialBranchFactor: " + initialBranchFactor + "\n" +
		"}";
	}
}
