package com.example.demo;

import cn.hyperchain.sdk.account.Account;
import cn.hyperchain.sdk.account.Algo;
import cn.hyperchain.sdk.common.solidity.Abi;
import cn.hyperchain.sdk.common.utils.ByteUtil;
import cn.hyperchain.sdk.common.utils.FileUtil;
import cn.hyperchain.sdk.common.utils.FuncParams;
import cn.hyperchain.sdk.exception.RequestException;
import cn.hyperchain.sdk.provider.DefaultHttpProvider;
import cn.hyperchain.sdk.provider.ProviderManager;
import cn.hyperchain.sdk.request.Request;
import cn.hyperchain.sdk.response.ReceiptResponse;
import cn.hyperchain.sdk.response.tx.TxResponse;
import cn.hyperchain.sdk.service.AccountService;
import cn.hyperchain.sdk.service.ContractService;
import cn.hyperchain.sdk.service.ServiceManager;
import cn.hyperchain.sdk.service.TxService;
import cn.hyperchain.sdk.service.impl.TxServiceImpl;
import cn.hyperchain.sdk.transaction.Transaction;
import com.example.demo.entity.Users;
import com.example.demo.repository.usersRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@SpringBootTest
class DemoApplicationTests {

	public static final String DEFAULT_URL = "127.0.0.1:8081";

	private static final String PASSWORD = "123";

	@Test
	public void test() {
		for (int i = 0; i < 10; i++) {
			try {
				testAdd();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (RequestException e) {
				e.printStackTrace();
			}
		}
	}

	static void testAdd() throws IOException, RequestException {
		// 1. build provider manager
		DefaultHttpProvider defaultHttpProvider = new DefaultHttpProvider.Builder().setUrl(DEFAULT_URL).build();
		ProviderManager providerManager = ProviderManager.createManager(defaultHttpProvider);

		// 2. build service
		ContractService contractService = ServiceManager.getContractService(providerManager);
		AccountService accountService = ServiceManager.getAccountService(providerManager);

		// 3. build transaction
		Account account = accountService.genAccount(Algo.SMAES, PASSWORD);

		InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/sol3/Accumulator.bin");
		InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/sol3/Accumulator.abi");
		String bin = FileUtil.readFile(inputStream1);
		String abiStr = FileUtil.readFile(inputStream2);
		Abi abi = Abi.fromJson(abiStr);

		Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).deploy(bin).build();
		transaction.sign(account);
		ReceiptResponse receiptResponse = contractService.deploy(transaction).send().polling();
		String contractAddress = receiptResponse.getContractAddress();
		System.out.println("contract address: " + contractAddress);
		System.out.println("账户私钥:" + account.getPrivateKey());

		FuncParams params = new FuncParams();
		params.addParams("100");
		params.addParams("200");

		Transaction transaction1 = new Transaction.EVMBuilder(account.getAddress()).invoke(contractAddress, "add(uint32,uint32)", abi, params).build();
		transaction1.sign(account);
		ReceiptResponse receiptResponse1 = contractService.invoke(transaction1).send().polling();
//		System.out.println(receiptResponse1.getRet());
		System.out.println(receiptResponse1.getTxHash());

		System.out.println("Add 100, 200");

		FuncParams nullParams = new FuncParams();

		Transaction transaction2 = new Transaction.EVMBuilder(account.getAddress()).invoke(contractAddress, "getSum()", abi, nullParams).build();
		transaction2.sign(account);
		ReceiptResponse receiptResponse2 = contractService.invoke(transaction2).send().polling();
		System.out.println(receiptResponse2.getRet());
	}

