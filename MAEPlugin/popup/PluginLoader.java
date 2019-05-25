/** File: PluginLoader.java */

package MAEPlugin.popup;

import MAEPlugin.*;
import java.awt.*;
import java.io.*;

/**
 * This class contains the dynamic MAEplugin loader. 
 * It will load either .jar or .class files (the latter is useful for 
 * debugging since no jar packaging is required).
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file. This notice must be included with the code. The MAExplorer 
 * Mozilla and Legal files are available on http://maexplorer.sourceforge.net/.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2003/06/18 18:21:02 $ / $Revision: 1.5 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
public final class PluginLoader extends PopupPlugin implements FilenameFilter
{
  /** Maximum number of MAEPlugins that may be loaded at one time */
  final static int
    MAX_PLUGINS= 200;
  
  /** List of unique loaded plugins full path for ensuring uniqueness */
  static String
    pluginPathsList[];
  /** Size of list of unique loaded plugins full path for ensuring uniqueness */
  static int
    nPluginPathsList;
  
  static
  { /* initialize list of all plugins */
    pluginPathsList= new String[MAX_PLUGINS];
    nPluginPathsList= 0;
  }
  
  /** Reference to the Dialog that will load files */
  java.awt.FileDialog
    fileDialog=null;
  
  /** Reference to the plain name of the class to load */
  String
    pluginClassName;
  
  /** Reference to the Object itself */
  MAEPlugin
    pluginObject;
  
  
  /**
   * PluginLoader() - Creates new LoadPlugins.
   * @param stub is the plugin stub
   * @throws PluginException
   */
  public PluginLoader(MAEStub stub) throws PluginException
  { /* PluginLoader */
    super("Load Plugins");
    setMAEStub(stub);
  } /* PluginLoader */
  
  
  /**
   * PluginLoader() - Creates new LoadPlugin but do it siliently.
   * @param stub is the plugin stub
   * @throws PluginException
   */
  public PluginLoader(MAEStub stub, String pluginJarPath) throws PluginException
  { /* PluginLoader */
     loadPluginSilently(pluginJarPath);
     mountPlugin();
    //setMAEStub(stub);
  } /* PluginLoader */
  
  
  /**
   * mountPlugin() - loads the plugin into the MAE application.
   * These must be instances of class MAEplugin.
   * @return false if plugin mount fails
   */
  private final boolean mountPlugin()
  { /* mountPlugin */
    //System.out.print(this.getMAEStub());
    if((this.pluginObject!=null) && (this.pluginObject instanceof MAEPlugin) &&
       (this.getMAEStub()!=null))
    { /* load the plugin */
      this.getMAEStub().insertPluginMenu( this.pluginObject.getMenuItem() );
      this.pluginObject.setMAEStub(this.getMAEStub());
      return(true);
    }
    else
      return(false);
  } /* mountPlugin */
  
  
  /**
   * loadPlugin() - tests and loads good MAEplugin.
   * Called in pluginMain() for GUI or in loadPlugin().
   * @param dir the path to the plugin file
   * @param fileName the name of the plugin .jar or .class file
   * @return false if plugin load fails
   */
  private final boolean loadPlugin(String dir, String fileName)
  { /* loadPlugin */
    this.getMAEStub().showMsg("Preparing to load Plugin");
    if (fileName == null )
    { /* [TODO] add code to load all plugins in a directory */
      this.getMAEStub().showMsg("No Plugin file :"+ fileName);
      return(false);
    }
    
    if (dir == null )
    { /* [to do] add code to load from default plugin dir */
      this.getMAEStub().showMsg("No Plugin directory :"+ dir);
      return(false);
    }
    
    this.getMAEStub().showMsg("Looking for Plugin file ="+
    dir+fileName);
    
    return(loadPlugin(dir+fileName));
  } /* loadPlugin */
    
  
  /**
   * loadPluginSilently() - silently tests and loads good MAPlugin from the path.
   * @param path to search for plugin
   * @return true if successfuly loaded the plugin
   */
  public final boolean loadPluginSilently(String path)
  {  return(loadPlugin(path)); }
  
  
  /**
   * loadPlugin() - tests and loads good MAPlugin from the path.
   * @param path to search for plugin
   * @return true if successfuly loaded the plugin
   */
  private final boolean loadPlugin(String path)
  { /* loadPlugin */
    String classFile= "";
    if(path==null)
      return(false);
    
    /* Test if plugin is already loaded, in which case unload the
     * old copy before loading the new one.
     */
    for(int i=0;i<nPluginPathsList;i++)
      if(pluginPathsList[i].equals(path))
      { /* found it - go unload it before reload it */
        /* [TODO] lookup plugin in the Plugins DB and then unload it... */
        //PluginUnLoader.unMountPlugin(pluginMenuNameString);
        break;
      }
    
    /* Push the plugin path into the unique list of plugins */
    pluginPathsList[nPluginPathsList++]= path;
    
    /* Load it */
    try
    { /* test and load plugin */
      /* test to see we have a valid dir and no inner classes */
      if ((path!=null) && (path.indexOf('$') < 0) &&
          (path.indexOf(".jar")>0 || path.indexOf(".class")>0))
      { /* load plugin as .jar or .class file */
        /* get File handle to dir */
        File file= new File(path);
        
        /* test File and if java class attempt to load */
        if(file.getName()!=null && file.exists() && file.canRead())
        {
          Class C= null;
          Object O= null;
          FileClassLoader CLC= (path.indexOf(".class") > 0)
                                  ? new FileClassLoader(file) : null;
          JarClassLoader CLJ= (path.indexOf(".jar") > 0)
                                 ? new JarClassLoader(file) : null;
          /* process out a classFile name for use later */
          classFile= file.getName();
          classFile= classFile.substring(0, classFile.lastIndexOf('.'));
          
          /* current error stop */
          this.getMAEStub().showMsg("Loading Plugin");
          if(CLC!=null)
            C= CLC.loadClass(classFile);
          else if(CLJ!=null)
            C= CLJ.loadClass(classFile);
          
          /* instantiate object */
          if((C != null) && (MAEPlugin.class.isAssignableFrom(C)))
          {
            this.getMAEStub().showMsg("Mounting Plugin");
            java.lang.reflect.Constructor  con= C.getConstructor(null);
            
            /* instantiate object */
            O= con.newInstance(null);
            if (O instanceof MAEPlugin)
            { /* set plugin in object */
              if(CLC!=null)
              { /* Class file object */
                this.pluginObject = (MAEPlugin)O;
              }
              if(CLJ!=null)
              { /* JAR object */
                this.pluginObject= ((MAEPlugin)O).getInstance();
                this.pluginObject.setJarResources(CLJ.getJarResources());
              }
            } /* set plugin in object */
            
            this.getMAEStub().showMsg("Plugin "+O.getClass()+" loaded.");
            return(true);
          }
        } /* load plugin as .jar or class file */
        else
        {
          this.getMAEStub().showMsg("Plugin file does not exist or is unreachable");
          return(false);
        }
      } /* load plugin */
    } /* test and load plugin */
    
    catch (SecurityException e)
    {
      this.getMAEStub().showMsg("Security Exception");
      return(false);
    }
    catch (InstantiationException e)
    {
      this.getMAEStub().showMsg("Plugin could not be instantiated.");
      return(false);
    }
    catch (IllegalAccessException e)
    {
      this.getMAEStub().showMsg("PluginI/O error: access denied.");
      return(false);
    }
    catch (ClassNotFoundException e)
    {
      this.getMAEStub().showMsg("Class not Found: "+classFile);
      return(false);
    }
    catch (NoClassDefFoundError er)
    {
      this.getMAEStub().showMsg("Class not Found: "+path);
      return(false);
    }
    catch (java.lang.reflect.InvocationTargetException e)
    { /* this is thrown if constructor throws an exception */
      if ( e.getTargetException() instanceof MaxNumberPluginsException)
      {
        this.getMAEStub().showMsg("Maximum number of "+
                                  MAEPlugin.registryMaxSize+
                                  " plugins reached");
      }
      else if ( e.getTargetException() instanceof DuplicatePluginLoadException)
      {
        this.getMAEStub().showMsg("Plugin already loaded");
      }
      else
      {
        this.getMAEStub().showMsg("Plugin unloadable");
      }
      return (false);
    }
    catch (Exception e)
    {
      this.getMAEStub().showMsg("Exception: "+path+" "+e);
      e.printStackTrace();
      return (false);
    }
    catch (Throwable er)
    {
      this.getMAEStub().showMsg("Throwable: "+path);
      er.printStackTrace();
      return (false);
    }
    finally
    {
      //this.getMAEStub().showMsg3("*PluginLoader-finally"));
    }
    return(false);
  } /* loadPlugin */
  
  
  /**
   * accept() - implentation of FilenameFilter.
   * Does not work and is never called in 1.1.2 and as as far as I
   * can tell in 1.3 [see "Class Libraries" p. 603]
   * @param dir is the diredtory to browse
   * @param name is the file to test for extensions
   * @return true if accept the file.
   */
  public boolean accept(File dir, String name)
  { /* accept */
    if (name!= null)
      return( (name.endsWith(".zip") || name.endsWith(".jar")) );
    else
      return(false);
  } /* accept */
  
  
  /**
   * pluginMain() - Is the method end-users implement to use the API.
   */
  public void pluginMain()
  { /* pluginMain */
    if (fileDialog == null)
    {
      fileDialog= new FileDialog(getMAEStub().getFrame(),
                                 "Select Plugins to load",
                                 FileDialog.LOAD);
      
      String
        fileSep= System.getProperty("file.separator"),
        userDir= System.getProperty("user.dir"),
        pluginDir= userDir + fileSep + "Plugins";
      fileDialog.setDirectory(pluginDir);
      
      //fileDialog.setFile(initialFile);
      
      /* set FileDialog to Filename filter */
      //fileDialog.setFilenameFilter(new PluginFilenameFilter());
      fileDialog.setFilenameFilter(this);
    }
    
    fileDialog.show();
    
    /* get results from dialog. Test, load and mount good plugin name */
    if ( loadPlugin(fileDialog.getDirectory(), fileDialog.getFile()))
    {
      mountPlugin();
    }
    else
    {
      /* System.out.println("Can not find Plugin File: \""+
       *                     fileDialog.getDirectory()+ fileDialog.getFile());
       */
    }
  } /* pluginMain */
  
  
  
  /**
   * getPluginDescription() - This returns a human readable description of the Plugin
   * @return plugin description if any.
   */
  public String getPluginDescription()
  { /* getPluginDescription */
    return ("Plugin Loader, JE/CIT 2001. Loads plugins anywhere anytime.");
  }
  
  /* getPluginDescription */
  
  
  /**
   * updateCurGene() - update any data since current gene has changed.
   * This is invoked from the PopupRegistry
   * @param ml is the gene list to update now that filter has changed.
   */
  public void updateCurGene(int mid)  { }
  
  
  /**
   * updateFilter() - update any dependent data since Filter has changed.
   * This is invoked from the PopupRegistry
   */
  public void updateFilter()  { }
  
  
  /**
   * updateSlider() - update any dependent data sincea threshold slider has changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateSlider()  { }
  
  
  /**
   * updateLabels() - update any dependent data since global labels have changed.
   * This is invoked from the PopupRegistry.
   */
  public void updateLabels()  { }
  
} /* PluginLoader */



