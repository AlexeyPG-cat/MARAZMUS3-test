package AlexeyPG.bots.M3;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class dataManager {

    public static void saveUserData(User user, String file, String key, String data){saveUserData(user.getId(),file,key,data);}
    public static void saveUserData(String user, String file, String key, String data){
        saveData("data/users/" + user + "/" + file,key,data);
    }

    public static String getUserData(User user, String file, String key){return getUserData(user.getId(),file,key);}
    public static String getUserData(String user, String file, String key){
        return getData("data/users/" + user + "/" + file,key);
    }

    public static void saveServerData(Guild server, String file, String key, String data){saveServerData(server.getId(),file,key,data);}
    public static void saveServerData(String server, String file, String key, String data){
        saveData("data/servers/" + server + "/" + file,key,data);
    }

    public static String getServerData(Guild server, String file, String key){return getServerData(server.getId(),file,key);}
    public static String getServerData(String server, String file, String key){
        return getData("data/servers/" + server + "/" + file,key);
    }

    public static void saveGlobalData(String file, String key, String data){
        saveData("data/global/" + file,key,data);
    }

    public static String getGlobalData(String file, String key){
        return getData("data/global/" + file,key);
    }

    public static void saveData(String file, String key, String data){
        File file_ = new File(file + ".txt");
        prepareFile(file_.toString());
        StringBuilder newData = new StringBuilder();
        try {
            ArrayList<String> lines = new ArrayList<>();
            Scanner fileScan = new Scanner(new FileReader(file_));
            while(fileScan.hasNextLine()){
                lines.add(fileScan.nextLine());
            }
            boolean isDone = false;
            for(String line : lines){
                String[] lineSplit = line.split(":");
                if(lineSplit[0].equals(key)){
                    lines.remove(line);
                    lines.add(key + ":" + data);
                    isDone = true;
                    break;
                }
            }
            if(!isDone) lines.add(key + ":" + data);
            boolean first = true;
            for(String line : lines){
                if(!first) newData.append("\n");
                first = false;
                newData.append(line);
            }
            fileScan.close();
        } catch (Exception e){
            return;
        }
        try {
            FileWriter dataWrite = new FileWriter(file_, false);
            if(key.isEmpty()){
                dataWrite.write(data);
            } else {
                dataWrite.write(newData.toString());
            }
            dataWrite.close();
        } catch (Exception e){
            return;
        }
    }

    public static String getData(String file, String key){
        if(!fileExists(file + ".txt")) return "";
        try {
            Scanner fileScan = new Scanner(new FileReader(file + ".txt"));
            if(key.isEmpty()){
                if(fileScan.hasNextLine()){
                    StringBuilder result = new StringBuilder(fileScan.nextLine());
                    while (fileScan.hasNextLine()){
                        result.append("\n").append(fileScan.nextLine());
                    }
                    fileScan.close();
                    return result.toString();
                }
                return "";
            } else {
                while (fileScan.hasNextLine()) {
                    String[] dataRead = fileScan.nextLine().split(":");
                    if (dataRead.length > 1) {
                        if (dataRead[0].equals(key)) {
                            fileScan.close();
                            String result = dataRead[1];
                            int skips = 0;
                            for (String data : dataRead) {
                                if (skips > 1) {
                                    result += ":" + data;
                                } else {
                                    skips++;
                                }
                            }
                            return result;
                        }
                    }
                }
            }
        } catch (Exception e){
            return e.toString();
        }
        return "";
    }

    public static List<String> getDataListed(String file){
        List<String> out = new ArrayList<>();
        if(!fileExists(file + ".txt")) return out;
        try {
            Scanner fileScan = new Scanner(new FileReader(file + ".txt"));
                if(fileScan.hasNextLine()){
                    while (fileScan.hasNextLine()){
                        out.add(fileScan.nextLine());
                    }
                    fileScan.close();
                }
                return out;
        } catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static void deleteData(String file, String key){
        File file_ = new File(file + ".txt");
        prepareFile(file_.toString());
        StringBuilder newData = new StringBuilder();
        try {
            ArrayList<String> lines = new ArrayList<>();
            Scanner fileScan = new Scanner(new FileReader(file_));
            while(fileScan.hasNextLine()){
                lines.add(fileScan.nextLine());
            }
            boolean isDone = false;
            for(String line : lines){
                String[] lineSplit = line.split(":");
                if(lineSplit[0].equals(key)){
                    lines.remove(line);
                    break;
                }
            }
            boolean first = true;
            for(String line : lines){
                if(!first) newData.append("\n");
                first = false;
                newData.append(line);
            }
            fileScan.close();
        } catch (Exception e){
            return;
        }
        try {
            FileWriter dataWrite = new FileWriter(file_, false);
            if(key.isEmpty()){
                file_.delete();
            } else {
                dataWrite.write(newData.toString());
            }
            dataWrite.close();
        } catch (Exception e){
            return;
        }
    }

    private static boolean fileExists(String file){
        File f = new File(file);
        return (f.exists() && !f.isDirectory());
    }

    private static void createFile(String file){
        try {
            File f = new File(file);
            f.getParentFile().mkdirs();
            f.createNewFile();
        } catch (Exception e){
            System.out.println("at creator");
            System.out.println(e);
        }
    }

    private static void prepareFile(String file){
        if(!fileExists(file)){
            createFile(file);
        }
    }
}
