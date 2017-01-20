package hk.hku.cecid.piazza.commons.json;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonArrayBuilder;
import javax.json.JsonBuilderFactory;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.json.JsonReader;
import javax.json.JsonReaderFactory;
import javax.json.JsonValue;


public class JsonUtil {

    private static JsonBuilderFactory jsonFactory = Json.createBuilderFactory(null);

    private static void fill_key_value(JsonObjectBuilder jsonObject, String key, Object value) {
        if (jsonObject == null || key == null) {
            return;
        }

        if (value == null) {
            jsonObject.addNull(key);
        }
        else if (value instanceof String) {
            jsonObject.add(key, (String) value);
        }
        else if (value instanceof Integer) {
            jsonObject.add(key, ((Integer) value).intValue());
        }
        else if (value instanceof Long) {
            jsonObject.add(key, ((Long) value).longValue());
        }
        else if (value instanceof Boolean) {
            jsonObject.add(key, ((Boolean) value).booleanValue());
        }
        else if (value instanceof List) {
            fill_list(jsonObject, key, (List<Object>) value);
        }
        else if (value instanceof Map) {
            JsonObjectBuilder entryObj = jsonFactory.createObjectBuilder();
            fill_map(entryObj, (Map<String, Object>) value);
            jsonObject.add(key, entryObj);
        }
    }

    private static void fill_list(JsonObjectBuilder jsonObject, String key, List<Object> list) {
        if (jsonObject == null || key == null) {
            return;
        }
        JsonArrayBuilder arrayBuilder = jsonFactory.createArrayBuilder();
        for (Object entry : list) {
            if (entry instanceof Map) {
                JsonObjectBuilder entryObj = jsonFactory.createObjectBuilder();
                fill_map(entryObj, (Map<String, Object>) entry);
                arrayBuilder.add(entryObj);
            }
        }
        jsonObject.add(key, arrayBuilder);
    }

    private static void fill_map(JsonObjectBuilder jsonObject, Map<String, Object> map) {
        if (jsonObject == null) {
            return;
        }
        for (String key : map.keySet()) {
            Object value = map.get(key);
            fill_key_value(jsonObject, key, value);
        }
    }

    public static String fromDictionary(Map<String, Object> dictionary) {
        if (dictionary == null) {
            return null;
        }
        JsonObjectBuilder root = jsonFactory.createObjectBuilder();
        fill_map(root, dictionary);
        return root.build().toString();
    }

    private static void fill_dictionary(Map<String, Object> dictionary, JsonObject jsonObject) {
        if (dictionary == null || jsonObject == null) {
            return;
        }

        for (String key : jsonObject.keySet()) {
            JsonValue valueObj = (JsonValue) jsonObject.get(key);
            JsonValue.ValueType type = valueObj.getValueType();
            if (type == JsonValue.ValueType.ARRAY) {
                JsonArray arrayObj = jsonObject.getJsonArray(key);
                ArrayList<Object> array = new ArrayList<Object>();
                for (int i=0 ; i<arrayObj.size() ; i++) {
                    Map<String, Object> entryDictionary = new HashMap<String, Object>();
                    fill_dictionary(entryDictionary, arrayObj.getJsonObject(i));
                    array.add(entryDictionary);
                }
                dictionary.put(key, array);
            }
            else if (type == JsonValue.ValueType.OBJECT) {
                Map<String, Object> entryDictionary = new HashMap<String, Object>();
                fill_dictionary(entryDictionary, jsonObject.getJsonObject(key));
                dictionary.put(key, entryDictionary);
            }
            else if (type == JsonValue.ValueType.STRING) {
                dictionary.put(key, jsonObject.getString(key));
            }
            else if (type == JsonValue.ValueType.NUMBER) {
                dictionary.put(key, new Long(jsonObject.getJsonNumber(key).longValue()));
            }
            else if (type == JsonValue.ValueType.TRUE) {
                dictionary.put(key, Boolean.TRUE);
            }
            else if (type == JsonValue.ValueType.FALSE) {
                dictionary.put(key, Boolean.FALSE);
            }
        }
    }

    public static Map<String, Object> toDictionary(String source) throws JsonParseException {
        if (source == null) {
            return null;
        }

        JsonObject jsonObject = null;
        try {
            JsonReaderFactory factory = Json.createReaderFactory(null);
            JsonReader jsonReader = factory.createReader(new StringReader(source));
            jsonObject = jsonReader.readObject();
            jsonReader.close();
        } catch (Exception e) {
            throw new JsonParseException(e);
        }

        Map<String, Object> dictionary = new HashMap<String, Object>();
        fill_dictionary(dictionary, jsonObject);
        return dictionary;
    }
}
