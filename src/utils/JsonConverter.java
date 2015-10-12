package utils;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.SortedMap;

import com.google.gson.reflect.TypeToken;

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

    public static SortedMap<Integer,Message> fromJsonToMap(String str){
        Type mapType = new TypeToken<SortedMap<Integer,Message>>() {}.getType();
        final Gson gson = new Gson();
        return gson.fromJson(str, mapType);
    }
}
