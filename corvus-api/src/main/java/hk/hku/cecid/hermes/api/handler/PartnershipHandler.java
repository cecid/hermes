package hk.hku.cecid.hermes.api.handler;

import java.util.Map;

public interface PartnershipHandler {
    public Map<String, Object> getPartnerships();
    public Map<String, Object> addPartnership(Map<String, Object> inputDict);
    public Map<String, Object> removePartnership(String id);
}
