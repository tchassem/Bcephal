package com.moriset.bcephal.messenger.send.sms;

import java.security.cert.CertificateException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.X509TrustManager;

import com.squareup.okhttp.OkHttpClient;

public interface ConfigClient {

	default OkHttpClient getHttpClient() {
		try {

			OkHttpClient client = new OkHttpClient();
			client.setSslSocketFactory(createIgnoreVerifySSL().getSocketFactory());
			client.setHostnameVerifier(getHostnameVerifier());
			return client;
		} catch (Exception e) {
			throw new RuntimeException("Failed to create new HTTP client", e);
		}
	}

	default SSLContext createIgnoreVerifySSL() throws Exception {
		SSLContext sc = SSLContext.getInstance("TLSv1.2"); // "SSLv3" "TLSv1.2" equivalent to TLS
		X509TrustManager trustManager = new X509TrustManager() {
			@Override
			public void checkClientTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public void checkServerTrusted(java.security.cert.X509Certificate[] paramArrayOfX509Certificate,
					String paramString) throws CertificateException {
			}

			@Override
			public java.security.cert.X509Certificate[] getAcceptedIssuers() {
				return null;
			}
		};
		sc.init(null, new X509TrustManager[] { trustManager }, null);
		return sc;
	}

	default HostnameVerifier getHostnameVerifier() {
		return (hostname, sess) -> {
			return true;
		};
	}
}
