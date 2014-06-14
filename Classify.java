package cs475;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

public class Classify {
	static public LinkedList<Option> options = new LinkedList<Option>();
	static int gd_iterations = 20;
	static double gd_eta = .01;
	static int num_features = 0;
	static double online_learning_rate = 1.0;
	static int online_training_iterations = 1;
	static double polynomial_kernel_exponent =2;
	static double cluster_lambda = 0.0;
	static int clustering_training_iterations = 10;
	static int num_clusters = 3;
	
	public static void main(String[] args) throws IOException {
		// Parse the command line.
		long startTime=System.currentTimeMillis();
		String[] manditory_args = { "mode"};
		createCommandLineOptions();
		CommandLineUtilities.initCommandLineParameters(args, Classify.options, manditory_args);
		
//		System.out.println("CLASSPATH: "+System.getProperty("java.class.path"));//prints out null
//	    System.out.println("PATH: "+System.getenv("PATH"));//prints out null
		if (CommandLineUtilities.hasArg("gd_iterations"))
		gd_iterations = CommandLineUtilities.getOptionValueAsInt("gd_iterations");
		
		if (CommandLineUtilities.hasArg("online_training_iterations"))
		online_training_iterations = CommandLineUtilities.getOptionValueAsInt("online_training_iterations");
			
		
		if (CommandLineUtilities.hasArg("gd_eta"))
		gd_eta = CommandLineUtilities.getOptionValueAsFloat("gd_eta");
		
		if (CommandLineUtilities.hasArg("polynomial_kernel_exponent"))
			polynomial_kernel_exponent = CommandLineUtilities.getOptionValueAsFloat("polynomial_kernel_exponent");
			
		if (CommandLineUtilities.hasArg("num_features_to_select"))
		num_features = CommandLineUtilities.getOptionValueAsInt("num_features_to_select");
	
		if (CommandLineUtilities.hasArg("online_learning_rate"))
			online_learning_rate = CommandLineUtilities.getOptionValueAsFloat("online_learning_rate");
		
		if (CommandLineUtilities.hasArg("cluster_lambda"))
			cluster_lambda = CommandLineUtilities.getOptionValueAsFloat("cluster_lambda");
		if (CommandLineUtilities.hasArg("clustering_training_iterations"))
			clustering_training_iterations = CommandLineUtilities.getOptionValueAsInt("clustering_training_iterations");
		
		if (CommandLineUtilities.hasArg("num_clusters"))
			num_clusters = CommandLineUtilities.getOptionValueAsInt("num_clusters");
		
		
		String mode = CommandLineUtilities.getOptionValue("mode");
		String data = CommandLineUtilities.getOptionValue("data");
		String predictions_file = CommandLineUtilities.getOptionValue("predictions_file");
		String algorithm = CommandLineUtilities.getOptionValue("algorithm");
		String model_file = CommandLineUtilities.getOptionValue("model_file");
		
		
		if (mode.equalsIgnoreCase("train")) {
			if (data == null || algorithm == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, algorithm, model_file");
				System.exit(0);
			}
			// Load the training data.
			
			DataReader data_reader = new DataReader(data, true);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Train the model.
			Predictor predictor = train(instances, algorithm);
			saveObject(predictor, model_file);		
			
		} else if (mode.equalsIgnoreCase("test")) {
			if (data == null || predictions_file == null || model_file == null) {
				System.out.println("Train requires the following arguments: data, predictions_file, model_file");
				System.exit(0);
			}
			
			// Load the test data.
			DataReader data_reader = new DataReader(data, true);
			List<Instance> instances = data_reader.readData();
			data_reader.close();
			
			// Load the model.
			Predictor predictor = (Predictor)loadObject(model_file);
//			System.out.println("after load model");
			evaluateAndSavePredictions(predictor, instances, predictions_file);
		} else {
			System.out.println("Requires mode argument.");
		}
		long endTime=System.currentTimeMillis(); 
		System.out.println("Time: "+(endTime-startTime)+"ms");   
	}
	

