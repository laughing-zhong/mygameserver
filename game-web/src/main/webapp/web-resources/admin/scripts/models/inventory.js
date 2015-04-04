var PM = PM || {};
PM.M = PM.M || {};

/**
 * A Inventory model.
 */
PM.M.Inventory = Backbone.Model.extend({
	defaults: {
		'1': 0,		// GEMS
		'4': 0		// GOLD
	},
	url: function () {
		return '/services/inventory/' + this.id;
	},
	parse: function (response) {
		return response || {};
	}
});

PM.M.Inventory.GEM = 1;
PM.M.Inventory.GOLD = 4;
PM.M.Inventory.UNITS_AND_CONSUMABLES = 10;
PM.M.Inventory.UNIT = 21;
PM.M.Inventory.RUNE = 22;
