package utils;

import com.google.gson.Gson;

/**
 * Created on 24.09.2015.
 */
public class JsonConverter {

    public static String toJson(Object model){
        final Gson gson = new Gson();
        return gson.toJson(model);
    }

    public static Object fromJson(String str, Class T){
        final Gson gson = new Gson();
        return gson.fromJson(str, T);
    }
}
