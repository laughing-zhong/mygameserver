package game.framework.dao.redis;



import redis.clients.jedis.Jedis;

/**
 * Simple callback that allows clients to use Jedis without worry about
 * how to open/close the jedis connection.
 * 
 * @author m.mcbride
 *
 * @param <T>
 */

public interface RedisCallback<T>
{
	T execute( Jedis jedis );
}
