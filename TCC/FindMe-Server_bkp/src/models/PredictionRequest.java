package models;

import java.util.HashMap;
import java.util.Map;

public class PredictionRequest {

    private Map<String, Float> accessPointsData;
    
    public PredictionRequest(){
        this.accessPointsData = new HashMap<>();
    }
    
    public void addAccessPoint(String bsid, Float signal){
        this.accessPointsData.put(bsid, signal);
    }
    
    public Map<String, Float> getAccessPointData(){
        return this.accessPointsData;
    }
}
