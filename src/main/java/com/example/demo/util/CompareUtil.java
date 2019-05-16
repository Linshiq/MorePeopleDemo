package com.example.demo.util;

import org.springframework.stereotype.Component;

@Component
public class CompareUtil {

	/**
	 * @param o1
	 * @param o2
	 * @param ignoreCase
	 * @param ignoreNullAndEmpty
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compare(T o1, T o2, boolean ignoreCase,
			boolean ignoreNullAndEmpty) {

		if (o1 == o2) {
			return 0;
		} else if (o1 == null) {
			return ignoreNullAndEmpty && String.class.isAssignableFrom(o2.getClass()) && "".equals(o2) ? 0 : -1;
		} else if (o2 == null) {
			return ignoreNullAndEmpty && String.class.isAssignableFrom(o1.getClass()) && "".equals(o1) ? 0 : 1;
		} else if (ignoreCase && String.class.isAssignableFrom(o1.getClass())
				&& String.class.isAssignableFrom(o2.getClass())) {
			return ((String) o1).compareToIgnoreCase((String) o2);
		} else {
			if (o1 != null && o1.getClass().isEnum()) {
				o1 = (T) String.valueOf(o1);
			}

			if (o2 != null && o2.getClass().isEnum()) {
				o2 = (T) String.valueOf(o2);
			}

			return ((Comparable) o1).compareTo(o2);
		}
	}
	
	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午4:18:08</li>
	 *         <li>功能说明：比较大小 (o1 == o2 return 0) (o1 > o2 return 1) (o1 < o2
	 *         return -1)</li>
	 *         </p>
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static <T extends Comparable<? super T>> int compare(T o1, T o2) {
		return compare(o1, o2, false, true);
	}
	
	/**
	 * @Author computer
	 *         <p>
	 *         <li>2018年5月18日-下午4:27:35</li>
	 *         <li>功能说明：是否相等</li>
	 *         </p>
	 * @param o1
	 * @param o2
	 * @return
	 */
	public static <T extends Comparable<? super T>> boolean equals(T o1, T o2) {

		return compare(o1, o2) == 0 ? true : false;
	}
}
