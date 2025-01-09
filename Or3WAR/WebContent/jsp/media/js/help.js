$(function() {
	$('#help_body').load(window.contextName + '/main?guid=' + guid + '&cmd=getHelpNoteContent&hid=' + hid + '&uid=' + uid, function() {
		console.log('loaded 000');
	});
	initHelp();
	
	function initHelp() {
		$('#help_layout').layout();
		$('#helpTree').tree({
			onClick: function(node){
				$('#help_body').load(window.contextName + '/main?guid=' + guid + '&cmd=getHelpNoteContent&hid=' + hid + '&uid=' + uid + '&id=' + node.id, function() {
					console.log('loaded ' + node.id);
				});
			}
		});
	}

});