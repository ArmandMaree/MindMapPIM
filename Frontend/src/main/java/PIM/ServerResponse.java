package PIM;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private String content;
    private Boolean isRegistered;

    public ServerResponse(String content) {
        this.content = content;
        System.out.println("Repsonse created");
    }
    public ServerResponse(Boolean isRegistered) {
        this.isRegistered = isRegistered;
        System.out.println("Response created");
    }
    
    public ServerResponse(Boolean isRegistered,String content) {
        this.content = content;
        this.isRegistered = isRegistered;
        System.out.println("Response created");
    }

    public String getContent() {
        return content;
    }

    public Boolean getIsRegistered() {
        return isRegistered;
    }


}
