package kz.tamur.server.plugins;

import static java.nio.charset.StandardCharsets.UTF_8;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.Stack;
import java.util.function.Consumer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hyperledger.fabric.gateway.Contract;
import org.hyperledger.fabric.gateway.ContractEvent;
import org.hyperledger.fabric.gateway.Gateway;
import org.hyperledger.fabric.gateway.Identities;
import org.hyperledger.fabric.gateway.Identity;
import org.hyperledger.fabric.gateway.Network;
import org.hyperledger.fabric.gateway.Transaction;
import org.hyperledger.fabric.gateway.Wallet;
import org.hyperledger.fabric.gateway.Wallets;
import org.hyperledger.fabric.sdk.Channel;
import org.hyperledger.fabric.gateway.X509Identity;
import org.hyperledger.fabric.sdk.Enrollment;
import org.hyperledger.fabric.sdk.User;
import org.hyperledger.fabric.sdk.security.CryptoSuite;
import org.hyperledger.fabric.sdk.security.CryptoSuiteFactory;
import org.hyperledger.fabric_ca.sdk.EnrollmentRequest;
import org.hyperledger.fabric_ca.sdk.HFCAClient;
import org.hyperledger.fabric_ca.sdk.RegistrationRequest;
import org.json.JSONObject;

import com.cifs.or2.kernel.KrnClass;
import com.cifs.or2.server.Context;
import com.cifs.or2.server.Session;
import com.cifs.or2.server.orlang.SrvOrLang;
import com.cifs.or2.server.orlang.SrvPlugin;

import kz.tamur.or3ee.server.kit.SrvUtils;

public class HyperLedgerFabricPlugin implements SrvPlugin {
	
	private static final Log log = LogFactory.getLog(HyperLedgerFabricPlugin.class);
	
	private Session session;
	
	private static String dsName = null;
	
	private static Map<String, Network> blockchains = new HashMap<>();
	
	public HyperLedgerFabricPlugin() {}

	@Override
	public Session getSession() {
		return session;
	}

	@Override
	public void setSession(Session session) {
		this.session = session;
		HyperLedgerFabricPlugin.dsName = session.getDsName();
	}
	
	/**
	 * Инициализация подключения к контракту блокчейн-сети.
	 * Можно вызывать несколько раз для подключения к разным контрактам в одной или разных блокчейн-сетях
	 * @param walletDir - путь к папке с кошельками,
	 * @param networkConfigFile - путь к файлу конфигурации блокчейн-сети,
	 * @param identityName - идентификатор кошелька для запросов,
	 * @param networkName - название блокчейн-сети,
	 * @return объект <code>BufferedReader</code>.
	 * @throws Exception 
	 * @throws IOException
	 *             Signals that an I/O exception has occurred.
	 */
	public Network initBlockchain(String walletDir, String networkConfigFile, String identityName, String networkName) {
		Network network = blockchains.get(networkName);
		if (network == null) {
			try {
				
				Path walletPath = Paths.get(walletDir);
				Wallet wallet = Wallets.newFileSystemWallet(walletPath);
		
		        Path networkConfigPath = Paths.get(networkConfigFile);
		
		        Gateway.Builder builder = Gateway.createBuilder()
		                .identity(wallet, identityName)
		                .discovery(true)
		                .networkConfig(networkConfigPath);
		        
		        Gateway gateway = builder.connect();
		        network = gateway.getNetwork(networkName);
		        blockchains.put(networkName, network);
		        
		        log.info(String.format("Blockchain '%s' initialized successfully!", networkName));
			} catch (Exception e) {
		        log.error(String.format("Error initializing blockchain '%s'! Parameters provided: walletDir - %s, networkConfigFile - %s, identityName - %s", networkName, walletDir, networkConfigFile, identityName));
				log.error(e, e);
			}
		}
		return network;
	}
	
	public boolean releaseBlockchain(String networkName) {
		Network network = blockchains.remove(networkName);
		if (network != null) {
			network.getGateway().close();
			network = null;
			return true;
		}
		return false;
	}
	
	public synchronized void stopListeners(String networkName, String chaincodeName, String contractName) {
		Network network = getBlockchain(networkName);
		
		Contract contract = network.getContract(chaincodeName, contractName);
		// remove all listeners
		if (contract instanceof AutoCloseable) {
			try {
				((AutoCloseable)contract).close();
				log.info(String.format("Listeners for contract '%s' of blockchain '%s' stopped successfully!", contractName, networkName));
			} catch (Exception e) {
				log.error(String.format("Listeners for contract '%s' of blockchain '%s' NOT stopped!", contractName, networkName));
				log.error(e, e);
			}
		} else {
			log.error(String.format("Listeners for contract '%s' of blockchain '%s' NOT stopped!", contractName, networkName));
		}
	}
	
	public synchronized void startListeners(String chaincodeName, String contractName) {
		startListeners(chaincodeName, contractName, -1);
	}
	
