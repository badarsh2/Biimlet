package example.com.miibletsample;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by srivatsan on 24/9/15.
 */
public class EMail {
    public String Id;
    public String From;
    public String Subject;
    public String Snippet;
    public EMail(String From, String Subject, String Snippet, String Id) {
        this.From = From;
        this.Subject = Subject;
        this.Snippet = Snippet;
        this.Id = Id;
    }
    public String makeStr() throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("From", From);
        jsonObject.put("Subject",Subject);
        jsonObject.put("Snippet",Snippet);
        jsonObject.put("Id",Id);
        return jsonObject.toString();
    }
}
