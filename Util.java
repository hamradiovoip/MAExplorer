/** File: Util.java */
 
import java.awt.*;
import java.awt.event.*;
import java.awt.List;
import java.awt.image.*;
import java.awt.Color;
import java.util.*;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.net.*;
import java.lang.*;
import java.text.*;
import java.io.*;
import java.util.zip.*;

/**
 * The Util class contains common utility functions used by the rest of MAExplorer. 
 * These include, several string-numeric converters,
 * messages to the three message lines in the top page as well as special
 * string array parsing, font string name to int size, ratio color setting,
 * zoom toggle mapping, etc.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin(NCI), G. Thornwall(SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.28 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */ 

class Util
{  
  /* --- global statics --- */
  /** link to global MAExplorer instance */
  static MAExplorer 
    mae;                     
  /** link to global FileIO instance */
  private static FileIO
    fio;   
  /** Copy of last ShowMsg() printed */
  static String
    mStatus= ""; 
  /** default int value for conversions */            
  static int
    defaultInt; 
  /** default float value for conversions */             
  static float
    defaultFloat;            
   
  /** Log msg text frames. If enabled, this logs all showMsgXXX() output that
   * contains all messages.
   */
  static LogTextFrame
    msgTextFrame= null; 
  /** Message window visibility flag */
  static boolean 
    msgIsVisibleFlag= false;
  /** Full path name of msg file if enabled. */
  private static String
    msgFileName= null;
  /** Flag: set if appending msg to historyFile */
  private static boolean 
    appendMsgFlag= false;
   
