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

public class ForagingCollection {

    private final GuildMessageReceivedEvent event;
    private final SkyblockCollection tiers;
    private final SkyblockCollection exp;
    private final String playerName;

    public ForagingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        this.event = event;
        this.tiers = tiers;
        this.exp = exp;
        this.playerName = playerName;
    }

    //Returns foraging collection data.
    public void getCollection() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Foraging Collection :video_game:");

        List<CollectionBuilder> collections = new ArrayList<>();
        collections.add(new CollectionBuilder(tiers.getLog_2(), exp.getLog_2(), ":evergreen_tree: Acacia Wood"));
        collections.add(new CollectionBuilder(tiers.getLog2(), exp.getLog2(), ":evergreen_tree: Birch Wood"));
        collections.add(new CollectionBuilder(tiers.getLog_21(), exp.getLog_21(), ":evergreen_tree: Dark Oak Wood"));
        collections.add(new CollectionBuilder(tiers.getLog(), exp.getLog(), ":evergreen_tree: Oak Wood"));
        collections.add(new CollectionBuilder(tiers.getLog3(), exp.getLog3(), ":evergreen_tree: Jungle Wood"));
        collections.add(new CollectionBuilder(tiers.getLog1(), exp.getLog1(), ":evergreen_tree: Spruce Wood"));

        for (CollectionBuilder collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}