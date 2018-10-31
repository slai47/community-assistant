package com.clanassist.backend;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Harrison on 5/13/14.
 */
public class ParserHelper {


    public static final int MILLIS = 1000;
    private static final String ERROR = "error";
    private static final String CODE = "code";
    private static final String MESSAGE = "message";
    public static final String DATA = "data";

    public static com.utilities.search.Error getError(JSONObject result) {
        com.utilities.search.Error e = null;
        JSONObject error = result.optJSONObject(ERROR);
        if (error != null) {
            String message = error.optString(MESSAGE);
            int code = error.optInt(CODE);
            e = new com.utilities.search.Error();
            e.setCode(code);
            e.setMessage(message);
        }
        return e;
    }

    public static Map<String, Double> createClassTypeMap() {
        Map<String, Double> map = new HashMap<String, Double>();
        map.put("lightTank", 0.0);
        map.put("mediumTank", 0.0);
        map.put("heavyTank", 0.0);
        map.put("AT-SPG", 0.0);
        map.put("SPG", 0.0);
        return map;
    }

    public static Map<Integer, Double> createTierMap() {
        Map<Integer, Double> map = new HashMap<Integer, Double>();
        map.put(1, 0.0);
        map.put(2, 0.0);
        map.put(3, 0.0);
        map.put(4, 0.0);
        map.put(5, 0.0);
        map.put(6, 0.0);
        map.put(7, 0.0);
        map.put(8, 0.0);
        map.put(9, 0.0);
        map.put(10, 0.0);
        return map;
    }

    public static Map<String, Integer> createClassTypeIntMap() {
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("lightTank", 0);
        map.put("mediumTank", 0);
        map.put("heavyTank", 0);
        map.put("AT-SPG", 0);
        map.put("SPG", 0);
        return map;
    }

    public static Map<Integer, Integer> createTierIntMap() {
        Map<Integer, Integer> map = new HashMap<Integer, Integer>();
        map.put(1, 0);
        map.put(2, 0);
        map.put(3, 0);
        map.put(4, 0);
        map.put(5, 0);
        map.put(6, 0);
        map.put(7, 0);
        map.put(8, 0);
        map.put(9, 0);
        map.put(10, 0);
        return map;
    }

    public static void averageIntMap(Map<Integer, Double> map, Map<Integer, Integer> battles) {
        for (Integer str : map.keySet()) {
            Double x = map.get(str);
            Integer b = battles.get(str);
            if (b > 0)
                x = x / b;
            map.put(str, x);
        }
    }

    public static void averageStrMap(Map<String, Double> map, Map<String, Integer> battles) {
        for (String str : map.keySet()) {
            Double x = map.get(str);
            Integer b = battles.get(str);
            if (b > 0)
                x = x / b;
            map.put(str, x);
        }
    }

    public static void averageDoubleMap(Map<Integer, Double> map, Map<Integer, Double> battles) {
        for (Integer str : map.keySet()) {
            Double x = map.get(str);
            Double b = battles.get(str);
            if (b > 0)
                x = x / b;
            map.put(str, x);
        }
    }

    public static void averageStrDoubleMap(Map<String, Double> map, Map<String, Double> battles) {
        for (String str : map.keySet()) {
            Double x = map.get(str);
            Double b = battles.get(str);
            if (b > 0)
                x = x / b;
            map.put(str, x);
        }
    }
}
