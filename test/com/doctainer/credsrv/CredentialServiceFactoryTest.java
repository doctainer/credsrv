package com.doctainer.credsrv;

import java.io.IOException;
import java.util.Map;

public class CredentialServiceFactoryTest {
	// very, very old stuff
	public static void main(String[] args) throws IOException {
		  CredentialService cs = CredentialServiceFactory.getCredentialService();
		  Map<String, String> result = cs.getCredentials("PRODL");
		  System.out.println(result);
		 }
}
