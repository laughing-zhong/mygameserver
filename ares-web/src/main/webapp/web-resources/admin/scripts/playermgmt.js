var PM = PM || {};

/**
 * This is the initialization for the Player Management Backbone application.
 * 
 * We use a global namespace of PM (Player Management), and sub-namespaces:
 *  - PM.M - Model Classes
 *  - PM.V - View Classes
 *  - PM.C - Collection Classes
 *  - PM.B - Bootstrap data in the HTML headers
 */
$(function () {
	var player = null;
	var playerIdInput = $('#playerId');
	var playerSearchForm = $('#playerLookupContainer > form');
	
	playerSearchForm.submit(function (eventObject){
		if (playerIdInput.val().trim() === '') {
			alert('Please enter a Player ID');
			return false;
		}
		
		if (player !== null)
			player.remove();
		
		player = new PM.V.Player({playerId: playerIdInput.val()});
		return false;
	});
	
	// Build the drop down menus from bootstrap data
	if (_.isArray(PM.B.Packs)) {
		PM.B.Packs.sort();
		var givePackDropdown = $('#givePackDropDown');
		$.each(PM.B.Packs, function (index, value) {
			givePackDropdown.append($('<option id="' + value + '">' + value + '</option>'));
		});
	}
	if (_.isObject(PM.B.Units)) {
		var giveUnitDropdown = $('#giveUnitDropDown');
		var rarityNames = ['', 'Common', 'Uncommon', 'Rare', 'Epic', 'Legendary'];
		var groupedUnits = _.groupBy(PM.B.Units, function (a, b) {return a.r});
		var i, optGroup, optGroupGiant;
		for (i = 5; i > 0; i--) {
			optGroup = '<optgroup label="' + rarityNames[i] + '">';
			optGroupGiant = '<optgroup label="' + rarityNames[i] + ' Giant">';
			$.each(groupedUnits[i+''].sort(function (a, b) {
				if (a.g !== b.g)
					return a.g === '1' ? -1 : 1;
				return a.n.localeCompare(b.n);
			}), function (index, value) {
				if (value.g === '1')
					optGroupGiant += '<option id="' + value.i + '">' + value.n + '</option>';
				else
					optGroup += '<option id="' + value.i + '">' + value.n + '</option>';
			});
			optGroup += '</optgroup>';
			optGroupGiant += '</optgroup>';
			giveUnitDropdown.append($(optGroupGiant));
			giveUnitDropdown.append($(optGroup));
		}
	}
	if (_.isArray(PM.B.Runes)) {
		PM.B.Runes.sort();
		var giveRuneDropdown = $('#giveRuneDropDown');
		$.each(PM.B.Runes, function (index, value) {
			giveRuneDropdown.append($('<option id="' + value + '">' + value + '</option>'));
		});
	}
});
