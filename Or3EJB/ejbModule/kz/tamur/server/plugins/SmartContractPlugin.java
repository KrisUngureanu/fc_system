package kz.tamur.server.plugins;

import java.io.IOException;
import java.math.BigInteger;

import org.web3j.crypto.CipherException;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class SmartContractPlugin  implements SrvPlugin {
	
	private Session session;
	private SmartContract smartContract;
	
	public SmartContractPlugin() {}
		
	public void init(String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		smartContract = SmartContract.instance(contractAddress, gbdrnAddress, gbdrnPassword);
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
	
	public void approveByRn(String _rka, BigInteger _time, String _text) throws Exception {
		SmartContract.instance().approveByRn(_rka, _time, _text);
	}
	
	public void rejectByRn(String _rka, BigInteger _time, String _reason) throws Exception {
		SmartContract.instance().rejectByRn(_rka, _time, _reason);
	}
	
	public void registeredByRn(String _rka, BigInteger _time, String _text) throws Exception {
		SmartContract.instance().registeredByRn(_rka, _time, _text);
	}
}