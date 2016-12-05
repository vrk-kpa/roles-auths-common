package fi.vm.kapa.rova.help;

/**
 * Created by mtom on 12/5/16.
 */
public class HelpDocument {
    private String title;
    private String body;

    public HelpDocument(String title, String body) {
        this.title = title;
        this.body = body;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
