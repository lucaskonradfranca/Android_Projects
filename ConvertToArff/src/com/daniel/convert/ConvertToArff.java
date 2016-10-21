package com.daniel.convert;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static spark.Spark.*;

import spark.*;

import weka.classifiers.bayes.BayesNet;
import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

public class ConvertToArff {

	/**
	 * @param args
	 * @throws IOException
	 */

	double powerSum1 = 0;
	double powerSum2 = 0;
	double stdev = 0;
	static String relation = "wifis";

	static String[] parts;

	// nome das salas identifacadas, sem repetir
	static List<String> nomeSalas = new ArrayList<String>();
	// nome dos APs identifacados, sem repetir
	static List<String> nomeAp = new ArrayList<String>();
	// chave primária,
	static List<String> key = new ArrayList<String>();
	// códigos das contagems
	static List<String> contagem = new ArrayList<String>();
	
	static List<SequenciasContagem> seqContagem = new ArrayList<SequenciasContagem>();

	static List<Sala> salasCont = new ArrayList<Sala>();
	
	// lista de access points
	static List<AccessPoint> AccessPoints = new ArrayList<AccessPoint>();
	// lista de rssi que será normalizado
	static List<AccessPoint> tempAccessPoints = new ArrayList<AccessPoint>();

	public static void main(String arg[]) {
		//File path = new File("C:\\Users\\DanielArthur\\Desktop\\WifiSignal\\");
		File path = new File("C:\\Users\\Lucas\\Desktop\\WifiSignal\\");
		if (path.exists()) {
			try {
				String[] args = new String[1];
				args[0] = "C:\\Users\\Lucas\\Desktop\\WiFiSignal\\saida.arff";
				listFilesForFolder(path);
				geraArff(path);
				try {
					BayesNet BayesNet = IncrementalClassifier.treinar(args);
					classificar(BayesNet);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			System.out.println("Pasta informada não existe!\n" + path);
		}

		// System.out.println(rssi);
	}

	/**
	 * Cria arrays com mac da rede, sinal, sala, e identificador único da
	 * contagem
	 * 
	 * @param file
	 *            um arquivo com as informações do sinal
	 */
	public static void loadData(File file) throws IOException {
		String line;
		String uuid = UUID.randomUUID().toString();

		BufferedReader br = new BufferedReader(new FileReader(file));

		tempAccessPoints.clear();
		boolean achou = false;
		while ((line = br.readLine()) != null) {
			AccessPoint AccessPoint = new AccessPoint();

			parts = line.split("\\x7C"); // \\x7C = | em ASCII

			if(parts[0].isEmpty() ||
					parts[1].isEmpty() ||
					parts[2].isEmpty() ||
					parts[3].isEmpty() ||
					parts[4].isEmpty() ||
					parts[5].isEmpty()){
				continue;
			}
			
			AccessPoint.setBssid(parts[0]);
			AccessPoint.setSsid(parts[1]);
			AccessPoint.setRssi(parts[2]);
			AccessPoint.setSala(parts[3]);
			AccessPoint.setContagem(parts[4]);
			AccessPoint.setSequencia(Integer.parseInt(parts[5]));
			AccessPoint.setGerou(false);

			tempAccessPoints.add(AccessPoint);

			achou = false;
			for(int i = 0; i < seqContagem.size(); i++){
				if (seqContagem.get(i).getContagem().equals(parts[4])){
					achou = true;
					seqContagem.get(i).addSequencia(Integer.parseInt(parts[5]));
				}
			}
			
			if (!achou){
				SequenciasContagem seqCont = new SequenciasContagem();
				seqCont.setContagem(parts[4]);
				seqCont.addSequencia(Integer.parseInt(parts[5]));
				seqContagem.add(seqCont);
			}
			
			if (!contagem.contains(parts[4])) {
				contagem.add(parts[4]);
			}
			if (!nomeSalas.contains(parts[3])) {
				nomeSalas.add(parts[3]);
			}
			if (!nomeAp.contains(parts[1])) {
				nomeAp.add(parts[1]);
			}
		}

		// quantContagem.add(contagem);
		AccessPoints.addAll(normalize(tempAccessPoints));

		br.close();
	}

	/**
	 * Geral: Mostra os arquivos da pasta informada
	 * 
	 * @param folder
	 *            pasta onde estão os arquivos
	 */
	public static void listFilesForFolder(final File folder) throws IOException {
		for (final File fileEntry : folder.listFiles()) {
			if (fileEntry.isDirectory()) {
				listFilesForFolder(fileEntry);
			} else {
				// cont += 1;
				loadData(fileEntry);
			}
		}
	}

	public static List<AccessPoint> normalize(List<AccessPoint> lista)
			throws IOException {

		/*
		 * x = (x - y) / a x = intensidade do sinal, y(mi) = média dos sinais, a
		 * = desvio padrão
		 */
		List<AccessPoint> listaRetorno = new ArrayList<AccessPoint>();

		double average = 0, finalsum = 0;

		for (int i = 0; i < lista.size(); i++) {
			finalsum += Double.parseDouble(lista.get(i).getRssi());
		}

		average = finalsum / (lista.size());

		double std = StandardDeviationCalc.stdDev(lista);

		// System.out.println(std);

		for (int i = 0; i < lista.size(); i++) {
			AccessPoint ap = new AccessPoint();

			ap.setBssid(lista.get(i).getBssid());
			ap.setSsid(lista.get(i).getSsid());
			ap.setRssi(String.valueOf(((Double.parseDouble(lista.get(i)
					.getRssi()) - average) / std)));
			ap.setSala(lista.get(i).getSala());
			ap.setContagem(lista.get(i).getContagem());
			ap.setSequencia(lista.get(i).getSequencia());

			listaRetorno.add(ap);
		}
		// System.out.println(listaRetorno);

		return listaRetorno;
	}

	public static void geraArff(File path) {
		Writer writer = null;
		String sinal = "";
		File file = new File("C:\\Users\\Lucas\\Desktop\\WifiSignal\\saida.arff");

		try {
			if (!file.exists()) {
				file.createNewFile();
			} else {
				file.delete();
				file.createNewFile();
			}

			writer = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(file), "utf-8"));

			writer.write("%Titulo:\n%Fontes:\n%Informações úteis:\n\n@RELATION "
					+ relation + "\n\n");

			for (int i = 0; i < nomeAp.size(); i++) {
				writer.write("@ATTRIBUTE \"" + nomeAp.get(i) + "\" REAL\n");
			}

			writer.write("@ATTRIBUTE class {");
			for (int i = 0; i < nomeSalas.size(); i++) {
				if (i + 1 == nomeSalas.size()) {
					writer.write(nomeSalas.get(i) + "}\n\n");
				} else {
					writer.write(nomeSalas.get(i) + ",");
				}
			}

			writer.write("@DATA\n");
			
			/*for (int i = 0; i < nomeSalas.size(); i++) {
				for (int x=0; x < salasCont.size(); x++){
					if(salasCont.get(x).getNome().equals(nomeSalas.get(i))){
						for(int j = 0; j < salasCont.get(x).getQuantidade(); j++){
							for (int k = 0; k < nomeAp.size(); k++) {
								for (int m = 0; m < AccessPoints.size(); m++) {
									if (AccessPoints.get(m).getSsid()
											.equals(nomeAp.get(k))
											&& AccessPoints.get(m).getSala()
											.equals(nomeSalas.get(i))
											&& ! AccessPoints.get(m).isGerou()) {
										sinal = AccessPoints.get(m).getRssi() + ",";
										AccessPoints.get(m).setGerou(true);
										break;
									}
								}
								if (sinal != "") {
									writer.write(sinal);
									sinal = "";
								} else {
									writer.write("0,");
									sinal = "";
								}
							}
							writer.write(nomeSalas.get(i) + "\n");
						}
					}
				}
			}*/
			
			
			//AQUI TA MAIS OU MENOS FUNCIONANDO
			//for (int i = 0; i < nomeSalas.size(); i++) {
			//	for (int j = 0; j < contagem.size(); j++) { // contagem
			//		for (int k = 0; k < nomeAp.size(); k++) {
			//			for (int m = 0; m < AccessPoints.size(); m++) {
			//				if (AccessPoints.get(m).getSsid()
			//						.equals(nomeAp.get(k))
			//						&& AccessPoints.get(m).getSala()
			//						.equals(nomeSalas.get(i))
			//						&& AccessPoints.get(m).getContagem().equals(contagem.get(j))
			//						&& AccessPoints.get(m).isGerou() == false) {
			//					sinal = AccessPoints.get(m).getRssi() + ",";
			//					AccessPoints.get(m).setGerou(true);
			//					break;
			//				}
			//			}
			//			if (sinal != "") {
			//				writer.write(sinal);
			//				sinal = "";
			//			} else {
			//				writer.write("0,");
			//				sinal = "";
			//			}
			//		}
			//		writer.write(nomeSalas.get(i) + "\n");
			//	}
			//}
			
		
			
			for(int i = 0; i < nomeSalas.size(); i++){
				for(int j = 0; j < contagem.size(); j++){
					for(int k = 0; k < seqContagem.size(); k++){
						if(seqContagem.get(k).getContagem().equals(contagem.get(j))){
							for(int l = 0; l < seqContagem.get(k).getSequencias().size(); l++){
								
							}
						}
					}
				}
			}
			
			
			
			
					
/*
			for (int i = 0; i < nomeSalas.size(); i++) {
				for (int j = 1; j <= 5; j++) { // contagem
					for (int k = 0; k < nomeAp.size(); k++) {
						for (int m = 0; m < AccessPoints.size(); m++) {
							if (AccessPoints.get(m).getSsid()
									.equals(nomeAp.get(k))
									&& AccessPoints.get(m).getSala()
									.equals(nomeSalas.get(i))
									/*&& Integer.parseInt(AccessPoints.get(m)
											.getContagem()) == j) {
								sinal = AccessPoints.get(m).getRssi() + ",";
								break;
							}
						}
						if (sinal != "") {
							writer.write(sinal);
							sinal = "";
						} else {
							writer.write("0,");
							sinal = "";
						}

					}
					writer.write(nomeSalas.get(i) + "\n");
				}
			}*/

		} catch (IOException ex) {
			// report

		} finally {
			try {
				writer.close();
			} catch (Exception ex) {
			}
		}
	}
	
