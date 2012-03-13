/*
 CredentialServer.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer kungfu.
*/
package com.doctainer.credsrv;

import java.io.IOException;
import java.util.Map;
/**
 * <h3>Purpose</h3>
 * <p></p>
 * 
 * @author maushake
 * 
 * <pre>
 *  #dt1109-crdsrv
 *  #dt1104-inapprehensible
 * </pre>
 */
public interface CredentialService {
 /**
  * <h3>Purpose</h3>
  * <p>Return credentials needed to access a deployment target - this may be anything for instance a web site or a certain backend system or whatsoever</p>	
  * @param deploymentTargetName
  * @return credentials
  * 
  * @throws IOException
  */
 Map<String, String> getCredentials(String deploymentTargetName) throws IOException;
}
