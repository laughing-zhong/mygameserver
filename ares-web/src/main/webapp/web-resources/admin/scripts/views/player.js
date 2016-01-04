var PM = PM || {};
PM.V = PM.V || {};

/**
 * The view for a Player.
 * 
 * By design, the objects are updated locally whether or not they are persisted on the server-side.
 * If a update fails, an alert icon will show besides the record to allow retrying.
 */
PM.V.Player = Backbone.View.extend({
	_storeTransactions: null,
	_supportHistory: null,
	_isRemovedState: false,
	id: 'playerView',
	events: {
		'submit #goldBalanceContainer form': '_creditGold',
		'submit #gemBalanceContainer form': '_creditGems',
		'submit #givePackContainer form': '_givePack',
		'submit #giveUnitContainer form': '_giveUnit',
		'submit #giveRuneContainer form': '_giveRune',
	},
	initialize: function (options) {
		_.bindAll(this, 'render', '_fetchFailed');
		
		this.setElement($('#playerContainer'));
		$('#playerDetails').text('Loading... ');
		$('#loadingError').addClass('hide');
		
		this.playerId = options.playerId;
		this.model = new PM.M.Player({id: this.playerId});
		this.model.fetch({success: this.render, error: this._fetchFailed});
	},
	render: function () {
		if (this._isRemovedState) {
			return this;
		}
		
		// In case we grabbed the player using a FB/Guest/Synergy/GameCenter/Google ID, we need the actual player ID for all further operations.
		this.playerId = this.model.id;
		
		$('#playerDetails').empty();
		
		this._playerInventoryContainer = $('#playerInventory');
		this._playerInventoryContainer.removeClass('hide').appendTo(this.$el);
		
		this._playerDetailsContainer = $('#playerDetails');
		this._playerDetailsContainer.append(
			'<ul>' + 
				'<li><a href="#playerBasic">Basic Info</li>' + 
				'<li><a href="#playerPurchases">Purchase History</li>' + 
				'<li><a href="#playerSupportHistory">Support History</li>' + 
			'</ul>' + 
			'<div>' + 
				'<div id="playerBasic" />' + 
				'<div id="playerPurchases" />' + 
				'<div id="playerSupportHistory" />' + 
			'</div>');
		this._playerDetailsContainer.tabs({
			beforeActivate: _.bind(function (event, ui) {
				switch (ui.newPanel.attr('id')) {
				case 'playerBasic':
					break;
				case 'playerPurchases':
					this._storeTransactions.load();
					break;
				case 'playerSupportHistory':
					this._supportHistory.load();
					break;
				}
			}, this)
		}).appendTo(this.$el);
		
		$('#playerBasic').append($('<table>')
			.append('<tr><td>Player Name</td><td>' + this.model.get('n') + '</td></tr>')
			//.append('<tr><td>Facebook ID</td><td>' + (this.model.get('f') || 'n/a') + '</td></tr>')
			.append('<tr><td>Synergy ID</td><td>' + (this.model.get('synergy_id') || 'n/a') + '</td></tr>')
			.append('<tr><td>Guest ID</td><td>' + (this.model.get('guestId') || 'n/a') + '</td></tr>')
			//.append('<tr><td>Google ID</td><td>' + (this.model.get('google_id') || 'n/a') + '</td></tr>')
			//.append('<tr><td>Game Center ID</td><td>' + (this.model.get('game_center_id') || 'n/a') + '</td></tr>')
			.append('<tr><td>Device Platform</td><td>' + this.model.get('dp') + '</td></tr>')
			.append('<tr><td>Language</td><td>' + this.model.get('language') + '</td></tr>')
			.append('<tr><td>Player Level</td><td>' + this.model.get('l') + ' [' + this.model.get('x') +' xp]</td></tr>')
			.append('<tr><td>Trophies</td><td>' + this.model.get('t') + '</td></tr>')
			.append('<tr><td>PvP Wins / Losses</td><td>' + this.model.get('wins') + ' / ' + this.model.get('losses') + '</td></tr>')
			.append('<tr><td>Stamina Full Time</td><td>' + new Date(this.model.get('s') * 1000).format('m/dd/yyyy, h:MM:sstt Z') + '</td></tr>')
			.append('<tr><td>Energy Full Time</td><td>' + new Date(this.model.get('e') * 1000).format('m/dd/yyyy, h:MM:sstt Z') + '</td></tr>')
			.append('<tr><td>Real Money Spent (estimated)</td><td>US $' + this.model.get('as') + '</td></tr>')
			.append('<tr><td>Gems from game</td><td>' + this.model.get('gem_fg') + '</td></tr>')
			.append('<tr><td>Gems from offers</td><td>' + this.model.get('gem_fo') + '</td></tr>')
			.append('<tr><td>Gems from MTX</td><td>' + this.model.get('gem_fm') + '</td></tr>')
			.append('<tr><td>Gems received total</td><td>' + this.model.get('gem_tr') + '</td></tr>')
			.append('<tr><td>FTUE Step</td><td>' + this.model.get('fts') + '</td></tr>')
			.append('<tr><td>FTUE Completed</td><td>' + this.model.get('ftc') + '</td></tr>')
			//.append('<tr><td>Test Player</td><td>' + this.model.get('tp') + '</td></tr>')
			.append('<tr><td>Join Date</td><td>' + new Date(this.model.get('j') * 1000).format('m/dd/yyyy, h:MM:sstt Z') + '</td></tr>'));
		
		this._loadPlayerInventory();
		this._initPlayerPurchases();
		this._initPlayerSupportHistory();
		
		return this;
	},
	_fetchFailed: function () {
		$('#playerDetails').text('Loading... Failed!');
		return this;
	},
	_loadPlayerInventory: function () {
		var inventoryModel = new PM.M.Inventory({id: this.playerId});
		inventoryModel.fetch({
			success: _.bind(function (model) {
				if (this._isRemovedState) {
					return;
				}
				
				$('#goldBalance').text(model.get(PM.M.Inventory.GOLD));
				$('#gemBalance').text(model.get(PM.M.Inventory.GEM));
			}, this), 
			error: function () {
				$('#loadingError').removeClass('hide');
			}
		});
		
		return this;
	},
	_initPlayerPurchases: function () {
		if (this._storeTransactions !== null) {
			this._storeTransactions.remove();
		}
		this._storeTransactions = new PM.V.StoreTransactions({playerId: this.playerId});
	},
	_initPlayerSupportHistory: function () {
		if (this._supportHistory !== null) {
			this._supportHistory.remove();
		}
		this._supportHistory = new PM.V.SupportHistory({playerId: this.playerId});
	},
	_creditGold: function () {
		var val = $('#creditGoldAmount').val().trim();
		if (!val || isNaN(+val) || !isFinite(val)) {
			alert('Credit value invalid.');
			return false;
		}
		
		var newEvent = new PM.M.SupportEvent({
			supportType: PM.M.SupportEvent.CREDIT,
			itemType: PM.M.Inventory.GOLD,
			quantity: val
		}, {
			playerId: this.playerId
		});
		newEvent.save(null, {
			success: _.bind(function (model) {
				this._supportHistory.add(model);
				this._loadPlayerInventory();
			}, this),
			error: _.bind(function () {
				alert('An error occured while crediting gold to player');
			}, this)
		});
		return false;
	},
	_creditGems: function () {
		var val = $('#creditGemsAmount').val().trim();
		if (!val || isNaN(+val) || !isFinite(val)) {
			alert('Credit value invalid.');
			return false;
		}
		
		var newEvent = new PM.M.SupportEvent({
			supportType: PM.M.SupportEvent.CREDIT,
			itemType: PM.M.Inventory.GEM,
			quantity: val
		}, {
			playerId: this.playerId
		});
		newEvent.save(null, {
			success: _.bind(function (model) {
				this._supportHistory.add(model);
				this._loadPlayerInventory();
			}, this),
			error: _.bind(function () {
				alert('An error occured while crediting gems to player');
			}, this)
		});
		return false;
	},
	_givePack: function () {
		var val = $('#givePackDropDown').children(":selected").attr("id");
		if (!val) {
			return false;
		}
		
		var newEvent = new PM.M.SupportEvent({
			supportType: PM.M.SupportEvent.CREDIT,
			itemType: PM.M.Inventory.UNITS_AND_CONSUMABLES,
			itemId: val,
			quantity: 1
		}, {
			playerId: this.playerId
		});
		newEvent.save(null, {
			success: _.bind(function (model) {
				this._supportHistory.add(model);
			}, this),
			error: _.bind(function () {
				alert('An error occured while giving ' + val + ' to player');
			}, this)
		});
		return false;
	},
	_giveUnit: function () {
		var val = $('#giveUnitDropDown').find("option:selected").attr("id");
		if (!val) {
			return false;
		}
		
		var newEvent = new PM.M.SupportEvent({
			supportType: PM.M.SupportEvent.CREDIT,
			itemType: PM.M.Inventory.UNIT,
			itemId: val,
			quantity: 1
		}, {
			playerId: this.playerId
		});
		newEvent.save(null, {
			success: _.bind(function (model) {
				this._supportHistory.add(model);
			}, this),
			error: _.bind(function () {
				alert('An error occured while giving ' + val + ' to player');
			}, this)
		});
		return false;
	},
	_giveRune: function () {
		var val = $('#giveRuneDropDown').children(":selected").attr("id");
		if (!val) {
			return false;
		}
		
		var newEvent = new PM.M.SupportEvent({
			supportType: PM.M.SupportEvent.CREDIT,
			itemType: PM.M.Inventory.RUNE,
			itemId: val,
			quantity: 1
		}, {
			playerId: this.playerId
		});
		newEvent.save(null, {
			success: _.bind(function (model) {
				this._supportHistory.add(model);
			}, this),
			error: _.bind(function () {
				alert('An error occured while giving ' + val + ' to player');
			}, this)
		});
		return false;
	},
	remove: function () {
		this._isRemovedState = true;
		this.undelegateEvents();
		
		if (this._storeTransactions)
			this._storeTransactions.remove();
		if (this._supportHistory)
			this._supportHistory.remove();
		
		if (this._playerDetailsContainer)
			this._playerDetailsContainer.tabs('destroy').empty();
		$('#goldBalance').empty();
		$('#gemBalance').empty();
		$('#playerInventory').addClass('hide');
		
		return this;
	}
});
