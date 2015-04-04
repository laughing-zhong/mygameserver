var PM = PM || {};
PM.M = PM.M || {};

/**
 * A StoreTransaction model.
 */
PM.M.StoreTransaction = Backbone.Model.extend({
	defaults: {
		//id: '',					// Auto-generated on the server side
		storeId: '',				// This is actually the itemId
		dollarValue: 0,				// This is an estimated USD value
		externalTransactionId: '',	// External Store's Transaction ID (iOS/Android Store)
		storePlatform: '',			// Store Platform
		initiatedTime: 0,			// Timestamp (ms) when native store processed the purchase
		storePurchasedTime: 0		// Timestamp (ms) when game server processed the purchase
	}
});
