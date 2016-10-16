package data;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;

/**
* A user that will be saved in a database.
*
* @author  Armand Maree
* @since   1.0.0
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
	* An array that contains the theme for various sections of the website.
	*/
	private String[] theme = {"#0f4d71", "#ffffff","rgba(255,255,255,0.8)"};

	/**
	* Key-value pair of IDs of the user on various PIM platforms.
	*/
	private List<PimId> pimIds = new ArrayList<>();

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
	private boolean isActive = true;

	/**
	* Specifies whether the map should be persisted during reloads or not.
	*/
	private boolean persistMap = true;

	/**
	* Default empty constructor.
	*/
	public User() {
		super();
	}

	/**
	* Constructor user for updates for user settings.
	* @param userId ID used in database.
	*/
	public User(String userId) {
		super();
		this.userId = userId;
	}

	/**
	* Copy constructor.
	* @param other The User where the values should be copied from.
	*/
	public User(User other) {
		super();
		this.userId = other.userId;
		this.firstName = other.firstName;
		this.lastName = other.lastName;
		this.firstName = other.firstName;

		if (other.theme != null)
			for (int i = 0; i < theme.length; i++)
				this.theme[i] = other.theme[i];
		else
			theme = null;

		for (int i = 0; i < other.pimIds.size(); i++)
			pimIds.add(new PimId(other.pimIds.get(i).pim, other.pimIds.get(i).uId));

		this.initialDepth = other.initialDepth;
		this.branchingFactor = other.branchingFactor;
		this.isActive = other.isActive;
	}

	/**
	* Default constructor.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	*/
	public User(String firstName, String lastName) {
		this.firstName = firstName;
		this.lastName = lastName;
	}

	/**
	* Constructor that initializes some member variables.
	* @param userId ID used in database.
	* @param firstName First name of the user.
	* @param lastName Last name of the user.
	*/
	public User(String userId, String firstName, String lastName) {
		this.userId = userId;
		this.firstName = firstName;
		this.lastName = lastName;
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
	* Returns value of pimIds.
	* @return Key-value pair of IDs of the user on various PIM platforms.
	*/
	public List<PimId> getPimIds() {
		return pimIds;
	}

	/**
	* Returns uId of the specified PIM.
	* <p>
	*	Will return NULL if the specified PIM does not exist.
	* </p>
	* @param pim The PIM whos ID is requested.
	* @return The ID of the specified PIM
	*/
	public String getPimId(String pim) {
		for (PimId pimId : pimIds)
			if (pimId.pim.equals(pim))
				return pimId.uId;

		return null;
	}

	/**
	* Removes a PimId from the list of PimIds if it exists.
	* @param _pim The name of the PIM that should be removed.
	*/
	public void removePimId(String _pim) {
		for (int i = 0; i < pimIds.size(); i++)
			if (pimIds.get(i).pim.equals(_pim))
				pimIds.remove(i);
	}

	/**
	* Adds the ID of a new pim to the list of IDs.
	* <p>
	*	Note: Duplicates are not allowed. If a new ID is specified for an existing PIM the old ID will be overwritten.
	* </p>
	* @param _pim The PIM (key) that the uId is associated with.
	* @param uId The uId of the user at the specified PIM.
	*/
	public void addPimId(String _pim, String uId) {
		for (PimId pimId : pimIds)
			if (pimId.pim.equals(_pim)) {
				pimId.uId = uId;
				return;
			}

		pimIds.add(new PimId(_pim, uId));
	}

	/**
	* Sets value of pimIds.
	* @param pimIds Key-value pair of IDs of the user on various PIM platforms.
	*/
	public void setPimIds(List<PimId> pimIds) {
		this.pimIds = pimIds;
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
	* @return True or False indicating if the user is active or not.
	*/
	public boolean getIsActive() {
		return isActive;
	}

	/**
	* Sets new value of isActive.
	* @param isActive True or False indicating is the user is active or not.
	*/
	public void setIsActive(boolean isActive) {
		this.isActive = isActive;
	}

	/**
	* Returns value of persistMap.
	* @return Specifies whether the map should be persisted during reloads or not.
	*/
	public boolean getPersistMap() {
		return persistMap;
	}

	/**
	* Sets new value of persistMap.
	* @param persistMap Specifies whether the map should be persisted during reloads or not.
	*/
	public void setPersistMap(boolean persistMap) {
		this.persistMap = persistMap;
	}

	/**
	* Returns a string representation of a user used for printing.
	* @return User as a string.
	*/
	@Override
	public String toString() {
		String u = "";
		String t = "";

		if (pimIds == null)
			u = "NULL\n";
		else
			for (PimId pimId : pimIds)
				u += "\t\t" + pimId.pim + ": " + pimId.uId + "\n";

		if (theme == null)
			t = "NULL\n";
		else
			for (String themeS : theme)
				t += "\t\t" + themeS + "\n";

		return "User {\n" +
			"\tid:" + userId + ",\n" +
			"\tfirstName:" + firstName  + ",\n" +
			"\tlastName:" + lastName + ",\n" +
			"\tids: [\n" + u + "\t]\n" +
			"\ttheme: [\n" + t + "\t]\n" +
			"\tinitialDepth:" + initialDepth + "\n" +
			"\tbranchingFactor:" + branchingFactor + "\n" +
			"\tisActive:" + isActive + "\n" +
			"\tpersistMap:" + persistMap + "\n" +
		"}";
	}
}
