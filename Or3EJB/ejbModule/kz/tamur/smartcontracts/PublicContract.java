package kz.tamur.smartcontracts;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.web3j.abi.EventEncoder;
import org.web3j.abi.EventValues;
import org.web3j.abi.TypeReference;
import org.web3j.abi.datatypes.Bool;
import org.web3j.abi.datatypes.Event;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Type;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.RemoteCall;
import org.web3j.protocol.core.methods.request.EthFilter;
import org.web3j.protocol.core.methods.response.Log;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
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
public class PublicContract extends Contract {
    private static final String BINARY = "{\r\n"
            + "\t\"object\": \"606060405234610000575b33600060006101000a81548173ffffffffffffffffffffffffffffffffffffffff021916908373ffffffffffffffffffffffffffffffffffffffff1602179055505b5b61077a8061005c6000396000f30060606040526000357c0100000000000000000000000000000000000000000000000000000000900463ffffffff16806311dd884514610054578063db70504b146100c3578063fbc47d531461011a575b610000565b34610000576100a9600480803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610171565b604051808215151515815260200191505060405180910390f35b3461000057610118600480803590602001908201803590602001908080601f016020809104026020016040519081016040528093929190818152602001838380828437820191505050505050919050506102de565b005b346100005761016f600480803590602001908201803590602001908080601f01602080910402602001604051908101604052809392919081815260200183838082843782019150505050505091905050610516565b005b600060406040519081016040528060206040519081016040528060008152508152602001600015158152506001836040518082805190602001908083835b602083106101d257805182526020820191506020810190506020830392506101af565b6001836020036101000a038019825116818451168082178552505050505050905001915050908152602001604051809103902060406040519081016040529081600082018054600181600116156101000203166002900480601f0160208091040260200160405190810160405280929190818152602001828054600181600116156101000203166002900480156102aa5780601f1061027f576101008083540402835291602001916102aa565b820191906000526020600020905b81548152906001019060200180831161028d57829003601f168201915b505050505081526020016001820160009054906101000a900460ff1615151515815250509050806020015191505b50919050565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561033c57610000565b6001826040518082805190602001908083835b60208310610372578051825260208201915060208101905060208303925061034f565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020905081816000019080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f106103f557805160ff1916838001178555610423565b82800160010185558215610423579182015b82811115610422578251825591602001919060010190610407565b5b50905061044891905b8082111561044457600081600090555060010161042c565b5090565b505060018160010160006101000a81548160ff0219169083151502179055507fa059a14672b8b4a0fd8e156ea573d700dcc0c59087da54d75ee38d22cc46f74e8260405180806020018281038252838181518152602001915080519060200190808383600083146104d8575b8051825260208311156104d8576020820191506020810190506020830392506104b4565b505050905090810190601f1680156105045780820380516001836020036101000a031916815260200191505b509250505060405180910390a15b5050565b6000600060009054906101000a900473ffffffffffffffffffffffffffffffffffffffff1673ffffffffffffffffffffffffffffffffffffffff163373ffffffffffffffffffffffffffffffffffffffff1614151561057457610000565b6001826040518082805190602001908083835b602083106105aa5780518252602082019150602081019050602083039250610587565b6001836020036101000a0380198251168184511680821785525050505050509050019150509081526020016040518091039020905081816000019080519060200190828054600181600116156101000203166002900490600052602060002090601f016020900481019282601f1061062d57805160ff191683800117855561065b565b8280016001018555821561065b579182015b8281111561065a57825182559160200191906001019061063f565b5b50905061068091905b8082111561067c576000816000905550600101610664565b5090565b505060008160010160006101000a81548160ff0219169083151502179055507f27a4cad6b98111ede500a8527ed0d906981301647db2c2db54f29c02579ecaa8826040518080602001828103825283818151815260200191508051906020019080838360008314610710575b805182526020831115610710576020820191506020810190506020830392506106ec565b505050905090810190601f16801561073c5780820380516001836020036101000a031916815260200191505b509250505060405180910390a15b50505600a165627a7a72305820070c39f357f40a0183476c27df5e2c6185841293154edda5e8f1ba09583ae1f70029\",\r\n"
            + "\t\"opcodes\": \"PUSH1 0x60 PUSH1 0x40 MSTORE CALLVALUE PUSH2 0x0 JUMPI JUMPDEST CALLER PUSH1 0x0 PUSH1 0x0 PUSH2 0x100 EXP DUP2 SLOAD DUP2 PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF MUL NOT AND SWAP1 DUP4 PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND MUL OR SWAP1 SSTORE POP JUMPDEST JUMPDEST PUSH2 0x77A DUP1 PUSH2 0x5C PUSH1 0x0 CODECOPY PUSH1 0x0 RETURN STOP PUSH1 0x60 PUSH1 0x40 MSTORE PUSH1 0x0 CALLDATALOAD PUSH29 0x100000000000000000000000000000000000000000000000000000000 SWAP1 DIV PUSH4 0xFFFFFFFF AND DUP1 PUSH4 0x11DD8845 EQ PUSH2 0x54 JUMPI DUP1 PUSH4 0xDB70504B EQ PUSH2 0xC3 JUMPI DUP1 PUSH4 0xFBC47D53 EQ PUSH2 0x11A JUMPI JUMPDEST PUSH2 0x0 JUMP JUMPDEST CALLVALUE PUSH2 0x0 JUMPI PUSH2 0xA9 PUSH1 0x4 DUP1 DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP3 ADD DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP1 PUSH1 0x1F ADD PUSH1 0x20 DUP1 SWAP2 DIV MUL PUSH1 0x20 ADD PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 SWAP4 SWAP3 SWAP2 SWAP1 DUP2 DUP2 MSTORE PUSH1 0x20 ADD DUP4 DUP4 DUP1 DUP3 DUP5 CALLDATACOPY DUP3 ADD SWAP2 POP POP POP POP POP POP SWAP2 SWAP1 POP POP PUSH2 0x171 JUMP JUMPDEST PUSH1 0x40 MLOAD DUP1 DUP3 ISZERO ISZERO ISZERO ISZERO DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP POP PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 RETURN JUMPDEST CALLVALUE PUSH2 0x0 JUMPI PUSH2 0x118 PUSH1 0x4 DUP1 DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP3 ADD DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP1 PUSH1 0x1F ADD PUSH1 0x20 DUP1 SWAP2 DIV MUL PUSH1 0x20 ADD PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 SWAP4 SWAP3 SWAP2 SWAP1 DUP2 DUP2 MSTORE PUSH1 0x20 ADD DUP4 DUP4 DUP1 DUP3 DUP5 CALLDATACOPY DUP3 ADD SWAP2 POP POP POP POP POP POP SWAP2 SWAP1 POP POP PUSH2 0x2DE JUMP JUMPDEST STOP JUMPDEST CALLVALUE PUSH2 0x0 JUMPI PUSH2 0x16F PUSH1 0x4 DUP1 DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP3 ADD DUP1 CALLDATALOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP1 PUSH1 0x1F ADD PUSH1 0x20 DUP1 SWAP2 DIV MUL PUSH1 0x20 ADD PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 SWAP4 SWAP3 SWAP2 SWAP1 DUP2 DUP2 MSTORE PUSH1 0x20 ADD DUP4 DUP4 DUP1 DUP3 DUP5 CALLDATACOPY DUP3 ADD SWAP2 POP POP POP POP POP POP SWAP2 SWAP1 POP POP PUSH2 0x516 JUMP JUMPDEST STOP JUMPDEST PUSH1 0x0 PUSH1 0x40 PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 PUSH1 0x20 PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 PUSH1 0x0 DUP2 MSTORE POP DUP2 MSTORE PUSH1 0x20 ADD PUSH1 0x0 ISZERO ISZERO DUP2 MSTORE POP PUSH1 0x1 DUP4 PUSH1 0x40 MLOAD DUP1 DUP3 DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP4 DUP4 JUMPDEST PUSH1 0x20 DUP4 LT PUSH2 0x1D2 JUMPI DUP1 MLOAD DUP3 MSTORE PUSH1 0x20 DUP3 ADD SWAP2 POP PUSH1 0x20 DUP2 ADD SWAP1 POP PUSH1 0x20 DUP4 SUB SWAP3 POP PUSH2 0x1AF JUMP JUMPDEST PUSH1 0x1 DUP4 PUSH1 0x20 SUB PUSH2 0x100 EXP SUB DUP1 NOT DUP3 MLOAD AND DUP2 DUP5 MLOAD AND DUP1 DUP3 OR DUP6 MSTORE POP POP POP POP POP POP SWAP1 POP ADD SWAP2 POP POP SWAP1 DUP2 MSTORE PUSH1 0x20 ADD PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 SHA3 PUSH1 0x40 PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE SWAP1 DUP2 PUSH1 0x0 DUP3 ADD DUP1 SLOAD PUSH1 0x1 DUP2 PUSH1 0x1 AND ISZERO PUSH2 0x100 MUL SUB AND PUSH1 0x2 SWAP1 DIV DUP1 PUSH1 0x1F ADD PUSH1 0x20 DUP1 SWAP2 DIV MUL PUSH1 0x20 ADD PUSH1 0x40 MLOAD SWAP1 DUP2 ADD PUSH1 0x40 MSTORE DUP1 SWAP3 SWAP2 SWAP1 DUP2 DUP2 MSTORE PUSH1 0x20 ADD DUP3 DUP1 SLOAD PUSH1 0x1 DUP2 PUSH1 0x1 AND ISZERO PUSH2 0x100 MUL SUB AND PUSH1 0x2 SWAP1 DIV DUP1 ISZERO PUSH2 0x2AA JUMPI DUP1 PUSH1 0x1F LT PUSH2 0x27F JUMPI PUSH2 0x100 DUP1 DUP4 SLOAD DIV MUL DUP4 MSTORE SWAP2 PUSH1 0x20 ADD SWAP2 PUSH2 0x2AA JUMP JUMPDEST DUP3 ADD SWAP2 SWAP1 PUSH1 0x0 MSTORE PUSH1 0x20 PUSH1 0x0 SHA3 SWAP1 JUMPDEST DUP2 SLOAD DUP2 MSTORE SWAP1 PUSH1 0x1 ADD SWAP1 PUSH1 0x20 ADD DUP1 DUP4 GT PUSH2 0x28D JUMPI DUP3 SWAP1 SUB PUSH1 0x1F AND DUP3 ADD SWAP2 JUMPDEST POP POP POP POP POP DUP2 MSTORE PUSH1 0x20 ADD PUSH1 0x1 DUP3 ADD PUSH1 0x0 SWAP1 SLOAD SWAP1 PUSH2 0x100 EXP SWAP1 DIV PUSH1 0xFF AND ISZERO ISZERO ISZERO ISZERO DUP2 MSTORE POP POP SWAP1 POP DUP1 PUSH1 0x20 ADD MLOAD SWAP2 POP JUMPDEST POP SWAP2 SWAP1 POP JUMP JUMPDEST PUSH1 0x0 PUSH1 0x0 PUSH1 0x0 SWAP1 SLOAD SWAP1 PUSH2 0x100 EXP SWAP1 DIV PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND CALLER PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND EQ ISZERO ISZERO PUSH2 0x33C JUMPI PUSH2 0x0 JUMP JUMPDEST PUSH1 0x1 DUP3 PUSH1 0x40 MLOAD DUP1 DUP3 DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP4 DUP4 JUMPDEST PUSH1 0x20 DUP4 LT PUSH2 0x372 JUMPI DUP1 MLOAD DUP3 MSTORE PUSH1 0x20 DUP3 ADD SWAP2 POP PUSH1 0x20 DUP2 ADD SWAP1 POP PUSH1 0x20 DUP4 SUB SWAP3 POP PUSH2 0x34F JUMP JUMPDEST PUSH1 0x1 DUP4 PUSH1 0x20 SUB PUSH2 0x100 EXP SUB DUP1 NOT DUP3 MLOAD AND DUP2 DUP5 MLOAD AND DUP1 DUP3 OR DUP6 MSTORE POP POP POP POP POP POP SWAP1 POP ADD SWAP2 POP POP SWAP1 DUP2 MSTORE PUSH1 0x20 ADD PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 SHA3 SWAP1 POP DUP2 DUP2 PUSH1 0x0 ADD SWAP1 DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP3 DUP1 SLOAD PUSH1 0x1 DUP2 PUSH1 0x1 AND ISZERO PUSH2 0x100 MUL SUB AND PUSH1 0x2 SWAP1 DIV SWAP1 PUSH1 0x0 MSTORE PUSH1 0x20 PUSH1 0x0 SHA3 SWAP1 PUSH1 0x1F ADD PUSH1 0x20 SWAP1 DIV DUP2 ADD SWAP3 DUP3 PUSH1 0x1F LT PUSH2 0x3F5 JUMPI DUP1 MLOAD PUSH1 0xFF NOT AND DUP4 DUP1 ADD OR DUP6 SSTORE PUSH2 0x423 JUMP JUMPDEST DUP3 DUP1 ADD PUSH1 0x1 ADD DUP6 SSTORE DUP3 ISZERO PUSH2 0x423 JUMPI SWAP2 DUP3 ADD JUMPDEST DUP3 DUP2 GT ISZERO PUSH2 0x422 JUMPI DUP3 MLOAD DUP3 SSTORE SWAP2 PUSH1 0x20 ADD SWAP2 SWAP1 PUSH1 0x1 ADD SWAP1 PUSH2 0x407 JUMP JUMPDEST JUMPDEST POP SWAP1 POP PUSH2 0x448 SWAP2 SWAP1 JUMPDEST DUP1 DUP3 GT ISZERO PUSH2 0x444 JUMPI PUSH1 0x0 DUP2 PUSH1 0x0 SWAP1 SSTORE POP PUSH1 0x1 ADD PUSH2 0x42C JUMP JUMPDEST POP SWAP1 JUMP JUMPDEST POP POP PUSH1 0x1 DUP2 PUSH1 0x1 ADD PUSH1 0x0 PUSH2 0x100 EXP DUP2 SLOAD DUP2 PUSH1 0xFF MUL NOT AND SWAP1 DUP4 ISZERO ISZERO MUL OR SWAP1 SSTORE POP PUSH32 0xA059A14672B8B4A0FD8E156EA573D700DCC0C59087DA54D75EE38D22CC46F74E DUP3 PUSH1 0x40 MLOAD DUP1 DUP1 PUSH1 0x20 ADD DUP3 DUP2 SUB DUP3 MSTORE DUP4 DUP2 DUP2 MLOAD DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP4 DUP4 PUSH1 0x0 DUP4 EQ PUSH2 0x4D8 JUMPI JUMPDEST DUP1 MLOAD DUP3 MSTORE PUSH1 0x20 DUP4 GT ISZERO PUSH2 0x4D8 JUMPI PUSH1 0x20 DUP3 ADD SWAP2 POP PUSH1 0x20 DUP2 ADD SWAP1 POP PUSH1 0x20 DUP4 SUB SWAP3 POP PUSH2 0x4B4 JUMP JUMPDEST POP POP POP SWAP1 POP SWAP1 DUP2 ADD SWAP1 PUSH1 0x1F AND DUP1 ISZERO PUSH2 0x504 JUMPI DUP1 DUP3 SUB DUP1 MLOAD PUSH1 0x1 DUP4 PUSH1 0x20 SUB PUSH2 0x100 EXP SUB NOT AND DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP JUMPDEST POP SWAP3 POP POP POP PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 LOG1 JUMPDEST POP POP JUMP JUMPDEST PUSH1 0x0 PUSH1 0x0 PUSH1 0x0 SWAP1 SLOAD SWAP1 PUSH2 0x100 EXP SWAP1 DIV PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND CALLER PUSH20 0xFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFFF AND EQ ISZERO ISZERO PUSH2 0x574 JUMPI PUSH2 0x0 JUMP JUMPDEST PUSH1 0x1 DUP3 PUSH1 0x40 MLOAD DUP1 DUP3 DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP4 DUP4 JUMPDEST PUSH1 0x20 DUP4 LT PUSH2 0x5AA JUMPI DUP1 MLOAD DUP3 MSTORE PUSH1 0x20 DUP3 ADD SWAP2 POP PUSH1 0x20 DUP2 ADD SWAP1 POP PUSH1 0x20 DUP4 SUB SWAP3 POP PUSH2 0x587 JUMP JUMPDEST PUSH1 0x1 DUP4 PUSH1 0x20 SUB PUSH2 0x100 EXP SUB DUP1 NOT DUP3 MLOAD AND DUP2 DUP5 MLOAD AND DUP1 DUP3 OR DUP6 MSTORE POP POP POP POP POP POP SWAP1 POP ADD SWAP2 POP POP SWAP1 DUP2 MSTORE PUSH1 0x20 ADD PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 SHA3 SWAP1 POP DUP2 DUP2 PUSH1 0x0 ADD SWAP1 DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP3 DUP1 SLOAD PUSH1 0x1 DUP2 PUSH1 0x1 AND ISZERO PUSH2 0x100 MUL SUB AND PUSH1 0x2 SWAP1 DIV SWAP1 PUSH1 0x0 MSTORE PUSH1 0x20 PUSH1 0x0 SHA3 SWAP1 PUSH1 0x1F ADD PUSH1 0x20 SWAP1 DIV DUP2 ADD SWAP3 DUP3 PUSH1 0x1F LT PUSH2 0x62D JUMPI DUP1 MLOAD PUSH1 0xFF NOT AND DUP4 DUP1 ADD OR DUP6 SSTORE PUSH2 0x65B JUMP JUMPDEST DUP3 DUP1 ADD PUSH1 0x1 ADD DUP6 SSTORE DUP3 ISZERO PUSH2 0x65B JUMPI SWAP2 DUP3 ADD JUMPDEST DUP3 DUP2 GT ISZERO PUSH2 0x65A JUMPI DUP3 MLOAD DUP3 SSTORE SWAP2 PUSH1 0x20 ADD SWAP2 SWAP1 PUSH1 0x1 ADD SWAP1 PUSH2 0x63F JUMP JUMPDEST JUMPDEST POP SWAP1 POP PUSH2 0x680 SWAP2 SWAP1 JUMPDEST DUP1 DUP3 GT ISZERO PUSH2 0x67C JUMPI PUSH1 0x0 DUP2 PUSH1 0x0 SWAP1 SSTORE POP PUSH1 0x1 ADD PUSH2 0x664 JUMP JUMPDEST POP SWAP1 JUMP JUMPDEST POP POP PUSH1 0x0 DUP2 PUSH1 0x1 ADD PUSH1 0x0 PUSH2 0x100 EXP DUP2 SLOAD DUP2 PUSH1 0xFF MUL NOT AND SWAP1 DUP4 ISZERO ISZERO MUL OR SWAP1 SSTORE POP PUSH32 0x27A4CAD6B98111EDE500A8527ED0D906981301647DB2C2DB54F29C02579ECAA8 DUP3 PUSH1 0x40 MLOAD DUP1 DUP1 PUSH1 0x20 ADD DUP3 DUP2 SUB DUP3 MSTORE DUP4 DUP2 DUP2 MLOAD DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP DUP1 MLOAD SWAP1 PUSH1 0x20 ADD SWAP1 DUP1 DUP4 DUP4 PUSH1 0x0 DUP4 EQ PUSH2 0x710 JUMPI JUMPDEST DUP1 MLOAD DUP3 MSTORE PUSH1 0x20 DUP4 GT ISZERO PUSH2 0x710 JUMPI PUSH1 0x20 DUP3 ADD SWAP2 POP PUSH1 0x20 DUP2 ADD SWAP1 POP PUSH1 0x20 DUP4 SUB SWAP3 POP PUSH2 0x6EC JUMP JUMPDEST POP POP POP SWAP1 POP SWAP1 DUP2 ADD SWAP1 PUSH1 0x1F AND DUP1 ISZERO PUSH2 0x73C JUMPI DUP1 DUP3 SUB DUP1 MLOAD PUSH1 0x1 DUP4 PUSH1 0x20 SUB PUSH2 0x100 EXP SUB NOT AND DUP2 MSTORE PUSH1 0x20 ADD SWAP2 POP JUMPDEST POP SWAP3 POP POP POP PUSH1 0x40 MLOAD DUP1 SWAP2 SUB SWAP1 LOG1 JUMPDEST POP POP JUMP STOP LOG1 PUSH6 0x627A7A723058 SHA3 SMOD 0xc CODECOPY RETURN JUMPI DELEGATECALL EXP ADD DUP4 0x47 PUSH13 0x27DF5E2C6185841293154EDDA5 0xe8 CALL 0xba MULMOD PC GASPRICE 0xe1 0xf7 STOP 0x29 \",\r\n"
            + "\t\"sourceMap\": \"27:1099:0:-;;;316:76;;;;374:10;360:11;;:24;;;;;;;;;;;;;;;;;;316:76;27:1099;;;;;;;\",\r\n"
            + "\t\"linkReferences\": {}\r\n"
            + "}";

