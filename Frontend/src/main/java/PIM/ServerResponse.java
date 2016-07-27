package PIM;

import java.io.Serializable;

public class ServerResponse implements Serializable {

    private String content;

    public ServerResponse(String content) {
        this.content = content;
    }

    public String getContent() {
        return content;
    }

}