  /** Command history number gets incremented each time a command is logged. */
  static int
    cmdHistoryNbr= 0;  
  /** Log History text frame. If enabled, this logs all commands */  
  static LogTextFrame
    historyTextFrame= null;
  /** History window visibility flag */
  static boolean 
    historyIsVisibleFlag= false;
  /** Full path name of history file if enabled. */
  private static String
    historyFileName= null;
  /** Flag: set if appending command history to historyFile */
  private static boolean 
    appendHistoryFlag= false; 
  
  
  /**
   * Util() - constructor for Utility package
   * @param mae is MAExplorer instance
   */
  Util(MAExplorer mae)
  { /* Util */
    this.mae= mae;
    fio= mae.fio;
    
    defaultInt= 0;
    defaultFloat= 0.0F;
  } /* Util */
  
  
  /**
   * setMsgFileLogging() - set up message file logging. If it is a standalone
   * application then you can also create a message log file.
   * @return true if logging is successfully set up. You then log messages
   * by calls to showMsgXXX().
   * @param historyFileName is the name of the history file excluding the path
   * @param appendLogDataFlag indicates that history accumulates between viewing sessions.
   * @return true if successful in setting up a history logging file data.
   * @see LogTextFrame
   */
  static boolean setMsgFileLogging(String msgFileName, boolean isVisibleFlag,
                                    boolean appendMsgDataFlag)
  { /* setMsgFileLogging */
    /* [1] Setup the state variables */
    msgTextFrame= null;
    appendMsgFlag= false;
    
    if(msgFileName==null || msgFileName.length()==0)
      msgFileName= "maeMessages.log";
    
    msgFileName= msgFileName;
    appendMsgFlag= appendMsgDataFlag;
    
    /* [2] Try to open history file for logging */
    msgTextFrame= new LogTextFrame("Log of MAExplorer messages",
                                    mae.fileBasePath,
                                    msgFileName,
                                    appendMsgFlag, true, 25);
    
    /* [2.1] Save flags - may have failed to open log file */
    appendMsgFlag= msgTextFrame.appendLogDataFlag;
    msgTextFrame.setVisible(isVisibleFlag);
    
    return(true);
  } /* setMsgFileLogging */
  
  
  /**
   * setHistoryFileLogging() - set up history file logging. If it is a
   * standalone application then we can also create a history log file.
   * @return true if logging is successfully set up. You then log messages
   * by calls to showMsgXXX().
   * @param historyFileName is the name of the history file excluding the path
   * @param isVisibleFlag makes the history window visible.
   * @param appendLogDataFlag indicates that history accumulates between viewing sessions.
   * @return true if successful in setting up a history logging file data.
   * @see LogTextFrame
   */
  static boolean setHistoryFileLogging(String histFileName, boolean isVisibleFlag,
                                        boolean appendLogDataFlag)
  { /* setHistoryFileLogging */
    /* [1] Setup the state variables */
    String
    fullLogFilePath= "";
    historyTextFrame= null;
    appendHistoryFlag= false;
    
    if(histFileName==null || histFileName.length()==0)
      histFileName= "maeHistory.log";
    
    historyFileName= histFileName;
    appendHistoryFlag= appendLogDataFlag;
    
    /* [2] Try to open history file for logging */
    historyTextFrame= new LogTextFrame("Log of command history",
    mae.fileBasePath,
    historyFileName,
    appendHistoryFlag, true, 50);
    
    /* [2.1] Save flags - may have failed to open log file */
    appendHistoryFlag= historyTextFrame.appendLogDataFlag;
    historyTextFrame.setVisible(isVisibleFlag);
    
    return(true);
  } /* setHistoryFileLogging */
  
  
  /**
   * setMsgLoggingState() - set the messages logging state on/off.
   * This only applies if it had been previously enabled.
   * @param isVisibleFlag makes the message logging window visible.
   * @param appndMsgFlag indicates that message logging accumulates between viewing sessions.
   * @return true if able to change the state.
   * @see #setLogMsgWindowVisible
   */
  boolean setMsgLoggingState(boolean isVisibleFlag, boolean appndMsgFlag)
  { /* setMsgLoggingState */
    appendMsgFlag= appndMsgFlag;
    msgTextFrame.appendLogDataFlag= appndMsgFlag;
    setLogMsgWindowVisible(isVisibleFlag);
    return(true);
  } /* setMsgLoggingState */
  
  
  /**
   * setHistoryLoggingState() - set the history logging state on/off.
   * This only applies if it had been previously enabled.
   * @param isVisibleFlag makes the history window visible.
   * @param appendLogDataFlag indicates that history accumulates between viewing sessions.
   * @return true if able to change the state.
   * @see #setLogHistoryWindowVisible
   */
  boolean setHistoryLoggingState(boolean isVisibleFlag, boolean appendHistFlag )
  { /* setHistoryLoggingState */
    appendHistoryFlag= appendHistFlag;
    historyTextFrame.appendLogDataFlag= appendHistFlag;
    setLogHistoryWindowVisible(isVisibleFlag);
    return(true);
  } /* setHistoryLoggingState */
  
  
  /**
   * setLogMsgWindowVisible() - make the popup window visible or invisible
   * @param isVisibleFlag makes the history window visible.
   * @see LogTextFrame#setLogWindowVisible
   * @see #setMsgFileLogging
   */
  static void setLogMsgWindowVisible(boolean isVisibleFlag)
  { /* setLogMsgWindowVisible */
    if(msgTextFrame==null)
      setMsgFileLogging(null, false,  true);
    if(msgTextFrame!=null)
      msgTextFrame.setLogWindowVisible(isVisibleFlag);
  } /* setLogMsgWindowVisible */
  
  
  /**
   * setLogHistoryWindowVisible() - make the popup window visible or invisible
   * @param isVisibleFlag makes the message logging window visible.
   * @see LogTextFrame#setLogWindowVisible
   * @see #setHistoryFileLogging
   */
  static void setLogHistoryWindowVisible(boolean isVisibleFlag)
  { /* setLogHistoryWindowVisible */
    if(historyTextFrame==null)
      setHistoryFileLogging(null, false, true);
    if(historyTextFrame!=null)
      historyTextFrame.setLogWindowVisible(isVisibleFlag);
  } /* setLogHistoryWindowVisible */
  
  
  /**
   * saveCmdHistory() - print msg to showMsg1() and also save in command history
   * and message logs if logging is enabled as well.
   * @param msg is the command message to log
   * @see #saveCmdHistory
   */
  static void saveCmdHistory(String msg)
  { /* saveCmdHistory */
    saveCmdHistory(msg, true);
  } /* saveCmdHistory */
  
  
  /**
   * saveCmdHistory() - save msg in command history and message logs if logging is enabled.
   * @param msg is the command message to log
   * @param doShowMsgFlag if set is set, then print msg to showMsg1() as well.
   * @see LogTextFrame#appendLogLn
   * @see #showMsg
   */
  static void saveCmdHistory(String msg, boolean doShowMsgFlag)
  { /* saveCmdHistory */
    if(doShowMsgFlag)
    { /* add message to msg1 */
      boolean
      saveAppendMsgFlag= appendMsgFlag;
      appendMsgFlag= false;
      showMsg(msg);
      appendMsgFlag= saveAppendMsgFlag;
    }
    
    String
    sCmd= "["+(++cmdHistoryNbr)+"] "+msg;
    
    if(appendHistoryFlag && historyTextFrame!=null && msg!=null &&
    msg.length()>0)
      historyTextFrame.appendLogLn(sCmd);
    
    if(appendMsgFlag && msgTextFrame!=null && msg!=null && msg.length()>0)
      msgTextFrame.appendLogLn(sCmd);
  } /* saveCmdHistory */
  
  
  /**
   * saveMsgHistory() - save msg in  message logs if logging is enabled.
   * If doShowMsgFlag is set, then print msg to showMsg1().
   * @param msg is the command message to log
   * @param isVisibleFlag makes the message logging window visible.
   * @see LogTextFrame#appendLogLn
   * @see #showMsg
   */
  static void saveMsgHistory(String msg, boolean doShowMsgFlag)
  { /* saveMsgHistory */
    if(doShowMsgFlag)
    { /* add message to msg1 */
      boolean
      saveAppendMsgFlag= appendMsgFlag;
      appendMsgFlag= false;
      showMsg(msg);
      appendMsgFlag= saveAppendMsgFlag;
    }
    
    if(appendMsgFlag && msgTextFrame!=null && msg!=null && msg.length()>0)
      msgTextFrame.appendLogLn(msg);
  } /* saveMsgHistory */
  
  
  /**
   * showMsg() - print msg to showStatus() and to txtField1
   * @param msg is the command message to show
   * @see MAExplorer#showStatus
   * @see #showMsg1
   */
  static void showMsg(String msg)
  { /* showMsg */
    mStatus= msg;
    if(mae.isAppletFlag)
      mae.showStatus(msg);   /* note: there is no status area in application */
    if(mae.is!=null)
      showMsg1(msg, true, /* saveTitleFlag*/
      Color.black, Color.white);
  } /* showMsg */
  
  
  /**
   * showMsg1() - print msg to showStatus() and to txtField1 and change color
   * @param msg is the command message to show
   * @param fg is foreground color
   * @param bg is background color
   * @see MAExplorer#showStatus
   * @see #showMsg1
   */
  static void showMsg1(String msg, Color fg, Color bg)
  { /* showMsg1 */
    mStatus= msg;
    if(mae.isAppletFlag)
      mae.showStatus(msg);   /* note: there is no status area in application*/
    if(mae.is!=null)
      showMsg1(msg, true, /* saveTitleFlag*/ fg, bg);
  } /* showMsg1 */
  
  
  /**
   * showMsg1() - print msg to showStatus() and to txtField1 and change color
   * @param msg is the command message to show
   * @param saveTitleFlag to save the  title(not used)
   * @param fg is foreground color
   * @param bg is background color
   * @see LogTextFrame#appendLogLn
   * @see MAExplorer#showStatus
   * @see #showMsg1
   */
  static void showMsg1(String msg, boolean saveTitleFlag, Color fg, Color bg)
  { /* showMsg1 */
    mStatus= msg;
    if(mae.isAppletFlag)
      mae.showStatus(msg);   /* note: there is no status area in application*/
    if(mae.is!=null)
      mae.is.setText1(msg, true, /* saveTitleFlag*/  fg, bg);
    
    if(appendMsgFlag && msgTextFrame!=null && msg!=null && msg.length()>0)
      msgTextFrame.appendLogLn(msg);
  } /* showMsg1 */
  
  
  /**
   * showFeatures() - print features to txtField2 and txtField3
   * @param genbankData is the msg2 data to show
   * @param genomicData is the msg3 data to show
   * @see #showMsg2
   * @see #showMsg3
   */
  static void showFeatures(String genbankData, String genomicData)
  { /* showFeatures */
    if(mae.is==null)
      return;
    if(genbankData!=null)
      showMsg2(genbankData,Color.black, Color.white);
    if(genomicData!=null)
      showMsg3(genomicData,Color.black, Color.white);
  } /* showFeatures */
  
  
  /**
   * showMsg2() - print msg2 to txtField2
   * @param msg2 is the msg2 data to show
   * @see #showMsg2
   */
  static void showMsg2(String msg2)
  { /* showMsg2 */
    if(mae.is==null)
      return;
    if(msg2!=null)
      showMsg2(msg2,Color.black, Color.white);
  } /* showMsg2 */
  
  
  /**
   * showMsg2() - print msg2 to txtField2 and change color
   * @param msg2 is the command message to show
   * @param fg is foreground color
   * @param bg is background color
   * @see LogTextFrame#appendLogLn
   */
  static void showMsg2(String msg2, Color fg, Color bg)
  { /* showMsg2 */
    if(mae.is==null)
      return;
    if(msg2!=null)
      mae.is.setText2(msg2, fg, bg);
    
    if(appendMsgFlag && msgTextFrame!=null && msg2!=null && msg2.length()>0)
      msgTextFrame.appendLogLn(msg2);
  } /* showMsg2 */
  
  
  /**
   * showMsg3() - print msg3 to txtField3
   * @param msg3 is the command message to show
   * @see #showMsg3
   */
  static void showMsg3(String msg3)
  { /* showMsg3 */
    if(mae.is==null)
      return;
    if(msg3!=null)
      showMsg3(msg3,Color.black, Color.white);
  } /* showMsg3 */
  
  
  /**
   * showMsg3() - print msg3 to txtField3
   * ALSO: side effect of setting cursor to WAIT_CURSOR
   * if the(fg,bg) is not(black,white).
   * @param msg3 is the command message to show
   * @param fg is foreground color
   * @param bg is background color
   * @see LogTextFrame#appendLogLn
   */
  static void showMsg3(String msg3, Color fg, Color bg)
  { /* showMsg3 */
    if(mae.is==null)
      return;
    if(msg3!=null)
      mae.is.setText3(msg3, fg, bg);
    
    boolean waitCursorFlag= (fg!=Color.black || bg!=Color.white);
    mae.pur.setWaitCursor(waitCursorFlag);
    
    if(appendMsgFlag && msgTextFrame!=null && msg3!=null && msg3.length()>0)
      msgTextFrame.appendLogLn(msg3);
  } /* showMsg3 */
  
  
  /**
   * popupAlertMsgLine) - popup ALERT message line and then timeout.
   * Do a local timeout if localTimeoutMsec>0.
   * @param msg is the command message to show
   * @see PopupDialogQuery
   * @see PopupDialogQuery#alertTimeout
   * @see #showMsg
   */
  static void popupAlertMsgLine(String msg)
  { /* popupAlertMsgLine */
    if(msg==null)
      return;
    
    Util.showMsg(msg);
    PopupDialogQuery pAlert= new PopupDialogQuery(mae,mae.mbf.dialogFrame,1);
    pAlert.alertTimeout(msg);
  } /* popupAlertMsgLine*/
  
  
  /**
   * popupAlertMsg() - popup alert message in a scrollable text area.
   * If the timeoutMsec is >0, then close the window after this time.
   * @param title is the optional title for the popup window
   * @param msg is the message for the popup window
   * @param nRows is the # of rows for the popup window, 20 if specify 0
   * @param nCols is the # of columns for the popup window, 80 if specify 0
   * @see ShowStringPopup
   * @see ShowStringPopup#updateText
   * @see #sleepMsec
   */
  static void popupAlertMsg(String title, String msg, int nRows, int nCols)
  { /* popupAlertMsg */
    if(msg==null)
      return;
    if(title==null)
      title= "Alert message";
    if(nRows==0)
      nRows= 20;
    if(nCols==0)
      nCols= 80;
    
    ShowStringPopup t= new ShowStringPopup(mae, msg, nRows, nCols, mae.rptFontSize,
                                           title,
                                           0, 0, "ALERT-MSG", mae.pur.UNIQUE,
                                           "alertMessage.txt"); 
  } /* popupAlertMsg */
  
  
  /**
   * popupDryrotMsgsAndQuit() - if any dryrot, tell us and die...
   * @return false if not quiting
   * @see MAExplorer#quit
   * @see ShowStringPopup
   * @see ShowStringPopup#updateText
   * @see UserState#createMAEstartupFileStr
   * @see #sleepMsec
   * @see #showMsg2
   */
  static boolean popupDryrotMsgsAndQuit()
  { /* popupDryrotMsgsAndQuit */
    if(mae.dryrotLogStr==null)
      return(false);
    
    /* Popup text area with SAVEAS so they can save it in a file in
     * the /Report directory. Then popup ALERT box then
     * wait for them to click OK then return to die.
     */
    Util.showMsg2("DRYROT - quitting...", Color.white, Color.red);
    
    /* Dump the specific DRYROT info followed by the state to help us
     * figure out what went wrong.
     */
    String sR= "\nFatal MAExplorer Error Report " + mae.date +
               "\nDatabase [" + mae.browserTitle + "]"+
               "\nVersion: "+mae.verStr+
               "\nDate: "+mae.date+
               "\nOS: "+mae.osName+
               "\nCodeBase: "+mae.codeBase+
               "\nStartup Dir: "+mae.defDir+
               "\nStartup File: "+ mae.startupFile+
               "\n";
    if(mae.hps!=null)
      for(int i=1;i<=mae.hps.nHP;i++)
        sR += " HP["+i+"]= "+mae.hps.msList[i].hpName+"\n";
    
    sR +=  "Please click on 'SaveAs' to save this MESSAGE and "+
          "then Email it to\n"+
          "    mae@ncifcrf.gov\n"+
          "so we can try to analyze and fix the problem.\n"+
          "------------------------------------------------\n\n"+
          mae.dryrotLogStr;
    
    /* [3] Create(Name,Value) contents of .mae file */
    String maeStateStr= mae.us.createMAEstartupFileStr(true);
    sR += "\nCurrent State\n------------\n"+ maeStateStr;
    
    ShowStringPopup t= new ShowStringPopup(mae, sR, 20,80, mae.rptFontSize,
                                           "Fatal MAExplorer error - aborting",
                                           0, 0, "ALERT", mae.pur.UNIQUE,
                                           "maeDRYROTmessage.txt");
    while(true)
    { /* wait for user to press the OK button */
      sleepMsec(500);            /* give them time to read it*/
      if(t.alertOK)
        break;                   /* ok - kill the sucker */
      if(t.savedMsg!=null)
      { /* changed to message after do "SaveAs" */
        String sDRM= "\n\n\n\n   DRYROT Status Has Been "+t.savedMsg+
                     "\n\n    Please E-mail this saved file to mae@ncifcrf.gov"+
                     "\n    so we can try to analyze and fix the problem."+
                     "\n\n    Click on OK to exit MAExplorer.\n";
        t.savedMsg= null;  /* clear it */
        t.updateText(sDRM);
      } /* changed to message after do "SaveAs" */
    } /* wait for user to press the OK button */
    
    mae.quit();
    
    sleepMsec(5000);               /* give them time to read it*/
    
    return(true);
  } /* popupDryrotMsgsAndQuit */
  
  
  /**
   * rmvDupIMAGEstr() - remove duplicate "IMAGE:xxxx:" prefix from string.
   * This is a SPECIAL microarray I.M.A.G.E URL hack!!!
   * If the string contains "IMAGE:IMAGE:" or "IMAGE:ATCC:"
   * then slice out the first "IMAGE:"
   * @param str I.M.A.G.E. id that may have extra "IMAGE:" string
   * @return cleaned up string.
   */
  String rmvDupIMAGEstr(String str)
  { /* rmvDupIMAGEstr */
    if(str==null)
      return(null);
    int
      idx1I= str.indexOf("IMAGE:IMAGE:"),
      idx1A= str.indexOf("IMAGE:ATCC:"),
      idx1= (idx1I!=-1) ?idx1I : idx1A;
    String sR= str;
    
    if(idx1!=-1)
    { /* found it */
      String sRest= str.substring(idx1);
      int idx2= sRest.indexOf(":");
      String
        s1= str.substring(0,idx1),
        s2= sRest.substring(idx2+1);
        sR= s1+s2;
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("U-RDIS str='"+str+"'\n  sRest='"+sRest+
                         "'\n  s1='"+s1+"'\n  s2='"+s2+"'\n  sR='"+sR+"'");
      */
    } /* found it */
    
    return(sR);
  } /* rmvDupIMAGEstr */
  
  
  /**
   * rmvIMAGEstr() -  - remove "IMAGE:" prefix from string.
   * This is a SPECIAL microarray I.M.A.G.E URL hack!!!
   * If the string contains "IMAGE:"  remove it.
   * If it contains "ATCC:", return null.
   * else return the str.
   * @param str I.M.A.G.E. id that may have "IMAGE:" or "ATCC:"
   * @return cleaned up string
   */
  String rmvIMAGEstr(String str)
  { /* rmvIMAGEstr */
    if(str==null)
      return(null);
    int
      idx1I= str.indexOf("IMAGE:"),
      idx1A= str.indexOf("ATCC:");
    if(idx1A!=-1)
      return(null);    
    String sR= str;   
    
    if(idx1I!=-1)
    { /* found it */
      String sRest= str.substring(idx1I);
      int idx2I= sRest.indexOf(":");
      String
        s1= str.substring(0,idx1I),
        s2= sRest.substring(idx2I+1);
        sR= s1+s2;
      /*
      if(mae.CONSOLE_FLAG)
        mae.fio.logMsgln("U-RDIS str='"+str+ "'\n  sRest='"+sRest+
                         "'\n  s1='"+s1+ "'\n  s2='"+s2+"'\n  sR='"+sR+"'" );
       */
    } /* found it */
    
    return(sR);
  } /* rmvIMAGEstr */
  
  
  /**
   * popupViewer() - popup a web browser for the help HTML file.
   * @param urlBase is the base URL
   * @param fileIn is the file to display
   * @param windowName  is the window to use if not null
   * @see FileIO#logMsgln
   * @see MAExplorer#logDRYROTerr
   * @see Util#showMsg
   * @see Util#showMsg2
   * @see Util#showMsg3
   * @see #displayURL
   * @see #rmvDupIMAGEstr
   */
  void popupViewer(URL urlBase, String fileIn, String windowName )
  { /* popupViewer */
    URL url= null;
    String file= rmvDupIMAGEstr(fileIn); /* SPECIAL microarray I.M.A.G.E URL
                                          * hack!!!  If the string contains
                                          * "IMAGE:IMAGE:" or "IMAGE:ATCC:"
                                          * then slice out the first "IMAGE:"
                                          */
    try
    {
      url= new URL(urlBase, file);
      /*
      if(mae.CONSOLE_FLAG)
        System.out.println("U-PV file="+file+ "\n     url="+url+
                           "\n     windowName="+windowName);
      */
    }
    catch(Exception e)
    { /* MalformedURLException */
      fio.logMsgln("Can't create URL[" + file + "]");
    }
    
    if(!mae.isAppletFlag)
    { /* popup a browser w/ stand alone*/
      displayURL(url.toString());
    }
    else
    { /* Note: see discussion in Java Handbook, page 259 */
      try
      {
        /* Note: see discussion in Java Handbook, page 259.
         *  it uses ARGS:(url, "_blank")
         */
        /* new popup browser */
        String sWindow= ((windowName!=null) ? windowName :  "_blank");
        
        mae.getAppletContext().showDocument(url,sWindow);
      }
      catch(Exception e)
      { /* MalformedURLException */
        showMsg("Can't find URL[" + file + "]");
        Util.popupAlertMsg("Can't find URL[" + file + "]",
                            "Can't find URL[" + file + "]", 
                            4, 60);
      }
    } /* Note: see discussion in Java Handbook, page 259 */
  } /* popupViewer */
  
  
  /**
   * displayURL() - Display URL file in system Web browser, for use in stand-alone
   * mode. This assumes that you have added the proper plugin to your browser
   * for this data if required.
   *<PRE>
   * Examples:
   * displayURL("http://www.javaworld.com")
   * displayURL("file://c:\\docs\\index.html")
   * displayURL("file:///user/joe/index.html");
   * displayURL("file:///user/joe/image.gif");
   * displayURL("file:///user/joe/image.pdf");
   * displayURL("file:///user/joe/image.xml");
   * displayURL("file:///user/joe/iimage.svg");
   *</PRE>
   * Under Unix, the system browser is hard-coded to be 'netscape'.
   * Netscape must be in your PATH for this to work.
   *<BR>
   * Under Windows, this will bring up the default browser under windows,
   * usually either Netscape or Microsoft IE. The default browser is
   * determined by the OS.
   *<BR>
   * adapted from :
   * http://www.javaworld.com/javaworld/javatips/jw-javatip66_p.html
   *<BR>
   * NOTE: param url the file's url(the url must start with either
   * "http://" or"file://").
   *
   * [TODO] Handle the Mac better.
   * @param url to display in popup browser
   * @return false if error.
   * @see MAExplorer#logDRYROTerr
   * @see Util#showMsg2
   * @see Util#showMsg3
   */
  boolean displayURL(String url)
  { /* displayURL */
    String
      WIN_PATH= "rundll32",	        /* default browser under windows.*/
      WIN_FLAG= "url.dll,FileProtocolHandler", /* The flag to display a url.*/
      UNIX_PATH= "netscape",	        /* default browser under UNIX.*/
      UNIX_FLAG= "-remote openURL";   /* flag to display a url.*/
    String cmd= null;
    
    try
    { /* try to start browser */
      if(mae.osName!=null && mae.osName.startsWith("Windows"))
      { /* windows */
        /* cmd= "rundll32 url.dll,FileProtocolHandler http://..." */
        cmd= WIN_PATH + " " + WIN_FLAG + " " + url;
        Process p= Runtime.getRuntime().exec(cmd);
      } /* windows */
      
      /* Mac Code  [JE-06-6-2001]
       * Use the MRJ from Apple to envoke the default browser
       */
      else if(mae.osName.startsWith("Mac"))
      { /* Macintosh */
        try
        {
          Class C = Class.forName("com.apple.mrj.MRJFileUtils");
          C.getMethod("openURL",
                      new Class [] {Class.forName("java.lang.String")}
                     ).invoke((Object)null, new Object [] {url});
        }
        catch(ClassNotFoundException e)
        {
          showMsg2("Can't start Web browser on this Mac "+ e,
                   Color.white, Color.red );
          showMsg3("Missing file 'com.apple.mrj.MRJFileUtils'",
                   Color.white, Color.red );
          Util.popupAlertMsg("Can't start Web browser on this Mac",
                             "Can't start Web browser on this Mac "+ e+"\n"+
                             "Missing file 'com.apple.mrj.MRJFileUtils'",
                             4, 60);
          return(false);
        } /* Macintosh */
        catch(Exception e)
        { /* this catches following exceptions related to envoking MRJ
           * + NoSuchMethodException
           * + SecurityException
           * + IllegalAccessException
           * + IllegalArgumentExceptionb
           * + InvocationTargetException
           */
           mae.logDRYROTerr("Can't start Mac Web browser, old version of MRJ "+e);
          Util.popupAlertMsg("Can't start Web browser on this Mac",
                             "Can't start Mac Web browser, old version of MRJ "+e,
                             4, 60);
           return(false);
        }
      }
      
      else
      { /* UNIX */
        /* Under Unix, Netscape has to be running for the
         * "-remote" command to work. So, we try sending the
         * command and check for an exit value. If the exit
         * command is 0, it worked, otherwise we need to start
         * the browser.
         */
        
        /* cmd= "netscape -remote openURL(http://www.javaworld.com)" */
        cmd= UNIX_PATH + " " + UNIX_FLAG + "(" + url + ")";
        Process p= Runtime.getRuntime().exec(cmd);
        
        try
        {
          /* wait for exit code -- if it's 0, command worked,
           * otherwise we need to start the browser up.
           */          
          if(p.waitFor()!=0)
          {
            /* Command failed, start up the browser*/
            /* cmd= "netscape http://www.javaworld.com" */
            cmd= UNIX_PATH + " " + url;
            p= Runtime.getRuntime().exec(cmd);
          }
        }
        catch(InterruptedException x)
        {
          showMsg("Can't start browser");
          Util.popupAlertMsg("Can't start Web browser",
                             "Can't start Web browser",
                             4, 60);
          return(false);
        }
      } /* UNIX */
    } /* try to start browser */
    
    catch(IOException e)               /* couldn't exec browser*/
    {
      showMsg("Can't start browser");
      return(false);
    }
    
    return(true);
  } /* displayURL */
  
  
  /**
   * displayPDF() - Display PDF file in adobe, for use in stand-alone.
   *<PRE>
   *</PRE>
   * Under Unix, the system browser is hard-coded to be 'Acrobat'.
   * Acrobat must be in your PATH for this to work.
   *<BR>
   * Under Windows, this will bring up Acrobat (if installed).
   *<BR>
   * adapted from :
   * http://www.javaworld.com/javaworld/javatips/jw-javatip66_p.html
   *<BR>
   *
   * [TODO] Handle the Mac better.
   * @param pdfFile file to display in Adobe Acrobat
   * @return false if error.
   * @see MAExplorer#logDRYROTerr
   * @see Util#showMsg2
   * @see Util#showMsg3
   */
  boolean displayPDF(String pdfFile)
  { /* displayPDF */
    String
      WIN_PATH= "rundll32",	               /* default under windows.*/
      WIN_FLAG= "url.dll,FileProtocolHandler", /* The flag to display a pdf.*/
      UNIX_PATH= "acroread";	               /* default Adobe Acrobat browser
                                              * in UNIX is "acroread".*/
    String cmd= null;
    // [TODO] get Mac and UNIX to work
    try
    { /* try to start browser */
      if(mae.osName!=null && mae.osName.startsWith("Windows"))
      { /* windows */
          /* cmd= "rundll32 url.dll,FileProtocolHandler filename.pdf" */
        cmd= WIN_PATH + " " + WIN_FLAG + " " + pdfFile;
        Process p= Runtime.getRuntime().exec(cmd);
      } /* windows */
      else          
      { /* UNIX */          
        /* UNIX exec is "acroread" */          
        cmd= UNIX_PATH + " " + pdfFile;          
        Process p= Runtime.getRuntime().exec(cmd);          
      } /* UNIX */      
    } /* try to start browser */
    
    catch(IOException e)               /* couldn't exec Acrobat */
    {
      showMsg("Can't start Acrobat");
      e.printStackTrace();
      return(false);
    }
    
    return(true);
  } /* displayPDF */
  
  
  /**
   * checkImagesDirExists() - check if images directory exists
   * @return true if  MAE maeDir/Images/ subdirectory exists.
   */
  static boolean checkImagesDirExists()
  { /* checkImagesDirExists */
    String maePrjDir= mae.defDir;
    if(maePrjDir==null)
      return(false);
    
    String
      endStr_MAE_= mae.fileSeparator + "MAE" + mae.fileSeparator,
      maeTreeDir= (((maePrjDir.toUpperCase()).endsWith(endStr_MAE_))
                     ? maePrjDir.substring(0,maePrjDir.length()-4)
                     : maePrjDir),
    imagesDir= maeTreeDir + "Images" + mae.fileSeparator;
    File fImgDir= new File(imagesDir);
    boolean flag= fImgDir.isDirectory();
    return(flag);
  } /* checkImagesDirExists */
  
    
  /**
   * popupImageOfHPsample() - popup image of current gene in Web browser.
   * <PRE>
   * Verify data exists before process:
   *    1) has Database_ID data corresponding to xxxx in images
   *    2) has maeDir/Images/xxxx.jpg,
   * @param ms is the sample to display
   * @return true if succeed
   * @see MAExplorer#logDRYROTerr
   * @see Util#showMsg
   * @see #checkImagesDirExists
   * @see #displayURL
   * @see #popupViewer
   * @see #prepForFileURL
   */
  boolean popupImageOfHPsample(MaHybridSample ms)
  { /* popupImageOfHPsample */
    /* [0] Use old MGAP DB images in Gifs/*.gif directory.
     * [TODO] depricate this.
     */
    if(mae.cfg.database.startsWith("MGAP DB") &&
       ms.oImageName!=null && ms.oImageName.length()>0)
    {
      popupViewer(mae.getCodeBase(), ms.oImageName,"MaeAux");
      return(true);
    }
    
    /* [1] Make sure name of .jpg image files exist.
     * [TODO] this assumes image file name is <idxDatabaseFileID>+".jpg"
     * which it is for the mAdb DB, but could also be
     * constructed as <Database_File>+".jpg". Perhaps the solution is
     * to check for both! If we are successful, then we have a list of
     *(imgX[],imgY[],sImgList[],nImgList) to pass to the montage generator.
     */
    if(mae.sampDB.idxDatabaseFileID==-1 && ms.databaseFileID==null ||
       ms.databaseFileID.length()==0)
    {
      showMsg("Can't show image. No 'DatabaseFileID' exists in the Samples DB.");
          Util.popupAlertMsg("Can't show image",
                             "Can't show image. No 'DatabaseFileID' exists in the Samples DB.",
                             4, 60);
      return(false);
    }
    
    /* [2] Check for MAE maeDir/Images/ subdirectory. */
    if(!checkImagesDirExists())
    {
      showMsg("No Image folder exists for file Image/'DatabaseFileID'.jpg");
      return(false);
    }
    
    /* [3] Make sure the image file exists */
    File fImg;
    String
      maePrjDir= mae.defDir,
      endStr_MAE_= mae.fileSeparator + "MAE" + mae.fileSeparator,
      maeTreeDir= (((maePrjDir.toUpperCase()).endsWith(endStr_MAE_))
                     ? maePrjDir.substring(0,maePrjDir.length()-4)
                     : maePrjDir),
    imagesDir= maeTreeDir,
    imageFileName= "Images" + mae.fileSeparator + ms.databaseFileID+".jpg",
    sImgName= imagesDir + imageFileName;
    
    /* make sure image files exist and make list of full paths */
    try
    { /* test if derived from "DatabaseFileID" */
      if(ms.databaseFileID==null)
        throw new Exception("Can't find 'databaseFileID' for sample");
          Util.popupAlertMsg("Can't find 'databaseFileID' for sample",
                             "Can't find 'databaseFileID' for sample.",
                             4, 60);
      fImg= new File(sImgName);
      
      String imageURL= prepForFileURL(""+sImgName); /* /C:/.../*.jpg */
      
      if(mae.CONSOLE_FLAG)
      {
        if(mae.isAppletFlag)
          popupViewer(mae.getCodeBase(),imageFileName, "SampleImage");
        else
          displayURL(imageURL);  /* popup in stand-alone web browser */
        return(false);
      }
      
      if(!fImg.exists())
      {
        throw new Exception("Can't find file["+sImgName+"]");
      }
    } /* test if derived from "DatabaseFileID" */
    
    catch(Exception e)
    { /* not derived from "DatabaseFileID" - try "Database_File" */
      sImgName= imagesDir+ ms.hpName+".jpg";
      try
      {
        fImg= new File(sImgName);
        if(!fImg.exists())
          throw new Exception("File not found");
      }
      catch(Exception e2)
      {
        showMsg("Can't find image to display");
        return(false);
      }
    } /* not derived from "DatabaseFileID" - try "Database_File" */
    
    /* [4] Create a File:/ url and pass it to popup browser. */
    String imageURL= "file://" + sImgName;
    
    popupViewer(null,imageURL,"SampleImage");
    
    return(true);
  } /* popupImageOfHPsample */
  
   
  /** 
   * getCurDateStr() - return date string in the format of YYMMDDHHMMSS.
   * Note: Adds "0" to first single digit if value is 1-9.
   */
  static String getCurDateStr()
  { /* getCurDateStr */
  
    GregorianCalendar cal= new GregorianCalendar();  /* setup date */
    int
      yy= cal.get(Calendar.YEAR)-2000,    /* year in 2 digit format */
      mm= cal.get(Calendar.MONTH)+1,      /* Month, note: add one since 0-11 */
      dd= cal.get(Calendar.DAY_OF_MONTH), /* day */
      hh= cal.get(Calendar.HOUR_OF_DAY),  /* hour */
      min= cal.get(Calendar.MINUTE),      /* minute */
      ss= cal.get(Calendar.SECOND);       /* seconds */
    String
      sY, /* Year  ie "03" */
      sM, /* Month ie "12" */
      sD, /* Day ie "30" */
      sH, /* Hour ie "01", */
      sMin, /* minutes ie "40" */
      sS; /* Seconds ie "60" */
    Integer i; /* for converting int to str */

    
    i= new Integer(yy);
    sY= "0"+ i.toString();
    
    if(mm<10)
    {
      i= new Integer(mm);
      sM= "0"+ i.toString();
    }
    else
    {
      i= new Integer(mm);
      sM= i.toString();  
    }
    if(dd<10)
    {
      i= new Integer(dd);
      sD= "0"+ i.toString();
    }
    else
    {
      i= new Integer(dd);
      sD= i.toString();  
    }
    if(hh<10)
    {
      i= new Integer(hh);
      sH= "0"+ i.toString();
    }
    else
    {
      i= new Integer(hh);
      sH= i.toString();  
    }
    if(min<10)
    {
      i= new Integer(min);
      sMin= "0"+ i.toString();
    }
    else
    {
      i= new Integer(min);
      sMin= i.toString();  
    }
    if(ss<10)
    {
      i= new Integer(ss);
      sS= "0"+ i.toString();
    }     
    else
    {
      i= new Integer(ss);
      sS= i.toString();  
    }
    
    String date = sY+sM+sD+sH+sMin+sS;
  
  return(date);
 } /* getCurDateStr */
  
  
  /**
   * cvf2s() - convert float to string with precision # of digits.
   * If precision > 0 then limit # of digits in fraction
   * @param v is value to convert
   * @param i is the # of digits precision in the mantissa
   * @return string approximating "%0.pd"
   * If abs(v) < pow(10.0,-p) then return "+0" or "-0"
   */
  static String cvf2s(float v, int precision)
  { /* cvf2s */
    NumberFormat nf= NumberFormat.getInstance();
    
    nf.setMaximumFractionDigits(precision);
    nf.setGroupingUsed(false);
    
    String s= nf.format(v);
    
    return(s);
  } /* cvf2s */
  
  
  /**
   * cvd2s() - convert double to string with precision  # of digits
   * If precision > 0 then limit # of digits in fraction
   * @param v is value to convert
   * @param i is the # of digits precision in the mantissa
   * @return string approximating "%0.pd"
   * If abs(v) < pow(10.0,-p) then return "+0" or "-0"
   */
  static String cvd2s(double v, int precision)
  { /* cvd2s */
    NumberFormat nf= NumberFormat.getInstance();
    
    nf.setMaximumFractionDigits(precision);
    nf.setGroupingUsed(false);
    
    String s= nf.format(v);
    
    return(s);
  } /* cvd2s */
  
  
  /**
   * cvs2d() - convert String to double
   * @param str to convert
   * @return numeric value, else defaultFloat if bad string
   */
  static double cvs2d(String str)
  { /* cvs2d */
    double d;
    try
    {
      Double D= new Double(str);
      d= D.doubleValue();
    }
    catch(NumberFormatException e)
    {d= (double)defaultFloat;}
    return(d);
  } /* cvs2d */
  
  
  /**
   * cvs2d() - convert String to double with default value
   * @param str to convert
   * @param defaultValue if bad numeric string
   * @return numeric value
   */
  static double cvs2d(String str, double defaultValue)
  { /* cvs2d */
    double d;
    try
    {
      Double D= new Double(str);
      d= D.doubleValue();
    }
    catch(NumberFormatException e)
    {d= defaultValue;}
    return(d);
  } /* cvs2d */
  
  
  /**
   * cvs2f() - convert String to float
   * @param str to convert
   * @return numeric value, else defaultFloat if bad string
   */
  static float cvs2f(String str)
  { /* cvs2f */
    float f;
    try
    {
      Float F= new Float(str);
      f= F.floatValue();
    }
    catch(NumberFormatException e)
    {f= defaultFloat;}
    return(f);
  } /* cvs2f */
  
  
  /**
   * cvs2f() - convert String to float with default value
   * @param str to convert
   * @param defaultValue if bad numeric string
   * @return numeric value
   */
  static float cvs2f(String str, float defaultValue)
  { /* cvs2f */
    float f;
    try
    {
      Float F= new Float(str);
      f= F.floatValue();
    }
    catch(NumberFormatException e)
    {f= defaultValue;}
    return(f);
  } /* cvs2f */
  
  
  /**
   * cvs2i() - convert String to int
   * @param str to convert
   * @return numeric value, else defaultInt if bad string
   */
  static int cvs2i(String str)
  { /* cvs2i */
    int i;
    try
    {
      i= java.lang.Integer.parseInt(str);
    }
    catch(NumberFormatException e)
    {i= defaultInt;}
    return(i);
  } /* cvs2i */
  
  
  /**
   * cvs2i() - convert String to int with default value
   * @param str to convert
   * @param defaultValue if bad numeric string
   * @return numeric value
   */
  static int cvs2i(String str, int defaultValue)
  { /* cvs2i */
    int i;
    try
    {
      i= java.lang.Integer.parseInt(str);
    }
    catch(NumberFormatException e)
    {i= defaultValue;}
    return(i);
  } /* cvs2i */
  
  
  /**
   * cvs2iArray() - convert String arg list "1,4,3,6,..."  to int[]
   * @param str string containt a list of Strings
   * @param maxExpected # of numbers to be parsed
   * @param delimChr delimiter to be used
   * @return array of ints else null if problem
   */
  static int[] cvs2iArray(String str, int maxExpected, String delimChr)
  { /* cvs2iArray */
    if(str==null)
      return(null);
    
    String sA[]= cvs2Array(str,maxExpected, delimChr);    
    if(sA == null)
      return(null);    
    int
      n= sA.length,
      iAR[]= new int[n];
    
    for(int i=0;i<n;i++)
      iAR[i]= cvs2i(sA[i]);
    
    if(n <= 0)
      return(null);
    else
      return(iAR);
  } /* cvs2iArray */
  
  
  /**
   * cvs2Array() - cvt arg list "1,4,3,6,..."  to "," - delim String[].
   * If there are more than maxExpected number of args in string, ignore
   * them and just return what has been parsed so far.
   * @param str string containt a list of Strings
   * @param maxExpected # of numbers to be parsed [DEPRICATED and ignored]
   * @param delimChr delimiter to be used
   * @return array of Strings else null if problem
   */
  static String[] cvs2Array(String str, int maxExpected, String delimiterChr)
  { /* cvs2Array */
    if(str==null || delimiterChr==null)
      return(null);
    
    if(maxExpected<=0)
      maxExpected= Math.min(str.length(),1000); /* estimate */
    char
      delim= delimiterChr.charAt(0),
      searchArray[]= str.toCharArray();
    int
      delimCnt= 0,
      count= 0,
      strLen= str.length();
    
    while(count <strLen)
    { /* count delimChr chars */
      if(searchArray[count++]==delim)
        delimCnt++;
    }
    delimCnt++; /* need one more */
    String
      token,
      tokArray[]= new String[delimCnt]; /* return them all at once */
    char
      ch,
      lineBuf[]= str.toCharArray(),     /* cvt input string to char[]*/
      tokBuf[]= new char[1000];         /* token buffer */
    int
      bufSize= str.length(),            /* size of input buffer */
      bufCtr= 0;                        /* working input buffer index */
       
    /* Parse data from line buffer into tokens */
    if(maxExpected<delimCnt)
      delimCnt= maxExpected;            /* min(maxExpected,delimCnt) */
    
    for(int c=0; c<delimCnt; c++)
    { /* get and store next token*/
      int
        lastNonSpaceTokCtr= 0,          /* idx of last non-space char*/
        tokCtr= 0;                      /* size of tokBuf */
      
      while(bufCtr<bufSize && lineBuf[bufCtr]!= delim)
      { /* build token*/
        ch= lineBuf[bufCtr++];
        
        /* track total string len and last non-space char */
        tokBuf[tokCtr++]= ch;
        lastNonSpaceTokCtr= tokCtr;     /* saves doing trim */        
      } /* build token*/
      
      tokBuf[tokCtr]= '\0';             /* terminate token */
      token= new String(tokBuf,0,tokCtr); /* cvt char[] to string */
      
      /* get just string we want with no trailing whitespace */
      token= token.substring(0,lastNonSpaceTokCtr);
      
      tokArray[c]= token;               /* i.e. save token */
      //tokArray[c]= token.trim();      /* i.e. save token */
      
      if(bufCtr<bufSize && lineBuf[bufCtr]==delim)
        bufCtr++;		                    /* move past delimChr */
    } /* get and store field names*/
    
    return(tokArray);
  } /* cvs2Array */  
  
  
  /**
   * cvtBackslash2Fwdslash() - convert '\\' to '/' in the string
   * @param sInput is input string
   * @param return converted string
   */
  static String cvtBackslash2Fwdslash(String sInput)
  { /* cvtBackslash2Fwdslash */
    if(sInput==null)
      return(null);
    char
      cBuf[]= sInput.toCharArray();     /* cvt input string to char[]*/
    int lth= sInput.length();
    
    for(int i=0;i<lth;i++)
      if(cBuf[i]=='\\')
        cBuf[i]= '/';                  /* replace it */
    
    String sOut= new String(cBuf);
    
    return(sOut);
  } /* cvtBackslash2Fwdslash */  
  
  
  /**
   * addQuotesIfSpace() - add "..." around string if it contains a space
   * but don't add quotes if it already has a leading ".
   * @param str to convert
   * @return converted string
   */
  static String addQuotesIfSpace(String str)
  { /* addQuotesIfSpace */
    if(str==null)
      return(null);
    if(str.length()==0)
      return(str);
    if(str.charAt(0)=='\"')
      return(str);    /* no change */
    int idxSpace= str.indexOf(" ");
    if(idxSpace==-1)
      return(str);
    
    String sR= "\"" + str + "\""; 
    return(sR);
  } /* addQuotesIfSpace */
  
  
  /**
   * dateStr() - return a new Date string of the current day and time
   * @return a new Date string of the current day and time
   */
  static String dateStr()
  { /* dateStr */
    Date dateObj= new Date();
    String date= dateObj.toString();
    
    return(date);
  } /* dateStr */
  
  
  /**
   * timeStr() - return a new daytime HH:MM:SS string of the current time.
   * @return a new daytime HH:MM:SS string of the current time
   */
  static String timeStr()
  { /* timeStr */
    Calendar cal= Calendar.getInstance();
    int
      hrs= cal.get(Calendar.HOUR_OF_DAY),
      mins= cal.get(Calendar.MINUTE),
      secs= cal.get(Calendar.SECOND);
    String dayTime= hrs+":"+mins+":"+secs;
    
    return(dayTime);
  } /* timeStr */
  
  
  /**
   * printCurrentMemoryUsage() - print: %free, total memory usage and timeOfday
   * @param msg to display with this memory snapshot
   * @see FileIO#logMsgln
   * @see #cvd2s
   * @see #timeStr
   */
  static void printCurrentMemoryUsage(String msg)
  { /* printCurrentMemoryUsage */
    if(!mae.PRINT_MEM_TIMES)
      return;               /* NO-OP */
    
    Runtime rt= Runtime.getRuntime();
    long
      totMem= rt.totalMemory(),
      freeMem= rt.freeMemory();
    int pctFreeMem= (int)(100*freeMem/totMem);
    if(msg==null)
      msg="-";
    String memStr= "**** Memory["+msg+ "]:\n  "+pctFreeMem+
                   "% free, tot="+ Util.cvd2s((totMem/1000000.0),2)+
                   "Mb ["+ Util.timeStr() + "] ****";
    
    fio.logMsgln(memStr);
  } /* printCurrentMemoryUsage */
  
  
  /**
   * prepForFileURL() - map "\" to "/" chars in string to use for URL
   * @param string to prep
   * @return possibly remapped string
   */
  static String prepForFileURL(String str)
  { /* prepForFileURL */
    String sR= str;
    char
      ch,
      cBuf[]= new char[str.length()];
    
    for(int i= str.length()-1; i>=0;i--)
    {
      ch= str.charAt(i);
      if(ch=='\\' )
        ch= '/';
      //else if(ch==':' )
      //  ch= '|';
      
      cBuf[i]= ch;
    }
    
    sR= new String(cBuf);
    return(sR);
  } /* prepForFileURL */
  
  
  /**
   * findIndexForKey() - lookup index of key in data[].
   * Get length from array and skip testing NULL fields.
   * @param data is data to search
   * @param key is key to use in searching data
   * @return -1 if it is not found.
   */
  static int findIndexForKey(String data[], String key)
  { /* findIndexForKey */
    if(key==null || data==null)
      return(-1);
    int
      lth= data.length,
      idx= -1;
    String datumI;
    
    for(int i=0;i<lth;i++)
    {
      datumI= data[i];
      if(datumI!=null && datumI.equalsIgnoreCase(key))
      {
        idx= i;
        break;
      }
    }
    return(idx);
  } /* findIndexForKey */
  
  
  /**
   * cvLongToHex() - convert long to hex string
   */
  static String cvLongToHex(long data)
  { /* cvLongToHex */
    String sR= null;
    sR= java.lang.Long.toHexString(data);
    return(sR);
  } /* cvLongToHex */
  
  
  /**
   * nextZoomMag() - modulo 1X, 2X, 5X, 10X, 20X, 50X, 100X, 200X, 500X, 1000X
   * @param val is the value to use in computing next zoom value mod maxVal
   * @param maxVal is the maximum value to use
   * @param is the next zoom value, else 1 if failed
   */
  static int nextZoomMag(int val, int maxVal)
  { /* nextZoomMag */
    if(val<=1)
      val= 2;
    else if(val==2)
      val= 5;
    else if(val==5)
      val= 10;
    else if(val==10)
      val= 20;
    else if(val==20)
      val= 50;
    else if(val==50)
      val= 100;
    else if(val==100)
      val= 200;
    else if(val==200)
      val= 500;
    else if(val==500)
      val= 1000;
    else if(val==1000)
      val= 1;
    
    if(val>maxVal)
      val= 1;
    
    return(val);
  } /* nextZoomMag */
  
  
  /**
   * setupColorSpectrumRange() - setup spectrumcolor map data range [0.0:1.0].
   * Delta is 1.0/(maxColors-1). If redIsZeroFlag, then RED is 0.0
   * else RED is 1.0. Note: maxColors MUST BE 9!!!!
   * [TODO] setup Dichromasy colors.
   * @param ratioColor[is the color mapping
   * @param ratioRange is the color values
   * @param maxColors is the size of arrays
   * @param redIsZeroFlag set red as zero else red is 1.0
   * @return true if ok.
   */
  static boolean setupColorSpectrumRange(Color ratioColor[], float ratioRange[],
  int maxColors, boolean redIsZeroFlag )
  { /* setupColorSpectrumRange */
    if(maxColors!=9)
      return(false);
    
    float
      delta= 1.0F/(maxColors-1),
      initialVal= (redIsZeroFlag) ? 0.0F : 1.0F,
      val;
    if(!redIsZeroFlag)
      delta= -delta;
    
    ratioRange[0]= (val= initialVal); /* RED */
    ratioRange[1]= (val += delta);
    ratioRange[2]= (val += delta);
    ratioRange[3]= (val += delta);
    ratioRange[4]= (val += delta);
    ratioRange[5]= (val += delta);
    ratioRange[6]= (val += delta);
    ratioRange[7]= (val += delta);
    ratioRange[8]= (val += delta);   /* BLACK */
    
    ratioColor[0]= Color.red;
    ratioColor[1]= Color.orange;
    ratioColor[2]= Color.yellow;
    ratioColor[3]= Color.green;
    ratioColor[4]= Color.cyan;
    ratioColor[5]= Color.blue;
    ratioColor[6]= Color.magenta;
    ratioColor[7]= Color.gray;
    ratioColor[8]= Color.darkGray;
    
    /* set the colors */
    if(mae.useDichromasyFlag)
    { /* COLORS  if Dichromasy */
      /* [TODO] */
    }
    
    /*
    if(mae.CONSOLE_FLAG)
      for(int i= 0;i<9; i++)
        System.out.println("U-SCSR i="+i+" spectrumRange="+spectrumRange[i]+
                           " spectrumColor="+spectrumColor[i]);
    */
    return(true);
  } /* setupColorSpectrumRange */
  
  
  /**
   * setPseudoArrayRatioColorMap() - set pseudoarray ratios color map data.
   * The Ratio Color Map is used with the Ratio PseudoArray images.
   * @param ratioColor[is the color mapping [0:MAX_COLORS-1]
   * @param ratioRange is the ratio range values [0:MAX_COLORS-1]
   * @return true if ok.
   */
  static boolean setPseudoArrayRatioColorMap(Color ratioColor[], float ratioRange[],
                                             int maxColors)
  { /* setPseudoArrayRatioColorMap */
    if(ratioColor.length<mae.dwPI.MAX_COLORS || ratioRange.length<mae.dwPI.MAX_COLORS)
      return(false);
    
    for(int i=0;i<mae.dwPI.MAX_COLORS;i++)
    {
      mae.dwPI.ratioColor[i]= ratioColor[i];
      mae.dwPI.ratioRange[i]= ratioRange[i];
    }
    mae.updatePseudoImgFlag= true;  /* force PseudoArray to be recomputed */
    
    return(true);
  } /* setPseudoArrayRatioColorMap */
  
  
  /**
   * setPseudoArrayColorSpectrumMap() - set pseudoarray color map spectrum data.
   * The Spectrum Map is used with the p-value PseudoArray image.
   * @param spectrumColor is the color mapping [0:MAX_COLORS-1]
   * @param spectrumRange is the range value [0:MAX_COLORS-1].
   * @param maxColors is the size of arrays
   * @return true if ok.
   */
  static boolean setPseudoArrayColorSpectrumMap(Color spectrumColor[], 
                                                float spectrumRange[])
  { /* setPseudoArrayColorSpectrumMap */
    if(spectrumColor.length<mae.dwPI.MAX_COLORS ||
       spectrumRange.length<mae.dwPI.MAX_COLORS)
      return(false);
    
    for(int i=0;i<mae.dwPI.MAX_COLORS;i++)
    {
      mae.dwPI.spectrumColor[i]= spectrumColor[i];
      mae.dwPI.spectrumRange[i]= spectrumRange[i];
    }
    
    mae.updatePseudoImgFlag= true;  /* force PseudoArray to be recomputed */
    
    return(true);
  } /* setPseudoArrayColorSpectrumMap */
  
  
  /**
   * setupColorRatioRange() - setup ratio color map iteratively w/spacing delta.
   * Note the range is centerVal-midColor*delta to 1+maxColor*delta
   * which maps from deep RED(min) to BLACK(1.0) to deep RED(max).
   * Note: maxColors MUST BE 9!!!!
   * If testing X/Y Ratio(useNegRangeFlag is FALSE):
   *     X/Y > 1 is RED
   *     X/Y < 1 is GREEN
   * If testing X-Y Zscore(useNegRangeFlag is TRUE):
   *     X-Y > 0 is RED
   *     X-Y < 0 is GREEN
   *
   * [TODO]
   *  1. make it work correctly with clusterGram
   *  2. add additive color map RED+GREEN = YELLOW and if saturated
   *     then it is WHITE.
   * @param delta is the difference between color values computed
   * @param ratioColor is the color mapping
   * @param ratioRange is the color values
   * @param maxColors is the size of arrays
   * @param useNegRangeFlag allow negative range values
   * @return true if ok.
   */
  static boolean setupColorRatioRange(float delta, Color ratioColor[],
                                      float ratioRange[], int maxColors,
                                      boolean useNegRangeFlag )
  { /* setupColorRatioRange */
    if(maxColors!=9)
      return(false);
    
    float
      val,
      centerVal= (useNegRangeFlag)
                   ? 0.0F    /* Zscore */  
                   : 1.0F;   /* Ratio */
    
    ratioRange[4]= (val= centerVal);
    ratioRange[5]= (val += delta);
    ratioRange[6]= (val += delta);
    ratioRange[7]= (val += delta);
    ratioRange[8]= (val += delta);        /* RED */
    
    if(useNegRangeFlag)
    { /* negative range */
      ratioRange[0]= -ratioRange[8];     /* Green */
      ratioRange[1]= -ratioRange[7];
      ratioRange[2]= -ratioRange[6];
      ratioRange[3]= -ratioRange[5];
    }
    else
    { /* inverse range */
      ratioRange[0]= 1.0F/ratioRange[8]; /* Green */
      ratioRange[1]= 1.0F/ratioRange[7];
      ratioRange[2]= 1.0F/ratioRange[6];
      ratioRange[3]= 1.0F/ratioRange[5];
    }
    
    /* set the colors */
    if(mae.useDichromasyFlag)
    { /* COLORS  if Dichromasy */
      ratioColor[5]= new Color(0XFFFF10);  /* dark yellow */
      ratioColor[6]= new Color(0XFFFF6B);
      ratioColor[7]= new Color(0XFFFF9C);
      ratioColor[8]= new Color(0XFFFFC6);  /* bright yellow */
      
      ratioColor[3]= new Color(0,0,90);   /* was 110 */
      ratioColor[2]= new Color(0,0,140);  /* was 150 */
      ratioColor[1]= new Color(0,0,190);  /* was 210 */
      ratioColor[0]= new Color(0,0,255);  /* BLUE */
    }
    else
    { /* COLORS normal RED to BLACK to GREEN */
      ratioColor[8]= new Color(255,0,0);  /* RED */
      ratioColor[7]= new Color(190,0,0);  /* was 210 */
      ratioColor[6]= new Color(140,0,0);  /* was 150 */
      ratioColor[5]= new Color(90,0,0);   /* was 110 */
      
      ratioColor[3]= new Color(0,90,0);   /* was 110 */
      ratioColor[2]= new Color(0,140,0);  /* was 150 */
      ratioColor[1]= new Color(0,190,0);  /* was 210 */
      ratioColor[0]= new Color(0,255,0);  /* GREEN */
    }
    
    ratioColor[4]= new Color(0,0,0);    /* BLACK */
    ratioColor[9]= Color.cyan;          /* Illegal color */
    /*
    if(mae.CONSOLE_FLAG)
    for(int i= 0;i<9; i++)
      System.out.println("U-SCR i="+i+" ratioRange="+ratioRange[i]+
                         " ratioColor="+ratioColor[i]);
    */
    return(true);
  } /* setupColorRatioRange */
  
  
  /**
   * setRatioColor() - map data ratio to color in current color scheme Color is in(ratioMin : ratioMax].
   * i.e. ratioMin < ratio <= ratioMax for ratio < ratioRange[midColor],
   * and  ratioMin <= ratio < ratioMax for ratio >ratioRange[midColor].
   * @param ratio is the value to map
   * @param ratioColor is the color mapping
   * @param ratioRange is the color values
   * @param maxColors is the size of arrays
   * @return color else if failed, return ratioColor[maxColors].
   */
  static Color setRatioColor(float ratio, Color ratioColor[],
                             float ratioRange[], int maxColors )
  { /* setRatioColor */
    int
      i,
      midColor= maxColors/2;  /* i.e. black */
    
    /* Test lower range [0:midColor-1] */
    for(i=1;i<=midColor;i++)
      if(ratio < ratioRange[i])
        return(ratioColor[i-1]);
    
    /* Test if middle range(midColors-1:midColor+1) */
    if((ratio > ratioRange[midColor-1]) && 
       (ratio < ratioRange[midColor+1]))
      return(ratioColor[midColor]);
    
    /* Test upper range [midColor+1:maxColors-1] */
    for(i=maxColors-2; i>=midColor;i--)
      if(ratio > ratioRange[i])
        return(ratioColor[i+1]);
    
    return(ratioColor[maxColors]);   /* failed */
  } /* setRatioColor */
  
  
  /**
   * cvFontS2I() - convert font size "8Ppt", "10pt", "12pt", or "14pt" to int
   * @param fontSizeStr is the string name of the font size
   * @return integer value of font size
   */
  static int cvFontS2I(String fontSizeStr)
  { /* cvFontS2I */
    int fontSize= 12;
    
    if(fontSizeStr==null)
      fontSize= 12;
    else if(fontSizeStr.equalsIgnoreCase("14pt"))
      fontSize= 14;
    else if(fontSizeStr.equalsIgnoreCase("12pt"))
      fontSize= 12;
    else if(fontSizeStr.equalsIgnoreCase("10pt"))
      fontSize= 10;
    else if(fontSizeStr.equalsIgnoreCase("8pt"))
      fontSize= 8;
    
    return(fontSize);
  } /* cvFontS2I */
  
  
  /**
   * cvtStrArrayToStr() - create(String) from String[] array with "\n" w/len
   * Fast method creates a tmp Character [] of required size and
   * stuffs it, then converts it to a string at one time instead
   * of repeatedly using concat(i.e. +=).
   * @param strData is array of strings
   * @param len is size of array of string(could be less thatn strData.length)
   * @return single '\n' separated string
   */
  static String cvtStrArrayToStr(String strData[], int len)
  { /* cvtStrArrayToStr */
    if(strData==null || len==0)
      return("");
    
    int
      n= 0,                            /* index of current free position in outCarray[]*/
      reqLen= 0,                       /* total size of outC[] */  
      k,
      sSize;
    String sData;
    
    for(int x= 0; x < len; x++)
    {
      sData= strData[x];
      if(sData!=null)
        reqLen += sData.length()+1;       /* add +1 for "\n" */
    }
    
    char outC[]= new char[reqLen+1]; 
    
    for(int x= 0; x < len; x++)
    { /* stuff chars into outC[] */
      sData= strData[x];
      if(sData!=null)
      { /* only stuff non-null strings */
        sSize= sData.length();
        for(k=0;k<sSize;k++)
          outC[n++]= sData.charAt(k);
        outC[n++]= '\n';
      }
    } /* stuff chars into outC[] */
    
    String outStr= new String(outC,0,n);  /* do only ONE string conversion */
    
    return(outStr);
  } /* cvtStrArrayToStr */
  
  
  /**
   * cvtStrArrayToStr() - create(String) from String[] array with "\n"
   * @param strData is array of strings
   * @return single '\n' separated string
   * @see #cvtStrArrayToStr
   */
  static String cvtStrArrayToStr(String strData[])
  { /* cvtStrArrayToStr */
    if(strData==null)
      return("");
    else
      return(cvtStrArrayToStr(strData, strData.length));
  } /* cvtStrArrayToStr */
  
  
  /**
   * cvtStrArrayToDelimStr() - create delimited(String) from String[]
   * array with terminator as last character.
   * @param strData is array of strings
   * @param delim is the delimiter to use
   * @param terminator is the end of string terminator to use
   * @return delimited string
   */
  static String cvtStrArrayToStr(String strData[], String delim, String terminator)
  { /* cvtStrArrayToStr */
    if(strData==null)
      return("");
    int lth= strData.length;
    String sR= "";
    
    for(int i=0;i<lth;i++)
    {
      sR += strData[i];
      if(i== (lth-1))
        sR += terminator;
      else
        sR += delim;
    }
    
    return(sR);
  } /* cvtStrArrayToStr */
  
  
  /**
   * cvtValueToStars() - cvt val in[0:valMax] to "****"+"   " string
   * where the maximum # of stars for v==vMax is specified.
   * The rest of the string is filled with spaces.
   * maxStars is clipped at 30.
   * @param val is value to be converted to starts
   * @param valMax is maximum value expected
   * @param maxStars is max # of stars to use for largest value
   * @param rightFillFlag if true will right fill the string with spaces
   * @return list of "*********"
   */
  static String cvtValueToStars(float val, float valMax, int maxStars,
                                 boolean rightFillFlag)
  { /* cvtValueToStars */
    if(maxStars>30)
      maxStars= 30;
    float similarity= 1.0F-(val/valMax);
    int
      nSpaces,
      iSim;
    String
      sSimilarity,
      leftStars,
      stars= "****************************************",
      spaces= "                                       ",
      rightFill;
    
    if(similarity<0)
      similarity= 0;
    
    iSim= (int)(maxStars*similarity);
    iSim= (iSim>maxStars) ? maxStars :((iSim<0) ? 0 : iSim);
    leftStars= (iSim==0) ? "" : stars.substring(1,iSim);
    nSpaces= (maxStars-iSim+1);
    nSpaces= Math.min(nSpaces,maxStars);
    rightFill= (iSim==maxStars) ? "" : spaces.substring(1,nSpaces);
    
    if(rightFillFlag)
      sSimilarity= leftStars + rightFill;   /* constant width for display */
    else
      sSimilarity= leftStars;
    
    return(sSimilarity);
  } /* cvtValueToStars */
  
  
  /**
   * cvtSpacesToUnderscores() - replace spaces and other illegal file name
   * characters with '_' chars to make file names highly portable.
   * <PRE>
   *      : space( ) ; " { } < > = , ? \ / % * ! @ & | ' ` \t
   * </PRE>
   * @param str to be converted
   * @return converted string
   */
  static String cvtSpacesToUnderscores(String str)
  { /* cvtSpacesToUnderscores */
    char
      ch,
      cBuf[]= new char[str.length()];
    
    for(int i= str.length()-1; i>=0;i--)
    {
      ch= str.charAt(i);
      cBuf[i]= (ch==':' || ch==' ' || ch=='(' || ch==')' ||
                ch==';' || ch=='"' || ch=='{' || ch=='}' ||
                ch=='<' || ch=='>' || ch=='=' || ch==',' ||
                ch=='?' || ch=='\\' || ch=='/' || ch=='%' ||
                ch=='*' || ch=='!' || ch=='@' || ch=='&' ||
                ch=='|' || ch=='\'' || ch=='`' || ch=='\t')
                   ? '_' : ch;
    }
    
    str= new String(cBuf);
    return(str);
  } /* cvtSpacesToUnderscores */
  
  
  /**
   * chkDirFinalSeparator() - make dir path end in fileSeparator.
   * Note: Mac Bug found and fixed by Jai Evans 10/12/00.
   * @param sPath is the path to chk
   * @param fS is the file separator
   * @return directory path at end of file separator
   */
  static String chkDirFinalSeparator(String sPath, String fS )
  { /* cchkDirFinalSeparator */
    String sDir= (sPath.lastIndexOf(fS)==sPath.length()-1)
                    ? sPath.substring(0,sPath.lastIndexOf(fS)+1)
                    : sPath+fS;
    return(sDir);
  } /* chkDirFinalSeparator */
  
  
  /**
   * getRatioStr() - get "F1/F2", "Cy3/Cy5" or "Cy5/Cy3", etc. string.
   * Analyze the mae.useRatioDataFlag and mae.useCy5OverCy3Flag
   * @return the new string and set it into mae.reportRatioStr.
   */
  static String getRatioStr()
  { /* getRatioStr */
    //if(!mae.useRatioDataFlag)
    //  mae.reportRatioStr= "F1/F2";
    //else
    mae.reportRatioStr= ((mae.useCy5OverCy3Flag)
                            ? (mae.cfg.fluoresLbl2+"/"+mae.cfg.fluoresLbl1)
                            : (mae.cfg.fluoresLbl1+"/"+ mae.cfg.fluoresLbl2));
    
    return(mae.reportRatioStr);
  } /* getRatioStr */
  
  
  /**
   * rmvFinalSubDirectory() - remove trailing ".../dir/" if it exists
   * otherwise return original string.
   * If remove the sub directory and optDir is not null, then add
   * it back with a delimiter.
   * @param dirStr is path to edit
   * @param optDir is optional directory to add
   * @param chkMAEtreeFlag also check MAE tree validity and make it if needed
   * @return final subdirectory
   */
  public static String rmvFinalSubDirectory(String dirStr, String optDir,
                                            boolean chkMAEtreeFlag )
  { /* rmvFinalSubDirectory */
    String sR= dirStr;
    if((dirStr.toUpperCase()).endsWith("MAE"+mae.dynFileSeparator))
    { /* remove the trailing directory */
      /* Check for MAE prject directory tree. If not found
       * create the directory tree.
       */
      String maeTreeDir= dirStr.substring(0,dirStr.length()-4);
      if(chkMAEtreeFlag)
      {
        boolean hasTreeFlag= mae.us.checkAndMakeMAEdirTree(maeTreeDir);
      }
      sR= maeTreeDir;
      if(optDir!=null)
        sR += optDir + mae.dynFileSeparator;
    }
    
    return(sR);
  } /* rmvFinalSubDirectory */
    
  
  /**
   * cvtEnvPaths2Properties() - get all system ENV variables and set them as 
   * separate Java properties so we can get them individually from 
   * System.getProperty(var) calls.
   * @return true if succeed
   */    
  static boolean cvtEnvPaths2Properties()
  { /* cvtEnvPaths2Properties */    
    /* [1] Under Windows use "cmd /C set" else "env" for Unix type systems */ 
    String 
      osName= System.getProperty("os.name"),
      cmd= (osName.startsWith("Win")) ? "cmd /C set" : "env";    
    Properties p= System.getProperties();
    
    /* [2] Parse it into properties */
    try 
    {
      Runtime runtime= Runtime.getRuntime();
      InputStream inputStream= runtime.exec(cmd).getInputStream();
      InputStreamReader isr= new InputStreamReader(inputStream);
      BufferedReader br= new BufferedReader(isr);
      String 
        readline= br.readLine(),
        key= "",
        val= "";
      int indexE= -1;
      while(readline != null) 
      {
        indexE= readline.indexOf("=");
        if(indexE >= 0) {
          key= readline.substring(0,indexE);
          val= readline.substring(indexE+1,
          readline.length());
          p.put(key,val);
        }
        readline= br.readLine();
      }
      
      System.setProperties(p);
    } 
    catch(IOException ioe)
    {
      ioe.printStackTrace();
      return(false);
    }
        
    return(true);
  } /* cvtEnvPaths2Properties */
  
  
  /** 
   * cvFileNameWithMod() - insert modifier string before file extension
   * @param fileName (with file extension)
   * @param mod to insert before file extension
   * @return new file name
   */
  static String cvFileNameWithMod(String fileName, String mod)
  { /* cvFileNameWithMod */
    if(mod==null)
      return(fileName);
    int idx= fileName.lastIndexOf(".");
    String
      fileBase= (idx==-1) ? "" : fileName.substring(0,idx-1),
      fileExt= (idx==-1) ? "" : fileName.substring(idx),
      sR= fileBase+mod+fileExt;
    return(sR);
  } /* cvFileNameWithMod */
  
  
  /**
   * sleepMsec() - sleep nMsec where nMsec must be >= 0 milliseconds.
   * @param sleepMsec # of milliseconds to sleep
   */
  static void sleepMsec(int sleepMsec)
  { /* sleepMsec */
    if(sleepMsec<=0)
      return;
    try
    { Thread.sleep(sleepMsec); }
    catch( InterruptedException e )
    {}
  } /* sleepMsec */
  
  
  /**
   * rmvRtnChars() - remove return chars. Map '\r' or "\r\n" to '\n' chars.
   * @param String str to process
   * @return String with '\r' removed.
   */
  static String rmvRtnChars(String str)
  { /* rmvRtnChars */    
    if(str==null)
      return(null);
    
    String sR= "";
    int
      oldSize= str.length(),
      oldSizeM1= oldSize-1,
      newSize= 0,
      iLA,
      j= 0;
    char
      ch, 
      chLA;
    
    for(int i= 0; i<oldSize; i++)
    { /* process the string */
      iLA= i+1;
      ch= str.charAt(i);
      
      if(ch=='\r')
      { /* Check look ahead to map \r to \n */
        chLA= (iLA<oldSizeM1) ? str.charAt(iLA) : '\0';
        if(chLA=='\n')
          continue;   /* ignore the \r if part of \r\n pair */
        else
          ch= '\n';   /* map the \r terminator to \n terminator */
      } /* Check look ahead to map \r to \n */
      
      /* Build new string */
      sR += ch;
      newSize++;
    } /* process the string */
    
    return(sR);
  } /* rmvRtnChars */
  
  
  /**
   * cleanup() - cleanup global static allocated variables in this class
   * If statics are added later to this class, then set them to
   * null here.
   */
  void cleanup()
  { /* cleanup */
    mStatus= "";
    fio= null;
  } /* cleanup */
  
} /* end of class Util */



