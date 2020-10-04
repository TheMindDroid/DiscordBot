//Hypixel Bot: Sends Hypixel Skyblock player statistics to Discord channels.

package commands;

import commands.HypixelClasses.Collections.*;
import commands.HypixelClasses.ComputationalClasses.AddCommas;
import commands.HypixelClasses.ComputationalClasses.AddDatedFooter;
import commands.HypixelClasses.ComputationalClasses.ErrorMessage;
import commands.HypixelClasses.ComputationalClasses.MillisToDate;
import commands.HypixelClasses.Minion;
import commands.HypixelClasses.Skills;
import commands.HypixelClasses.SlayerStats;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import zone.nora.slothpixel.Slothpixel;
import zone.nora.slothpixel.guild.Guild;
import zone.nora.slothpixel.guild.member.GuildMember;
import zone.nora.slothpixel.player.Player;
import zone.nora.slothpixel.skyblock.items.SkyblockItem;
import zone.nora.slothpixel.skyblock.players.SkyblockPlayer;
import zone.nora.slothpixel.skyblock.players.collection.SkyblockCollection;
import zone.nora.slothpixel.skyblock.players.stats.auctions.SkyblockPlayerAuctions;
import zone.nora.slothpixel.skyblock.players.stats.kills.SkyblockPlayerKills;

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
                getPlayerStatistic(event, hypixel, messageSentArray);
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


    public void getPlayerStatistic(GuildMessageReceivedEvent event, Slothpixel hypixel, String[] messageSentArray) {

        if (messageSentArray.length == 2) {
            new ErrorMessage(event, "Invalid Arguments...", "Please provide a valid Hypixel player. \n" +
                    "Usage: ~hypixel stats <player>", false).sendErrorMessage();
            return;
        }

        try {
            EmbedBuilder eb = new EmbedBuilder();
            eb.setColor(Color.GREEN);

            String playerName = messageSentArray[2];
            Player player = hypixel.getPlayer(playerName);
            SkyblockPlayer skyblockPlayer = hypixel.getSkyblockProfile(playerName).getMembers().get(player.getUuid());

            //Sets user's MC profile picture.
            eb.setImage("https://minotar.net/helm/" + playerName + "/100.png");
            eb.setTitle(":video_game: Player Name: " + playerName + " :video_game:");

            //Calls if player is online.
            if (player.getOnline()) {
                eb.addField("Player Online: ", ":green_circle: \n"
                        + "Location: " + hypixel.getPlayerStatus(playerName).getGame().getMode(), true);
            } else {
                String lastLoginDate = new MillisToDate(player.getLastLogin()).convertMillisToDate();
                eb.addField("Player Online: ", ":red_circle: \n [Last Online: " + lastLoginDate + "] :hourglass:", true);
            }

            //Calls the rank of the player.
            StringBuilder rank = new StringBuilder();
            try {
                String[] rankArray = player.getRank().split("");

                if (player.getRank().contains("_PLUS")) {
                    if (player.getRank().contains("_PLUS_PLUS")) {
                        rank.append("MVP++");
                    } else {
                        for (int i = 0; i < 3; i++) {
                            rank.append(rankArray[i]);
                        }
                        rank.append("+");
                    }
                } else {
                    rank.append(player.getRank());
                }
                eb.addField(":military_medal: Player Rank: ", rank.toString(), true);
            } catch (Exception e) {
                eb.addField(":military_medal: Player Rank: ", "No Rank", true);
            }

            //Calls money in purse.
            if (hypixel.getSkyblockProfile(playerName).getBanking().getBalance() == 0) {
                eb.addField(":moneybag: Money:","Purse: $" + new AddCommas(skyblockPlayer.getCoinPurse()).addCommas(),
                        true);
            } else {
                eb.addField(":moneybag: Money:","Bank: $"
                        + new AddCommas(hypixel.getSkyblockProfile(playerName).getBanking().getBalance()).addCommas()
                        + "\nPurse: $" + new AddCommas(skyblockPlayer.getCoinPurse()).addCommas(), true);
            }

            //Calls the armor the player is wearing.
            StringBuilder armor = new StringBuilder();
            for (SkyblockItem skyblockItem : skyblockPlayer.getArmor()) {
                String[] shortenedName = skyblockItem.getName().split("");

                for (int i = 2; i < shortenedName.length; i++) {
                    if (!shortenedName[i].equalsIgnoreCase("§")) {
                        armor.append(shortenedName[i]);
                    } else {
                        break;
                    }
                }

                if (!skyblockItem.getName().isEmpty()) {
                    armor.append("\n");
                }
            }

            if (armor.toString().equalsIgnoreCase("")) {
                eb.addField(":shield: Armor Loadout: ", "No Armor Equipped.", true);
            } else {
                eb.addField(":shield: Armor Loadout: ", armor.toString(), true);
            }

            //Calls auction statistics for the player.
            try {
                SkyblockPlayerAuctions skyblockPlayerAuctions = skyblockPlayer.getStats().getAuctions();
                eb.addField(":chart_with_upwards_trend: Auction Stats:", "Money Spent: $"
                        + new AddCommas(skyblockPlayerAuctions.getGoldSpent()).addCommas()
                        + "\n Money Earned: $" + new AddCommas(skyblockPlayerAuctions.getGoldEarned()).addCommas()
                        + "\n Commons Sold: " + skyblockPlayerAuctions.getSold().getCommon()
                        + "\n Uncommons Sold: " + skyblockPlayerAuctions.getSold().getUncommon()
                        + "\n Rares Sold: " + skyblockPlayerAuctions.getSold().getRare()
                        + "\n Epics Sold: " + skyblockPlayerAuctions.getSold().getRare()
                        + "\n Legendaries Sold: " + skyblockPlayerAuctions.getSold().getLegendary(), true);
            } catch (Exception e) {
                eb.addField(":chart_with_upwards_trend: Auction Stats:", "Player has no auction data.", true);
            }

            //Calls combat statistics for the player.
            try {
                eb.addField(":crossed_swords: Combat Statistics:", "Total Kills: "
                        + new AddCommas(skyblockPlayer.getStats().getTotalKills()).addCommas()
                        + "\nTotal Deaths: " + new AddCommas(skyblockPlayer.getStats().getTotalDeaths()).addCommas()
                        + "\nHighest Crit Damage: " + new AddCommas(skyblockPlayer.getStats().getHighestCriticalDamage()).addCommas(),
                        true);
            } catch (Exception e) {
                eb.addField(":crossed_swords: Combat Statistics:", "Player does not have available combat statistics.", true);
            }

            //Calls fishing catches.
            try {
                SkyblockPlayerKills skyblockPlayerKills = skyblockPlayer.getStats().getKills();

                int totalSeaCreatureKills = skyblockPlayerKills.getWaterHydra()
                        + skyblockPlayerKills.getSeaWitch()
                        + skyblockPlayerKills.getSeaWalker()
                        + skyblockPlayerKills.getSeaLeech()
                        + skyblockPlayerKills.getSeaGuardian()
                        + skyblockPlayerKills.getSeaArcher()
                        + skyblockPlayerKills.getPondSquid()
                        + skyblockPlayerKills.getNightSquid()
                        + skyblockPlayerKills.getGuardianEmperor()
                        + skyblockPlayerKills.getFrozenSteve()
                        + skyblockPlayerKills.getFrostyTheSnowman()
                        + skyblockPlayerKills.getDeepSeaProtector()
                        + skyblockPlayerKills.getChickenDeep()
                        + skyblockPlayerKills.getCatfish()
                        + skyblockPlayerKills.getCarrotKing();

                eb.addField(":fishing_pole_and_fish: Items Fished:", "Total Items: "
                        + new AddCommas(skyblockPlayer.getStats().getItemsFished().getTotal()).addCommas()
                        + "\nNormal Items: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getNormal()).addCommas()
                        + "\nTreasures Fished: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getTreasure()).addCommas()
                        + "\nLarge Treasures Fished: " + new AddCommas(skyblockPlayer.getStats().getItemsFished().getLargeTreasure()).addCommas()
                        + "\nSea Creatures Killed: " + new AddCommas(totalSeaCreatureKills).addCommas(), true);
            } catch (Exception e) {
                eb.addField(":tropical_fish: Items Fished:", "Unable to retrieve fishing data.", true);
            }

            //Calls fairy souls collected.
            eb.addField(":woman_fairy_tone2: Fairy Souls:", skyblockPlayer.getFairySoulsCollected() + " / "
                    + "209", true);

            //Calls date first joined.
            eb.addField(":calendar: Player First Joined:", new MillisToDate(skyblockPlayer.getFirstJoin()).convertMillisToDate(),
                    true);

            eb.setFooter(new AddDatedFooter(Objects.requireNonNull(event.getMember()).getUser().getAsTag()).addDatedFooter());
            event.getChannel().sendMessage(eb.build()).queue();

        } catch (Exception e) {
            new ErrorMessage(event, "API Unavailable", "Unable to retrieve player data.*", true).sendErrorMessage();
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