package player.rongyun.com.playsdk.Utils;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取单例的Gson
 * Created by HeHu on 2017/4/7.
 */

public class GsonUtils {

    private static class LazyHolder {
        private static final Gson INSTANCE = new GsonBuilder()
                .registerTypeAdapterFactory(new NullStringToEmptyAdapterFactory())
                .create();
    }

    public static Gson getInstance() {
        return LazyHolder.INSTANCE;
    }

    /**
     * 读取本地json
     *
     * @param context
     * @param fileName
     * @return
     */
    public static String readLocalJson(Context context, String fileName) {
        String resultString = "";
        try {
            InputStream inputStream = context.getResources().getAssets().open(fileName);
            byte[] buffer = new byte[inputStream.available()];
            inputStream.read(buffer);
            resultString = new String(buffer, "UTF-8");
        } catch (Exception e) {
            // TODO: handle exception
        }
        return resultString;
    }


    /**
     * 将jsonObject转成T类型对象
     *
     * @param jsonObject
     * @param clazz
     * @return
     */
    public static <T> T jsonToBean(JsonObject jsonObject, Class<T> clazz) throws
            Exception {
        return getInstance().fromJson(jsonObject, clazz);
    }

    /**
     * 将jsonString转成T类型对象
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> T jsonToBean(String jsonString, Class<T> clazz) throws Exception {
        return getInstance().fromJson(jsonString, clazz);
    }


    /**
     * 将jsonArray转成T类型的数组
     *
     * @param jsonArray
     * @param clazz
     * @return
     */
    public static <T> List<T> jsonToList(JsonArray jsonArray, Class<T> clazz) throws
            Exception {
        List<T> list = new ArrayList<T>();
        for (final JsonElement elem : jsonArray) {
            list.add(getInstance().fromJson(elem, clazz));
        }
        return list;
    }

    /**
     * 将jsonString转成T类型的数组
     *
     * @param jsonString
     * @param clazz
     * @return
     */
    public static <T> List<T> jsonToList(String jsonString, Class<T> clazz) throws Exception {
        List<T> list = new ArrayList<T>();
        JsonArray jsonArray = new JsonParser().parse(jsonString).getAsJsonArray();
        for (final JsonElement elem : jsonArray) {
            list.add(getInstance().fromJson(elem, clazz));
        }
        return list;
    }

    public static class NullStringToEmptyAdapterFactory<T> implements TypeAdapterFactory {
        public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {

            Class<T> rawType = (Class<T>) type.getRawType();
            if (rawType != String.class) {
                return null;
            }
            return (TypeAdapter<T>) new StringAdapter();
        }
    }

    public static class StringAdapter extends TypeAdapter<String> {
        public String read(JsonReader reader) throws IOException {
            if (reader.peek() == JsonToken.NULL) {
                reader.nextNull();
                return "";
            }
            return reader.nextString();
        }

        public void write(JsonWriter writer, String value) throws IOException {
            if (value == null) {
                writer.nullValue();
                return;
            }
            writer.value(value);
        }
    }


}
