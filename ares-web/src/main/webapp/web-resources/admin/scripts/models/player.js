var PM = PM || {};
PM.M = PM.M || {};

/**
 * A Player model.
 */
PM.M.Player = Backbone.Model.extend({
	defaults: {
		id: '',
	    n: '',				// Name
	    f: '',				// Facebook Id
	    synergy_id: '',		// Synergy Id
	    guestId: '',		// Guest Id
	    google_id: '',		// Google Id
	    game_center_id: '',	// Game Center Id
	    synergySellId: '',	// Synergy Sell Id
	    x: 0,				// XP
	    l: 1,				// Level
	    s: 0,				// Stamina
	    e: 0,				// Energy
	    t: 0,				// Trophies
	    ft: 0,				// First time user experience state
	    ftc: true,			// First time user experience complete
	    fts: 0,				// First time user experience step
	    dp: '',				// Device platform
	    abi: '',			// App Bundle Id
	    ncc: 0,				// Name Change Count
	    tp: false,			// Test player
	    j: '',				// Joined time (aka. account creation timestamp)
	    winStreak: 0,		// PvP current win streak
	    wins: 0,			// PvP battle wins
	    losses: 0,			// PvP battle losses
	    initiatedBattles: 0,// Initiated PvP battles
	    lastMatchedOpponent: null,
	    language: '',		// Language
	    p: 0,				// Last push note time
	    help: false,		// Has submitted a help ticket
	    as: 0,				// Amount of real money spent
	    ns: 0,				// Number of real money purchases
	    gem_fg: 0,			// Gems from in game actions
	    gem_fo: 0,			// Gems from offers
	    gem_fm: 0,			// Gems from MTX purchases
	    gem_tr: 0,			// Total gems received
	    lsof: 0,			// Last share offer time
	    ea: false			// Has accepted EULA
	},
	url: function () {
		return '/services/players/' + this.id;
	},
	parse: function (response) {
		// An extra data filtering is required as the data from the REST service differs from what Backbone expects
		response = response || {};
		return response.player || response;
	}
});
