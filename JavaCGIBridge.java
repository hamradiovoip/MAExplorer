/** File: JavaCGIBridge.java*/

// package COM.Extropia.net;

import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Vector;

import java.net.*;
import java.net.URL;
import java.net.URLConnection;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.net.MalformedURLException;
import java.net.Socket;

import java.io.*;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.io.IOException;

/**
 * The JavaCGIBridge class POSTS and GETS data from URLs using various helper methods.  It also provides the capability of 
 * timing out if the connection takes too long to transfer data by 
 * using threads to monitor whether the data is
 * taking too long to transfer (implements Runnable).
 *<P>
 * @author: Copyright Info: This class was written by Gunther Birznieks
 * (birzniek@hlsun.redcross.org) and Selena Sol (selena@eff.org)
 * having been inspired by countless other Perl authors.
 *
 *  1.00 08/10/97 
 *
 * @(#)JavaCGIBridge.java 1.00 08/10/97
 * Feel free to copy, cite, reference, sample, borrow, resell
 * or plagiarize the contents.  However, if you don't mind,
 * please let Selena know where it goes so that we can at least
 * watch and take part in the development of the memes. Information
 * wants to be free; support public domain freeware.  Donations are
 * appreciated and will be spent on further upgrades and other public
 * domain programs.
 *<P>
 * <B>PFL-TODO</B> 
 *<PRE>
 * 1. (3-18-99) add binary I/O. See getRawCGIDataByte() and
 *     additions to getHttpRequestInThread().
 * 2. (8-7-00) add "extends HttpURLConnection" so can write
 *      connect(), disconnect(), etc. 
 *    See pg929, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
 *    With connect() we can
 *    better control the initial HTTP request to include the
 *    "Authentication: Basic ..." line. Check into the implications.
 *</PRE>
 *<P>
 * This is a class that POSTS and GETS data from URLs using various
 * helper methods.  This class also provides the capability of
 * timing out if the connection takes too long to transfer data
 * by using threads to monitor whether the data is taking too long
 * to transfer (implements Runnable).
 * <P>
 * Helper methods allow you to set up form variables, get setup
 * information over a URL, get raw or pre-parsed HTML data, and more.
 * <P>
 * The getParsedData method relies on instance variables which
 * tell the parser where the data begins and ends (top and bottom
 * separators respectively).   The field and row separators inform
 * the parser where records and fields end.
 * <P><BLOCKQUOTE>
 * The default top separator is &LT!--start of data-->
 * <P>
 * The default bottom separator is &LT!--end of data-->
 * <P>
 * The default field separator is ~|~ (tilde + pipe + tilde).
 * <P>
 * The default row separator is ~|~\n (tilde + pipe + tilde + newline).
 * </BLOCKQUOTE>
 * @version     1.00, 10 Aug 1997
 * @author      Gunther Birznieks
 *<P>
 *<PRE>
 *
 * <B>Modifications including adding 401 HTTP security </B>
 * @author P. Lemkin (NCI), J. Evans(CIT), C. Santos(CIT), G. Thornwall (SAIC), 
 *         NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:45:02 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 *</PRE>
 *
 * @see JavaCGIBridgeTimeOutException
 */

