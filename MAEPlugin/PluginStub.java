/** File: PluginStub.java */

package MAEPlugin;

/**
 * Enforces method implementations in suport of MAEPlugin.
 * Provides bean-like interface for the plugins.
 *
 * Created on September 6, 2001, 8:28 AM
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/03/03 16:21:29 $ / $Revision: 1.5 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public interface PluginStub
{
  
  /** MAEPlugin version # for tracking */
  public final float PLUGIN_VERSION= 1.0F;
  
  
  /**
   * menuActivated() - method called when menu is clicked for menu driven plugins.
   */
  public void menuActivated();
  
  
  /**
   * pluginInit() - Plugin init method.
   * Code to initialize the plugin data structures etc. at load time
   * goes here.
   */
  public void pluginInit();
  
  
  /**
   * pluginHalt() - Plugin halt method. Code to execute if plugin is disabled.
   */
  public void pluginHalt();
  
  
  /**
   * pluginMain() - this is the method end-users implement to use the API.
   */
  public void pluginMain();
  
  
  /**
   * getPluginName() - returns the human readable name for use in labels.
   */
  public String getPluginName();
  
  
  /**
   * getPluginFileName() - returns the plugin file name without the ".jar"
   */
  public String getPluginFileName();
  
  
  /**
   * getPluginDescription() - returns a human readable description of the Plugin.
   */
  public String getPluginDescription();
  
  
  /**
   *  getInstance - returns the isntance of this plugin, and
   * registration.
   */
  ///public PluginStub getInstance();
  
  
} /* end of class PluginStub */

