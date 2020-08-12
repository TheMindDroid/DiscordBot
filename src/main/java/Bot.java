import commands.Help;
import commands.Hypixel;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

public class Bot {

    public static void main (String[] args) throws Exception{

        JDA jda = JDABuilder.createDefault(args[0]).build();

        jda.addEventListener(new Hypixel());
        jda.addEventListener(new Help());
    }
}