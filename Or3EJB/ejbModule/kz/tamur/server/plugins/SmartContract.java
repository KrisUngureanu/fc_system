package kz.tamur.server.plugins;

import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.web3j.crypto.CipherException;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.admin.Admin;
import org.web3j.protocol.admin.methods.response.PersonalUnlockAccount;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.ipc.WindowsIpcService;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.ClientTransactionManager;
import org.web3j.tx.TransactionManager;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;

import kz.tamur.or3ee.server.kit.SrvUtils;
import kz.tamur.smartcontracts.Encumbrance;

public class SmartContract {
	
    private static final Log LOG = LogFactory.getLog(SmartContract.class);
    private static final String IPC_FILE_NAME = "\\\\.\\pipe\\geth.ipc";

    private static SmartContract instance;
	
    private final String contractAddress;
    private final String gbdrnAddress;
    private final String gbdrnPassword;
    
    private final Web3jService invService;
    private final Admin invAdmin;
    private final Encumbrance invEnc;
    
	private boolean isStartedListeners = false;
	
	private SmartContract(final String contractAddress, final String gbdrnAddress, final String gbdrnPassword) throws IOException, CipherException {
		this.contractAddress = contractAddress;
		this.gbdrnAddress = gbdrnAddress;
		this.gbdrnPassword = gbdrnPassword;

		invService = new WindowsIpcService(IPC_FILE_NAME);
		Web3j web3j = Web3j.build(invService);
		TransactionManager txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
		invEnc = Encumbrance.load(contractAddress, web3j, txMgr, BigInteger.valueOf(36000000000L), BigInteger.valueOf(3000000L));
		
		invAdmin = Admin.build(invService);
		
		LOG.info("Initialized successfully.");
	}
	
	public static synchronized SmartContract instance(final String contractAddress, final String gbdrnAddress, final String gbdrnPassword) throws IOException, CipherException {
		if (instance == null) {
			instance = new SmartContract(contractAddress, gbdrnAddress, gbdrnPassword);
		}
		return instance;
	}
	
	public static SmartContract instance() {
		return instance;
	}
	
	public synchronized void startListeners(String dsName) {
		if (!isStartedListeners) {
			Web3j web3j = Web3j.build(new WindowsIpcService(IPC_FILE_NAME));
			TransactionManager txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
			Encumbrance encumbrance = Encumbrance.load(contractAddress, web3j, txMgr, BigInteger.valueOf(36000000000L), BigInteger.valueOf(100000));
			encumbrance.pledgeRecordCreatedEventObservable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST).subscribe(event -> {
				LOG.info("Received event 'PledgeRecordCreated'.");
				Session session = null;
	            try {
	            	String pledger;
	            	String pledgee;
	            	synchronized (invService) {
						Tuple4<String, String, BigInteger, String> res = invEnc.getRecord(event._rka, event._time).send();
			            pledger = res.getValue1();
			            pledgee = res.getValue2();
					}
					// вызов метода smartContract_checkData
	               	session = SrvUtils.getSession(dsName, "sys", null);
		            KrnClass blockchainUtilCls = session.getClassByName("BlockchainUtil");
		            Context ctx = new Context(new long[0], 0, 0);
		            ctx.langId = 0;
		            ctx.trId = 0;
		            session.setContext(ctx);
		            SrvOrLang orlang = session.getSrvOrLang();
					List<Object> args = new ArrayList<Object>();
		            args.add(event._rka);
		            args.add(event._time);
		            args.add(pledger);
		            args.add(pledgee);
		            Map<String, Object> vars = new HashMap<String, Object>();
					orlang.exec(blockchainUtilCls, blockchainUtilCls, "smartContract_checkData", args, new Stack<String>(), vars);
				} catch (Throwable e) {
					e.printStackTrace();
		        } finally {
		            if (session != null)
		            	session.release();
		        }
			});
			LOG.info("Listener for 'PledgeRecordCreated' event started.");
			
			web3j = Web3j.build(new WindowsIpcService(IPC_FILE_NAME));
			txMgr = new ClientTransactionManager(web3j, gbdrnAddress);
			encumbrance = Encumbrance.load(contractAddress, web3j, txMgr, BigInteger.valueOf(36000000000L), BigInteger.valueOf(100000));
			encumbrance.pledgeRecordApprovedByPledgeeEventObservable(DefaultBlockParameterName.LATEST, DefaultBlockParameterName.LATEST).subscribe(event -> {
				LOG.info("Received event 'PledgeRecordApprovedByPledgee'.");
				Session session = null;
	            try {
	            	String pledger;
	            	String pledgee;
	            	synchronized (invService) {
						Tuple4<String, String, BigInteger, String> res = invEnc.getRecord(event._rka, event._time).send();
			            pledger = res.getValue1();
			            pledgee = res.getValue2();
					}
					// вызов метода smartContract_getEncumbrance
	               	session = SrvUtils.getSession(dsName, "sys", null);
		            KrnClass blockchainUtilCls = session.getClassByName("BlockchainUtil");
		            Context ctx = new Context(new long[0], 0, 0);
		            ctx.langId = 0;
		            ctx.trId = 0;
		            session.setContext(ctx);
		            SrvOrLang orlang = session.getSrvOrLang();
					List<Object> args = new ArrayList<Object>();
		            args.add(event._rka);
		            args.add(event._time);
		            args.add(pledger);
		            args.add(pledgee);
		            Map<String, Object> vars = new HashMap<String, Object>();
					orlang.exec(blockchainUtilCls, blockchainUtilCls, "smartContract_getEncumbrance", args, new Stack<String>(), vars);
				} catch (Throwable e) {
					e.printStackTrace();
		        } finally {
		            if (session != null)
		            	session.release();
		        }
			});
			LOG.info("Listener for 'PledgeRecordApprovedByPledgee' event started.");

			isStartedListeners = true;
		}		
	}

	public void rejectByRn(String _rka, BigInteger _time, String _reason) throws Exception {
		synchronized (invService) {
			PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
			if (unlockAccount.accountUnlocked()) {
				LOG.info("Invoking method rejectByRn(" + _rka + ", " + _time + ", " + _reason + ")");
				TransactionReceipt tr = invEnc.rejectByRn(_rka, _time, _reason).send();
				LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
			} else {
				LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
			}
		}
	}

	public void approveByRn(String _rka, BigInteger _time, String _text) throws Exception {
		synchronized (invService) {
			PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
			if (unlockAccount.accountUnlocked()) {
				LOG.info("Invoking method approveByRn(" + _rka + ", " + _time + ")");
				TransactionReceipt tr = invEnc.approveByRn(_rka, _time, _text).send();
				LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
			} else {
				LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
			}
		}
	}

	public void registeredByRn(String _rka, BigInteger _time, String _text) throws Exception {
		synchronized (invService) {
			PersonalUnlockAccount unlockAccount = invAdmin.personalUnlockAccount(gbdrnAddress, gbdrnPassword, BigInteger.valueOf(180)).send();
			if (unlockAccount.accountUnlocked()) {
				LOG.info("Invoking method registeredByRn(" + _rka + ", " + _time + ", " + _text + ")");
				TransactionReceipt tr = invEnc.registeredByRn(_rka, _time, _text).send();
				LOG.info("Hash: " + tr.getTransactionHash() + "; Status: " + tr.getStatus() + "; Gas used: " + tr.getCumulativeGasUsed());
			} else {
				LOG.error("Failed to unlock account " + gbdrnAddress + ": " + unlockAccount.getError().getMessage());
			}
		}
	}
}