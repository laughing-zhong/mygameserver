package com.ares.framework.util;

import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.util.UUID;

/**
 * @author  wesley
 */



public class IdUtils {
	private static final Logger LOGGER = LoggerFactory.getLogger( IdUtils.class );


	/**
	 * Generates a random UUID and then compacts it.
	 *
	 * @return a random and compacted UUID as a string
	 */

	public static String generate() {
		return compact( UUID.randomUUID() );
	}

	/**
	 * Compacts a UUID into a 22 byte string, using the full character set (not just hex chars).
	 *
	 * @param uuid the input UUID
	 * @return the compacted UUID as a string
	 */

	public static String compact( UUID uuid ) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream( baos );
		try {
			dos.writeLong( uuid.getMostSignificantBits() );
			dos.writeLong( uuid.getLeastSignificantBits() );
			dos.flush();
		} catch ( Exception e ) {
			LOGGER.error( "compact() exception: ", e );
		}

		byte[] uuidBytes = baos.toByteArray();

		return Base64.encodeBase64URLSafeString(uuidBytes);
	}

	/**
	 * Expands a previously compacted UUID back into original form.
	 *
	 * @param uniqueId the compacted UUID
	 * @return a java.util.UUID matching the input
	 */

	public static UUID expand( String uniqueId ) {
		long mostSigBits = 0;
		long leastSigBits = 0;

		byte[] uuidBytes = Base64.decodeBase64( uniqueId );
		ByteArrayInputStream bais = new ByteArrayInputStream( uuidBytes );
		DataInputStream dis = new DataInputStream( bais );

		try {
			mostSigBits = dis.readLong();
			leastSigBits = dis.readLong();
		} catch ( Exception e ) {
			LOGGER.error( "expand() exception: ", e );
		}

		return new UUID( mostSigBits, leastSigBits );
	}
}
