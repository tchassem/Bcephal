package com.moriset.bcephal.integration.service;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.security.KeyPair;
import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import org.apache.commons.io.FileUtils;
import org.bouncycastle.openssl.PEMEncryptedKeyPair;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.bc.BcPEMDecryptorProvider;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.openssl.jcajce.JceOpenSSLPKCS8DecryptorProviderBuilder;
import org.bouncycastle.operator.InputDecryptorProvider;
import org.bouncycastle.operator.OperatorCreationException;
import org.bouncycastle.pkcs.PKCS8EncryptedPrivateKeyInfo;
import org.bouncycastle.pkcs.PKCSException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.moriset.bcephal.config.MultiTenantJpaSecurityConfiguration;

@Service
@Transactional(value =MultiTenantJpaSecurityConfiguration.SECURITY_TRANSACTION_MANAGER)
public class SslService {

	private static CertificateFactory getCertificateFactory() throws CertificateException {
    	CertificateFactory fact = CertificateFactory.getInstance("X.509");
    	return fact;
    }
	
	public  PrivateKey loadPrivateKey(String filePath, String pwd) throws IOException, PKCSException, OperatorCreationException {
	    try (FileReader keyReader = new FileReader(Paths.get(filePath).toFile());) {
	    	PEMParser pEMParser = new PEMParser(keyReader);
	    	JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");	        
	    	Object obj = pEMParser.readObject();
	    	if(obj instanceof PEMEncryptedKeyPair) {
	    		PEMEncryptedKeyPair pEMEncryptedKeyPair = (PEMEncryptedKeyPair) obj;
	    		KeyPair pair = converter.getKeyPair(pEMEncryptedKeyPair.decryptKeyPair(new BcPEMDecryptorProvider(pwd.toCharArray())));	       
		        return pair.getPrivate();
	    	}
	    	
	        InputDecryptorProvider pkcs8Prov = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(pwd.toCharArray());	        
	    	PKCS8EncryptedPrivateKeyInfo pEMEncryptedKeyPair2 = (PKCS8EncryptedPrivateKeyInfo) obj;
	    	PrivateKey pair = converter.getPrivateKey(pEMEncryptedKeyPair2.decryptPrivateKeyInfo(pkcs8Prov));	       
	        return pair;
	    }
	}

	    public  Certificate loadCertificate(final String certificatePath) throws CertificateException {
	        try {
	            InputStream is = FileUtils.openInputStream(new File(certificatePath));
	            return getCertificateFactory().generateCertificate(is);
	        } catch (IOException exception) {
	            throw new IllegalArgumentException(new FileNotFoundException("Resource Path not found:" + certificatePath));
	        }
	    }
	    
	    public X509Certificate generateCertificateFromDER(byte[] certBytes) throws CertificateException {
			return (X509Certificate) getCertificateFactory().generateCertificate(new ByteArrayInputStream(certBytes));
		}
	    
	    
	    public PrivateKey generatePrivateKeyFromDER(byte[] keyBytes, String pwd) throws IOException, OperatorCreationException, PKCSException{
	    	 try (InputStreamReader keyReader = new InputStreamReader(new ByteArrayInputStream(keyBytes));) {
			    	PEMParser pEMParser = new PEMParser(keyReader);
			    	JcaPEMKeyConverter converter = new JcaPEMKeyConverter().setProvider("BC");	        
			    	Object obj = pEMParser.readObject();
			    	if(obj instanceof PEMEncryptedKeyPair) {
			    		PEMEncryptedKeyPair pEMEncryptedKeyPair = (PEMEncryptedKeyPair) obj;
			    		KeyPair pair = converter.getKeyPair(pEMEncryptedKeyPair.decryptKeyPair(new BcPEMDecryptorProvider(pwd.toCharArray())));	       
				        return pair.getPrivate();
			    	}
			    	
			        InputDecryptorProvider pkcs8Prov = new JceOpenSSLPKCS8DecryptorProviderBuilder().build(pwd.toCharArray());	        
			    	PKCS8EncryptedPrivateKeyInfo pEMEncryptedKeyPair2 = (PKCS8EncryptedPrivateKeyInfo) obj;
			    	PrivateKey pair = converter.getPrivateKey(pEMEncryptedKeyPair2.decryptPrivateKeyInfo(pkcs8Prov));	       
			        return pair;
			    }
		}

}
