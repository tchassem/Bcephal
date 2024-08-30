package com.moriset.bcephal.gateway;

import org.springframework.core.io.ClassPathResource;

import javax0.license3j.License;
import javax0.license3j.io.IOFormat;
import javax0.license3j.io.KeyPairReader;
import javax0.license3j.io.LicenseReader;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class LicenseUtil {
	
	
//	static final String CIPHER = "RSA";
//    static final int KEY_SIZE = 1024;
//    static final String PRIVATE_KEY = "private.key";
    static final String PUBLIC_KEY = "public.key";
//    static final String SIGN = "SHA-512";
//    String licencePath = "license.bcp";
//    static byte[] KEY = null;
	
	
//	public static void generate() throws Exception {
//		var license = License.Create.from("product:STRING=B-CEPHAL\nversion:STRING=8\nvalidity:DATE=2023-07-31\nClient:STRING=Finflag");
//		
//		var keysWriter = new KeyPairWriter(PRIVATE_KEY, PUBLIC_KEY);
//        var keys = LicenseKeyPair.Create.from(CIPHER, KEY_SIZE);
//        keysWriter.write(keys, IOFormat.BASE64);
//        
//		var fpUnsigned = license.fingerprint();		
//		license.sign(keys.getPair().getPrivate(), SIGN);		
//		try (final var sut = new LicenseWriter(LICENCE)) {
//            sut.write(license, IOFormat.STRING);
//        }
//		KEY = keys.getPublic();
//	}


	public static boolean validate(String licencePath) throws Exception {
		try (var reader = new LicenseReader(licencePath)) {
		    License license = reader.read(IOFormat.STRING);		
		    String product = license.get("product").getString();
		    String version = license.get("version").getString();
		    String validity = license.get("validity").getString();
		    log.info("{} V{}, Validity : {}", product, version, validity);
		    
		    var resource = new ClassPathResource(PUBLIC_KEY);
		    KeyPairReader keys = new KeyPairReader(resource.getInputStream());
		    
		    if(license.isOK(keys.readPublic().getPublic()) ){
		    	keys.close();
			    return true;
			}	
		    keys.close();
		} catch (Exception e) {
		    log.error("Licence ", e);
		}
		return false;
	}

}
