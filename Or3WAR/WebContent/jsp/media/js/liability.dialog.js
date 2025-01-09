function signBtnPressed() {

	let xml = '<la><liabilityText>' + b64EncodeUnicode(document.getElementById('liabilityText').innerHTML) + '</liabilityText></la>';

	signXml(BASICS, SIGN, xml).then(data2 => {
		if (data2.responseObject) {
		
			let signedData = data2.responseObject;
			par = {
				cmd: "signLiability",
				signedData: signedData,
				liabilityText: document.getElementById('liabilityText').innerHTML,
				liabilityObjectUID: liabilityObjectUID,
				signDate: (new Date).getTime(),
				rnd: rnd(),
				guid: guid,
				isNCAMode: 1,
				json: 1
			};

        	$.post(url, par, function(data) {
        		if (data.signResult == 0) {
        			alert("Ошибка при подписании соглашения!" + (data.errorMessage != null ? (" " + data.errorMessage) : ""));
        		} else {
					alert("Соглашение успешно подписано!");
					$('#liabilityContentDiv').dialog("close");
        			window.location.replace(window.contextName + "/jsp/index.jsp?guid=" + guid + "&rnd=" + rnd());
        		}
        		isLASingOp = false;
        	}, 'json');
			
		} else {
			let error = (data2.message === 'action.canceled')
				? 'Действие отменено пользователем'
				: 'Код ошибки: ' + data2.code + ' Сообщение: ' + data2.message;
			console.error("msg text: " + error);
			alert(error);
			$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
			$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
		}
	}, error => {
		console.error("error text: " + error);
		alert(error);
		$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
		$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
		$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");
	});

}

function cancelBtnPressed() {
	$('#loginBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
	$('#loginECPBtn').removeClass("btn-disabled").removeAttr("disabled").html('Войти в систему');
	$('#resetBtn').removeClass("btn-disabled").removeAttr("disabled");

	var par = {"json": 1, "cmd": "ext", "guid": guid};
	$.post(url + "?rnd=" + rnd(), par, function(data) {}, 'json');
	$('#liabilityContentDiv').dialog("close");
}