	public static void classificar(BayesNet BayesNet) throws IOException{
		// testa com os mesmos valores do treinamento
		// Usando um ARFF
		Instance current2;

		ArffLoader loader2 = new ArffLoader();
		loader2.setFile(new File(
				"C:\\Users\\Lucas\\Desktop\\WifiSignal\\saida.arff"));
		Instances structure2 = loader2.getStructure();
		structure2.setClassIndex(structure2.numAttributes() - 1);

		double pred = 0;
		
		while ((current2 = loader2.getNextInstance(structure2)) != null) {
			try {
				pred = BayesNet.classifyInstance(current2);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Class predicted: "
					+ current2.classAttribute().value((int) pred));
		}
		
		/*
		// recebendo texto
		String line;
		String sinal = "";
		List<Double> leitura = new ArrayList<>();
		
		BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\DanielArthur\\Desktop\\WifiSignal\\B1011.txt"));

		AccessPoints.clear();
		tempAccessPoints.clear();
		
		while ((line = br.readLine()) != null) {
			AccessPoint AccessPoint = new AccessPoint();

			parts = line.split("\\x7C"); // \\x7C = | em ASCII

			AccessPoint.setBssid(parts[0]);
			AccessPoint.setSsid(parts[1]);
			AccessPoint.setRssi(parts[2]);
			AccessPoint.setSala(parts[3]);
			AccessPoint.setContagem(parts[4]);
			
			tempAccessPoints.add(AccessPoint);
		}
		
		AccessPoints = normalize(tempAccessPoints);
		br.close();
		
		for (int k = 0; k < nomeAp.size(); k++) {
			for (int m = 0; m < AccessPoints.size(); m++) {
				if (AccessPoints.get(m).getSsid().equals(nomeAp.get(k))) {
					sinal = AccessPoints.get(m).getRssi();
					break;
				}
			}
			if (sinal != "") {
				leitura.add(Double.parseDouble(sinal));
				sinal = "";
			} else {
				leitura.add(0.0);
				sinal = "";
			}
		}
		
		FastVector fvNominalVal = new FastVector(nomeAp.size());
		
		for (int i = 0; i < nomeAp.size(); i++) {
			fvNominalVal.addElement(nomeAp.get(i));
		}
		
		Attribute attribute1 = new Attribute("class", fvNominalVal);
		
		fvNominalVal.removeAllElements();
		
		for (int i = 0; i < leitura.size(); i++) {
			fvNominalVal.addElement(leitura.get(i).toString());
		}
		
		Attribute attribute2 = new Attribute("REAL", fvNominalVal);
		// Create list of instances with one element
		FastVector fvWekaAttributes = new FastVector(nomeAp.size());
		fvWekaAttributes.addElement(attribute1);
		fvWekaAttributes.addElement(attribute2);
		
		Instances instances = new Instances("Test relation", fvWekaAttributes, 1);
		// Set class index
		instances.setClassIndex(0);
		// Create and add the instance
		//DenseInstance instance = new DenseInstance(2);
		//instance.setValue(attribute2, text);
		
		Instance instance = new Instance(nomeAp.size());
		
		for (int i = 0; i < leitura.size(); i++) {
			//System.out.println(leitura.get(i));
			//instance.setValue(attribute2, (leitura.get(i)));
		}
		instances.add(instance);
		
		double pred = 0;
		try {
			pred = BayesNet.classifyInstance(instances.instance(0));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Class predicted: "
				+ instances.instance(0).classAttribute().value((int) pred));
				*/
	}

}