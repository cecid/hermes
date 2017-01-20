package hk.hku.cecid.piazza.commons.json;


public class JsonParseException extends Exception {
    public JsonParseException(String message) {
        super(message);
    }

    public JsonParseException(Exception exception) {
        super(exception);
    }
}
