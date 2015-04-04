var PM = PM || {};
PM.V = PM.V || {};

/**
 * The view & controller for store transactions.
 */
PM.V.StoreTransactions = Backbone.View.extend({
	tagName: 'table',
	_isLoaded: false,
	_isLoading: false,
	_isRemovedState: false,
	events: {
		'click .clickToRetry': 'load'
	},
	initialize: function (options) {
		_.bindAll(this, 'render', '_fetchFailed');
		
		this.playerId = options.playerId;
	},
	load: function () {
		if (this._isLoaded || this._isLoading)
			return;
		
		this._isLoading = true;
		this.$el.empty()
			.append('<tr><th>Store</th><th>Store Transaction Id</th><th>Item Id</th><th>Est. USD Value</th><th>Store Processed</th><th>Game Server Processed</th></tr>')
			.append('<tr class="loadingStatus"><td colspan="6">Loading... </td></tr>')
			.appendTo($('#playerPurchases'))
		
		this.collection = new PM.C.StoreTransactions(null, {playerId: this.playerId});
		this.collection.fetch({success: this.render, error: this._fetchFailed});
	},
	render: function () {
		if (this._isRemovedState) {
			return this;
		}
		
		this._isLoaded = true;
		this._isLoading = false;
		
		$('.loadingStatus', this.$el).remove();
		
		if (this.collection.length === 0) {
			$('<tr class="norecordsrow"><td colspan="6">No existing records could be found.</td></tr>').appendTo(this.$el);
		} else {
			this.collection.each(function (model) {
				this._buildRow(model).appendTo(this.$el);
			}, this);
		}
		
		return this;
	},
	remove: function () {
		this._isRemovedState = true;
		this.$el.remove();
		return this;
	},
	_buildRow: function (model) {
		return $('<tr>' + 
					'<td>' + model.get('storePlatform') + '</td>' + 
					'<td>' + model.get('externalTransactionId') + '</td>' + 
					'<td>' + model.get('storeId') + '</td>' + 
					'<td>' + (model.get('dollarValue') || '---') + '</td>' + 
					'<td>' + new Date(model.get('storePurchasedTime')).format('m/dd/yyyy, h:MM:sstt Z') + '</td>' + 
					'<td>' + new Date(model.get('initiatedTime')).format('m/dd/yyyy, h:MM:sstt Z') + '</td>' + 
				'</tr>');
	},
	_fetchFailed: function () {
		this._isLoading = false;
		$('.loadingStatus > td', this.$el).addClass('clickToRetry').text('Loading... Failed! [click to retry]');
		return this;
	}
});