/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */
/*   Class LogTextFrame to log commands, messages and errors        */
/* @@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@@ */

/**
 * LogTextFrame() - simple text area frame with control buttons
 * Derived from TextFrame.java written by Bob Stephens, ABCC,FCRDC
 */
class LogTextFrame extends Frame implements ActionListener, WindowListener
{
  
  /** Background color of entire frame */
  Color
    frameBkGrdColor= Color.white; /* was: Color.gray; */
  /** background color of text area */
  Color
    textBkGrdColor= Color.white;
  /** background color of buttons */
  Color
    buttonBkGrdColor= Color.lightGray;
  
  /** Text area  for displaying the text*/
  TextArea
    area;
  /** hide button to hide the Log window. You need to issue a command
   * from the MAExplorer menu to get it back). */
  Button
    hideButton;
  /** Save Log As button to save the log data as a new named Report/XXXX.txt file */
  Button
    saveAsButton;
  /** clear button to clear the log and clear the log file is using a file */
  Button
    clearButton;
  /** GUI layout */
  GridBagLayout
    layout;
  /** grid bag constraints */
  GridBagConstraints
    constraints;
  /** default font */
  Font
    default_font= new Font("SansSerif", Font.BOLD+Font.ITALIC,12);
  /** control button font */
  Font
    button_font= new Font("Serif", Font.BOLD,12);
  /** text font in TextArea */
  Font
    text_font= new Font("MonoSpaced", Font.BOLD,12);
  /** text to beas popup window title */
  String
    title= "";
  /** text to be written to text area */
  String
    logTextStr= "";
  
