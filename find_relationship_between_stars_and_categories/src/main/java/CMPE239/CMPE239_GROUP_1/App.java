package CMPE239.CMPE239_GROUP_1;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App 
{
	
	public static final int BEGIN = 600;

	public static final int END = 5000;
	
    public static void main( String[] args ) throws Exception
    {
        List<String> lines = FileUtils.readLines(new File("/Users/xiaofengli/Downloads/yelp_dataset_challenge_academic_dataset/yelp_academic_dataset_business.json"), "utf8");
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
        			System.out.println(value + 1);
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
        	String cvsLine = e.toCVSLine(longestCategory, map, BEGIN, END);
        	if (!cvsLine.isEmpty()) {
        		printList.add(cvsLine);
        	}
        	set.addAll(e.getCategories());
        }
        System.out.println("\"" + String.join("\",\"", set) + "\"");
        FileUtils.writeLines(new File("test1.csv"), printList);
    }
}
