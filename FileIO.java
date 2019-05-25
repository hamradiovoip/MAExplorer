/** File: FileIO.java */

import java.util.zip.*;
import java.util.Vector;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.net.URL;
import java.net.MalformedURLException;
import java.io.*;
import java.io.FileReader;
import java.lang.String;
import java.lang.*;
import java.awt.Color;
import java.text.SimpleDateFormat;
import java.lang.*;
import java.util.*;
import java.io.*;
import java.io.File;
import java.util.zip.*;


/**
 * This file I/O class includes methods to do file I/O to a local disk or URL.
 * The disk (standalone file:// mode) or through a URL (standalone or
 * applet http://) connects to a Web server. If you are doing a URL I/O is used,
 * then it uses JavaCGIbridge to read  files. If you are using password
 * protection or writing a file to a URL, then it needs a cooperating CGI
 * process running the the target Web server.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/07/21 19:40:15 $   $Revision: 1.13 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class FileIO
{ /* FileIO  */
  /* [TODO] 1. add ZipReader and ZipURLreader */
  
  /** link to global MAExplorer instance */
  private MAExplorer
    mae;
  
  /** standalone log file name */
  private String
    logFileName;
  /** standalone log file descriptor */
  private File
    logFile;
  /** standalone log file writer */
  FileWriter
    logFileWriter;
  /** standalone log file data buff */
  private char[]
    logDataBuf;
  /** size of standalone log file data buff */
  private int
    logDataBufSize;
    
  
  /**
   * FileIO() - constructor to create fio instance and to close the log file.
   * @param mae is instance of MAExplorer
   * @see #closeLogFile
   */
  FileIO(MAExplorer mae)
  { /* FileIO */
    this.mae= mae;
    closeLogFile();     /* reset the log vars. */
  } /* FileIO */
  
  
  /**
   * readData() - read data from URL or local file depending on prefix.
   * @param fileName is the full path filename to read the data. If it starts with
   *         "http://" then it should be a URL.
   * @param msg to display while reading the file
   * @return string data for entire file if succeed, else null if fail.
   * @see Util#showMsg
   * @see #cacheRead
   * @see #cacheWrite
   * @see #readFileFromDisk
   * @see #readFileFromUrl
   */
  String readData(String fileName, String msg)
  { /* readData */
    String sR= null;
    if(fileName==null)
      return(null);
    
    /*
    if(mae.CONSOLE_FLAG)
      logMsgln("FIO-RD fileName="+fileName+"\n   msg=" + msg );
     */
    
    if(fileName.startsWith("http://"))
    { /* Read from URL if it is a http address */
      Util.showMsg(msg);
      if(mae.cacheFIOflag)
        sR= cacheRead(fileName);             /* try to get from cache */
      boolean notInCacheFlag= (sR==null);
      if(sR==null)
        sR= readFileFromUrl(fileName,false); /* get from URL if not
                                              * using cache or if
                                              * cacheRead failed */
      if(notInCacheFlag && mae.cacheFIOflag)
        cacheWrite(fileName,sR);            /* try to put into cache */
    }
    else
    { /* read from disk */
      Util.showMsg(msg);
      //Util.printCurrentMemoryUsage("Before fio.readFileFromDisk["+fileName+"]");
      sR= readFileFromDisk(fileName);
      //Util.printCurrentMemoryUsage("After fio.readFileFromDisk["+fileName+"]");
    }
    
    return(sR);
  } /* readData */
  
  
  /**
   * writeData() - write data to a URL or local file depending on file name prefix.
   * @param fileName is the full path filename to write the data
   * @param msg to display while writing the file
   * @param data is the string to write to the file.
   * @return string data for entire file if succeed, else null if fail.
   * @see Util#showMsg
   * @see #writeFileToDisk
   * @see #writeFileFromUrl
   */
  boolean writeData(String fileName, String msg, String data)
  { /* writeData */
    boolean flag= false;
    if(fileName==null)
      return(false);
    
    if (fileName.startsWith("http://"))
    { /* write to URL if it is a http address */
      Util.showMsg(msg);
      flag= writeFileFromUrl(mae.cfg.writeFileCGIurl, fileName, data);
    }
    else
    { /* write to disk */
      Util.showMsg(msg);
      flag= writeFileToDisk(fileName, data);
    }
    
    return(flag);
  } /* writeData */
  
  
  /**
   * readFileFromDisk() - read file from disk and return entire file as String
   * @param fileName is the full path filename to read the data
   * @return string data for entire file if succeed, else null if fail.
   * @see #logMsgln
   * @see #readZipFileFromDisk
   */
  String readFileFromDisk(String fileName)
  { /* readFileFromDisk */
    if(fileName==null)
      return(null);
    String sR;
    File  f;
    RandomAccessFile rin= null;
    byte dataB[]= null;
    int size;
    
    if(fileName.endsWith(".zip"))
    { /* read it as a ZIP file and unzip the string */
      sR= readZipFileFromDisk(fileName, fileName);
      return(sR);
    }
    
    /* Otherwise, just read it as unpacked file */
    try
    {
      f= new File(fileName);
      if(!f.canRead())
      {
        logMsgln("FIO-RFFD Can't read ["+fileName+"]");
        return(null);
      }
      
      if(!f.isFile())
      {
        logMsgln("FIO-RFFD File not found ["+fileName+"]");
        return(null);
      }
      
      if(! mae.osName.equals("MacOS"))
        rin= new RandomAccessFile(f,"r");
      else
      { /* handle problem with MacOS8/9 with short 32 char filenames */
        try
        {
          rin= new RandomAccessFile(f,"r");
        }
        catch(Exception eMacOS)
        { /* try short file name for macOS */
          String shortFileName= fileName;   /* default */
          int
            lastDelimIdx= fileName.lastIndexOf(mae.dynFileSeparator),
            fileNameLth= fileName.length(),
            nameLth= (fileNameLth-lastDelimIdx)+1,
            shortLth= lastDelimIdx+ Math.min(32,nameLth);
          if(lastDelimIdx!=-1 && lastDelimIdx<lastDelimIdx)
            shortFileName= fileName.substring(1,shortLth);
          f= new File(shortFileName);
          rin= new RandomAccessFile(f,"r");
        } /* try short file name for macOS */
      } /* handle problem with MacOS8/9 with short 32 char filenames */
      
      size= (int) f.length();
      dataB= new byte[size];          /* make char array exact size needed! */
      rin.readFully(dataB);
      
      rin.close();                    /* done reading */
      f= null;
      System.runFinalization();
      System.gc();
      
      sR= new String(dataB);          /* convert String from char[]*/
      dataB= null;
      System.runFinalization();
      System.gc();
      return(sR);
    }
    
    catch (SecurityException e)
    {
      logMsgln("FIO-RFFD secur.Excep.["+fileName+"] "+e);
      return(null);
    }
    
    catch (FileNotFoundException e)
    {
      logMsgln("FIO-RFFD FileNotFoundExcep.["+fileName+"] "+e);
    }
    catch (IOException e)
    {
      logMsgln("FIO-RFFD IOExcep.["+fileName+"] "+e);
    }
    
    return(null);             /* error */
  } /* readFileFromDisk */
  
  
  /**
   * readZipFileFromDisk() - [TODO] read Zip file from disk and return it as a String.
   * [NOTE] Could use this method for unpacking a RDBMS data sets from an array Web server...
   * @param fileName is the full path zip filename to read the data
   * @param entryName is the particular entry to read from the zip file
   * @return string data for entire file if succeed, else null if fail.
   * @see #logMsgln
   */
  String readZipFileFromDisk(String fileName, String entryName)
  { /* readZipFileFromDisk */
    String sR;
    File f;
    ZipEntry ze;
    ZipFile zf;
    InputStream is;
    ZipInputStream zin= null;
    byte[] data;
    int
      size,
      chars_read;
    
    try
    {
      f= new File(fileName);
      if(!f.canRead())
      {
        logMsgln("FIO-RZFFD Can't read Zip file ["+fileName+"]");
        return(null);
      }
      
      if(!f.exists())
      {
        logMsgln("FIO-RZFFD Zip file not found ["+fileName+"]");
        return(null);
      }
      
      zf= new ZipFile(f);
      ze= zf.getEntry(entryName);
      if(ze==null)
      {
        logMsgln("FIO-RZFFD Zip entry not found ["+entryName+"]");
        return(null);
      }
      
      is= zf.getInputStream(ze);
      zin= new ZipInputStream(is);
      zin.getNextEntry();
      
      size= (int) ze.getSize();
      data= new byte[size];
      chars_read= 0;
      
      while(chars_read < size)
        chars_read += zin.read(data, chars_read, (size - chars_read));
      zf.close();                   /* done reading */
      sR= new String(data);         /* convert String from char[]*/
      return(sR);
    }
    
    catch (SecurityException e)
    {
      logMsgln("FIO-RZFFD secur.Excep.["+fileName+"]");
      // logMsgln((""+e));
      return(null);
    }
    
    catch (FileNotFoundException e)
    {
      logMsgln("FIO-RZFFD FileNotFoundException ["+fileName+"] "+e);
    }
    catch (ZipException e)
    {
      logMsgln("FIO-RZFFD ZipExcepion format ["+fileName+"] "+e);
    }
    catch (IOException e)
    {
      logMsgln("FIO-RZFFD IOException ["+fileName+"] "+e);
    }
    
    return(null);             /* error */
  } /* readZipFileFromDisk */
  
  
  /**
   * extractZipFiles() - extract files from zip file. Create the
   * dirs if needed.
   * @param zipfileName Name of zip file including full path
   * @param rootPath Directory to extract the zip file 
   */
  public boolean extractZipFiles(String zipFileName, String rootPath)
  {/* extractZipFiles */
    
    if(zipFileName==null || rootPath==null)
      return(false);   /* error */
    boolean isZipEntryDirFlag= false;
    final int bufSize= 2048;
    BufferedOutputStream outputStream= null;
    FileInputStream fis;
    ZipInputStream zis;
    ZipEntry entry;
    String
      fileSep= System.getProperty("file.separator");
    /* open zip file */
    try 
    {
      fis= new  FileInputStream(zipFileName);
      zis= new ZipInputStream(new BufferedInputStream(fis));
    }
    catch(Exception e)
    {
      System.out.println("Error w/ zip file '" + zipFileName + "' " + e);  
      return(false);   /* error */
    }
     try 
     {
       while((entry = zis.getNextEntry()) != null) 
       { /* unzip all files */
         int count= 0;
         byte data[]= new byte[bufSize];
         boolean dirFlag= false;
         File dir= null;
         String fullPath= rootPath + entry.getName(); /* create fullpath */
         isZipEntryDirFlag= entry.isDirectory();
         
         String newDir= "";
         
         if(mae.DEBUG)
           System.out.println("Unpacking file: " + fullPath);
         
         File f= new File(fullPath);
         String dirPath= f.getAbsolutePath(); 
         boolean isDirFlag= f.isDirectory();
         File dp = new File(dirPath);/* create dir file */
         String fileName= dp.getName();/* get file name */
        
         if(isZipEntryDirFlag)
         { /* create dir  */
           dir= new File(dirPath);
           dir.mkdirs(); /* create the entire dir tree */
         } /* create dir  */
         else  
         { /* if dir does not exist create it; rmv file name first */
           int i= dirPath.indexOf(fileName);
           int dirSize= dirPath.length();
           char[] buf= new char[dirSize];
           dirPath.getChars(0,i,buf,0);
           for(int x= 0; x<i ;x++)
             newDir += buf[x];
           dir= new File(newDir);
           dirFlag= dir.mkdirs(); /* create the entire dir tree */
         } /* if dir does not exeist create it; rmv file name first */
         
          if(!isZipEntryDirFlag)
          {
            /* write out file from zipfile to newly created dir */
            FileOutputStream fos= new FileOutputStream(fullPath);
            outputStream= new BufferedOutputStream(fos, bufSize);
            
            while((count = zis.read(data, 0, bufSize))!= -1)
              outputStream.write(data, 0, count);
            
            /* close file output stream */
            outputStream.flush();
            outputStream.close();
          }
       } /* unzip all files */
       
       zis.close();/* close zip entry */
     }
     catch(Exception e) 
     {
       System.out.println("extractZipFiles(): Error unzipping files: "+ e);
       return(false);   /* error */
     }
    return(true);
  }/* extractZipFiles */

  
  /**
   * readFileFromUrl() - read data from http:// URL, using JavaCGIBridge.
   * It returns string if successful. If it fails with a 401 UNAUTHORIZED
   * error, it trys again using the mae.(userName,userPasswd) if it
   * exists. If it does not exist, it pops up the dialog box to get it.
   * If it tries the password and it fails, then fails returning null.<BR>
   * [BUG] security does not always work correctly.
   * @param URLaddress is the full URLaddress to read the data
   * @param sendAuthReqFlag is the authorization required (optional)
   * @return string data for entire file if succeed, else null if fail.
   * @see JavaCGIBridge
   * @see JavaCGIBridge#getRawCGIData
   * @see #logMsgln
   */
  String readFileFromUrl(String URLaddress, boolean sendAuthReqFlag)
  { /* readFileFromUrl */
    String
      data= null,
      buf= null;
    URL u;
    JavaCGIBridge jcb;
    
    /* Add "user:passwd@" before host name of URL if using
     * "Authentication: Basic encode64('user:passwd')"
     */
    try
    {
      if (URLaddress.startsWith("http")) /* if it is a http address */
        u= new URL(URLaddress);   /* get URL */
      else
        return(null);             /* error */
      
      if(u != null)
      { /* get the file from the server */
        if(sendAuthReqFlag)
        { /* must send username and password to try again */
          if(mae.userName.length()==0 || mae.userPasswd.length()==0)
          { /* popup window to request [userName,userPasswd] */
            /* [TODO] add popup code to get and test [u,p] */
            /* Login the user when required */
            mae.us.doLogin("", false);  /* get (uN,uPW) and validate */
          }
          jcb= new JavaCGIBridge(u, mae.userName, mae.userPasswd);
          //jcb= new JavaCGIBridge();
        }
        else
          jcb= new JavaCGIBridge(u);  /* no authentication */
        //jcb= new JavaCGIBridge();  /* no authentication */
        
        buf= jcb.getRawCGIData(u);    /* get raw data via jcb */
        if(buf==null || buf.equals("*NOT-FOUND*"))
          return(null);
        else if(buf.startsWith("*401 UNAUTHORIZED*"))
        { /* call it recursively ONE time */
          if(sendAuthReqFlag)
            return(null);   /* failed, since only 1 retry! */
          else
          { /* retry it ONE time */
            data= readFileFromUrl(URLaddress,true);
          }
        }
      } /* get the file from the server */
    }
    
    catch (MalformedURLException e)
    {
      logMsgln("FIO-RFFU MalformedURLExcep.: " + e);
      return(null);
    }
    catch (JavaCGIBridgeTimeOutException e)
    {
      logMsgln("FIO-RFFU HTTP Req.Timed-Out: " + e);
      return(null);
    }
    
    if(buf != null) /* error if null */
      data= new String(buf); /* to be returned as string */
    
    return(data);
  } /* readFileFromUrl */
  
  
  /**
   * writeFileToDisk() - write string data to a local disk file.
   * @param fileName is the full path filename to write the data
   * @param data is the string to write to the file.
   * @return string data for entire file if succeed, else null if fail.
   * @see #logMsgln
   */
  boolean writeFileToDisk(String fileName, String data)
  { /* writeFileToDisk */
    String sR;
    File f;
    FileWriter out= null;
    char[] dataBuf;
    int size= data.length();
    
    try
    { /* try to write it */
      f= new File(fileName);
      out= new FileWriter(f);
      if(!f.canWrite())
        return(false);
      dataBuf= new char[size];
      for(int i=0;i<size;i++)
        dataBuf[i]= data.charAt(i);
      out.write(dataBuf, 0, size);
      out.close();                       /* done writing */
      return(true);
    }
    catch (SecurityException e)
    {
      logMsgln("FIO-WFTD secur.Excep.["+fileName+"] "+e);
    }
    catch (FileNotFoundException e)
    {
      logMsgln("FIO-WFTD fileNotFound["+fileName+"] "+e);
    }
    catch (IOException e)
    {
      logMsgln("FIO-WFTD IOexcep.["+ fileName+"] "+e);
    }
    
    return(false);             /* error */
  } /* writeFileToDisk */
  
  
  /**
   * writeFileFromUrl() - write string data to http URL, using JavaCGIBridge.
   * @param URLaddress is the full CGI URL to write the data
   * @param fileName is the filename on the server
   * @param data is the string to write to the file.
   * @return string data for entire file if succeed, else null if fail.
   * @see JavaCGIBridge
   * @see JavaCGIBridge#setThreadJavaCGIBridgeTimeOut
   * @see JavaCGIBridge#addFormValue
   * @see JavaCGIBridge#getRawCGIData
   * @see #logMsgln
   */
  boolean writeFileFromUrl(String URLaddress, String fileName, String data )
  { /* writeFileFromUrl */
    String buf= null;
    URL u= null;
    JavaCGIBridge jcb= new JavaCGIBridge();
    
    try
    {
      Hashtable formVars= new Hashtable();
      if (URLaddress.startsWith("http")) /* if it is a http address */
        u= new URL(URLaddress);         /* get URL */
      if(u != null)
      {
        jcb.setThreadJavaCGIBridgeTimeOut(10000);
        jcb.addFormValue(formVars,"fileName",fileName);
        jcb.addFormValue(formVars,"data",data);
        buf= jcb.getRawCGIData(u);    /* get raw data via jcb */
        if(buf.startsWith("ok"))
          return(true);
        else return(false);
      }
      else
        return(false);                    /* error */
    }
    catch (MalformedURLException e)
    {
      logMsgln("FIO-WFFU MalformedURLExcep.: " + e);
    }
    catch (JavaCGIBridgeTimeOutException e)
    {
      logMsgln("FIO-WFFU HTTP Req.Timed-Out: " + e);
    }
    
    return(false);
  } /* writeFileFromUrl */
  
  
  /**
   * createCacheFileName() - create cache file name.
   * @param fullFilePath is the full path filename to read the data
   * @return string data for entire file if succeed, else null if fail.
   */
  private String createCacheFileName(String fullFilePath)
  { /* createCacheFileName */
   /*
    * [TODO] add code to name files when using complex CGI URL...
    * Issues: [add DBname, project? what if multiple projects?
    *          quant file sample name, etc.
    *          We need to map cfg.baseCgiURLxxxx to cfg.xxxFile names.]
    */
    
    String separator= mae.dynFileSeparator;
    int idx= fullFilePath.lastIndexOf(separator);
    if(idx==-1)
      return(null);
    String
      file= fullFilePath.substring(idx+1),
      cacheFile= mae.cacheDir + file;
    return(cacheFile);
  } /* createCacheFileName */
  
  
  /**
   * cacheRead() - read data file from Cache file it it exists.
   * Strip off file name from end of fullPath.
   * @param fullFilePath is the full path filename to read the data
   * @return string data for entire file if succeed, else null if fail.
   * @see #createCacheFileName
   * @see #readFileFromDisk
   */
  private String cacheRead(String fullFilePath)
  { /* cacheRead */
    try
    {
      String cacheFile= createCacheFileName(fullFilePath),
      sR= readFileFromDisk(cacheFile);
      if(sR!=null && sR.length()==0)
        return(null);
      return(sR);
    }
    catch (Exception e)
    {
      return(null);
    }
  } /* cacheRead */
  
  
  /**
   * cacheWrite() - Write string into file in local cache.
   * @param fullFilePath is the full path filename to write the data
   * @param data is the data to write to the file
   * @return true if succeed
   * @see #createCacheFileName
   * @see #writeFileToDisk
   */
  private boolean cacheWrite(String fullFilePath, String data)
  { /* cacheWrite */
    try
    {
      String cacheFile= cacheFile= createCacheFileName(fullFilePath);
      boolean flag= writeFileToDisk(cacheFile, data);
      return(flag);
    }
    catch (Exception e)
    {
      return(false);
    }
  } /* cacheWrite */
  
  
  /**
   * closeLogFile() - close log file if it is open, but always reset the log state
   */
  void closeLogFile()
  { /* closeLogFile */
    try
    {
      if(logFileWriter!=null)
        logFileWriter.close();  /* close file */      
    }
    catch (IOException e)
    {
    }
    
    logFileName= null;        /* name of standalone log file */
    logFile= null;            /* standalone log file descriptor */
    logFileWriter= null;      /* standalone log file writer */
    logDataBuf= null;         /* standalone log file data buff */
    logDataBufSize= 0;        /* size of standalone log file data buff */
  } /* closeLogFile */
  
  
  /**
   * logMsg() - write msg to opened log file if stand-alone else print to Java console.
   * Do NOT append a "\n".
   * If applet, just do System.out.print() call to console.
   * If it is not opened, then a NO-OP.
   */
  void logMsg(String msg)
  { /* logMsg() */
    if(mae.isAppletFlag || logFileWriter==null)
      System.out.print(msg);
    else logMsg(msg, logFileName,
                false /* newFileFlag*/,
                true /* appendFlag */ );
  } /* logMsg() */
  
  
  /**
   * logMsgln() - write msg+"\n" to opened log file if stand-alone
   * else if applet, just do System.out.println() call to java console.
   * If it is not opened, then a NO-OP.
   * @param msg to write to log file
   */
  void logMsgln(String msg)
  { /* logMsgln() */
    if(mae.isAppletFlag || logFileWriter==null)
      System.out.println(msg);
    else logMsg(msg+"\n", logFileName, false /* newFileFlag*/,
                true /* appendFlag */ );
  } /* logMsgln() */
  
  
  /**
   * logMsg() - write msg to log file. The \n must be added by the caller.
   * First time over write, then append.
   * @param msg to write,  add \n if want it in the file
   * @param logFileName is name of log file
   * @param newFileFlag to force new file
   * @param appendFlag if want to append, else new file
   */
  void logMsg(String msg, String logFileName, boolean newFileFlag, boolean appendFlag)
  { /* logMsg() */
    int size= msg.length();
    this.logFileName= logFileName;
    
    if(mae.DEBUG)
      System.out.print(msg);
    /*
    if(mae.CONSOLE && !mae.isAppletFlag)
      System.out.println(msg);
     */
    
    if(newFileFlag)
    { /* Delete the file if newFileFlag is true */
      try
      {
        File
          oldLogFile= new File(logFileName);
          oldLogFile.delete();
      }
      catch(Exception e)
      {
        //System.out.println("Can't delete file");
      }
    } /* Delete the file if newFileFlag is true */
    
    try
    {
      /* open file if not already opened */
      if(logFile==null || newFileFlag)
        logFile= new File(logFileName);
      if(logFileWriter==null || newFileFlag)
        logFileWriter= new FileWriter(logFileName, appendFlag);
      
      if(!logFile.canWrite())
        return;
      if(logDataBufSize<size)
      { /* grow only if need to */
        logDataBufSize= size+1;
        logDataBuf= new char[logDataBufSize];
      }
      
      /* [TODO] rewrite as System.arraycopy() to copy substring directly in one call */
      for(int i=0; i<size; i++)
        logDataBuf[i]= msg.charAt(i); /* create buffer to write */
      
      logFileWriter.write(logDataBuf, 0, size); /* write (not size+1) */
      
      if(!appendFlag)
        logFileWriter.close();		   /* close file */
      else
        logFileWriter.flush();		   /* flush data file */
      return;
    }
    
    /* Handle trouble  to console since NO FILE!!! */
    catch (SecurityException e)
    {
      System.out.println("FIO-LM-securityExcep.["+logFileName+"] "+e);
    }
    catch (FileNotFoundException e)
    {
      System.out.println("FIO-LM-fileNotFound["+logFileName+"] "+e);
    }
    catch (IOException e)
    {
      System.out.println("FIO-LM-IOexcep. ["+logFileName+"] "+e);
    }
  } /* logMsg */
  
  
  /**
   * copyFile() - binary copy of one file or URL to a local file
   * @param srcName is either a full path local file name or 
   *        a http:// prefixed URL string of the source file.
   * @param dstName is the full path of the local destination file name
   * @param optUpdateMsg (opt) will display message in showMsg() and 
   *        increasing ... in showMsg2(). One '.' for every 10K bytes read.
   *        This only is used when reading a URL. Set to null if not used.
   * @param optEstInputFileLth is the estimate size of the in;ut file if known
   *        else 0. Used in progress bar.
   * @return true if succeed.
   */
  public boolean copyFile(String srcName, String dstName,
                          String optUpdateMsg, int optEstInputFileLth)
  { /* copyFile */
    try
    { /* copy data from input to output file */
      FileOutputStream dstFOS= new FileOutputStream(new File(dstName));
      FileInputStream srcFIS= null;
      int
        bufSize= 20000,
        nBytesRead= 0,
        nBytesWritten= 0;
      byte buf[]= new byte[bufSize];
      
      boolean isURL= (srcName.startsWith("http://"));
      if(isURL)
      { /* Copy the file from Web site */
        if(optUpdateMsg!=null)
          Util.showMsg(optUpdateMsg);
        String
          sDots= "";
        URL url= new URL(srcName);
        InputStream urlIS= url.openStream();
        while(true)
        { /* read-write loop */
          if(optUpdateMsg!=null)
          { /* show progress every read */
            sDots += ".";
            String
              sPct= (optEstInputFileLth>0) 
                       ? ((int)((100*nBytesWritten)/optEstInputFileLth))+"% "
                       : "",
              sProgress= "Copying " + sPct + sDots;
            Util.showMsg2(sProgress, Color.white, Color.red); 
          }                                                       
          nBytesRead= urlIS.read(buf);
          if(nBytesRead==-1)
            break;         /* end of data */
          else
          {
            dstFOS.write(buf,0,nBytesRead);
            nBytesWritten += nBytesRead;
          }
        } /* read-write loop */
        dstFOS.close();
        if(optUpdateMsg!=null)
        {
          Util.showMsg("");
          Util.showMsg2("");
        }
      }
      else
      { /* copy the file on the local file system */
        srcFIS= new FileInputStream(new File(srcName));
        while(true)
        { /* read-write loop */
          nBytesRead= srcFIS.read(buf);
          if(nBytesRead==-1)
            break;         /* end of data */
          else
          {
            dstFOS.write(buf,0,nBytesRead);
            nBytesWritten += nBytesRead;
          }
        } /* read-write loop */
        srcFIS.close();
        dstFOS.close();
      } /* copy the file on the local file system */
    } /* copy data from input to output file */
    
    catch(Exception e1)
    { /* just fail if any problems at all! */ 
      System.out.println("FileIO: copyFile(): Error with URL/File: "+e1);
      return(false);
    } /* just fail if any problems at all! */
    
    return(true);
  } /* copyFile */
  
  
  /**
   * readBytesFromURL() - read binary data from URL 
   * @param srcName is either a full path local file name or 
   *        a http:// prefixed URL string of the source file.
   * @param optUpdateMsg (opt) will display message in showMsg() and 
   *        increasing ... in showMsg2(). One '.' for every 10K bytes read.
   *        This only is used when reading a URL. Set to null if not used.
   * @return a byte[] if succeed, else null.
   */
  public byte[] readBytesFromURL(String srcName, String optUpdateMsg)
  { /* readBytesFromURL */
     if(!srcName.startsWith("http://"))
       return(null);
     int
        bufSize= 20000,
        nBytesRead= 0,
        nBytesWritten= 0,
        oByteSize= bufSize;
      byte
        buf[]= null,
        oBuf[]= null;      
     URL url= null; 
     InputStream urlIS= null;
    try
    { /* copy data from input to output file */
      buf= new byte[bufSize];
      oBuf= new byte[bufSize];
      
      /* Copy the file from Web site */
      if(optUpdateMsg!=null)
        Util.showMsg(optUpdateMsg);
      String sDots= "";
      try
      {
       url= new URL(srcName);
       urlIS= url.openStream();
      }
      catch(IOException ioe)
      {
         System.out.println("readBytesFromURL(): Error connecting to URL: " + ioe);
         return(null);         
         /* [TODO] retry connect? */
      }
      while(true)
      { /* read-write loop */
        if(optUpdateMsg!=null)
        { /* show progress every read */
          sDots += ".";
          String sProgress= "Reading " + sDots;
          Util.showMsg2(sProgress, Color.white, Color.red); 
        }     
        
        nBytesRead= urlIS.read(buf);
        if(nBytesRead==-1)
          break;         /* end of data */
        else
        { /* copy buf to end of oBuf */
          if(nBytesRead + nBytesWritten > oByteSize)
          { /* regrow oBuf */
            byte tmp[]= new byte[oByteSize + bufSize];
            for(int i=0;i<oBuf.length;i++)
              tmp[i]= oBuf[i];
            
            oBuf= new byte[tmp.length];
            oBuf= tmp;
            oByteSize += bufSize;
          } /* regrow oBuf */
           for(int i=0;i<nBytesRead;i++)
              oBuf[nBytesWritten++]= buf[i];
         
        }/* copy buf to end of oBuf */
      } /* read-write loop */
        
       /* Shrink oBuf to exact size needed */
      byte tmp[]= new byte[nBytesWritten];
      for(int i=0;i<nBytesWritten;i++)
        tmp[i]= oBuf[i];
      oBuf= tmp;
      
      if(optUpdateMsg!=null) 
      {
        Util.showMsg("");
        Util.showMsg2("");
      }      
    } /* copy data from input to output file */
    
    catch(Exception e1)
    { /* just fail if any problems at all! */
      System.out.println("readBytesFromURL(): Error: "+e1);
      return(null);
    }
    
    return(oBuf);
  } /* readBytesFromURL */
  
  
  /**
   * deleteLocalFile() - delete local file.
   * @param fileName to be deleted on the local file system.
   * @return false if failed.
   */
  public boolean deleteLocalFile(String fileName)
  { /* deleteLocalFile */
    try
    {
      File srcF= new File(fileName);
      if(srcF.exists())
        srcF.delete();      /* delete it first */
    }
    catch(Exception e)
    { return(false); }
    
    return(true);
  } /* deleteLocalFile */
    
  
  /*
   * deleteRecursive() - Delete enitre dir tree
   */
  public boolean deleteRecursive(File dirNameToDelete)
  { /* deleteRecursive */
    
    if(dirNameToDelete==null)
      return(false);
    else
    {
      DelFileWalker dfw= new DelFileWalker();
      dfw.walk(dirNameToDelete,true);
    }
    
    return(true);
  } /* deleteRecursive */
  
  
  
} /* end of class FileIO  */

