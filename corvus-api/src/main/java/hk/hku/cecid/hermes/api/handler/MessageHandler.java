package hk.hku.cecid.hermes.api.handler;

import hk.hku.cecid.hermes.api.listener.HermesAbstractApiListener;


public class MessageHandler {
    protected HermesAbstractApiListener listener;

    public MessageHandler(HermesAbstractApiListener listener) {
        this.listener = listener;
    }
}
