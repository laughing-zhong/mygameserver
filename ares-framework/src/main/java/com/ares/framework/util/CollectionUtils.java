package com.ares.framework.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * 
 * @author m.mcbride
 *
 */
public class CollectionUtils {

	/**
	 * extracts a random subList from the provided collection.
	 * @param collection the collection to be extracted from
	 * @param numberOfElements in the extracted subList
	 * @param <T> generic type of collection contents
	 * @return the extracted subList
	 */

	public static <T> List<T> extractRandomSubList(Collection<T> collection, int numberOfElements) {
		List<T> shuffled = new ArrayList<>();
		shuffled.addAll(collection);
		Collections.shuffle(shuffled);
		
		if (numberOfElements >= shuffled.size()) {
			return shuffled;
		} else {
			return shuffled.subList(0, numberOfElements);
		}
	}
	
}