public class JavaCGIBridge extends HttpURLConnection
       implements Runnable 
{

  private final static boolean 
    PFL_NEW_READ_BUF_CODE= false;     /* Future  PFL- TODO */
  
  /**
   * The field separator.  When a CGI script or HTML file
   * returns data, then the getParsedData() method will
   * know how to separate fields by looking at this variable.
   *
   * The default value is "~|~" (tilde + pipe + tilde). This is
   * considered sufficiently unlikely to appear inside of actual
   * field data that it is a pretty good field separator to parse
   * on. If the user of this class needs a different separator, it
   * may be overridden by calling the appropriate method.
   */
  private String
    fieldSeparator= "~|~";  
  
  /** The row separator.  When a CGI script or HTML file
   * returns data, then the getParsedData() method will
   * know how to separate returned rows by looking at this variable.
   *
   * The default value is "~|~\n" (tilde + pipe + tilde + newline). 
   * This is considered sufficiently unlikely to appear inside of
   * actual row data that it is a pretty good row separator to parse
   * on. If the user of this class needs a different separator, it
   * may be overridden by calling the appropriate method.
   */
  private String
    rowSeparator= "~|~\n";  
  
  /** The top of data separator.  When a CGI script or HTML file
   * returns data, then the getParsedData() method will
   * determine when to start parsing data by encountering this
   * separator.
   *
   * The default value is "<!--start of data-->" implicitly followed
   * by a newline.  That is, a newline is generally expected to follow
   * this separator.
   */
  private String
    topSeparator= "<!--start of data-->";  
  
  /** The bottom of data separator.  When a CGI script or HTML file
   * returns data, then the getParsedData() method will
   * determine when to stop parsing data by encountering this
   * separator.
   *
   * The default value is "<!--end of data-->" implicitly followed
   * by a newline.  That is, a newline is generally expected to follow
   * this separator.
   */
  private String
    bottomSeparator= "<!--end of data-->";  
  
  /** The URL That data is retrieved from.  This is an instance
   * variable rather than a parameter because the data retrieval
   * is done from a launched thread which gets no parameters.
   */
  private URL
    threadURL= null;

  /** The HTML form data for the URL that data is retrieved from.  
   * This is an instance variable rather than a parameter because
   * the data retrieval is done from a launched thread which gets
   * no parameters.
   */
  private Hashtable
    threadFormVar= null;  
  
  /** This is a flag indicating whether the URL data was
   * retrieved inside the thread or not.
   */
  private boolean
    threadCompleted= false;

  /** This is the returned URL data. It is an instance variable because
   * the run() method of the thread cannot explicitly return data.
   */
  private String
    threadCGIData= null;

  /** This is the default CGI Timeout in milliseconds.  For example,
   * a value of 10000 will tell the class to throw a
   * JavaCGIBridgeTimeOutException if the data is not retrieved within
   * 10 seconds of having initiated a data transfer.
   * PFL 2-16-99, reset to 60 seconds or 60000.
   */
  private static
    int defaultThreadJavaCGIBridgeTimeOut= 60000;

  /** This is the actual CGI Timeout value in milliseconds for the
   * instantiated object. Notice that the default value is a static
   * class variable that applies across all instances of this class.
   * This variable, on the other hand, is the actual value the object
   * uses.
   */
  private int
    threadJavaCGIBridgeTimeOut= defaultThreadJavaCGIBridgeTimeOut;  
  
  /** This is a flag that makes it so that the run() method is
   * at least semi-private for Threading.  This flag has to be
   * set true in order for another object to execute the run() method.
   */
  private boolean
    threadCanRunMethodExecute= false;
  
  
  /* --- PFL added 8-7-00 to hand 401 UNAUTHORIZED errors --- */
  /** may need because of HttpURLConnection*/
  private URL
    urlArg= null; 
  /** possibly used for Authentication */
  private String
    userName= null,             
    passwd= null;
  /** socket for fine-grain control */
  private Socket
    sock= null;                 
  private Hashtable
    keys= new Hashtable();
  private Vector
    headers= new Vector();
  private boolean
    gotHdrFlag= false;
  private static final char
    keySeparator= ':';
  /** set when return from server */
  private int 
    responseCode= 0;            
  private String
    responseMsg= null;  
   
   
  /**
   * JavaCGIBridge() - Constructs the object with no initialization.
   * This is the default (empty) constructor.
   */
  public JavaCGIBridge(URL url)
  {
    super(url);
    this.urlArg= url;
  } /* PFL added 8-7-00 */
  
  
  /**
   * JavaCGIBridge() - Constructs the object with no initialization.
   * This is the default (empty) constructor.
   * *DEPRICATE*???? - need the url for connect() call.
   */
  public JavaCGIBridge()
  { /* JavaCGIBridge */
    super(null);
    this.urlArg= null;
  } /* JavaCGIBridge */
  
  
  /**
   * JavaCGIBridge() - Constructs the object with userName, password.
   * This is the default (empty) constructor.
   * @param url
   * @param userName
   * @param passwd
   */
  public JavaCGIBridge(URL url, String userName, String passwd)
  { /* JavaCGIBridge */
    super(url);
    this.urlArg= url;
    this.userName= userName;
    this.passwd= passwd;
  } /* JavaCGIBridge */
  
  
  /**
   * JavaCGIBridgeSep() - constructs the object with new separator values
   * @param field
   * @param row,
   * @param top
   * @param bottom
   */
  public void JavaCGIBridgeSep(String field, String row, String top, String bottom)
  { /* JavaCGIBridgeSep */
    fieldSeparator= new String(field);
    rowSeparator= new String(row);
    topSeparator= new String(top);
    bottomSeparator= new String(bottom);
  } /* JavaCGIBridgeSep */
  
  
  /**
   * connect() - implements abstract method for HttpURLconnection.
   * This lets us control Authorization: requests...
   * if userName, and password were specified.
   * PFL - 8-7-00.
   * See pg929, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   */
  public void connect() throws IOException
  { /* connect */
    if(connected)
      return;             /* connected definned in URLconnection */
    
    URL url2= getURL();
    String
      hostName= url2.getHost();
      InetAddress
      dst= InetAddress.getByName(hostName);
    int port= url2.getPort();
    if(port==-1)
      port= 80;
    sock= new Socket(dst,port);
    OutputStream  out= sock.getOutputStream();
    String urlStr= url.toString();
    int idx= urlStr.indexOf("/",8);  /* find '/' after initial "http://" */
    String
      bar= basicAuthenticationEncode(userName,passwd), /* if given*/
      fileStr= (idx!=-1) ? urlStr.substring(idx) : urlStr,
      request= getRequestMethod() + " " + fileStr +  " HTTP/1.0\r\n";
    
    send(out, request);  /* send out initial request protocol */
    if(bar!=null)
      send(out, bar+"\r\n");   /* add Authorization request */
    send(out, "\r\n");
    
    connected= true;      /* set flag  that all is well */
  } /* connect */
  
  
  /**
   * disconnect() - implements abstract method for HttpURLconnection.
   * PFL - 8-7-00
   * See pg929, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   */
  public void disconnect()
  { /* disconnect */
    if(sock!=null)
      try
      { sock.close(); }
      catch(IOException e)
      {
      }
    sock= null;
    connected= false;
  } /* disconnect */
  
  
  /**
   * usingProxy() - impimplements abstract method for HttpURLconnection.
   * PFL - 8-7-00
   * See pg929, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @return true if using a proxy server
   */
  public boolean usingProxy()
  { return(false); }
  
  
  /**
   * getInputStream() - implements getInputStream for HttpURLconnection.
   * Overide default provided in URL connection.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @return input stream
   * @see #connect
   */
  public InputStream getInputStream()
  throws IOException
  { /* getInputStream */
    if(!connected)
      connect();
    return(sock.getInputStream());
  } /* getInputStream */
  
  
  /**
   * getOutputStream() - implements getOutputStream for HttpURLconnection.
   * Overide default provided in URL connection.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @return output stream
   * @see #connect
   */
  public OutputStream getOutputStream()
  throws IOException
  { /* getOutputStream */
    if(!connected)
      connect();
    return(sock.getOutputStream());
  } /* getOutputStream */
  
  
  /**
   * getHeaderField() - implements getHeaderField for HttpURLconnection.
   * Overide default provided in URL connection.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param n is the field to get
   * @return nth header field data
   * @see #getField
   * @see #getHeaders
   */
  public String getHeaderField(int n)
  { /* getHeaderField */
    getHeaders();
    if(n < headers.size())
      return(getField((String)headers.elementAt(n)));
    else
      return(null);
  } /* getHeaderField */
  
  
  /**
   * getHeaderField() - implements HeaderField for HttpURLconnection.
   * Overide default provided in URL connection.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param key specified the header field to get
   * @return data
   * @see #getHeaders
   */
  public String getHeaderField(String key)
  { /* getHeaderField */
    getHeaders();
    return((String)keys.get(key.toLowerCase()));
  } /* getHeaderField */
  
  
  /**
   * HeaderFieldKey() - implements HeaderFieldKey for HttpURLconnection.
   * Overide default provided in URL connection.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param n is the header field
   * @return data associated with key
   * @see #getHeaders
   * @see #getKey
   */
  public String HeaderFieldKey(int n)
  { /* HeaderField */
    getHeaders();
    if(n < headers.size())
      return(getKey((String)headers.elementAt(n)));
    else
      return(null);
  } /* HeaderFieldKey */
  
  
  /**
   * send() - helper routine to send a string to output stream.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param out output stream to write to
   * @param s is string to write
   */
  static void send(OutputStream out, String s)
  throws IOException
  { /* send */
    if(s==null)
      return;
    int len= s.length();
    for(int i= 0; i<len; i++)
      out.write((byte)s.charAt(i));
  } /* send */
  
  
  /**
   * recv() - helper routine to read a \n-terminated string from input stream.
   * PFL - 8-7-00
   * See pg930, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param in input stream to read from
   * @return string data
   */
  static String recv(InputStream in)
  throws IOException
  { /* recv */
    String sR= "";
    int ch= in.read();
    while(ch>=0 && ch!='\n')
    {  /* get a line's worth of data */
      if(ch!='\r')
        sR += (char)ch;   /* Trash RETURN chars */
      ch= in.read();
    }
    return(sR);
  } /* recv */
  
  
  /**
   * getKey() - helper routine for parsing header field
   * PFL - 8-7-00
   * See pg931, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param str
   * @return data
   */
  static String getKey(String str)
  { /* getKey */
    if(str==null)
      return(null);
    int idx= str.indexOf(keySeparator);
    if(idx>=0)
      return(str.substring(0,idx).toLowerCase());
    return(null);
  } /* getKey */
  
  
  /**
   * getField() -  helper routine for parsing header key
   * PFL - 8-7-00
   * See pg931, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @param str
   * @return data
   */
  public String getField(String str)
  { /* getField */
    if(str==null)
      return(null);
    int idx= str.indexOf(keySeparator);
    if(idx>=0)
      return(str.substring(idx+1).trim());
    else
      return(str);
  } /* getField */
  
  
  /**
   * getHeaders() - help routine read header from HTTP connection.
   * PFL - 8-7-00
   * See pg931, Patrick Chan etal, Java Class Libraries 2nd Ed, Vol 1.
   * @see #basicAuthenticationEncode
   * @see #connect
   * @see #getField
   */
  void getHeaders()
  { /* getHeaders */
    if(gotHdrFlag)
      return;
    
    gotHdrFlag= true;
    try
    {
      connect();
      InputStream
      in= sock.getInputStream();
      while(true)
      {
        String hdr= recv(in);
        if(hdr.length()==0)
          break;
        headers.addElement(hdr);
        String key= getKey(hdr);
        if(key!=null)
          keys.put(key,getField(hdr));
      }
    }
    catch (IOException e)
    {
      System.out.println("JCGI-GH e="+e);
    }
  } /* getHeaders */
  
  
  /**
   * addFormValue() - adds a form variable, value pair to the passed Hashtable.
   * @param ht the Hashtable that contains the form variable/value pairs
   * @param formKey the String that contains the form variable to add
   * @param formValue the String that contains the form value to add
   */
  public void addFormValue(Hashtable ht, String formKey, String formValue)
  { /* End of addFormValue */
    Vector vValues= null;
    
    if (formValue != null && formValue.length() > 0)
    {
      if (ht.containsKey(formKey))
      {
        vValues= (Vector)ht.get(formKey);
      }
      else
      {
        vValues= new Vector();
      }
      vValues.addElement(formValue);
      ht.put(formKey, vValues);
    }
  } /* End of addFormValue */
  
  
  /**
   * getKeyValuePairs() - cvt getParsedData to hashtable.
   * Takes the parsed data returned from the getParsedData method
   * and changes it to a Hashtable of key, value pairs where the first
   * Vector entry of each Vector record is the key and the rest of the
   * second Vector entry for each record is the value of the Hashtable.
   * @param vectorOfVectors the Vector of Vectors for the parsed data
   * @return Hashtable containing converted variable/value pairs
   * @see #getParsedData
   */
  public Hashtable getKeyValuePairs(Vector vectorOfVectors)
  { /* getKeyValuePairs */
    Hashtable  h= new Hashtable();
    Vector v= null;
    
    for (Enumeration e= vectorOfVectors.elements(); e.hasMoreElements();)
    {
      v= (Vector)e.nextElement();
      h.put((String)v.elementAt(0), (String)v.elementAt(1));
    }
    return h;
  } /* getKeyValuePairs */
  
  
  /**
   * getParsedData() - returns parsed data in form of  Vector of Vectors
   * containing the returned fields inside of a Vector of returned rows.
   * It throws JavaCGIBridgeTimeOutException if the retrieval times out.
   * @param u URL to get parsed data from.
   * @return Vector (records) of vectors (fields) of parsed data
   * @exception JavaCGIBridgeTimeOutException
   * @see #getParsedData
   */
  public Vector getParsedData(URL u)
  throws JavaCGIBridgeTimeOutException
  { /* getParsedData */
    return(getParsedData(u, null));
  } /* getParsedData */
  
  
  /**
   * getParsedData() - returns parsed data in form of  Vector of Vectors
   * Returns parsed data in the form of a Vector of Vectors containing
   * the returned fields inside of a Vector of returned rows. This form
   * POSTs the HTML Form variable data to the URL.
   * It throws JavaCGIBridgeTimeOutException if the retrieval times out.
   * @param u URL to get parsed data from.
   * @param ht Hashtable contains form variables to POST
   * @return Vector (records) of vectors (fields) of parsed data
   * @exception JavaCGIBridgeTimeOutException
   * @see #getRawCGIData
   */
  public Vector getParsedData(URL u, Hashtable ht)
  throws JavaCGIBridgeTimeOutException
  { /* getParsedData */
      /* getParsedData calls getRawCGIData to obtain the '
       * actual HTML text.
       */
    String data= getRawCGIData(u,ht);
    int topIndex, bottomIndex;
    Vector  v= new Vector();         /* parsed data is returned as a
                                      * series of Vectors of Vectors */
    
      /* topIndex and bottomIndex delimit the top and bottom
       * of data in the return HTML from the URL
       */
    topIndex= data.indexOf(topSeparator);
    bottomIndex = data.indexOf(bottomSeparator);
    
    /* Of course, there must be data between the topSeparator and
     * bottomSeparator in order for us to build a vector...
     */
    if (topIndex == -1 || bottomIndex == -1 || bottomIndex < topIndex ||
        topIndex + topSeparator.length() > bottomIndex)
    {
      return(v);
    }
    
    /* Now that we have the top and bottom, we can clip the data.
     * Make sure that the whole top of data separator line is
     * is clipped off using index of newline.
     */
    data= data.substring(data.indexOf("\n", topIndex) + 1,
    bottomIndex);
    
    while (data.length() > 0)
    {
      int rowIndex= data.indexOf(rowSeparator);
      String rowData= null;
      if (rowIndex==-1)
      {
        rowData= data;
        data = "";
      }
      else
      {
        rowData= data.substring(0,rowIndex);
        data= data.substring(rowIndex + rowSeparator.length());
      }
      if (rowData.length() > 0)
      {
        v.addElement(new Vector());
        while (rowData.length() > 0)
        {
          int fieldIndex= rowData.indexOf(fieldSeparator);
          String fieldData= null;
          if(fieldIndex == -1)
          {
            fieldData= rowData;
            rowData= "";
          }
          else
          {
            fieldData= rowData.substring(0,fieldIndex);
            rowData= rowData.substring(fieldIndex +
            fieldSeparator.length());
          }
          //if (fieldData.length() > 0)
          ((Vector)v.lastElement()).addElement(fieldData);
        }
      }
    }
    
    return(v);
  } /* getParsedData */
  
  
  /**
   * getRawCGIData() - returns raw HTML data as a String from the passed URL.
   * It throws JavaCGIBridgeTimeOutException if the retrieval times out.
   * @param u URL to get raw HTML from.
   * @return String containing plain HTML text
   * @exception JavaCGIBridgeTimeOutException
   * @see #getRawCGIData
   */
  public String getRawCGIData(URL u) throws JavaCGIBridgeTimeOutException
  {
    return(getRawCGIData(u,null));
  }
  
  
  /**
   * getRawCGIDataByte() - returns raw HTML data as byte[] from URL&hashtable.
   * It returns raw HTML data as a byte[] from the passed URL and list.
   * It throws JavaCGIBridgeTimeOutException if the retrieval times out.
   *
   * [TODO] rewrite this using buffered Byte I/O * * * * T O D O * * * *
   * See if(PFL_NEW_READ_BUF_CODE) code in getHttpRequestInThread().
   * @param u URL to get raw HTML from.
   * @return ht is hashtable
   * @exception JavaCGIBridgeTimeOutException
   */
  public byte[] getRawCGIDataByte(URL u, Hashtable ht)
  throws JavaCGIBridgeTimeOutException
  { /* getRawCGIDataByte */
    return(null);
  } /* getRawCGIDataByte */
  
  
  /**
   * getRawCGIData() - returns raw HTML data as a String from URL & hashtable.
   * Returns raw HTML data as a String from the passed URL and list
   * of Form variable/value pairs stored in a Hashtable.  This form
   * POSTs the HTML Form variable data to the URL.
   * It throws JavaCGIBridgeTimeOutException if the retrieval times out.
   * @param u URL to get raw HTML from.
   * @param ht Hashtable contains form variables to POST
   * @return String containing plain HTML text
   * @see #getParsedData
   * @exception JavaCGIBridgeTimeOutException
   */
  public String getRawCGIData(URL u, Hashtable ht)
  throws JavaCGIBridgeTimeOutException
  { /* getRawCGIData */
    /* We set up the information for passing to the thread
     * ahead of time.
     */
    threadURL= u;
    threadFormVar= ht;
    
    /* Create a new thread and flag the fact that the thread
     * did not get us any data yet. Then start the thread.
     */
    Thread t= new Thread(this);
    
    threadCompleted= false;
    threadCanRunMethodExecute= true;
    t.start();
    
    /* We calculate the current time that the thread started.
     * The delay to wait will be calculated below.
     */
    long
    base= System.currentTimeMillis(),
    delay= 0;
    synchronized(this)
    { /* synchronized block */
      try
      {
        while(t.isAlive() && threadCompleted == false)
        {
          delay= threadJavaCGIBridgeTimeOut -
          (System.currentTimeMillis() - base);
          if (delay <= 0)
            break;
          wait(delay);
        } /* End of while */
      }
      catch (InterruptedException e)
      { /* nothing */  }
    } /* synchronized block */
    
    t.stop();             /* No effect if already done
                           * but stops if timed out... */
    
    if (!threadCompleted)
    {
      throw new JavaCGIBridgeTimeOutException();
    }
    return(threadCGIData);
  } /* getRawCGIData */
  
  
  /**
   * getDefaultThreadJavaCGIBridgeTimeOut() - default communication time out
   * Return default static value in milliseconds for the class.
   * When the object retrieves data from a URL, it must get
   * the data within timeout milliseconds or a
   * JavaCGIBridgeTimeOutException is thrown.
   *
   * @return default communication time out in milliseconds
   * @see #setDefaultThreadJavaCGIBridgeTimeOut
   */
  public static int getDefaultThreadJavaCGIBridgeTimeOut()
  {
    return(defaultThreadJavaCGIBridgeTimeOut);
  }
  
  
  /**
   * setDefaultThreadJavaCGIBridgeTimeOut() - set communication time out
   * Static method sets the default communication time out in
   * milliseconds for the class.
   *
   * When the object retrieves data from a URL, it must get
   * the data within timeout milliseconds or a
   * JavaCGIBridgeTimeOutException is thrown.
   *
   * @param t default communication time out in milliseconds
   * @see #getDefaultThreadJavaCGIBridgeTimeOut
   */
  public static void setDefaultThreadJavaCGIBridgeTimeOut(int t)
  {
    defaultThreadJavaCGIBridgeTimeOut= t;
  }
  
  
  /**
   * getThreadJavaCGIBridgeTimeOut() - actual communication time out in msec.
   * Returns the actual communication time out in milliseconds
   * for the object.
   *
   * When the object retrieves data from a URL, it must get
   * the data within timeout milliseconds or a
   * JavaCGIBridgeTimeOutException is thrown.
   *
   * @return communication time out in milliseconds
   * @see #setThreadJavaCGIBridgeTimeOut
   * @see #getDefaultThreadJavaCGIBridgeTimeOut
   * @see #setDefaultThreadJavaCGIBridgeTimeOut
   */
  public int getThreadJavaCGIBridgeTimeOut()
  {
    return(threadJavaCGIBridgeTimeOut);
  }
  
  
  /**
   * setThreadJavaCGIBridgeTimeOut() -  actual communication time out in msec.
   * Sets the actual communication time out in milliseconds
   * for the object.
   *
   * When the object retrieves data from a URL, it must get
   * the data within timeout milliseconds or a
   * JavaCGIBridgeTimeOutException is thrown.
   *
   * @param t communication time out in milliseconds
   * @see #getThreadJavaCGIBridgeTimeOut
   * @see #getDefaultThreadJavaCGIBridgeTimeOut
   * @see #setDefaultThreadJavaCGIBridgeTimeOut
   */
  public void setThreadJavaCGIBridgeTimeOut(int t)
  {
    threadJavaCGIBridgeTimeOut= t;
  }
  
  
  /**
   * setFieldSeparator() - Sets the field separator for the object.
   * When getParsedData
   * method is called, the object uses the field separator to determine
   * where fields in a returned record of the raw HTML result set
   * begin and end.
   * @param s String containing new delimiting separator
   * @see #getParsedData
   * @see #getFieldSeparator
   */
  public void setFieldSeparator(String s)
  { fieldSeparator= s;  }
  
  
  /**
   * getFieldSeparator() - Returns the field separator for the object.
   * When getParsedData
   * method is called, the object uses the field separator to determine
   * where fields in a returned record of the raw HTML result set
   * begin and end.
   *
   * @return Separator string
   * @see #getParsedData
   * @see #setFieldSeparator
   */
  public String getFieldSeparator()
  { return(new String(fieldSeparator)); }
  
  
  /**
   * setRowSeparator() - Sets the row separator for the object.
   * When getParsedData
   * method is called, the object uses the row separator to determine
   * where rows/records of the raw HTML result set
   * begin and end.
   * @param s String containing new delimiting separator
   * @see #getParsedData
   * @see #getRowSeparator
   */
  public void setRowSeparator(String s)
  { rowSeparator= s; }
  
  
  /**
   * getRowSeparator() - Returns the row separator for the object.
   * When getParsedData
   * method is called, the object uses the row separator to determine
   * where rows/records of the raw HTML result set
   * begin and end.
   * @return Separator string
   * @see #getParsedData
   * @see #setRowSeparator
   */
  public String getRowSeparator()
  { return(new String(rowSeparator)); }
  
  
  /**
   * setTopSeparator() - Sets the top separator for the object.
   * When getParsedData
   * method is called, the object uses the top separator to determine
   * where the rows of data inside the raw HTML output actually begin.
   * @param s String containing new delimiting separator
   * @see #getParsedData
   * @see #getTopSeparator
   */
  public void setTopSeparator(String s)
  { topSeparator= s;  }
  
  
  /**
   * getTopSeparator() - Returns the top separator for the object.
   * When getParsedData
   * method is called, the object uses the top separator to determine
   * where the rows of data inside the raw HTML output actually begin.
   * @return Separator string
   * @see #getParsedData
   * @see #setTopSeparator
   */
  public String getTopSeparator()
  { return(new String(topSeparator)); }
  
  
  /**
   * setBottomSeparator() - Sets the bottom separator for the object.
   * When getParsedData
   * method is called, the object uses the bottom separator to determine
   * where the rows of data inside the raw HTML output actually end.
   * @param s String containing new delimiting separator
   * @see #getParsedData
   * @see #getBottomSeparator
   */
  public void setBottomSeparator(String s)
  {
    bottomSeparator= s;
  }
  
  
  /**
   * getBottomSeparator() - Returns the bottom separator for the object
   * When getParsedData
   * method is called, the object uses the bottom separator to determine
   * where the rows of data inside the raw HTML output actually end.
   * @return Separator string
   * @see #getParsedData
   * @see #setBottomSeparator
   */
  public String getBottomSeparator()
  { return(new String(bottomSeparator)); }
  
  
  /**
   * run() - This run thread asynchronously POSTs and GETs data from URL
   * and places the contents into the threadCGIData variable.
   * Since Threads do not return or pass parameter values, instance
   * variables in this object are used to maintain state.  This method
   * is only public so that the Thread class can launch the thread.
   * @see #getRawCGIData
   */
  public void run()
  { /* run */
    if (threadCanRunMethodExecute)
    { /* run thread to get data */
      threadCGIData= getHttpRequestInThread(threadURL, threadFormVar);
      threadCompleted= true; // set completed flag
      synchronized(this)
      { notifyAll(); }
      /* ends the wait() for thread to end in getRawCGIData */
    }  /* run thread to get data */
  } /* run */
  
  
  /**
   * getURLEncodedHashTable() - form variables inside hashtable as URLencoded
   * string of parameters for a CGI based program to process.
   * @param ht Hashtable containing form variables
   * @return URLencoded string of HTML form variables & values
   * @see #addFormValue
   */
  private String getURLEncodedHashTable(Hashtable ht)
  { /* getURLEncodedHashTable */
    StringBuffer encodedString= null;
    Vector vFormValues= null;
    
    /* First, we enumerate through the keys (Form variables) */
    for (Enumeration eKeys= ht.keys() ; eKeys.hasMoreElements() ;)
    {
      String formVariable= (String)eKeys.nextElement();
      vFormValues= (Vector)ht.get(formVariable);
      
    /* Now, we have to enumerate through the values for the
     * form variables.
     * NOTE: It IS entirely possible that form variable has MANY values
     * to simulate events such as multiple checkboxes or multiple
     * select <SELECT> form elements in an HTML form.
     */
      for (Enumeration eVals= vFormValues.elements() ;
      eVals.hasMoreElements() ;)
      {
        String formValue= (String)eVals.nextElement();
        
        if (encodedString!=null)
          encodedString.append("&");
        else
          encodedString= new StringBuffer();
        
        encodedString.append(URLEncoder.encode(formVariable) + "=" +
        URLEncoder.encode(formValue));
      }
    }
    return(encodedString.toString());
  } /* getURLEncodedHashTable */
  
  
  /**
   * getHttpRequestInThread() - Returns HTTP Request data to the thread
   * that was launched to GET/POST data for a URL.  This is
   * a private method.
   * @param u URL to retrieve and post data for
   * @param ht Form variables to send to URL
   * @return String containing retrieved HTML text
   * @see #getURLEncodedHashTable
   * @see #run
   */
  private String getHttpRequestInThread(URL u, Hashtable ht)
  { /* getHttpRequestInThread */
    String postContent= null;
    StringBuffer strBuffer= new StringBuffer();
    
    if (ht != null)
      postContent= getURLEncodedHashTable(ht);
    
    try
    { /* setup connection */
      URLConnection       urlConn;
      DataOutputStream    dos;
      DataInputStream     dis= null;
      
      /* Establish the URL connection */
      urlConn= u.openConnection();
      
     /* We always want some input from the CGI script
      * in general even if it is just a success story.
      */
      urlConn.setDoInput(true);
      
      /* If ht was not null, then know there is data to be posted.*/
      if (ht != null)
        urlConn.setDoOutput(true);
      
      /* Turn off caching as may screw with our dynamic programs */
      urlConn.setUseCaches(false);
      
      /* Specify the content type */
      urlConn.setRequestProperty("Content-type", "application/x-www-form-urlencoded");
      
      /* [TODO] do we need to check if don't get 401 error first? */
      
      // Send the POST data if we are writing
      if (ht != null)
      {
        dos= new DataOutputStream(urlConn.getOutputStream());
        dos.writeBytes(postContent);
        dos.flush();
        dos.close();
      }
      
      if(!PFL_NEW_READ_BUF_CODE)
      { /* Old way using String */
        dis= new DataInputStream(urlConn.getInputStream());
        String str;
        
        /* [TODO] readLine() is DEPRECATED - see pg 452 Class book-I */
        
        while (null != ((str= dis.readLine())))
        { /* get input and check for trouble */
          /* Do troubleshooting HERE such as System.println */
          strBuffer.append(str + "\n");
        } /* get input and check for trouble */
      } /* Old way using String */
      
      else if(PFL_NEW_READ_BUF_CODE)
      { /* new way using byte[] */
        /* ***** REWRITE THIS MESS!!! ***** */
        //dis = new DataInputStream (urlConn.getInputStream());
        // BufferReader bufRdr= new BufferedReader(dis);
        // add BufferedInputStream(dis) and
        // add bufRdr.readLine() ?????
        
        //byte bBuf[];
        // while (null != ((bBuf= bufRdr.readLine())))
        //{
        // Do troubleshooting here such as System.println
        // strBuffer.append(str + "\n");
        //}
      } /* new way using byte[] */
      
      dis.close();
      responseCode= this.getResponseCode();
      responseMsg= this.getResponseMessage();  /* for DEBUGGING*/
    } /* setup connection */
    
    catch (MalformedURLException me)
    {
      System.out.println("JCGIB MalformedURLExcep.: " + me+
                         " responseCode="+responseCode+
                         "\n  responseMsg="+responseMsg);
    }
    catch (IOException ioe)
    {
      System.out.println("JCGIB IOExcep.: " + ioe.getMessage()+
                         " responseCode="+responseCode+
                         "\n  responseMsg="+responseMsg);
    }
    
    /* Convert buffer to string */
    String
    sR= strBuffer.toString();
    
    /* Handle SOME of the special errors */
    if(responseCode==HttpURLConnection.HTTP_UNAUTHORIZED)
    {
      System.out.println("JCGIB *401 UNAUTHORIZED* responseCode="+responseCode+
                         ",  responseMsg="+responseMsg+ " sR='"+sR+"'");
      return("*401 UNAUTHORIZED*");   /* let caller try again... */
    }
    
    
    //if(MAExplorer.CONSOLE_FLAG /* && responseCode>=300 */)
    System.out.println("JCGIB responseCode="+responseCode+
                       " responseMsg="+responseMsg+ " sR='"+sR+"'");
    
    return(sR);
  } /* getHttpRequestInThread */
  
  
  /**
   * base64Length() - compute# bytes it takes to store LEN bytes in base64.
   * Code mapped from wget-src-http.c.
   */
  private int base64Length(int strLength)
  { /* base64Length */
    return(4 * ((strLength + 2) / 3));
  } /* base64Length */
  
  
  /**
   * base64encode() - return str encoded to base64 format.
   * Code mapped from wget-src-http.c.
   */
  private String base64encode(String str)
  { /* base64encode */
    char tbl[]= { /* Conversion table */
                 'A','B','C','D','E','F','G','H',
                 'I','J','K','L','M','N','O','P',
                 'Q','R','S','T','U','V','W','X',
                 'Y','Z','a','b','c','d','e','f',
                 'g','h','i','j','k','l','m','n',
                 'o','p','q','r','s','t','u','v',
                 'w','x','y','z','0','1','2','3',
                 '4','5','6','7','8','9','+','/'
                };
    
    char
      chI0, chI1, chI2,
      ch1, ch2, ch3, ch4;
    int
      length= str.length(),
      bufLen= base64Length(length),
      i;
    StringBuffer sBuf= new StringBuffer(bufLen);
    String sR;
    
    /* Transform the 3x8 bits to 4x6 bits, as required by base64.
     * Pad the end of the string with '"'s if not divisible by 3.
     * Drived from apache Web server ap_base64.c:ap_base64encode()
     */
    for (i= 0; i < length-2; i += 3)
    { /* Build base64 string */
      chI0= str.charAt(i);           /* source characters */
      chI1= str.charAt(i+1);
      chI2= str.charAt(i+2);
      
      ch1= tbl[(chI0 >> 2) & 0x3F];  /* destination characters */
      ch2= tbl[((chI0 & 0x3) << 4) + ((chI1 & 0xF0) >> 4)];
      ch3= tbl[((chI1 & 0xF) << 2) + ((chI2 & 0xC0) >> 6)];
      ch4= tbl[chI2 & 0x3F];
      
      sBuf.append(ch1);
      sBuf.append(ch2);
      sBuf.append(ch3);
      sBuf.append(ch4);
    } /* Build base64 string */
    
    /* Pad the result if necessary...  */
    if (i < length)
    {
      chI0= str.charAt(i);        /* source characters */
      ch1= tbl[(chI0 >> 2) & 0x3F];
      sBuf.append(ch1);
      if(i==(length-1))
      {
        ch2= tbl[(chI0 & 0x3) << 4];
        sBuf.append(ch2);
        sBuf.append('=');
      }
      else
      {
        chI1= str.charAt(i+1);
        ch2= tbl[((chI0 & 0x3) << 4) | ((int) (chI1 & 0xF0) >> 4)];
        ch3= tbl[((chI1 & 0xF) << 2)];
        sBuf.append(ch2);
        sBuf.append(ch3);
      }
      sBuf.append('=');
    }
    
    sR= new String(sBuf);            /* Return string not string buffer*/
    return(sR);
  } /* base64encode */
  
  
  /**
   * basicAuthenticationEncode() - create authentication header contents
   * for the `Basic' scheme.
   * This is done by encoding the string `USER:PASS' in base64 and
   * constructing the string
   *<PRE>
   *    "Authorization: Basic " + base64encode(user+":"+passwd)
   *</PRE>
   * @return null if a problem.
   * @see #base64encode
   */
  public String basicAuthenticationEncode(String user, String passwd)
  { /* basicAuthenticationEncode */
    if(user==null || passwd==null || user.length()==0 || passwd.length()==0)
      return(null);
    String
      up= user + ":" + passwd,
      encodedUP= base64encode(up),
      res= "Authorization: Basic " + encodedUP +"\r\n";
    
    return(res);
  } /* basicAuthenticationEncode */
  
  
} /* End of JavaCGIBridge class */