	public synchronized void startListeners(String chaincodeName, String contractName, Number startBlock) {
		String networkName = blockchains.keySet().iterator().next();
		startListeners(networkName, chaincodeName, contractName, startBlock);
	}
	
	public synchronized void startListeners(String networkName, String chaincodeName, String contractName) {
		startListeners(networkName, chaincodeName, contractName, -1);
	}
	
	public synchronized void startListeners(String networkName, String chaincodeName, String contractName, Number startBlock) {
		startListeners(networkName, chaincodeName, contractName, "BlockchainUtil.smartContract_receivedEvent", "Org1MSPHardPledgeUpdate", -1);
	}
	
	public synchronized void startListeners(String networkName, String chaincodeName, String contractName, String or3MethodName, String eventName, Number startBlock) {
		stopListeners(networkName, chaincodeName, contractName);
		
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);
		Channel channel = network.getChannel();
		
		Consumer<ContractEvent> listener = event -> {
			try {
				log.info(String.format("Before Handle Event '%s' on contract '%s' in network '%s' chain '%s'.", event.getName(), contractName, contractName, networkName, chaincodeName));
				handleEvent(event, or3MethodName, channel, contractName);
			} catch (Exception e) {
				log.error(e, e);
			}
		};
		
		if (startBlock.longValue() < 0)
			contract.addContractListener(listener, eventName);
		else
			contract.addContractListener(startBlock.longValue(), listener, eventName);
		