  /** Flag indicating that data is to be appended text area.
   * If it is not being appended to text area, it is at least appended
   * to the logTextStr.
   */
  boolean
    appendLogDataFlag;
  /** Visibility flag */
  boolean
    isVisibleFlag;
  /** optional log file if logging */
  File
    logFile= null;
  /** optional log file "maeHistory.log" */
  String
   logFileName= null;
  /** optional full path of log file if it exists. */
  String
    fullLogFilePath= null;
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
   * LogTextFrame() - constructor to create popup logging text window
   * @param title of window
   * @param fullLogFilePath file path to save file
   * @param logFileName name of log file
   * @param appendLogDataFlag determines if new file or append to old file
   * @param isVisibleFlag if visible when created
   * @param optOffset
   * @see #repaint
   */
  LogTextFrame(String title, String fullLogFilePath, String logFileName,
               boolean appendLogDataFlag, boolean isVisibleFlag, int optOffset)
  { /* LogTextFrame */
    if(title!=null)
      //super(title);
      setTitle(title);
    
    /* [1] Save State */
    this.title= title;
    this.fullLogFilePath= fullLogFilePath;
    this.logFileName= logFileName;
    this.appendLogDataFlag= appendLogDataFlag;
    this.isVisibleFlag= isVisibleFlag;
    
    /* [2] Set up the GUI */
    setSize(450,200);                   /*(320,250) */
    
    /* Center frame on the screen, PC only */
    Dimension scr= Toolkit.getDefaultToolkit().getScreenSize();
    int frmOffset= (optOffset==0) ? 50 : optOffset;
    Point pos= new Point(frmOffset+((scr.width-this.getSize().width)/3),
                         frmOffset+((scr.height-this.getSize().height)/3));
    this.setLocation(pos);
    
    setFont(default_font);
    addWindowListener(this);
    
    layout= new GridBagLayout();
    constraints= new GridBagConstraints();
    constraints.fill= GridBagConstraints.BOTH;
    setLayout(layout);
    
    setBackground(frameBkGrdColor);
    
    area= new TextArea();
    area.setBackground(textBkGrdColor);
    area.setFont(text_font);
    addComponent(area,0,0,2,1,1,100);
    
    /* add Button Panel */
    Panel buttonPanel= new  Panel();
    
    hideButton= new Button("Hide Log");
    hideButton.setBackground(buttonBkGrdColor);
    hideButton.setFont(button_font);
    hideButton.addActionListener(this);
    buttonPanel.add(hideButton);
    
    saveAsButton= new Button("Save Log As");
    saveAsButton.setBackground(buttonBkGrdColor);
    saveAsButton.setFont(button_font);
    saveAsButton.addActionListener(this);
    buttonPanel.add(saveAsButton);
    
    clearButton= new Button("Clear Log");
    clearButton.setBackground(buttonBkGrdColor);
    clearButton.setFont(button_font);
    clearButton.addActionListener(this);
    buttonPanel.add(clearButton);
    addComponent(buttonPanel,1,1,2,1,1,1);
    
    /* [3] Repaint it if enabled. */
    if(isVisibleFlag)
      this.repaint();
  } /* LogTextFrame */
  
  
  /**
   * saveAsTxtFile() - save logTextStr data in .txt file logFileName
   * @return true if succeed
   * @see EventMenu#promptFileName
   * @see FileIO#writeFileToDisk
   * @see Util#dateStr
   * @see Util#showMsg
   */
  boolean saveAsTxtFile()
  { /* saveAsTxtFile */
    String
      defTxtDir= fullLogFilePath,
      oTxtFileName= Util.mae.em.promptFileName("Enter .txt file name",
                                               fullLogFilePath,
                                               logFileName,
                                               null,   /* sub dir */
                                               ".log",
                                               true,   /* saveMode*/
                                               true    /* useFileDialog */
                                               );
    if(oTxtFileName!=null)
    {
      String
        savedMsg= "Saved "+oTxtFileName,     /* Write the file */
        sReport= "File: "+oTxtFileName+
                 "\nDatabase: "+Util.mae.cfg.dbSubset+
                 "\nDate: "+Util.dateStr()+
                 "\n"+logTextStr;
      boolean
      flag= Util.mae.fio.writeFileToDisk(oTxtFileName, sReport);
      
      if(flag)
        Util.showMsg("Saved as ["+oTxtFileName+"]");
      return(flag);
    }
    return(false);
  } /* saveAsTxtFile */
  
  
  /**
   * addComponent() - add a text component to the frame
   */
  private void addComponent(Component obj, int x, int y, int xw,int yw,
                            int xg, int yg)
  { /* addComponent */
    constraints.gridx= x;
    constraints.gridy= y;
    constraints.gridwidth= xw;
    constraints.gridheight= yw;
    constraints.weightx= xg;
    constraints.weighty= yg;
    layout.setConstraints(obj,constraints);
    add(obj);
  } /* addComponent */
  
  
  /**
   * actionPerformed() - event handler for button press
   * @param e is button press event
   * @see #hideWindow
   * @see #saveAsTxtFile
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    Button butObj= (Button)e.getSource();
    
    if(butObj==hideButton)
    {
      hideWindow();
    }
    else if(butObj==saveAsButton)
    {  /* save logTextStr data in .txt file logFileName */
      saveAsTxtFile();
    }
    else if(butObj==clearButton)
    { /* clear the log and log file if logging data and open new log file */
      area.setText("");
      logTextStr= "";
    }
  } /* actionPerformed */
  
  
  /**
   * setLogWindowVisible() - make the popup window visible or invisible
   * @param isVisibleFlag to set whether the window is visible or not
   * @see #repaint
   */
  void setLogWindowVisible(boolean isVisibleFlag)
  { /* setLogWindowVisible */
    this.setVisible(isVisibleFlag);
    this.isVisibleFlag= isVisibleFlag;
    this.repaint();
  } /* setLogWindowVisible */
  
  
  /**
   * appendLog() - save text into log area.
   * @param text to append to log
   */
  void appendLog(String text)
  { /* appendLog */
    logTextStr += text;
    area.append(text);
  } /* appendLog */
  
  
  /**
   * appendLogLn() - save text+"\n" into log area.
   * @param text to append to log
   * @see #appendLog
   */
  void appendLogLn(String text)
  { /* appendLog */
    appendLog(text+"\n");
  } /* appendLog */
  
  
  /**
   * appendToLogFile() - append text to open log file
   * @param text to append to log
   * @return true if succeed
   */
  boolean appendToLogFile(String text)
  { /* appendToLogFile */
    int size= text.length();
    if(logDataBufSize<size)
    { /* grow only if need to */
      logDataBufSize= size+1;
      logDataBuf= new char[logDataBufSize];
    }
    
    /* [TODO] rewrite as System.arraycopy() to copy substring directly in one call */
    for(int i=0; i<size; i++)
      logDataBuf[i]= text.charAt(i); /* create buffer to write */
    
    try
    {
      logFileWriter.write(logDataBuf, 0, size); /* write(not size+1) */
      //logFileWriter.flush();		    /* flush data file */
      return(true);
    }
    catch(Exception e)
    { return(false); }
  } /* appendToLogFile */
  
  
  /**
   * clearLog() - clear the log area and delete old log file.
   */
  void clearLog()
  { /* clearLog */
    area.setText("");
    logTextStr= "";
  } /* clearLog */
  
  
  /**
   * hideWindow() - flush the buffer and hide the log window
   */
  void hideWindow()
  { /* hideWindow */
    setVisible(false);
  } /* hideWindow */
  
  
  /**
   * windowClosing() - close the log window
   * @param e is window closing event
   * @see #hideWindow
   */
  public void windowClosing(WindowEvent e)
  { /* windowClosing */
    hideWindow();
  } /* windowClosing */
  
  public void windowActivated(WindowEvent e) {}
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) {}
  public void windowDeiconified(WindowEvent e) {}
  public void windowIconified(WindowEvent e) {}
  public void windowOpened(WindowEvent e) {}
  
  
} /* end of class TextFrame */
