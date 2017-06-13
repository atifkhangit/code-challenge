/**
 * Created by Atif on 6/11/2017.
 */
public class Customer {
    public double LTV = 0;
    public int active = 1;
    public float revenuePerVisit = 0;
    public float visitPerWeek = 0;
    private String type;
    private String verb;
    private String key;
    private String eventTime;
    private String lastName;
    private String adrCity;
    private String adrState;

    public Customer() {
        this.type = "CUSTOMER";
    }

    /**
     * Getter and setter methods for class variables.
     */
    public void setadrCity(String adrCity) {
        this.adrCity = adrCity;
    }

    public void setadrState(String adrState) {
        this.adrState = adrState;
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

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getadrCity() {
        return adrCity;
    }

    public String getadrState() {
        return adrState;
    }

}