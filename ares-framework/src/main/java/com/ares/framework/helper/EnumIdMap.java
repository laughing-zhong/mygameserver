package com.ares.framework.helper;

import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for inverse mapping between Enum Ids and Enum Constants.
 * This class should be used in the following manner, inside a parent Enum that implements EnumId:
 * <pre>
 * {@code
 *
 *  private static final EnumIdMap<EnumType> enumId = new EnumIdMap<>( EnumType.class, EnumType.DEFAULT );
 *
 * 	public static EnumType valueForId( int id )
 *  {
 * 		return enumId.get( id );
 *     }
 *
 * }
 * </pre>
 * <p/>
 * For deserialization from the enum id, mark the valueForId method with @JsonCreator
 * so that the Jackson deserializer can map from id->enum constant.
 *
 * @author wesley
 */


public class EnumIdMap<E extends Enum<E> & EnumId> {
	private final Map<Integer, E> enumMap = new HashMap<>();
	private E defaultValue;

	/**
	 * As Java lacks generic type information, we must manually
	 * pass the Enum class to retrieve the Enum constants.
	 *
	 * @param enumClass    E.class
	 * @param defaultValue The
	 */
	public EnumIdMap( Class<E> enumClass, E defaultValue ) {
		for ( E e : enumClass.getEnumConstants() )
			enumMap.put( e.getId(), e );

		this.defaultValue = defaultValue;
	}

	/**
	 * Convenience constructor. As Java lacks generic type information, we must manually
	 * pass the Enum class to retrieve the Enum constants.
	 *
	 * @param enumClass should match E.class
	 */

	public EnumIdMap( Class<E> enumClass ) {
		this( enumClass, null );
	}

	/**
	 * Reverse lookup method for mapping from int id->Enum constant.
	 * If defaultValue has been set via constructor and no match is found,
	 * it will be returned. id is autoboxed to Integer for lookup in enumMap.
	 *
	 * @param id the integer id to look up
	 * @return The matching Enum constant
	 */
	public E get( int id ) {
		E e = enumMap.get( id );
		if ( (e == null) && (defaultValue != null) ) return defaultValue;
		return e;
	}
}

