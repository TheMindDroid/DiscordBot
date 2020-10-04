//Hypixel Bot: Sends Hypixel Skyblock player statistics to Discord channels.

package commands;

import commands.HypixelClasses.Collections.*;
import commands.HypixelClasses.ComputationalClasses.AddCommas;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import commands.HypixelClasses.Minion;
import commands.HypixelClasses.PlayerStats;
import commands.HypixelClasses.Skills;
import commands.HypixelClasses.SlayerStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.guild.Guild;
import zone.nora.slothpixel.guild.member.GuildMember;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;

import java.awt.*;
import java.util.Objects;

public class Hypixel extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String[] messageSentArray = event.getMessage().getContentRaw().split(" ");

        if (!messageSentArray[0].equalsIgnoreCase("~hypixel")) {
            return;
        }

        if (messageSentArray.length == 1) {
            new ErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel [collections/minions/guild/skills/slayer/stats]", false).sendErrorMessage();
            return;
        }

        Slothpixel hypixel = new Slothpixel();

        switch (messageSentArray[1]) {
            case "guild":
                getGuildInformation(event, hypixel);
                break;
            case "stats":
                new PlayerStats(event, hypixel, messageSentArray).getPlayerStatistics();
                break;
            case "slayer":
                new SlayerStats(event, hypixel, messageSentArray).getSlayerStats();
                break;
            case "skills":
                new Skills(event, hypixel, messageSentArray).getSkills();
                break;
            case "minions":
                new Minion(event, hypixel, messageSentArray).getMinions();
                break;
            case "collections":
                getCollections(event, hypixel, messageSentArray);
                break;
            default:
                new ErrorMessage(event, "Invalid Arguments...",
                        "Usage: ~hypixel [collections/minions/guild/stats/slayer/skills]", false)
                        .sendErrorMessage();
                break;
        }
    }


    public void getGuildInformation(GuildMessageReceivedEvent event, Slothpixel hypixel) {
        //Currently only gathers information of personal Guild as larger Guilds have a tendency to throw exceptions.
        event.getChannel().sendMessage("Processing request...").queue();

        try {
            Guild guild = hypixel.getGuild("86473522c26e4c2daf8b3fff05540b85");

            EmbedBuilder eb = new EmbedBuilder();

            eb.setColor(Color.GREEN);
            eb.setTitle(":shield: Guild Name: " + guild.getName());
            eb.addField(":label: Guild Tag:", guild.getTag(), true);
            eb.addField(":memo: Description", guild.getDescription(), true);

            //Calls guild public.
            if (guild.getPublic()) {
                eb.addField(":busts_in_silhouette: Guild Public:", ":white_check_mark:", true);
            } else {
                eb.addField(":busts_in_silhouette: Guild Public:", ":x:", true);
            }

            StringBuilder online = new StringBuilder();
            StringBuilder offline = new StringBuilder();

            //Calls member list.
            for (GuildMember member : guild.getMembers()) {
                try {
                    String name = Objects.requireNonNull(member.getProfile()).getUsername();
                    Player player = hypixel.getPlayer(name);

                    if (player.getOnline()) {
                        online.append(name).append("\n");
                    } else {
                        offline.append(name).append("\n");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            eb.addField("Online Players: :green_circle:", online.toString(), true);
            eb.addField("Offline Players: :red_circle:", offline.toString(), true);

            //Calls guild experience.
            eb.addField(":chart_with_upwards_trend: Guild Level:", "Level: " + guild.getLevel()
                    + " \nExperience: " + new AddCommas(guild.getExp()).addCommas(), true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();
        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to gather Guild information." , false)
                    .sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }


    //Returns the collection stats of the player.
    public void getCollections(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {
        if (messageSentArray.length == 2 || messageSentArray.length == 3) {
            new ErrorMessage(event, "Invalid Arguments...",
                    "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",true)
                    .sendErrorMessage();
            return;
        }

        try {
            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockCollection skyblockCollectionTiers = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getCollectionTiers();
            SkyblockCollection skyblockCollectionExp = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid()).getCollection();

            switch (messageSentArray[3]) {
                case "farming":
                    new FarmingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName).getCollection();
                    break;
                case "mining":
                    new MiningCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName).getCollection();
                    break;
                case "combat":
                    new CombatCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName).getCollection();
                    break;
                case "foraging":
                    new ForagingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName).getCollection();
                    break;
                case "fishing":
                    new FishingCollection(event, skyblockCollectionTiers, skyblockCollectionExp, playerName).getCollection();
                    break;
                default:
                    new ErrorMessage(event, "Invalid Arguments...",
                            "Usage: ~hypixel collections <player> [combat/farming/fishing/foraging/mining]",
                            true).sendErrorMessage();
                    break;
            }
        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve Minions data.*", true).sendErrorMessage();
            event.getChannel().sendMessage("**Exception:** " + e).queue();
        }
    }
}