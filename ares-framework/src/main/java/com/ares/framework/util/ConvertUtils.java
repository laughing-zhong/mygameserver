package com.ares.framework.util;

import com.ares.framework.helper.Msg;
import com.google.common.base.Strings;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import java.math.BigDecimal;
import java.util.*;

/**
 * @author dadler
 */

public class ConvertUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger( ConvertUtils.class );
	public static final Map<Class<?>, Class<?>> primitiveBoxedTypes = new HashMap<>();

	static {
		primitiveBoxedTypes.put( boolean.class, Boolean.class );
		primitiveBoxedTypes.put( byte.class, Byte.class );
		primitiveBoxedTypes.put( short.class, Short.class );
		primitiveBoxedTypes.put( char.class, Character.class );
		primitiveBoxedTypes.put( int.class, Integer.class );
		primitiveBoxedTypes.put( long.class, Long.class );
		primitiveBoxedTypes.put( float.class, Float.class );
		primitiveBoxedTypes.put( double.class, Double.class );
	}

	public static int parseInt( String value, int defaultValue ) {

		if ( !Strings.isNullOrEmpty( value ) ) {
			try {
				return Integer.parseInt( value );
			} catch( Exception ignored ) {}
		}

		return defaultValue;
	}

	public static long toLong( Long value, long defaultValue ) {
		return value == null ? defaultValue : value;
	}

	public static <T> T convert( final String value, final Class<T> clazz ) {

		try {
			return convertUnsafe( value, clazz );
		} catch ( NumberFormatException e ) {
			LOGGER.warn( "Value '{}' failed numeric conversion to type {}", value, clazz.getSimpleName() );
		} catch ( IllegalArgumentException e ) {
			LOGGER.warn( e.getMessage() );
		} catch ( Exception e ) {
		//	LOGGER.warn( "Value '{}' failed conversion to type {}: {}", value, clazz.getSimpleName(), e.getMessage() );
		}

		return null;
	}

	public static <T> T convertUnsafe( final String value, final Class<T> clazz ) {

		Object convertedValue;

		if ( boolean.class.equals( clazz ) || Boolean.class.equals( clazz ) ) {
			convertedValue = Boolean.parseBoolean( value );
		} else if ( int.class.equals( clazz ) || Integer.class.equals( clazz ) ) {
			convertedValue = Integer.parseInt( value );
		} else if ( short.class.equals( clazz ) || Short.class.equals( clazz ) ) {
			convertedValue = Short.parseShort( value );
		} else if ( long.class.equals( clazz ) || Long.class.equals( clazz ) ) {
			convertedValue = Long.parseLong( value );
		} else if ( float.class.equals( clazz ) || Float.class.equals( clazz ) ) {
			convertedValue = Float.parseFloat( value );
		} else if ( double.class.equals( clazz ) || Double.class.equals( clazz ) ) {
			convertedValue = Double.parseDouble( value );
		} else if ( String.class.equals( clazz ) ) {
			convertedValue = value;
		} else if ( char.class.equals( clazz ) || Character.class.equals( clazz ) ) {
			convertedValue = Strings.isNullOrEmpty( value ) ? '\0' : value.charAt( 0 );
		} else if ( BigDecimal.class.equals( clazz ) ) {
			convertedValue = new BigDecimal( value );
		} else if ( byte.class.equals( clazz ) || Byte.class.equals( clazz ) ) {
			convertedValue = Byte.parseByte( value );
		} else {
			throw new IllegalArgumentException( Msg.format( "Unsupported type: '{}'", clazz.getSimpleName() ) );
		}

		return clazz.cast( convertedValue );
	}

	public static Class<?> toBoxed( Class<?> clazz ) {
		Class<?> result = primitiveBoxedTypes.get( clazz );
		return result != null ? result : clazz;
	}
	
	/**
	 * 
	 * @param commaDelimitedList example "1,2,3,4"
	 * @return int array with the values
	 */
	public static int[] toIntArray(String commaDelimitedList) {
		String[] values = commaDelimitedList.split( "," );

		int[] parsedValues = new int[values.length];
		for ( int i = 0; i < values.length; i++ ) {
			try {
				parsedValues[i] = Integer.parseInt( values[i] );
			} catch ( NumberFormatException e ) {
				LOGGER.warn( "Unable to parse slot for position {} defaulting to 0.", i, e );
				parsedValues[i] = 0;
			}
		}
		return parsedValues.length == 0 ? new int[]{0} : parsedValues;
	}
}
