package main;

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
	        //String dataFile = "wifi_data_university.txt";
	        
	        DatabaseManager.createDatabase();
	        DatabaseManager.seedLocations(dataFile);
	        PredictionService predService = PredictionService.getInstance();
	        predService.generateTrainData();
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
