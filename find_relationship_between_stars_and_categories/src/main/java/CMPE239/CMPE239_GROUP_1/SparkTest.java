package CMPE239.CMPE239_GROUP_1;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.mllib.clustering.KMeans;
import org.apache.spark.mllib.clustering.KMeansModel;
import org.apache.spark.mllib.linalg.Vector;
import org.apache.spark.mllib.linalg.Vectors;
import org.apache.spark.api.java.function.Function;

import com.google.gson.Gson;

public class SparkTest {

	public static Map<String, Integer> indexMap = new HashMap<String, Integer>();

	public static final int NUM_CLUSTERS = 20;

	public static final int NUM_ITERATIONS = 10;

	public static final int BEGIN = 2000;

	public static final int END = 13000;

	public static void main(String[] args) throws IOException {

		System.out.println(kmean(BEGIN, END, NUM_CLUSTERS, NUM_ITERATIONS));
	}

	/***
	 * K-Mean based on Spark MLlib.
	 * 
	 * @param begin
	 *            : The begin frequency of the model.
	 * @param end
	 *            : The end frequency of the model.
	 * @param numClusters
	 *            : The number of clusters.
	 * @param numIterations
	 *            : The number of iterations.
	 * @return Within Set Sum of Squared Errors
	 * @throws IOException
	 */
	private static double kmean(int begin, int end, int numClusters, int numIterations) throws IOException {

		SparkConf conf = new SparkConf().setAppName("K-means Example").setMaster("local")
				.set("spark.driver.allowMultipleContexts", "true");

		JavaSparkContext sc = new JavaSparkContext(conf);

		// Load and parse data
		String path = "test.csv";

		prepareData(begin, end);

		JavaRDD<String> data = sc.textFile(path);
		JavaRDD<Vector> parsedData = data.map(new Function<String, Vector>() {
			public Vector call(String s) {
				String[] sarray = s.split(",");
				double[] values = new double[sarray.length];
				for (int i = 0; i < sarray.length; i++) {
					values[i] = Double.parseDouble(sarray[i]);
				}
				return Vectors.dense(values);
			}
		});
		// parsedData.cache();

		// Cluster the data into classes using KMeans
		KMeansModel clusters = KMeans.train(parsedData.rdd(), numClusters, numIterations);
		for (Vector center : clusters.clusterCenters()) {
			System.out.println(" " + center);
		}
		// Evaluate clustering by computing Within Set Sum of Squared Errors
		double WSSSE = clusters.computeCost(parsedData.rdd());
		System.out.println("Within Set Sum of Squared Errors = " + WSSSE);
		System.out.println(begin + "\t" + end + "\t" + numClusters + "\t" + numIterations + "\t" + WSSSE);
		return WSSSE;
	}

	/**
	 * Prepare the data. It will read the data from the json file and generate
	 * csv file which will be loaded by Spark MLlib.
	 * 
	 * @param begin
	 * @param end
	 * @throws IOException
	 */
	private static void prepareData(int begin, int end) throws IOException {
		List<String> lines = FileUtils
				.readLines(
						new File(
								"/Users/xiaofengli/Downloads/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"),
						"utf8");
		Gson gson = new Gson();
		List<Example> list = new ArrayList<Example>();
		List<String> printList = new ArrayList<String>();
		int longestCategory = 0;
		for (String line : lines) {
			Example example = gson.fromJson(line, Example.class);
			list.add(example);
			longestCategory = Math.max(example.getCategories().size(), longestCategory);
		}
		Map<String, Integer> map = new HashMap<String, Integer>();
		for (Example e : list) {
			for (String s : e.getCategories()) {
				Integer value = map.get(s);
				if (value == null) {
					map.put(s, 1);
				} else {
					map.put(s, value + 1);
				}
			}
		}
		Set<String> set = new HashSet<String>();
		Iterator<Example> it = list.iterator();
		while (it.hasNext()) {
			Example e = it.next();
			if (e.getCategories().size() == 0) {
				it.remove();
			}
		}
		for (Example e : list) {
			String cvsLine = e.toDoubleCVSLine(longestCategory, map, begin, end);
			if (!cvsLine.isEmpty()) {
				printList.add(cvsLine);
			}
			set.addAll(e.getCategories());
		}
		int index = 1;
		for (String s : set) {
			map.put(s, index++);
		}
		FileUtils.writeLines(new File("test.csv"),
				printList);
	}

}
