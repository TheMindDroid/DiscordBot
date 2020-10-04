package commands.HypixelClasses.Collections;

import commands.HypixelClasses.CollectionBuilder;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class FishingCollection {

    private final GuildMessageReceivedEvent event;
    private final SkyblockCollection tiers;
    private final SkyblockCollection exp;
    private final String playerName;

    public FishingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        this.event = event;
        this.tiers = tiers;
        this.exp = exp;
        this.playerName = playerName;
    }

    //Returns fishing collection data.
    public void getCollection() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Fishing Collection :video_game:");

        List<CollectionBuilder> collections = new ArrayList<>();
        collections.add(new CollectionBuilder(tiers.getClayBall(), exp.getClayBall(), ":fishing_pole_and_fish: Clay"));
        collections.add(new CollectionBuilder(tiers.getRawFish2(), exp.getRawFish2(), ":fishing_pole_and_fish: Clown Fish"));
        collections.add(new CollectionBuilder(tiers.getInkSack(), exp.getInkSack(), ":fishing_pole_and_fish: Ink Sack"));
        collections.add(new CollectionBuilder(tiers.getWaterLily(), exp.getWaterLily(), ":fishing_pole_and_fish: Lily Pad"));
        collections.add(new CollectionBuilder(tiers.getRawFish(), exp.getRawFish(), ":fishing_pole_and_fish: Raw Fish"));
        collections.add(new CollectionBuilder(tiers.getRawFish1(), exp.getRawFish1(), ":fishing_pole_and_fish: Raw Salmon"));
        collections.add(new CollectionBuilder(tiers.getPrismarineCrystals(), exp.getPrismarineCrystals(), ":fishing_pole_and_fish: Prismarine Crystal"));
        collections.add(new CollectionBuilder(tiers.getPrismarineShard(), exp.getPrismarineShard(), ":fishing_pole_and_fish: Prismarine Shard"));
        collections.add(new CollectionBuilder(tiers.getRawFish3(), exp.getRawFish3(), ":fishing_pole_and_fish: Puffer Fish"));
        collections.add(new CollectionBuilder(tiers.getSponge(), exp.getSponge(), ":fishing_pole_and_fish: Sponge"));

        for (CollectionBuilder collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}