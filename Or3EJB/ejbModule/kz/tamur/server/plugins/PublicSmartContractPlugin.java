package kz.tamur.server.plugins;

import java.io.IOException;

import org.web3j.crypto.CipherException;

import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvPlugin;

public class PublicSmartContractPlugin  implements SrvPlugin {
	
	private Session session;
	private PublicSmartContract smartContract;
	
	public PublicSmartContractPlugin() {}
		
	public void init(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		smartContract = PublicSmartContract.instance(url, contractAddress, gbdrnAddress, gbdrnPassword);
	}
	
	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
	}
	
	public void createRecord(String rightHash) throws Exception {
		smartContract.createRecord(rightHash);
	}
	
	public void deleteRecord(String rightHash) throws Exception {
		smartContract.createRecord(rightHash);
	}
	
	public boolean getRecord(String rightHash) throws Exception {
		return smartContract.getRecord(rightHash);
	}
}