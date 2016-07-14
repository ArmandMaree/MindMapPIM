package webservices.google;

/**
* Used by the RESTController to map JSON objects to. Class contains the authorization code needed to access a user account.
*
* @author  Armand Maree
* @since   2016-07-11
*/
public class AuthCode {
    private String authCode = "";

    public AuthCode() {
    	
    }

    /**
    * Constructor that sets the authCode
    */
    public AuthCode(String authCode) {
    	this.authCode = authCode;
    }

    /**
    * Getter to return the authCode.
    * @return Returns the authCode.
    */
    public String getAuthCode() {
        return authCode;
    }
}
