package commands.HypixelClasses.ComputationalClasses;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent;

import java.awt.*;

public class ErrorMessage {

    private final GuildMessageReceivedEvent event;
    private final String errorType;
    private final String message;
    private final boolean footer;


    public ErrorMessage(GuildMessageReceivedEvent event, String errorType, String message, boolean footer) {
        this.event = event;
        this.errorType = errorType;
        this.message = message;
        this.footer = footer;
    }

    //Sends a formatted error message.
    public void sendErrorMessage() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setColor(Color.RED);
        eb.setTitle(":warning: Error :warning:");
        eb.addField(errorType, message, true);
        if (footer) {
            eb.setFooter("*Player may not have a Skyblock profile or their API settings are disabled.");
        }
        event.getChannel().sendMessage(eb.build()).queue();
    }
}