    protected PublicContract(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    protected PublicContract(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        super(BINARY, contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public List<RecordCreatedEventResponse> getRecordCreatedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RecordCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RecordCreatedEventResponse> responses = new ArrayList<RecordCreatedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RecordCreatedEventResponse typedResponse = new RecordCreatedEventResponse();
            typedResponse.rightHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RecordCreatedEventResponse> recordCreatedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RecordCreated", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RecordCreatedEventResponse>() {
            @Override
            public RecordCreatedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                RecordCreatedEventResponse typedResponse = new RecordCreatedEventResponse();
                typedResponse.rightHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public List<RecordDeletedEventResponse> getRecordDeletedEvents(TransactionReceipt transactionReceipt) {
        final Event event = new Event("RecordDeleted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        List<EventValues> valueList = extractEventParameters(event, transactionReceipt);
        ArrayList<RecordDeletedEventResponse> responses = new ArrayList<RecordDeletedEventResponse>(valueList.size());
        for (EventValues eventValues : valueList) {
            RecordDeletedEventResponse typedResponse = new RecordDeletedEventResponse();
            typedResponse.rightHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
            responses.add(typedResponse);
        }
        return responses;
    }

    public Observable<RecordDeletedEventResponse> recordDeletedEventObservable(DefaultBlockParameter startBlock, DefaultBlockParameter endBlock) {
        final Event event = new Event("RecordDeleted", 
                Arrays.<TypeReference<?>>asList(),
                Arrays.<TypeReference<?>>asList(new TypeReference<Utf8String>() {}));
        EthFilter filter = new EthFilter(startBlock, endBlock, getContractAddress());
        filter.addSingleTopic(EventEncoder.encode(event));
        return web3j.ethLogObservable(filter).map(new Func1<Log, RecordDeletedEventResponse>() {
            @Override
            public RecordDeletedEventResponse call(Log log) {
                EventValues eventValues = extractEventParameters(event, log);
                RecordDeletedEventResponse typedResponse = new RecordDeletedEventResponse();
                typedResponse.rightHash = (String) eventValues.getNonIndexedValues().get(0).getValue();
                return typedResponse;
            }
        });
    }

    public RemoteCall<Boolean> getRecord(String rightHash) {
        Function function = new Function("getRecord", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(rightHash)), 
                Arrays.<TypeReference<?>>asList(new TypeReference<Bool>() {}));
        return executeRemoteCallSingleValueReturn(function, Boolean.class);
    }

    public RemoteCall<TransactionReceipt> createRecord(String rightHash) {
        Function function = new Function(
                "createRecord", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(rightHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public RemoteCall<TransactionReceipt> deleteRecord(String rightHash) {
        Function function = new Function(
                "deleteRecord", 
                Arrays.<Type>asList(new org.web3j.abi.datatypes.Utf8String(rightHash)), 
                Collections.<TypeReference<?>>emptyList());
        return executeRemoteCallTransaction(function);
    }

    public static RemoteCall<PublicContract> deploy(Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PublicContract.class, web3j, credentials, gasPrice, gasLimit, BINARY, "");
    }

    public static RemoteCall<PublicContract> deploy(Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return deployRemoteCall(PublicContract.class, web3j, transactionManager, gasPrice, gasLimit, BINARY, "");
    }

    public static PublicContract load(String contractAddress, Web3j web3j, Credentials credentials, BigInteger gasPrice, BigInteger gasLimit) {
        return new PublicContract(contractAddress, web3j, credentials, gasPrice, gasLimit);
    }

    public static PublicContract load(String contractAddress, Web3j web3j, TransactionManager transactionManager, BigInteger gasPrice, BigInteger gasLimit) {
        return new PublicContract(contractAddress, web3j, transactionManager, gasPrice, gasLimit);
    }

    public static class RecordCreatedEventResponse {
        public String rightHash;
    }

    public static class RecordDeletedEventResponse {
        public String rightHash;
    }
}
