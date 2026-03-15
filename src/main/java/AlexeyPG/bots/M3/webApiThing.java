package AlexeyPG.bots.M3;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.model.TokensResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

public class webApiThing {
    public static void start() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(20207); //TODO make port configurable

        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                PrintWriter writer = resp.getWriter();
                resp.setHeader("Access-Control-Allow-Origin", "*");
                //I know that's not how it works. I don't know how to make it work how it supposes to work. Well it still works so who cares
                if(req.getParameter("question") != null) switch (req.getParameter("question")){
                    case "PIOnline": writer.println(statistics.getOnline("860944840853291008"));break;
                    case "PITotal": writer.println(statistics.getTotal("860944840853291008"));break;
                    case "PIInGame": writer.println(statistics.getInGame("860944840853291008"));break;
                    case "SendRoles": writer.println(publicAccess.roles.getRolesFormatted());break;
                    case "addRole": writer.println(grantRole(req.getParameter("oAuth2code"),req.getParameter("role")));break;
                    case "addRole2": writer.println(grantRole2(req.getParameter("refreshToken"),req.getParameter("role")));break;
                    case "addRoleWithAT":
                    case "sendReview": break;
                    default: writer.println("Unknown");break;
                }
            }
        };

        String servletName = "Servlet1";
        String urlPattern = "/MARAZMUS";

        tomcat.addServlet(contextPath, servletName, servlet);
        context.addServletMappingDecoded(urlPattern, servletName);
        tomcat.start();
        System.out.println(tomcat.getConnector().getState()); //It doesn't work without this line

        tomcat.getServer().await();
    }

    //TODO: REDO all this shit (or at least clean it all up)
    //and make better bulk role selector

    public static String grantRole2(String refreshTokenIn, String roleID){
        String refreshToken = "";
        try {
            Connection request = Jsoup.connect("https://discord.com/api/oauth2/token").data("client_id", dataManager.getData("data/bot/oAuth2/private","clientID")).data("client_secret", dataManager.getData("data/bot/oAuth2/private","clientSecret")).data("grant_type", "refresh_token").data("refresh_token", refreshTokenIn).ignoreContentType(true);
            String response = request.post().body().text();
            Gson gson = (new GsonBuilder()).serializeNulls().enableComplexMapKeySerialization().create();

            TokensResponse tokens = ((TokensResponse)gson.fromJson(response, TokensResponse.class));
            DiscordAPI api = new DiscordAPI(tokens.getAccessToken());
            refreshToken = tokens.getRefreshToken();


            for(String roleID2 : roleID.split(",")) {
                if (publicAccess.roles.isRoleIDPublic(roleID2)) {
                    System.out.println("granting");
                    Role role = Main.jda.getRoleById(roleID2);
                    Guild guild = Main.jda.getGuildById("860944840853291008");

                    System.out.println(role.getName());
                    System.out.println(guild.getName());

                    guild.retrieveMemberById(api.fetchUser().getId()).queue(member -> {
                        if (!member.getRoles().contains(role)) {
                            guild.addRoleToMember(member, role).queue();
                            Main.jda.getTextChannelById(config.get("Game message channel")).sendMessage("Удалённый запрос: <@" + member.getUser().getId() + "> теперь ищет в **" + role.getName() + "**").setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                        }
                    });
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return refreshToken;
    }

    public static String grantRole(String oAuth2code,String roleID){
        System.out.println("start");
        String refreshToken = "";
        try {
            Connection request = Jsoup.connect("https://discord.com/api/oauth2/token").data("client_id", dataManager.getData("data/bot/oAuth2/private","clientID")).data("client_secret", dataManager.getData("data/bot/oAuth2/private","clientSecret")).data("grant_type", "authorization_code").data("code", oAuth2code).data("redirect_uri", dataManager.getData("data/bot/oAuth2/private","url")).data("scope", String.join(" ", "identify")).ignoreContentType(true);

            String response = request.post().body().text();
            Gson gson = (new GsonBuilder()).serializeNulls().enableComplexMapKeySerialization().create();

            TokensResponse tokens = ((TokensResponse)gson.fromJson(response, TokensResponse.class));
            DiscordAPI api = new DiscordAPI(tokens.getAccessToken());
            refreshToken = tokens.getRefreshToken();


            for(String roleID2 : roleID.split(",")){
            if(publicAccess.roles.isRoleIDPublic(roleID2)) {
                System.out.println("granting");
                Role role = Main.jda.getRoleById(roleID2);
                Guild guild = Main.jda.getGuildById("860944840853291008");

                System.out.println(role.getName());
                System.out.println(guild.getName());

                guild.retrieveMemberById(api.fetchUser().getId()).queue(member -> {
                    if (!member.getRoles().contains(role)) {
                        guild.addRoleToMember(member, role).queue();
                        Main.jda.getTextChannelById(config.get("Game message channel")).sendMessage("Удалённый запрос: <@" + member.getUser().getId() + "> теперь ищет в **" + role.getName() + "**").setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                    }
                });
            }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }


        return refreshToken;
    }
}
