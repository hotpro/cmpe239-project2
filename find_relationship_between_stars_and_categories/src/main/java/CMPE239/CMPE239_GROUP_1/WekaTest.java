package CMPE239.CMPE239_GROUP_1;

import java.io.File;

import weka.clusterers.SimpleKMeans;
import weka.core.DistanceFunction;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

/**
 * Invoke the api of Weka and produce the result which we want.
 * @author xiaofengli
 *
 */
public class WekaTest {

	public static void main(String[] args) {
		
		Instances ins = null;

		SimpleKMeans KM = null;

		DistanceFunction disFun = null;

		try {
			File file = new File("test.arff");
			ArffLoader loader = new ArffLoader();
			loader.setFile(file);
			ins = loader.getDataSet();

			KM = new SimpleKMeans();
			KM.setNumClusters(20);
			KM.buildClusterer(ins);
			KM.setMaxIterations(10);

			System.out.println(KM.toString());
			for (String option : KM.getOptions()) {
				System.out.println(option);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