	private static Predictor train(List<Instance> instances, String algorithm) {
		// TODO Train the model using "algorithm" on "data"
		
		if (algorithm.equalsIgnoreCase("majority")){
			MajorityClassification mc = new MajorityClassification();
			mc.train(instances);
			
			// TODO Evaluate the model
			MajorityEvaluator mae = new MajorityEvaluator();
			double eva = mae.evaluate(instances, mc);
			System.out.println(eva);
			return mc;
		}
		if(algorithm.equalsIgnoreCase("even_odd")){
			EvenOddClassification eoc = new EvenOddClassification();
			eoc.train(instances);
			
			// TODO Evaluate the model
			EvenOddEvaluator eoe = new EvenOddEvaluator();
			double eva = eoe.evaluate(instances, eoc);
			System.out.println(eva);
			return eoc;
		}
		
		if(algorithm.equalsIgnoreCase("logistic_regression")){
			
			//TODO Train the model
			LogisticRegression lr = new LogisticRegression(gd_iterations,gd_eta,num_features);
			lr.train(instances);
			lr.printWeight();
			
			//Evaluate the model
			LogisticRegressionEvaluator lre = 
					new LogisticRegressionEvaluator();
			double eva = lre.evaluate(instances, lr);
			return lr;
		}
		
		if(algorithm.equalsIgnoreCase("margin_perceptron")){
			//TODO Train the model
			MarginPerceptron mp = new MarginPerceptron(online_training_iterations,online_learning_rate);
			mp.train(instances);
			
			//Evaluate the model
			MarginPerceptronEvaluator mpe = 
					new MarginPerceptronEvaluator();
			double eva = mpe.evaluate(instances, mp);
			return mp;
		}

		if(algorithm.equalsIgnoreCase("mira")){
			//TODO Train the model
			MarginInfusedRelaxation mira = new MarginInfusedRelaxation(online_training_iterations);
			mira.train(instances);
			
			//Evaluate the model
			MiraEvaluator mirae = 
					new MiraEvaluator();
			double eva = mirae.evaluate(instances, mira);
			return mira;
		}
		
		if(algorithm.equalsIgnoreCase("perceptron_linear_kernel")){
			//TODO Train the model
			DualPerceptron dp = new DualPerceptron(online_training_iterations,
					online_learning_rate, true, false,0,instances);
			dp.train(instances);
			
			//Evaluate the model
			DualPerceptronEvaluator dpe = 
					new DualPerceptronEvaluator();
			double eva = dpe.evaluate(instances, dp);
			return dp;
		}

		if(algorithm.equalsIgnoreCase("perceptron_polynomial_kernel")){
			//TODO Train the model
			DualPerceptron dp = new DualPerceptron(online_training_iterations,
					online_learning_rate, false, true, polynomial_kernel_exponent, instances);
			System.out.println("d = " + polynomial_kernel_exponent);
			dp.train(instances);
			
			//Evaluate the model
			DualPerceptronEvaluator dpe = 
					new DualPerceptronEvaluator();
			double eva = dpe.evaluate(instances, dp);
			return dp;
		}
		
		if(algorithm.equalsIgnoreCase("ska")){
			//TODO Train the model
			SKA ska = new SKA(num_clusters,clustering_training_iterations);
//			System.out.println("d = " + polynomial_kernel_exponent);
			ska.train(instances);
			
			//Evaluate the model
			return ska;
		}
		
		if(algorithm.equalsIgnoreCase("lambda_means")){
			//TODO Train the model
			LambdaMeans lm = new LambdaMeans(cluster_lambda, clustering_training_iterations);
//			System.out.println("d = " + polynomial_kernel_exponent);
			lm.train(instances);
//			LambdaMeansEvaluator lme = new LambdaMeansEvaluator();
//			double eva = lme.evaluate(instances, lm);
			System.out.println("cluster: " +lm.numCluster);

//			lm.printResult();
			
			//Evaluate the model
//			LambdaMeansEvaluator lme = 
//					new LambdaMeansEvaluator();
//			double eva = mirae.evaluate(instances, mira);
			return lm;
		}
		
		return null;
	}

