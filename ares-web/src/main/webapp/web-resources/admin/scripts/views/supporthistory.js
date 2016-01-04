var PM = PM || {};
PM.V = PM.V || {};

/**
 * The view & controller for customer support history.
 */
PM.V.SupportHistory = Backbone.View.extend({
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
			.append('<tr><th>Action</th><th>Details</th><th>CS Agent</th><th>Timestamp</th></tr>')
			.append('<tr class="loadingStatus"><td colspan="4">Loading... </td></tr>')
			.appendTo($('#playerSupportHistory'));
		
		this._fetchFailed();
		this.collection = new PM.C.SupportHistory(null, {playerId: this.playerId});
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
			$('<tr class="norecordsrow"><td colspan="4">No existing records could be found.</td></tr>').appendTo(this.$el);
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
	add: function (model) {
		if (!this._isLoaded)
			return this;
		
		$('.norecordsrow', this.$el).remove();
		this.$el.find("tr:first").after(this._buildRow(model));
		return this;
	},
	_buildRow: function (model) {
		var details = '';
		var supportType = '';
		var allowWrapDetails = false;
		switch (model.get('supportType')) {
		case PM.M.SupportEvent.CREDIT:
			supportType = (model.get('quantity') < 0 ? 'Debit' : 'Credit');
			switch (model.get('itemType')) {
			case PM.M.Inventory.GEM:
				details = (model.get('quantity') < 0 ? 'Debited from' : 'Credited') + ' player <span class="gems">' + Math.abs(model.get('quantity')) + ' Gems</span>.';
				break;
			case PM.M.Inventory.GOLD:
				details = (model.get('quantity') < 0 ? 'Debited from' : 'Credited') + ' player <span class="gold">' + Math.abs(model.get('quantity')) + ' Gold</span>.';
				break;
			case PM.M.Inventory.UNITS_AND_CONSUMABLES:
				details = 'Gave player a <span class="pack">' + model.get('itemId') + '</span> pack. Contents: <ul class="packContents">';
				$.each(model.get('subSupportEvents'), function (index, value) {
					switch (value.itemType) {
					case PM.M.Inventory.UNIT:
						details += '<li>Unit: <span class="unit">' + PM.B.Units[value.itemId].n + '</span></li>';
						break;
					case PM.M.Inventory.RUNE:
						details += '<li>Rune: <span class="rune">' + value.itemId + '</span></li>';
						break;
					}
				});
				details += '</ul>';
				break;
			case PM.M.Inventory.UNIT:
				details = 'Gave player a <span class="unit">' + PM.B.Units[model.get('itemId')].n + '</span> unit.';
				break;
			case PM.M.Inventory.RUNE:
				details = 'Gave player a <span class="rune">' + model.get('itemId') + '</span> rune.';
				break;
			}
			break;
		case PM.M.SupportEvent.COMMENT:
			supportType = 'Comment';
			details = model.get('comment');
			allowWrapDetails = true;
			break;
		}
		return $('<tr>' + 
					'<td>' + supportType + '</td>' + 
					'<td' + (allowWrapDetails ? ' class="allowWrap"' : '') + '>' + details + '</td>' + 
					'<td>' + (model.get('accountId') || '<span class="system">[system]</span>') + '</td>' + 
					'<td>' + new Date(model.get('timestamp')).format('m/dd/yyyy, h:MM:sstt Z') + '</td>' + 
				'</tr>');
	},
	_fetchFailed: function () {
		this._isLoading = false;
		$('.loadingStatus > td', this.$el).addClass('clickToRetry').text('Loading... Failed! [click to retry]');
		return this;
	}
});
