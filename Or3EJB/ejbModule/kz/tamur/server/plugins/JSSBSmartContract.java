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

public class JSSBSmartContract {
	
    private static final Log LOG = LogFactory.getLog(JSSBSmartContract.class);

    private static JSSBSmartContract instance;
	
    private final String url;
    private final String contractAddress;
    private final String gbdrnAddress;
    private final String gbdrnPassword;
    
    private final Admin invAdmin;
    private final JSSBContract invEnc;
    
	private boolean isStartedListeners = false;
	
	private JSSBSmartContract(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		this.url = url;
		this.contractAddress = contractAddress;
		this.gbdrnAddress = gbdrnAddress;
		this.gbdrnPassword = gbdrnPassword;

		Web3j web3j = Web3j.build(new HttpService(url));
		TransactionManager txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
		invEnc = JSSBContract.load(contractAddress, web3j, txMgr, BigInteger.valueOf(36000000000L), BigInteger.valueOf(3000000L));
		
		invAdmin = Admin.build(new HttpService(url));
		
		LOG.info("Initialized successfully.");
	}
	
	public static synchronized JSSBSmartContract instance(String url, String contractAddress, String gbdrnAddress, String gbdrnPassword) throws IOException, CipherException {
		if (instance == null) {
			instance = new JSSBSmartContract(url, contractAddress, gbdrnAddress, gbdrnPassword);
		}
		return instance;
	}
	
