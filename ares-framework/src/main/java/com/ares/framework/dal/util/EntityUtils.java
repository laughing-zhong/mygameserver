package  com.ares.framework.dal.util;


import com.ares.framework.dao.redis.EntityKey;
import com.google.common.base.Strings;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author wesley
 */


public class EntityUtils {

	private static final Logger LOGGER = LoggerFactory.getLogger( EntityUtils.class );
	private static final String ENTITY_KEY_DEFAULT_SEPARATOR = ":";
	private static final String ENTITY_FORMAT_SINGLE = "%s%s%%s";
	private static final String ENTITY_FORMAT_DOUBLE = "%s%s%s%s%%s";

	/**
	 * @return the key template.
	 */
	public static String buildEntityKeyTemplateForClass( Class<?> entityClass, String separator ) {

		String className = entityClass.getSimpleName();

		EntityKey entityKeyAnnotation = entityClass.getAnnotation( EntityKey.class );

		// No annotation present, use the class name.

		if ( entityKeyAnnotation == null ) {
			LOGGER.error( "Class {} missing @EntityKey annotation", className );
			// Assume all DAOs in a game have unique simpleNames (e.g. avoid cross-package/inner-class collisions)

			// Example: player:1234 / "%s:%%s" -> format is %s%s%%s

			return buildSingleEntityKey( className, separator );

		} else {

			// Default value, overrides everything else
			String value = entityKeyAnnotation.value();

			if ( !Strings.isNullOrEmpty( value ) ) return buildSingleEntityKey( value, separator );

			String prefix = entityKeyAnnotation.prefix();
			String postfix = entityKeyAnnotation.suffix();

			if ( !Strings.isNullOrEmpty( prefix ) && !Strings.isNullOrEmpty( postfix ) )
				return buildDoubleEntityKey( prefix, postfix, separator );
			if ( !Strings.isNullOrEmpty( prefix ) && Strings.isNullOrEmpty( postfix ) )
				return buildSingleEntityKey( prefix, separator );
			if ( !Strings.isNullOrEmpty( postfix ) && Strings.isNullOrEmpty( prefix ) )
				return buildSingleEntityKey( postfix, separator );
		}

		throw new IllegalArgumentException( "Could not build EntityKey for class: " + className );

	}

	public static String buildEntityKeyTemplateForClass( Class<?> entityClass ) {
		return buildEntityKeyTemplateForClass( entityClass, ENTITY_KEY_DEFAULT_SEPARATOR );
	}

	private static String buildSingleEntityKey( String descriptor, String separator ) {
		return String.format( ENTITY_FORMAT_SINGLE, descriptor, separator );
	}

	private static String buildDoubleEntityKey( String descriptor1, String descriptor2, String separator ) {
		return String.format( ENTITY_FORMAT_DOUBLE, descriptor1, separator, descriptor2, separator );
	}

}
