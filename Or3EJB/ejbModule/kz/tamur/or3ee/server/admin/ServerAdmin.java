package kz.tamur.or3ee.server.admin;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.ejb.TimerService;

/**
 * Session Bean implementation class ServerAdmin
 */
@Stateless
public class ServerAdmin implements ServerAdminRemote, ServerAdminLocal {
	
	@Resource
	TimerService timerService;

    /**
     * Default constructor. 
     */
    public ServerAdmin() {
    }
    
	public void emptyMethod() {
	}

}
