package de.mpa.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

/**
 * This class contains helper utilities for maps, e.g. generic sorting.
 * 
 * @author T.Muth
 * @date 26-07-2012
 *
 */
public class MapUtil {
	
	/**
	 * Sorts a generic map by the values and returns its keys.
	 * @param <K> Map key 
	 * @param <V> Map value
	 * @param map Generic map
	 * @return keys sorted by values
	 */
	public static <K, V extends Comparable<? super V>> List<K> getKeysSortedByValue(Map<K, V> map, boolean reversed) {
	    final int size = map.size();
	    
	    // List of map entries
	    final List<Map.Entry<K, V>> list = new ArrayList<Map.Entry<K, V>>(size);
	    list.addAll(map.entrySet());
	    
	    
	    // Value comparator
	    final ValueComparator<V> cmp = new ValueComparator<V>();
	    
	    // Sort the map entry list by its values
	    Collections.sort(list, cmp);
	    final List<K> keys = new ArrayList<K>();
	    
	    
	    // Set the sorted keys list.
	    if(reversed) {
	    	for (int i = size -1; i>= 0; i--) {
		        keys.add(list.get(i).getKey());
		    }
	    } else {
	    	for (int i = 0; i < size; i++) {
		        keys.add(list.get(i).getKey());
		    }
	    }
	    
	    return keys;
	}
	
	/**
	 * ValueComparator class. 
	 * @author T.Muth
	 * @date 26-07-2012
	 * @param <V> Map value to be compared
	 */
	private static final class ValueComparator<V extends Comparable<? super V>> implements Comparator<Map.Entry<?, V>> {
	    public int compare(Map.Entry<?, V> o1, Map.Entry<?, V> o2) {
	        return o1.getValue().compareTo(o2.getValue());
	    }
	}

}	
