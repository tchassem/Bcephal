package com.moriset.bcephal.messenger.config;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.List;

import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;

import org.apache.activemq.broker.BrokerService;
import org.apache.activemq.broker.SslBrokerService;
import org.apache.activemq.broker.TransportConnector;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import com.moriset.bcephal.messenger.properties.KeystoreServer;


@Component
public class SslBcephalActivemqServer extends BcephalActivemqServer {
	@Autowired
	public KeystoreServer keystore;

	@Override
	protected BrokerService createBrocker() throws IOException, Exception {
		String KEYSTORE_TYPE = "jks";
		InputStream SERVER_KEYSTORE = new ClassPathResource("server.keystore").getInputStream();
		InputStream CER_CLIENT_KEYSTORE = new ClassPathResource("server.ts").getInputStream();

		KeyManager[] km = getKeyManager(KEYSTORE_TYPE, SERVER_KEYSTORE);
		TrustManager[] tm = getTrustManager(KEYSTORE_TYPE, CER_CLIENT_KEYSTORE);
		SslBrokerService broker = new SslBrokerService();
		broker.addSslConnector(new URI(getLocalURI("ssl",hostAddress.getTcpPort())), km, tm, null);
		broker.addSslConnector(new URI(getLocalURI("stomp+ssl",hostAddress.getStompPort())), km, tm, null);
		List<TransportConnector> trans = broker.getTransportConnectors();
		for(int offset = 0; offset < trans.size() ; offset++) {
			trans.get(offset).setName(TRANSPORT_NAME + offset);
			trans.get(offset).setUpdateClusterClients(true);
			trans.get(offset).setRebalanceClusterClients(true);
			trans.get(offset).setUpdateClusterClientsOnRemove(true);
		}
		return broker;
	}

	@Override
	protected String getRemoteURI(String protocol,String ip, int port) {
		return String.format("%s://%s:%s", protocol, ip, port);
	}

	@Override
	protected String getLocalURI(String protocol,int port) {
		return String.format("%s://0.0.0.0:%s?transport.needClientAuth=true", protocol, port);
	}

	protected TrustManager[] getTrustManager(String KEYSTORE_TYPE, InputStream TRUST_KEYSTORE) throws Exception {
		TrustManager[] trustStoreManagers = null;
		KeyStore trustedCertStore = KeyStore.getInstance(KEYSTORE_TYPE);
		trustedCertStore.load(TRUST_KEYSTORE, keystore.getTrustPassword().toCharArray());
		TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
		tmf.init(trustedCertStore);
		trustStoreManagers = tmf.getTrustManagers();
		return trustStoreManagers;
	}

	protected KeyManager[] getKeyManager(String KEYSTORE_TYPE, InputStream SERVER_KEYSTORE) throws Exception {
		KeyManagerFactory kmf = KeyManagerFactory.getInstance(KeyManagerFactory.getDefaultAlgorithm());
		KeyStore ks = KeyStore.getInstance(KEYSTORE_TYPE);
		KeyManager[] keystoreManagers = null;
		byte[] sslCert = loadClientCredential(SERVER_KEYSTORE);
		if (sslCert != null && sslCert.length > 0) {
			ByteArrayInputStream bin = new ByteArrayInputStream(sslCert);
			ks.load(bin, keystore.getKeyPassword().toCharArray());
			kmf.init(ks, keystore.getKeyPassword().toCharArray());
			keystoreManagers = kmf.getKeyManagers();
		}
		return keystoreManagers;
	}

	protected byte[] loadClientCredential(InputStream in) throws IOException {
		if (in == null) {
			return null;
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		byte[] buf = new byte[512];
		int count = in.read(buf);
		while (count > 0) {
			out.write(buf, 0, count);
			count = in.read(buf);
		}
		in.close();
		return out.toByteArray();
	}

}