		log.info(String.format("Listeners for contract '%s' of blockchain '%s' of chaincode '%s' from block '%d' started successfully!", contractName, networkName, chaincodeName, startBlock));
	}
	
	public String evaluateTransaction(String chaincodeName, String contractName, String transactionMethod, List<String> params) throws Exception {
		String networkName = blockchains.keySet().iterator().next();
		return evaluateTransaction(networkName, chaincodeName, contractName, transactionMethod, params);
	}
	
	public String evaluateTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, List<String> params) throws Exception {
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);

		byte[] result = contract.evaluateTransaction(transactionMethod, params.toArray(new String[0]));
		return new String(result, UTF_8);
	}
	
	public String evaluateTransaction(String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData) throws Exception {
		String networkName = blockchains.keySet().iterator().next();
		return evaluateTransaction(networkName, chaincodeName, contractName, transactionMethod, transientData);
	}
	
	public String evaluateTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData, List<String> params) throws Exception {
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);
		
		Transaction transaction = contract.createTransaction(transactionMethod);
		transaction.setTransient(transientData);
		byte[] result = transaction.evaluate(params.toArray(new String[0]));
		
		return new String(result, UTF_8);
	}
	
	public String evaluateTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData) throws Exception {
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);
		
		Transaction transaction = contract.createTransaction(transactionMethod);
		transaction.setTransient(transientData);
		byte[] result = transaction.evaluate();
		
		return new String(result, UTF_8);
	}
	
	public String submitTransaction(String chaincodeName, String contractName, String transactionMethod, List<String> params) throws Exception {
		String networkName = blockchains.keySet().iterator().next();
		return submitTransaction(networkName, chaincodeName, contractName, transactionMethod, params);
	}
	
	public String submitTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, List<String> params) throws Exception {
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);

		byte[] result = contract.submitTransaction(transactionMethod, params.toArray(new String[0]));
		return new String(result, UTF_8);
	}
	
	public String submitTransaction(String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData) throws Exception {
		String networkName = blockchains.keySet().iterator().next();
		return submitTransaction(networkName, chaincodeName, contractName, transactionMethod, transientData);
	}
	
	public String submitTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData) throws Exception {
		return submitTransaction(networkName, chaincodeName, contractName, transactionMethod, transientData, Collections.emptyList());
	}
	
	public String submitTransaction(String networkName, String chaincodeName, String contractName, String transactionMethod, Map<String, byte[]> transientData, List<String> params) throws Exception {
		Network network = getBlockchain(networkName);
		Contract contract = network.getContract(chaincodeName, contractName);
		
		Transaction transaction = contract.createTransaction(transactionMethod);
		transaction.setTransient(transientData);
		byte[] result = transaction.submit(params.toArray(new String[0]));
		
		return new String(result, UTF_8);
	}
	
	public Network getBlockchain(String networkName) {
		Network network = blockchains.get(networkName);
		if (network == null ) {
			log.error(String.format("Blockchain '%s' not yet initialized. Call 'initBlockchain(walletDir, networkConfigFile, identityName, networkName)' first.", networkName));
		}
		return network;
	}
	
	private void handleEvent(ContractEvent event, String or3MethodName, Channel channel, String contractName) {
		log.info("Received event '" + event.getName() + "'.");
		
		byte[] eventBytes = event.getPayload().get();
		String eventJSON = new String(eventBytes, UTF_8);
		JSONObject eventObj = new JSONObject(eventJSON);
		
		String channelName = channel != null ? channel.getName() : "";

		log.info("Event payload '" + eventJSON + "'.");

		String[] tokens = or3MethodName.split("\\.");
		
		String clsName = tokens[0];
		String methodName = tokens[1];
		
		Session session = null;
		try {
			if (dsName != null) {
				// Вызов метода smartContract_receivedEvent
				session = SrvUtils.getSession(dsName, "sys", null);
				KrnClass blockchainUtilCls = session.getClassByName(clsName);
				Context ctx = new Context(new long[0], 0, 0);
				ctx.langId = 0;
				ctx.trId = 0;
				session.setContext(ctx);
				SrvOrLang orlang = session.getSrvOrLang();
				List<Object> args = new ArrayList<Object>();
				args.add(event.getChaincodeId());
				args.add(event.getName());
				args.add(eventObj);
				args.add(eventJSON);
				args.add(channelName);
				args.add(event.getTransactionEvent().getBlockEvent().getBlockNumber());
				args.add(contractName);
				
				Map<String, Object> vars = new HashMap<String, Object>();
				orlang.exec(blockchainUtilCls, blockchainUtilCls, methodName, args, new Stack<String>(), vars);
				
				session.commitTransaction();
			} else {
				log.info("Event chaincode: " + event.getChaincodeId());
				log.info("Event name: " + event.getName());
				log.info("eventObj: " + eventObj);
				log.info("eventJSON: " + eventJSON);
			}
		} catch (Throwable e) {
			log.error(e, e);
		} finally {
			if (session != null)
				session.release();
		}
	}
	
	public void enrollAdmin(String caPath, String caHost, int caPort, String orgMSP, String walletPath) throws Exception {
		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile", caPath);
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://" + caHost + ":" + caPort, props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));

		// Check to see if we've already enrolled the admin user.
		if (wallet.get("admin") != null) {
			System.out.println("An identity for the admin user \"admin\" already exists in the wallet");
			return;
		}

		// Enroll the admin user, and import the new identity into the wallet.
		final EnrollmentRequest enrollmentRequestTLS = new EnrollmentRequest();
		enrollmentRequestTLS.addHost(caHost);
		enrollmentRequestTLS.setProfile("tls");
		Enrollment enrollment = caClient.enroll("admin", "adminpw", enrollmentRequestTLS);
		Identity user = Identities.newX509Identity(orgMSP, enrollment);
		wallet.put("admin", user);
		System.out.println("Successfully enrolled user \"admin\" and imported it into the wallet");
	}
	
	public void registerUser(String caPath, String caHost, int caPort, String orgMSP, String affiliation, String walletPath, String userName) throws Exception {
		// Create a CA client for interacting with the CA.
		Properties props = new Properties();
		props.put("pemFile", caPath);
		props.put("allowAllHostNames", "true");
		HFCAClient caClient = HFCAClient.createNewInstance("https://" + caHost + ":" + caPort, props);
		CryptoSuite cryptoSuite = CryptoSuiteFactory.getDefault().getCryptoSuite();
		caClient.setCryptoSuite(cryptoSuite);

		// Create a wallet for managing identities
		Wallet wallet = Wallets.newFileSystemWallet(Paths.get(walletPath));

		// Check to see if we've already enrolled the user.
		if (wallet.get(userName) != null) {
			System.out.println("An identity for the user \"" + userName + "\" already exists in the wallet");
			return;
		}

		X509Identity adminIdentity = (X509Identity)wallet.get("admin");
		if (adminIdentity == null) {
			System.out.println("\"admin\" needs to be enrolled and added to the wallet first");
			return;
		}
		User admin = new User() {

			@Override
			public String getName() {
				return "admin";
			}

			@Override
			public Set<String> getRoles() {
				return null;
			}

			@Override
			public String getAccount() {
				return null;
			}

			@Override
			public String getAffiliation() {
				return affiliation;
			}

			@Override
			public Enrollment getEnrollment() {
				return new Enrollment() {

					@Override
					public PrivateKey getKey() {
						return adminIdentity.getPrivateKey();
					}

					@Override
					public String getCert() {
						return Identities.toPemString(adminIdentity.getCertificate());
					}
				};
			}

			@Override
			public String getMspId() {
				return orgMSP;
			}

		};

		// Register the user, enroll the user, and import the new identity into the wallet.
		RegistrationRequest registrationRequest = new RegistrationRequest(userName);
		registrationRequest.setAffiliation(affiliation);
		registrationRequest.setEnrollmentID(userName);
		String enrollmentSecret = caClient.register(registrationRequest, admin);
		caClient.enroll(userName, enrollmentSecret);
		Identity user = Identities.newX509Identity(orgMSP, adminIdentity.getCertificate(), adminIdentity.getPrivateKey());
		wallet.put(userName, user);
		System.out.println("Successfully enrolled user \"" + userName + "\" and imported it into the wallet");
	}
}
