package kz.tamur.test;

import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.cifs.or2.client.ResponseWaiter;
import com.cifs.or2.kernel.ReportNote;
import com.cifs.or2.kernel.UserSessionValue;
import com.cifs.or2.server.ServerUserSession;
import com.cifs.or2.server.Session;

import kz.tamur.comps.Constants;
import kz.tamur.or3ee.common.UserSession;
import kz.tamur.web.controller.WebController;

public class ReportController implements ResponseWaiter {
	private String reportResponse;
	private UUID uuid = UUID.randomUUID();
	private String reportId;
	
	private static final Map<String, ReportController> map = Collections.synchronizedMap(new HashMap<String, ReportController>());
	
	public ReportController(String reportId) {
		this.reportId = reportId;
	}

	public static ReportController instance(String designerUUID, String reportId) {
		String key = designerUUID + "-" + reportId;
		
		ReportController rc = map.get(key);
		if (rc == null) {
			rc = new ReportController(reportId);
			map.put(key, rc);
		}
		
		return rc;
	}
	
	public String sendToDesigner(ServerUserSession us, String cmd, String param) throws InterruptedException {
        reportResponse = null;

        Session.putResponseWaiter(this.uuid, this);
    	Session.sendNoteClustered(us, new ReportNote(new Date(), 
    			new UserSessionValue(this.uuid, UserSession.SERVER_ID), this.reportId,
    			cmd, param));

        synchronized (this) {
        	for (int i = 0; i<Constants.TIME_OUT_REPORT_WAIT_QUANTS; i++) {
            	this.wait(Constants.TIME_OUT_WEB_WAIT_QUANT);
            	if (WebController.isDestroying() || reportResponse != null)
            		break;
        	}
		}
        
        Session.getResponseWaiter(this.uuid, this.reportId);
        return reportResponse;
	}
	
    @Override
	public synchronized void responseRecieved(String response) {
    	this.reportResponse = response;
        try {
        	this.notify();
        } catch (Exception e) {
        	e.printStackTrace();
        }
	}


    @Override
    public String getReportId() {
		return reportId;
	}
}