	public static JSSBSmartContract instance() {
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
	
	public synchronized void startListeners(String dsName) {
		if (!isStartedListeners) {
			Web3j web3j = Web3j.build(new HttpService(url));
			TransactionManager txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
			JSSBContract jssbContract = JSSBContract.load(contractAddress, web3j, txMgr, BigInteger.valueOf(70000000000L), BigInteger.valueOf(100000));
			jssbContract.pledgeRecordApprovedByPledgorsEventObservable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST).subscribe(event -> {
				LOG.info("Received event 'PledgeRecordApprovedByPledgors'.");
				Session session = null;
	            try {
					// Вызов метода smartContract_checkDataJSSB
	               	session = SrvUtils.getSession(dsName, "sys", null);
		            KrnClass blockchainUtilCls = session.getClassByName("BlockchainUtil");
		            Context ctx = new Context(new long[0], 0, 0);
		            ctx.langId = 0;
		            ctx.trId = 0;
		            session.setContext(ctx);
		            SrvOrLang orlang = session.getSrvOrLang();
					List<Object> args = new ArrayList<Object>();
		            args.add(event._documentId);
					args.add(event._RKA);
					String pledgorId = hexToASCII(Numeric.toHexStringNoPrefix(event._pledgorId)).trim();
					args.add(pledgorId);
		            List<String> guarantorsIds = new ArrayList<>();
		            for (int i = 0; i < event._guarantorsIds.size(); i++) {
		            	Object guarantorIdObject = event._guarantorsIds.get(i);
		            	String guarantorId = hexToASCII(Numeric.toHexStringNoPrefix(((org.web3j.abi.datatypes.generated.Bytes32) guarantorIdObject).getValue())).trim();
		            	guarantorsIds.add(guarantorId);
		            }
		            args.add(guarantorsIds);
		            Map<String, Object> vars = new HashMap<String, Object>();
					orlang.exec(blockchainUtilCls, blockchainUtilCls, "smartContract_checkDataJSSB", args, new Stack<String>(), vars);
				} catch (Throwable e) {
					e.printStackTrace();
		        } finally {
		            if (session != null)
		            	session.release();
		        }
			});
			LOG.info("Listener for 'PledgeRecordApprovedByPledgors' event started.");

			web3j = Web3j.build(new HttpService(url));
			txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
			jssbContract = JSSBContract.load(contractAddress, web3j, txMgr, BigInteger.valueOf(70000000000L), BigInteger.valueOf(100000));
			jssbContract.pledgeRecordPaymentCompletedEventObservable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST).subscribe(event -> {
				LOG.info("Received event 'PledgeRecordPaymentCompleted'.");
				Session session = null;
	            try {
					// Вызов метода smartContract_registerJSSB
	               	session = SrvUtils.getSession(dsName, "sys", null);
		            KrnClass blockchainUtilCls = session.getClassByName("BlockchainUtil");
		            Context ctx = new Context(new long[0], 0, 0);
		            ctx.langId = 0;
		            ctx.trId = 0;
		            session.setContext(ctx);
		            SrvOrLang orlang = session.getSrvOrLang();
					List<Object> args = new ArrayList<Object>();
					args.add(event._RKA);
					args.add(event._bankId);
					args.add(event._documentHash);
					args.add(event._documentId);
					args.add(event._paymentId);
					String pledgorId = hexToASCII(Numeric.toHexStringNoPrefix(event._pledgorId)).trim();
					args.add(pledgorId);
		            List<String> guarantorsIds = new ArrayList<>();
		            for (int i = 0; i < event._guarantorsIds.size(); i++) {
		            	Object guarantorIdObject = event._guarantorsIds.get(i);
		            	String guarantorId = hexToASCII(Numeric.toHexStringNoPrefix(((org.web3j.abi.datatypes.generated.Bytes32) guarantorIdObject).getValue())).trim();
		            	guarantorsIds.add(guarantorId);
		            }
		            args.add(guarantorsIds);
		            Map<String, Object> vars = new HashMap<String, Object>();
					orlang.exec(blockchainUtilCls, blockchainUtilCls, "smartContract_registerJSSB", args, new Stack<String>(), vars);
				} catch (Throwable e) {
					e.printStackTrace();
		        } finally {
		            if (session != null)
		            	session.release();
		        }
			});
			LOG.info("Listener for 'PledgeRecordPaymentCompleted' event started.");
			
			isStartedListeners = true;
		}		
	}

	public void checkingApprovedByGBDRN(String _documentId, String _checkingOpinion, String _paymentParams) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method checkingApprovedByGBDRN(" + _documentId + ", " + _checkingOpinion + ", " + _paymentParams + ")");
			TransactionReceipt tr = invEnc.checkingApprovedByGBDRN(_documentId, _checkingOpinion, _paymentParams).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public void checkingCancelledByGBDRN(String _documentId, String _checkingOpinion) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method checkingCancelledByGBDRN(" + _documentId + ", " + _checkingOpinion + ")");
			TransactionReceipt tr = invEnc.checkingCancelledByGBDRN(_documentId, _checkingOpinion).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public void registeredByGbdrn(String _documentId, String _notificationId, String _finalOpinion) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method registeredByGbdrn(" + _documentId + ", " + _notificationId + ", " + _finalOpinion + ")");
			TransactionReceipt tr = invEnc.registeredByGbdrn(_documentId, _notificationId, _finalOpinion).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public void cancelledByGbdrn(String _documentId, String _notificationId, String _finalOpinion) throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			LOG.info("Invoking method cancelledByGbdrn(" + _documentId + ", " + _notificationId + ", " + _finalOpinion + ")");
			TransactionReceipt tr = invEnc.cancelledByGbdrn(_documentId, _notificationId, _finalOpinion).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
	}
	
	public void test() throws Exception {
		PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
		if (unlockAccount.accountUnlocked()) {
			byte[] _pledgorId = Numeric.hexStringToByteArray(asciiToHex("900313301276"));
			List<byte[]> _guarantorsIds = new ArrayList<>();
			_guarantorsIds.add(Numeric.hexStringToByteArray(asciiToHex("890621301276")));
			TransactionReceipt tr = invEnc.createRecord("123123123123", "123456789", "123456789123456789", "7777777", _pledgorId, _guarantorsIds).send();
			LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
		} else {
			LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
		}
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