package com.ares.framework.config;



/**
 * Global constant values. Before adding a value to this class, consider adding an entry to
 * the XML configuration. This class (or another Constants class) is the only place that
 * public fields are allowed in the Vanguard codebase.
 *
 * @author wesley
 */

public class Constants   {
	public static final String JSON_MIME_TYPE = "application/json";

	public static final int COUCHBASE_UPDATE_RETRIES_MAX = 10; // cas loop attempts

	public static final int COUCHBASE_APPEND_RETRIES_MAX = 10; // cas loop attempts

	public static final int COUCHBASE_SLEEP_UPDATE = 103;  // use a prime number

	public static final int COUCHBASE_CACHED_DATA_FLAGS_DEFAULT = 0; // Default flags, means something I'm sure

}
