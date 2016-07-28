package PIM;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private String content;

    public ServerResponse(String content) {
        this.content = content;
        System.out.println("Resonse created");
    }

    public String getContent() {
        return content;
    }

}
