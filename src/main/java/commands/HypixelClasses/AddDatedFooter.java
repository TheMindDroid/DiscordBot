package commands.HypixelClasses;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AddDatedFooter {

    private final String name;

    public AddDatedFooter(String name) {
        this.name = name;
    }

    //Adds a dated footer to each embed.
    public String addDatedFooter() {

        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss a");
        Date date = new Date();

        return "Request was made @ " + formatter.format(date) + " by " + name;
    }
}