package PIM;

import java.io.Serializable;

public class AccessToken implements Serializable {

    private String authCode;

    public String getAuthCode() {
        return authCode;
    }
}
