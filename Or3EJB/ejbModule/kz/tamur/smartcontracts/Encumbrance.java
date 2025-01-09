package kz.tamur.smartcontracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Address;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.abi.datatypes.generated.Uint8;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple4;
import org.web3j.tx.Contract;
import org.web3j.tx.TransactionManager;
import rx.Observable;
import rx.functions.Func1;

/**
 * <p>Auto generated code.
 * <p><strong>Do not modify!</strong>
 * <p>Please use the <a href="https://docs.web3j.io/command_line.html">web3j command line tools</a>,
 * or the org.web3j.codegen.SolidityFunctionWrapperGenerator in the 
 * <a href="https://github.com/web3j/web3j/tree/master/codegen">codegen module</a> to update.
 *
 * <p>Generated with web3j version 3.2.0.
 */
public class Encumbrance extends Contract {
    private static final String BINARY = "6060604052341561000f57600080fd5b60405160208061177883398101604052808051906020019091905050806000806101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550506116fd8061007b6000396000f30060606040526004361061008e576000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff1680630f32de4e146100935780631c2fd9251461013c57806320c66e6b146101e5578063265fdbbf1461028e57806384b47b0f146103375780639b2c2f42146103b35780639ee8c2221461045c578063bc60f6a114610505575b600080fd5b341561009e57600080fd5b61013a600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190505061065f565b005b341561014757600080fd5b6101e3600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610859565b005b34156101f057600080fd5b61028c600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610a53565b005b341561029957600080fd5b610335600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610c4d565b005b341561034257600080fd5b6103b1600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803573ffffffffffffffffffffffffffffffffffffffff16906020019091905050610e47565b005b34156103be57600080fd5b61045a600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050611052565b005b341561046757600080fd5b610503600480803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190803590602001909190803590602001908201803590602001908080601f0160208091040260200160405190810160405280939291908181526020018383808284378201915050505050509190505061124c565b005b341561051057600080fd5b610569600480803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091908035906020019091905050611446565b604051808573ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff1681526020018360068111156105dd57fe5b60ff16815260200180602001828103825283818151815260200191508051906020019080838360005b83811015610621578082015181840152602081019050610606565b50505050905090810190601f16801561064e5780820380516001836020036101000a031916815260200191505b509550505050505060405180910390f35b60006001846040518082805190602001908083835b6020831015156106995780518252602082019150602081019050602083039250610674565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008481526020019081526020016000209050600260068111156106ec57fe5b8160010160149054906101000a900460ff16600681111561070957fe5b14801561076557503373ffffffffffffffffffffffffffffffffffffffff168160000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b151561077057600080fd5b60068160010160146101000a81548160ff0219169083600681111561079157fe5b0217905550818160020190805190602001906107ae929190611618565b507f9141a4aff8f9f46be62f33950d389c1c244c9b760e758b419f439ef4792e23ac84846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b838110156108185780820151818401526020810190506107fd565b50505050905090810190601f1680156108455780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156108b657600080fd5b6001846040518082805190602001908083835b6020831015156108ee57805182526020820191506020810190506020830392506108c9565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600084815260200190815260200160002090506003600681111561094157fe5b8160010160149054906101000a900460ff16600681111561095e57fe5b14151561096a57600080fd5b60048160010160146101000a81548160ff0219169083600681111561098b57fe5b0217905550818160020190805190602001906109a8929190611618565b507f2609c49f59162a770ac0e997f521a82bd947735c876f2410bf73ce37478bae1684846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610a125780820151818401526020810190506109f7565b50505050905090810190601f168015610a3f5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff16141515610ab057600080fd5b6001846040518082805190602001908083835b602083101515610ae85780518252602082019150602081019050602083039250610ac3565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206000848152602001908152602001600020905060016006811115610b3b57fe5b8160010160149054906101000a900460ff166006811115610b5857fe5b141515610b6457600080fd5b60028160010160146101000a81548160ff02191690836006811115610b8557fe5b021790555081816002019080519060200190610ba2929190611618565b507f7a3af2d0ec6fc41883db780704d50ec815cc761b7132feaff73b4d314354aa0384846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610c0c578082015181840152602081019050610bf1565b50505050905090810190601f168015610c395780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b60006001846040518082805190602001908083835b602083101515610c875780518252602082019150602081019050602083039250610c62565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206000848152602001908152602001600020905060026006811115610cda57fe5b8160010160149054906101000a900460ff166006811115610cf757fe5b148015610d5357503373ffffffffffffffffffffffffffffffffffffffff168160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b1515610d5e57600080fd5b60038160010160146101000a81548160ff02191690836006811115610d7f57fe5b021790555081816002019080519060200190610d9c929190611618565b507fae4f33d8db8bb078a73e52bafdd61468c835b8a5619dbcd6997b03dee7d138da84846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015610e06578082015181840152602081019050610deb565b50505050905090810190601f168015610e335780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b60006001836040518082805190602001908083835b602083101515610e815780518252602082019150602081019050602083039250610e5c565b6001836020036101000a03801982511681845116808217855250505050505090500191505090815260200160405180910390206000428152602001908152602001600020905060006006811115610ed457fe5b8160010160149054906101000a900460ff166006811115610ef157fe5b141515610efd57600080fd5b338160000160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff160217905550818160010160006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff16021790555060018160010160146101000a81548160ff02191690836006811115610fa457fe5b02179055507f5e0698aa254accb1c6127545c89267e641d554aa9d9a78a2fb46e90aa3ec5ff883426040518080602001838152602001828103825284818151815260200191508051906020019080838360005b83811015611012578082015181840152602081019050610ff7565b50505050905090810190601f16801561103f5780820380516001836020036101000a031916815260200191505b50935050505060405180910390a1505050565b60008060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff161415156110af57600080fd5b6001846040518082805190602001908083835b6020831015156110e757805182526020820191506020810190506020830392506110c2565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020600084815260200190815260200160002090506001600681111561113a57fe5b8160010160149054906101000a900460ff16600681111561115757fe5b14151561116357600080fd5b60058160010160146101000a81548160ff0219169083600681111561118457fe5b0217905550818160020190805190602001906111a1929190611618565b507fdb888ce6299f0de436b7ed9d9fa20b159fd4fe7b804d312f93bb6d9f0c428c0b84846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b8381101561120b5780820151818401526020810190506111f0565b50505050905090810190601f1680156112385780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b60006001846040518082805190602001908083835b6020831015156112865780518252602082019150602081019050602083039250611261565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008481526020019081526020016000209050600260068111156112d957fe5b8160010160149054906101000a900460ff1660068111156112f657fe5b14801561135257503373ffffffffffffffffffffffffffffffffffffffff168160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff16145b151561135d57600080fd5b60068160010160146101000a81548160ff0219169083600681111561137e57fe5b02179055508181600201908051906020019061139b929190611618565b507f9141a4aff8f9f46be62f33950d389c1c244c9b760e758b419f439ef4792e23ac84846040518080602001838152602001828103825284818151815260200191508051906020019080838360005b838110156114055780820151818401526020810190506113ea565b50505050905090810190601f1680156114325780820380516001836020036101000a031916815260200191505b50935050505060405180910390a150505050565b6000806000611453611698565b60006001876040518082805190602001908083835b60208310151561148d5780518252602082019150602081019050602083039250611468565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060008781526020019081526020016000209050600060068111156114e057fe5b8160010160149054906101000a900460ff1660068111156114fd57fe5b1415151561150a57600080fd5b8060000160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168160010160009054906101000a900473ffffffffffffffffffffffffffffffffffffffff168260010160149054906101000a900460ff1683600201808054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156115ff5780601f106115d4576101008083540402835291602001916115ff565b820191906000526020600020905b8154815290600101906020018083116115e257829003601f168201915b5050505050905094509450945094505092959194509250565b828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061165957805160ff1916838001178555611687565b82800160010185558215611687579182015b8281111561168657825182559160200191906001019061166b565b5b50905061169491906116ac565b5090565b602060405190810160405280600081525090565b6116ce91905b808211156116ca5760008160009055506001016116b2565b5090565b905600a165627a7a723058203c9bcc0903f43c758f376d124c8a9496ccc276d7927db486123cf2993d081b5e0029\r\n";

