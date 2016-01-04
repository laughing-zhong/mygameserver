package com.ares.framework.helper;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.springframework.beans.factory.config.AbstractFactoryBean;

/**
 * @author wesley
 */
public class ObjectMapperFactoryBean extends AbstractFactoryBean<ObjectMapper> {
	@Override
	public Class<?> getObjectType() {
		return ObjectMapper.class;
	}

	@Override
	protected ObjectMapper createInstance() throws Exception {
		return new ObjectMapper().
				configure( DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false ).
				configure( DeserializationFeature.WRAP_EXCEPTIONS, true ).
				configure( SerializationFeature.WRITE_NULL_MAP_VALUES, false );
	}
}
