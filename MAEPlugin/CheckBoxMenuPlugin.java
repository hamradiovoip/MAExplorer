/** File: CheckBoxMenuPlugin.java */

package MAEPlugin;

import java.awt.*;
import java.awt.event.*;

/**
 * This class extends the MAEPlugin base class to implement a CheckBoxMenuPlugin base class.
 * 
 * Created on September 10, 2001, 7:59 AM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:21:31 $ / $Revision: 1.5 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public abstract class CheckBoxMenuPlugin extends MAEPlugin implements ItemListener
{
  
  /**
   * CheckBoxMenuPlugin() - constuctor with user supplied label
   */
  public CheckBoxMenuPlugin(String label)
  { /* CheckBoxMenuPlugin */
    super(label);
    this.setMenuItem(new CheckboxMenuItem(label));
    ((CheckboxMenuItem)this.getMenuItem()).addItemListener(this);
  } /* CheckBoxMenuPlugin */
  
  
  /**
   * CheckBoxMenuPlugin() - constuctor with user supplied label and plugin filename
   * @param menuLabel is the name of the plugin for the menu entry.
   * @param pluginFileName name of the plugin without the ".jar"
   */
  public CheckBoxMenuPlugin(String menuLabel, String pluginFileName)
  { /* CheckBoxMenuPlugin */
    super(menuLabel, pluginFileName);
    this.setMenuItem(new CheckboxMenuItem(menuLabel));
    ((CheckboxMenuItem)this.getMenuItem()).addItemListener(this);
  } /* CheckBoxMenuPlugin */
  
  
  /**
   * CheckBoxMenuPlugin() - constuctor to use default "CheckBoxPlugin" label name
   */
  public CheckBoxMenuPlugin()
  { /* CheckBoxMenuPlugin */
    this("CheckBoxPlugin");
  } /* CheckBoxMenuPlugin */
  
  
  /**
   * itemStateChanged() - implements ItemListener for checkbox change
   */
  public void itemStateChanged(ItemEvent e) // was final
  { /* itemStateChanged */
    setFlag( !getFlag() );   /* toggle the checkbox menu item */
    
    menuActivated();  /* Update the menu */
  } /* itemStateChanged */
  
  
  /**
   * setState() - utility to set the checkbox state
   */
  public void setState(boolean b)
  { /* setState */
    ((CheckboxMenuItem)(getMenuItem())).setState(b);
    setFlag(b);
  } /* setState */
  
  
  /**
   * getState() - utility to get the checkbox state
   */
  public boolean getState()
  { return( ((CheckboxMenuItem)(getMenuItem())).getState() ); }
  
  
  /**
   * pluginMain() - abstract the method end-users implement to use the API
   */
  public abstract void pluginMain();
  
} /* end of class CheckBoxMenuPlugin */

