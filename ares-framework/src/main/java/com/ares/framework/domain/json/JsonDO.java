package com.ares.framework.domain.json;


import com.ares.framework.domain.KeyedDO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import java.util.List;

/**
 * @author zhongwq
 */
abstract public class JsonDO extends KeyedDO<String> {
	private static final String SEPARATOR = ":";
	private static final Joiner joiner = Joiner.on( SEPARATOR ).skipNulls();
	private static final Splitter splitter = Splitter.on( SEPARATOR );

	/**
	 * Returns a list of Id elements used to make the key
	 *
	 * @return list of Id elements
	 */
	@JsonIgnore
	public List<String> getIds() {
		return Lists.newArrayList( splitter.split( getId() ) );
	}

	/**
	 * Returns an array of Id elements used to make the key
	 *
	 * @return array of Id elements
	 */
	@JsonIgnore
	public String[] getIdsArray() {
		return Iterables.toArray( splitter.split( getId() ), String.class );
	}

	/**
	 * Sets the full concatenated Id key by joining
	 * the provided ids.
	 *
	 * @param ids Id keys to join
	 */
	@JsonIgnore
	public void setIds( String... ids ) {
		setId( joiner.join( ids ) );
	}
}