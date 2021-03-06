package services;

import models.AccessPoint;
import models.LocationData;
import models.PredictionRequest;
import models.Room;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Evaluation;
import weka.classifiers.functions.LibSVM;
import weka.core.Instances;
import weka.core.SelectedTag;
import weka.core.converters.ConverterUtils.DataSource;

public class PredictionService {
    private static PredictionService instance;
    private LibSVM classifier;
    
    public static PredictionService getInstance(){
        if(instance == null){
            instance = new PredictionService();
        }
        
        return instance;
    }
    
    private PredictionService(){
        
    }
    
    public String generateTestData(PredictionRequest req){
        try{
            StringBuilder sb = new StringBuilder();
            List<AccessPoint> aps = LocationService.getInstance().getAccessPoints();
            sb.append("@RELATION wifi\n");
            sb.append("\n");
            for(AccessPoint ap : aps){
                sb.append("@ATTRIBUTE ap" + ap.getId() + " REAL\n");
            }
            List<Room> rooms = RoomService.getInstance().getAll();
            sb.append("@ATTRIBUTE class {");
            for(int i=0;i<rooms.size();i++){
                Room room = rooms.get(i);
                sb.append(room.getId());
                if(i != rooms.size()-1)
                    sb.append(",");
            }
            sb.append(",none");
            sb.append("}\n");
            sb.append("\n");
            sb.append("@DATA\n");
            
            
            
            for (int c = 0; c < aps.size(); c++) {
                AccessPoint ap = aps.get(c);
                Map<String, Float> coletaData = req.getAccessPointData();
                if (coletaData.containsKey(ap.getBssid())) {
                    sb.append(coletaData.get(ap.getBssid()));
                } else {
                    sb.append("0");
                }
                if (c != aps.size() - 1) {
                    sb.append(",");
                } else {
                    sb.append(",none");
                    sb.append("\n");
                }
            }
            
            return sb.toString();
            
        }catch(Exception ex){
            Logger.getLogger(PredictionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public String generateTrainData(){
        try{
            StringBuilder sb = new StringBuilder();
            File arff = new File("C:/wifi_data/export.arff");
            if (arff.isFile()){
            	BufferedReader reader = new BufferedReader(new FileReader ("C:/wifi_data/export.arff"));
            	String line = "";
            	while((line = reader.readLine()) != null) {
                    sb.append(line);
                    sb.append("\n");
                }
            	reader.close();
            }else{
            	List<AccessPoint> aps = LocationService.getInstance().getAccessPoints();
                sb.append("@RELATION wifi\n");
                sb.append("\n");
                for(AccessPoint ap : aps){
                    sb.append("@ATTRIBUTE ap" + ap.getId() + " REAL\n");
                }
                List<Room> rooms = RoomService.getInstance().getAll();
                sb.append("@ATTRIBUTE class {");
                for(int i=0;i<rooms.size();i++){
                    Room room = rooms.get(i);
                    sb.append(room.getId());
                    if(i != rooms.size()-1)
                        sb.append(",");
                }
                sb.append(",none");
                sb.append("}\n");
                sb.append("\n");
                sb.append("@DATA\n");
                List<LocationData> locData = LocationService.getInstance().getAllLocationData();

                HashMap<String, HashMap<Long, Float>> data = new HashMap<String, HashMap<Long,Float>>();
                
                for(LocationData loc : locData){
                    String uuid = loc.getUuid();
                    System.out.println("locData: " + loc.getUuid());
                    for(AccessPoint ap : aps){
                        if(loc.getAccessPoint().getId().equals(ap.getId())){
                            if(!data.containsKey(uuid)){
                                data.put(uuid, new HashMap<Long, Float>());    
                            }
                            data.get(uuid).put(ap.getId(), loc.getSignalIntesity());
                        }
                        
                    }
                }
                
                System.out.println("Acabou for LocationData");
                
                for(String key : data.keySet()){
                	for(int c=0;c<aps.size();c++){
                        AccessPoint ap = aps.get(c);
                        Map<Long, Float> coletaData = data.get(key);
                        if(coletaData.containsKey(ap.getId())){
                            sb.append(coletaData.get(ap.getId()));
                        }else{
                            sb.append("0");
                        }
                        if(c!=aps.size()-1){
                            sb.append(",");
                        }else{
                            String category = LocationService.getInstance().getLocationDataByUUID(key).getRoom().getId().toString();
                            sb.append("," + category);
                            sb.append("\n");
                        }
                    }
                }
                File file = new File("C:/wifi_data/export.arff");
                BufferedWriter writer = null;
                writer = new BufferedWriter(new FileWriter(file));
                writer.write(sb.toString());
                writer.close();
            }
            
            System.out.println("Acabou for data.keySet");
            
            
            return sb.toString();
            
        }catch(Exception ex){
            Logger.getLogger(PredictionService.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        return null;
    }
    
    public Evaluation train(){
        try{
            String arffData = this.generateTrainData();
            InputStream stream = new ByteArrayInputStream(arffData.getBytes(StandardCharsets.UTF_8));
            DataSource source = new DataSource(stream);
            Instances data = source.getDataSet();
            data.setClassIndex(data.numAttributes() - 1);
            this.classifier = new LibSVM();
            this.classifier.setKernelType(new SelectedTag(LibSVM.KERNELTYPE_POLYNOMIAL, LibSVM.TAGS_KERNELTYPE));
            this.classifier.setSVMType(new SelectedTag(LibSVM.SVMTYPE_C_SVC, LibSVM.TAGS_SVMTYPE));
            
            Evaluation eval = new Evaluation(data);
            eval.crossValidateModel(this.classifier, data, 10, new Random(1));
            
            this.classifier.buildClassifier(data);
            return eval;
        }catch(Exception ex){
            Logger.getLogger(PredictionService.class.getName()).log(Level.SEVERE, null, ex);
        }
 
        return null;
    }
    
    public Room predict(PredictionRequest request){
        try{

            String arffData = this.generateTestData(request);
            StringReader reader = new StringReader(arffData);
            Instances unlabeled = new Instances(reader);
            System.out.println("Test data size: " + unlabeled.size());
            unlabeled.setClassIndex(unlabeled.numAttributes() - 1);
            Instances labeled = new Instances(unlabeled);
            Double clsLabel = this.classifier.classifyInstance(unlabeled.get(0));
            labeled.instance(0).setClassValue(clsLabel);
            String roomIdString = unlabeled.classAttribute().value(clsLabel.intValue());
            
            Long roomId = Long.parseLong(roomIdString);
            Room predictedRoom = RoomService.getInstance().getById(roomId);
            System.out.println(clsLabel + " -> " + roomIdString + " -> " + predictedRoom.getName());
            return predictedRoom;
            
            
        }catch(Exception ex){
           Logger.getLogger(PredictionService.class.getName()).log(Level.SEVERE, null, ex); 
        }
        return null;
    }    
}
