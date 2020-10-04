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

public class FarmingCollection {

    private final GuildMessageReceivedEvent event;
    private final SkyblockCollection tiers;
    private final SkyblockCollection exp;
    private final String playerName;

    public FarmingCollection(GuildMessageReceivedEvent event, SkyblockCollection tiers, SkyblockCollection exp, String playerName) {
        this.event = event;
        this.tiers = tiers;
        this.exp = exp;
        this.playerName = playerName;
    }

    //Returns farming collection data.
    public void getCollection() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.GREEN);

        eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
        eb.setTitle(":video_game: " + playerName + "'s Farming Collection :video_game:");

        List<CollectionBuilder> collections = new ArrayList<>();
        collections.add(new CollectionBuilder(tiers.getCactus(), exp.getCactus(), ":tractor: Cactus"));
        collections.add(new CollectionBuilder(tiers.getCarrotItem(), exp.getCarrotItem(), ":tractor: Carrot"));
        collections.add(new CollectionBuilder(tiers.getRawChicken(), exp.getRawChicken(), ":tractor: Chicken"));
        collections.add(new CollectionBuilder(tiers.getFeather(), exp.getFeather(), ":tractor: Feather"));
        collections.add(new CollectionBuilder(tiers.getLeather(), exp.getLeather(), ":tractor: Leather"));
        collections.add(new CollectionBuilder(tiers.getMelon(), exp.getMelon(), ":tractor: Melon"));
        collections.add(new CollectionBuilder(tiers.getMushroomCollection(), exp.getMushroomCollection(), ":tractor: Mushroom"));
        collections.add(new CollectionBuilder(tiers.getMutton(), exp.getMutton(), ":tractor: Mutton"));
        collections.add(new CollectionBuilder(tiers.getNetherStalk(), exp.getNetherStalk(), ":tractor: Nether Wart"));
        collections.add(new CollectionBuilder(tiers.getPork(), exp.getPork(), ":tractor: Pork Chop"));
        collections.add(new CollectionBuilder(tiers.getPotatoItem(), exp.getPotatoItem(), ":tractor: Potato"));
        collections.add(new CollectionBuilder(tiers.getPumpkin(), exp.getPumpkin(), ":tractor: Pumpkin"));
        collections.add(new CollectionBuilder(tiers.getRabbit(), exp.getRabbit(), ":tractor: Rabbit"));
        collections.add(new CollectionBuilder(tiers.getSeeds(), exp.getSeeds(), ":tractor: Seeds"));
        collections.add(new CollectionBuilder(tiers.getSugarCane(), exp.getSugarCane(), ":tractor: Sugar Cane"));
        collections.add(new CollectionBuilder(tiers.getWheat(), exp.getWheat(), ":tractor: Wheat"));

        for (CollectionBuilder collection : collections) {
            eb.addField(collection.getCompletedString());
        }

        eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
        event.getChannel().sendMessage(eb.build()).queue();
    }
}