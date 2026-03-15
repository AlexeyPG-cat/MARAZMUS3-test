package AlexeyPG.bots.M3;

import javax.xml.crypto.dsig.spec.XSLTTransformParameterSpec;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static AlexeyPG.bots.M3.dataManager.*;

public class publicAccess {

    public static class roles {
        private static Map<String,String> roleList = new HashMap<>();

        public static Map<String,String> getAll(){
            return roleList;
        }
        public static String getName(String id){
            return roleList.get(id);
        }
        public static String load(){
            int i = 0;
            roleList.clear();
            for(String role : getDataListed("data/bot/publicAccess/roles")){
                if(role.split(":").length>=2) {
                    roleList.put(role.split(":")[0], role.split(":")[1]);
                    i++;
                }
            }
            return i + " public roles loaded";
        }
        public static boolean isRoleIDPublic(String role){
            return roleList.containsKey(role);
        }
        public static void addRole(String id, String name){
            saveData("data/bot/publicAccess/roles",id,name);
            load();
        }
        public static void removeRole(String id){
            deleteData("data/bot/publicAccess/roles",id);
        }
        public static String getRolesFormatted(){
            StringBuilder sb = new StringBuilder();
            for(Map.Entry<String,String> entry : getAll().entrySet()){
                if(!sb.isEmpty()) sb.append("\n");
                sb.append(entry.getKey()).append(":").append(entry.getValue());
            }
            return sb.toString();
        }
    }
}
