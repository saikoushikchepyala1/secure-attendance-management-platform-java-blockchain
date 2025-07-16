package com.attendance.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.abi.FunctionEncoder;
import org.web3j.abi.datatypes.Function;
import org.web3j.abi.datatypes.Utf8String;
import org.web3j.abi.datatypes.generated.Bytes32;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthSendTransaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;
import java.security.MessageDigest;

import java.util.Collections;

@Service
public class BlockchainService {
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
        this.contractAddress = contractAddress;
        this.gasProvider = new StaticGasProvider(
                BigInteger.valueOf(2_000_000_000L),
                BigInteger.valueOf(3_000_000)
        );
    }

    public void logDailyHash(String date, String hashString) throws Exception {
        System.out.println("⛓ Logging daily hash to blockchain: " + hashString);

        byte[] hashBytes = hexStringToByteArray(hashString);
        Function function = new Function(
                "logDailyHash",
                java.util.List.of(
                        new Utf8String(date),
                        new Bytes32(hashBytes)
                ),
                Collections.emptyList()
        );

        String encodedFunction = FunctionEncoder.encode(function);

        EthSendTransaction tx = web3j.ethSendTransaction(
                Transaction.createFunctionCallTransaction(
                        credentials.getAddress(),
                        null,
                        gasProvider.getGasPrice(),
                        gasProvider.getGasLimit(),
                        contractAddress,
                        encodedFunction
                )
        ).send();

        if (tx.hasError()) {
            throw new RuntimeException("Blockchain Error: " + tx.getError().getMessage());
        }

        System.out.println("Hash logged to blockchain.");
    }

    private byte[] hexStringToByteArray(String s) {
        if (s.startsWith("0x")) s = s.substring(2);
        byte[] data = new byte[32];
        for (int i = 0; i < s.length(); i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }
}
