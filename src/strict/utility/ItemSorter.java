package strict.utility;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class ItemSorter {

	public static List<Map.Entry<String, Integer>> sortHashMapInt(
			HashMap<String, Integer> wordMap) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(
				wordMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {

				// TODO Auto-generated method stub
				Integer v2 = o2.getValue();
				Integer v1 = o1.getValue();
				return v2.compareTo(v1);
			}
		});
		// returning the sorted list
		return list;
	}
	
	public static List<Map.Entry<String, Integer>> sortHashMapIntASC(
			HashMap<String, Integer> wordMap) {
		List<Map.Entry<String, Integer>> list = new LinkedList<>(
				wordMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Integer>>() {
			@Override
			public int compare(Entry<String, Integer> o1,
					Entry<String, Integer> o2) {

				// TODO Auto-generated method stub
				Integer v2 = o2.getValue();
				Integer v1 = o1.getValue();
				return v1.compareTo(v2);
			}
		});
		// returning the sorted list
		return list;
	}

	public static List<Map.Entry<String, Double>> sortHashMapDouble(
			HashMap<String, Double> wordMap) {
		List<Map.Entry<String, Double>> list = new LinkedList<>(
				wordMap.entrySet());
		Collections.sort(list, new Comparator<Map.Entry<String, Double>>() {
			@Override
			public int compare(Entry<String, Double> o1,
					Entry<String, Double> o2) {

				// TODO Auto-generated method stub
				Double v2 = o2.getValue();
				Double v1 = o1.getValue();
				return v2.compareTo(v1);
			}
		});
		// returning the sorted list
		return list;
	}
}
