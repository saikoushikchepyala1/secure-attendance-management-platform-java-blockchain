package com.attendance.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Uint256;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.util.Arrays;
import java.util.Collections;

@Service
public class BlockchainService {

    // Retain variable names used in other files
    private final Object contract = new Object(); // Dummy to avoid build breaks
    private final Web3j web3j;
    private final Credentials credentials;
    private final StaticGasProvider gasProvider;
    private final String contractAddress;

    public BlockchainService(
            @Value("${web3j.client-address}") String rpcUrl,
            @Value("${wallet.private-key}") String privateKey,
            @Value("${contract.address}") String contractAddress
    ) {
        this.web3j = Web3j.build(new HttpService(rpcUrl));
        this.credentials = Credentials.create(privateKey);
        this.gasProvider = new StaticGasProvider(
                BigInteger.valueOf(2_000_000_000L),
                BigInteger.valueOf(3_000_000)
        );
        this.contractAddress = contractAddress;
    }

    public void logAttendance(Long empId, String date, BigInteger statusEnum) throws Exception {
        System.out.println("⛓ Logging to blockchain: empId=" + empId + ", date=" + date + ", status=" + statusEnum);

        Function function = new Function(
                "markAttendance",
                Arrays.asList(
                        new Uint256(empId),
                        new Utf8String(date),
                        new Uint256(statusEnum)
                ),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction transactionResponse = web3j.ethSendTransaction(
                Transaction.createFunctionCallTransaction(
                        credentials.getAddress(),
                        null,
                        gasProvider.getGasPrice(),
                        gasProvider.getGasLimit(),
                        contractAddress,
                        encodedFunction
                )
        ).send();

        if (transactionResponse.hasError()) {
            throw new RuntimeException("Blockchain Tx Error: " + transactionResponse.getError().getMessage());
        }

        System.out.println("Logged to blockchain successfully.");
    }
}
