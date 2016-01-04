package com.ares.app.content.service;


import com.ares.app.content.Content;
import com.ares.app.content.vip.VipConfig;


/**
 * Service responsible for publishing and retrieving content from the game.
 *
 * @author m.mcbride
 */
 
public interface ContentService {

	static final String CURRENT_VIP_KEY                  ="current";

	
	/**
	 * 
	 * @param bundle
	 */
//	void publishLocalizationBundle( LocalizationBundleDO bundle );
//	
//	void publishLocalizationQuestBundle(LocalizationQuestBundleDO bundle);
//	
//	void publishActivitionContent(ActivitionCode  activitionCode);
//	void publishSmRewardContent(SmReward smReward);
	
	/**
	 * 
	 * @param id
	 * @return loc bundles
	 */
//	LocalizationBundleDO getLocalizationBundle (String id);
//	
//	LocalizationQuestBundleDO getLocalizationQuest(String id);
	
	/**
	 * Publish content to the game server. The content will include the unit definitions
	 * and the store catalog. Those two items will be independently versioned and the client
	 * will specify the version.
	 *
	 * @param content content to validate and publish
	 */
	void publishContent( Content content );

	void publishContent( Content content, boolean allowRemoves );

	void publishContent( Content content, byte[] contentData, String revision );

	void publishContent( Content content, byte[] contentData, String revision, boolean allowRemoves, boolean silent );


    VipConfig                  	getVipConfig();

   
}
