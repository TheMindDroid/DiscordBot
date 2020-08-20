//Manages data for the Skyblock Collections.
package commands.HypixelClasses;

import net.dv8tion.jda.api.entities.MessageEmbed;

import java.text.DecimalFormat;

public class Collection {

    private int skyblockCollectionTiers;
    private int skyblockCollectionExp;
    private String collectionName;

    public Collection(int skyblockCollectionTiers, int skyblockCollectionExp, String collectionName) {
        this.skyblockCollectionTiers = skyblockCollectionTiers;
        this.skyblockCollectionExp = skyblockCollectionExp;
        this.collectionName = collectionName;

        if (skyblockCollectionTiers == -1) {
            this.skyblockCollectionTiers *= -1;
        }
    }

    public MessageEmbed.Field getCompletedString() {

        return new MessageEmbed.Field(collectionName, "Level: " + skyblockCollectionTiers + "\nExperience: "
                + addCommas(skyblockCollectionExp), true);
    }

    public static String addCommas(double withoutCommas) {

        DecimalFormat decimalFormat = new DecimalFormat(",###");
        decimalFormat.setGroupingUsed(true);
        decimalFormat.setGroupingSize(3);

        return decimalFormat.format(withoutCommas);
    }
}