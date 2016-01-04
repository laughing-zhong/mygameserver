package com.ares.framework.util;

import com.ares.framework.dao.couchbase.transcoder.JsonObjectMapper;
import com.ares.framework.domain.json.JsonDO;
import com.fasterxml.jackson.core.JsonProcessingException;

public class JsonUtil {
	public static String  genJsonStr(JsonDO jsonDo){
		
		try {
			return JsonObjectMapper.getInstance().writeValueAsString(jsonDo);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

}
