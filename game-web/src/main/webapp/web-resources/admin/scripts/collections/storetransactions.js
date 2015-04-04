var PM = PM || {};
PM.C = PM.C || {};

/**
 * A collection for holding store transactions.
 * StoreTransaction model must be included before including this file.
 */
PM.C.StoreTransactions = Backbone.Collection.extend({
	model: PM.M.StoreTransaction,
	search: '',
	initialize: function (models, options) {
		this.id = options.playerId;
	},
	url: function () {
		return '/services/store/history/' + this.id;
	},
	comparator: function (p1, p2) {
		if (p1.get('storePurchasedTime') === p2.get('storePurchasedTime'))
			return 0;
		return p1.get('storePurchasedTime') > p2.get('storePurchasedTime') ? -1 : 1;	// Sort in descending order
	},
	parse: function (response) {
		// An extra data filtering is required as the data from the REST service differs from what Backbone expects
		response = response || {};
		return response.storeTransactionList || [];
	}
});
