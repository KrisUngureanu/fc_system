package kz.tamur.server.plugins;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.smartcontracts.JSSBContract;
import kz.tamur.smartcontracts.PublicContract;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;
import org.web3j.utils.Numeric;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

public class PublicSmartContract {
	
    private static final Log LOG = LogFactory.getLog(PublicSmartContract.class);

    private static PublicSmartContract instance;
	
    private final String gbdrnAddress;
    private final String gbdrnPassword;
    
    private final Admin invAdmin;
    private final PublicContract invEnc;
    
	private PublicSmartContract(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		this.gbdrnAddress = gbdrnAddress;
		this.gbdrnPassword = gbdrnPassword;

		Web3j web3j = Web3j.build(new HttpService(url));
		TransactionManager txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
		invEnc = PublicContract.load(contractAddress, web3j, txMgr, BigInteger.valueOf(36000000000L), BigInteger.valueOf(3000000L));
		
		invAdmin = Admin.build(new HttpService(url));
		
		LOG.info("Initialized successfully.");
	}
	
	public static synchronized PublicSmartContract instance(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		if (instance == null) {
			instance = new PublicSmartContract(url, contractAddress, gbdrnAddress, gbdrnPassword);
		}
		return instance;
	}
	
	public static PublicSmartContract instance() {
		return instance;
	}
	
	public static String hexToASCII(String hexValue)
    {
        StringBuilder output = new StringBuilder("");
        for (int i = 0; i < hexValue.length(); i += 2)
        {
            String str = hexValue.substring(i, i + 2);
            output.append((char) Integer.parseInt(str, 16));
        }
        return output.toString();
    }
	
	public void createRecord(String rightHash) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method createRecord(" + rightHash + ")");
			TransactionReceipt tr = invEnc.createRecord(rightHash).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public void deleteRecord(String rightHash) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method deleteRecord(" + rightHash + ")");
			TransactionReceipt tr = invEnc.deleteRecord(rightHash).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public boolean getRecord(String rightHash) throws Exception {
		LOG.info("Invoking method getRecord(" + rightHash + ")");
		return invEnc.getRecord(rightHash).send();
	}

	public static String asciiToHex(String asciiValue) {
        char[] chars = asciiValue.toCharArray();
        StringBuffer hex = new StringBuffer();
        for (int i = 0; i < chars.length; i++) {
            hex.append(Integer.toHexString((int) chars[i]));
        }
        return hex.toString() + "".join("", Collections.nCopies(32 - (hex.length()/2), "00"));
    }
}