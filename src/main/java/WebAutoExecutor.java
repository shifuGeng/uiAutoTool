import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import com.google.gson.JsonObject;

public class WebAutoExecutor {

    public static void main(String[] args){
        String filePath = null;
        String url = null;
        String webDriverPath = null;
        Integer waitTime = 3;

        /**
         *  解析外部参数
         */
        try{
            if(args.length == 2){
                url = args[0];
                filePath = args[1];
            }

            if(args.length == 3){
                url = args[0];
                filePath = args[1];
                webDriverPath = args[2];
            }

            if(args.length == 4) {
                url = args[0];
                filePath = args[1];
                webDriverPath = args[2];
                waitTime = Integer.parseInt(args[3]);
            }

            if(args.length < 2 || args.length > 4){
                System.out.println("执行jar需要起始网页的URL和执行所需的excel模板数据");
                System.out.println("例子：java -jar ui-demo-1.0-SNAPSHOT.jar https://www.baidu.com XLS/XLSX_File_" +
                        "Absolute_Path WebDriver_Absolute_Path");
                return;
            }
        } catch (Exception e) {
            System.out.println("执行jar需要起始网页的URL和执行所需的excel模板数据");
            System.out.println("例子：java -jar ui-demo-1.0-SNAPSHOT.jar https://www.baidu.com XLS/XLSX_File_" +
                    "Absolute_Path WebDriver_Absolute_Path");
            return ;
        }

        /**
         *  读取excel文本的执行参数
         */
        List<List<String>> lists = new ArrayList<List<String>>();
        lists = ExcelReaderUtil.readExcel(filePath);
//        for (List<String> list: lists) {
//            for (String str: list) {
//                System.out.print(str + "\t");
//            }
//            System.out.print("\r\n");
//        }

        /**
         * read json file context and convert to json array
         */
        List<JsonObject> jsonObjectList = new ArrayList<>();
        try {
            String jsonFilePath = System.getProperty("user.dir") + "\\json_file.txt";
            File jsonFile = new File(jsonFilePath);
            if (jsonFile.exists()) {
                FileReader fr = new FileReader(jsonFilePath);
                BufferedReader br = new BufferedReader(fr);
                String str = br.readLine();
                while (str != null) {
                    jsonObjectList.add(JsonGenerator.str2Json(str));
                    str = br.readLine();
                }
            }
        } catch (Exception e) {
            System.out.println("剔除已成功数据录入结果的操作出错");
            e.printStackTrace();
        }

        /**
         * 读取已经完成的历史件
         */
        List<JsonObject> jsonObjectHisList = new ArrayList<>();
        try {
            String successFilePath = System.getProperty("user.dir") + "\\success_result.txt";
            File successFile = new File(successFilePath);
            if(successFile.exists()) {
                String failedFilePath = System.getProperty("user.dir") + "\\failed_result.txt";
                File failedFile = new File(failedFilePath);
                if(failedFile.exists()){
                    failedFile.delete();
                }
                FileReader fr = new FileReader(successFilePath);
                BufferedReader br = new BufferedReader(fr);
                String str = br.readLine();
                while (str != null) {
                    jsonObjectHisList.add(JsonGenerator.str2Json(str));
                    str = br.readLine();
                }
            }
        } catch (Exception e) {
            System.out.println("剔除已成功数据录入结果的操作出错");
            e.printStackTrace();
        }

        for(JsonObject jsonObject:jsonObjectHisList) {
            Iterator iter = jsonObjectList.iterator();
            while (iter.hasNext()) {
                JsonObject next = (JsonObject) iter.next();
                if (next.get("key3").getAsString().equals(jsonObject.get("key3").getAsString())){
                    iter.remove();
                }
            }
        }

        /**
         * 执行参数设置
         */
        WebAuto webAuto = (new WebAuto()).setWebDriverPath(webDriverPath, "firefox")
                .setURL(url)
                .setWaitTime(waitTime);

        webAuto.initWebDriver();

        /**
         * 读取配置文件
         */
        ConfigReader configReader = ConfigReader.getInstance();
        if ((boolean)configReader.getValueByKey("login", "needLogin")){
            String bank = (String) configReader.getValueByKey("login", "bank");
            String user =(String) configReader.getValueByKey("login", "user");
            String password = (String) configReader.getValueByKey("login", "password");
            String bank_element = (String) configReader.getValueByKey("login", "bank_element");
            String user_element = (String) configReader.getValueByKey("login", "user_element");
            String password_element = (String) configReader.getValueByKey("login", "password_element");
            String button_element = (String) configReader.getValueByKey("login", "button");
            String via = (String) configReader.getValueByKey("login", "via");
            String login_url = (String) configReader.getValueByKey("webDriver","url");
            webAuto.login(bank, user, password, bank_element, user_element, password_element,
                    button_element, via, login_url);
        }


        /** 操作列表定义
         * operList 操作列表
         * viaList 控件获取方式列表
         * elementList 控件列表
         * mappingList 映射关系列表
         */
        List<String> actList = lists.get(0);
        List<String> viaList= lists.get(1);
        List<String> valueList = lists.get(2);
        List<String> keyList = lists.get(3);

        if(null != jsonObjectList) {
            for(JsonObject jsonObject:jsonObjectList) {
                webAuto.executeFlow(actList, viaList, valueList, keyList, jsonObject);
            }
        }
    }
}
