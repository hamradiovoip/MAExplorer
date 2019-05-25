/** File: UserState.java */

import java.awt.*;
import java.io.*;
import java.io.FileReader;
import java.util.*;

/**
 * The UserState class is used to read and write the user state.
 * It also performs other functions including login validation for 
 * specific projects, read and write GeneBitSets (.cbs)
 * and Condition lists (.hpl) in the State/ directory.
 * directory.
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Governzipipent there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author P. Lemkin (NCI), G. Thornwall (SAIC), NCI-Frederick, Frederick, MD
 * @version $Date: 2003/11/24 21:28:30 $   $Revision: 1.52 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class UserState
{
  /* [TODO] Cleanup. Resolve the use of read/save...LPW() methods.
   * We are not currently using them and they need only be
   * used when talking with a Web-server. 
   */

  /** User Set version # for file format compatibility */
  final static String
    US_VERSION= "1.2";    /* NOTE:11-9-02 changed to 1.2 when added Condition Params[] data */
  
  /** link to global instance of MAExplorer */
  private MAExplorer 
    mae;                
  /** link to global instance of FileIO */
  private FileIO
    fio;                
  /** user name from dialog handler */
  String 
    uName;              
  
  /** current name of "name=value" pair */
  private String
    parseName;
  /** current value of "name=value" pair */
  private String             
    parseValue;         
  /** current delim of "name(delim)value" pair */
  private int
    parseDelimChar;     
  
  /** application startup database table */    
  private Table
    msuTbl;             
  /** indices for values in startup DB table */
  private int
    idxName;
  /** indices for values in startup DB table */
  private int                
    idxValue;
  
  /** for pLdq login dialog popup */
  private Frame
    dialogFrame;        
  /** holds popup Login dialog query */ 
  PopupLoginDialogQuery 
    pLdq;                 
   
       
  /**
   * UserState() - constructor
   * @param mae is MAExplorer instance
   * @param userStateName is the user state name
   */
  UserState(MAExplorer mae, String userStateName)
  { /* UserState */
    this.mae= mae;
    this.uName= (userStateName==null) ? "UserState" : userStateName;
    fio= mae.fio;
    
    msuTbl= null;
    idxName= -1;
    idxValue= -1;
  } /* UserState */
  
  
  /**
   * check_dbID() - get or create Unique Database ID ("DIDxxxxxx").
   * It is used as prefix for State/ files.
   * If it does not exist, then create it when save the database.
   * The param is "dbID".
  */  
  String check_dbID()
  { /* check_dbID */
    if(mae.dbID.length()==0)
    { /* Create Unique Database ID ("DIDxxxxxx") */
      Date date= new Date();
      long time= date.getTime();
      int hashCode= (int)((time+14825L) % 999997L);
      mae.dbID= "DID"+hashCode;
    }
    return(mae.dbID);
  } /* check_dbID */
  
  
  /**
   * setName() - set UserState name
   * @param userStateName is the user state name
   */
  void setName(String userStateName)
  { /* setName */
    if(userStateName!=null)
      this.uName= userStateName;
  } /* setName */
  
  
  /**
   * doLogin() - get user login,password and do validation if requested
   * @param projectName is the MAExplorer project database name
   * @param doValidateChkFlag do a validation check.
   * @see PopupLoginDialogQuery
   * @see PopupLoginDialogQuery#dialogQuery
   * @see Util#popupAlertMsg
   * @see Util#showMsg
   * @see #validateLoginAtServer
   */
  boolean doLogin(String projectName, boolean doValidateChkFlag)
  { /* doLogin */
    /* [TODO] rbb */
    boolean flag= false;
    String optMsg= null;
    
    if(dialogFrame==null)
    { /* make popup if never made it before */
      dialogFrame= new Frame("Dialog");
      pLdq= new PopupLoginDialogQuery(mae,dialogFrame);
    }
    
    for(int nTimes=1;nTimes<=3; nTimes++)
    { /* Give them several chances to type it in right */
      flag= pLdq.dialogQuery(projectName,optMsg);
      if(!flag)
      { /* no more chances since they did Cancel! */
        Util.showMsg("Login aborted...");
        return(false);
      }
      else
      { /* finish login process */
        String
          uN= pLdq.loginNameData,
          uPW= pLdq.passwordData,
          uPrj= projectName; /* Note: use last global value */
        
        if(!doValidateChkFlag)
        { /* don't bother with validation - just set values */
          /* Note: these will be validated with a 401 error
           * test in JavaCGIBridge.
           */
          flag= true;
          mae.userName= pLdq.loginNameData;
          mae.userPasswd= pLdq.passwordData;
          break;
        }
        flag= validateLoginAtServer(uN, uPW, uPrj);
        if(flag)
        {
          Util.popupAlertMsgLine("You are logged in.");
          mae.isLoggedInFlag= true;
          break;
        }
        else
          if(nTimes==1)
            optMsg= "Invalid username or password - try again.";
          else if(nTimes==2)
            optMsg= "Still invalid login. Last chance ...";
      } /* finish login process */
    } /* Give them several chances to type it in right */
    
    return(flag);
  } /* doLogin */
  
  
  /**
   * validateLoginAtServer() - validate login at MAE web server.
   * If this is on the web, validate login at server.
   * @param uN is the login Name
   * @param uPW is the user password
   * @param uPrj is the project name
   * @return true if login is valid
   * @see FileIO#logMsgln
   * @see FileIO#readData
   * @see Util#showMsg
   */
  boolean validateLoginAtServer(String uN, String uPW, String uPrj )
  { /* validateLoginAtServer */
    boolean flag= false;
    String sR= null;
    
    if(uN!=null && uN.length()>0 && uPW!=null && uPW.length()>0 &&
       uPrj!=null && uPrj.length()>0 )
    { /* login at server */
    /* [TODO] do javaCGIbridge CGI login to
     *    /cgi-bin/maeLogin?uN=<uName>&uPW=<password>&uPrj=<project>
     * it should return "OK", or "FALSE - <error msg> ".
     */
      if(mae.DBUG_LOGIN)
        flag= true;    /* just make it true for "file://" */
      else if(mae.codeBase.startsWith("http://"))
      { /* go to server */
        String
          loginURL= mae.maeServerBase + "cgi-bin/maeLogin?uN="+uN+
                    "&uPW="+uPW+"&uPrj="+uPrj;
        if(mae.DBUG_LOGIN)
        { /* ***DEBUG_LOGIN*** */
          sR= "OK";
        } /* ***DEBUG_LOGIN*** */
        else
          sR= fio.readData(loginURL, "Logging in ...");
        
        if(sR.startsWith("OK"))
          flag= true;
        else
        { /* handle error */
          fio.logMsgln("maeLogin: "+sR);
        }
      } /* go to server */
    } /* login at server */
    
    /* [2] Fatal error */
    if(!flag)
    {
      Util.showMsg("Invalid login - check your password.");
      return(false);
    }
    
    mae.userName= uN;      /* save it for user DB access */
    mae.userPasswd= uPW;
    mae.curPrjName= uPrj;
    
    return(true);
  } /* validateLoginAtServer */
  
  
  /**
   * validateUserProject() - validate this HybSample login for this project.
   * If the user had already logged in and the project is the
   * mae.validPrj[0:mae.nValidPrj-1] list, then don't check since already
   * logged in. Otherwise, get the (uN,uPW) from popup dialog and then check
   * with the server to see if the (uN,uPW,uPrj) is in the password file.
   *<P>
   * If the login is OK, then it has the side effect of setting
   *<PRE>
   *   mae.curProjName= projectName;
   *   mae.isLoggedInFlag= true;
   * Also push projectName into the list of projects.
   *   mae.validProjects[mae.nValidProjects++]
   *</PRE>
   * @param userName is the user name
   * @param userPasswd is the user password
   * @param prjName is the project name
   * @return true if login is OK.
   * @see #doLogin
   * @see #validateLoginAtServer
   */
  boolean validateUserProject(String userName, String userPasswd, String prjName)
  { /* validateUserProject */
    /* Try to validate user for this HP */
    boolean
      loginOK= false,     /* default is not logged in */
      sameUserLoggedIn= (mae.isLoggedInFlag &&
                         mae.userName.equals(userName) &&
                         mae.userPasswd.equals(userPasswd));
      /*
      if(mae.CONSOLE_FLAG)
        fio.logMsgln("US-VUP isLoggedInFlag="+mae.isLoggedInFlag+
                     " sameUserLoggedIn="+sameUserLoggedIn+
                     " prjName="+prjName+
                     " mae.curProjName="+mae.curProjName+
                     " userName="+userName+
                     " userPasswd="+userPasswd);
       */
    
    /* Test if log in */
    if(mae.validPrj==null)
    { /* setup login verified projects list*/
      mae.validPrj= new String[mae.MAX_PROJECTS];
      mae.nValidPrj= 0;
    }
    
    if(sameUserLoggedIn && userName.length()>0 && userPasswd.length()>0 &&
       prjName.length()>0)
    { /* try to use the previous login but check projects */
      for(int k=0;k<mae.nValidPrj;k++)
        if(mae.validPrj[k].equals(prjName))
          loginOK= true;
      if(!loginOK)
      { /* validate user for a different project */
        loginOK= validateLoginAtServer(userName,userPasswd, prjName);
        if(loginOK)
          mae.validPrj[mae.nValidPrj++]= prjName;
      }
    }
    
    if(!loginOK)
    { /* get (uN,uPW) and go do full log in since not seen before */
      loginOK= doLogin(prjName, true); /* get (uN,uPW) and
       * validate for uPRJ*/
      if(loginOK)
        mae.validPrj[mae.nValidPrj++]= prjName;
    }
    
    if(loginOK)
    { /* save ok status and project in project list */
      mae.curPrjName= prjName;  /* global cur. project */
      mae.isLoggedInFlag= true;
    }
    
    /*
    if(mae.CONSOLE_FLAG)
    {
      fio.logMsgln("US-VUP.2 mae.isLoggedInFlag="+
                   mae.isLoggedInFlag+
                   " sameUserLoggedIn="+sameUserLoggedIn+
                   " loginOK="+loginOK+
                   " prjName="+prjName+
                   " mae.nValidProjects="+mae.nValidProjects);
      for(int k=0;k<mae.nValidProjects;k++)
        fio.logMsgln("MHP-VHP.2 mae.validProjects["+k+"]="+
                     mae.validProjects[k]);
    }
   */
    
    return(loginOK);
  } /* validateUserProject */
  
  
  /**
   * openState() - open named user state
   * @param userStateName is the name of their state to access
   */
  boolean openState(String userStateName)
  { /* openState */
    /* [TODO] resolve how we merge state from .mae startup file with
     * specific state we are reading.
     */
    
    mae.userStateName= userStateName;
    
    EventMenu.notAvailableYet();  /* print "Not available yet." */
    return(false);
    
   /* [TODO] add code to
    * 1. map state name to state index mae.userStateIdx.
    * 2. read state
    */
  } /* openState */
  
  
  /**
   * openOtherUserState() - open other user's named user state [FUTURE]
   * @param otherUserName is the other use name to access
   * @param userStateName is the name of their state to access
   * @return true if succeed in switching the state.
   */
  boolean openOtherUserState(String otherUserName, String userStateName)
  { /* openOtherUserState */
    /* [TODO] We have to first verify with the Web server that the
     *  mae.userName is allowed access to
     *  state: userStateName for user: otherUserName
     */
    
    mae.otherUserName= otherUserName;
    mae.userStateName= userStateName;
    
    EventMenu.notAvailableYet();  /* print "Not available yet." */
    return(false);
    
    /* [TODO] add code to
     * verify with the Web server that the
     *  ma.userName is allowed access to
     *  state: userStateName for user: otherUserName
     */

    /* [TODO] add code to
     * 1. map state name to state index mae.userStateIdx.
     * 2. read state
     */
  } /* openOtherUserState */
  
  
  /**
   * shareUserState() - share/unshare user state with other user [FUTURE]
   * @param otherUserName is the other use name to access
   * @param userStateName is the name of their state to access
   * @param opr is the operation to perform "share" or "unshare"
   * @return true if succeed in switching the state.
   */
  boolean shareUserState(String otherUserName, String userStateName,
  String opr)
  { /* shareUserState */
    /* [TODO] Tell the Web server to (DIS)ALLOW sharing for
     *  user: ma.userName access to
     *  state: userStateName for user: otherUserName
     */
    
    mae.otherUserName= otherUserName;
    mae.userStateName= userStateName;
    
    EventMenu.notAvailableYet();  /* print "Not available yet." */
    return(false);
    
    /* [TODO] add code to
     * tell the Web server to (DIS)ALLOW sharing for
     *  user: ma.userName access to
     *  state: userStateName for user: otherUserName
     */
  } /* shareUserState */
  
  
  /**
   * saveUserGeneSetUPW() - save user gene bit set by set number [FUTURE]
   * @param uN is the login Name
   * @param uPW is the user password
   * @param userCBSnbr is the bit set number
   * @see FileIO#writeData
   * @see Util#showMsg
   */
  boolean saveUserGeneSetUPW(String uN, String uPW, int userCBSnbr )
  { /* saveUserGeneSetUPW */
    /* [TODO] do password protection by using CGI to check password.
     * Make list of what is required to be saved to restore the
     * gene set when it is read back.
     */
    
    if(userCBSnbr<1 || userCBSnbr>GeneBitSet.MAX_USER_BS ||
       GeneBitSet.userBS[userCBSnbr]==null)
      return(false);   /* did not save set */
    
    GeneBitSet cbs= GeneBitSet.userBS[userCBSnbr];
    int
      maxWords= cbs.maxWords,
      count= cbs.count;
    String
      bName= cbs.bName,
      fileName= uN + "-" + userCBSnbr + ".cbs",
      fullPath= mae.codeBase + "State/" + fileName,
      hdr=  "Date=" + Util.dateStr() + "\n" +
            "database=" + mae.cfg.database + "\n" +
            "dbSubset=" + mae.cfg.dbSubset + "\n" +
            "CBS#=" + userCBSnbr + "\n" +
            "login=" + uN + "\n" +
            "bName=" + bName + "\n" +
            "maxWords=" + maxWords + "\n" +
            "count=" + count + "\n";
    
    int nBytesEst= maxWords*(16+1)+hdr.length()+20;
    StringBuffer sBuf= new StringBuffer(nBytesEst);  /* est. - optimize */
    sBuf.append(hdr);
    
    for(int i=0;i<maxWords;i++)
    {
      String sWord= Util.cvLongToHex(cbs.bitData[i]);
      sBuf.append(sWord);
      sBuf.append("\n");
    }
    
    String dataStr= new String(sBuf);   /* data length is about 55K */
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("US-SUCS: fileName="+fileName+
                   "  fullPath="+fullPath+
                   "  dataStr='"+dataStr+"'");
    */
    
    if(!fio.writeData(fullPath, "Saving gene set #"+userCBSnbr, dataStr))
    {
      Util.showMsg("Can't save gene set #"+userCBSnbr);
      Util.popupAlertMsg("Can't save gene set",
                         "Can't save gene set #"+userCBSnbr,
                         4, 60);
      return(false);
    }
    return(true);
  } /* saveUserGeneSetUPW */
  
  
  /**
   * readUserGeneSetUPW() - read user gene bit set by set number [FUTURE]
   * @param uN is the login Name
   * @param uPW is the user password
   * @param userCBSnbr is the bit set number
   */
  static boolean readUserGeneSetUPW(String uN, String uPW, int userCBSnbr)
  { /* readUserGeneSetUPW */
    /* [TODO] Do password protection by using CGI to check password. */
    return(true);
  } /* readUserGeneSetUPW */
  
  
  /**
   * saveUserGeneSet() - save user gene bit set by set number.
   * Make list of what is required to be saved to restore the
   * gene set when it is read back...
   * It saves it in a .cbs file with the prefix being the gene
   * set name but with spaces changed to '_'
   * @param userCBSnbr is the bit set number
   * @see FileIO#writeData
   * @see Util#showMsg
   * @see Util#cvtSpacesToUnderscores
   * @see Util#dateStr
   */
  boolean saveUserGeneSet(int userCBSnbr)
  { /* saveUserGeneSet */
    if(userCBSnbr<1 || userCBSnbr>GeneBitSet.MAX_USER_BS ||
       GeneBitSet.userBS[userCBSnbr]==null)
    return(false);                  /* did not save set */

    check_dbID();                   /* make sure mae.dbID exists */
    
    GeneBitSet cbs= GeneBitSet.userBS[userCBSnbr];
    int
      maxWords= cbs.maxWords,
      count= cbs.count;
    String
      assignedBSname= cbs.assignedBSname,               /* may be null */
      bName= cbs.bName,                                 /* name of bitset */
      bNameNoSpaces= Util.cvtSpacesToUnderscores(bName),
      fileName= bNameNoSpaces + ".cbs";    
    if(!fileName.startsWith("DID"))
      fileName= mae.dbID + "-" + fileName;
    String
      sStatePath= mae.codeBase + "State" + mae.fileSeparator,
      fullPath= sStatePath + fileName;
    
    File
      f= new File(mae.codeBase),
      fState;
    
    if(!f.isDirectory())
      f.mkdirs();         /* make it - should be there... */
    /* [NOTE] could abort it if it is not there... */
    
    if(!(fState= new File(sStatePath)).isDirectory())
      fState.mkdirs();    /* create "State/" directory */
    
    String
      sWord,
      hdr= "US_VERSION=" + US_VERSION + "\n" +
           "Date=" + Util.dateStr() + "\n" +
           "database=" + mae.cfg.database + "\n" +
           "dbSubset=" + mae.cfg.dbSubset + "\n" +
           "CBSnbr=" + userCBSnbr + "\n";
    
    if(assignedBSname!=null)
      hdr += "assignedBSname="+ assignedBSname + "\n";       /* if exists*/
    
    hdr +=  "bName=" + bName + "\n" +
            "maxWords=" + maxWords + "\n" +
            "count=" + count + "\n";
    
    int nBytesEst= maxWords*(16+1)+hdr.length()+20;
    StringBuffer sBuf= new StringBuffer(nBytesEst);  /* est. - optimize */
    sBuf.append(hdr);
    
    for(int i=0;i<maxWords;i++)
    { /* save each word as a long */
      sWord= ""+cbs.bitData[i];   /* output as long values */
      sBuf.append("w-"+i+"=");
      sBuf.append(sWord);
      sBuf.append("\n");
    }
    
    String dataStr= new String(sBuf);   /* data length is about 55K */
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("US-SUCS: fileName="+fileName+
                   "  fullPath="+fullPath+
                   "  dataStr='"+dataStr+"'");
    */
    
    if(!fio.writeData(fullPath, "Saving gene set #"+userCBSnbr,dataStr))
    {
      Util.showMsg("Can't save gene set #"+userCBSnbr);
      Util.popupAlertMsg("Can't save gene set",
                         "Can't save gene set #"+userCBSnbr,
                         4, 60);
      return(false);
    }
    
    Util.showMsg("Saved Gene Set #"+userCBSnbr+"["+bName+"]");
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("U-SUCS Saved Gene Set #"+userCBSnbr+"["+bName+"]");
    */
    
    return(true);
  } /* saveUserGeneSet */
  
  
  /**
   * readUserGeneSet() - read user gene bit set by file name and save it as a named gene set.
   * If the gene set exists already, wipe out the old one.
   * Note: the gene sets are kept in the State/ subdirectory.
   * If optMAEpath is "@FULL-PATH", then cbsFileName is full path.
   * @param cbsFileName file name for gene bit set
   * @param optMAEpath is the optional path to the State/ directory
   * @return the userCBSnbr else -1 if failed.
   * @see FileIO#readData
   * @see GeneBitSet#findCountAndhighMID
   * @see GeneBitSet#lookupOrMakeNewGeneBitSet
   * @see Util#showMsg
   * @see #getIntValFromLine
   * @see #getLongValFromLine
   * @see #getNextStrNameValFromLine
   * @see #getStrValFromLine
   */
  int readUserGeneSet(String cbsFileName, String optMAEpath)
  { /* readUserGeneSet */
    if(cbsFileName==null && cbsFileName.length()==0)
      return(-1);
    
    String
      basePath= (optMAEpath!=null) ? optMAEpath : mae.codeBase,
      sStatePath= basePath + "State" + mae.fileSeparator,
      fullPath= sStatePath + cbsFileName;
    int userCBSnbr= -1; /* bit set number */
    File
      f= new File(basePath),
      fState= new File(sStatePath);
    boolean fullPathFlag= (optMAEpath!=optMAEpath &&
                           optMAEpath.equals("@FULL-PATH"));
    
    if(fullPathFlag)
      fullPath= cbsFileName;     /* specified full path */
    else if(!fullPathFlag && !fState.isDirectory())
      return(-1);
    
    /* Read user Gene Bit Set file */
    String cbsData= fio.readData(fullPath,"Loading state["+cbsFileName+"]");
    if(cbsData==null)
    {
      Util.showMsg("Can't load state["+cbsFileName+"]");
      Util.popupAlertMsg("Can't load state",
                         "Can't load state gene set ["+cbsFileName+"]",
                         4, 60);
      return(-1);
    }
    
    StringTokenizer parser= new StringTokenizer(cbsData, "=\n", false);
    
    String
      sVal,
      sBitData,
      userStateVersion= null,   /* "US_VERSION" */
      creationDate= null,       /* "Date" */
      database= null,           /* "database" */
      dbSubset= null,           /* "dbSubset" */
      assignedBSname= null,     /* "assignedBSname" */
      bName= null;              /* "bName" */
    int
      iVal= 0,
      maxWords= 0,              /* "maxWords" */
      count= 0;                 /* "count" */
    
    /* Get next line & parse 'name=value\n'
     * Return false if EOF. If fail, return null.
     * The (parseName, parseValue, parseDelimChar) is saved
     * in the global instance.
     */
    parseName= "";               /* start it off */
    
    /* Get parameters which may be out of order in the file. */
    while(parseName!=null)
    { /* Get & test (parseName, parseValue, parseDelimChar) */
      if(!getNextStrNameValFromLine(parser))
        break;
      if((sVal= getStrValFromLine("US_VERSION"))!=null)
        userStateVersion= sVal;
      else if((sVal= getStrValFromLine("Date"))!=null)
        creationDate= sVal;
      else if((sVal= getStrValFromLine("database"))!=null)
        database= sVal;
      else if((sVal= getStrValFromLine("dbSubset"))!=null)
        dbSubset= sVal;
      else if((iVal= getIntValFromLine("CBSnbr",null))!=0)
        userCBSnbr= iVal;
      else if((sVal= getStrValFromLine("assignedBSname"))!=null)
        assignedBSname= sVal;
      else if((sVal= getStrValFromLine("bName"))!=null)
        bName= sVal;
      else if((iVal= getIntValFromLine("maxWords",null))!=0)
        maxWords= iVal;
      else if((iVal= getIntValFromLine("count",null))!=0)
      { /* last token before actual bit sets */
        count= iVal;
        break;
      }
      else
      { /* bad data */
        /*
        mae.logDRYROTerr("[US-RUGS] - parseName='" + parseName +
                         "', parseValue='" + parseValue + "'" +
                         "', parseDelimChar='" + parseDelimChar + "'");
        return(-1);
         */
        break;
      }
    } /* Get & test (parseName, parseValue, parseDelimChar) */
    
    /* Validate version number - ignore data if not equal
     * to current CBS_VERSION
     */
    boolean isDifferentVersion= (userStateVersion!=null &&
                                 !userStateVersion.equals(US_VERSION));
    if(bName==null || userCBSnbr==-1 || 
       (isDifferentVersion && mae.NEVER))
      return(-1);       /* can't read data from older version */    
    
    /* [TODO] add additional Gene Set filtering based on DB validation */
    
    /* Make a new gene bit set  with NO cList[] (for now). */
    int idx= GeneBitSet.lookupOrMakeNewGeneBitSet(bName, null);
    if(idx==-1)
      return(-1);          /* failed */
    
    /* Lookup or create new GBS to userBS[]. It returns -1 if it fails,
     * else return index of userBS[] entry.
     */
    GeneBitSet cbs= GeneBitSet.userBS[idx];
    cbs.assignedBSname= assignedBSname;   /* could be null */
    
    /* Stuff the data */
    for(int i=0;i<maxWords;i++)
      cbs.bitData[i]= getLongValFromLine("w-"+i,parser);
    
    /* Set other fields as well */
    cbs.count= count;        /* save count from file */
    cbs.findCountAndhighMID(cbs.ml);
    
    /* Note: the numbers may change since realloc each time */
    //if(cbs.cl!=null)
    //cbs.cvtBStoCL(cbs.cl);
    
    /*
    if(mae.CONSOLE_FLAG && bName.equals("Edited Gene List"))
      System.out.println("US-RUGS.2 (editedCL.bitSet==userBS[11])="+
                         (mae.gct.editedCL.bitSet==GeneBitSet.userBS[11])+
                         "\n  userBS[11].count="+GeneBitSet.userBS[11].count+
                         " userBS[11].bName="+GeneBitSet.userBS[11].bName );
    */
    Util.showMsg("Reloaded Gene Set #"+userCBSnbr+"["+bName+"]");
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("U-RUCS Reloaded Gene Set #"+userCBSnbr+"["+bName+"]");
     */
    
    return(userCBSnbr);
  } /* readUserGeneSet */
  
  
  /**
   * saveNamedHPlist() - save the named sample HP list (i.e. condition) as a .hpl file.
   * A named sample HP list is a Condition instance and is kept in the list of
   * conditions.
   * It saves it in a State/*.hpl file with the prefix being the gene
   * set condition name but with spaces changed to '_'
   * @param cond is the Condition list of samples to save
   * @return true if succeed
   * @see FileIO#writeData
   * @see Util#cvtSpacesToUnderscores
   * @see Util#dateStr
   * @see Util#showMsg
   */
  boolean saveNamedHPlist(Condition cond, int curCond)
  { /* saveNamedHPlist */
    if(cond==null)
      return(false);
    
    int
      hpListNbr= -1,
      nMScond= cond.nMScond;
    for(int i=0;i<Condition.nCondLists;i++)
      if(cond==Condition.condList[i])
      {
        hpListNbr= i;
        break;
      }
    if(hpListNbr==-1)
      return(false);
    
     check_dbID();                   /* make sure mae.dbID exists */
     
    String
      cName= cond.cName,                /* name of Condition */
      cNameNoSpaces= Util.cvtSpacesToUnderscores(cName),
      fileName= cNameNoSpaces + ".hpl";  
    if(!fileName.startsWith("DID"))
      fileName= mae.dbID + "-" + fileName;
    String
      sStatePath= mae.codeBase + "State" + mae.fileSeparator,
      fullPath= sStatePath + fileName;
    
    File f= new File(mae.codeBase), fState;
    
    if(!f.isDirectory())
      f.mkdirs();                       /* make it - should be there... */
    /* [NOTE] could abort it if it is not there... */
    
    if(!(fState= new File(sStatePath)).isDirectory())
      fState.mkdirs();    /* create "State/" directory */
    
    String  hdr= "US_VERSION=" + US_VERSION + "\n" +
                 "Date=" + Util.dateStr() + "\n" +
                 "database=" + mae.cfg.database + "\n" +
                 "dbSubset=" + mae.cfg.dbSubset + "\n" +
                 "HPlistNbr=" + (hpListNbr+1) + "\n" +
                 "cName=" + cName + "\n" +
                 "nMScond=" + nMScond + "\n";    /* last item in list before
                                                  * "hp-#" lists */
    
    StringBuffer sBuf= new StringBuffer(10000);  /* est. - optimize */
    sBuf.append(hdr);
    
    /* Map cond.msCond[0:nMScond-1] to "hp-#=<value>" list of #s */
    for(int i=0;i<cond.nMScond;i++)
    {
      MaHybridSample ms= cond.msCond[i];
      sBuf.append("hp-"+i+"=");
      sBuf.append(""+ms.idxHP);	         /* indices in mae.hps.msList[] space*/
      sBuf.append("\n");
    }
   
    /* [NOTE] The following has been added as of VERSION 1.2 11-9-2002 */
    
    /* Add the list of cond.cfParamNames[0:cond.cfNparams-1] and 
     * cond.cfParamValues[0:cond.cfNparams-1] parameters associated with the
     * condition.
     */
    sBuf.append("cfNparams=" + cond.cfNparams + "\n"); /* # of params (N,V) pairs */
    for(int i=0;i<cond.cfNparams;i++)
    { /* write out the parameter names across all conditions */
      sBuf.append("paramName-"+i+"=");
      sBuf.append(cond.cfParamNames[i]);
      sBuf.append("\n");
    }
    for(int i=0;i<cond.cfNparams;i++)
    { /* write out the parameter values specific for THIS condition */
      sBuf.append("paramValue-"+i+"=");
      String sParam= cond.cfParamValues[curCond][i];
      if(sParam==null)
        sParam= "";  /* do not print 'null' */
      sBuf.append(sParam);
      sBuf.append("\n");
    }
    
    /* Now convert to string to write it out */
    String dataStr= new String(sBuf);       /* data length */
    
    if(!fio.writeData(fullPath, "Saving sample HP list #"+hpListNbr,dataStr))
    {
      Util.showMsg("Can't save sample HP list #"+hpListNbr);
      return(false);
    }
    
    Util.showMsg("Saved HP list #"+hpListNbr+"["+cName+"]");
    
    return(true);
  } /* saveNamedHPlist */
  
  
  /**
   * readUserHPlist() - read user sample HP list by file name and save it as a
   * named HP list.
   * A named sample HP list is a Condition instance and is kept in the list of 
   * conditions.
   * It reads it in a State/*.hpl file with the prefix being the gene
   * set condition name but with spaces changed to '_'
   * If the named sample HP list exists already, wipe out the old one.
   * Note: the HP list files are kept in the State/ subdirectory.
   * If optMAEpath is "@FULL-PATH", then hplFileName is full path.
   * @param hplFileName file name for HP sample list
   * @param optMAEpath is the optional path to the State/ directory
   * @return the hpListNbr else -1 if failed.
   * @see Condition
   * @see FileIO#readData
   * @see Util#showMsg
   * @see #getIntValFromLine
   * @see #getNextStrNameValFromLine
   * @see #getStrValFromLine
   */
  int readUserHPlist(String hplFileName, String optMAEpath)
  { /* readUserHPlist */
    if(hplFileName==null && hplFileName.length()==0)
      return(-1);

    check_dbID();                   /* make sure mae.dbID exists */
    /* add the DB prefix if it is not there already */
    if(!hplFileName.startsWith("DID"))
      hplFileName= mae.dbID+"-" + hplFileName;
    
    String
      basePath= (optMAEpath!=null) ? optMAEpath : mae.codeBase,
      sStatePath= basePath + "State" + mae.fileSeparator,
      fullPath= sStatePath + hplFileName;
    int hpListNbr= -1;           /* list number */
    File
      f= new File(basePath),
      fState= new File(sStatePath);
    boolean fullPathFlag= (optMAEpath!=optMAEpath &&
                           optMAEpath.equals("@FULL-PATH"));
    
    if(fullPathFlag)
      fullPath= hplFileName;     /* specified full path */
    else if(!fullPathFlag && !fState.isDirectory())
      return(-1);
    
    /* Read user named HP list file */
    String hplData= fio.readData(fullPath,"Loading state["+hplFileName+"]");
    if(hplData==null)
    {
      Util.showMsg("Can't load state["+hplFileName+"]");
      Util.popupAlertMsg("Can't load state",
                         "Can't load state sample set file ["+hplFileName+"]",
                         4, 60);
      return(-1);
    }
    StringTokenizer parser= new StringTokenizer(hplData, "=\n", false);
    
    String
      sVal,
      userStateVersion= null,   /* "US_VERSION" */
      creationDate= null,       /* "Date" */
      database= null,           /* "database" */
      dbSubset= null,           /* "dbSubset" */
      cName= null;              /* "cName" */
    int
      iVal,
      nMScond= 0,               /* "nMScond" */
      cfNparams= 0;             /* "cfNparams" */
    
    /* Get next line & parse 'name=value\n'
     * Return false if EOF. If fail, return null.
     * The (parseName, parseValue, parseDelimChar) is saved
     * in the global instance.
     */
    parseName= "";         /* start it off */
    
    /* Get parameters which may be out of order in the file. */
    while(parseName!=null) 
    { /* Get & test (parseName, parseValue, parseDelimChar) */
      getNextStrNameValFromLine(parser);    /* Get data */
      if((sVal= getStrValFromLine("US_VERSION"))!=null)
        userStateVersion= sVal;
      else if((sVal= getStrValFromLine("Date"))!=null)
        creationDate= sVal;
      else if((sVal= getStrValFromLine("database"))!=null)
        database= sVal;
      else if((sVal= getStrValFromLine("dbSubset"))!=null)
        dbSubset= sVal;
      else if((iVal= getIntValFromLine("HPlistNbr",null))!=0)
        hpListNbr= iVal;
      else if((sVal= getStrValFromLine("cName"))!=null)
        cName= sVal;
      else if((iVal= getIntValFromLine("nMScond",null))!=0)
      { /* last token before actual "hp-#" lists data */
        nMScond= iVal;
        break;
      }
      else 
      { /* bad data */
        /*
        mae.logDRYROTerr("[US-RUHPL] - parseName='" + parseName +
                         "', parseValue='" + parseValue + "'" +
                         "', parseDelimChar='" + parseDelimChar + "'");
        return(-1);
         */
        break;
      }
    } /* Get & test (parseName, parseValue, parseDelimChar) */
    
    /* Validate version number - ignore data if not equal to current CBS_VERSION */
    if(hpListNbr==-1 ||
       userStateVersion!=null && !userStateVersion.equals(US_VERSION))
      return(-1);       /* can't read data from older version */
        
    /* Now add user defined HP condition sets.
     * Make a new conditionusing the name , msList read from the file 
     * and then save the param (NV) data.
     */    
    MaHybridSample msListTmp[]= new MaHybridSample[mae.MAX_HYB_SAMPLES+1]; 
    for(int i=0;i<nMScond;i++)
    { /* Stuff the data */
      int idx= getIntValFromLine("hp-"+i,parser);
      msListTmp[i]= mae.hps.msList[idx];
    }
    Condition cond= new Condition(cName, msListTmp, nMScond, 0);    
    
    /* [NOTE] The following has been added as of VERSION 1.2 11-9-2002 */
    
    /* If present, read the list of cond.cfParamNames[0:cond.cfNparams-1] and 
     * cond.cfParamValues[0:cond.cfNparams-1] parameters associated with the
     * condition.
     */            
    if((cfNparams= getIntValFromLine("cfNparams",parser))!=0)
      { /* read the Condition cfNparams list if present */
        String
          paramName,
          paramValue;
        cond.cfNparams= cfNparams;                  /* set the state */
        cond.cfParamNames= new String[cfNparams];  /* make sure large enough */
        for(int i=0;i<cfNparams;i++)
        { /* get parameter names common across all conditions */
          paramName= getStrValFromLine("paramName-"+i,parser);
          if(paramName!=null)
            cond.cfParamNames[i]= paramName;
        } 
        
        int cIndex= hpListNbr-1;      /* we count from 0 for param array access */
        cond.cfParamValues[cIndex]= new String[cfNparams]; /* realloc values array*/
        for(int i=0;i<cfNparams;i++)
        { /* get parameter values specific to THIS condition */
          paramValue= getStrValFromLine("paramValue-"+i,parser); 
          if(paramValue!=null)
            cond.cfParamValues[cIndex][i]= paramValue;
        }
      } /* read the Condition cfNparams list if present */
    
    Util.showMsg("Reloaded Gene Set #"+hpListNbr+"["+cName+"]");
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("U-RUHPL Reloaded HP list #"+hpListNbr+"["+cName+"]");
    */
    
    return(hpListNbr);
  } /* readUserHPlist */
  
    
  /**
   * saveNamedOCLlist() - save the named OCL list (i.e. ordered condition list)
   * as a .ocl file.
   * @param oclNbr is the Ordered Condition list  to save
   * @return true if succeed
   * @see FileIO#writeData
   * @see Util#cvtSpacesToUnderscores
   * @see Util#dateStr
   * @see Util#showMsg
   */
  boolean saveNamedOCLlist(int oclNbr)
  { /* saveNamedOCLlist */
    
    /* [1] check for valid data */
    if(oclNbr<0)
      return(false);
    Condition cListI[]= Condition.orderedCondList[oclNbr];    
    int oclSize= Condition.nOrderedCondList[oclNbr]; 
    if(cListI==null)
      return(false);   
        
    /* [1] get or create Unique Database ID ("DIDxxxxxx").
     * It is used as prefix for State/ files.
     */
    check_dbID();
     
    /* [2] Setup filenames & paths */
    String
      oclName= Condition.orderedCondListName[oclNbr], /* name of Condition */
      oclNameNoSpaces= Util.cvtSpacesToUnderscores(oclName),
      fileName= oclNameNoSpaces + ".ocl";  
    if(!fileName.startsWith("DID"))
      fileName= mae.dbID + "-" + fileName;
    String
      sStatePath= mae.codeBase + "State" + mae.fileSeparator,
      fullPath= sStatePath + fileName;
    
    File f= new File(mae.codeBase), fState;
    
    if(!f.isDirectory())
      f.mkdirs();                       /* make it - should be there... */
    /* [NOTE] could abort it if it is not there... */
    
    int oclAdjustedNbr= oclNbr+1; /* zero-one counting problem */
    
    if(!(fState= new File(sStatePath)).isDirectory())
      fState.mkdirs();    /* create "State/" directory */
    
    String hdr= "US_VERSION=" + US_VERSION + "\n" +
                "Date=" + Util.dateStr() + "\n" +
                "database=" + mae.cfg.database + "\n" +
                "dbSubset=" + mae.cfg.dbSubset + "\n" +
                "oclNbr=" + oclAdjustedNbr + "\n" +
                "oclName=" + oclName + "\n"+
                "oclSize=" + oclSize + "\n";    /* last item in list before
                                                 * "ocl-#" lists */
    StringBuffer sBuf= new StringBuffer(10000); /* est. - optimize */
    sBuf.append(hdr);
    
    /* [3] Write out the OCL as a list of condition names */
    for(int i=0; i<oclSize; i++) 
    {
      String condName= cListI[i].cName;
      sBuf.append("ocl-" + i + "=");
      sBuf.append(condName);
      sBuf.append("\n");
    }
    
    /* [FUTURE] The following has been added as of VERSION # ??? as of ???? */
    
    /* [4] Add the list of Condition.oclParamNames[0:Condition.oclNparams-1] and
     * Condition.oclParamValues[0:Condition.oclNparams-1] parameters associated with the
     * condition.
     */    
    sBuf.append("oclNparams=" + Condition.oclNparams + "\n"); /* # of params (N,V) pairs */
    for(int i=0;i<Condition.oclNparams;i++)
    { /* save the OCL parameter names across all conditions */
      sBuf.append("oclParamName-"+i+"=");
      sBuf.append(Condition.oclParamNames[i]);
      sBuf.append("\n");
    } /* save the OCL parameter names across all conditions */
    for(int i=0;i<Condition.cfNparams;i++) 
    { /* save OCL parameter values specific for THIS OCL */
      sBuf.append("oclParamValue-"+i+"=");
      String sParam= Condition.oclParamValues[oclNbr][i];
      if(sParam==null)
        sParam= "";  /* do not print 'null' */
      sBuf.append(sParam);
      sBuf.append("\n");
    } /* save OCL parameter values specific for THIS OCL */

    /* [5] Now convert to string to write it out */
    String dataStr= new String(sBuf);       /* data length */
    
    if(!fio.writeData(fullPath, "Saving OCL list #" + oclNbr, dataStr))
    {
      Util.showMsg("Can't save OCL list #"+oclNbr);
      Util.popupAlertMsg("Can't save OCL list",
                         "Can't save OCL list #"+oclNbr,
                         4, 60);
      return(false);
    }
    Util.showMsg("Saved OCL list #"+oclNbr+"["+oclName+"]");
    
    return(true);
  } /* saveNamedOCLlist */
  
  
  /**
   * readUserOCLlist() - read user OCL list by file name and save it as a named OCL list.
   * A named OCL list is an Ordered condition instance and contains a list of 
   * conditions.
   * It reads it in a State/*.ocl file with the prefix being the OCL name
   * but with spaces changed to '_'
   *
   * @param oclFileName file name for OCL file
   * @param optMAEpath is the optional path to the State/ directory
   * @return the oclListNbr else -1 if failed.
   * @see Condition
   * @see FileIO#readData
   * @see Util#showMsg
   * @see #getIntValFromLine
   * @see #getNextStrNameValFromLine
   * @see #getStrValFromLine
   */
  int readUserOCLlist(String oclFileName, String optMAEpath)
  { /* readUserOCLlist */
    /* [1] check for valid data */
    if(oclFileName==null && oclFileName.length()==0)
      return(-1);
    check_dbID();                   /* make sure mae.dbID exists */
    
    /* [2] add the DB prefix if it is not there already */
    if(!oclFileName.startsWith("DID"))
      oclFileName= mae.dbID+"-" + oclFileName;
    
    /* [3] Setup file names & paths */
    String
      basePath= (optMAEpath!=null) ? optMAEpath : mae.codeBase,
      sStatePath= basePath + "State" + mae.fileSeparator,
      fullPath= sStatePath + oclFileName;
    int oclListNbr= -1;           /* list number */
    File
      f= new File(basePath),
      fState= new File(sStatePath);
    boolean fullPathFlag= (optMAEpath!=optMAEpath &&
                           optMAEpath.equals("@FULL-PATH"));
    if(fullPathFlag)
      fullPath= oclFileName;     /* specified full path */
    else if(!fullPathFlag && !fState.isDirectory())
      return(-1);
    
    /* [4] Read user named OCL list file */
    String oclData= fio.readData(fullPath,"Loading state["+oclFileName+"]");
    if(oclData==null)
    {
      Util.showMsg("Can't load state["+oclFileName+"]");
      Util.popupAlertMsg("Can't load OCL state",
                         "Can't load OCL state["+oclFileName+"]",
                         4, 60);
      return(-1);
    }
    StringTokenizer parser= new StringTokenizer(oclData, "=\n", false);
    
    String
      sVal,
      userStateVersion= null,   /* "US_VERSION" */
      creationDate= null,       /* "Date" */
      database= null,           /* "database" */
      dbSubset= null,           /* "dbSubset" */
      oclName= null;            /* "oclName" */
    int
      iVal,
      oclNbr= -1,                /* "oclNbr" */
      oclSize= 0,                /* "oclSize" */
      oclNparams= 0;             /* "cfNparams" */
    
    /* [5] Get next line & parse 'name=value\n'
     * Return false if EOF. If fail, return null.
     * The (parseName, parseValue, parseDelimChar) is saved
     * in the global instance.
     */
    parseName= "";         /* start it off */
   
    while(parseName!=null)  /* Get parameters which may be out of order in the file. */
    { /* Get & test (parseName, parseValue, parseDelimChar) */
      getNextStrNameValFromLine(parser);    /* Get data */
      if((sVal= getStrValFromLine("US_VERSION"))!=null)
        userStateVersion= sVal;
      else 
        if((sVal= getStrValFromLine("Date"))!=null)
          creationDate= sVal;
      else
        if((sVal= getStrValFromLine("database"))!=null)
          database= sVal;
      else 
        if((sVal= getStrValFromLine("dbSubset"))!=null)
          dbSubset= sVal;
      else 
        if((iVal= getIntValFromLine("oclNbr",null))!=0)
          oclNbr= iVal;
      else
        if((sVal= getStrValFromLine("oclName"))!=null)
          oclName= sVal;
      else
        if((iVal= getIntValFromLine("oclSize",null))!=0)
        {
          oclSize= iVal;   /* last token before actual "ocl-#" lists data */
          break;
        }
      else 
      { /* bad data */
       /*
        mae.logDRYROTerr("[US-RUOCLL] - parseName='" + parseName +
                         "', parseValue='" + parseValue + "'" +
                         "', parseDelimChar='" + parseDelimChar + "'");
        return(-1);
       */
        break;
      }
    } /* Get & test (parseName, parseValue, parseDelimChar) */
    
    /* [6] Validate version number - ignore data if not equal to current CBS_VERSION */
    oclNbr--; /* because of 0-1 counter problem */
    if(oclNbr==-1 ||
       userStateVersion!=null && !userStateVersion.equals(US_VERSION))
      return(-1);       /* can't read data from older version */
        
    /* [7] Now add user defined ordered condition list. Make a new OCL using the name 
     * and then save the param (NV) data.
     */
    Condition cList[]= new Condition[oclSize];
    
    for(int i=0;i<oclSize;i++) 
    { /* Stuff the data */
       String cName= getStrValFromLine("ocl-"+i, parser);
       Condition cond= Condition.getConditionByName(cName);
       cList[i]= cond;
    } /* Stuff the data */
    
    /* [8] Save data in Condition OCL database */
    Condition.orderedCondList[oclNbr]= cList;
    Condition.orderedCondListName[oclNbr]= oclName;
    Condition.nOrderedCondList[oclNbr]= oclSize;
   
    Condition.maxOrderedCondLists++; /* max # of ordered condition lists
                                      * that have been created. */
    
    /* [NOTE] The following has been added as of VERSION 1.2 11-9-2002 */
    
    /* [9] If present, read the list of Condition.oclParamNames[0:Condition.oclNparams-1] and
     * Condition.oclParamValues[0:Condition.oclNparams-1] parameters associated with the
     * condition.
     */  
    if((oclNparams= getIntValFromLine("oclNparams",parser))!=0) 
    { /* read the OCL params list if present */
      String
        paramName,
        paramValue;
      Condition.oclNparams= oclNparams;                  /* set the state */
      Condition.oclParamNames= new String[oclNparams];  /* make sure large enough */
      for(int i=0;i<oclNparams;i++) 
      { /* get  the OCL parameter names across all conditions */
        paramName= getStrValFromLine("oclParamName-"+i,parser);
        if(paramName!=null)
          Condition.oclParamNames[i]= paramName;
      } /* get  the OCL parameter names across all conditions */
      
      Condition.oclParamValues[oclNbr]= new String[oclNparams]; /* realloc values array*/
      for(int i=0;i<oclNparams;i++) 
      { /* get  OCL parameter values specific for THIS OCL  */
        paramValue= getStrValFromLine("oclParamValue-"+i,parser);
        if(paramValue!=null)
          Condition.oclParamValues[oclNbr][i]= paramValue;
      } /* get  OCL parameter values specific for THIS OCL  */
    } /* read the OCL params list if present */
     
    Util.showMsg("Reloaded OCL #"+oclNbr+"["+oclName+"]");
    
    return(oclListNbr);
  } /* readUserOCLlist */
  
  
  /**
   * restoreGeneSets() - read user gene bit sets from stand-alone
   * file. If the data exists, then overide the Gene Class subsets
   * set up by default by the GeneClass constructor.
   * If the gene set exists already, wipe out
   * the old one. Note: the gene sets are kept in the State/
   * subdirectory.
   * @return number of sets restored.
   * @see #getMAEstartupValue
   * @see #readUserGeneSet
   */
  int restoreGeneSets()
  { /* restoreGeneSets */
    int nRestored= 0;
    
    for(int i=mae.gct.nFixedGC;i<=GeneBitSet.MAX_USER_BS;i++)
    { /* look for CBS gene set file names */
      String cbsFileName= getMAEstartupValue("CBSfileName-"+i);
      if(cbsFileName!=null)
      {
        readUserGeneSet(cbsFileName, null /* optMAEpath*/);
        nRestored++;
      }
    }
    
    return(nRestored);
  } /* restoreGeneSets */
  
  
  /**
   * restoreCondLists() - read user HP condition lists from stand-alone
   * file. If the data exists, then overide the named HP lists
   * set up by default by the init().
   * If the list exists already, wipe out the old one.
   * Note: the HP list files are kept in the State/ subdirectory.
   * @return number of lists restored.
   * @see #getMAEstartupValue
   * @see #readUserHPlist
   */
  int restoreCondLists()
  { /* restoreCondLists */
    int nRestored= 0;
    for(int i=0;i<Condition.MAX_COND_LISTS;i++)
    { /* look for HP cond list set file names */
      String hplFileName= getMAEstartupValue("HPLfileName-"+i);
      if(hplFileName!=null)
      {
        readUserHPlist(hplFileName, null /* optMAEpath*/);
        nRestored++;
      }
    } /* look for HP cond list set file names */
    
    /* read OCL */  
    for(int i=0; i<Condition.MAX_ORDERED_COND_LISTS; i++)
    { /* look for ocl, set file names */
      String oclFileName= getMAEstartupValue("OCLfileName-"+i);
      if(oclFileName!=null)
      {
        readUserOCLlist(oclFileName, null /* optMAEpath*/);
        nRestored++;
      }
    } /* look for ocl, set file names */
    
    return(nRestored);
  } /* restoreCondLists */
  
  
  /**
   * saveStateFile() - save the user state in a file. [FUTURE]
   * @param loginName is the login Name
   * @param password is the user password
   * @param stateName is the state name
   * @return true if login is valid
   * @see FileIO#writeData
   * @see Util#showMsg
   * @see #saveUserGeneSetUPW
   */
  boolean saveStateFile(String loginName, String password, String stateName)
  { /* saveStateFile */
    boolean flag= false;   
    
    check_dbID();                   /* make sure mae.dbID exists */
    String
      fileName= loginName+"-"+ stateName + ".txt",
      saveURL= mae.maeServerBase +
               "cgi-bin/maeSaveFile?uN="+loginName+
               "&uPW="+password+
               "fileName="+fileName+
               "&stateName="+stateName,
      fgrcData= "",
      hdr=  "login=" + loginName + "\n" +
            "stateName=" + stateName + "\n" +
            "fileName=" + fileName + "\n" +
            "CurSampleName=" + mae.ms.hpName + "\n" +
            "maxGenes=" + mae.mp.maxGenes + "\n" +
            "maxSpots=" + mae.mp.maxSpots + "\n";
    
    /* Note "CurSampleName" was "CurHybSampleName"  but never read back...
     * [DEPRICATION-COMPATIBILITY PROBLEM] */
    
    /* Save Gene sets as separate files. */
    for(int i=1;i<=GeneBitSet.maxUserBS;i++)
    { /* only save sets which exist - no placeholders */
      if(GeneBitSet.userBS[i]!=null)
      {
        flag= saveUserGeneSetUPW(loginName, password, i);
        if(!flag)
          return(false);
      }
    }
    
    /* Save the rest of the state */
    int nBytesEstimate= 50000+hdr.length()+20;
    StringBuffer sBuf= new StringBuffer(60000);  /* est. at 55K */
    sBuf.append(hdr);
    String data= new String(sBuf);               /* data length is about 55K */
    
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("US-SHPSF: data.length=" + data.length());
     */
    
    if(!fio.writeData(fileName,"Saving HP State:", data))
    {
      Util.showMsg("Can't save User-State.");
      Util.popupAlertMsg("Can't User-State",
                         "Can't User-State ["+fileName+"]",
                         4, 60);
      return(false);
    }
    
    return(true);
  } /* saveStateFile */
  
  
  /**
   * getNextStrNameValFromLine() - get next line and parse 'name=value\n'.
   * Return false if EOF. If fail, return null.
   * The (parseName, parseValue, parseDelimChar) is saved
   * in the global instance.
   * @param parser is the string tokenizer
   * @return true succeed
   */
  boolean getNextStrNameValFromLine(StringTokenizer parser)
  { /* getNextStrNameValFromLine */
    parseName= parser.nextToken("=");
    if(parseName==null || parseName.length()==0)
      return(false);          /* no more data */
    int chName= (int)parseName.charAt(0);
    
    if(chName=='\r' || chName=='\n')
      parseName= parseName.substring(1);
    if(parseName.length()==0)
      return(false);          /* no more data */
    
    parseValue= parser.nextToken("\n");
    parseDelimChar= (int)parseValue.charAt(0);
    parseValue= (parseDelimChar!='=') ? parseValue : parseValue.substring(1);
    return(true);
  } /* getNextStrNameValFromLine */
  
  
  /**
   * getStrValFromLine() - get line and parse value for key 'name=value\n'.
   * If parser is not null, get data from parser else use global values
   * for next (parseName,parseValue) data.
   * The (parseName, parseValue, parseDelimChar) is saved in the
   * global instance so it can be reparsed if need be.
   * @param key is the key to use
   * @param parser is the string tokenizer
   * @return parseValue. If fail, return null.
   * @see #getNextStrNameValFromLine
   */
  String getStrValFromLine(String key, StringTokenizer parser)
  { /* getStrValFromLine */
    /* If parsing, get the (parseName, parseValue, parseDelimChar)
     * data.
     */
    if(parser!=null && !getNextStrNameValFromLine(parser))
      return(null);
    
    if(parseName==null || !parseName.equals(key))
    {
      /* mae.logDRYROTerr("[US-GSVFL] - key='" + key +
                          "', parseName='" + parseName +
                          "', parseValue='" + parseValue + "'" +
                          "', parseDelimChar='" + parseDelimChar + "'");
       */
      return(null);
    }
    
    return(parseValue);
  } /* getStrValFromLine */
  
  
  /**
   * getStrValFromLine() - parse value for key 'name=value\n'.
   * Use global values for next (parseName,parseValue) data.
   * The (parseName, parseValue, parseDelimChar) was saved in the
   * global instance so it can be reparsed if need be.
   * @param key is the key to use
   * @return parseValue. If fail, return null.
   */
  String getStrValFromLine(String key)
  { /* getStrValFromLine */
    if(parseName==null || !parseName.equals(key))
    {
      /* mae.logDRYROTerr("[US-GSVFL] - key='" + key +
                          "', parseName='" + parseName +
                          "', parseValue='" + parseValue + "'" +
                          "', parseDelimChar='" + parseDelimChar + "'");
       */
      return(null);
    }
    
    return(parseValue);
  } /* getStrValFromLine */
  
  
  /**
   * getIntValFromLine() - get next line and parse value for key 'name='.
   * @param key is the key to use
   * @param parser is the string tokenizer
   * @return int value, else 0.
   * @see #getStrValFromLine
   */
  int getIntValFromLine(String key, StringTokenizer parser)
  { /* getIntValFromLine */
    String sVal= getStrValFromLine(key,parser);    
    if(sVal==null)
      return(0);    
    int iVal= java.lang.Integer.parseInt(sVal); /* ignore '=' */
    return(iVal);
  } /* getIntValFromLine */
  
  
  /**
   * getLongValFromLine() - get next line and parse value for key 'name='.
   * @param key is the key to use
   * @param parser is the string tokenizer
   * @return long value, else 0.
   * @see #getStrValFromLine
   */
  long getLongValFromLine(String key, StringTokenizer parser)
  { /* getLongValFromLine */
    String sVal= getStrValFromLine(key,parser);    
    if(sVal==null)
      return(0);    
    Long valL= new Long(sVal);
    long longVal= valL.longValue();    
    return(longVal);
  } /* getLongValFromLine */
  
  
  /**
   * getFloatValFromLine() - get next line and parse value for key 'name='.
   * @param key is the key to use
   * @param parser is the string tokenizer
   * @return float value.
   * @see #getStrValFromLine
   */
  float getFloatValFromLine(String key, StringTokenizer parser)
  { /* getFloatValFromLine */
    String sVal= getStrValFromLine(key,parser);    
    if(sVal==null)
      return(0);    
    Float valF= new Float(sVal);
    float rVal= valF.floatValue();    
    return(rVal);
  } /* getFloatValFromLine */
  
  
  /**
   * getNextInt() - parse next int from StringTokenizer
   * the integers are separated by ',' or '\n'.
   * Return int.
   */
  int getNextInt(StringTokenizer parser)
  { /* getNextInt */
    String sVal= parser.nextToken("\n");
    int rVal= java.lang.Integer.parseInt(sVal);
    return(rVal);
  } /* getNextInt */
  
  
  /**
   * restoreUserGeneSetUPW() - restore user gene bit set by set nbr [FUTURE]
   * @param loginName is the login Name
   * @param password is the user password
   * @param userCBSnbr is the gene bit set name
   * @return true if succeed
   */
  static boolean restoreUserGeneSetUPW(String userName, String password,
                                       int userCBSnbr)
  { /* restoreUserGeneSetUPW */
    return(true);
  } /* restoreUserGeneSetUPW */
  
  
  
  /**
   * restoreUserGeneSet() - restore user gene bit set by set number [FUTURE]
   * @param userCBSnbr is the gene bit set name
   * @return true if succeed
   */
  static boolean restoreUserGeneSet(int userCBSnbr)
  { /* restoreUserGeneSet */
    return(true);
  } /* restoreUserGeneSet */
  
  
  /**
   * readStateFile() - read the protected user State File
   * [FUTURE] see writeStateFile() to see what have to read.
   * @param loginName is the login Name
   * @param password is the user password
   * @param stateName is the state name
   * @return true if succeed
   * @see FileIO#readData
   * @see Util#showMsg
   * @see #getIntValFromLine
   * @see #getStrValFromLine
   * @see #restoreUserGeneSetUPW
   */
  boolean readStateFile(String userName, String password, String stateName)
  { /* readStateFile */
    boolean 
      flag= false,
      parsedStateFlag= false;
    
    String
      stateStr= null,
      fileName= userName+"-"+ stateName + ".txt",
      readURL= mae.maeServerBase +
               "cgi-bin/maeReadFile?uN="+userName+
               "&uPW="+password+
               "&fileName="+fileName+
               "&stateName="+stateName;
    int nXYATB= 0;
    
    /*
    if(mae.CONSOLE_FLAG)
      fio.logMsgln("US-RSF stateStr=" + stateStr);
     */
    
    /* Read State file */
    stateStr= fio.readData(fileName,"Loading state["+stateName+"]");
    if(stateStr==null)
    {
      Util.showMsg("Can't load state["+stateName+"]");
      Util.popupAlertMsg("Can't load state",
                         "Can't load state["+stateName+"]",
                         4, 60);
      return(false);
    }
    StringTokenizer parser= new StringTokenizer(stateStr, "=,\n", false);
    
    /* Parse State string and set flag to restore state  */
    userName= getStrValFromLine("userName", parser);
    mae.ms.hpName= getStrValFromLine("hpName",parser);
    int
      maxGenes= getIntValFromLine("maxGenes",parser),   /* local copy */
      maxSpots= getIntValFromLine("maxSpots",parser);   /* local copy */
    
    /* Restore state */
    for(int i=1; i<=GeneBitSet.maxUserBS;i++)
      if(GeneBitSet.userBS[i]!=null)
        restoreUserGeneSetUPW(userName,password,i);
    
    return(parsedStateFlag);
  } /* readStateFile */
  
  
  /**
   * getMAEstartupValue() - get value from MAE startup file DB
   * @param name is name of the value to lookup
   * @return true if succeed
   */
  String getMAEstartupValue(String name)
  { /* getMAEstartupValue */
    if(msuTbl==null || name==null)
      return(null);
    
    String
      value,
      data[];
    int tRows= msuTbl.tRows;
    for(int r=0;r<tRows;r++)
    { /* read name, value pairs */
      data= msuTbl.tData[r];
      if(name.equals(data[idxName]))
      {
        value= data[idxValue];
        return(value);
      }
    }
    
    return(null);
  } /* getMAEstartupValue */
  
  
  /**
   * readMAEstartupFile() - read the .mae startup database File.
   * Keep the table while do startup parsing. Could delete it later.
   * @param maeDirName is the project directory
   * @param maeStartupFile is .mae startup file
   * @return true if successful.
   */
  boolean readMAEstartupFile(String maeDirName, String maeStartupFile)
  { /* readMAEstartupFile*/
    /* [1] Get the startup table from the file */
    String fileName= maeDirName+maeStartupFile; /* fix up full pathname */
    
    msuTbl= new Table(mae, fileName,"Startup DB");
    
    if(msuTbl==null || msuTbl.tRows==0)
    { /* can't find startup file */
      if(!msuTbl.fileOKflag)
        System.out.println("Can't find MAExplorer startup file ["+fileName+"]");
      else
        System.out.println("MAExplorer startup file ["+ fileName +
                           "] Table is empty");
      return(false);
    }
    else
      msuTbl.trimWhitespace(); /* remove leading & trailing whitespace */
    
    /* [2] Get the name, value Table indices */
    idxName= msuTbl.lookupFieldIdx("Name");
    idxValue= msuTbl.lookupFieldIdx("Value");
    if(idxName==-1 || idxValue==-1)
    {
      System.out.println("Illegal MAExplorer startup file ["+
                         maeStartupFile+"]"+
                         "\nBad indices: idxName="+idxName+
                         " idxValue="+idxValue);
      msuTbl= null;
      return(false);
    }
    
    /* [3] Special setups to get around "chicken and the egg" problem
     * of which comes first.
     */
    for(int i=0;i<msuTbl.tRows;i++)
    { /* search .mae startup file for special names */
      String
        rowData[]= msuTbl.tData[i],
        name= rowData[idxName],
        value= rowData[idxValue];
      if(name==null)
        break;
      
      if(name.equals("configFile"))
      {
        mae.configFile= value;
      }
      else if(name.equals("useWebDB"))
      {
        mae.useWebDBflag= (value.equals("TRUE") ||
        value.equals("true"));
      }
      else if(name.equals("saCodeBase") && value.length()>0)
      { /* use different CodeBase */
        mae.saCodeBase= value;
      }
      else if(rowData[idxName].equals("enableFIOcaching"))
      { /* use different CodeBase */
        mae.enableFIOcachingFlag= (value.equals("TRUE") ||
                                   value.equals("true"));
      }
    } /* search .mae startup file for special names */
    
    /* Post processing */
    mae.chkIfCache();   /*  check if set up to cache from Web DB. */
    
    return(true);
  } /* readMAEstartupFile */
  
  
  /**
   * createMAEstartupFileStr() - create .mae startup DB File string.
   * If projects are enabled, then also update the maeProjects.txt
   * current project status file. It saves the current HP-X, HP-Y sets
   * and HP-E lists as well as loading all samples even in they are not
   * in any of those lists.
   * @param saveAllsamplesFlag save all samples, else just save the samples
   *        in the HP-X, HP-Y 'sets' and HP-E 'list'
   * @return true if successful.
   * @see Util#cvtSpacesToUnderscores
   */
  String createMAEstartupFileStr(boolean saveAllsamplesFlag)
  { /* createMAEstartupFileStr */
    Config cfg= mae.cfg;
    SampleSets hps= mae.hps;
    MaHybridSample
      ms,
      maL[]= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    int
      n= 0,
      j,
      k;
    boolean
      foundIt,
      foundHPX,
      foundHPY;
    StringBuffer sBuf= new StringBuffer(20000);  /* make it more reasonable later*/
    
    /* [1] create file contents */   
    String
      sR,
      sXlist= "\nXlist\t",
      sYlist= "\nYlist\t",
      sElist= "\nElist\t";    
    
    /* [1.1] Get Unique Database ID ("DIDxxxxxx") - used as prefix for State/ files.
     * If it does not exist, then create it when save the database.
     * The param is "dbID".
    */ 
     check_dbID();                   /* make sure mae.dbID exists */
    
    /* [2] Build unique list of all samples in X, Y and E lists.
     * Handle named HP sets and lists in a similar way
     * and save as lists of #s indices...
     */
    if(hps!=null)
    { /* save the Lists if HPS instance exists */
      for(k= 1;k<=hps.nHP_X;k++)
      {
        foundIt= false;
        ms= hps.msListX[k];
        if(ms==null)
          continue;
        for(j=1; j<=n;j++)
          if(ms==maL[j])
          {
            foundIt= true;
            break;
          }
        if(!foundIt)
          maL[++n]= ms;       /* push it */
        sXlist += (""+j);     /* make list of samples */
        if(k<hps.nHP_X)
          sXlist += ",";
      }
      
      for(k= 1;k<=hps.nHP_Y;k++)
      {
        foundIt= false;
        ms= hps.msListY[k];
        if(ms==null)
          continue;
        for(j=1; j<=n;j++)
          if(ms==maL[j])
          {
            foundIt= true;
            break;
          }
        if(!foundIt)
          maL[++n]= ms;       /* push it */
        sYlist += (""+j);     /* make list of samples */
        if(k<hps.nHP_Y)
          sYlist += ",";
      }
      
      for(k= 1;k<=hps.nHP_E;k++)
      {
        foundIt= false;
        ms= hps.msListE[k];
        if(ms==null)
          continue;
        for(j=1; j<=n;j++)
          if(ms==maL[j])
          {
            foundIt= true;
            break;
          }
        if(!foundIt)
          maL[++n]= ms;       /* push it */
        sElist += (""+j);     /* make list of samples */
        if(k<hps.nHP_E)
          sElist += ",";
      }
      
      /* [2.1] Add msX and msY if not in list */
      foundHPX= false;
      foundHPY= false;
      for(j=1; j<=n;j++)
      {
        if(mae.msX!=null && mae.msX==maL[j])
          foundHPX= true;
        if(mae.msX!=null && mae.msY==maL[j])
          foundHPY= true;
      }
      if(!foundHPX && mae.msX!=null)
        maL[++n]= mae.msX; /* push it */
      if(!foundHPY && mae.msY!=null)
        maL[++n]= mae.msY; /* push it */      
    } /* save the Lists if HPS instance exists */
    
    /* [3] Build output file string */
    sBuf.append("Name\tValue");    /* Write the table field names */
    
    /* [3.1] Write out data (name,value) pairs */
    sBuf.append("\nconfigFile\t" + mae.configFile);
    sBuf.append("\ndatabase\t"+ cfg.database);
    sBuf.append("\ndbSubset\t"+ cfg.dbSubset);
    sBuf.append("\nDate\t" + Util.dateStr());
    sBuf.append("\ndbID\t" + mae.dbID);
    sBuf.append("\nuserName\t" + mae.userName);
    sBuf.append("\nmaxPreloadImages\t" + n);
    
    /* [3.1.1] Save the HP samples */
    if(!saveAllsamplesFlag)
    { /* save all samples - else just ones in HP-X, HP-Y and HP-E lists*/
      n= hps.nHP;
      maL= hps.msList;
    }
    for(k= 1;k<=n;k++)
      sBuf.append(("\nimage" + k) + "\t" + maL[k].hpName);
    
    /* [3.2] Save the HP msList[1:nHP].swapCy5Cy3DataFlag status */
    if(hps!=null)
      for(int i=1;i<=hps.nHP;i++)
      { /* Save the swapCy5Cy3 status */
        boolean flag= hps.msList[i].swapCy5Cy3DataFlag;
        String sFlag= (flag) ? "TRUE" : "FALSE";
        sBuf.append("\nHPcy53Flag-"+i+"\t" + sFlag);
      }
    
    /* [3.3] Save the startup HP-X,-Y,-E lists */
    sBuf.append(sXlist);
    sBuf.append(sYlist);
    sBuf.append(sElist);
    sBuf.append("\nRatioHP\t" + cfg.RatioHP);
    sBuf.append("\ncurCondIdx\t" + Condition.curCondIdx);
    sBuf.append("\ncurOCLidx\t" + Condition.curOCLidx); 
    
    sBuf.append("\nclassNameX\t" + mae.classNameX);
    sBuf.append("\nclassNameY\t" + mae.classNameY);
    sBuf.append("\nfontFamily\t" + cfg.fontFamily);
    
    sBuf.append("\nuseRatioData\t" + mae.useRatioDataFlag);
    sBuf.append("\nuseCy5/Cy3\t" +  mae.useCy5OverCy3Flag);
    sBuf.append("\nfluorescentLbl1\t" + cfg.fluoresLbl1);
    sBuf.append("\nfluorescentLbl2\t" + cfg.fluoresLbl2);
    
    /* [3.4] Save array configuration substate */
    sBuf.append("\nnoMsgReporting\t" + mae.noMsgReportFlag);
    sBuf.append("\nswapRowsColumns\t" + mae.swapRowsColsFlag);
    sBuf.append("\nreuseXYcoords\t" + mae.reuseXYcoordsFlag);
    sBuf.append("\nusePseudoXYcoords\t" + mae.usePseudoXYcoordsFlag);
    
    /* [3.5] Save Special caching params */
    sBuf.append("\nuseWebDB\t" + mae.useWebDBflag);
    sBuf.append("\nsaCodeBase\t" +
                ((mae.saCodeBase!=null) ? mae.saCodeBase : ""));
    sBuf.append("\nenableFIOcaching\t" + mae.enableFIOcachingFlag);
    
    /* [3.6] Save current thresholds */
    sBuf.append("\nSpotRadius\t" + mae.spotRad);
    sBuf.append("\nnbrOfClustersThr\t" + cfg.nbrOfClustersThr);
    sBuf.append("\ndiffThr\t" + cfg.diffThr);
    sBuf.append("\npctOKthr\t" + cfg.pctOKthr);
    sBuf.append("\nclusterDistThr\t" + cfg.clusterDistThr);
    sBuf.append("\nspotCVthr\t" + cfg.spotCVthr);
    sBuf.append("\npValueThr\t" + cfg.pValueThr);
    sBuf.append("\nqualThr\t" + cfg.qualThr);
    sBuf.append("\ndetValueSpotThr\t" + cfg.detValueSpotThr);
    
    sBuf.append("\nlowRangeScaleFactor\t" + cfg.lowRangeScaleFactor);
    
    /* [3.7] Save slider range thresholds */
    sBuf.append("\nSI1\t" + mae.sit1);
    sBuf.append("\nSI2\t" + mae.sit2);
    sBuf.append("\nI1\t" + mae.t1);
    sBuf.append("\nI2\t" + mae.t2);
    sBuf.append("\nR1\t" + mae.r1);
    sBuf.append("\nR2\t" + mae.r2);
    sBuf.append("\nCR1\t" + mae.cr1);
    sBuf.append("\nCR2\t" + mae.cr2);
    
    /* [3.8] Save canvas and other sizes */
    sBuf.append("\nCanvasHorSize\t" + mae.canvasHSize);
    sBuf.append("\nCanvasVertSize\t" + mae.canvasVSize);
    
    /* [3.9] Save additional View state switches */
    sBuf.append("\nallowNegQuantDataFlag\t" + mae.allowNegQuantDataFlag);
    sBuf.append("\nusePosQuantDataFlag\t"+ mae.usePosQuantDataFlag);
    sBuf.append("\nposQuantTestMode\t" + mae.posQuantTestMode);
    sBuf.append("\nuseMouseOver\t" + mae.useMouseOverFlag);
    sBuf.append("\nuseDichromasy\t" + mae.useDichromasyFlag);
    sBuf.append("\npresentationViewFlag\t" + mae.presentViewFlag);
    sBuf.append("\nuseSPLUSasComputingEngineFlag\t" + 
                mae.useSPLUSasComputingEngineFlag); 
    
    sBuf.append("\nuseRLOloggingFlag\t" + mae.useRLOloggingFlag);    
    sBuf.append("\nmaxGenesReported\t" + cfg.maxGenesToRpt);
    
    /* [3.10] Save additional View substate */
    sBuf.append("\nshowEGLflag\t" + mae.showEGLflag);
    sBuf.append("\ngenBankViewerFlag\t" + mae.genBankViewerFlag);
    sBuf.append("\ndbESTviewerFlag\t" + mae.dbESTviewerFlag);
    sBuf.append("\nuniGeneViewerFlag\t" + mae.uniGeneViewerFlag);
    sBuf.append("\nomimViewerFlag\t" + mae.omimViewerFlag);
    sBuf.append("\nmAdbViewerFlag\t" + mae.mAdbViewerFlag);
    sBuf.append("\nlocusLinkViewerFlag\t" + mae.locusLinkViewerFlag);
    sBuf.append("\nmedMinerViewerFlag\t" + mae.medMinerViewerFlag);
    sBuf.append("\nswissProtViewerFlag\t" + mae.swissProtViewerFlag);
    sBuf.append("\npirViewerFlag\t" + mae.pirViewerFlag);
    sBuf.append("\nviewFilteredSpotsFlag\t" + mae.viewFilteredSpotsFlag);
    sBuf.append("\ngangSpotFlag\t" + mae.gangSpotFlag);
    sBuf.append("\nflickerXYflag\t" + mae.flickerXYflag);
    
    sBuf.append("\ngeoPlatformID\t" + mae.cfg.geoPlatformID);
    
    /* [3.11] Save active Filters substate */
    sBuf.append("\ncurGeneClassName\t"+ GeneClass.curGeneClassName);
    sBuf.append("\ngeneClassMbrFilterFlag\t" + mae.geneClassMbrFilterFlag);
    sBuf.append("\nuseGeneSetFilterFlag\t" + mae.useGeneSetFilterFlag);
    sBuf.append("\nuseEditedCLflag\t" + mae.useEditedCLflag);
    sBuf.append("\nuseGoodGeneCLflag\t" + mae.useGoodGeneCLflag);
    sBuf.append("\nuseReplicateGenesFlag\t" + mae.useReplicateGenesFlag);
    sBuf.append("\nuseRatioHistCLflag\t" + mae.useRatioHistCLflag);
    sBuf.append("\nuseIntensHistCLflag\t" + mae.useIntensHistCLflag);
    
    sBuf.append("\nspotIntensFilterFlag\t" + mae.spotIntensFilterFlag);
    sBuf.append("\nspotIntensRangeMode\t" +mae.spotIntensRangeMode);
    sBuf.append("\nspotIntensTestMode\t" + mae.spotIntensTestMode);
    sBuf.append("\nspotIntensCompareMode\t" + mae.spotIntensCompareMode);
    
    sBuf.append("\nintensFilterFlag\t" + mae.intensFilterFlag);
    sBuf.append("\nsampleIntensityRangeMode\t" + mae.sampleIntensityRangeMode);
    
    sBuf.append("\nratioFilterFlag\t" + mae.ratioFilterFlag);
    sBuf.append("\nratioRangeMode\t" + mae.ratioRangeMode);
    
    sBuf.append("\nratioCy3Cy5FilterFlag\t"+mae.ratioCy3Cy5FilterFlag);
    sBuf.append("\ratioCy3Cy5RangeMode\t" + mae.ratioCy3Cy5RangeMode);
    
    sBuf.append("\nuseSpotCVfilterFlag\t" + mae.useSpotCVfilterFlag);
    sBuf.append("\ncvTestMode\t" + mae.cvTestMode);
    
    sBuf.append("\nuseCVmeanElseMaxFlag\t" + mae.useCVmeanElseMaxFlag);
    sBuf.append("\ntTestXYfilterFlag\t" + mae.tTestXYfilterFlag);
    sBuf.append("\ntTestXYsetsFilterFlag\t" + mae.tTestXYsetsFilterFlag);
    sBuf.append("\nKS_TestXYsetsFilterFlag\t" + mae.KS_TestXYsetsFilterFlag);
    sBuf.append("\nF_TestOCLFilterFlag\t" + mae.F_TestOCLFilterFlag);
    sBuf.append("\nclusterHP_EfilterFlag\t" + mae.clusterHP_EfilterFlag);
    sBuf.append("\nuseDiffFilterFlag\t" + mae.useDiffFilterFlag);
    sBuf.append("\nuseHighRatiosFilterFlag\t" + mae.useHighRatiosFilterFlag);
    sBuf.append("\nuseLowRatiosFilterFlag\t" + mae.useLowRatiosFilterFlag);
    
    sBuf.append("\nuseGoodSpotDataFlag\t" + mae.useGoodSpotDataFlag);
    sBuf.append("\ngoodSpotTestMode\t" + mae.goodSpotTestMode);
    sBuf.append("\nqualTypeMode\t" + mae.qualTypeMode);
    sBuf.append("\nuseGoodSpotsForGlobalStatsFlag\t" + mae.useGoodSpotsForGlobalStatsFlag);
    
    sBuf.append("\nuseDetValueSpotDataFlag\t" + mae.useDetValueSpotDataFlag);
    sBuf.append("\nuseOnlyGenesWithNonZeroDensityFlag\t" + mae.useOnlyGenesWithNonZeroDensityFlag);
    sBuf.append("\ndetValueSpotTestMode\t" + mae.detValueSpotTestMode);
    
    /* [3.12] Save Plot substate  */
    sBuf.append("\nplotImageMode\t" + mae.plotImageMode);
    sBuf.append("\ndualXYpseudoPlotFlag\t" + mae.dualXYpseudoPlotFlag);
    sBuf.append("\nuseEPoverlayFlag\t" + mae.useEPoverlayFlag);
    
    /* [3.13] Save Clustering substate */
    sBuf.append("\nhierClustMode\t" + mae.hierClustMode);
    sBuf.append("\nhierClustUnWtAvgFlag\t" + mae.hierClustUnWtAvgFlag);
    sBuf.append("\nuseClusterDistFlag\t" + mae.useClusterDistFlag);
    sBuf.append("\nuseSimGeneClusterDispFlag\t" + mae.useSimGeneClusterDispFlag);
    sBuf.append("\nuseClusterCountsDispFlag\t" + mae.useClusterCountsDispFlag);
    sBuf.append("\nuseHierClusterDispFlag\t" + mae.useHierClusterDispFlag);
    sBuf.append("\nuseKmeansClusterCntsDispFlag\t" + mae.useKmeansClusterCntsDispFlag);
    sBuf.append("\nuseLSQmagNormFlag\t" + mae.useLSQmagNormFlag);
    sBuf.append("\nnormHCbyRatioHPflag\t" + mae.normHCbyRatioHPflag);
    sBuf.append("\nuseClusterDistCacheFlag\t"+mae.useClusterDistCacheFlag);
    sBuf.append("\nuseShortClusterDistCacheFlag\t" + mae.useShortClusterDistCacheFlag);
    sBuf.append("\nuseMedianForKmeansClusteringFlag\t"+ mae.useMedianForKmeansClusteringFlag);
    
    /* [3.14] Save Table substate */
    sBuf.append("\ntblFmtMode\t" + mae.tblFmtMode);
    sBuf.append("\nrptFontSize\t" + mae.rptFontSize);
    sBuf.append("\naddExprProfileFlag\t" + mae.addExprProfileFlag);
    sBuf.append("\nuseEPrawIntensValFlag\t" + mae.useEPrawIntensValFlag);
    sBuf.append("\naddHP_XYstatFlag\t" + mae.addHP_XYstatFlag);
    sBuf.append("\naddOCLstatFlag\t" + mae.addOCLstatFlag);    
    
    /* [3.15] Save Preferences substate */
    sBuf.append("\nautoStateScrPopupFlag\t" +mae.autoStateScrPopupFlag);
    sBuf.append("\nclusterOnFilteredCLflag\t" +mae.clusterOnFilteredCLflag);
    sBuf.append("\nfontFamily\t" + mae.cfg.fontFamily);
    
    /* [3.16] Save Edit substate */
    sBuf.append("\neditMode\t" + mae.editMode);
    
    /* [3.17] Save Normalization substate */
    sBuf.append("\nnormName\t" + mae.normName);
    sBuf.append("\nisZscoreFlag\t" + mae.isZscoreFlag);
    sBuf.append("\nnormByZscoreFlag\t" + mae.normByZscoreFlag);
    sBuf.append("\nnormByMedianFlag\t" + mae.normByMedianFlag);
    sBuf.append("\nnormByLogMedianFlag\t" + mae.normByLogMedianFlag);
    sBuf.append("\nnormByZscoreMeanStdDevLogFlag\t" +
                mae.normByZscoreMeanStdDevLogFlag);
    sBuf.append("\nnormByZscoreMeanAbsDevLogFlag\t" +
                mae.normByZscoreMeanAbsDevLogFlag);
    sBuf.append("\nnormByCalDNAflag\t" + mae.normByCalDNAflag);
    sBuf.append("\nnormByGeneSetFlag\t" + mae.normByGeneSetFlag);
    sBuf.append("\nnormByHousekeepGenesFlag\t" + mae.normByHousekeepGenesFlag);
    sBuf.append("\nscaleDataToMaxIntensFlag\t" + mae.scaleDataToMaxIntensFlag);
    sBuf.append("\nuseRatioMedianCorrection\t" + mae.ratioMedianCorrectionFlag);
    sBuf.append("\nuseBackgroundCorrection\t" + mae.bkgdCorrectFlag);
    
    /* [3.18] Save [i.e. SAMPLE] HybSample substate
     * [DEPRICATION-COMPATIBILITY PROBLEM]
     */
    sBuf.append("\nuseHPxySetDataFlag\t" + mae.useHPxySetDataFlag);
    sBuf.append("\nuseMeanHPeListDataFlag\t" + mae.useMeanHPeListDataFlag);    
    
    /* [3.19] Save optional Genomic Viewer data lists if defined */
    if(mae.genomicViewerFlag!=null)
    { /* Only save if genomic Viewer data if list exists */
      int nGenomicViewers= mae.genomicViewerFlag.length;
      for(int i=1;i<=nGenomicViewers;i++)
      {
        sBuf.append("\ngenomicViewerFlag-"+i+"\t" + mae.genomicViewerFlag[i]);
        if(cfg.sGenomicMenu!=null)
          sBuf.append("\nGenomicMenu-"+i+"\t" + cfg.sGenomicMenu[i]);
        if(cfg.sGenomicURL!=null)
          sBuf.append("\nGenomicURL-"+i+"\t" + cfg.sGenomicURL[i]);
        if(cfg.sGenomicURLepilogue!=null)
          sBuf.append("\nGenomicURLepilogue-"+i+"\t" + cfg.sGenomicURLepilogue[i]);
        if(cfg.sGenomicIDreq!=null)
          sBuf.append("\nsGenomicIDreq-"+i+"\t" + cfg.sGenomicIDreq[i]);
      }
    } /* Only save if genomic Viewer data if list exists */
    
    /* [3.20] Save optional MAEPlugins lists if defined */
    if(cfg.sPluginMenuName!=null)
    { /* Only save if MAEPlugins lists exist */
      for(int i=1;i<=cfg.nPluginMenus;i++)
      {
        if(cfg.sPluginMenuName!=null)
          sBuf.append("\nPluginMenuName-"+i+"\t" + cfg.sPluginMenuName[i]);
        if(cfg.sPluginMenuStubName!=null)
          sBuf.append("\nPluginMenuStubName-"+i+"\t" + cfg.sPluginMenuStubName[i]);
        if(cfg.sPluginMenuStubName!=null)
          sBuf.append("\nPluginMenuStubName-"+i+"\t" + cfg.sPluginMenuStubName[i]);
        if(cfg.sPluginCallAtStartup!=null)
          sBuf.append("\nPluginCallAtStartup-"+i+"\t" + cfg.sPluginCallAtStartup[i]);
      }
    } /* Only save if MAEPlugins lists exist */
    
    /* [4] Save additional sets of genes and conditions save sets
     * and lists in State/
     */
    /* [4.1] Save Gene Bit Sets as separate files. */
    if(GeneBitSet.userBS!=null)
    { /* Only save if userBS[] list exists */
      sBuf.append("\nnUserBS\t" + GeneBitSet.nUserBS);
      for(int i=1;i<=GeneBitSet.MAX_USER_BS;i++)
      { /* only save sets which exist - no placeholders */
        GeneBitSet cbs= GeneBitSet.userBS[i];
        if(cbs!=null)
        { /* only save non-null sets */
          String
            bName= cbs.bName,            /* name of bitset */
            bNameNoSpaces= Util.cvtSpacesToUnderscores(bName),
            cbsFileName= bNameNoSpaces + ".cbs";
          
          if(!cbsFileName.startsWith("DID"))
            cbsFileName= mae.dbID + "-" + cbsFileName;
          sBuf.append("\nCBSfileName-"+i+"\t" + cbsFileName);
        }
      } /* only save sets which exist - no placeholders */
    } /* Only save if userBS[] list exists */
    
    /* [4.2] Save HP Sets(of HP-X,HP-Y,HP-E) as separate files. */
    sBuf.append("\nmaxHPcondLists\t" + Condition.nCondLists);
    if(Condition.condList!=null)
      for(int i=0;i<Condition.MAX_COND_LISTS;i++)
      { /* only save sets which exist - no placeholders */
        Condition cond= Condition.condList[i];
        if(cond!=null)
        { /* only save non-null sets */
          String
            cName= cond.cName,         /* name of Condition */
            cNameNoSpaces= Util.cvtSpacesToUnderscores(cName),
            hplFileName= cNameNoSpaces + ".hpl";
          if(!hplFileName.startsWith("DID"))
            hplFileName= mae.dbID + "-" + hplFileName;
          sBuf.append("\nHPLfileName-"+i+"\t" + hplFileName);
        }
      } /* only save sets which exist - no placeholders */
        
    /* [4.3] Save ocl file names.*/
    if(Condition.orderedCondListName!=null)
      for(int i=0; i<Condition.maxOrderedCondLists; i++)
      { 
        String oclName= Condition.orderedCondListName[i];
        if(oclName!=null)
        { /* only save non-null sets */
          String
            oclNameNoSpaces= Util.cvtSpacesToUnderscores(oclName),
            oclFileName= oclNameNoSpaces + ".ocl";
          if(!oclFileName.startsWith("DID"))
            oclFileName= mae.dbID + "-" + oclFileName;
          sBuf.append("\nOCLfileName-"+i+"\t" + oclFileName);
        }
      } 
    
    /* [5] Terminate the StringBuffer and cvt to String */
    sBuf.append("\n");        /* add final CR */
    sR= new String(sBuf);
    sBuf= null;               /* for G.C. */
    
    return(sR);
  } /* createMAEstartupFileStr */
  
  
  /**
   * writeMAEstartupFile() - write the .mae startup DB File.
   * If projects are enabled, then also update the maeProjects.txt
   * current project status file.
   * @param maeDirName is the project directory that contains MAE/ subdirectory
   * @param maeStartupFile is .mae startup file name
   * @param saveAllsamplesFlag save all samples, else just save the samples
   *        in the HP-X, HP-Y 'sets' and HP-E 'list'
   * @return true if successful.
   * @see FileIO#writeFileToDisk
   * @see Util#cvtSpacesToUnderscores
   * @see Util#showMsg
   * @see #checkAndMakeMAEdirTree
   * @see #createMAEstartupFileStr
   * @see #saveNamedHPlist
   * @see #saveUserGeneSet
   * @see #updateMAEprojectDB
   */
  boolean writeMAEstartupFile(String maeDirName, String maeStartupFile,
                              boolean saveAllsamplesFlag)
  { /* writeMAEstartupFile*/
    Config cfg= mae.cfg;
    SampleSets hps= mae.hps;
    MaHybridSample
      ms,
      maL[]= new MaHybridSample[mae.MAX_HYB_SAMPLES+1];
    int
      n= 0,
      j,
      k;
    
    /* [1] create file contents */
    String
      sR,
      fileName;

    if(!maeStartupFile.endsWith(".mae"))
      maeStartupFile += ".mae";
    fileName= maeDirName+maeStartupFile; /* fix up full pathname */
    
    /* [2] Check for MAE prject directory tree. If not found
     * create the directory tree.
     */
    String
      endStr= mae.fileSeparator+"MAE"+mae.fileSeparator,
      maeTreeDir= (maeDirName.toUpperCase().endsWith(endStr))
                     ? maeDirName.substring(0,maeDirName.length()-4)
                     : maeDirName;
    boolean hasTreeFlag= checkAndMakeMAEdirTree(maeTreeDir);
    
    /* [3] Create (Name,Value) contents of .mae file */
    sR= createMAEstartupFileStr(saveAllsamplesFlag);
    
    /* [4] Save additional sets of genes and conditions if tree
     * exists so the State/ exists.
     */
    if(hasTreeFlag)
    { /* save sets and lists in State/ */
      /* [4.1] Save Gene Bit Sets as separate files. */
      for(int i=1;i<=GeneBitSet.MAX_USER_BS;i++)
      { /* only save sets which exist - no placeholders */
        GeneBitSet cbs= GeneBitSet.userBS[i];
        if(cbs!=null)
        { /* only save non-null sets */
          saveUserGeneSet(i);            /* save the gene set i */
        }
      } /* only save sets which exist - no placeholders */
      
      /* [4.2] Save HP Sets(of HP-X,HP-Y,HP-E) as separate files. */
      for(int i=0;i<Condition.MAX_COND_LISTS;i++)
      { /* only save sets which exist - no placeholders */
        Condition cond= Condition.condList[i];
        if(cond!=null)
        { /* only save non-null sets */
          saveNamedHPlist(cond,i);       /* save the named HP list */
        }
      } /* only save sets which exist - no placeholders */
    } /* save sets and lists in State/ */
    
    /*  [4.3] Save ocl */
    for(int i=0; i<Condition.maxOrderedCondLists; i++)
      { 
        String oclName= Condition.orderedCondListName[i];
        if(oclName!=null)
        { /* only save non-null sets */
          saveNamedOCLlist(i);       /* save the ocl */
        }
      }
      
    /* [5] Write the .mae startup file */
    boolean flag= fio.writeFileToDisk(fileName, sR);
    
    if(flag)
      Util.showMsg("Saved startup DB ["+maeStartupFile+"]");
    
    /* [6] Update 'install-dir'/maeProjects.txt DB file. */
    if(mae.defSAprjName.equals("Empty database") &&
       mae.cfg.database!=null && mae.cfg.database.length()>0)
    {
      mae.defSAprjName= mae.cfg.database;
    }
    
    updateMAEprojectDB(mae.defSAprjName, maeDirName, maeStartupFile,
                       mae.defSAwebAddr);
    
    return(flag);
  } /* writeMAEstartupFile */
  
  
  /**
   * checkAndMakeMAEdirTree()- check for MAE prject directory tree.
   * This contains the following subdirectories:
   * <PRE>
   *    Cache  - (opt)
   *    Config - configuration .txt files
   *    Images - (opt) images keyed to XY data in .quant files
   *    MAE    - startup .mae files and SaveAs DB files
   *    Quant  - quantified spot data files, one per sample .quant files
   *    Report - (opt) saved text files .txt and  plot files.gif
   *    State  - (opt) saves the named Gene Sets and named HP sample lists
   *</PRE>
   * If not found, then create the MAE project directory tree.
   * @param maePrjDir is the project directory path
   * @return true if successful.
   */
  boolean checkAndMakeMAEdirTree(String maePrjDir)
  { /* checkAndMakeMAEdirTree */
    try
    {
      File
        f= new File (maePrjDir),
        fCache,
        fConfig,
        fImages,
        fMAE,
        fQuant,
        fReport,
        fState,
        fTemp,
        fTmp;
      
      if(!f.isDirectory())
        f.mkdirs();        /* make it */
      
      String
        sCachePath= maePrjDir + "Cache" + mae.fileSeparator,
        sConfigPath= maePrjDir + "Config" + mae.fileSeparator,
        sImagesPath= maePrjDir + "Images" + mae.fileSeparator,
        sMAEPath= maePrjDir + "MAE" + mae.fileSeparator,
        sQuantPath= maePrjDir + "Quant" + mae.fileSeparator,
        sReportPath= maePrjDir + "Report" + mae.fileSeparator,
        sStatePath= maePrjDir + "State" + mae.fileSeparator,
        sTempPath= maePrjDir + "Temp" + mae.fileSeparator,
        sTmpPath= maePrjDir + "tmp" + mae.fileSeparator;
      
      if(!(fCache= new File(sCachePath)).isDirectory())
        fCache.mkdirs();
      if(!(fConfig= new File(sConfigPath)).isDirectory())
        fConfig.mkdirs();
      if(!(fImages= new File(sImagesPath)).isDirectory())
        fImages.mkdirs();
      if(!(fMAE= new File(sMAEPath)).isDirectory())
        fMAE.mkdirs();
      if(!(fQuant= new File(sQuantPath)).isDirectory())
        fQuant.mkdirs();
      if(!(fReport= new File(sReportPath)).isDirectory())
        fReport.mkdirs();
      if(!(fState= new File(sStatePath)).isDirectory())
        fState.mkdirs();
      if(!(fTemp= new File(sTempPath)).isDirectory())
        fTemp.mkdirs();
      if(!(fTmp= new File(sTmpPath)).isDirectory())
        fTmp.mkdirs();
      
      /*
      if(mae.CONSOLE_FLAG)
        System.out.println("US-CAMMDT maePrjDir="+maePrjDir+
                           "\n sCachePath="+sCachePath+
                           "\n sConfigPath="+sConfigPath+
                           "\n sImagesPath="+sImagesPath+
                           "\n sMAEPath="+sMAEPath+
                           "\n sQuantPath="+sQuantPath+
                           "\n sReportPath="+sReportPath+
                           "\n sStatePath="+sStatePath+
                           "\n sTempPath="+sTempPath+
                           "\n sTmpPath="+sTmpPath);
     */
      return(true);
    }
    
    catch (Exception e)
    {
      return(false);
    }
  } /* checkAndMakeMAEdirTree */
  
  
  /**
   * updateMAEprojectDB() - update 'install-dir'/maeProjects.txt DB file.
   * This may only be called from stand-alone mode.
   * @param maeProj is project name
   * @param maeStartupDir is startup diredtory
   * @param maeStartupFile is .mae startup file
   * @param maeWebAddr is Web address if using an Web database
   * @return true if successful.
   * @see FileIO#writeFileToDisk
   * @see SimpleTable
   * @see Table
   * @see Table#copyTable
   * @see Table#makeTabDelimReport
   * @see Util#showMsg3
   * @see #checkAndMakeMAEdirTree
   */
  boolean updateMAEprojectDB(String maeProj, String maeStartupDir,
                             String maeStartupFile, String maeWebAddr )
  { /* updateMAEprojectDB */
    if(maeProj==null || maeProj.length()==0 ||
       maeStartupDir==null || maeStartupDir.length()==0)
    {
      return(false); /* no-op */
    }
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("US-UMPDB maeProj="+maeProj+
                         "\n  maeStartupDir="+maeStartupDir+
                         "\n  maeStartupFile="+maeStartupFile+
                         "\n  maeWebAddr="+maeWebAddr );
    */
    
    Table prjTbl= new Table(mae, mae.prjListFile, "Project List");
    SimpleTable tmpTbl= null;         /* new table */
    String fields[]= {"Project-Name", "Project-Directory",
                      "Last-Project-File", "Project-Web-Addr",
                      "Active-Project", "Last-date-used"
                     };
     int
       idxPrjName= 0,            /* col index into table */
       idxPrjDir= 1,
       idxPrjFile= 2,
       idxWebAddr= 3,
       idxActPrj= 4,
       idxDate= 5,
       curPrjRow= -1,            /* row of cur proj else -1 */
       nRows= 0,                 /* size of table */
       nCols= fields.length;
     
     /* [1] Find current project in table if it exists */
     if(prjTbl.tRows>0)
     { /* table exists - search for existing proj entry */
       nRows= prjTbl.tRows -1;  /* avoids null row at end */
       for(int r=0;r<nRows;r++)
       { /* search for matching project */
         String cpName= prjTbl.tData[r][idxPrjName];
         if(cpName.equals(maeProj))
         { /* found it - save current project row */
           curPrjRow= r;
           break;
         }
       } /* search for matching project */
     } /* table exists - search for existing proj entry */
     
     /* [2] Extend table length if necessary */
     if(prjTbl.tRows==0 || curPrjRow==-1)
     { /* add a new row */
       curPrjRow= nRows;   /* add new row at end */
       nRows++;
     }
     
     /* [3] Make a new table, copying old table if necessary. */
     tmpTbl= new SimpleTable("tmp MAEproj DB",  /* title of table */
                             "MAE Project DB",  /* loading message */
                             null,              /* file name */
                             nRows, nCols);
     tmpTbl.tFields= fields;                    /* setup the field names*/
     
     /* [3.1] Copy base part of table & copy new data if necessary */
     if(prjTbl.tRows==0)
       curPrjRow= 0;                            /* new database */
     else
       for(int r=0;r<nRows;r++)
         tmpTbl.tData[r]= prjTbl.tData[r];      /* copy old DB */
     
     /* Add new row if needed. */
     tmpTbl.tData[curPrjRow][idxPrjName]= maeProj;
     tmpTbl.tData[curPrjRow][idxPrjDir]= maeStartupDir;
     tmpTbl.tData[curPrjRow][idxPrjFile]= maeStartupFile;
     tmpTbl.tData[curPrjRow][idxWebAddr]= maeWebAddr;
     tmpTbl.tData[curPrjRow][idxDate]= mae.util.dateStr();
     
     /* [3.2] Mark active database */
     for(int r=0;r<nRows;r++)
       tmpTbl.tData[r][idxActPrj]= (r==curPrjRow) ? "TRUE" : "";
       
       prjTbl.copyTable(tmpTbl);            /* replace table */
       
     /* [4] Check for MAE prject directory tree. If not found
      * create the directory tree.
      */
     String
       endStr_MAE_= mae.fileSeparator+"MAE"+mae.fileSeparator,
       maeTreeDir= ((maeStartupDir.toUpperCase()).endsWith(endStr_MAE_))
                       ? maeStartupDir.substring(0,maeStartupDir.length()-4)
                       : maeStartupDir;
     boolean flag= checkAndMakeMAEdirTree(maeTreeDir);
     if(!flag)
       return(false);
      
     /* [5] Write out the table into <user.dir>/MAEprojects.txt file. */
     String sR= prjTbl.makeTabDelimReport(null);
       
     flag= fio.writeFileToDisk(mae.prjListFile, sR);
     if(!flag)
       return(false);
       
     /* [6] Update global state if successful. */
     mae.defSAprjName= maeProj;
     mae.defDir= maeStartupDir;
     mae.defStartupFile= maeStartupFile;
     mae.defSAwebAddr= maeWebAddr;
       
     Util.showMsg3("");
     return(true);
  } /* updateMAEprojectDB */
  
    
   /**
    * updateMAExplorerJarFile() - update MAExplorer.jar into program install area.
    *<PRE>
    * [1] Define directory for MAExplorer.jar path and other file and URL names.
    * [2] Backup the old MAExplorer.jar as MAExplorer.jar.bkup
    * [3] Open the url: from maeJarURL. This is hardwired to be
    *         "http://maexplorer.sourceforge.net/MAExplorer/MAExplorer.jar" 
    *     and read the file from the Web into local file "MAExplorer.jar.tmp"
    * [4] Move the "MAExplorer.jar.tmp" file into "MAExplorer.jar" in the program directory
    *
    * Since changing the MAExplorer.jar file is a potential security risk, 
    * we make this procedure final and hardwire the maeJarURL! 
    *</PRE>
    * @return true if succeed
    * @see FileIO#copyFile 
    * @see FileIO#deleteLocalFile 
    */
   final boolean updateMAExplorerJarFile()
   { /* updateMAExplorerJarFile */
     /* [1] Define directory for MAExplorer.jar path and other file and URL names. */
     String
       userDir= System.getProperty("user.dir")+mae.fileSeparator,
       localMAEjarFile= userDir + "MAExplorer.jar",
       localMAEjarFileBkup= userDir + "MAExplorer.jar.bkup",
       localMAEjarFileTmp= userDir + "MAExplorer.jar.tmp", 
       maeJarURL= "http://maexplorer.sourceforge.net/MAExplorer/MAExplorer.jar",
       maeJarServer= "maexplorer.sourceforge.net";     
     
     /* [2] Backup the old MAExplorer.jar as MAExplorer.jar.bkup if it exists
      * (it won't if you are running from the debugger!). 
      * But first, delete old backup if it exists.
      */
     mae.fio.deleteLocalFile(localMAEjarFileBkup);
     mae.fio.copyFile(localMAEjarFile, localMAEjarFileBkup, null,0);
      
     /* [3] Open the url: maeJarURL and read the file from the Web into "MAExplorer.jar.tmp" */
     String updateMsg= "Updating your MAExplorer.jar file from "+maeJarServer+
                       " server.";
     File f= new File(localMAEjarFileBkup);
     int estInputFileLth= (f!=null) ? (int)f.length() : 0;
     if(! mae.fio.copyFile(maeJarURL,localMAEjarFileTmp,updateMsg,estInputFileLth))
       return(false);

     /* [4] Move the "MAExplorer.jar.tmp" file into  "MAExplorer.jar" in the program directory */
     if(! mae.fio.deleteLocalFile(localMAEjarFile))
        return(false);   
     if(! mae.fio.copyFile(localMAEjarFileTmp, localMAEjarFile, null,0))
       return(false);
      
      return(true);
   } /* updateMAExplorerJarFile */
   
  
   /**
    * updateAllPluginsJarFiles() - update all MAEPlugins from server in the /Plugins directory
    * This prompts for the user to verify they want to update all plugins jar files.
    *<PRE>
    * 1. Reads the directory of .jar file names in maexplorer.sourceforge.net/MAEPlugins/jar/
    * 2. Copies all of these files into INSTALLATION_DIR/tmp
    * 3. Copies INSTALLATION_DIR/tmp to INSTALLATION_DIR/Plugins
    *</PRE>
    * @return true if succeed, false if any part of this fails
    * @see FileIO#copyFile
    * @see FileIO#deleteLocalFile
    * @see FileIO#readBytesFromURL
    */
   boolean updateAllPluginsJarFiles()
  { /* updateAllPluginsJarFiles */
    String maeJarServer= "maexplorer.sourceforge.net";
    try
    { /* rdownload the plugins from the server */
      /* [1] Define directories for Plugins path and other file and URL names. */
      String
        fileSep= mae.fileSeparator,
        userDir= System.getProperty("user.dir")+fileSep,
        localPluginsDir= userDir + "Plugins" + fileSep,
        localTmpDir= userDir + "tmp" + fileSep,
        maePluginsJarDirURL= "http://maexplorer.sourceforge.net/MAEPlugins/jar/",        
        maePluginsJarDirTocURL= maePluginsJarDirURL + "pluginToc.txt";        
     
      /* [2] Make sure installation directory Plugins/ and tmp/ directories 
       * are there else make them.
       */
      File fPlugins= new File(localPluginsDir);
      if(!fPlugins.isDirectory())
        fPlugins.mkdir();
      File fTmp= new File(localTmpDir);
      if(!fTmp.isDirectory())
        fTmp.mkdir();
      
      /* [3] Remove all files in tmp/ directory  */
      fTmp= new File(localTmpDir);
      String tmpFiles[]= fTmp.list();   /* list of all tmp/ files */
      if(tmpFiles!=null)
      {
        for(int i=0;i<tmpFiles.length;i++)
          mae.fio.deleteLocalFile(tmpFiles[i]);
      }
      
      /* [4] Get list of files in the server maePluginsJarDirURL */
      byte bVersion[]= mae.fio.readBytesFromURL(maePluginsJarDirTocURL, null);
      if(bVersion==null) 
        return(false);                              /* sorry, no data */
      
      /* [4.1] Convert list of files to string */
      String s= new String(bVersion);
      int idx= s.indexOf('\0');
      String serverPluginJarFilesList= s.substring(0,idx); 
      
      /* [4.2] Convert string list of files to string array of file names */
      String
        nextFile,
        serverPluginJarFiles[]= mae.util.cvs2Array(serverPluginJarFilesList, 
                                                   200, "\n" /* delimiterChr */ );
      int 
        nFiles= 0;
      for(int i=0;i<serverPluginJarFiles.length;i++)
      { /* count # of files */
        nextFile= serverPluginJarFiles[i];
        if(nextFile==null || nextFile.length()==0)
          break;
        else
          nFiles++; 
      }
      if(nFiles==0)
        return(false);              /* don't bother */
      String
        areYouSure= mae.mbf.pdq.dialogQuery(": [yes|no]. Update ALL "+nFiles+" plugins from "+
                                              maeJarServer+
                                              " Web site - are you sure? ", 
                                              "no");
      if(!areYouSure.equalsIgnoreCase("yes"))
        return(false);               /* don't bother */
      
      /* [5] Read Plugin/*.jar files to tmp/ */
      for(int j=0;j<nFiles;j++)
      { /* read j'th plugin into tmp file and then copy to plugin directory */ 
        nextFile= serverPluginJarFiles[j].trim();  /* remove whitespace */  
        String
          tmpFile= localTmpDir + nextFile,
          pluginFile= localPluginsDir + nextFile,
          pluginJarURLfile= maePluginsJarDirURL + nextFile,
          updateMsg= "Updating Plugin #"+(j+1)+" ["+nextFile+"] from "+
                     maeJarServer+" server.";
         
          if(mae.fio.copyFile(pluginJarURLfile,tmpFile,updateMsg,0))
          { /* copy j'th tmp/*.jar file to Plugin/ directory */
            if(! mae.fio.copyFile(tmpFile,pluginFile,null,0))
            {
              Util.showMsg2("Can't update plugin "+nextFile+" files from "+
                            maeJarServer+".", Color.white, Color.red );
              Util.popupAlertMsg("Can't Can't update plugin files",
                                 "Can't update plugin "+nextFile+" files from "+
                                 maeJarServer+".",
                                 4, 60);
            }
            else
            { /* del old jar file */ 
              mae.fio.deleteLocalFile(tmpFile);
            }
          } /* copy j'th tmp/*.jar file to Plugin/ directory */      
      } /* read j'th plugin into tmp file and then copy to plugin directory */           
            
      return(true);
    } /* download the plugins from the server */
    
    catch(Exception e)
    {
      Util.showMsg2("FAILED! Unable to update MAExplorer Plugins jar files from "+
                    maeJarServer+".", Color.white, Color.red );
      Util.showMsg3("Make sure you are connected to the Internet and the "+
                    maeJarServer+" server is up.", Color.white, Color.red);
      return(false);
    }
   } /* updateAllPluginsJarFiles */
   
   
   /**
    * updateRmethodsFiles() - download MAExplorer/{R/,RLO/,lib/MAERlibr/} methods
    * files into MAExplorer program install area. This prompts for the user to 
    * verify they want to update all RLOmethods files.
    *<PRE>
    * 1. Reads the directories of RLO (.rlo,.R)  file names in
    *   maexplorer.sourceforge.net/MAExplorer/RLOmethods.txt
    * 1.1 Builds list of .R files Rlist and .rlo files RLOlist to use
    *   in doing the downloads.
    * 2. Copies all of these files into INSTALLATION_DIR/tmp
    * 3. Copies INSTALLATION_DIR/tmp/*.R to INSTALLATION_DIR/MAExplorer/R/
    * 4. Copies INSTALLATION_DIR/tmp/*.rlo to INSTALLATION_DIR/MAExplorer/RLO/
    *</PRE>
    * @return true if succeed, false if any part of this fails
    * @see FileIO#copyFile
    * @see FileIO#deleteLocalFile
    * @see FileIO#readBytesFromURL
    */
   boolean updateRmethodsFiles()
   { /* updateRmethodsFiles */
     String
       maeRLOmethodsDirURL= "http://maexplorer.sourceforge.net/MAExplorer/",
       RdirURL= maeRLOmethodsDirURL + "R/",
       RLOdirURL= maeRLOmethodsDirURL + "RLO/",
       RLOmethodsTocURL= maeRLOmethodsDirURL + "RLOmethodsToc.txt";
     try
     { /* rdownload the plugins from the server */
      /* [1] Define directories for Plugins path and other file and URL names. */
      String
        fileSep= mae.fileSeparator,
        userDir= System.getProperty("user.dir")+fileSep,
        localRscriptDir= userDir + "R" + fileSep,
        localRLO_Dir= userDir + "RLO" + fileSep,
        localTmpDir= userDir + "tmp" + fileSep;      
     
      /* [2] Make sure installation directories R/ RLO/ and tmp/
       * directories are there else make them.
       */
      File fRscript= new File(localRscriptDir);
      if(!fRscript.isDirectory())
        fRscript.mkdir();
      File fRLO= new File(localRLO_Dir);
      if(!fRLO.isDirectory())
        fRLO.mkdir();
      File fTmp= new File(localTmpDir);
      if(!fTmp.isDirectory())
        fTmp.mkdir();
      
      /* [3] Remove all files in tmp/ directory  */
      fTmp= new File(localTmpDir);
      String tmpFiles[]= fTmp.list();   /* list of all tmp/ files */
      if(tmpFiles!=null)
      {
        for(int i=0;i<tmpFiles.length;i++)
          mae.fio.deleteLocalFile(tmpFiles[i]);
      }
      
      /* [4] Get list of files in the server RLOmethodsTocURL.
       * Note that this list is the list of .rlo files created
       * by doing a "(cd RLO/; ls -s *.rlo > ../RLOmethodsToc.txt)
       */
      byte newRLOList[]= mae.fio.readBytesFromURL(RLOmethodsTocURL, null);
      if(newRLOList==null) 
        return(false);                              /* sorry, no data */
      
      /* [4.1] Convert list of files to string */
      String s= new String(newRLOList);
      int idx= s.indexOf('\0');
      String serverRLOmethodsFilesList= s.substring(0,idx); 
      
      /* [4.2] Convert string list of files to string array of file names */
      String
        rloFile,
        serverRLOmethodsFiles[]= mae.util.cvs2Array(serverRLOmethodsFilesList, 
                                                   200, "\n" /* delimiterChr */ );
      int 
        nFiles= 0;
      for(int i=0;i<serverRLOmethodsFiles.length;i++)
      { /* count # of files */
        rloFile= serverRLOmethodsFiles[i];
        if(rloFile==null || rloFile.length()==0)
          break;
        else
          nFiles++; 
      }
      if(nFiles==0)
        return(false);              /* don't bother */
      String
        areYouSure= mae.mbf.pdq.dialogQuery(": [yes|no]. Update ALL "+nFiles+
                                            " RLO methods from "+
                                            maeRLOmethodsDirURL+
                                            " Web site - are you sure? ", 
                                            "no");
      if(!areYouSure.equalsIgnoreCase("yes"))
        return(false);               /* don't bother */
      
      /* [5] Read in R & RLO files to tmp/ then to install dirs */
      for(int j=0;j<nFiles;j++)
      { /* read j'th RLO method & R files into tmp file and then copy to install dir */ 
        rloFile= serverRLOmethodsFiles[j].trim();  /* remove whitespace */ 
        int rloLen= rloFile.length();
        String
          RscriptFile= rloFile.substring(0,(rloLen-4)) + ".R",
          RscriptURLfile= RdirURL + RscriptFile,
          RLO_URLfile= RLOdirURL + rloFile,          
          tmpRfile= localTmpDir + RscriptFile,          
          tmpRLOfile= localTmpDir + rloFile,
          updateMsg= "Updating RLO method #"+(j+1)+" ["+RscriptFile+"] from "+
                     maeRLOmethodsDirURL+" server.";
        
         /* Copy R file from URL to tmp dir first */
          if(mae.fio.copyFile(RscriptURLfile,tmpRfile,updateMsg,0))
          { /* copy j'th tmp/.R file to R dir */
            
            /* Now copy R file from tmp to R dir */
            if(! mae.fio.copyFile(tmpRfile,localRscriptDir+RscriptFile,null,0))
            {
              Util.showMsg2("Can't update Rscript "+RscriptFile+" files from "+
                            maeRLOmethodsDirURL+".", Color.white, Color.red );
              Util.popupAlertMsg("Can't Can't update R script files",
                                 "Can't update R script "+RscriptFile+
                                 " files from "+ maeRLOmethodsDirURL+".",
                                 4, 60);
               return(false);                   /* Error */
            }
            else
            { /* del old file */ 
              mae.fio.deleteLocalFile(tmpRfile);
            }
          }  /* copy j'th tmp/.R file to R dir */
                 
         /* [5.2] Copy RLO file from URL to tmp dir first */
          if(mae.fio.copyFile(RLO_URLfile,tmpRLOfile,updateMsg,0))
          { /* copy j'th tmp/*.rlo file to RLO/ directory */
            
             /* Now copy RLO file from tmp to RLO dir */
            if(! mae.fio.copyFile(tmpRLOfile,localRLO_Dir + rloFile,null,0))
            {
              Util.showMsg2("Can't update RLO "+rloFile+" files from "+
                            maeRLOmethodsDirURL+".", Color.white, Color.red );
              Util.popupAlertMsg("Can't Can't update RLO files",
                                 "Can't update RLO "+rloFile+" files from "+
                                 maeRLOmethodsDirURL+".",
                                 4, 60);
              return(false);/* Error */
            }
            else
            { /* del old file */ 
              mae.fio.deleteLocalFile(tmpRLOfile);
            }
          } /* copy j'th tmp/*.rlo file to RLO/ directory */
      } /* read j'th RLO method & R file into tmp files and then copy to install dir */           
            
     /* [5.3] Download MAExplorer/lib/MAERlibr/* files into MAExplorer program 
      * install area. 
      */
     if(updateMAERlibrFiles())
       return(false);
      
      return(true);
    } /* download the plugins from the server */
    
    catch(Exception e)
    {
      Util.showMsg2("FAILED! Unable to update MAExplorer RLO methods files from "+
                    maeRLOmethodsDirURL+".", Color.white, Color.red );
      Util.showMsg3("Make sure you are connected to the Internet and the "+
                    maeRLOmethodsDirURL+" server is up.", Color.white, Color.red);
      return(false);
    } 
   } /* updateRmethodsFiles */
      
   
   /**
    * updateMAERlibrFiles() - download MAExplorer/lib/MAERlibr/* files
    * into MAExplorer program install area. 
    * [TODO] needs to be finished and debugged...<BR>
    * The server file MAERlibrTocURL contains a relative directory 
    * tree of lib/MAERlibr/ created by doing a Unix 
    *<PRE>
    *   "ls -1R lib/ > ../MAERlibrToc.txt"
    * 1. Reads the list of files to download from
    *   maexplorer.sourceforge.net/MAExplorer/MAERlibrToc.txt
    * 2. Copies all of these files into INSTALLATION_DIR/tmp
    * 3. Copies INSTALLATION_DIR/tmp/* to INSTALLATION_DIR/MAExplorer/lib/MAERlibr/
    *</PRE>
    * @return true if succeed, false if any part of this fails
    * @see FileIO#copyFile
    * @see FileIO#deleteLocalFile
    * @see FileIO#readBytesFromURL
    */
   boolean updateMAERlibrFiles()
   { /* updateMAERlibrFiles */
     String
       maeRLOmethodsDirURL= "http://maexplorer.sourceforge.net/MAExplorer/",
       MAERlibDirURL= maeRLOmethodsDirURL + "lib/MAERlibr/",
       MAERlibFileURL= MAERlibDirURL + "R/MAERlibr",
       MAERlibrTocURL= maeRLOmethodsDirURL + "MAERlibrToc.txt";
     try
     { /* download the MAE lib from the server */
       /* [1] Define directories for Plugins path and other file and URL names. */
       String
         fileSep= mae.fileSeparator,
         userDir= System.getProperty("user.dir")+fileSep,
         localLibrDir= userDir + "lib" + fileSep,
         localMAERlibrDir= localLibrDir + "MAERlibr" + fileSep,
         localMAERlibrFileDir= localMAERlibrDir + "R" + fileSep,
         localMAERlibrFileBkup= localMAERlibrFileDir+ "MAERlibr.bkup",
         localMAERlibrFileTmp= localMAERlibrFileDir+ "MAERlibr.tmp",
         localMAERlibrFile= localMAERlibrFileDir+ "MAERlibr" ,
         localTmpDir= userDir + "tmp" + fileSep;      
     
       /* [2] Make sure installation directories lib/MAERlibr/ and tmp/
        * directories are there else make them.
        */
       File fLib= new File(localLibrDir);
       if(!fLib.isDirectory())
          fLib.mkdir();
      
       File fMAERlibr= new File(localMAERlibrDir);
       if(!fMAERlibr.isDirectory())
       { /* Make all of the MAERlibr package subdirectories */
         /* Package includes: data/ doc/ help/ html/ man/ R/ R-ex
          * and files: CONTENTS COPYING DESCRIPTION INDEX README TITLE
          *[TODO] copy the files from server to directories.sy
          */
         fMAERlibr.mkdir();
         /* make package sub directories */
         File fp= new File(localMAERlibrDir+ "R" + fileSep);
         fp.mkdir();
         fp= new File(localMAERlibrDir+ "R-ex" + fileSep);
         fp.mkdir();
         fp= new File(localMAERlibrDir+ "HTML" + fileSep);
         fp.mkdir();
         fp= new File(localMAERlibrDir+ "doc" + fileSep);
         fp.mkdir();
         fp= new File(localMAERlibrDir+ "help" + fileSep);
         fp.mkdir();
         fp= new File(localMAERlibrDir+ "man" + fileSep);
         fp.mkdir();
       }
       File fTmp= new File(localTmpDir);
       if(!fTmp.isDirectory())
         fTmp.mkdir();
       
       /* [3] Remove all files in tmp/ directory so don't copy trash into working system */
       fTmp= new File(localTmpDir);
       String tmpFiles[]= fTmp.list();   /* list of all tmp/ files */
       if(tmpFiles!=null)
       {
         for(int i=0;i<tmpFiles.length;i++)
           mae.fio.deleteLocalFile(tmpFiles[i]);
       }
       
       /* [4] Copy just lib/MAERlibr/MAElibr from the server RLOmethodsTocURL.
        * First delete the local backup and then copy the current
        *  lib/MAERlibr/R/MAElibr to  lib/MAERlibr/R/MAElibr.bkup
        */
       mae.fio.deleteLocalFile(localMAERlibrFileBkup);
       mae.fio.copyFile(localMAERlibrFile, localMAERlibrFileBkup, null,0);
       
       /* [4.1] Copy the url: MAExplorer/lib/MAERlibr/R/MAERlibr from the Web
        * server into {userdir}/lib/MAERlibr/R/MAElibr.tmp" 
        */
       String updateMsg= "Updating your MAERlibr file from "+maeRLOmethodsDirURL+
                         " server.";
       File fEst= new File(localMAERlibrFileBkup);
       int estLth= (fEst!=null) ? (int)fEst.length() : 0;
       if(! mae.fio.copyFile(MAERlibFileURL,localMAERlibrFileTmp,updateMsg,estLth))
         return(false);
       
       /* [4.2] Move the "lib/MAERlibr/R/MAERlibr.tmp" file into 
        * "lib/MAERlibr/R/MAERlibr.tmp" in the program directory.
        */
       mae.fio.deleteLocalFile(localMAERlibrFile);
       if(! mae.fio.copyFile(localMAERlibrFileTmp, localMAERlibrFile, null,0))
         return(false);

        if(mae.NEVER)
        { /* Copy the full lib/MAERlibr directory tree */
          /* [5] [TODO]
           * Get list of files in the server RLOmethodsTocURL from the tree.
           * Note that this list is the list of .rlo files created
           * by doing a "(cd RLO/; ls -s *.rlo > ../RLOmethodsToc.txt)
          */
          byte newMAERlibrList[]= mae.fio.readBytesFromURL(MAERlibrTocURL, null);
          if(newMAERlibrList==null)
            return(false);                              /* sorry, no data */
         
          /* [5.1] Convert list of files to string */
          String s= new String(newMAERlibrList);
          int idx= s.indexOf('\0');
          String serverMAERlibrFilesList= s.substring(0,idx);
         
          /* [5.2'] Convert string list of files to string array of file names */
          String
            rloFile,
            serverMAERlibrFiles[]= mae.util.cvs2Array(serverMAERlibrFilesList,
                                                     200, "\n" /* delimiterChr */ );
          int nFiles= 0;
          for(int i=0;i<serverMAERlibrFiles.length;i++)
          { /* count # of files */
            rloFile= serverMAERlibrFiles[i];
            if(rloFile==null || rloFile.length()==0)
              break;
            else
              nFiles++;
          }
          if(nFiles==0)
            return(false);              /* don't bother */
          
          /* [5.3'] Read in lib/MAERlibr/* files to tmp/ then to install in dirs */
          for(int j=0;j<nFiles;j++)
          { /* read j'th RLO/ method & R-ex/ file into tmp/, and then to install dir */
            String sFile= serverMAERlibrFiles[j].trim();  /* remove whitespace */
            int sLen= sFile.length();
            String
              urlFile= MAERlibDirURL + sFile,
              tmpFile= localTmpDir + sFile,
              updateMsg2= "Updating MAERlibr #"+(j+1)+" ["+sFile+"] from "+
                          MAERlibDirURL+" server.";
           
            /* Copy MAERlibr file from URL to tmp dir first */
            if(mae.fio.copyFile(urlFile,tmpFile,updateMsg,0))
            { /* copy j'th tmp/ file to install dir */
              if(! mae.fio.copyFile(tmpFile,urlFile+sFile,null,0))
              {
                Util.showMsg2("Can't update MAERlibr "+sFile+" files from "+
                              MAERlibDirURL+".", Color.white, Color.red );
                Util.popupAlertMsg("Can't Can't update MAERlibr files",
                                   "Can't update MAERlibr "+sFile+
                                   " files from "+ MAERlibDirURL+".",
                                   4, 60);
                return(false);                   /* Error */
              }
              else
              { /* del old file */
                mae.fio.deleteLocalFile(tmpFile);
              }
            }  /* copy j'th tmp/ file to install dir */
          } /* read j'th RLO/ method & R-ex/ file into tmp/, and then to install dir */
          
        } /* Copy the full lib/MAERlibr directory tree */
     
      return(true);
    } /* download the MAE lib from the server */
    
    catch(Exception e)
    {
      Util.showMsg2("FAILED! Unable to update MAExplorer MAERlibr package files from "+
                    MAERlibDirURL+".", Color.white, Color.red );
      Util.showMsg3("Make sure you are connected to the Internet and the "+
                    MAERlibDirURL+" server is up.", Color.white, Color.red);
      return(false);
    }     
   } /* updateMAERlibrFiles */
 
   
   /**
    * updateMAERlibrFilesViaZip() - Download and install library files via MAERlibr.zip file.
    *<PRE>
    * 1. Rename and move old dir tree from program area to tmp dir
    * 2. Download MAERlibr.zip file from the URL into MAExplorer tmp area.
    * 3. Extract zip file from tmp to the MAExplorer program directory
    *</PRE>
    * @return true if succeed, false if any part of this fails
    * @see FileIO#extractZipFiles
    * @see FileIO#readBytesFromURL
    * @see Util#popupAlertMsg
    */
  public boolean updateMAERlibrFilesViaZip()
   { /* updateMAERlibrFilesViaZip */     
     /* [1] Define directories & other file and URL names. */
     String
       maeDirURL= "http://maexplorer.sourceforge.net/MAExplorer/",
       MAERlibZipFileURL= maeDirURL + "MAERlibr.zip";
     String
       fileSep= mae.fileSeparator,
       userDir= System.getProperty("user.dir")+fileSep,
       localLibrDir= userDir + "lib" + fileSep,
       localMAERlibrDir= localLibrDir + "MAERlibr" + fileSep,
       localTmpDir= userDir + "tmp" + fileSep,
       localMAERlibrDirTemp= localTmpDir + "MAERlibr" + "BackupTempDir" + fileSep,
       localMAERlibrZipFile= localTmpDir + "MAERlibr.zip",
       localMAERlibrZipFileTmp= localTmpDir + "MAERlibrTmp.zip";
     final int bufSize= 2048;
     File
       curFileMAERlibr= null, /* current mae R lib dir if exists */
       newFileMAERlibr= null; /* new mae lib dir */
     boolean errorFlag= false;  
       
     try
     { /* download the MAE lib from the server */       
       /* [2] Setup dirs */   
       BufferedOutputStream outputStream= null;   
       curFileMAERlibr= new File(localMAERlibrDir);
       newFileMAERlibr= new File(localMAERlibrDirTemp); 
       
       if(curFileMAERlibr==null || newFileMAERlibr==null)
         return(false);                       /* Error */
       
       /* [3] Rename and move cur dir (will be come the old one) if exists */
       if(curFileMAERlibr.isDirectory())
       { /* Rename cur dir */         
         if(localMAERlibrDirTemp != null)
         {
           File dirToDel= new File(localMAERlibrDirTemp);          
           if(dirToDel != null)
             mae.fio.deleteRecursive(dirToDel); /* delete old tmp dir */
         }
          
         curFileMAERlibr.renameTo(newFileMAERlibr);
         
         if(localMAERlibrDir!=null)
         {
           File dirToDel= new File(localMAERlibrDir);
           if(dirToDel != null)
             mae.fio.deleteRecursive(dirToDel); /* since copied del original */      
         }
         errorFlag= false; /* everything ok */
       } /* Rename cur dir */
       
       /* create tmp dir */
       File fTmp= new File(localTmpDir);
       if(!fTmp.isDirectory())
         fTmp.mkdir();
          
       /* [4] Get and save zip file from url address into tmp location */
       byte newMAERlibrZip[]= mae.fio.readBytesFromURL(MAERlibZipFileURL, null);
       
       if(newMAERlibrZip != null)
       {        
         /* [5] Write zip file byte data to tmp disk */
         File f= new File(localMAERlibrZipFileTmp);
         FileOutputStream fos= new FileOutputStream(f);
         outputStream= new BufferedOutputStream(fos, bufSize);
         outputStream.write(newMAERlibrZip);
         
         outputStream.flush();                    /* close file output stream */
         outputStream.close();
         
         /* [6] Extract MAE R lib zip file to new dir tree */
         boolean extractFlag= mae.fio.extractZipFiles(localMAERlibrZipFileTmp, 
                                                      localMAERlibrDir);         
         if(!extractFlag) 
         {
           Util.showMsg2("Can't update MAERlibr file from "+
                         MAERlibZipFileURL+".", Color.white, Color.red );
           Util.popupAlertMsg("Can't update MAERlibr files",
                        "Can't update MAERlibr file from "+ MAERlibZipFileURL+".", 4, 60);
           errorFlag= true; /* problem */
         }
       }
       else
         errorFlag= true;                          /* problem */
     
     } /* download the MAE lib from the server */
     catch(Exception e)
     {
       System.out.println("updateMAERlibrFilesViaZip(): Error: " + e);
       Util.showMsg2("FAILED! Unable to update MAExplorer MAERlibr package files from "+
                     maeDirURL+".", Color.white, Color.red );
       Util.showMsg3("Make sure you are connected to the Internet and the "+
                     maeDirURL+" server is up.", Color.white, Color.red);
       errorFlag= true;                           /* problem */
     }
     
     /* [7] Fatal error occured, must copy original dir back and delete the tmp */
     if(errorFlag)
     {  /* Restore old dir since there was a problem */
       System.out.println("updateMAERlibrFilesViaZip(): Error with zip file: Now restoring original lib files " );
       Util.showMsg2("FAILED, Error updating MAERlib files from Zip ", Color.white, Color.red );
       Util.showMsg3("Restored original MAE R lib files.", Color.white, Color.red);
       
       if(newFileMAERlibr!=null && curFileMAERlibr!=null)
       { /* copy old dir back */
         File dirToDel= new File(localMAERlibrDir);
         if(dirToDel != null)
           mae.fio.deleteRecursive(dirToDel);    /* del newly created one since error*/
         
         newFileMAERlibr.renameTo(curFileMAERlibr);
         
         dirToDel= new File(localMAERlibrDirTemp);
         if(dirToDel != null)
           mae.fio.deleteRecursive(dirToDel);   /* delete tmp dir since copied back */
         
         return(false);         /* sorry, no data */
       } /* copy old dir back */
     } /* Restore old dir since there was a problem */
     
     return(true);     
  } /* updateMAERlibrFilesViaZip */
  
  
   /**
    * resizeLAXfileData() - resize MAExplroer.lax file data for MAExplorer.
    * This file is read by the InstallAnywhere runtime when you start
    * MAExplorer and is used to set the initial heap and stack process size.
    * The LAX file (e.g., C:/ProgramFiles/MAExplorer/MAExplorer.lax)
    * specifies the startup sizes for both the heap and stack.
    * It determines the current size, asks the user for a new size
    * then creates a new file if it succeeds in the Q and A.
    * The memory size specified must be > 128Mbytes and less than the swap space.
    * However, it is not clear how to get access to that value across all systems.
    * @return -1 if fail, else the new memory size
    */
   int resizeLAXfileData()
   { /* resizeLAXfileData */
     int
       memSize= -1;
     try
     { /* do file modifications */
       /* [1] Define directory for MAExplorer.lax path and other file and URL names. */       
       String
         line,
         laxSizeStr,
         fileSep= mae.fileSeparator,
         userDir= System.getProperty("user.dir")+fileSep,
         localMAElaxFile= userDir + "MAExplorer.lax",
         localMAElaxFileBkup= userDir + "MAExplorer.lax.bkup",
         localMAElaxFileTmp= userDir + "MAExplorer.lax.tmp",
         laxHeapSizeStr= null,
         laxStackSizeStr= null,
         laxHeapSizeName=  "lax.nl.java.option.java.heap.size.max=",
         laxStackSizeName= "lax.nl.java.option.native.stack.size.max=",
         laxStr;
       int
         laxHeapSize= 0,
         laxStackSize= 0,
         lthHeapName= laxHeapSizeName.length(),
         lthStackName= laxStackSizeName.length();       
       
       /* [2] Parse out the two sizes from laxStr */
       File f= new File(localMAElaxFile);
       if(!f.exists())
         return(-1);
       else if(!f.canRead())
         return(-1);
       BufferedReader rin= new BufferedReader(new FileReader(f));
       while(laxHeapSizeStr==null || laxStackSizeStr==null)
       { /* read-write loop */
         line= rin.readLine();
         if(line==null)
           break;
         if (line.startsWith(laxHeapSizeName))
           laxHeapSizeStr= line.substring(lthHeapName);
         else if (line.startsWith(laxStackSizeName))
           laxStackSizeStr= line.substring(lthStackName);
       } /* read-write loop */ 
       
       /* [2.1] cvt to ints */
       if(laxHeapSizeStr==null || laxStackSizeStr==null)
         return(-1);
       laxHeapSize= Util.cvs2i(laxHeapSizeStr,256000000);
       laxStackSize= Util.cvs2i(laxStackSizeStr,256000000);
       memSize= Math.max(laxHeapSize, laxStackSize);
       
       /* [3] Popup a dialog box that asks for a new memory size */
       laxSizeStr=
         mae.mbf.pdq.dialogQuery("the new MAExplorer memory size. Current value (in Mbytes) is:",
                                 (""+(memSize/1000000)));
       if(laxSizeStr==null)
         return(-1);
       memSize= Util.cvs2i(laxSizeStr,256)*1000000;
       if(memSize<128)
         return(-1);                  /* must have at least 128Mb */
       
      /* [4] Backup the old MAExplorer.lax  as MAExplorer.lax.bkup if it exists
       * (it won't if you are running from the debugger!).
       * But first, delete old backup if it exists.
       */
       mae.fio.deleteLocalFile(localMAElaxFileBkup);
       mae.fio.copyFile(localMAElaxFile, localMAElaxFileBkup, null,0);
       
       /* [5] Then create a new MAExplorer.lax file */
       laxHeapSize= memSize;
       laxStackSize= memSize;
       /* copy the data from MAExplorer.lax.bkup to MAExplorer.lax */
       StringBuffer strBuffer= new StringBuffer(25000);     /* est. - optimize */
       f= new File(localMAElaxFileBkup);
       if(!f.canRead() || !f.exists())
         return(-1);
       rin= new BufferedReader(new FileReader(f));
       
      /* The data of interest is of the following form:
       * "#  LAX.NL.JAVA.OPTION.JAVA.HEAP.SIZE.MAX
       *  #   -------------------------------------
       *
       *  lax.nl.java.option.java.heap.size.max=256000000
       *
       *
       *  #   LAX.NL.JAVA.OPTION.NATIVE.STACK.SIZE.MAX
       *  #   ----------------------------------------
       *
       *  lax.nl.java.option.native.stack.size.max=256000000
       *  "
       */
       while(true)
       { /* read-write loop */
         line= rin.readLine();
         if(line==null)
           break;
         if (line.startsWith(laxHeapSizeName))
           line= laxHeapSizeName+laxHeapSize;
         else if (line.startsWith(laxStackSizeName))
           line= laxStackSizeName+laxStackSize;
         strBuffer.append(line + "\n");
       } /* read-write loop */   
       
       String dataStr= new String(strBuffer);   /* make string to write */
       if(!fio.writeData(localMAElaxFile, "Updating LAX startup file", dataStr))
         return(-1);
     } /* do file modifications */
     
     catch(Exception e1)
     { /* just fail if any problems at all! */
       return(-1);
     }
     
     return(memSize);   
  } /* resizeLAXfileData */
  
} /* end of class UserState */
