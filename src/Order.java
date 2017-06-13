/**
 * Created by Atif on 6/11/2017.
 */
public class Order {
    public int active = 1;
    private String type;
    private String verb;
    private String key;
    private String eventTime;
    private String customerId;
    private float totalAmount;

    public Order() {
        this.type = "ORDER";
    }

    public String getVerb() {
        return verb;
    }

    /**
     * Getter and setter methods for the class variables.
     */
    public void setVerb(String verb) {
        this.verb = verb;
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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Float getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(Float totalAmount) {
        this.totalAmount = totalAmount;
    }
}
