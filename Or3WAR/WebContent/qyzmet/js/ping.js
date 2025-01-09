import {Util} from './util.js';

export class Ping {
	pinger;
    mainUrl = "";
    breadcrumpsVisible = true;
    lostRequests = Array();
    editingTable = undefined;
    ifcController;
    editIndex = {};

	constructor() {
        // this.mainUrl = url;
        // this.ifcController = ifcController;
        // this.editingTable = editingTable;
	}
	
	init() {
        this.ping();
	}

    ping() {
        if (this.pinger != null) clearTimeout(this.pinger);
        this.pingServer();
        this.pinger = setTimeout(this.ping.bind(this), 30000);
    }

    pingServer() {
        var url = window.mainUrl + "&ping&rnd=" + Util.rnd();
        var beginTime = (new Date).getTime();
        $.post(url, function(data) {
            var pingTime = (new Date).getTime() - beginTime;
            $('#ping').text('' + pingTime);
        }, 'json');
    }

}