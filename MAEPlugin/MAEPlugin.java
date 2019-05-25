/** File: MAEPlugin.java */

package MAEPlugin;

import java.awt.event.*;
import java.awt.*;

/**
 * This abstract class defines the base MAEPlugin base class.
 *
 * Created on September 5, 2001, 5:26 PM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:21:30 $ / $Revision: 1.5 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see MaeJavaAPI
 * @see MAEStub
 */

public abstract class MAEPlugin extends Object implements PluginStub
{
  
  final public static boolean
    DBUG_MAEP= true;
  
  /** The human readable name for labels and such, the class names need to
   * be unique to identify the plugins
   */
  private String
    plugInName= "untitled";
  
  /** File name of the plugin without the ".jar" */
  private String
    plugInFileName= "MAEPlugin";
  
  /** GUI Elements */
  private java.awt.MenuItem
    menuItem;
  
  /** Instance registry for all sub classes of MAEPLugin
   * Works with constructors() and dispose() to maintain a
   * database of all plugins
   */  
  private static java.util.Hashtable
    registry;
  
  /** Set the initial and max size for the number of resgistry entries */
  public static final int
    registryMaxSize= 50;
  
  /* static initializer */
  static
  {
    MAEPlugin.registry= new java.util.Hashtable(registryMaxSize);
  }
  
  /** This interface must be implemented in the user's plugin code */
  private MAEStub
    maestub= null;
  
  /** Provides a hook into the mae Menubar */
  private MenuItem
    menuHook;
  
  /** Menu flag, activated from menuItem.
   * This flay is the state for this process, weither it is activated
   * or not.
   */
  private boolean
    menuFlag= false;
  
  /** menu insertion point name if not null.If null, then put into
   * PluginMenu hook
   */
  private String
    menuInsertionPointName= null;
  
  
  /** bean property for future use */
  private java.beans.PropertyChangeSupport
    propertySupport;
  
