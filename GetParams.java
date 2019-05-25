/** File: GetParams.java */

import java.applet.Applet;
import java.awt.*;
import java.applet.*;
 
/** 
 * This class get the argument list from the Applet <PARAM> or .mae startup file.
 * Which is used depends on whether the execution instance is an Applet or not.
 * Arguments are read from a startup file with a .mae file extension in 
 * the case of a stand-alone application. It will first set the defaults
 * from the Config class that reads the MaExplorerConfig.txt Configuration file.
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
 * @version $Date: 2004/01/13 16:45:02 $   $Revision: 1.8 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class GetParams
{
   /** link to global MAExplorer instance */
   private static MAExplorer 
    mae;                  
  
 
   /**
    * GetParams() - constructor
    * @param mae is instance of MAExplorer
    */
   GetParams(MAExplorer mae)
   { /* GetParams */
     this.mae= mae;
   } /* GetParams */
   
   
   /**
    * getParamVal() - get parameter from Configuration database file
    * @param param is the parameter name to search for
    * @return value if found, else null
    * @see ConfigTable#getValueByName
    */
   String getParamVal(String param)
   { /* getParamVal */
     if(mae.cfgDB==null || !mae.cfgDB.hasValidData)
       return(null);   /* no-op */
     
     String value= mae.cfgDB.getValueByName(param);
     
     return(value);
   } /* getParamVal */
   
   
   /**
    * getSAParam() - get parameter from applet or standalone application
    * startup database Table previously read from file.
    * @param tag is name of parameter to search for
    * @return value if found, else null.
    * @see UserState#getMAEstartupValue
    */
   String getSAParam(String tag)
   { /* getSAParam */
     String value= (mae.isAppletFlag)
                     ? mae.getParameter(tag)
                     : mae.us.getMAEstartupValue(tag);
     
     return(value);
   } /* getSAParam */
   
   
   /**
    * getNumberedParamList() - get a numbered parameter list
    * The parameters are of the form:
    *  "image1", "image2", ...
    * with integers starting at 1 appended to the right of the prefix.
    * Return a String[0:nItems] if any elements exist, else return null
    * @param prefix to serach for
    * @return list of values if found, else null.
    * @see #getParamVal
    */
   String[] getNumberedParamList(String prefix)
   { /* getNumberedParamList */
     int
       i= 0,
       nItems= 0;
     String
       sName,
       sVal,
       sList[];
     
     while(true)
     { /* count # of PARAMS with the numbered prefix */
       sName= prefix + (++i);
       sVal= getParamVal(sName);
       if(sVal==null)
         break;             /* no more */
       ++nItems;            /* count it */
     }
     
     if(nItems==0)
       return(null);
     sList= new String[nItems];
     
     for(i=1;i<=nItems;i++)
     { /* get the value for the PARAM name */
       sName= prefix + i;
       sVal= getParamVal(sName);
       sList[i-1]= sVal;              /* push it */
     }
     
     return(sList);
   } /* getNumberedParamList */
   
   
   /**
    * setDefParam() - set value or default if <APPLET> <PARAM NAME=tag VALUE=val>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getParamVal
    * @see #getSAParam
    */
   boolean setDefParam(String tag, boolean defaultVal)
   { /* setDefParam */
     String
       param= getParamVal(tag),       /* get default from Config file*/
       paramApplet= getSAParam(tag);  /* get from applet or standalone */
     
     if(paramApplet!=null)
       param= paramApplet;            /* overide if defined */
     
     if(param==null)
       return(defaultVal);
     else
       return(param.equalsIgnoreCase("TRUE"));
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set value or default if <APPLET> <PARAM NAME=tag VALUE=val>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getParamVal
    * @see #getSAParam
    */
   Color setDefParam(String tag, Color defaultVal)
   { /* setDefParam */
     String
       param= getParamVal(tag),        /* get from Config file*/
       paramApplet= getSAParam(tag);   /* get from applet or standalone */
     
     if(param==null)
       param= paramApplet;
     
     Color 
       val= defaultVal,
       color[]= { Color.black, Color.white, Color.red,
                  Color.orange, Color.yellow, Color.green,
                  Color.blue, Color.cyan, Color.magenta,
                  null
                };
     String sColor[]= { "black", "white", "red", "orange",
                        "yellow", "green", "blue", "cyan",
                        "magenta", null
                      };
     if(param==null)
       return(defaultVal);
     else for(int i=0; sColor[i]!=null; i++)
       if(param.equalsIgnoreCase(sColor[i]))
         val= color[i];
     
     return(val);
   } /* setDefParam */
   
   
   /**
    * getConfigAndPARAMvalue() - get Config then <APPLET> <PARAM> val for tag
    * 1. First: get values from stand-alone .mae file if defined.
    *    NAME=tag VALUE=val
    * 2. Else second: get from Applet <PARAM> if it exists
    *    <APPLET> <PARAM NAME=tag VALUE=val>
    * 3. Else third: get it from the configuration file.
    *    Config/MaExplorerConfig.txt file.
    * @param tag is the parameter name to search for
    * @return value if found, else defaultVal
    * @see #getParamVal
    * @see #getSAParam
    */
   String getConfigAndPARAMvalue(String tag)
   { /* getConfigAndPARAMvalue */
     String param= getSAParam(tag);   /* get from standalone or applet */
     
     if(param==null)
       param= getParamVal(tag);  /* get from Config file*/
     
     return(param);
   } /* getConfigAndPARAMvalue */
   
   
   /**
    * setDefParam() - set value or default value if <APPLET> <PARAM NAME=tag VALUE=val>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    <APPLET> <PARAM NAME=tag VALUE=val>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    */
   int setDefParam(String tag, int defaultVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);
     
     if(param==null)
       return(defaultVal);
     
     int val;
     try
     { val= java.lang.Integer.parseInt(param);}
     catch(NumberFormatException e)
     { val= defaultVal;}
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set int value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @param minVal is minimum allowable value
    * @param maxVal is maximum allowable value
    * @return value if found, else defaultVal
    * @see #setDefParam
    */
   int setDefParam(String tag, int defaultVal, int minVal, int maxVal)
   { /* setDefParam */
     int val= this.setDefParam(tag,(minVal-1));
     if(val<minVal || val>maxVal)
       val= defaultVal;	/* overide it */
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set float value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @param minVal is minimum allowable value
    * @param maxVal is maximum allowable value
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    */
   float setDefParam(String tag, float defaultVal, float minVal, float maxVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);
     Float valP;
     float val;
     
     if(param==null)
       return(defaultVal);
     try
     {
       valP= new Float(param);
       val= valP.floatValue();
     }
     catch(NumberFormatException e)
     { val= defaultVal;}
     if(val<minVal || val>maxVal)
       val= defaultVal;	/* overide it */
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set double value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @param minVal is minimum allowable value
    * @param maxVal is maximum allowable value
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    */
   double setDefParam(String tag, double defaultVal, double minVal, double maxVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);
     Double valP;
     double val;
     
     if(param==null)
       return(defaultVal);
     try
     {
       valP= new Double(param);
       val= valP.doubleValue();
     }
     catch(NumberFormatException e)
     { val= defaultVal;}
     if(val<minVal || val>maxVal)
       val= defaultVal;	/* overide it */
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set double value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    */
   double setDefParam(String tag, double defaultVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);     
     Double valP;
     double val;
     
     if(param==null)
       return(defaultVal);
     try
     {
       valP= new Double(param);
       val= valP.doubleValue();
     }
     catch(NumberFormatException e)
     { val= defaultVal;}
     
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set floag value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    */
   float setDefParam(String tag, float defaultVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);
     Float valP;
     float val;
     
     if(param==null)
       return(defaultVal);
     try
     {
       valP= new Float(param);
       val= valP.floatValue();
     }
     catch(NumberFormatException e)
     { val= defaultVal;}
     
     return(val);
   } /* setDefParam */
   
   
   /**
    * setDefParam() - set String value or default value if &lt;APPLET> &lt;PARAM NAME=tag VALUE=val>
    *</PRE>
    * 1. the default is set by final constants in .java files.
    * 2. the first overide is set by the
    *    Config/MaExplorerConfig.txt file.
    * 3. the final overide is set by
    *    "APPLET  PARAM NAME=tag VALUE=val"
    *</PRE>
    * @param param is the parameter name to search for
    * @param defaultVal to use if not found
    * @return value if found, else defaultVal
    * @see #getConfigAndPARAMvalue
    **/
   String setDefParam(String tag, String defaultVal)
   { /* setDefParam */
     String param= getConfigAndPARAMvalue(tag);
     if(param==null)
       return(defaultVal);
     return(param);
   } /* setDefParam */
   
   
} /* end of class GetParams */

