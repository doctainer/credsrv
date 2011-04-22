/*
 IncludesSuffix.java -- <br/>(C) doctainer 2001-2007<br/><br/>
 This file is part of doctainer io.
*/
package com.doctainer.credsrv.impl;

import java.io.File;
import java.io.FilenameFilter;


/**
 * 
 * This class has been borrowed from io.files where it is maintained.
 * 
 * @author unascribed
 * 
 * @see ExcludesSuffix
 * 
 * <ul>
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
 private String concat(File arg0, String filename) {
  String dirPath = arg0.getAbsolutePath();
  int n_dirPath = dirPath.length();
  if(n_dirPath != 0) {
   char t = dirPath.charAt(n_dirPath-1);
   if(t == '/' || t == '\\')
    return dirPath+filename;
  } 
  return dirPath+'/'+filename;
 }

 /*
  * (Kein Javadoc)
  * 
  * @see java.io.FilenameFilter#accept(java.io.File, java.lang.String)
  */
 public boolean accept(File arg0, String filename) {
  File g = new File(concat(arg0,filename));
  if(g.isDirectory())
   return false;
  if(theSuffix == null)
   return true;
  int d = filename.lastIndexOf('.');
  // System.out.println("includes suffix: "+filename);
  if (d == -1)
   return false;
  String foundSuffix = filename.substring(d + 1);
  return theSuffix.equals(foundSuffix);

 }

}