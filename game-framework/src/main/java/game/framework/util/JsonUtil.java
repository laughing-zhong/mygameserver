package game.framework.util;

import com.fasterxml.jackson.core.JsonProcessingException;

import game.framework.dao.couchbase.transcoder.JsonObjectMapper;
import game.framework.domain.json.JsonDO;

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
