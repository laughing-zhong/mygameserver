package game.app.content.service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.inject.Inject;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.stereotype.Service;

import redis.clients.jedis.Jedis;


import game.app.content.Content;
import game.app.content.vip.VipConfig;
import game.app.content.vip.VipConfigDAO;
import game.app.content.vip.VipConfigDO;
import game.app.service.redis.ContentChangeListener;
import game.framework.dao.redis.RedisCallback;
import game.framework.dao.redis.RedisTemplate;
/**
 * @author m.mcbride
 */
@Service
public class ContentServiceImpl implements ContentService, ApplicationEventPublisherAware {

	private static final Logger LOGGER = LoggerFactory.getLogger( ContentServiceImpl.class );

	
	private ApplicationEventPublisher appEventPublisher;
	
	@Inject
	private ContentCacheManager cacheManager;
	
	@Inject
	private VipConfigDAO   vipConfigDAO;
	@Inject
	private RedisTemplate redisTemplate;

	@Override
	public void publishContent( Content content ) {
		publishContent( content, null, null, false, false );
	}

	@Override
	public void publishContent( Content content, byte[] contentData, String revision ) {
		publishContent( content, contentData, revision, false, false );
	}

	@Override
	public void publishContent( Content content, boolean allowRemoves ) {
		publishContent( content, null, null, allowRemoves, false );
	}

	/**
	 * Handles a content push: first validates the data, then writes it to the database, and finally
	 * fires an event to cause cache invalidation across the server cluster.
	 *
	 * @param content      complete Content object containing content to publish
	 * @param contentData  raw byte array containing the original data that yielded Content
	 * @param allowRemoves disables a safety check regarding removing content, that if removed, can break the game
	 */
	@Override
	public void publishContent( Content content, byte[] contentData, String revision, boolean allowRemoves, boolean silent ) {
		LOGGER.info( "Preparing to add content to the system (allowRemoves = {}, silent = {}).", allowRemoves, silent );

		// This method will throw various exceptions if validation errors are encountered,
		// and control flow will stop here.
		validateAndPrepData( content, allowRemoves );

		//LOGGER.info( "Update UnitFragments: {}", content.getUnitFragments());
		
		// Persist content directly to Couchbase


		// let's generate an id if not present. good for quick iteration.
	//	if ( StringUtils.isNullOrWhitespace( unitDefinitionVersion ) ) unitDefinitionVersion = IdUtils.generate();

		// let's generate an id if not present. good for quick iteration.
	//	if ( StringUtils.isNullOrWhitespace( storeVersion ) ) storeVersion = IdUtils.generate();

		vipConfigDAO.put(new VipConfigDO(CURRENT_VIP_KEY,content.getVipConfig()));

		// invalidate cache
		cacheManager.invalidateAll();

		redisTemplate.execute( new RedisCallback<Long>() {
			public Long execute( Jedis jedis ) {
				return jedis.publish( ContentChangeListener.CONTENT_CHANGE, ContentChangeListener.INVALIDATE_COMMAND );
			}
		} );
	}



	/**
	 * It's expected that the document has been validated (elements exist, attributes not null) before being submitted to the service. In this method we: -
	 * Validate there are no duplicate unit definitions. - Validate the unit promotions extensively and also generate the promoteGroup data to store as part of
	 * the Unit Definition. - Find the TrainingXpWorth, ConsumeLevelXpWorth, and CombineLevelXpWorth here and store it as part of the Unit Definition. - Find
	 * the FactionFormationBonus here and store it as part of the Unit Definition. - Validate all unit definition factions are valid and convert them to
	 * UnitFaction objects. - Validate all store items point to a valid unit definition and duplicates don't exist. - Validate PvE map data and generate PvE
	 * enemy unit's power and health if an override is not set. Also set enemy unit levels to 1 if null. - Validate all constants are parsable
	 * <p/>
	 * Code comments labeled "INFO" are validation checks against features the game client does not support yet but the server can.
	 *
	 * @param content      the value object containing all data from GameData.xml
	 * @param allowRemoves safety check, must be enabled to remove records whose absence could break the game.
	 */

	@SuppressWarnings({ "StatementWithEmptyBody", "ConstantConditions" })
	private void validateAndPrepData( Content content, boolean allowRemoves ) {
		
	}







	// ===========================================================================================================================
	// Validate PvE maps:
	// - Make sure there are no duplicate maps.
	// - Make sure each map node use steps 1-n with no holes.
	// - Make sure each node step has an enemy squad, each with unit definitions that are valid.
	// - If the health or power are not set for units in enemy squads, calculate them from the level (if specified).
	// - If the level is not specified, set it to level 1 for displaying to players.
//	private Map<String, GameMap> validatePveMaps( List<GameMap> gameMaps, Map<String, UnitDefinition> unitDefinitionMap, Set<String> runeDefIds, Set<String> battleEnvNames ) {
//		return null;
//	}


	// ===========================================================================================================================
	// Validate Other maps:
	// - Make sure there are no duplicate maps.
	

	public VipConfig  getVipConfig(){
		return cacheManager.getVipConfig().getUnchecked(ContentService.CURRENT_VIP_KEY);
		
	}
	
	@Override
	public void setApplicationEventPublisher( ApplicationEventPublisher appEventPublisher ) {
		this.appEventPublisher = appEventPublisher;
	}

}
