import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class JsonGenerator {
    public static JsonObject str2Json(String target) {
        JsonObject jsonObject = new JsonParser().parse(target).getAsJsonObject();
        return jsonObject;
    }
}