  /** access to the jar file for loading support files */
  protected JarResources
    jarResources= null;
  
  
  /**
   * MAEPlugin() - This private constructor prevents non-labeled and registered plugins
   */
  public MAEPlugin()   /* throws PluginException */
  { /* MAEPlugin */
    /* minimual bean support */
    propertySupport= new java.beans.PropertyChangeSupport( this );
  } /* MAEPlugin */
    
  
  /**
   * getInstance() - returns Registry instance of this class or new if not loaded
   * @return instance of MAEPlugin
   * @exception PluginException,
   * @exception InstantiationException
   * @exception IllegalAccessException
   */
  public final MAEPlugin getInstance() throws PluginException,
                                              InstantiationException,
                                              IllegalAccessException
  { /* getInstance */
    /*Check Registry */
    String type= this.getClass().getName();
    
    if ( registry.size()<= registryMaxSize )
    { /* generate unique name of the class type*/
      /* This avoids duplicate loaded plugins */
      if (!registry.containsKey(type))
      { /* not in registry so load it */
        registry.put(type, this.getClass().newInstance());
        System.out.println("type = "+type);
      }
    }
    else
      throw (new MaxNumberPluginsException());
    
    Object o = registry.get(type);
    if ((o != null)&&(o instanceof MAEPlugin))
      return (MAEPlugin)registry.get(type);
    else
      return (null);
  } /* getInstance */
  
  
  /**
   * MAEPlugin() - Constructor registers the plugin and sets up menu item.
   * @param menuLabel name of the plugin that will be used as MAExplorer menu item.
   */
  public MAEPlugin(String menuLabel)
  { /* MAEPlugin */
    this();
    
    if (menuLabel != null)
      plugInName= menuLabel;
  } /* MAEPlugin */
 
  
  /**
   * MAEPlugin() - Constructor registers the plugin and sets up menu item.
   * @param menuLabel name of the plugin that will be used as MAExplorer menu item.
   * @param pluginFileName name of the plugin without the ".jar"
   */
  public MAEPlugin(String menuLabel, String pluginFileName)
  { /* MAEPlugin */
    this();
    if (menuLabel != null)
      plugInName= menuLabel;
    if(pluginFileName!=null)
      plugInFileName= plugInFileName;
  } /* MAEPlugin */
 
  
  /**
   * removePlugin () - unloads plugin and removes it from the MAexplorer
   * menu.
   * @param o plugin object to be removed
   * @return true if succeed.
   * @see #pluginHalt
   */
  protected final static boolean removePlugin(MAEPlugin o)
  { /* removePlugin */
    String type = o.getClass().getName();
    
    /* clean up any resources this plugin may have consumed */
    try
    { /* calls plugn specific finalization routine */
      o.pluginHalt();
      
      /* calls standard finilizers. This will throw exception if failed. */
      o.finalize();
      
      registry.remove(type);
      return(!(registry.containsKey(type)));
    }
    catch (Throwable err)
    {
      return (false);
    }
  } /* removePlugin */
  
  
  /**
   * getNumberPlugins() - get the number of plugins (loaded).
   */
  public final static int getNumberPlugins()
  { return (registry.keySet().size()); }
  
  
  /**
   * getPlugins() - gets Registry list of MAEPlugins
   * @return enumeration list of MAEPlugins
   */
  public final static java.util.Enumeration getPlugins()
  { return(registry.elements()); }
  
  
  /**
   * getMAEStub() - get the menu stub for this instance of plugin
   * @return the MAEstub for this MAEPlugin
   */
  public final MAEStub getMAEStub()
  { return(this.maestub); }
  
  
  /**
   * setMAEStub() - get the menu stub for this instance of plugin
   * @param obj MAEstub to be registered for this MAEPlugin
   */
  public final void setMAEStub(MAEStub obj)
  { this.maestub= obj; }
  
  
  /**
   * getMenuItem() - get menu item for this instance of the plugin
   * @return menu item for this MAEplugin
   */
  public final java.awt.MenuItem getMenuItem()
  { return(this.menuItem); }
  
  
  /**
   * setMenuItem() - get menu item for this instance of the plugin
   * @param menuItem to be assigned for this MAEPlugin
   */
  public final void setMenuItem(java.awt.MenuItem menuItem)
  { this.menuItem= menuItem; }
  
  
  /**
   * getMenuHook() - get menu Item hook to put our instance of plugin
   * @return menu item hook for this MAEPlugin
   */
  public final MenuItem getMenuHook()
  { return(this.menuHook); }
  
  
  /**
   * setMenuHook() - set menu Item hook to put our instance of plugin
   * @param mi is the menu item hook to be assigned for this MAEPlugin
   */
  public final void setMenuHook(MenuItem mi)
  { this.menuHook= mi; }
  
  
  /**
   * setFlag() - set the menu status (CheckBox) menuFlag
   * @param b is the status to be assigned
   */
  public final void setFlag(boolean b)
  { this.menuFlag= b; }
  
  
  /**
   * getFlag() - get the menu status (CheckBox) menuFlag
   * @return the status to be assigned
   */
  public final boolean getFlag()
  { return(this.menuFlag); }
  
    
  /**
   * menuActivated() - this method calls the pluginMain() method in
   * GUI contexts.
   */
  public final void menuActivated()
  { pluginMain(); }
  
  
  /**
   * pluginInit() - Plugin init method. Code to initialize the plugin
   * at plugin Load goes here when it is installed in the menu.
   * The plugin writer may override this with their own method.
   */
  public void pluginInit() {  }
  
  
  /**
   * pluginMain() - abstract method end-users must implement to use
   * the API. It is called when the plugin is started from the menu.
   * The plugin writer may override this with their own method.
   */
  public abstract void pluginMain();
  
  
  /**
   * pluginHalt() - Plugin halt method. Code to execute if plugin in
   * disabled when the plugin is Unloaded - not when it is closed.
   * The plugin writer may override this with their own method.
   */
  public void pluginHalt() { }
  
  
  /**
   * getMenuInsertionPointName() - get the name of menu insertion point
   * If it is not defined, then it returns null that indicates the Plugins menu.
   * @return the menu Insertion Point name
   */
  public String getMenuInsertionPointName()
  { return (menuInsertionPointName); }
  
  
  /**
   * setMenuInsertionPointName() - set the name of menu insertion point.
   *<BR>
   * [NOT FULLY IMPLEMENTED]<BR>
   *<B>List of acceptable Menu stub names for: PluginMenuStubName</B>
   * (When enabled), you will be able to insert plugins into
   * specified parts of the MAExplorer menu. If the menu stub is not found,
   * it will install them in the generic "Plugin" pull-down menu.
   *<UL>
   * <LI> "FileMenu"
   * <LI> "FileMenu:Databases"
   * <LI> "FileMenu:State"
   * <LI> "FileMenu:Groupware"
   * <LI> "SampleMenu"
   * <LI> "GeneClassMenu"
   * <LI> "NormMenu"
   * <LI> "EditMenu"
   * <LI> "EditMenu:EGL"
   * <LI> "EditMenu:GeneSet"
   * <LI> "EditMenu:CondList"
   * <LI> "EditMenu:Preferences"
   * <LI> "FilterMenu"
   * <LI> "PlotMenu"
   * <LI> "PlotMenu:ScatterPlots"
   * <LI> "PlotMenu:Histogram"
   * <LI> "PlotMenu:EPplots"
   * <LI> "PlotMenu:PseudoArray"
   * <LI> "ClusterMenu"
   * <LI> "ClusterMenu:ClusterFlags"
   * <LI> "ReportMenu"
   * <LI> "ReportMenu:Genes"
   * <LI> "ReportMenu:Samples"
   * <LI> "ViewMenu"
   * <LI> "PluginMenu"
   * <LI> "HelpMenu"
   * </UL>
   * @param menuInsertionPointName
   */
  public void setMenuInsertionPointName(String menuInsertionPointName)
  { this.menuInsertionPointName= menuInsertionPointName; }
  
  
  /**
   * getPluginName() - This is the human readable name for use in menu labels
   * @return the plugInName.
   */
  public String getPluginName()
  { return (plugInName); }
  
  
  /**
   * getPluginFileName() - returns the plugin file name without the ".jar"
   * @return the plugInFileName.
   */
  public String getPluginFileName()
  { return (plugInFileName); }
  
  
  /**
   * getPluginDescription() - returns a human readable description of
   * the Plugin if any is defined by the plugin writer.
   * @return the plugin description.
   */
  public String getPluginDescription()
  { return ("<no description>"); }
  
  
  /**
   * setMenuLabel() - changes the label on the mennItem
   * @param s is the new menu item label
   */
  protected final void setMenuLabel(String s)
  {
    if(getMenuItem()!=null)
      getMenuItem().setLabel(s);
  }
  
  
  /**
   * setJarResources() - sets the JarResources object to get to
   * the contents of the Jar object.
   * @param obj is the jarResources to set.
   */
  public final void setJarResources(JarResources obj )
  { this.jarResources= obj; }
  
  
} /* end of class MAEPlugin */



