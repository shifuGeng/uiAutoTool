import com.google.gson.JsonObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    public static List<JsonObject> GetApprovalContent(String filePath, String keyListStr) {
        List<JsonObject> jsonObjectList = new ArrayList<>();
        String keys="APP_BANK,APP_SEQ";
        try {
            String[] sa1 = keys.split(",");
            String jsonFilePath = System.getProperty("user.dir") + "\\RS-CCCB-APPROVAL-20190510-01.txt";
            File jsonFile = new File(jsonFilePath);
            if (jsonFile.exists()) {
                FileReader fr = new FileReader(jsonFilePath);
                BufferedReader br = new BufferedReader(fr);
                String str = null;
                while ((str = br.readLine()) != null) {
                    JsonObject j1 = JsonGenerator.str2Json(str);
                    JsonObject j2 = new JsonObject();
                    for(String key :sa1){
                        j2.add(key,j1.get(key));
                    }
                    jsonObjectList.add(j2);
                }
            }

            System.out.println( jsonObjectList);
        } catch (Exception e) {
            System.out.println("");
            e.printStackTrace();
        }
        return jsonObjectList;
    }
}

