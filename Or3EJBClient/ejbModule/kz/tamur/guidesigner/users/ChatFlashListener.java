package kz.tamur.guidesigner.users;

import java.util.HashMap;
import java.util.Map;

import org.red5.client.net.rtmp.RTMPClient;
import org.red5.server.api.service.IPendingServiceCall;
import org.red5.server.api.service.IPendingServiceCallback;

import chrriis.dj.nativeswing.swtimpl.components.FlashPlayerCommandEvent;
import chrriis.dj.nativeswing.swtimpl.components.FlashPlayerListener;

public class ChatFlashListener implements FlashPlayerListener {

	RTMPClient rtmpClient = new RTMPClient();
	private boolean finished = false;
	private ChatPanel chatPanel;
	
	private String red5Host;
	private int red5Port;
	private String red5App;
	private String userUid;
	private String red5Id;
	
	public ChatFlashListener(ChatPanel cp, String red5Host, int red5Port, String red5App, String userUid) {
		super();
		this.chatPanel = cp;
		this.red5Host = red5Host;
		this.red5Port = red5Port;
		this.red5App = red5App;
		this.userUid = userUid;
	}

	@Override
	public void commandReceived(FlashPlayerCommandEvent e) {
		if ("ConnectionEstablished".equals(e.getCommand())) {
	        IPendingServiceCallback cb = new IPendingServiceCallback() {
				@Override
				public void resultReceived(IPendingServiceCall call) {
					System.out.println("connectCallback");
	                Map map = (Map) call.getResult();
	                if (map != null) {
		                String code = (String) map.get("code");
		                if ("NetConnection.Connect.Rejected".equals(code)) {
		                        System.out.printf("Rejected: %s\n", map.get("description"));
		                        rtmpClient.disconnect();
		                } else if ("NetConnection.Connect.Success".equals(code)) {
		                    System.out.printf("OK: %s\n", map.get("description"));
		                    getUserIdRed5();
		                    getUsersList();
		                } else {
		                    System.out.printf("Unhandled response code: %s\n", code);
		                }
	                } else {
	                    System.out.printf("No connection to Red5!");
	                }
				}
			};
		  
			Map<String, Object> connParams = new HashMap<String, Object>();
			connParams.put("app", red5App);
			connParams.put("tcUrl", "rtmp://" +red5Host + ":" + red5Port + "/" + red5App);
            
			Object[] callArgs = new Object[1];
			callArgs[0] = userUid;
			
	        rtmpClient.connect(red5Host, red5Port, connParams, cb, callArgs);
	        
	        chatPanel.setRtmpClient(rtmpClient);
		} else if ("camera".equals(e.getCommand())) {
			String uid = (String) e.getParameters()[0];
			String status = (String) e.getParameters()[1];
			
			Red5User user = chatPanel.getRed5User(uid);
			user.cameraSwitchedOn = "on".equals(status);
			chatPanel.tableChanged();
		} else if ("watching".equals(e.getCommand())) {
			String uid = (String) e.getParameters()[0];
			String status = (String) e.getParameters()[1];
			
			Red5User user = chatPanel.getRed5User(uid);
			user.watchingYou = "on".equals(status);
			chatPanel.tableChanged();
		}
	}
	
	private void getUserIdRed5() {
        rtmpClient.invoke("getUserId", new IPendingServiceCallback() {
			@Override
			public void resultReceived(IPendingServiceCall call) {
                String res = (String) call.getResult();
                if (res != null) {
                	red5Id = res;
                } else {
                    System.out.printf("ERROR!!!");
                }
			}
		});
	}

	private void getUsersList() {
        rtmpClient.invoke("getUserList", new IPendingServiceCallback() {
			@Override
			public void resultReceived(IPendingServiceCall call) {
                Map<String, Object> res = (Map<String, Object>) call.getResult();
                if (res != null) {
                	for (String uid : res.keySet()) {
                		Red5User user = chatPanel.getRed5User(uid);
                		if (user != null) {
                			Map<String, Object> serverUser = (Map<String, Object>) res.get(uid);
                			user.watchingYou = "true".equals(serverUser.get("isWatching"));
                			user.cameraSwitchedOn = "on".equals(serverUser.get("cam"));
                		}
                	}
        			chatPanel.tableChanged();
                } else {
                    System.out.printf("ERROR!!!");
                }
			}
		});
	}
}
