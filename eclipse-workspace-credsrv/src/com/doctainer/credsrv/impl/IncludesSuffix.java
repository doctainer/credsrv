/*
 IncludesSuffix.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer io.
*/
package com.doctainer.credsrv.impl;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

/**
 * 
 * This class has been borrowed from io.files where it is maintained.
 * 
 * @author unascribed
 * 
 * @see ExcludesSuffix
 * 
 * <ul>
 * <li>dt1109-consider-mac 
 * <li>jm1107-includeDir_-hack, 
 *  see treewalk project where FilenameFilterImpls is maintained.
 * <li>jm711IncludesExcludesSuffix
 * <li>jm710 added this filter
 * </ul>
 * 
 */
public class IncludesSuffix implements FilenameFilter {

 final String theSuffix;

 public IncludesSuffix(String suffix) {
  this.theSuffix = suffix;
 }
 
 /*
  * (Kein Javadoc)
  * 
  * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
  */
 public boolean accept(File dir, String filename) {
  File g = new File(dir, filename);
  if(g.isDirectory())
   return false;
  if(theSuffix == null)
   return true;
  int d = filename.lastIndexOf('.');
  if (d == -1)
   return false;
  // let java handle differences between operating systems. 
  // Construct a filename with the given suffix 
  // and use File to compare the constructed filename with the given one.
  String constructed = filename.substring(0, d + 1) + theSuffix;
  return g.equals(new File(dir, constructed));
 }
 
 public static String[] list(String fromDir, String suffix) throws IOException {
  File dir = new File(fromDir);
  if (!dir.isDirectory())
   throw new IOException(fromDir + " is not a directory");
  FilenameFilter fnF = new IncludesSuffix(suffix);
  String[] files = dir.list(fnF);
  int n = files.length;
  String[] result = new String[n];
  for (int i = 0; i < n; i++) {
   String f = files[i];
   String fullF = fromDir + '/' + f;
   result[i] = fullF;
  }
  return result;
 }
}
