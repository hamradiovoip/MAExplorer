/** File: PluginUnLoader.java */

package MAEPlugin.popup;

import MAEPlugin.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;

import java.util.*;

/**
 * This class contains the dynamic MAEplugin unloader for .jar files
 *<P>
 * This work was produced by Peter Lemkin of the National Cancer
 * Institute, an agency of the United States Government.  As a work of
 * the United States Government there is no associated copyright.  It is
 * offered as open source software under the Mozilla Public License
 * (version 1.1) subject to the limitations noted in the accompanying
 * LEGAL file.
 *<P>
 * @author Jai Evans (DECA/CIT), C. Santos (DECA/CIT), P. Lemkin (NCI-FCRDC)
 * @version $Date: 2002/10/13 17:39:34 $ / $Revision: 1.3 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

public final class PluginUnLoader extends PopupPlugin
{
  
   /* [ please LEAVE this code as is until further notice.
    * I may want to add to it. [JE]] */
  
  /** Reference to the Dialog that will load files */
  UnloaderDialog
    unloaderDialog=null;
  
  /** Reference to the plain name of the class to load */
  String
    pluginClassName;
  
  /** Reference to the Object itself */
  MAEPlugin
    pluginObject;
  
  /** Reference to MAE Stub */
  MAEStub
    maestub;
  
  /** Hashtable used to remove plugs */
  Hashtable
    pHashtable = new Hashtable();
  
  /**
   * PluginLoader() - Creates new LoadPlugins.
   */
  public PluginUnLoader(MAEStub stub)
  {
    super("Unload Plugin(s)");
    this.maestub = stub;
  }
  
  
  /**
   * unMountPlugin() - unloads the plugin from the MAE application.
   * These must be MAEplugins.
   * @param name of plugin to unmount
   * @return true if succeed
   */
  private final boolean unMountPlugin(String name)
  { /* mountPlugin */    
    this.pluginObject = lookUp(name);
    
    if((this.pluginObject != null) &&
       (this.pluginObject instanceof MAEPlugin) && (this.maestub!=null))
    {
      this.maestub.removePluginMenu(this.pluginObject.getMenuItem());
      return(MAEPlugin.removePlugin(this.pluginObject));
    }
    else
    {
      return(false);
    }
  } /* mountPlugin */
  
  
  /**
   * lookUp() - finds plugin in the loaded list from MAExplorer
   * @param name of MAEPlugin to lookup.
   * @return instance of MAEPlugin if succeed, else return null.
   */
  private final MAEPlugin lookUp(String name)
  { /* lookUp */
    Object o = pHashtable.get(name);
    if ((o!= null)&&(o instanceof MAEPlugin))
      return(MAEPlugin)o;
    else
      return (null);
  } /* lookUp */
  
  
  /**
   * pluginMain() - Is the method end-users implement to use the API.
   */
  public void pluginMain()
  { /* pluginMain */
    if (unloaderDialog == null)
    {
      unloaderDialog = new UnloaderDialog(maestub.getFrame(),
                                          "Select Plugins to unload");
    }
    
    unloaderDialog.show();
  } /* pluginMain */
  
  
  /**
   * getPluginDescription() - This returns a human readable description of the Plugin
   */
  public String getPluginDescription()
  { /* getPluginDescription */
    return ("Plugin unLoader, JE/CIT 2001. un-Loads plugins anywhere anytime.");
  }
  
  
  /**
   * Class UnloaderDialog is used to select loaded plugins to unload
   */
  final class UnloaderDialog extends Dialog
  {
    private PluginList 
      pList= new PluginList();
    private Button
      bUnload= new Button("Unload");
    private Button
      bDone= new Button("Done");
    
    {
      Dimension ctr = getToolkit().getScreenSize();
      this.setSize(500,300);
      setLocation((ctr.width-500)/2, (ctr.height-300)/2);
    }
    
    
    /**
     * UnloaderDialog() - constructor
     * @param mother frame to use
     */
    public UnloaderDialog(Frame mother )
    { this(mother, "Select Plugin to Unload", true); }
    
    
    /**
     * UnloaderDialog() - constructor
     * @param mother frame to use
     * @param msg to use in prompt
     */
    public UnloaderDialog(Frame mother, String msg)
    { this(mother, msg, true); }
    
    
    /**
     * UnloaderDialog() - constructor
     * @param mother frame to use
     * @param msg to use in prompt
     * @param modal use modeal dialog
     */
    public UnloaderDialog(Frame mother, String msg, boolean modal)
    { /* UnloaderDialog */
      super(mother, msg, modal);
      setModal(true);
      
      /* build interface */
      /* layout */
      setLayout(new BorderLayout());
      Panel bPanel = new Panel();
      
      /* add GUI elements */
      bPanel.add(bUnload);
      bPanel.add(bDone);
      add(bPanel, "South");
      add(pList, "Center");
      
      /* attach controlers */
      addWindowListener(new WindowAdapter()
      {
        public void windowClosing(WindowEvent evt )
        { hide(); }
      });
      
      bDone.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        { hide(); }
      });
      
      bUnload.addActionListener(new ActionListener()
      {
        public void actionPerformed(ActionEvent evt)
        {
          setEnabled(false);
          doUnMountPlugin();
          setEnabled(true);
        }
      });
      refreshLists();
    } /* UnloaderDialog */
    
    
    /**
     * show() - necessary override to allow refresh of hash and lists
     * in window
     */
    public void show()
    {
      refreshLists();
      super.show();
    }
    
    
    /**
     * refreshLists() - called whenever the state of laded plugins needs to be refreshed.
     */
    private void refreshLists()
    { /* refreshLists */
      /* clear lists */
      pList.clearLists();
      
      /* populate list  and make hash to do lookups */
    Enumeration oList = getPlugins();
      pHashtable.clear();
      while( oList.hasMoreElements())
      {
        /* get each element */
        MAEPlugin
        o = (MAEPlugin)oList.nextElement();
        
        /* prevent unloading of the loaders */
        if (!(o instanceof PluginLoader) && !(o instanceof PluginUnLoader))
        {
          /* get menu label */
          String item = o.getMenuItem().getLabel();
          
          /* add to human readable list */
          pList.add(item);
          
          /* add to hash */
          pHashtable.put(item, o);
        }
      }
    } /* refreshLists */
    
    
    /**
     * doUnMountPlugin() - Begin the process of unloading the plugins in question
     */
    public void doUnMountPlugin()
    { /* doUnMountPlugin */
      String
        rmList[]= pList.getRmList(),
        item = null;
      for (int i=0; i<rmList.length; i++ )
      {
        item = rmList[i].toString();
        if (unMountPlugin(item))
        {
          pList.remove( item );
        }
      }
      refreshLists();
    } /* doUnMountPlugin */
    
    
    /**
     * class PluginList was modified from Chan P. et al
     * The Java Class Libraries 2nd Ed.,
     * Addison Wesley 1997 pp.986-988
     */
    class PluginList extends Panel implements ActionListener,ItemListener
    {
      
      final static int ITEMS = 10;
      java.awt.List
      ltList= new java.awt.List(ITEMS, true),
      rtList = new java.awt.List(0, true);
      
      /**
       * PluginList() - constructor
       **/
      PluginList()
      { /* PluginList */
        GridBagLayout gbl = new GridBagLayout();
        
        setLayout(gbl);
        add(new Label("Loaded Plugins"), 0, 0, 1, 1, 0, 1.0);
        add(new Label("Plugins to unload"), 2, 0, 1, 1, 0, 1.0);
        
        add(ltList, 0, 1, 1, 5, 1.0, 1.0);
        add(rtList, 2, 1, 1, 5, 1.0, 1.0);
        // Add action and item listeners to list
        ltList.addActionListener(this);
        ltList.addItemListener(this);
        rtList.addActionListener(this);
        rtList.addItemListener(this);
        
        // Create buttons for adding/removing items from lists
        Button b;
        add(b = new Button(">"), 1, 1, 1, 1, 0, 1.0);
        b.addActionListener(this);
        add(b = new Button(">>"), 1, 2, 1, 1, 0, 1.0);
        b.addActionListener(this);
        add(b = new Button("<"), 1, 3, 1, 1, 0, 1.0);
        b.addActionListener(this);
        add(b = new Button("<<"), 1, 4, 1, 1, 0, 1.0);
        b.addActionListener(this);
        add(b = new Button("!"), 1, 5, 1, 1, 0, 1.0);
        b.addActionListener(this);
      } /* PluginList */
      
      
      public void add(String item)
      { ltList.add(item); }
      
      
      public void remove(String item)
      { rtList.remove(item); }
      
      
      public void clearLists()
      {
        ltList.removeAll();
        rtList.removeAll();
      }
      
      public String [] getRmList()
      { return (rtList.getItems()); }
      
      
      /**
       * add() - add component
       */
      void add(Component comp, int x, int y, int w, int h,
               double weightx, double weighty)
      { /* add */
        GridBagLayout gbl = (GridBagLayout)getLayout();
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = x;
        c.gridy = y;
        c.gridwidth = w;
        c.gridheight = h;
        c.weightx = weightx;
        c.weighty = weighty;
        add(comp);
        gbl.setConstraints(comp, c);
      } /* add */
      
      
      /**
       * reverseSelections() -
       */
      void reverseSelections(java.awt.List l)
      { /* reverseSelections */
        for (int i=0; i<l.getItemCount(); i++)
        {
          if (l.isIndexSelected(i))
          {
            l.deselect(i);
          }
          else
          {
            l.select(i);
          }
        }
      } /* reverseSelections */
      
      
      /**
       * deselectAll() -
       */
      void deselectAll(java.awt.List l)
      {
        for (int i=0; i<l.getItemCount(); i++)
        {
          l.deselect(i);
        }
      }
      
      
      /**
       * replaceItem() -
       */
      void replaceItem(java.awt.List l, String item)
      {
        for (int i=0; i<l.getItemCount(); i++)
        {
          if (l.getItem(i).equals(item))
          {
            l.replaceItem(item + "*", i);
          }
        }
      }
      
      
      /**
       * move() -
       */
      void move(java.awt.List l1, java.awt.List l2, boolean all)
      {
        if (all)
        {
          for (int i=0; i<l1.getItemCount(); i++)
          {
            l2.add(l1.getItem(i));
          }
          l1.removeAll();
        }
        else
        {
          String[] items = l1.getSelectedItems();
          int[] itemIndexes = l1.getSelectedIndexes();
          
          deselectAll(l2);
          for (int i=0; i<items.length; i++)
          {
            l2.add(items[i]);        // add it
            l2.select(l2.getItemCount()-1);// and select it
            if (i == 0)
            {
              l2.makeVisible(l2.getItemCount()-1);
            }
          }
          for (int i=itemIndexes.length-1; i>=0; i--)
          {
            l1.remove(itemIndexes[i]);
          }
        }
      }
      
      
      /**
       * actionPerformed() -
       */
      public void actionPerformed(ActionEvent evt)
      {
        String arg = evt.getActionCommand();
        if (">".equals(arg))
        {
          move(ltList, rtList, false);
        }
        else if (">>".equals(arg))
        {
          move(ltList, rtList, true);
        }
        else if ("<".equals(arg))
        {
          move(rtList, ltList, false);
        }
        else if ("<<".equals(arg))
        {
          move(rtList, ltList, true);
        }
        else if ("!".equals(arg))
        {
          if (ltList.getSelectedItems().length > 0)
          {
            reverseSelections(ltList);
          }
          else if (rtList.getSelectedItems().length > 0)
          {
            reverseSelections(rtList);
          }
        }
        else
        {
          Object target = evt.getSource();
          if (target == rtList || target == ltList)
          {
            replaceItem((java.awt.List)target, arg);
          }
        }
      }
      
      
      /**
       * itemStateChanged() -
       */
      public void itemStateChanged(ItemEvent evt)
      {
        java.awt.List target = (java.awt.List)evt.getSource();
        if (target == ltList)
        {
          deselectAll(rtList);
        }
        else if (target == rtList)
        {
          deselectAll(ltList);
        }
      }
    }
    
  }
  
  
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
  
  
  /**
   * close() - close the plugin
   * @param preserveDataStructuresFlag to save data structures
   */
  public void close(boolean preserveDataStructuresFlag)  { }
} /* PluginUnLoader */
