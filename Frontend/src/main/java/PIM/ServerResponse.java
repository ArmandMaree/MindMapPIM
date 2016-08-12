package PIM;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private String content;
    private Boolean isRegistered;

    public ServerResponse(String content) {
        this.content = content;
        System.out.println("Resonse created");
    }
    public ServerResponse(Boolean isRegistered) {
        this.isRegistered = isRegistered;
        System.out.println("Response created");
    }

    public String getContent() {
        return content;
    }

    public String getIsRegistered() {
        return isRegistered;
    }


}
