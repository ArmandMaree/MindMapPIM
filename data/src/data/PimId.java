package data;

import java.io.Serializable;

/**
* A key value pair that contains the uId accociated with a certain PIM.
*
* @author  Armand Maree
* @since   1.0.0
*/
public class PimId implements Serializable {
	private static final long serialVersionUID = 207904753280249L;

	/**
	* Name of the PIM. Case sensitive.
	*/
	public String pim = "";

	/**
	* The ID associated with the PIM.
	*/
	public String uId = "";

	public PimId() {

	}

	/**
	* Constructor.
	* @param pim Name of the PIM. Case sensitive.
	* @param uId The ID associated with the PIM.
	*/
	public PimId(String pim, String uId) {
		this.pim = pim;
		this.uId = uId;
	}
}