/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*       FileWalker Class for recursive dir traversal           */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

class DelFileWalker extends Observable implements Observer
{
  
  /*  DelFileWalker() - constructor
   *
   */
  public DelFileWalker()
  {
    
  }
  
  
  /**
   * walk() - get file names from dir, visit each file
   */
  void walk(File dir, boolean includeDirectories)
   {/* walk */
     if (dir.isDirectory()) 
     { /* if dir */
       if (includeDirectories) 
       {/* Notify the observers */
         
         setChanged();
         notifyObservers(dir);
       }/* Notify the observers */
       
       String[] filenames = dir.list();
       
       /* Visit each file in this directory */
       for (int i= 0; i < filenames.length; i++) 
         walk(new File(dir, filenames[i]), includeDirectories);
       dir.delete();
     } /* if dir */
     else 
     {
       dir.delete();
       setChanged();
       notifyObservers(dir);
     }
  }
  
  /**
   * update() - delete file
   */
  public void update(Observable o, Object arg)
  { /* update */
    File file = (File) arg; 
   
    int len; 
     try
      { 
        file.delete();
      } 
      catch(Exception e)
      {       
        System.out.println("Error deleting file: " + e);
        
      } 
  
  }/* update */

  
/* walk */
  
}/* end class FileWalker */


