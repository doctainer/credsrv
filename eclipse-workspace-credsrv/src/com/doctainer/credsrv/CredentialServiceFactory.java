/*
 CredentialServerFactory.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer kungfu.
*/
package com.doctainer.credsrv;

import java.io.IOException;
import java.util.Map;

import com.doctainer.credsrv.impl.ScatteredProps;
/**
 * 
 *
 * (C) doctainer 2001-2007<br/><br/>
 * 
 *
 * @author mausi
 *
 * <h4>change log</h4>
 * <ul>
 *  <li>jm711credential-server-config
 * <ul>
 */
public class CredentialServiceFactory {
 private static final String propertyPath= 
  // "/programme_wa/ldapbrowser/*.cfg";
  "usr/credsrv.properties";

 public static CredentialService getCredentialService() throws IOException {
  return new ScatteredProps(propertyPath);
 }
 
 /**
  * <h3>Description</h3>
  * <p>Create a credential server that scans all available drives ...</p>
  * @param configString
  * @return implementation of CredentialServer
  * @throws IOException
  */
 public static CredentialService getCredentialService(String configString) throws IOException{
  return new ScatteredProps(configString);
 }
 
 
}
