/** File:ExprProfilePopup.java */

import java.text.*;
import java.util.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

/**
 * This class creates and displays a popup window containing an ExprProfilePanel.
 * The ExprProfilePanel contains an ExprProfileCanvas. It also services the 
 * control buttons.
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.9 $ 
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class ExprProfilePopup extends Frame 
       implements ActionListener, ItemListener, WindowListener
{
  /** link to global MAExplorer instance */
  private MAExplorer 
    mae;                       
  
  /** hight of expression profile ploit */  
  final static int 
    EP_BUTTON_HEIGHT= 70;
  
  /** panel we put into this popup window */
  private ExprProfilePanel
    eppl;                      
 
  /** prefered height canvas size, 0 to use defaults */
  private int 
    preferredHeight;           
  /** prefered width canvas size, 0 to use defaults */
  private int 
    preferredWidth;    
    
  /** checkbox to show error bars */
  private Checkbox 
    showErrBarsCheckBox; 
             
  /** Save display as GIF file */
  private Button
    saveAsGIF;                      
  /** plot style (line, circle, curve) button */
  private Button
    plotStyleButton;                 
  /** zoom magnification button */    
  private Button
    zoomBarsButton;
  /** flag: if false, don't add title or Close button */
  private boolean
    addButtonsFlag;            
     
    
  /**
   * ExprProfilePopup() - Constructor to popup expression profile window.
   * @param mae is the MAExplorer instance
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param font is the overide labels font if not null
   * @param preferredWidth is the canvas size, 0 to use defaults
   * @param preferredHeight,
   * @param drawLabelsFlag if true
   * @see EventMenu#promptFileName
   * @see ExprProfileCanvas#drawGifFile
   * @see ExprProfilePanel
   * @see SampleSets#showHP_E_assignmentsPopup
   * @see Util#nextZoomMag
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   * @see #getMinimumSize
   */
  ExprProfilePopup(MAExplorer mae, int mid, String title, Font font,
                   int preferredWidth, int preferredHeight,
                   boolean drawLabelsFlag)
  { /*ExprProfilePopup */
    super("ExprProfilePopup");
    
    this.mae= mae;
    if(preferredHeight==0 || preferredWidth==0)
    {
      Dimension
      mySize= getMinimumSize();
      preferredHeight= mySize.height;
      preferredWidth= mySize.width;
    }
    this.preferredHeight= preferredHeight;
    this.preferredWidth= preferredWidth;
    
    this.setLayout(new BorderLayout(0,0));
    
    /* Create a canvas to display the expression profile */
    eppl= new ExprProfilePanel(mae, mid, title,
                               font, /* overide labels font if not null*/
                               0, /* preferredWidth */
                               0, /* preferredHeight */
                               false, /* showKmeansDataFlag*/
                               drawLabelsFlag
                               );
    this.add(eppl,"Center");
    
    /* Create a bottom panel to hold buttons */
    Panel p= new Panel();
    p.setLayout(new FlowLayout(FlowLayout.LEFT, 6,1));
    this.add(p, "South");
    
    showErrBarsCheckBox= new Checkbox("Err",
    eppl.epc.showErrBarsFlag);
    showErrBarsCheckBox.setBackground(Color.white);
    showErrBarsCheckBox.addItemListener(this);
    p.add(showErrBarsCheckBox);
    
    zoomBarsButton= new Button(" 1X");
    zoomBarsButton.setBackground(Color.white);
    zoomBarsButton.addActionListener(this);
    p.add(zoomBarsButton);
    
    plotStyleButton= new Button("Line  ");
    plotStyleButton.setBackground(Color.white);
    plotStyleButton.addActionListener(this);
    p.add(plotStyleButton);
    
    /* Create the buttons and arrange to handle button clicks */
    Button showHPnames= new Button("Show HPs");
    showHPnames.addActionListener(this);
    showHPnames.setFont(font);
    p.add(showHPnames);
    
    if(!mae.isAppletFlag)
    { /* only if stand-alone */
      saveAsGIF= new Button("SaveAs");
      saveAsGIF.addActionListener(this);
      saveAsGIF.setFont(font);
      p.add(saveAsGIF);
    }
    
    Button close= new Button("Close");
    close.addActionListener(this);
    close.setFont(font);
    p.add(close);
    
    /* [CHECK] why clearing flag here? */
    mae.cdb.isValidObjFlag= false;
    
    this.setSize(preferredWidth, preferredHeight);
    
    this.addWindowListener(this);  /* listener for window events */
    
    this.setTitle(title);
    this.pack();
    
    /* Center frame on the screen, PC only */
    int
      maeWidth= mae.mbf.getSize().width,
      maeHeight= mae.mbf.getSize().height,
      xPos= ( maeWidth+20),
      yPos= ( maeHeight - preferredHeight )/2;
    this.setLocation(new Point(xPos,yPos));
    
    this.setVisible(true);
  } /* ExprProfilePopup */
  
  
  /**
   * getPreferredSize() - get the preferred size
   * @return preferred size of window
   */
  public Dimension getPreferredSize()
  { /*getPreferredSize*/
    return(new Dimension(preferredWidth,preferredHeight));
  } /* getPreferredSize */
  
  
  /**
   * getMinimumSize() - get the minimum preferred window size
   * and add on room for the buttons at the bottom.
   * @return minimum window size
   */
  public Dimension getMinimumSize()
  { /* getMinimumSize */
    int
      w= ExprProfileCanvas.MIN_CANVAS_WIDTH + (!mae.isAppletFlag ? 45 : 0), 
                                              /* add SaveAs button width*/
      h= (ExprProfileCanvas.MIN_CANVAS_HEIGHT+ EP_BUTTON_HEIGHT);
    return(new Dimension(w,h));
  } /* getMinimumSize */
  
  
  /**
   * updateData() - update expression profile with new gene data.
   * This will cause it to repaint as well.
   * @param mid is the Master Gene Index if not -1
   * @param title is the title for canvas label
   * @param drawLabelsFlag if true
   * @see ExprProfilePanel#updateData
   */
  void updateData(int mid, String title, boolean drawLabelsFlag )
  { /* updateData */
    eppl.updateData(mid,title,drawLabelsFlag);
  } /* updateData */
  
  
  /**
   * paint() - repaint buttons and canvas
   * @param g is graphics context
   * @see ExprProfilePanel#repaint
   */
  public void paint(Graphics g)
  { /* paint */
    eppl.repaint();
  } /* paint */
  
  
  /**
   * actionPerformed() - event handler for button clicks
   * @param e is button click event
   * @see EventMenu#promptFileName
   * @see ExprProfileCanvas#drawGifFile
   * @see SampleSets#showHP_E_assignmentsPopup
   * @see Util#nextZoomMag
   * @see Util#rmvFinalSubDirectory
   * @see Util#saveCmdHistory
   * @see #repaint
   */
  public void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    Button item= (Button)e.getSource();
    String cmd= e.getActionCommand();
    
    if (cmd.equals("Close"))
      quit();                              /* close window */
    
    else if(cmd.equals("Show HPs"))
    {
      Util.saveCmdHistory("Show list of HP-E samples", false);
      mae.hps.showHP_E_assignmentsPopup();
    }
    
    else if(cmd.equals("SaveAs"))
    { /* Save Canvas as GIF image */
      String
        defGifDir= Util.rmvFinalSubDirectory(mae.defDir,"Report", true),
        oGifFileName= mae.em.promptFileName("Enter GIF file name",
                                            defGifDir,
                                            "maeExprProfilePlot.gif",
                                            null,   /* sub dir */
                                            ".gif",
                                            true,   /* saveMode*/
                                            true    /* useFileDialog */
                                            );
      if(oGifFileName!=null)
      {
        Util.saveCmdHistory("Saved Expression Profile plot as "+oGifFileName,
                            false);
        eppl.epc.drawGifFile(oGifFileName);
      }
    }
    
    else if(zoomBarsButton==item)
    { /* zoom lines by 1X, 2X, 5X, 10X, 20X mod */
      eppl.epc.zoomBarsMag= Util.nextZoomMag(eppl.epc.zoomBarsMag,20);
      Util.saveCmdHistory("Setting Expression Profile zoom to "+
                          eppl.epc.zoomBarsMag+"X", false);
      zoomBarsButton.setLabel(eppl.epc.zoomBarsMag+"X");
      repaint();
    }
    
    else if(plotStyleButton==item)
    { /* zoom lines by 1X, 2X, 5X, 10X, 20X mod */
      eppl.epc.plotStyleMode= (eppl.epc.plotStyleMode+1)%3;
      String label= ((eppl.epc.plotStyleMode==0) 
                       ? "Line"
                       : ((eppl.epc.plotStyleMode==1)
                           ? "Circle" : "Curve"));
      Util.saveCmdHistory("Setting Expression Profile plot style to "+label, false);
      plotStyleButton.setLabel(label);
      repaint();
    }
  } /* actionPerformed */
  
  
  /*
   * itemStateChanged() - handle checkbox item state changed events.
   * NOTE: need to implement radio groups here since AWT only
   * implements radio groups for Checkboxes, and not for Checkbox MenuItems.
   * @param e is checkbox change event
   * @see Util#saveCmdHistory
   * @see #repaint
   */
  public void itemStateChanged(ItemEvent e)
  { /* itemStateChanged */
    Checkbox item= (Checkbox)e.getSource();
    if(showErrBarsCheckBox==item)
    {
      eppl.epc.showErrBarsFlag= item.getState();
      Util.saveCmdHistory("Expression Profile error bars are "+
      ((eppl.epc.showErrBarsFlag) ? "enabled" : "disabled"),
      false);
    }
    repaint();
  } /* itemStateChanged */
  
  
  /**
   * quit() - dispose of this object
   */
  void quit()
  { /* quit */
    this.dispose();
  } /* quit */
  
  
  /**
   * windowClosing() - closing down the window, get rid of the frame.
   * @param e is window closing event
   * @see #quit
   */
  public void windowClosing(WindowEvent e)
  {
    quit();
  }
  
  
  /*Others not used at this time */
  public void windowOpened(WindowEvent e) { }
  public void windowActivated(WindowEvent e) { }
  public void windowClosed(WindowEvent e) { }
  public void windowDeactivated(WindowEvent e) { }
  public void windowDeiconified(WindowEvent e) { }
  public void windowIconified(WindowEvent e) { }
  
  
}   /* end of class ExprProfilePopup */

