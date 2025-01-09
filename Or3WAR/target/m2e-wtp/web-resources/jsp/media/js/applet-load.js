$(document).ready(function () {
});

var appletLoading = false;

function loadApplet(name) {
	if (navigator.javaEnabled()) {
		if (name == 'ECPApplet') {
			loadECPApplet();
		} else if (name == 'IdCardReaderApplet') {
			loadIdCardReaderApplet();
		} else if (name == 'UCGOApplet') {
			loadUCGOApplet();
		} else {
			alert("Апплет " + name + "не найден!");
		}
	}
}

function loadECPApplet() {
	if (document.getElementById('ECPApplet') == null) {
		appletLoading = true;
		block();
		$("body").append($('<applet width="1" height="1" style="position:absolute;top:0;left:0;"'
						+ ' codebase="."'
						+ ' code="kz.crypto.CryptoApplet"'
						+ ' archive="commons-logging-1.1.1.jar,xmlsec-1.4.4.jar,kalkancrypt-0.1.1.jar,kalkancrypt_xmldsig-0.1.jar,crypto-applet-kalkan-1.4.jar"'
						+ ' type="application/x-java-applet"'
						+ ' mayscript="true"'
						+ ' id="ECPApplet" name="ECPApplet">'
						+ '<param name="code" value="kz.crypto.CryptoApplet"/>'
						+ '<param name="archive" value="commons-logging-1.1.1.jar,xmlsec-1.4.4.jar,kalkancrypt-0.1.1.jar,kalkancrypt_xmldsig-0.1.jar,crypto-applet-kalkan-1.4.jar"/>'
						+ '<param name="mayscript" value="true"/>'
						+ '<param name="scriptable" value="true"/>'
						+ '<param name="lang" value="' + langCode + '"/>'
						+ '<param name="separate_jvm" value="true"/>'
						+ '</applet>'
		));
	}
}

function loadIdCardReaderApplet() {
	if (document.getElementById('IdCardReaderApplet') == null) {
		appletLoading = true;
		$("body").append($('<applet width="1" height="1"'
						+ ' style="position:absolute;top:0;left:0;"'
						+ ' codebase="."'
						+ ' code="kz.tamur.or3.idcard.ReaderApplet"'
					    + ' archive="bcprov-jdk15on-153.jar,cert-cvc.jar,jdeli.jar,idcard-reader-1.0.jar"'
					    + ' type="application/x-java-applet"'
					    + ' mayscript="true"'
					    + ' id="IdCardReaderApplet" name="IdCardReaderApplet">'
					    + '<param name="code" value="kz.tamur.or3.idcard.ReaderApplet"/>'
					    + '<param name="archive" value="bcprov-jdk15on-153.jar,cert-cvc.jar,jdeli.jar,idcard-reader-1.0.jar"/>'
					    + '<param name="mayscript" value="true"/>'
					    + '<param name="scriptable" value="true"/>'
					    + '<param name="lang" value="' + langCode + '"/>'
					    + '<param name="separate_jvm" value="true"/>'
					    + '</applet>'));
	}
}

function loadUCGOApplet() {
	if (document.getElementById('UCGOApplet') == null) {
		appletLoading = true;
		block();
		$("body").append($('<applet width="1" height="1" style="position:absolute;top:0;left:0;"'
						+ ' codebase="."'
						+ ' code="kz.crypto.gamma.CryptoApplet"'
						+ ' archive="crypto_applet_ucgo_hardware-1.0.jar,crypto-hardware-1.0.jar"'
						+ ' type="application/x-java-applet"'
						+ ' mayscript="true"'
						+ ' id="UCGOApplet" name="UCGOApplet">'
						+ '<param name="code" value="kz.crypto.gamma.CryptoApplet"/>'
						+ '<param name="archive" value="crypto_applet_ucgo_hardware-1.0.jar,crypto-hardware-1.0.jar"/>'
						+ '<param name="mayscript" value="true"/>'
						+ '<param name="scriptable" value="true"/>'
						+ '<param name="lang" value="' + langCode + '"/>'
						+ '<param name="separate_jvm" value="true"/>'
						+ '</applet>'
		));
	}
}

function unblock() {
	appletLoading = false;
	$.unblockUI();
}

function block() {
    $.blockUI({
        message: '<img src="' + window.contextName + '/jsp/media/img/loading.gif" /><br/>Подождите, идет загрузка Java-апплета...',
        css: {
            border: 'none',
            padding: '15px',
            backgroundColor: '#000',
            '-webkit-border-radius': '10px',
            '-moz-border-radius': '10px',
            opacity: .5,
            color: '#fff'
        }
    });
}