	private static void evaluateAndSavePredictions(Predictor predictor,
			List<Instance> instances, String predictions_file) throws IOException {
		PredictionsWriter writer = new PredictionsWriter(predictions_file);
		// TODO Evaluate the model if labels are available. 
//		System.out.println(predictor);
		if(predictor.getpreName().equalsIgnoreCase("majority")){
			System.out.println("majority test");
			MajorityEvaluator mae = new MajorityEvaluator();
			double eva = mae.evaluate(instances, predictor);
			System.out.println(eva);
			
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
		if(predictor.getpreName().equalsIgnoreCase("even_odd")){
			System.out.println("even_odd test");
			EvenOddEvaluator eoe = new EvenOddEvaluator();
			double eva = eoe.evaluate(instances, predictor);
			System.out.println(eva);
		
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
		if(predictor.getpreName().equalsIgnoreCase("logistic_regression")){
			System.out.println("LR test");
			LogisticRegressionEvaluator lre = new LogisticRegressionEvaluator();
			double eva = lre.evaluate(instances, predictor);
			System.out.println(eva);
		
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
		
		if(predictor.getpreName().equalsIgnoreCase("margin_perceptron")){
			System.out.println("margin_perceptron test");
			MarginPerceptronEvaluator mpe = new MarginPerceptronEvaluator();
			double eva = mpe.evaluate(instances, predictor);
			System.out.println(eva);
			
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}

		if(predictor.getpreName().equalsIgnoreCase("mira")){
			System.out.println("margin_perceptron test");
			MiraEvaluator mirae = new MiraEvaluator();
			double eva = mirae.evaluate(instances, predictor);
			System.out.println(eva);
			
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
		
		if(predictor.getpreName().equalsIgnoreCase("perceptron_linear_kernel")){
			System.out.println("perceptron linear test");
			DualPerceptronEvaluator dpe = 
					new DualPerceptronEvaluator();
			double eva = dpe.evaluate(instances, predictor);
			System.out.println(eva);
			DualPerceptron dp = (DualPerceptron)predictor;
			double d = dp.dvalue();
			boolean isLinear = dp.isLinear();
			boolean isPoly = !isLinear;
			for (Instance instance : instances) {
				Label label = dp.predict(instance,isLinear,isPoly,d);
				writer.writePrediction(label);
			}
		}

		if(predictor.getpreName().equalsIgnoreCase("perceptron_polynomial_kernel")){
			System.out.println("perceptron polynomial test");
			DualPerceptronEvaluator dpe = 
					new DualPerceptronEvaluator();
			double eva = dpe.evaluate(instances, predictor);
			System.out.println(eva);
			DualPerceptron dp = (DualPerceptron)predictor;
			double d = dp.dvalue();
			boolean isLinear = dp.isLinear();
			boolean isPoly = !isLinear;
			for (Instance instance : instances) {
				Label label = dp.predict(instance,isLinear,isPoly,d);
				writer.writePrediction(label);
			}
		}
		
		if(predictor.getpreName().equalsIgnoreCase("lambda_means")){
			System.out.println("lambda test");
			LambdaMeansEvaluator lme = new LambdaMeansEvaluator();
			double eva = lme.evaluate(instances, predictor);
//			System.out.println(eva);
		
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
		
		if(predictor.getpreName().equalsIgnoreCase("ska")){
			System.out.println("ska test");
			SKAEvaluator skae = new SKAEvaluator();
			double eva = skae.evaluate(instances, predictor);
//			System.out.println(eva);
		
			for (Instance instance : instances) {
				Label label = predictor.predict(instance);
				writer.writePrediction(label);
			}
		}
			
		writer.close();
		
	}

	public static void saveObject(Object object, String file_name) {
		try {
			ObjectOutputStream oos =
				new ObjectOutputStream(new BufferedOutputStream(
						new FileOutputStream(new File(file_name))));
			oos.writeObject(object);
			oos.close();
		}
		catch (IOException e) {
			System.err.println("Exception writing file " + file_name + ": " + e);
		}
	}

	/**
	 * Load a single object from a filename. 
	 * @param file_name
	 * @return
	 */
	public static Object loadObject(String file_name) {
		ObjectInputStream ois;
		try {
			ois = new ObjectInputStream(new BufferedInputStream(new FileInputStream(new File(file_name))));
			Object object = ois.readObject();
			ois.close();
			return object;
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("IO Error loading: " + file_name);
		} catch (ClassNotFoundException e) {
			System.err.println("Not found class in when loading: " + file_name);
		}
		return null;
	}
	
	public static void registerOption(String option_name, String arg_name, boolean has_arg, String description) {
		OptionBuilder.withArgName(arg_name);
		OptionBuilder.hasArg(has_arg);
		OptionBuilder.withDescription(description);
		Option option = OptionBuilder.create(option_name);
		
		Classify.options.add(option);		
	}
	
	private static void createCommandLineOptions() {
		registerOption("data", "String", true, "The data to use.");
		registerOption("mode", "String", true, "Operating mode: train or test.");
		registerOption("predictions_file", "String", true, "The predictions file to create.");
		registerOption("algorithm", "String", true, "The name of the algorithm for training.");
		registerOption("model_file", "String", true, "The name of the model file to create/load.");
		// Other options will be added here.
		registerOption("gd_eta", "int", true, "The step size parameter for GD.");
		registerOption("gd_iterations", "int", true, "The number of GD iterations.");
		registerOption("num_features_to_select", "int", true, "The number of features to select.");
		registerOption("online_learning_rate", "double",true,"The learning rate for pereceptron.");
		registerOption("online_training_iterations", "int", true, "The number of training iterations for online methods.");
		registerOption("polynomial_kernel_exponent", "double", true, "The exponent of the polynomial kernel.");
		registerOption("cluster_lambda", "double", true, "The value of lambda of lambda-means.");
		registerOption("clustering_training_iterations", "int", true, "The number of clustering iterations");
		registerOption("num_clusters", "int", true, "The number of cluster");
		
	}
}
