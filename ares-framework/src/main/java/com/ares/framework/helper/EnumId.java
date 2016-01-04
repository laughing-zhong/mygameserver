package com.ares.framework.helper;

/**
 * Enums should implement this interface to use the EnumIdMap reverse lookup utility.
 *
 * @author wesley
 */

public interface EnumId {
	/**
	 * This method is used by EnumIdMap to determine the Id key of an Enum constant
	 * <p/>
	 * For serialization, this method can be marked with @JsonValue so that
	 * the Jackson JSON serializer uses the int id instead of the enum string constant.
	 *
	 * @return the int Id of the underlying enum constant
	 */
	int getId();
}
