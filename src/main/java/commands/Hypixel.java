//Hypixel Bot: Sends Hypixel Skyblock player statistics to Discord channels.

package commands;

import commands.HypixelClasses.Collections.*;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import commands.HypixelClasses.*;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;

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
                new GuildInfo(event, hypixel).getGuildInformation();
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