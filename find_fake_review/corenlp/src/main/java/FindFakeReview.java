import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class FindFakeReview {

    private static final Logger logger = LoggerFactory.getLogger(FindFakeReview.class);

	static StanfordCoreNLP pipeline;
	// For the next variables the index is the mode of text's class
	// estimation which can be: [averaged, weighted, counted]
	static String[] methods = {"Average", "Weighted", "Counts"};
	static int[] pos;
	static int[] neg;
	static int[] unknown;
	static final double[] NEUTRAL = {2.0, 2.0, 0.0};

	private static void getFileScores(File fin) throws Exception {
		FileInputStream fis = new FileInputStream(fin);
		BufferedReader br = new BufferedReader(new InputStreamReader(fis));
	 
		String line = null;
        StarAndReview starAndReview;
		double[] scores;
		while ((line = br.readLine()) != null) {
            starAndReview = readJSON(line);
            if (starAndReview.stars <= 1) {
                scores = getSentimentScores(starAndReview.review);
                if (scores[1] > 2.0) {
                    logger.info("=================================Found fake review=================================");
                    logger.info("stars: {}, score: {}", starAndReview.stars, scores[1]);
                    logger.info("review: " + starAndReview.review);
                }
            } else if (starAndReview.stars == 5) {
                scores = getSentimentScores(starAndReview.review);
                if (scores[1] < 0.5) {
                    logger.info("=================================Found fake review=================================");
                    logger.info("stars: {}, score: {}", starAndReview.stars, scores[1]);
                    logger.info("review: " + starAndReview.review);
                }

            }
		}
	 
		br.close();
	}

    // {"votes": {"funny": 0, "useful": 0, "cool": 0}, "user_id": "PUFPaY9KxDAcGqfsorJp3Q", "review_id": "Ya85v4eqdd6k9Od8HbQjyA",
    // "text": "xxx", business_id": "5UmKMjUEUNdYWqANhGckJw", "stars": 4, "date": "2012-08-01", "type": "review"}
    static class StarAndReview {
        int stars;
        String review;

        public StarAndReview(int stars, String reviews) {
            this.stars = stars;
            this.review = reviews;
        }
    }

	private static StarAndReview readJSON(String str) throws Exception{
		JSONParser jsonParser = new JSONParser();
		JSONObject jsonObject = (JSONObject) jsonParser.parse(str);

		String review = (String) jsonObject.get("text");
        long stars = (Long) jsonObject.get("stars");
        return new StarAndReview((int)stars, review);
	}
	
	/**
	 * Computes the sentiment estimation of the whole text.
	 * It iterates over all the sentences and gets a sentiment for each one.
	 * A sentiment score of 0 or 1 is negative, 2 neutral and 3 or 4 positive.
	 * The aggregation is being doe with three different modes.
	 * The first is the average of all the sentences. The second uses the 
	 * each sentence's length as weight and the third counts the number of 
	 * positive/negative sentences.
	**/
	public static double[] getSentimentScores(String text) {
		double sentimentAvg = 0; // Average sentiment of all sentences.
		double sentimentWeight = 0; // Weight each sentence by length.
		double sentimentCount = 0; // Count pos/neg sentences, ignoring neutral.

		if (text != null && text.length() > 0) {
			int count = 0;
			int weight = 0;
			int clss = 0;

			Annotation annotation = pipeline.process(text);

			for (CoreMap sentence : annotation.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence.get(SentimentCoreAnnotations.SentimentAnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String sentenceText = sentence.toString();

				// System.out.println(sentenceText + " " + sentiment + "\n");
				count++;
				weight += sentenceText.length();
				sentimentAvg += sentiment;
				sentimentWeight += (double)sentiment * sentenceText.length();
				if (sentiment > 2) 
					sentimentCount++;
				else if (sentiment < 2)
					sentimentCount--;
			}

			return new double[]{sentimentAvg / (double)count, 
				sentimentWeight / weight, 
				sentimentCount};
		} else {
			return NEUTRAL;
		}
	}

	public static void main(String[] args) {

		// The CoreNLP pipeline
		Properties props = new Properties();
		props.setProperty("annotators", "tokenize, ssplit, parse, sentiment");
		pipeline = new StanfordCoreNLP(props);

		String filePath = "/Users/yutao/project_b/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_review_restaurant_40000.json";
		File fin;
		double[] accuracy = {0.0, 0.0, 0.0};
        fin = new File(filePath);

        try {
            getFileScores(fin);
        } catch (Exception ex) {
            System.out.println("There was a problem: ");
            ex.printStackTrace();
            System.exit(0);
        }
	}
}