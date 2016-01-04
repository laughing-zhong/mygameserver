var PM = PM || {};
PM.C = PM.C || {};

/**
 * A collection for holding support events.
 * SupportEvent model must be included before including this file.
 */
PM.C.SupportHistory = Backbone.Collection.extend({
	model: PM.M.SupportEvent,
	search: '',
	initialize: function (models, options) {
		this.id = options.playerId
	},
	url: function () {
		return '/services/support/' + this.id;
	},
	comparator: function (p1, p2) {
		if (p1.get('timestamp') === p2.get('timestamp'))
			return 0;
		return p1.get('timestamp') > p2.get('timestamp') ? -1 : 1;	// Sort in descending order
	},
	parse: function (response) {
		// An extra data filtering is required as the data from the REST service differs from what Backbone expects
		response = response || {};
		return response.supportEventList || [];
	}
});
