package commands.HypixelClasses.ComputationalClasses;

import java.text.DecimalFormat;

public class AddCommas {

    private final double withoutCommas;

    public AddCommas(double withoutCommas) {
        this.withoutCommas = withoutCommas;
    }

    //Adds comma separators to large numbers.
    public String addCommas() {

        DecimalFormat decimalFormat = new DecimalFormat(",###");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(withoutCommas);
    }
}