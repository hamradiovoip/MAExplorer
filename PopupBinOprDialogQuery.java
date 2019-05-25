/** File: PopupBinOprDialogQuery.java */

import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.io.*;

/**
 * The class creates apopup binary operation query dialog window. 
 * This requests the names of the two source operands and the
 * name of a destination operand.  It offers both pull-down selectors for
 * each operand as well as a type-in text field so they may be specified either
 * way. This class is used with Gene sets and with Conditions sets Boolean
 * operations (eg. union, intersection, difference) etc.
 * The class displays 3 dialog windows containing editable TextFields. 
 * There are also 2 buttons ("Ok" and "Cancel") to pass the infomormation on.
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
 * @version $Date: 2004/01/13 16:45:59 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
  
class PopupBinOprDialogQuery extends Dialog
             implements ActionListener, ItemListener, WindowListener 
{
  /** link to MAExplorer instance */
  private MAExplorer
     mae; 
  /** for returning data back to MAE */
  String
    data1;
  /** for returning data back to MAE */
  String			
    data2;
  /** for returning data back to MAE */
  String
    data3;
  /** # of columns to display */
  int 
    colSize;
  /** size of frame */
  int 
    width;	
  /** size of frame */		
  int 			
    height;
  /** popup frame instance */
  Frame
    frame;
  /** panel that holds choice list */
  private Panel
    optionPanel1;
  /** panel that holds choice list */
  private Panel
    optionPanel2;
  /** panel that holds choice list */
  private Panel    
    optionPanel3;
  /** opt. option choice list */ 
  private Choice
    optionChoice1;
  /** opt. option choice list */ 
  private Choice
    optionChoice2;
  /** opt. option choice list */ 
  private Choice
    optionChoice3;
  /** place text to be edited here */
  TextField
    textField1;
  /** place text to be edited here */
  TextField			
    textField2;
  /** place text to be edited here */
  TextField
    textField3;
  /** for data label */
  Label				
    label1;
  /** for data label */
  Label					
    label2;
  /** for data label */
  Label		
    label3;
  /** wait for button to be pushed */
  boolean
    sleepFlag;
  /** Tried this instead of "this" */
  private ActionListener 
    listener;
  /** spaces */
  private String
    spaces= "                                          "; 
  /** list of option values if present */ 
  String
    optionValues[]= null;       
  /** optionValues[0:nOptions] */
  int
    nOptions= 0;                
  

  /**
   * PopupBinOprDialogQuery() - Constructor
   * @param mae is instance of MAExplorer
   * @param f is frame of parent
   * @see #startPopupDialog
   */
  PopupBinOprDialogQuery(MAExplorer MaE, Frame f)
  { /* PopupBinOprDialogQuery */
    super(f,"Bin Opr dialog box",true);
    
    /* [1] set some defaults */
    mae= MaE;
    colSize= 60;
    data1= spaces;
    data2= spaces;
    data3= spaces;
    frame= f;
    
    /* [2] create popup and hide it for use later */
    this.startPopupDialog("Operation dialog",colSize);
  } /* PopupBinOprDialogQuery */
  
  
  /**
   * startPopupDialog() - create a hidden dialog panel within a frame.
   * @param windowTitle for setTitle
   * @param colSize is size of textField
   */
  void startPopupDialog(String windowTitle,int colSize)
  { /* startPopupDialog */
    Panel buttonPanel;		/* place buttons here */
    Button
     ok,		              /* update data */
     cancel;			        /* use default data */
    GridLayout  gd;       /* for layout of text fields, label, etc */
    
    /* [1] initialize */
    gd= new GridLayout(10,1);
    this.setLayout(gd);	/* set gridlayout to frame */
    
    /* [1.1] Create User instruction label */
    label1= new Label(spaces);
    label2= new Label(spaces);
    label3= new Label(spaces);    
    
    /* [2] Create the buttons and arrange to handle button clicks */
    buttonPanel= new Panel();
    
    ok= new Button("Ok");
    ok.addActionListener(this);
    
    cancel= new Button("Cancel");
    cancel.addActionListener(this);
    
    buttonPanel.add("Center",ok);
    buttonPanel.add("Center", cancel);
    
    /* [3] add data text fields to panel */
    textField1= new TextField(colSize);
    textField2= new TextField(colSize);
    textField3= new TextField(colSize);
    
    optionPanel1= new Panel();
    optionPanel2= new Panel();
    optionPanel3= new Panel();
    
    /* [4] add to grid */
    this.add(label1);	                /* data description label */
    this.add(optionPanel1);
    this.add(textField1);             /* editable text */
    
    this.add(label2);	                /* data description label */
    this.add(optionPanel2);
    this.add(textField2);             /* editable text */
    
    this.add(label3);	                /* data description label */
    this.add(optionPanel3);
    this.add(textField3);             /* editable text */
    
    this.add(buttonPanel);            /* buttons (ok & cancel) */
    this.addWindowListener(this);     /* listener for window events */
    
    /* [5] add components and create frame */
    this.setTitle(windowTitle);       /* frame title */
    this.pack();
    
    /* Center frame on the screen, PC only */
    Dimension screen= Toolkit.getDefaultToolkit().getScreenSize();
    Point  pos= new Point((screen.width-frame.getSize().width)/2,
                          (screen.height-frame.getSize().height)/4);
    this.setLocation(pos);
    
    this.setVisible(false);	         /* hide frame which can be shown later */
  } /* startPopupDialog */
  
  
  /**
   * updatePopupDialog() - display/unhide popup dialog frame and  set new vals
   * Remove recreate actionListeners &  components
   * @param windowTitle for window
   * @param dataMsg1 is label for textField 1
   * @param dataMsg2 is label for textField 2
   * @param dataMsg3 is label for textField 3
   * @param defData1 is data for textField 1
   * @param defData2 is data for textField 2
   * @param defData3 is data for textField 3
   */
  void updatePopupDialog(String windowTitle,
                         String dataMsg1, String dataMsg2, String dataMsg3,
                         String defData1, String defData2, String defData3)
  { /* updatePopupDialog */
    /* [1] Remove components so they can updated below */
    data1= defData1;	             /* store default data  */
    data2= defData2;	             /* store default data  */
    data3= defData3;	             /* store default data  */
    
    this.setTitle("Operation dialog"+windowTitle+ "                        ");
    
    label1.setText(dataMsg1);
    label2.setText(dataMsg2);
    label3.setText(dataMsg3);
    
    /* [2] Add data text fields to panel */
    textField1.setText(defData1);
    textField2.setText(defData2);
    textField3.setText(defData3);
    
    /* [3] Add option choices if option list exists */
    if(optionValues!=null)
    { /* add the option choices */
      optionChoice1= new Choice();
      optionChoice2= new Choice();
      optionChoice3= new Choice();
      optionPanel1.add(optionChoice1);
      optionPanel2.add(optionChoice2);
      optionPanel3.add(optionChoice3);
      for(int i=0;i<nOptions; i++)
      {
        optionChoice1.add(optionValues[i]);
        optionChoice2.add(optionValues[i]);
        optionChoice3.add(optionValues[i]);
      }
      optionChoice1.addItemListener(this);
      optionChoice2.addItemListener(this);
      optionChoice3.addItemListener(this);
    } /* add the option choices */
    
    /* [4] add components and unhide */
    this.setVisible(true);		/* display it; unhide it */
  } /* updatePopupDialog */
  
  
  /**
   * actionPerformed() - Handle button clicks
   * @param e is button press event
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
    }
    
    else if(cmd.equals("Ok"))
      { /* hide window and return data back entered by user */
        data1= textField1.getText(); /* altered data returned */
        data2= textField2.getText(); /* altered data returned */
        data3= textField3.getText(); /* altered data returned */
        this.setVisible(false);     /* hide frame which can be shown later*/
      }
  } /* actionPerformed */
  
  
  /**
   * itemStateChanged() - event handler for pull-down selector Choices for selecting an operand
   * @param e is choices event
   * @see #repaint
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Object obj= e.getSource();
    Choice itemC= (Choice)obj;
    String optionStr;
    
    if(itemC==optionChoice1)
    { /* change the option used - get matching option */
      optionStr= optionChoice1.getSelectedItem();
      if(optionStr!=null)
      { /* update text field */
        textField1.setText(optionStr);
        repaint();
      }
    }
    
    else if(itemC==optionChoice2)
    { /* change the option used - get matching option */
      optionStr= optionChoice2.getSelectedItem();
      if(optionStr!=null)
      { /* update text field */
        textField2.setText(optionStr);
        repaint();
      }
    }
    
    else if(itemC==optionChoice3)
    { /* change the option used - get matching option */
      optionStr= optionChoice3.getSelectedItem();
      if(optionStr!=null)
      { /* update text field */
        textField3.setText(optionStr);
        repaint();
      }
    }
  } /* itemStateChanged */
  
  
  /**
   * windowClosing() - close down the window on PC only.
   * @param e is window closing event
   */
  public void windowClosing(WindowEvent e)
  {
     this.setVisible(false);    /* hide frame which can be shown later */
     //e.getWindow().dispose();
  }
  
  
  /**
   * dialogQuery() - query String variables for the three operands.
   * Note: the caller must go in after this (modal) return to get the data
   * that has been set in this.data1, this.data2, and this.data3.
   * @param windowTitle for window
   * @param dataMsg1 is label for textField 1
   * @param dataMsg2 is label for textField 2
   * @param dataMsg3 is label for textField 3
   * @param defValue1 is data for textField 1
   * @param defValue2 is data for textField 2
   * @param defValue3 is data for textField 3
   * @see #dialogQuery
   */
  boolean dialogQuery(String windowTitle,
                      String dataMsg1, String dataMsg2, String dataMsg3,
                      String defValue1, String defValue2, String defValue3)
  { /* dialogQuery */
    boolean flag= dialogQuery(windowTitle, dataMsg1,dataMsg2, dataMsg3,
                              defValue1, defValue2, defValue3, null, 0);
    return(flag);
  } /* dialogQuery */
  
  
  /**
   * dialogQuery() - query String variables for the three operands.
   * Note: the caller must go in after this (modal) return to get the data
   * that has been set in this.data1, this.data2, and this.data3.
   * @param windowTitle for window
   * @param dataMsg1 is label for textField 1
   * @param dataMsg2 is label for textField 2
   * @param dataMsg3 is label for textField 3
   * @param defValue1 is data for textField 1
   * @param defValue2 is data for textField 2
   * @param defValue3 is data for textField 3
   * @param optionValues is the option lists
   * @param nOptions is the number of options
   * @see #updatePopupDialog
   */
  boolean dialogQuery(String windowTitle,
                      String dataMsg1, String dataMsg2, String dataMsg3,
                      String defValue1, String defValue2, String defValue3,
                      String optionValues[], int nOptions)
  { /* dialogQuery */
    this.sleepFlag= true;	/* flag for waiting */
    
    data1= defValue1;	/* save default */
    data2= defValue2;	/* save default */
    data3= defValue3;	/* save default */
    
    /* Remove old option entries */
    if(optionChoice1!=null)
    {
      optionPanel1.remove(optionChoice1);
      optionPanel2.remove(optionChoice2);
      optionPanel3.remove(optionChoice3);
      optionChoice1= null;
      optionChoice2= null;
      optionChoice3= null;
    }
    
    /* Save new option list */
    this.optionValues= optionValues;
    this.nOptions= nOptions;
    
    updatePopupDialog(windowTitle, dataMsg1,dataMsg2,dataMsg3, data1,data2,data3);
    /*
     if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("PDODQ-DQ data1="+data1+" data2="+data2+" data3="+data3);
     */
    
    return(true);		/* return string */
  } /* dialogQuery */
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e)  { }
  public void windowActivated(WindowEvent e)  { }
  public void windowClosed(WindowEvent e)  { }
  public void windowDeactivated(WindowEvent e)  { }
  public void windowDeiconified(WindowEvent e)  { }
  public void windowIconified(WindowEvent e)  { }
  
} /* end of PopupBinOprDialogQuery class */