    protected Encumbrance(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected Encumbrance(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<PledgeRecordCreatedEventResponse> getPledgeRecordCreatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordCreatedEventResponse> responses = new ArrayList<PledgeRecordCreatedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordCreatedEventResponse typedResponse = new PledgeRecordCreatedEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordCreatedEventResponse> pledgeRecordCreatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordCreatedEventResponse>() {
            @Override
            public PledgeRecordCreatedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordCreatedEventResponse typedResponse = new PledgeRecordCreatedEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<PledgeRecordFailedEventResponse> getPledgeRecordFailedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordFailed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordFailedEventResponse> responses = new ArrayList<PledgeRecordFailedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordFailedEventResponse typedResponse = new PledgeRecordFailedEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordFailedEventResponse> pledgeRecordFailedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordFailed", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordFailedEventResponse>() {
            @Override
            public PledgeRecordFailedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordFailedEventResponse typedResponse = new PledgeRecordFailedEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<PledgeRecordApprovedByRnEventResponse> getPledgeRecordApprovedByRnEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordApprovedByRn", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordApprovedByRnEventResponse> responses = new ArrayList<PledgeRecordApprovedByRnEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordApprovedByRnEventResponse typedResponse = new PledgeRecordApprovedByRnEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordApprovedByRnEventResponse> pledgeRecordApprovedByRnEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordApprovedByRn", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordApprovedByRnEventResponse>() {
            @Override
            public PledgeRecordApprovedByRnEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordApprovedByRnEventResponse typedResponse = new PledgeRecordApprovedByRnEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<PledgeRecordCancelledEventResponse> getPledgeRecordCancelledEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordCancelled", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordCancelledEventResponse> responses = new ArrayList<PledgeRecordCancelledEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordCancelledEventResponse typedResponse = new PledgeRecordCancelledEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordCancelledEventResponse> pledgeRecordCancelledEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordCancelled", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordCancelledEventResponse>() {
            @Override
            public PledgeRecordCancelledEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordCancelledEventResponse typedResponse = new PledgeRecordCancelledEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<PledgeRecordApprovedByPledgeeEventResponse> getPledgeRecordApprovedByPledgeeEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordApprovedByPledgee", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordApprovedByPledgeeEventResponse> responses = new ArrayList<PledgeRecordApprovedByPledgeeEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordApprovedByPledgeeEventResponse typedResponse = new PledgeRecordApprovedByPledgeeEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordApprovedByPledgeeEventResponse> pledgeRecordApprovedByPledgeeEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordApprovedByPledgee", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordApprovedByPledgeeEventResponse>() {
            @Override
            public PledgeRecordApprovedByPledgeeEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordApprovedByPledgeeEventResponse typedResponse = new PledgeRecordApprovedByPledgeeEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public List<PledgeRecordRegisteredEventResponse> getPledgeRecordRegisteredEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("PledgeRecordRegistered", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<PledgeRecordRegisteredEventResponse> responses = new ArrayList<PledgeRecordRegisteredEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            PledgeRecordRegisteredEventResponse typedResponse = new PledgeRecordRegisteredEventResponse();
            typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
            typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<PledgeRecordRegisteredEventResponse> pledgeRecordRegisteredEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("PledgeRecordRegistered", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}, new TypeReference<Uint256>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, PledgeRecordRegisteredEventResponse>() {
            @Override
            public PledgeRecordRegisteredEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                PledgeRecordRegisteredEventResponse typedResponse = new PledgeRecordRegisteredEventResponse();
                typedResponse._rka = (String) eventValues.getNonIndexedValues().get(0).getValue();
                typedResponse._time = (BigInteger) eventValues.getNonIndexedValues().get(1).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<TransactionReceipt> cancelByPledger(String _rka, BigInteger _time, String _reason) {
        Function function = new Function(
                "cancelByPledger", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_reason)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> registeredByRn(String _rka, BigInteger _time, String _text) {
        Function function = new Function(
                "registeredByRn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_text)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approveByRn(String _rka, BigInteger _time, String _text) {
        Function function = new Function(
                "approveByRn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_text)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> approveByPledgee(String _rka, BigInteger _time, String _text) {
        Function function = new Function(
                "approveByPledgee", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_text)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> create(String _rka, String _pledgee) {
        Function function = new Function(
                "create", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.Address(_pledgee)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> rejectByRn(String _rka, BigInteger _time, String _reason) {
        Function function = new Function(
                "rejectByRn", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_reason)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> cancelByPledgee(String _rka, BigInteger _time, String _reason) {
        Function function = new Function(
                "cancelByPledgee", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time), 
                new org.web3j.abi.datatypes.Utf8String(_reason)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<Tuple4<String, String, BigInteger, String>> getRecord(String _rka, BigInteger _time) {
        final Function function = new Function("getRecord", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(_rka), 
                new org.web3j.abi.datatypes.generated.Uint256(_time)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Address>() {}, new TypeReference<Address>() {}, new TypeReference<Uint8>() {}, new TypeReference<Utf8String>() {}));
        return new RemoteCall<Tuple4<String, String, BigInteger, String>>(
                new Callable<Tuple4<String, String, BigInteger, String>>() {
                    @Override
                    public Tuple4<String, String, BigInteger, String> call() throws Exception {
                        List<Type> results = executeCallMultipleValueReturn(function);;
                        return new Tuple4<String, String, BigInteger, String>(
                                (String) results.get(0).getValue(), 
                                (String) results.get(1).getValue(), 
                                (BigInteger) results.get(2).getValue(), 
                                (String) results.get(3).getValue());
                    }
                });
    }

    public static RemoteCall<Encumbrance> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit, String _gbdrn) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_gbdrn)));
        return deployRemoteCall(Encumbrance.class, web3j, credentials, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static RemoteCall<Encumbrance> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit, String _gbdrn) {
        String encodedConstructor = FunctionEncoder.encodeConstructor(Arrays.<Type>asList(new org.web3j.abi.datatypes.Address(_gbdrn)));
        return deployRemoteCall(Encumbrance.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, encodedConstructor);
    }

    public static Encumbrance load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new Encumbrance(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static Encumbrance load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new Encumbrance(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class PledgeRecordCreatedEventResponse {
        public String _rka;

        public BigInteger _time;
    }

    public static class PledgeRecordFailedEventResponse {
        public String _rka;

        public BigInteger _time;
    }

    public static class PledgeRecordApprovedByRnEventResponse {
        public String _rka;

        public BigInteger _time;
    }

    public static class PledgeRecordCancelledEventResponse {
        public String _rka;

        public BigInteger _time;
    }

    public static class PledgeRecordApprovedByPledgeeEventResponse {
        public String _rka;

        public BigInteger _time;
    }

    public static class PledgeRecordRegisteredEventResponse {
        public String _rka;

        public BigInteger _time;
    }
}