	@Test
	public void testEVM() throws IOException, RequestException {
		// 1. build provider manager
		DefaultHttpProvider defaultHttpProvider = new DefaultHttpProvider.Builder().setUrl(DEFAULT_URL).build();
		ProviderManager providerManager = ProviderManager.createManager(defaultHttpProvider);

		// 2. build service
		ContractService contractService = ServiceManager.getContractService(providerManager);
		AccountService accountService = ServiceManager.getAccountService(providerManager);

		// 3. build transaction
		Account account = accountService.genAccount(Algo.SMAES, PASSWORD);

		InputStream inputStream1 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/sol2/TestContract_sol_TypeTestContract.bin");
		InputStream inputStream2 = Thread.currentThread().getContextClassLoader().getResourceAsStream("solidity/sol2/TestContract_sol_TypeTestContract.abi");
		String bin = FileUtil.readFile(inputStream1);
		String abiStr = FileUtil.readFile(inputStream2);
		Abi abi = Abi.fromJson(abiStr);

		FuncParams params = new FuncParams();
		params.addParams("contract01");
		Transaction transaction = new Transaction.EVMBuilder(account.getAddress()).deploy(bin, abi, params).build();
		transaction.sign(account);
		ReceiptResponse receiptResponse = contractService.deploy(transaction).send().polling();
		String contractAddress = receiptResponse.getContractAddress();
		System.out.println("contract address: " + contractAddress);
		System.out.println("账户私钥:" + account.getPrivateKey());

		FuncParams params1 = new FuncParams();
		params1.addParams("1");
		Transaction transaction1 = new Transaction.EVMBuilder(account.getAddress()).invoke(contractAddress, "TestBytes32(bytes32)", abi, params1).build();
		transaction1.sign(account);
		ReceiptResponse receiptResponse1 = contractService.invoke(transaction1).send().polling();
		System.out.println(receiptResponse1.getRet());
		System.out.println("--------------------------------------");
//		Request<TxResponse> responseRequest;
//		TxService txService = new Tx
//		responseRequest = TxServiceImpl.
		List decodeList = abi.getFunction("TestBytes32(bytes32)").decodeResult(ByteUtil.fromHex(receiptResponse1.getRet()));
		String[] topics = receiptResponse1.getLog()[0].getTopics();
		byte[][] topicsData = new byte[topics.length][];
		for (int i = 0; i < topics.length; i ++) {
			topicsData[i] = ByteUtil.fromHex(topics[i]);
		}
		List decodeLogList = abi.getEvent("eventA(bytes,string)").decode(ByteUtil.fromHex(receiptResponse1.getLog()[0].getData()), topicsData);
		for (Object o : decodeList) {
			System.out.println(o.getClass());
			System.out.println(new String((byte[]) o));
		}
		System.out.println("---");
		for (Object o : decodeLogList) {
			System.out.println(o.getClass());
			System.out.println(new String((byte[]) o));
		}

		System.out.println("*********************************************************************");
		// maintain contract test

		// test freeze
		Transaction transaction2 = new Transaction.EVMBuilder(account.getAddress()).upgrade(contractAddress, bin).build();
		transaction2.sign(account);
		ReceiptResponse receiptResponse2 = contractService.maintain(transaction2).send().polling();
		System.out.println(receiptResponse2.getRet());

		// test thaw
		Transaction transaction3 = new Transaction.EVMBuilder(account.getAddress()).freeze(contractAddress).build();
		transaction3.sign(account);
		ReceiptResponse receiptResponse3 = contractService.maintain(transaction3).send().polling();
		System.out.println(receiptResponse3.getRet());

		// test upgrade
		Transaction transaction4 = new Transaction.EVMBuilder(account.getAddress()).unfreeze(contractAddress).build();
		transaction4.sign(account);
		ReceiptResponse receiptResponse4 = contractService.maintain(transaction4).send().polling();
		System.out.println(receiptResponse4.getRet());
	}

	@Autowired
	private usersRepository repository;

	@Test
	public void test1() {
		repository.save(new Users("AAA", "10","13888888888",true));

		Users re  = repository.findByAccountId("AA");
		if(re==null)
			System.out.println("failed to find This");
		else
			System.out.println("save = " + re.getAccountId());
	}

}
