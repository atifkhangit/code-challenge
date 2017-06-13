/**
 * Created by Atif on 6/12/2017.
 */

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateCalculator {
    public static final DateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
    private String eventTime;
    private String firstDay;

    public DateCalculator(String eventTime) {
        this.eventTime = eventTime;
    }

    /**
     * @return returns week's start date for the event based on event_time.
     */
    public String getFirstDay() {
        try {
            Date dt = fmt.parse(eventTime);
            Calendar c = Calendar.getInstance();
            c.setTime(dt);
            c.set(Calendar.DAY_OF_WEEK, Calendar.SUNDAY);
            c.set(Calendar.MINUTE, 0);
            c.set(Calendar.SECOND, 0);
            c.set(Calendar.HOUR_OF_DAY, 0);
            firstDay = fmt.format(c.getTime());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return firstDay;
    }
}
