import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public class ConfigReader {

    private static Map<String, Map<String, Object>> properties;

    private ConfigReader() {
        if (SingletonHolder.instance != null) {
            throw new IllegalStateException();
        }
    }

    /**
     * use static inner class  achieve singleton
     */
    private static class SingletonHolder {
        private static ConfigReader instance = new ConfigReader();
    }

    public static ConfigReader getInstance() {
        return SingletonHolder.instance;
    }

    /**
     * init property when class is loaded
     */
    static {
        FileInputStream in = null;
        try {
            properties = new HashMap<>();
            Yaml yaml = new Yaml();
            File file = new File(System.getProperty("user.dir") + "\\application.yaml");
            in = new FileInputStream(file);
            properties = yaml.loadAs(in, HashMap.class);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * get yaml property
     * @param root
     * @param key
     * @return
     */
    public Object getValueByKey(String root, String key) {
        Map<String, Object> rootProperty = properties.get(root);
        return rootProperty.getOrDefault(key, "");
    }
}