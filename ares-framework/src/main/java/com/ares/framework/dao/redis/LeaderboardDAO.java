package com.ares.framework.dao.redis;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.inject.Inject;

import org.springframework.stereotype.Repository;

import com.ares.service.exception.RecordNotFoundException;

import redis.clients.jedis.Jedis;
import redis.clients.jedis.Tuple;


/**
 * @author mark.mcbride
 */
@Repository
public class LeaderboardDAO {

	@Inject
	private RedisTemplate redisTemplate;
	
	public void score(final String leaderboard, final double score, final String playerId) {
		redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zadd(leaderboard, score, playerId);
			}
		});
		
	}
	
	public void remove(final String leaderboard, final String playerId) {
		redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zrem(leaderboard, playerId);
			}
		});
	}
	
	public long getRank(final String leaderboard, final String playerId) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zrevrank(leaderboard, playerId);
			}
		});
	}
	
	public long getIncRank(final String leaderboard, final String playerId) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zrank(leaderboard, playerId);
			}
		});
	}
	public double getScore(final String leaderboard, final String playerId) {
		return redisTemplate.execute(new RedisCallback<Double>() {
			public Double execute(Jedis jedis) {
				return jedis.zscore(leaderboard, playerId);
			}
		});
	}
	
	public Long getRankFromScore(final String leaderboard, final int score) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				Set<String> members = jedis.zrevrangeByScore(leaderboard, score, score, 0, 1);
				if (members.isEmpty())
					return null;
				
				return jedis.zrevrank(leaderboard, members.iterator().next());
			}
		});
	}
	
	public Long getClosestRankFromScore(final String leaderboard, final int score) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				Set<String> members = jedis.zrevrangeByScore(leaderboard, score, 0, 0, 1);
				if (!members.isEmpty())
					return jedis.zrevrank(leaderboard, members.iterator().next());
				
				// If there is no one with a lower score, then look for higher scores
				members = jedis.zrangeByScore(leaderboard, score, Integer.MAX_VALUE, 0, 1);
				if (!members.isEmpty())
					return jedis.zrevrank(leaderboard, members.iterator().next());
				
				// This scenario should be practically impossible to reach. There should be enough players in the leaderboard to find at least one.
				return null;
			}
		});
	}
	
	/**
	 * This method call only works if there are more players in the leaderboard than playersToAvoid.
	 * It simply finds a record close to the rank passed in and returns it.
	 * @param leaderboard
	 * @param rank
	 * @param playersToAvoid
	 * @return
	 */
	public Tuple getRecord(final String leaderboard, final long rank, final Set<String> playersToAvoid) {
		return redisTemplate.execute(new RedisCallback<Tuple>() {
			public Tuple execute(Jedis jedis) {
				int start = (int) Math.max(0, rank - playersToAvoid.size());
				Set<Tuple> records = jedis.zrevrangeWithScores(leaderboard, start, start + playersToAvoid.size());
				
//				for (Iterator<Tuple> iterator = records.iterator(); iterator.hasNext();) {
//					Tuple tuple = iterator.next();
//					if (!playersToAvoid.contains(tuple.getElement()))
//						return tuple;
//				}
				
				for (Iterator<Tuple> iterator = records.iterator(); iterator.hasNext();) {
					Tuple tuple = iterator.next();
					String id = tuple.getElement().split("#")[0];
					boolean checkResult = false;
					for(String avoidPlayerId : playersToAvoid) {
						String avoidId = avoidPlayerId.split("#")[0];
						if(id.equals(avoidId)) {
							checkResult = true;
						}
					}
					if(!checkResult) {
						return tuple;
					}
				}
				
				throw new RecordNotFoundException("Could not find a leaderboard record for trophy rank of " + rank +  ".");
			}
		});
	}
	
	public List<String> getKeyByRank(final String leaderBoard,final long start,final long end ,final String playersToAvoid)
	{	
		final List<String> playerIds = new ArrayList<String>();
		 redisTemplate.execute(new RedisCallback<Long>() {
				public Long execute(Jedis jedis) {
					Set<Tuple> records = jedis.zrangeWithScores(leaderBoard, start, end);
					boolean bFound = false;
					for (Iterator<Tuple> iterator = records.iterator(); iterator.hasNext();) {
						Tuple tuple = iterator.next();
						String id = tuple.getElement().split("#")[0];
				
							String avoidId = playersToAvoid.split("#")[0];
							if(!id.equals(avoidId)) {
								playerIds.add(id);
								bFound =true;
							}
					}
					
					if( !bFound)
					  throw new RecordNotFoundException("Could not find a leaderboard record for trophy rank of start = " + start +  "  end ="+end);
					return 0l;
				}
			});		 
		 return playerIds;	
	}
	
	public Set<Tuple> getLeaderBoard(final String leaderboard, final int start, final int limit)
	{		
		return redisTemplate.execute(new RedisCallback<Set<Tuple>>() {
			public Set<Tuple> execute(Jedis jedis) {	
				return jedis.zrevrangeWithScores(leaderboard, start , start + limit - 1);
			}
		});	
	}
	
	public Set<Tuple> getLadderBoard(final String leaderboard,final int start, final int  limit)
	{
		return redisTemplate.execute(new RedisCallback<Set<Tuple>>() {
			public Set<Tuple> execute(Jedis jedis) {	
				return jedis.zrangeWithScores(leaderboard, start , start + limit - 1);
			}
		});	
	}
	
	public long getCount(final String leaderboard) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zcard(leaderboard);
			}
		});
	}

	public boolean leaderboardExists(final String leaderboard) {
        return redisTemplate.execute(new RedisCallback<Boolean>() {
            public Boolean execute(Jedis jedis) {

            return jedis.exists(leaderboard);
            }
        });
    }

	public void expire(final String leaderboard, final long duration /* in seconds */) {
		redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.expire(leaderboard, (int) duration);
			}
		});
	}
	
	public Long  LockLeaderBoardCaluteReward(final String  leaderBoard)
	{
		final String leaderBoardLock = leaderBoard +":lock";
		return redisTemplate.execute(new RedisCallback<Long>(){
			public Long execute(Jedis jedis){
				long result =  jedis.setnx(leaderBoardLock, "lock");
				if(result > 0)
				{
					jedis.expire(leaderBoardLock, 600);
				}
				return result;
		}
	});
	}
	
	
	public boolean tryLockLeaderBoard(final String leaderBoard)
	{
		final String leaderBoardLock = leaderBoard +":lock";
		long result =  redisTemplate.execute(new RedisCallback<Long>(){
				public Long execute(Jedis jedis){
				int loopCount = 0;
				while(true)
				{
					long result =  jedis.setnx(leaderBoardLock, "lock");
					if(result > 0)
					{
						jedis.expire(leaderBoard, 30);
						return result;
					}
					loopCount++;
					if(loopCount < 8)
					{
						try {
							System.out.println("============ try to get lock count = "+loopCount);
							Thread.sleep(10);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					else//can not get the lock
					{
						System.out.println("Error : can not get the lock");
						return 0l;
					}
				}
			}
		});
		
		if(result == 1)
			return true;
		return false;
	}
	public void UnLockLeaderBoard(final String leaderBoard)
	{
		final String leaderBoardLock = leaderBoard +":lock";
		redisTemplate.execute(new RedisCallback<Long>(){
			public Long execute(Jedis jedis){
			   jedis.del(leaderBoardLock);
				return 0l;
		}
	});
	}
	
	public long getLadderBoardRank(final String leaderboard, final String playerId) {
		return redisTemplate.execute(new RedisCallback<Long>() {
			public Long execute(Jedis jedis) {
				return jedis.zrank(leaderboard, playerId);
			}
		});
	}

}
