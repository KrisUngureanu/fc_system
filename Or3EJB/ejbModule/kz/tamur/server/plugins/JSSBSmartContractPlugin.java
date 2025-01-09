package kz.tamur.server.plugins;

import java.io.IOException;

import org.web3j.crypto.CipherException;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class JSSBSmartContractPlugin  implements SrvPlugin {
	
	private Session session;
	private JSSBSmartContract smartContract;
	
	public JSSBSmartContractPlugin() {}
		
	public void init(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		smartContract = JSSBSmartContract.instance(url, contractAddress, gbdrnAddress, gbdrnPassword);
		smartContract.startListeners(session.getDsName());
	}
	
	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	
	public void checkingApprovedByGBDRN(String _documentId, String _checkingOpinion, String _paymentParams) throws Exception {
		JSSBSmartContract.instance().checkingApprovedByGBDRN(_documentId, _checkingOpinion, _paymentParams);
	}
	
	public void checkingCancelledByGBDRN(String _documentId, String _checkingOpinion) throws Exception {
		JSSBSmartContract.instance().checkingCancelledByGBDRN(_documentId, _checkingOpinion);
	}
	
	public void registeredByGbdrn(String _documentId, String _notificationId, String _finalOpinion) throws Exception {
		JSSBSmartContract.instance().registeredByGbdrn(_documentId, _notificationId, _finalOpinion);
	}
	
	public void cancelledByGbdrn(String _documentId, String _notificationId, String _finalOpinion) throws Exception {
		JSSBSmartContract.instance().cancelledByGbdrn(_documentId, _notificationId, _finalOpinion);
	}
	
	public void test() throws Exception {
		JSSBSmartContract.instance().test();
	}
}