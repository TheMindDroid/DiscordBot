package commands;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class Help extends ListenerAdapter {

    public void onGuildMessageReceived(GuildMessageReceivedEvent event) {

        String messageSent = event.getMessage().getContentRaw();
        if(messageSent.equals("~") || messageSent.equals("~help")) {

            EmbedBuilder eb = new EmbedBuilder();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyy HH:mm:ss");
            Date date = new Date();

            eb.setColor(Color.RED);
            eb.setTitle(":question: Help :question:");
            eb.addField("~help", "Displays this help message.", true);
            eb.addField("~hypixel", "Displays options for the Hypixel API.", true);

            eb.setFooter("Request was made @ " + formatter.format(date) + " by " + Objects.requireNonNull(event.getMember()).getUser().getAsTag());
            event.getChannel().sendMessage(eb.build()).queue();
        }
    }
}
