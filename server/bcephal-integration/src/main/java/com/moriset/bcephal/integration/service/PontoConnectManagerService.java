package com.moriset.bcephal.integration.service;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.google.common.io.Files;
import com.ibanity.apis.client.builders.IbanityServiceBuilder;
import com.ibanity.apis.client.builders.OptionalPropertiesBuilder;
import com.ibanity.apis.client.models.IbanityCollection;
import com.ibanity.apis.client.paging.IbanityPagingSpec;
import com.ibanity.apis.client.products.ponto_connect.models.Account;
import com.ibanity.apis.client.products.ponto_connect.models.BulkPayment;
import com.ibanity.apis.client.products.ponto_connect.models.FinancialInstitution;
import com.ibanity.apis.client.products.ponto_connect.models.Payment;
import com.ibanity.apis.client.products.ponto_connect.models.Transaction;
import com.ibanity.apis.client.products.ponto_connect.models.Userinfo;
import com.ibanity.apis.client.products.ponto_connect.models.create.BulkPaymentCreateQuery;
import com.ibanity.apis.client.products.ponto_connect.models.create.PaymentCreateQuery;
import com.ibanity.apis.client.products.ponto_connect.models.delete.BulkPaymentDeleteQuery;
import com.ibanity.apis.client.products.ponto_connect.models.delete.PaymentDeleteQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.AccountReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.AccountsReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.BulkPaymentReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.OrganizationFinancialInstitutionReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.OrganizationFinancialInstitutionsReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.PaymentReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.TransactionReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.TransactionsReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.read.UserinfoReadQuery;
import com.ibanity.apis.client.products.ponto_connect.models.revoke.TokenRevokeQuery;
import com.ibanity.apis.client.products.ponto_connect.sandbox.models.FinancialInstitutionAccount;
import com.ibanity.apis.client.products.ponto_connect.sandbox.models.FinancialInstitutionTransaction;
import com.ibanity.apis.client.products.ponto_connect.sandbox.models.factory.read.FinancialInstitutionAccountReadQuery;
import com.ibanity.apis.client.products.ponto_connect.sandbox.models.factory.read.FinancialInstitutionAccountsReadQuery;
import com.ibanity.apis.client.products.ponto_connect.sandbox.models.factory.read.FinancialInstitutionTransactionsReadQuery;
import com.ibanity.apis.client.products.ponto_connect.sandbox.services.FinancialInstitutionAccountsService;
import com.ibanity.apis.client.products.ponto_connect.sandbox.services.FinancialInstitutionTransactionsService;
import com.ibanity.apis.client.products.ponto_connect.sandbox.services.SandboxService;
import com.ibanity.apis.client.products.ponto_connect.services.AccountService;
import com.ibanity.apis.client.products.ponto_connect.services.BulkPaymentService;
import com.ibanity.apis.client.products.ponto_connect.services.FinancialInstitutionService;
import com.ibanity.apis.client.products.ponto_connect.services.PaymentService;
import com.ibanity.apis.client.products.ponto_connect.services.PontoConnectService;
import com.ibanity.apis.client.products.ponto_connect.services.SynchronizationService;
import com.ibanity.apis.client.products.ponto_connect.services.TokenService;
import com.ibanity.apis.client.products.ponto_connect.services.TransactionService;
import com.ibanity.apis.client.products.ponto_connect.services.UserinfoService;
import com.ibanity.apis.client.services.IbanityService;
import com.ibanity.apis.client.utils.IbanityUtils;
import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;
import com.moriset.bcephal.integration.domain.EntityColumn;
import com.moriset.bcephal.integration.domain.EntityReferenceType;
import com.moriset.bcephal.integration.domain.PontoConnectEntity;
import com.moriset.bcephal.integration.domain.PontoSynchronization;
import com.moriset.bcephal.integration.repository.PontoSynchronizationRepository;
import com.moriset.bcephal.utils.BcephalException;
import com.moriset.bcephal.utils.CsvGenerator;
import com.moriset.bcephal.utils.FileUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class PontoConnectManagerService {

	@Autowired
	PontoConnectEntityService pontoConnectEntityService;

	@Autowired
	private IntegrationProperties properties;

	@Autowired
	private SslService sslService;

	public IbanityService getIbanityService(PontoConnectEntity entity) throws IOException, CertificateException, OperatorCreationException, PKCSException {

		if (entity != null && entity.getClientTls() != null && entity.getClientTls().getCertificate() != null) {

			File certificateFile = FileUtil.createTempFile("Certificate", ".pem");
			pontoConnectEntityService.writeBytesTofile(entity.getClientTls().getCertificate(), certificateFile);
			String passphrase = entity.getClientTls().getPrivateKeyPassphrase();
			File privateKeyFile = FileUtil.createTempFile("PrivateKey", ".pem");
			pontoConnectEntityService.writeBytesTofile(entity.getClientTls().getPrivateKey(), privateKeyFile);
			PrivateKey privateKey = sslService.loadPrivateKey(privateKeyFile.getCanonicalPath(), passphrase);
			X509Certificate certificate = (X509Certificate) sslService
					.loadCertificate(certificateFile.getCanonicalPath());
			OptionalPropertiesBuilder ibanityServiceBuilder = IbanityServiceBuilder.builder()
					.ibanityApiEndpoint(entity.getApiEndpoint()).tlsPrivateKey(privateKey).passphrase(passphrase)
					.tlsCertificate(certificate)
//					.withHttpRequestInterceptors((request, context) -> log.info("This is a HttpRequestInterceptor"))
//					.withHttpResponseInterceptors((response, context) -> log.info("This is a HttpResponseInterceptor"))
					.connectTimeout(10_000).socketTimeout(60_000).connectionRequestTimeout(10_000);

			if (entity.getClientTlsCa() != null) {
//			ibanityServiceBuilder
//					.caCertificate((X509Certificate)sslService.loadCertificate(pontoConnectProperties.getClientTlsCa().getCertificatePath()));
			}

			if (entity.getClientSignature().getPrivateKeyPath() != null
					&& StringUtils.hasText(entity.getClientSignature().getPrivateKeyPath())) {
				String signaturePassphrase = entity.getClientSignature().getPrivateKeyPassphrase();
				File certificate_SignatureFile = FileUtil.createTempFile("Certificate_Signature", ".pem");
				pontoConnectEntityService.writeBytesTofile(entity.getClientTls().getPrivateKey(),
						certificate_SignatureFile);
				File privateKey_SignatureFile = FileUtil.createTempFile("PrivateKey_Signature", ".pem");
				pontoConnectEntityService.writeBytesTofile(entity.getClientTls().getPrivateKey(),
						privateKey_SignatureFile);
				ibanityServiceBuilder
						.requestSignaturePrivateKey(sslService
								.loadPrivateKey(privateKey_SignatureFile.getCanonicalPath(), signaturePassphrase))
						.requestSignaturePassphrase(signaturePassphrase)
						.requestSignatureCertificate((X509Certificate) sslService
								.loadCertificate(certificate_SignatureFile.getCanonicalPath()))
						.signatureCertificateId(entity.getClientSignature().getCertificateId());
			}

			ibanityServiceBuilder.pontoConnectOauth2ClientId(entity.getOauth2().getClientId());
//					.withHttpRequestInterceptors((request, context) -> {
//						log.info("This is a HttpRequestInterceptor");
//					})

			// ibanityServiceBuilder.withHttpResponseInterceptors(httpResponseInterceptor::process);

			return ibanityServiceBuilder.build();
		}
		return null;
	}

	public Userinfo userinfo(UserinfoService userinfoService, String accessToken) {
		return userinfoService.getUserinfo(UserinfoReadQuery.builder().accessToken(accessToken).build());
	}

	public List<Account> accounts(AccountService accountService, String accessToken) {
		log.info("Accounts samples");
		IbanityCollection<Account> accounts = accountService
				.list(AccountsReadQuery.builder().accessToken(accessToken).build());
//        UUID accountId = accounts.getItems().stream().map(Account::getId).findFirst().orElseThrow(RuntimeException::new);
		return accounts.getItems();
	}

	public Account findAccounts(AccountService accountService, UUID accountId, String accessToken) {
		log.info("find Account samples");
		Account account = accountService
				.find(AccountReadQuery.builder().accessToken(accessToken).accountId(accountId).build());

		return account;
	}

	public void sampleSandbox(SandboxService sandboxService, UUID financialInstitutionId, String accessToken) {
		List<FinancialInstitutionAccount> financialInstitutionAccounts = financialInstitutionAccounts(
				sandboxService.financialInstitutionAccountsService(), financialInstitutionId, accessToken);
		log.trace("List of financialInstitutionAccounts {}", financialInstitutionAccounts);

		UUID financialInstitutionAccountId = financialInstitutionAccounts.stream()
				.map(FinancialInstitutionAccount::getId).findFirst().orElseThrow(RuntimeException::new);

		List<FinancialInstitutionTransaction> financialInstitutionAccountTransaction = financialInstitutionTransactions(
				sandboxService.financialInstitutionTransactionsService(), financialInstitutionId,
				financialInstitutionAccountId, accessToken);
		log.trace("List of financialInstitutionAccountTransaction {}", financialInstitutionAccountTransaction);
	}

	public List<FinancialInstitutionAccount> financialInstitutionAccounts(
			FinancialInstitutionAccountsService financialInstitutionAccountsService, UUID financialInstitutionId,
			String accessToken) {
		IbanityCollection<FinancialInstitutionAccount> list = financialInstitutionAccountsService
				.list(FinancialInstitutionAccountsReadQuery.builder().accessToken(accessToken)
						.financialInstitutionId(financialInstitutionId).build());

		UUID financialInstitutionAccountId = list.getItems().stream().map(FinancialInstitutionAccount::getId)
				.findFirst().orElseThrow(RuntimeException::new);
		financialInstitutionAccountsService.find(FinancialInstitutionAccountReadQuery.builder().accessToken(accessToken)
				.financialInstitutionId(financialInstitutionId)
				.financialInstitutionAccountId(financialInstitutionAccountId).build());
		return list.getItems();
	}

	public void revokeToken(TokenService tokenService, String clientSecret, String accessToken) {
		tokenService.revoke(TokenRevokeQuery.builder().token(accessToken).clientSecret(clientSecret).build());
	}

	public List<FinancialInstitution> financialInstitutions(FinancialInstitutionService financialInstitutionService,
			String accessToken) {
		log.info("Transactions samples");
		IbanityCollection<FinancialInstitution> financialInstitutions = financialInstitutionService
				.list(OrganizationFinancialInstitutionsReadQuery.builder().accessToken(accessToken).build());
//	       FinancialInstitution financialInstitution = financialInstitutions.getItems()
//	    		   .stream()
//	    		   .findFirst()
//	    		   .orElseThrow(RuntimeException::new);
		return financialInstitutions.getItems();
	}

	public FinancialInstitution findFinancialInstitutions(FinancialInstitutionService financialInstitutionService,
			UUID financialInstitutionId, String accessToken) {
		log.info("Financial Institution samples");
		return financialInstitutionService.find(OrganizationFinancialInstitutionReadQuery.builder()
				.financialInstitutionId(financialInstitutionId).accessToken(accessToken).build());
	}

	public List<FinancialInstitutionTransaction> financialInstitutionTransactions(
			FinancialInstitutionTransactionsService financialInstitutionTransactionsService,
			UUID financialInstitutionId, UUID financialInstitutionAccountId, String accessToken) {

		return financialInstitutionTransactionsService.list(FinancialInstitutionTransactionsReadQuery.builder()
				.accessToken(accessToken).financialInstitutionId(financialInstitutionId)
				.financialInstitutionAccountId(financialInstitutionAccountId).build()).getItems();
	}

	public String generateCodeVerifier() throws UnsupportedEncodingException {
		SecureRandom secureRandom = new SecureRandom();
		byte[] codeVerifier = new byte[32];
		secureRandom.nextBytes(codeVerifier);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
	}

	public String generateCodeChallange(String codeVerifier)
			throws UnsupportedEncodingException, NoSuchAlgorithmException {
		byte[] bytes = codeVerifier.getBytes("US-ASCII");
		MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
		messageDigest.update(bytes, 0, bytes.length);
		byte[] digest = messageDigest.digest();
		return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
	}

//	private Synchronization synchronization(SynchronizationService synchronizationService, UUID accountId, String accessToken) {
//        log.info("Synchronization samples");
//        Synchronization synchronization = synchronizationService.create(SynchronizationCreateQuery.builder()
//                .accessToken(accessToken)
//                .resourceId(accountId.toString())
//                .resourceType("account")//account
//                .subtype("accountTransactions")//accountTransactions,accountDetails
//                .customerIpAddress("192.168.100.114")
//                .build());
////        return synchronizationService.find(SynchronizationReadQuery.builder()
////                .synchronizationId(synchronization.getId())
////                .accessToken(accessToken)
////                .build());
//        log.info("Synchronization samples infos : {}",synchronization);
//        return synchronization;
//    }

	private IbanityCollection<Transaction> transactions(TransactionService transactionService,
			FinancialInstitution financialInstitution, Account account, String accessToken,
			int integer, String synchronizationId, boolean first) {
		log.info("Try to get Transactions from Ponto");
		TransactionsReadQuery request = TransactionsReadQuery.builder().accessToken(accessToken)
				.accountId(account.getId()).pagingSpec(getIbanityPagingSpec(synchronizationId, integer,first)).build();
//		
//        UUID transactionId = transactions.getItems()
//        		.stream()
//        		.map(Transaction::getId)
//        		.findFirst()
//        		.orElseThrow(RuntimeException::new);
		return transactionService.list(request);
	}

//	private IbanityCollection<Transaction> transactionsSync(TransactionService transService, SynchronizationService syncService,
//			UUID accountId, UUID synchronizationId, String accessToken, IbanityCollection<Transaction> lastTransactions, int pageSize) {
//		log.info("Try to get Transactions from Ponto");
//			TransactionsReadQuery request = TransactionsReadQuery
//				.builder()
//				.accessToken(accessToken)
//				.accountId(accountId)
//				.pagingSpec(getIbanityPagingSpec(synchronizationId, pageSize))
//				.synchronizationId(synchronizationId)
//				.build();
//		return transService.listUpdatedForSynchronization(request);
//	}

	private IbanityPagingSpec getIbanityPagingSpec(String synchronizationId, int pageSize, boolean isFist) {
		if (pageSize <= 0) {
			pageSize = 50;
		}
		IbanityPagingSpec item = IbanityPagingSpec.builder().limit(pageSize).build();
		if(isFist) {
			if (StringUtils.hasText(synchronizationId)) {
				item.setBefore(synchronizationId);
				log.debug("setBefore: {}", synchronizationId);
	
	//			if(lastTransactions.getBeforeCursor() != null) {
	//				//item.setBefore(lastTransactions.getBeforeCursor().toString());
	//			}
			}
		}else {
			if(StringUtils.hasText(synchronizationId)) {
				item.setAfter(synchronizationId);
				log.debug("setAfter: {}", synchronizationId);
			}
		}
		return item;
	}

	private CsvGenerator csvGenerator;

	private int transactionsCount = 0;

	@Autowired
	PontoSynchronizationRepository pontoSynchronizationRepository;

	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
	public int buildTransactions(PontoConnectEntity entity, IbanityService ibanityService)
			throws CertificateException, IOException {
		if (ibanityService != null) {
			PontoConnectService pontoConnectService = ibanityService.pontoConnectService();
			TransactionService transService = pontoConnectService.transactionService();
			SynchronizationService syncService = pontoConnectService.synchronizationService();
			List<FinancialInstitution> financialInstitutions = financialInstitutions(
					pontoConnectService.financialInstitutionService(), entity.getAccessToken());
			List<Account> accounts = accounts(pontoConnectService.accountService(), entity.getAccessToken());
			FinancialInstitution financialInstitution = null;
			if (financialInstitutions != null && financialInstitutions.size() > 0) {
				financialInstitution = financialInstitutions.get(0);
			}
			log.trace("List of accounts {}", accounts);
			transactionsCount = 0;
			for (Account account : accounts) {
				List<PontoSynchronization> syncs = pontoSynchronizationRepository.findAllByConnectEntityIdAndResourceIdOrderByIdDesc(entity.getId(),
						account.getId().toString());				
				PontoSynchronization sync = syncs != null && syncs.size() > 0 ? syncs.get(0) : new PontoSynchronization();
				log.debug("Initial sync: {}", sync);
				buildTransactionsByAccount(financialInstitution, account,
						sync, entity, transService, syncService);
			}
			return transactionsCount;
		}
		throw new BcephalException("Cannot created ibanity service check your parameters");
	}

	private void buildTransactionsByAccount(FinancialInstitution financialInstitution, Account account,
			PontoSynchronization sync, PontoConnectEntity entity, TransactionService transactionService,
			SynchronizationService synchronizationService) throws IOException {
		String synchronizationId = sync.getSynchronizationId();
		String previewSynchronizationId = sync.getSynchronizationId();
		IbanityCollection<Transaction> transactions = null;
		Transaction lastTransaction = null;
		Transaction previewTransaction = null;
		boolean first = StringUtils.hasText(synchronizationId);
		if (entity != null && entity.getColumnListChangeHandler() != null
				&& entity.getColumnListChangeHandler().getItems().size() > 0) {
			List<EntityColumn> items_ = entity.getColumnListChangeHandler().getItems();
			Collections.sort(items_, new Comparator<EntityColumn>() {
				@Override
				public int compare(EntityColumn value1, EntityColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
            int offset = 0;
			do {
				transactions = transactions(transactionService, financialInstitution, account, entity.getAccessToken(),
						entity.getPageSize(), first ? synchronizationId : previewSynchronizationId, first);
				if (transactions != null) {
					int count = transactions.getItems().size();
					log.debug("Transaction count : {}", count);
					transactionsCount += count;
					if(count > 0) {
						lastTransaction = getSynchronizationId(transactions);
						if (lastTransaction != null) {
							synchronizationId = lastTransaction.getId().toString();
							if(offset == 0) {
								previewTransaction = lastTransaction;
							}
						}
						Transaction previewT = getSynchronizationIdLast(transactions);
						if (previewT != null) {
							previewSynchronizationId = previewT.getId().toString();
						}
						offset++;
						log.trace("List of transactions {}", transactions);
						log.info("try to Generate CSV");
						if (StringUtils.hasText(entity.getOutPath())) {
							String accountId = account.getId().toString();
							String accountReference = account.getReference().replaceAll("[^\\w]", "");
							
							String lastid = System.currentTimeMillis() + "";
							Path path = Paths.get(properties.getInDir(), entity.getOutPath(), accountReference,
									entity.getName().concat("__").concat(accountId).concat("__")
											.concat(lastid + ".csv"))
									.normalize();
							log.trace("CSV link output Path {}", path);
							if (!path.getParent().toFile().exists()) {
								path.getParent().toFile().mkdirs();
							}
							log.trace("CSV output Path {}", path);
							csvGenerator = new CsvGenerator(path.toFile().getCanonicalPath());
							csvGenerator.start();
							transactions.getItems().forEach(x -> generateCSV(x, financialInstitution, account, items_));
							csvGenerator.end();
							log.trace("end to Generate Path {}", path);
							process(accountId, accountReference, properties.getBackupDir(), transactions, lastid);
						}
					}
				}
			} 
			while ((transactions != null && transactions.getItems().size() > 0));
			if (lastTransaction != null) {
				if(!first && previewTransaction != null) {
					lastTransaction = previewTransaction;
				}
				//PontoSynchronization sync = new PontoSynchronization();
				sync.setConnectEntityId(entity.getId());
				sync.setResourceId(account.getId().toString());
				sync.setCreatedAt(lastTransaction.getValueDate());
				sync.setSynchronizationId(lastTransaction.getId().toString());
				sync = pontoSynchronizationRepository.save(sync);
				log.debug("Sync saved : {}", sync);
			}
			else{
				log.debug("No Sync to save!");
			}
		} else {
			throw new BcephalException("Empty column");
		}
	}

	public void process(String accountId, String accountIdRef, String backupDir, IbanityCollection<Transaction> trans, String lastid) {
		log.info("Try to save backup json file");

		if (StringUtils.hasText(accountId) && StringUtils.hasText(accountIdRef)) {
			try {
				Path path = Paths.get(backupDir, accountIdRef,
						"Transaction__".concat(accountId).concat("__".concat(lastid + ".json")))
						.normalize();
				if (!path.getParent().toFile().exists()) {
					path.getParent().toFile().mkdirs();
				}
				try {
					Files.write(IbanityUtils.objectMapper().writeValueAsBytes(trans.getItems()), path.toFile());
					log.trace("Backup saved to  Path: {}", path);
				} catch (Exception e) {
					log.error("Error to write backup file to {}", path);
				}
			} catch (Exception e) {
				log.error("Error Created destination from  {}", backupDir);
			}
		} else {
			log.error("Account Reference is Empty", backupDir);
		}
	}

	private Transaction getSynchronizationId(IbanityCollection<Transaction> transactions) {
		if (transactions != null && transactions.getItems().size() > 0) {
			return transactions.getItems().get(0);
		}
		return null;
	}
	
	private Transaction getSynchronizationIdLast(IbanityCollection<Transaction> transactions) {
		if (transactions != null && transactions.getItems().size() > 0) {
			return transactions.getItems().get(transactions.getItems().size()-1);
		}
		return null;
	}

	private void generateCSV(Transaction transaction, FinancialInstitution financialInstitution, Account account,
			List<EntityColumn> items_) {
		if (csvGenerator != null) {
			try {
				csvGenerator.printRecord(getDatas(transaction, financialInstitution, account, items_));
			} catch (IOException e) {
				e.printStackTrace();
				log.error("Error to write data to out file {}\n{}", csvGenerator.getPath(), e);
			}
		}
	}

	private Object[] getDatas(Transaction transaction, FinancialInstitution financialInstitution, Account account,
			List<EntityColumn> items_) {
		List<Object> items = new ArrayList<>();
		if (items_ != null && items_.size() > 0) {
			Collections.sort(items_, new Comparator<EntityColumn>() {
				@Override
				public int compare(EntityColumn value1, EntityColumn value2) {
					return value1.getPosition() - value2.getPosition();
				}
			});
			items_.forEach(x -> addValue(x, transaction, items, financialInstitution, account));
		}
		return items.toArray(new Object[items.size()]);
	}

	private void addValue(EntityColumn column, Transaction transaction, List<Object> items,
			FinancialInstitution financialInstitution, Account account) {
		if (transaction != null && EntityReferenceType.TRANSATION.equals(column.getReferenceType())) {
			Field[] fields = transaction.getClass().getDeclaredFields();
			for (Field field : fields) {
				boolean isReference = StringUtils.hasText(column.getCopyReferenceName())
						&& column.getCopyReferenceName().equalsIgnoreCase(field.getName());
				if (column.getName().equalsIgnoreCase(field.getName()) || isReference) {
					if (!column.isDefaultValue()) {
						field.setAccessible(true);
						try {
							boolean isdate = ((isReference && field.get(transaction) instanceof Instant)
									|| !isReference);
							if (column.getType().isPeriod() && isdate) {
								Instant value = (Instant) field.get(transaction);
								Date date = new Date(value.toEpochMilli());
								items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
							} else {
								items.add(field.get(transaction));
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							items.add(null);
						}
					} else {
						if (column.getType().isMeasure()) {
							items.add(column.getDecimalValue());
						} else if (column.getType().isPeriod()) {
							Date date = column.getDateValue().buildDynamicDate();
							items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
						} else {
							String val = column.getStringValue();
							items.add(StringUtils.hasText(val) ? val.replace("\n", "") : val);
						}
					}
					return;
				}
			}
		}
		if (column.isDefaultValue()) {
			if (column.getType().isMeasure()) {
				items.add(column.getDecimalValue());
			} else if (column.getType().isPeriod()) {
				Date date = column.getDateValue().buildDynamicDate();
				items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
			} else {
				String val = column.getStringValue();
				items.add(StringUtils.hasText(val) ? val.replace("\n", "") : val);
			}
			return;
		}
		if (account != null && StringUtils.hasText(column.getCopyReferenceName())
				&& EntityReferenceType.ACCOUNT.equals(column.getReferenceType())) {
			Field[] accountFields = account.getClass().getDeclaredFields();
			for (Field field : accountFields) {
				if (column.getCopyReferenceName().equalsIgnoreCase(field.getName())) {
					if (!column.isDefaultValue()) {
						field.setAccessible(true);
						try {
							if (column.getType().isPeriod() && field.get(account) instanceof Instant) {
								Instant value = (Instant) field.get(account);
								Date date = new Date(value.toEpochMilli());
								items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
							} else {
								items.add(field.get(account));
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							items.add(null);
						}
					} else {
						if (column.getType().isMeasure()) {
							items.add(column.getDecimalValue());
						} else if (column.getType().isPeriod()) {
							Date date = column.getDateValue().buildDynamicDate();
							items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
						} else {
							items.add(column.getStringValue());
						}
					}
					return;
				}
			}
		}
		if (financialInstitution != null && StringUtils.hasText(column.getCopyReferenceName())
				&& EntityReferenceType.FINANCIAL_INSTITUTION.equals(column.getReferenceType())) {
			Field[] financialInstitutionFields = financialInstitution.getClass().getDeclaredFields();
			for (Field field : financialInstitutionFields) {
				if (column.getCopyReferenceName().equalsIgnoreCase(field.getName())) {
					if (!column.isDefaultValue()) {
						field.setAccessible(true);
						try {
							if (column.getType().isPeriod() && field.get(financialInstitution) instanceof Instant) {
								Instant value = (Instant) field.get(financialInstitution);
								Date date = new Date(value.toEpochMilli());
								items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
							} else {
								items.add(field.get(financialInstitution));
							}
						} catch (IllegalArgumentException | IllegalAccessException e) {
							e.printStackTrace();
							items.add(null);
						}
					} else {
						if (column.getType().isMeasure()) {
							items.add(column.getDecimalValue());
						} else if (column.getType().isPeriod()) {
							Date date = column.getDateValue().buildDynamicDate();
							items.add(new SimpleDateFormat("dd/MM/yyyy").format(date));
						} else {
							String val = column.getStringValue();
							items.add(StringUtils.hasText(val) ? val.replace("\n", "") : val);
						}
					}
					return;
				}
			}
		}
		items.add(null);
	}

	public Transaction findTransactions(TransactionService transactionService, UUID accountId, UUID transactionId,
			String accessToken) {
		log.info("Find transactions samples");
		Transaction transaction = transactionService.find(TransactionReadQuery.builder().accessToken(accessToken)
				.accountId(accountId).transactionId(transactionId).build());
		return transaction;
	}

	public Payment DeletePayment(PaymentService paymentService, UUID accountId, UUID paymentId, String accessToken) {
		log.info("try to Delete Payment id {}", paymentId);
		Payment payment = paymentService.delete(PaymentDeleteQuery.builder().accessToken(accessToken)
				.accountId(accountId).paymentId(paymentId).build());

		return payment;
	}

	public Payment payments(PaymentService paymentService, UUID accountId, String accessToken) {
		log.info("Payments samples");
		Payment payment = paymentService.create(PaymentCreateQuery.builder().accessToken(accessToken)
				.accountId(accountId).remittanceInformation("payment").remittanceInformationType("unstructured")
				.requestedExecutionDate(LocalDate.now().plusDays(1)).currency("EUR").amount(BigDecimal.valueOf(59))
				.creditorName("Cajo-Alex Creditor").creditorAccountReference("BE55732022998044")
				.creditorAccountReferenceType("IBAN").creditorAgent("NBBEBEBB203").creditorAgentType("BIC").build());
		return payment;
	}

	public Payment findPayments(PaymentService paymentService, UUID accountId, UUID paymentId, String accessToken) {
		log.info("try to find payments id : {} account Id : {}", paymentId, accountId);
		Payment payment = paymentService.find(
				PaymentReadQuery.builder().accessToken(accessToken).accountId(accountId).paymentId(paymentId).build());

		return payment;
	}

	public BulkPayment bulkPayments(BulkPaymentService bulkPaymentService, UUID accountId, String accessToken) {
		log.info("BulkPayments samples");
		BulkPayment bulkPayment = bulkPaymentService
				.create(BulkPaymentCreateQuery.builder().accessToken(accessToken).batchBookingPreferred(true)
						.reference("myReference").requestedExecutionDate(LocalDate.now().plusDays(1))
						// .redirectUri(pontoConnectRedirectUrl)
						.accountId(accountId).payments(Collections.singletonList(createPayment())).build());
		return bulkPayment;
	}

	public BulkPayment findBulkPayments(BulkPaymentService bulkPaymentService, UUID accountId, UUID bulkPaymentId,
			String accessToken) {
		log.info("try to find Bulk payments id : {} account Id : {}", bulkPaymentId, accountId);
		BulkPayment bulkPayment = bulkPaymentService.find(BulkPaymentReadQuery.builder().accessToken(accessToken)
				.accountId(accountId).bulkPaymentId(bulkPaymentId).build());
		return bulkPayment;
	}

	public BulkPayment deleteBulkPayments(BulkPaymentService bulkPaymentService, UUID accountId, UUID bulkPaymentId,
			String accessToken) {
		log.info("try to Delete Bulk Payment id {}", bulkPaymentId);
		BulkPayment bulkPayment = bulkPaymentService.delete(BulkPaymentDeleteQuery.builder().accessToken(accessToken)
				.accountId(accountId).bulkPaymentId(bulkPaymentId).build());
		return bulkPayment;
	}

	private BulkPaymentCreateQuery.Payment createPayment() {
		return BulkPaymentCreateQuery.Payment.builder().remittanceInformation("payment")
				.remittanceInformationType("unstructured").currency("EUR").amount(BigDecimal.valueOf(59))
				.creditorName("Alex Creditor").creditorAccountReference("BE55732022998044")
				.creditorAccountReferenceType("IBAN").creditorAgent("NBBEBEBB203").creditorAgentType("BIC").build();
	}

//	@Transactional(value = MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
//	public int buildTransactions(PontoConnectEntity entity, IbanityService ibanityService) throws CertificateException, IOException {
//		if(ibanityService != null) {
//			PontoConnectService pontoConnectService = ibanityService.pontoConnectService();
//			TransactionService transService = pontoConnectService.transactionService();	
//			SynchronizationService syncService = pontoConnectService.synchronizationService();	
//			List<Account> accounts = accounts(pontoConnectService.accountService(), entity.getAccessToken());
//			log.info("List of accounts {}", accounts);
//			transactionsCount = 0;
//			
//			for (Account account : accounts) {
//				PontoSynchronization sync = pontoSynchronizationRepository.findBySynchronization(entity.getId(), account.getId().toString());
////				Synchronization synchronization = null;
////				if(sync != null) {
////					 synchronization = syncService.find(SynchronizationReadQuery.builder()
////		                .synchronizationId(UUID.fromString(sync.getSynchronizationId()))
////		                .accessToken(entity.getAccessToken())
////		                .build());
////				}
//				//Synchronization synchronization = synchronization(syncService, account.getId(), entity.getAccessToken());
////				payments(pontoConnectService.paymentService(), account.getId(), entity.getAccessToken());
////				payments(pontoConnectService.paymentService(), account.getId(), entity.getAccessToken());
////				payments(pontoConnectService.paymentService(), account.getId(), entity.getAccessToken());
////				payments(pontoConnectService.paymentService(), account.getId(), entity.getAccessToken());
////				payments(pontoConnectService.paymentService(), account.getId(), entity.getAccessToken());
//				buildTransactionsByAccountId(account.getId(),sync != null ? sync.getSynchronizationId() : null, entity, transService,syncService);
//			}
//			return transactionsCount;
//		}
//	   throw new BcephalException("Cannot created ibanity service check your parameters");
//	}
}
