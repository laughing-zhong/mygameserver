package com.ares.framework.dao.couchbase.transcoder;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * @author wesley
 */
public class JsonObjectMapper {

	public static final ObjectMapper objectMapper = JsonObjectMapper.createInstance();

	private static ObjectMapper createInstance() {
		return new ObjectMapper().
				configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ).
				configure( DeserializationFeature.WRAP_EXCEPTIONS, true ).
				configure( SerializationFeature.WRITE_NULL_MAP_VALUES, false );

	}

	public static ObjectMapper getInstance() {
		return JsonObjectMapper.objectMapper;
	}

}
