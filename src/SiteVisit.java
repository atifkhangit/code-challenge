
import java.util.ArrayList;

/**
 * Created by Atif on 6/11/2017.
 */
public class SiteVisit {
    private String type;
    private String verb;
    private String key;
    private String eventTime;
    private String customerId;
    private ArrayList<Object> tags;

    public SiteVisit() {
        this.type = "SITE_VISIT";
    }

    /**
     * Getter and setter methods for class variables.
     */

    public void setTags(ArrayList<Object> obj) {
        this.tags = obj;
    }

    public String getVerb() {
        return verb;
    }

    public void setVerb(String verb) {
        this.verb = verb;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getEventTime() {
        return eventTime;
    }

    public void setEventTime(String eventTime) {
        this.eventTime = eventTime;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }
}
