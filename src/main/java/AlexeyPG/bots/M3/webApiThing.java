package AlexeyPG.bots.M3;

import AlexeyPG.bots.M3.features.reviews.PublicReview;
import AlexeyPG.bots.M3.features.reviews.Review;
import AlexeyPG.bots.M3.features.reviews.reviewManager;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.mokulu.discord.oauth.DiscordAPI;
import io.mokulu.discord.oauth.model.TokensResponse;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import kotlin.Pair;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.User;
import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.startup.Tomcat;
import org.jsoup.Connection;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class webApiThing {
    public static void start() throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setBaseDir("temp");
        tomcat.setPort(20207);

        String contextPath = "/";
        String docBase = new File(".").getAbsolutePath();

        Context context = tomcat.addContext(contextPath, docBase);

        HttpServlet servlet = new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                PrintWriter writer = resp.getWriter();
                resp.setHeader("Access-Control-Allow-Origin", "*");
                if (req.getParameter("question") != null) switch (req.getParameter("question")) {
                    case "authorize": authorize(req, resp, writer);break;
                    case "SendRoles": writer.println(publicAccess.roles.getRolesFormatted());break;
                    case "getStatistics": getStatistics(req, resp, writer);break;
                    case "getReviews": getReviews(req, resp, writer);break;
                    default: resp.setStatus(400);writer.println("Unknown");break;
                }
            }
            @Override
            protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                PrintWriter writer = resp.getWriter();
                resp.setHeader("Access-Control-Allow-Origin", "*");
                if (req.getParameter("question") != null) switch (req.getParameter("question")) {
                    case "sendReview": saveReview(req, resp, writer);break;
                    case "deleteReview": deleteReview(req, resp, writer);break;
                    case "rateReview": rateReview(req, resp, writer);break;
                    case "addRoles": grantRoles(req, resp, writer);break;
                    default: resp.setStatus(400);writer.println("Unknown");break;
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



    private static void getStatistics(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Map<String, Integer> data = new HashMap<>();
        data.put("Total", statistics.getTotal("860944840853291008"));
        data.put("Online", statistics.getOnline("860944840853291008"));
        data.put("InGame", statistics.getInGame("860944840853291008"));
        String json = gson.toJson(data);
        writer.println(json);
    }

    private static void grantRoles(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {
        Pair<User, String> auths = reAuthorize(req.getParameter("token"));
        User user = auths.component1();
        Map<String, String> data = new HashMap<>();
        if(user == null){
            resp.setStatus(400);
            writer.println("Invalid token");
            return;
        }
        if (req.getParameter("roles") == null) {
            resp.setStatus(400);
            data.put("Error", "Missing roles input");
        } else {
            try {
                ArrayList<Role> roles = new ArrayList<>();
                for (String roleID : req.getParameter("roles").split(",")) {
                    if (publicAccess.roles.isRoleIDPublic(roleID))
                        roles.add(Main.jda.getRoleById(roleID));
                }
                Guild guild = Main.jda.getGuildById("860944840853291008");
                if(user!=null)
                    guild.retrieveMemberById(user.getId()).queue(member -> {
                    ArrayList<Role> roles2 = new ArrayList<>();
                    for (Role role_ : roles) {
                        if (!member.getRoles().contains(role_))
                            roles2.add(role_);
                    }
                    StringBuilder sb = new StringBuilder();
                    for (Role role_ : roles2) {
                        if (!sb.isEmpty()) {
                            sb.append(", ");
                        }
                        guild.addRoleToMember(member, role_).queue();
                        sb.append(role_.getName());
                    }
                    if(!roles2.isEmpty())
                        Main.jda.getTextChannelById(config.get("Game message channel")).sendMessage("Удалённый запрос: <@" + member.getUser().getId() + "> теперь ищет в **" + sb + "**").setSuppressedNotifications(true).setAllowedMentions(List.of()).queue();
                });
                else{
                    resp.setStatus(400);
                    data.put("Error","Invalid user");
                }
            } catch (Exception e) {
                System.out.println("Exception trying give role:" + e.getMessage());
            }
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        data.put("token", auths.component2());
        String json = gson.toJson(data);
        writer.println(json);
    }

    public static void rateReview(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer){
        Pair<User, String> auths = reAuthorize(req.getParameter("token"));
        User user = auths.component1();
        Map<String, String> data = new HashMap<>();
        if(user == null){
            resp.setStatus(400);
            writer.println("Invalid token");
            return;
        }
        if (req.getParameter("review") == null) {
            resp.setStatus(400);
            data.put("Error", "Missing review");
        } else if (req.getParameter("rating") == null) {
            resp.setStatus(400);
            data.put("Error", "Missing rating");
        } else if ((!req.getParameter("rating").equals("like")) && (!req.getParameter("rating").equals("dislike"))) {
            resp.setStatus(400);
            data.put("Error", "Unknown rating");
        } else {
            try {
                reviewManager.rateReview(req.getParameter("review"),user.getId(),!req.getParameter("rating").equals("dislike"),"data/bot/reviews/site");
            } catch (Exception e) {
                data.put("Error", "Unknown");
                resp.setStatus(500);
            }
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        data.put("token", auths.component2());
        String json = gson.toJson(data);
        writer.println(json);
    }

    public static void getReviews(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {
        Map<String, String> data = new HashMap<>();
        ArrayList<Object> reply = new ArrayList<>();
        ArrayList<Review> reviews = reviewManager.getReviews("data/bot/reviews/site", false);
        if (reviews == null) {
            writer.println("[]");
            return;
        }
        String userId = null;
        if (req.getParameter("token") != null) {
            Pair<User, String> auths = reAuthorize(req.getParameter("token"));
            if (auths.component1() != null) {
                userId = auths.component1().getId();
            }
            data.put("token", auths.component2());
            reply.add(data);
        }
        ArrayList<PublicReview> revs = new ArrayList<>();
        for (Review review : reviews) {
            revs.add(review.getPublic(userId));
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        reply.add(revs);
        writer.println(gson.toJson(reply));
    }

    public static void deleteReview(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {
        Pair<User, String> auths = reAuthorize(req.getParameter("token"));
        User user = auths.component1();
        Map<String, String> data = new HashMap<>();
        if(user == null){
            resp.setStatus(400);
            writer.println("Invalid token");
            return;
        }
        try {
            reviewManager.deleteReview(user.getId(), "data/bot/reviews/site");
        } catch (Exception e) {
            data.put("Error", "Unknown");
            resp.setStatus(500);
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        data.put("token", auths.component2());
        String json = gson.toJson(data);
        writer.println(json);
    }

    private static void saveReview(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {
        Pair<User, String> auths = reAuthorize(req.getParameter("token"));
        User user = auths.component1();
        Map<String, String> data = new HashMap<>();
        if(user == null){
            resp.setStatus(400);
            writer.println("Invalid token");
            return;
        }
        if (req.getParameter("rating") == null) {
            resp.setStatus(400);
            data.put("Error", "Missing rating");
        } else {
            try {

                reviewManager.createReview(user.getId(), req.getReader().lines().collect(Collectors.joining(System.lineSeparator())), Integer.parseInt(req.getParameter("rating")), "data/bot/reviews/site");
            } catch (Exception e) {
                data.put("Error", "Unknown");
                resp.setStatus(500);
            }
        }
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        data.put("token", auths.component2());
        String json = gson.toJson(data);
        writer.println(json);
    }

    private static void authorize(HttpServletRequest req, HttpServletResponse resp, PrintWriter writer) {

        String refreshToken = "";
        User user = null;
        try {
            Connection request = Jsoup.connect("https://discord.com/api/oauth2/token").data("client_id", dataManager.getData("data/bot/oAuth2/private", "clientID")).data("client_secret", dataManager.getData("data/bot/oAuth2/private", "clientSecret")).data("grant_type", "authorization_code").data("code", req.getParameter("oAuth2code")).data("redirect_uri", dataManager.getData("data/bot/oAuth2/private", "url")).data("scope", String.join(" ", "identify")).ignoreContentType(true);
            String response = request.post().body().text();
            Gson gson = (new GsonBuilder()).serializeNulls().enableComplexMapKeySerialization().create();
            TokensResponse tokens = ((TokensResponse) gson.fromJson(response, TokensResponse.class));
            DiscordAPI api = new DiscordAPI(tokens.getAccessToken());
            refreshToken = tokens.getRefreshToken();
            user = Main.jda.getUserById(api.fetchUser().getId());
        } catch (HttpStatusException e1) {
            resp.setStatus(400);
            writer.println("Invalid code");
            return;
        } catch (Exception e) {
            resp.setStatus(500);
            System.out.println(e);
            writer.println("Unknown error");
            return;
        }
        String name;
        try {
            name = user.getGlobalName();
        } catch (Exception ignored) {
            name = user.getId();
        }

        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        Map<String, String> data = new HashMap<>();
        data.put("userID", user.getId());
        data.put("token", refreshToken);
        data.put("username:", name);
        String json = gson.toJson(data);
        writer.println(json);
    }

    private static Pair<User, String> reAuthorize(String token) {
        String refreshToken = "";
        User user = null;
        try {
            Connection request = Jsoup.connect("https://discord.com/api/oauth2/token").data("client_id", dataManager.getData("data/bot/oAuth2/private", "clientID")).data("client_secret", dataManager.getData("data/bot/oAuth2/private", "clientSecret")).data("grant_type", "refresh_token").data("refresh_token", token).ignoreContentType(true);
            String response = request.post().body().text();
            Gson gson = (new GsonBuilder()).serializeNulls().enableComplexMapKeySerialization().create();

            TokensResponse tokens = ((TokensResponse) gson.fromJson(response, TokensResponse.class));
            DiscordAPI api = new DiscordAPI(tokens.getAccessToken());
            refreshToken = tokens.getRefreshToken();
            user = Main.jda.getUserById(api.fetchUser().getId());

        } catch (Exception ignore) {
        }

        return new Pair<>(user, refreshToken);
    }

}