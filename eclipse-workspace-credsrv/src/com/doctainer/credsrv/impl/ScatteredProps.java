/*
 CredentialServerImpl.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer kungfu.
 */
package com.doctainer.credsrv.impl;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import com.doctainer.credsrv.CredentialService;

/*
 *  This {@link CredentialServer} implementation picks up a set of property files each of which containing credential information. 
 *  One such property file may reside on your hard disk, another may be located on another device like a usb stick.
 *  If you have more than one file on different devices you can arrange that a whole secret is scattered
 *  to all the files, so the access to only one device won't reveal the secret.
 *  The format for keys and values in property files is as follows
 *  <Env>.<Real Key> = <Value>
 *  <Env> names the deployment target like PROD or whatever you choose.
 *  <Real Key> denotes a key like "password"
 *  <Value> denotes the value or part of it.
 *  For instance, if you have
 *  PROD.password = wert
 *  in a property file
 *  and ask the credential server for credentials of PROD then 
 *  you will get a map which maps "password" to "wert".
 *  And if you have 
 *  PROD.password = 123 
 *  in another file, then you will get a map which maps "password" to "wert123" or to "123wert".
 *  Wether you get "wert123" or "123wert" depends on which of the property files comes first: currently property files are scanned
 *  in order of increasing drive letters.
 */
public class ScatteredProps implements CredentialService {
 final Map<String, String> credMap;

 public ScatteredProps(String propertyPath) throws IOException {
  credMap = createCredMap(propertyPath);
 }

 /**
  * implements {@link CredentialServer}
  * <p>
  *  Produces a map based on credMap and the name of a deployment target.   
  * </p>
  * @param deploymentTargetName - denotes a deployment target
  * 
  */
 public Map<String, String> getCredentials(String deploymentTargetName) throws IOException {
  Map<String, String> result = new HashMap<String, String>();
  Iterator<Map.Entry<String, String>> iter = credMap.entrySet().iterator();
  while (iter.hasNext()) {
   Map.Entry<String, String> item = iter.next();
   String itemKey = item.getKey();
   if (itemKey.startsWith(deploymentTargetName)) {
    int keyl = deploymentTargetName.length();
    int itemKeyl = itemKey.length();
    if (keyl < itemKeyl && itemKey.charAt(keyl) == '.') {
     String resultKey = new String(itemKey.substring(keyl + 1));
     result.put(resultKey, item.getValue());
    } // if
   } // if item starts with key
  } // while
  return result;
 }

 private static Map<String, String> createCredMap(String propertyPath) throws IOException {
  Map<String, String> result = new HashMap<String, String>();
  accumulateCredMap(result, propertyPath);
  return result;
 }

 private static void accumulateCredMap(Map<String, String> result, String propertyPath) throws IOException {
  // scan special syntax:
  int lastIdxOfSlash = lastIndexOfSlash(propertyPath);
  if (lastIdxOfSlash != -1) {
   int lastIdxOfStar = propertyPath.lastIndexOf('*');
   if (lastIdxOfSlash + 1 == lastIdxOfStar) {
    // determine directory and suffix, then call createMap
    // from directories.
    int lastIndexOfDot = propertyPath.lastIndexOf('.');
    String directory = propertyPath.substring(0, lastIdxOfSlash);
    String suffix;
    if (lastIdxOfStar + 1 == lastIndexOfDot) {
     suffix = propertyPath.substring(lastIndexOfDot + 1);
    } else
     suffix = null;
    accumulateCredMapFromDir(result, directory, suffix);
    return;
   }
  }
  File[] drives = File.listRoots();
  int n_drives = drives.length;
  for (int i = 0; i < n_drives; i++) {
   String name;
   try {
    name = drives[i].getCanonicalPath();
    // System.out.println("drive path: "+name);
    // produces output like:
    // drive path: C:\
    // drive path: Z:\
   } catch (IOException e) {
    // skip: drive not ready/available
    continue;
   }
   String candidatePath = name + propertyPath;
   File candidate = new File(candidatePath);
   if (candidate.exists())
    accumulateCredMapFromFile(result, candidate);
  } // for
 } // createCredMap

 private static void accumulateCredMapFromDir(Map<String, String> result, String directory, String suffix) throws IOException {
  File[] drives = File.listRoots();
  for (int i = 0, n_drives = drives.length; i < n_drives; i++) {
   String name;
   try {
    name = drives[i].getCanonicalPath();
    // System.out.println("drive path: "+name);
    // produces output like:
    // drive path: C:\
    // drive path: Z:\
   } catch (IOException e) {
    // skip: drive not ready/available
    continue;
   }
   String candidatePath = name + directory;
   File candidate = new File(candidatePath);
   if (!candidate.isDirectory())
    continue;
   String[] fileList = candidate.list(new IncludesSuffix(suffix));
   int suffixLen = suffix == null ? -1 : suffix.length();
   int n_fileList = fileList.length;
   for (int f = 0; f < n_fileList; f++) {
    String fileName = fileList[f];
    Properties props = getProperiesFromFile(new File(candidatePath + '/' + fileName));
    // now derive a key prefix from filename: for example if "PRODL.properties" were the filename,
    // the key prefix would be "PRODL." and you would provide "PRODL" as argument to the
    // getCredentials(key) method to get all key-value pairs of all scanned
    // files named PRODL.properties 
    String propertyPrefix = suffix == null ? fileName : fileName.substring(0,fileName.length()-suffixLen);
    accumulateProperties(result, props, propertyPrefix);
   } // for - scanning through directory
  } // for
 }
 
 /**
  * Note, this method is borrowed from FileStuff where it is maintained
  * 
  * @param file
  * @return the properties
  * @throws IOException
  * 
  * <pre>
  *  jm711getProperties
  * </pre>
  */
 public static Properties getProperiesFromFile(File file) throws IOException {
  Properties props = new Properties();
  BufferedInputStream bIS = new BufferedInputStream(new FileInputStream(file));
  try {
   props.load(bIS);
   return props;
  } finally {
   bIS.close();
  }
 }
 private static void accumulateCredMapFromFile(Map<String, String> result, File candidate) throws IOException {
  Properties props = getProperiesFromFile(candidate);
  accumulateProperties(result,  props);
 }
 
 private static void accumulateProperties(Map<String, String> result,  Properties props) {
  Enumeration enum_ = props.propertyNames();
  while (enum_.hasMoreElements()) {
   String propKey = (String) enum_.nextElement();
   String propValue = props.getProperty(propKey);
   result.put(propKey, propValue);
  }
 }
 private static void accumulateProperties(Map<String, String> result, Properties props, String prefix) {
  Enumeration enum_ = props.propertyNames();
  while (enum_.hasMoreElements()) {
   String propKey = (String) enum_.nextElement();
   String propValue = props.getProperty(propKey);
   result.put(prefix+propKey, propValue);
  }
 }
 
 private static int lastIndexOfSlash(String path) {
  int kLinux = path.lastIndexOf('/');
  int kWindows = path.lastIndexOf('\\');
  if (kLinux < kWindows)
   return kWindows;
  if (kWindows < kLinux)
   return kLinux;
  return -1;
 }
}
