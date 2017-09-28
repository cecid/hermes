package hk.hku.cecid.hermes.api.handler;

import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;


public class MessageHandler {
    public static int MAX_NUMBER = 2147483647;
    protected HermesAbstractApiListener listener;

    public MessageHandler(HermesAbstractApiListener listener) {
        this.listener = listener;
    }
}
