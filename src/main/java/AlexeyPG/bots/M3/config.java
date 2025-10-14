package AlexeyPG.bots.M3;

import net.dv8tion.jda.api.entities.Guild;

import java.io.FileReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;


//This is not needed anymore, I'll replace it with dataManager soon
public class config {
    static HashMap<String,String> configs = new HashMap<>();
    static HashMap<String,HashMap<String,String>> serverConfigs = new HashMap<>();
    static HashMap<String, List<String>> serverAdmins = new HashMap<>();

    public static void loadAdminLists(){
        List<Guild> guilds = Main.jda.getGuilds();
        for(Guild guild : guilds){
            try{
                String[] admins = Files.readString(Paths.get("data/servers/" + guild.getId() + "/adminRoles.txt")).split(",");
                List<String> adminsList = new ArrayList<>(List.of(admins));
                serverAdmins.put(guild.getId(),adminsList);
                System.out.println("Admin lists loaded");
            } catch (Exception e){
                System.out.println("Error loading admin lists");
            }
        }
    }
    public static String addAdmin(Guild guild,String admin){
        try{
            if(Files.readString(Paths.get("data/servers/" + guild.getId() + "/adminRoles.txt")).contains(admin)){
                return "ALREADY ADMIN";
            }
        } catch (Exception e){System.out.println("AdminsCheckFail");}
        try{
            Files.writeString(Paths.get("data/servers/" + guild.getId() + "/adminRoles.txt"),","+admin,StandardOpenOption.CREATE,StandardOpenOption.APPEND);
            loadAdminLists();
            return "OK";
        } catch (Exception e){
            //System.out.println(e.getMessage());
            return "FAIL";
        }
    }


    public static String loadConfig(){
        String output = "";
        configs.clear();
        try {
            int i = 0;
            Scanner fileScan = new Scanner(new FileReader("config.txt"));
            while(fileScan.hasNextLine()){
                String[] data = fileScan.nextLine().split(":");
                if(data.length>1) {
                    configs.put(data[0],data[1]); i++;
                }
            }
            output += "\nLoaded " + i + " config entries";
        } catch (Exception e){
            output += "\nCan't load config file! Generating new one...";
            //System.out.println(e.getMessage());
            try {
                Files.createFile(Paths.get("config.txt"));
            } catch (Exception e2){
                output += "\nCan't generate config file!";
            }
        }
        System.out.println(output);
        return output;
    }


    public static String get(String configName){
        if(configs.get(configName)==null) return "notFound";
        return configs.get(configName);
    }

    public static void addMissingConfigs(){
        loadConfig();
        for(Map.Entry<String,String> entry : getDefaultConfigValues().entrySet()){
                if(get(entry.getKey()).equals("notFound")) {
                    System.out.println("Adding new config: " + entry.getKey());
                    try{
                        Files.writeString(Paths.get("config.txt"),entry.getKey() + ":" + entry.getValue() + "\n", StandardOpenOption.APPEND);
                    } catch (Exception e){
                        System.out.println("Can't add [" + entry.getKey() + "] to config");
                    }
                }
        }
        loadConfig();
    }

    public static HashMap<String,String> getDefaultConfigValues(){
        HashMap<String,String> defaultValues = new HashMap<>() ;

        defaultValues.put("Token","BOT TOKEN THERE");
        defaultValues.put("Welcome channel", "0");
        defaultValues.put("Player roles","");
        defaultValues.put("Game message channel", "937299063576092742");

        return defaultValues;
    }
}
