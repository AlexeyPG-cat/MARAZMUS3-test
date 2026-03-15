package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.voiceNotifier;
import io.mokulu.discord.oauth.DiscordOAuth;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.MemberCachePolicy;
import net.dv8tion.jda.api.utils.cache.CacheFlag;
import org.apache.catalina.LifecycleException;

import java.util.List;



public class Main {
    public static boolean debug_mode = false;
    public static JDA jda;
    public static DiscordOAuth oAuthHandler =  new DiscordOAuth(dataManager.getData("data/bot/oAuth2/private","clientID"),dataManager.getData("data/bot/oAuth2/private","clientSecret"),dataManager.getData("data/bot/oAuth2/private","url"),new String[]{"identify"});
    public static String version = "0.2";
    public static void main(String[] args) throws LifecycleException {
        config.addMissingConfigs();
        voiceNotifier.loadWatchers();
        start(config.get("Token"));
        Thread consoleInput = new Thread(new localConsoleInput());
        consoleInput.start();
        webApiThing.start();

    }
    public static void start(String token){
        try {
            Bot bot = new Bot();
            jda = JDABuilder.createDefault(token)
                    .addEventListeners(bot)
                    .enableIntents(
                            List.of(GatewayIntent.GUILD_MESSAGES,
                                    GatewayIntent.GUILD_PRESENCES,
                                    GatewayIntent.MESSAGE_CONTENT,
                                    GatewayIntent.SCHEDULED_EVENTS,
                                    GatewayIntent.GUILD_MEMBERS
                                    //GatewayIntent.DIRECT_MESSAGES,
                                    //GatewayIntent.DIRECT_MESSAGE_TYPING
                            ))
                    .setStatus(OnlineStatus.IDLE)
                    .setMemberCachePolicy(MemberCachePolicy.ALL)
                    .enableCache(CacheFlag.ACTIVITY)
                    .build();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}

//ignore this