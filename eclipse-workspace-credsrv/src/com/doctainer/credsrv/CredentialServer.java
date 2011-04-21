/*
 CredentialServer.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer kungfu.
*/
package com.doctainer.credsrv;

import java.io.IOException;
import java.util.Map;

public interface CredentialServer {
 Map<String, String> getCredentials(String key) throws IOException;
}
