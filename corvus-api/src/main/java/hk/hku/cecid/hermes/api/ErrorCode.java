package hk.hku.cecid.hermes.api;


public class ErrorCode {
    public static final int ERROR_UNKNOWN = 10000;
    public static final int ERROR_MISSING_REQUIRED_PARAMETER = 10001;
    public static final int ERROR_PROTOCOL_UNSUPPORTED = 10002;
    public static final int ERROR_READING_DATABASE = 10003;
    public static final int ERROR_WRITING_DATABASE = 10004;
    public static final int ERROR_READING_REQUEST = 10005;
    public static final int ERROR_PARSING_REQUEST = 10006;
    public static final int ERROR_RECORD_ALREADY_EXIST = 10007;
    public static final int ERROR_DATA_NOT_FOUND = 10008;
    public static final int ERROR_WRITING_MESSAGE = 10009;
    public static final int ERROR_SENDING_MESSAGE = 10010;
    public static final int ERROR_EXTRACTING_PAYLOAD_FROM_MESSAGE = 10011;
    public static final int ERROR_UNKNOWN_ACTION = 10012;
}
