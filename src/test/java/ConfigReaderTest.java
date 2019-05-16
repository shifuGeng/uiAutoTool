public class ConfigReaderTest {
    public static void main(String args[]) {
        ConfigReader configReader = ConfigReader.getInstance();
        String host = (String) configReader.getValueByKey("server", "host");
        System.out.println(host);
        System.out.println(System.getProperty("user.dir"));
    }
}
