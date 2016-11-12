package main;

import java.io.File;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import util.DatabaseManager;
import services.PredictionService;

import weka.classifiers.Evaluation;

public class MainServletContextListener implements ServletContextListener{

	@Override
    public void contextInitialized(ServletContextEvent arg0) {
		try{
			String dataFile = "C:/wifi_data/wifi_data.txt";
			String dataArff = "C:/wifi_data/export.arff";
	        //String dataFile = "wifi_data_university.txt";
	        File arquivo = new File(dataArff);
	        PredictionService predService = PredictionService.getInstance();
	        
	        if (!arquivo.isFile()){
	        	System.out.println("NÃO ENCONTROU*********************");
	        	DatabaseManager.createDatabase();
		        DatabaseManager.seedLocations(dataFile);
		        predService.generateTrainData();
	        }else{
	        	System.out.println("****ENCONTROU*********************");
	        }
	        Evaluation eval = predService.train();
	        System.out.println("================================================");
	        System.out.println(eval.toSummaryString("\nResults\n======\n", true));
	        System.out.println("================================================");
	        System.out.println(eval.toMatrixString("\nConfusion Matrix\n======\n"));
	        System.out.println("================================================");
		}catch(Exception e){
			e.printStackTrace();
		}
    }

    @Override
    public void contextDestroyed(ServletContextEvent arg0) {
    }
	
}
