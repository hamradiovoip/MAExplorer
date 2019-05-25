/** File: MenuPlugin.java */

package MAEPlugin;

import java.awt.event.*;

/**
 * This abstract class defines MenuPlugin class.
 * 
 * Created on September 10, 2001, 8:00 AM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/02/21 16:30:17 $ / $Revision: 1.3 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public abstract class MenuPlugin extends MAEPlugin 
                implements java.awt.event.ActionListener 
{
  
  /**
   * MenuPlugin() - constructor for menu. Here we add in actionListeners
   * to the menu item.
   * @param label to assign to the plugin and menu item label
   */
  public MenuPlugin(String label)
  { /* MenuPlugin */
    super(label);
    setMenuItem(new java.awt.MenuItem(label));
    getMenuItem().addActionListener(this);
  } /* MenuPlugin */
  
  
  /**
   * MenuPlugin() - constructor to override to implement the plugin
   */
  public MenuPlugin()
  { /* MenuPlugin */
    /* if not defined here it is the class name */
    this(null);
  } /* MenuPlugin */
  
  
  /**
   * actionPerformed() - actionListener
   * @param e is the ActionEvent
   */
  public final void actionPerformed(ActionEvent e)
  { /* actionPerformed */
    menuActivated();
  }
  
  
  /**
   * pluginMain() - abstract the method end-users implement to use
   * the API
   */
  public void pluginMain()  { }
  
} /* end of class MenuPlugin */

