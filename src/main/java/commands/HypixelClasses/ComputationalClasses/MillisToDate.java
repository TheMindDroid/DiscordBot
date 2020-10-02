package commands.HypixelClasses.ComputationalClasses;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class MillisToDate {

    private final long millisecond;

    public MillisToDate(long millisecond) {
        this.millisecond = millisecond;
    }

    //Converts epoch time to date.
    public String convertMillisToDate() {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date(millisecond);
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(millisecond);

        return formatter.format(date);
    }
}