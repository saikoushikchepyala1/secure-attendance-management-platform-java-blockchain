package com.attendance.blockchain;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;
import org.web3j.tx.RawTransactionManager;
import org.web3j.tx.gas.StaticGasProvider;

import java.math.BigInteger;

@Service
public class BlockchainService {

    private final AttendanceSystem contract;

    public BlockchainService(
            @Value("${web3j.client-address}") String rpcUrl,
            @Value("${wallet.private-key}") String privateKey,
            @Value("${contract.address}") String contractAddress
    ) throws Exception {

        Web3j web3j = Web3j.build(new HttpService(rpcUrl));
        Credentials credentials = Credentials.create(privateKey);

        StaticGasProvider gasProvider = new StaticGasProvider(
                BigInteger.valueOf(2_000_000_000L),
                BigInteger.valueOf(3_000_000)
        );

        this.contract = AttendanceSystem.load(
                contractAddress,
                web3j,
                new RawTransactionManager(web3j, credentials),
                gasProvider
        );
    }
    public void logAttendance(Long empId, String date, BigInteger statusEnum) throws Exception {
        System.out.println("⛓ Logging to blockchain: empId=" + empId + ", date=" + date + ", status=" + statusEnum);
        contract.markAttendance(
                BigInteger.valueOf(empId),
                date,
                statusEnum
        ).send();
        System.out.println(" Logged to blockchain successfully.");
    }
}
