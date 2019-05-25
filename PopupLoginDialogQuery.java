/** File: PopupLoginDialogQuery.java */
 
import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * The class creates apopup query dialog for requesting user login (username,password) data.
 * This is used for sample access verification or other protected access.
 * This class displays 2 dialogs in the same window containing editable TextFields 
 * for "Login name" and "Password". There are also 2 buttons ("Ok" and "Cancel")
 * used to specify whether to pass the information on or not.
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class PopupLoginDialogQuery extends Dialog implements ActionListener, WindowListener 
{
  /** link to global instance of MAExplorer */
  private MAExplorer 
    mae;           
  /** default prompt string */
  final private String
    defaultMsg= "Enter project login and password";
  
  /** for optional message label */
  String
    optMsg;
  /** login name to pass back to MAE */
  String
    loginNameData;
  /** password data for returning back to MAE */
  String
    passwordData;
  
  /** # of columns to use */
  private int 
    colSize;
  /** size of frame */
  private int 
    width;
  /** size of frame */
  private int 
    height;
  /* Frame holding the popup */
  private Frame
    frame;
  /** place login text to be edited here */
  TextField
    loginTextField;
  /** place password text to be edited here */
  TextField	        
    passwordTextField;
  /** for optional message label */
  private Label				
    optLabel;
  /** for login label */
  private Label			
    loginLabel;
  /** for password label */
  private Label	
    passwordLabel;
  /** flag set by OK and Cancel buttons */
  boolean
    dataOK; 
  /** flag to wait for button to be pushed */                    
  boolean
    sleepFlag;	
  /* Tried this instead of "this" */
  private ActionListener 
    listener;	
  /** variable holding spaces */		
  private String
    spaces= "                                          "; 


  /**
   * PopupLoginDialogQuery() - Constructor
   * @param MaE is instance of MAExplorer
   * @param f is the frame to insert this in.
   * @see #startPopupDialog
   */
  PopupLoginDialogQuery(MAExplorer MaE, Frame f)
  { /* PopupLoginDialogQuery */
    super(f,"Login dialog box",true);
    
    /* [1] set some defaults */
    mae= MaE;
    colSize= 40;
    loginNameData= "";
    passwordData= "";
    frame= f;
    optMsg= defaultMsg;
    
    /* [2] create popup and hide it for use later */
    this.startPopupDialog("Login Dialog",colSize);
  } /* PopupLoginDialogQuery */
  
  
  /**
   * startPopupDialog() - create a hidden dialog panel within a frame.
   * @param windowTitleis the window title for setTitle
   * @param colSize is the size of textField
   */
  void startPopupDialog(String windowTitle, int colSize)
  { /* startPopupDialog */
    Panel buttonPanel;		/* place buttons here */
    Button
      ok,		              /* update data */
      cancel;	        		/* use default data */
    GridLayout  gd;       /* for layout of text fields, label, etc */
   
    
    /* [1] initialize */
    gd= new GridLayout(7,1);
    this.setLayout(gd);	    /* set gridlayout to frame */
    
    /* [1.1] Create User instruction label */
    optLabel= new Label(spaces);
    optLabel.setText(optMsg);
    loginLabel= new Label("Login name");
    passwordLabel= new Label("Password");
    
    /* [2] Create the buttons and arrange to handle button clicks */
    buttonPanel= new Panel();
    
    ok= new Button("Ok");
    ok.addActionListener(this);
    
    cancel= new Button(" Cancel");
    cancel.addActionListener(this);
    
    buttonPanel.add("Center",ok);
    buttonPanel.add("Center", cancel);
    
    /* [3] add data text fields to panel  */
    loginTextField= new TextField(colSize);
    passwordTextField= new TextField(colSize);
    passwordTextField.setEchoChar('*');
    
    /* [4] add to grid */
    this.add(optLabel);	             /* data description label */
    this.add(loginLabel);	           /* data description label */
    this.add(loginTextField);        /* editable text */
    
    this.add(passwordLabel);	       /* data description label */
    this.add(passwordTextField);     /* editable text */
    
    this.add(buttonPanel);           /* buttons (ok & cancel) */
    this.addWindowListener(this);    /* listener for window events */
    
    /* [5] add components and create frame */
    this.setTitle(windowTitle);      /* frame title */
    this.pack();
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point pos= new Point((screen.width - frame.getSize().width)/2,
                         (screen.height - frame.getSize().height)/2);
    this.setLocation(pos);
    
    this.setVisible(false);	      /* hide frame which can be shown later */
  } /* startPopupDialog */
  
  
  /**
   * updatePopupDialog() - display/unhide popup dialog frame set new values.
   * Remove recreate actionListeners & components
   * @param projectName is the project name
   * @param optMsg is optional message
   */
  void updatePopupDialog(String projectName, String optMsg)
  { /* updatePopupDialog */
    mae.curPrjName= projectName;
    
    /* [1] add data text fields to panel */
    if(optMsg==null)
      optMsg= defaultMsg;
    optLabel.setText(optMsg);
    loginTextField.setText("");
    passwordTextField.setText("");
    
    /* [2] add components and unhide */
    this.setVisible(true);		/* display it; unhide it */
  } /* updatePopupDialog */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is action event when pressed a button
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    String cmd= e.getActionCommand();
    
    /* [1] see which button was pushed and do the right thing,
     * hide window and return default/altered data 
     */
    if (cmd.equals("Cancel"))
    { /* send default data back - data is already stored into this.data */
      this.setVisible(false);    /* hide frame which can be shown later */
      dataOK= false;
    } 
    
    else
      if(cmd.equals("Ok"))
      { /* hide window and return data back entered by user */
        loginNameData= loginTextField.getText(); /* altered data returned */
        passwordData= passwordTextField.getText(); /* altered data returned */
        this.setVisible(false);     /* hide frame which can be shown later*/
        dataOK= true;
      }
  } /* actionPerformed */
  
  
  /**
   * windowClosing() - close down the window on PC only.
   * @param e is window closing event
   */
  public void windowClosing(WindowEvent e)
  {
    e.getWindow().dispose();
  }
  
  
  /**
   * dialogQuery() - query user name and password
   * Note: must go in after return to get loginNameData, and passwordData.
   * @param projectName is the project name
   * @param optMsg is optional message
   * @return true if pressed Ok, false if pressed Cancel
   * @see #updatePopupDialog
   */
  boolean dialogQuery(String projectName, String optMsg)
  { /* dialogQuery */
    this.sleepFlag= true;	      /* flag for waiting */
    this.dataOK= false;
    this.loginNameData= mae.userName;
    this.passwordData= mae.userPasswd;
    
    updatePopupDialog(projectName, optMsg);  /* do it and wait for event handler*/
    
    /*
    if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("PDODQ-DQ loginNameData="+this.loginNameData+
                       " passwordData="+passwordData+" projectName="+projectName+
                       " dataOK="+dataOK);
    */
    if(!dataOK)
      return(false);
    else
      mae.curPrjName= projectName;   /* save default */
    
    return(true);		      /* return string */
  } /* dialogQuery */


  /*Others not used at this time */
  public void windowOpened(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }

} /* end of PopupLoginDialogQuery class */

