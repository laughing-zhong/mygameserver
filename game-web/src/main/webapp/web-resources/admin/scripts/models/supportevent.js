var PM = PM || {};
PM.M = PM.M || {};

/**
 * A SupportEvent model.
 */
PM.M.SupportEvent = Backbone.Model.extend({
	defaults: {
		//id: '',		// Auto-generated on the server side
		supportType: 0,	// Support type
		itemType: 0,	// Item type (1 = GEM, 4 = GOLD)
		itemId: '',		// Item Id
		quantity: 0		// Battle outcome reason
		//accountId: '',	// Support's account ID
		//comment: '',	// Comment
		//timestamp: 0,	// Timestamp (ms)
		//subSupportEvents: []
	},
	initialize: function (attributes, options) {
		this.playerId = options.playerId;
	},
	url: function () {
		return '/services/support/' + this.playerId + (this.isNew() ? '' : '/' + this.id);
	},
	parse: function (response) {
		// An extra data filtering is required as the data from the REST service differs from what Backbone expects
		response = response || {};
		return response.supportEvent || response;
	}
});

PM.M.SupportEvent.CREDIT = 1;
PM.M.SupportEvent.DETAIL = 2;
PM.M.SupportEvent.COMMENT = 3;
