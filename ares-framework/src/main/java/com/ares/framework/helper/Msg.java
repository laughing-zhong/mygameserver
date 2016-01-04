package com.ares.framework.helper;

import org.slf4j.helpers.MessageFormatter;

/**
 * String formatting utility that surfaces slf4j's brace-substitution pattern.
 *
 * Example:
 *
 * 		format( "{}, {}, {}", 1, 2, 3 ) returns "1, 2, 3"
 *
 * @author wesley
 */

public class Msg {

	public static String format( String message, Object... args ) {
		return MessageFormatter.arrayFormat( message, args ).getMessage();
	}

}
