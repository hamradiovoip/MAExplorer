/** File: Statistics.java */

/** 
 * The class contains various parametric and non-parametric probability statistics methods.
 * These include: F-test, t-test, p-values for t-test, histogram, mean, stdDev, 
 * median, mode, extrema, etc.
 *<P>
 * NOTE: Statistics package (derived from WebGel and GELLAB-II which were
 * derived from Numerical Recipes, etc.). 
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.12 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */

class Statistics
 {

  /* [TODO] On an as-needed basis:
   * add Numerical Recipes, etc. (etc) WMS, ChiSQ, gamma 
   * distributions (what microarray data looks like, 
   * and other relevant tests.
   */


   /** link to global instance of MAExplorer */
   private MAExplorer 
     mae;                       
   
   /* --- CALC values computed by various methods --- */
   /** CALC: t-test p-value w/NULL hypoth set by calcTandPvalues() */
   double
     pT;                        
   /** CALC: f-test p-value w/NULL hypoth set by calcTandPvalues() */
   double
     pF;                      
   /** CALC: KS-test p-value w/NULL hypoth set by calcKStestStat() */
   double
     pKS;                         
   /** CALC: f-test p-value w/NULL hypoth for nConditions
    * set by calcNCondFtestStat() */
   double
     pFnConds;  
   
   /** CALC: calculated f statistic set by calcTandPvalues() */
    double
     f;                         
   /** CALC: t or t' statistic set by calcTandPvalues() */
   double
     t;     
   
   /** CALC: f-statistic 2 conditions */
   double
     fStat;    
   /** CALC: KS-test Kolmogorov-Smirnov D statistic set by calcKStestStat() */
   double 
     ksD;
   /** CALC: degrees of freedom of t-test or F-test (2 conditions)
    * set by calcTandPvalues() */
    double
     dF;                        
   /** CALC: degrees of freedom for KS-test set by calcKStestStat() */
    double
     dFks;                        
   /** CALC: 'B' or 'T' - t-test to use set by calcTandPvalues() */
   char
     useTest;  
     
   /** CALC: f-statistic nConditions test set by calcNCondFtestStat() */
   double
     fStatNconds; 
   /** CALC: mean square within variance, f-statistic nConditions test
    * set by calcNCondFtestStat() */
   double
     mnSqWithin;
   /** CALC: mean square between variance, f-statistic nConditions test
    * set by calcNCondFtestStat() */
   double
     mnSqBetween;
   /** CALC: degrees of freedom dfWithin, f-statistic nConditions test
    * set by calcNCondFtestStat() */
   double
     dfWithin;
   /** CALC: degrees of freedom dfBetween, f-statistic nConditions test
    * set by calcNCondFtestStat() */
   double
     dfBetween;

   /* --- Values computed in analysis of histogram set by calcHistStats() --- */
   /** for data used in histogram set by calcHistStats() */
   String
     title;                     
   /** index of mean in hist[] set by calcHistStats() */
   int
     meanIdx;     
   /** index of median in hist[] set by calcHistStats() */
   int
     medianIdx;     
   /** index of mode in hist[] set by calcHistStats() */
   int
     modeIdx;
   /** 0 if none. size of histogram set by calcHistStats() */
   int
     nBinsH;
   /** histogram of size [0:nBinsH-1] set by calcHistStats() */
   int
     hist[];     
   /** histogram data median set by calcHistStats() */               
   float
     medianH;         
   /** histogram data mode set by calcHistStats() */
   float
     modeH;
   /** histogram data median set by calcHistStats() */
   float
     meanH;         
   /** histogram data stdDev set by calcHistStats() */
   float
     stdDevH;
   /** histogram data mean absolute deviation  set by calcHistStats() */
   float
     meanAbsDevH;
   /** histogram data min value set by calcHistStats() */
   float
     minDataH;
   /** histogram data max value set by calcHistStats() */
   float
     maxDataH;
   /** width of the histogram bins computed as: nBinsH/(maxDataH-minDataH) */ 
   float
     deltaBinH;
   
   /** size of in mean,variance,stdDev,condData,nCondData stats arrays 
    * set by calcNCondFtestStat() */
   private int
     nCondAlloc;
   /** # of conditions in mean,var stats set by calcNCondFtestStat() */
   int
     nConditions;
   /** data[0:nConditions-1][sampleNbrInClass] for computations, set by
    * calcMeanAndVariance() */
   float
     condData[][];
   /** data[0:nConditions-1][sampleNbrInClass] for computations, set by
    * calcMeanAndVariance()*/
   int
     nCondData[];
   /** means[0:nConditions-1] of data for computations, set by
    * calcMeanAndVariance() */
   double
     mean[];
   /** variance[0:nConditions-1] of data for computations, set by
    * calcMeanAndVariance() */
   double
     variance[];
   /** stdDev[0:nConditions-1] of data for computations, set by
    * calcMeanAndVariance() if calcStdDevFlag set on call*/
   double
     stdDev[];    

   
   /**
    * Statistics() - constructor
    */
   Statistics()
   { /* Statistics */
     nCondAlloc= -1;           /* no allocation */
     nBinsH= 0;                /* no histogram */
   } /* Statistics */
   
   
   /**
    * Statistics() - constructor
    * @param mae is MAExplorer instance
    */
   Statistics(MAExplorer mae)
   { /* Statistics */
     this.mae= mae;
     nBinsH= 0;                       /* no histogram */
   } /* Statistics */
   
   
   /**
    * calcMeanAndVariance() - compute the mean and variance of dataS[] 
    * for 1 condition and save the results inmean[conditionK] and 
    * variance[conditionK] arrays. Also save the dataS in 
    * data[conditionK] and nSamples in nData[conditionK]. 
    * This method was derived Snedecore and Chochran Statistical Methods.
    * @param data is array of size [0:nSamples-1] of data
    * @param nSamples is size of data
    * @param conditionK is the class # associated with this data (start at 0)
    * @param calcStdDevFlag also compute stdDev[conditionK] as well
    * @return true if succeed
    */
    boolean calcMeanAndVariance(float dataS[], int nSamples, int conditionK,
                                boolean calcStdDevFlag) 
    { /* calcMeanAndVariance */
      if(condData==null || nSamples==0)
        return(false);
      
      double
        mn,
        sum= 0.0,
        diff,
        sumDiffSq= 0.0;
      
      for(int i=0;i<nSamples;i++)
        sum += dataS[i];     
      mn= sum/nSamples;
      mean[conditionK]= mn;
      condData[conditionK]= dataS;
      nCondData[conditionK]= nSamples;
     
      for(int i= 0;i<nSamples;i++)
      {
        diff= (mn - dataS[i]);
        sumDiffSq += diff*diff;
      }
     
      variance[conditionK]= (nSamples<2) ? 0.0 : (sumDiffSq/(nSamples-1));
      if(calcStdDevFlag)
        stdDev[conditionK]= Math.sqrt(variance[conditionK]);
      
      return(true);
    } /* calcMeanAndVariance */
    
    
   /**
    * calcFprobFromVariances() - calculate 2-tailed f probility that variables are same.
    * It computes:
    *<PRE>
    *    fStat - the f-statistic
    *    pF    - CALC: probab. vars. same
    *</PRE>
    * This method was derived from GELLAB-II which was derived from 
    * Numerical Recipes in C and Snedecore and Chochran Statistical Methods.
    * @param n1 # samples class 1
    * @param n2 # samples class 2
    * @param var1 variance of class 1
    * @param var2 variance of class 2
    * @return true and set variables if succeed, else false if any problems.
    * @see MathMAE#nr_betai
    */
   boolean calcFprobFromVariances(int n1, int n2, double var1, double var2)
   { /* calcFprobFromVariances */
     double
       df1,
       df2;
     
     if (n1==0 || n2==0 || var1<=0.0 || var2<=0.0)
       return(false);
     
     /* See Numerical Recipes in C 2nd Edition, Page 619 Sec. 14.2 */
     if (var1 > var2)
     {
       fStat= var1/var2;
       df1= n1 - 1.0;
       df2= n2 - 1.0;
     }
     else
     {
       fStat= var2/var1;
       df1= n2 - 1.0;
       df2= n1 - 1.0;
     }
     
     /* Calc. 2-tailed f-test probability that the means are from the
      * same distribution. See Numerical Recipes in C, 2nd Ed.
      * Page 616, Section 14-2.
      */
     pF= 2.0*MathMAE.nr_betai(0.5*df2, 0.5*df1,
                              (df2 / (df2 + (df1 * fStat))) );
     
     if(MathMAE.okBetaI || pF<0.0)
     {
       pF= 1.0;
       return(false);
     }
     
     if(pF > 1.0)
       pF= 2.0 - pF;
     
     return(true);
   } /* calcFprobFromVariances */
   
   
   /**
    * calcTandPvalues() - calculate f, t, p, dF from (n1,m1,s1) and (n2,m2,s2).
    * Use Behrens-Fisher/Satterthwaite estimate for t and dF if f-stat
    * is < 0.05 p-value that variances are different. Otherwise use the
    * standard student t-statistic with DF= (n1+n2-2).
    * If you want to force the test the set useTest to 'B' or 'T', else it
    * will pick the test to use (ie. TB or TP).
    * It uses the algorithm described Numerical Recipes in C (1st Ed)
    * for estimating p-value given the t-statistic using the incomplete
    * beta function betai().
    * It computes:
    *<pre>
    *    f - calculated f statistic
    *    t - t or t' statistic
    *    pT - t-test p-value w/NULL hypoth
    *    pF - f-test p-value w/NULL hypoth
    *    dF - degrees of freedom
    * </pre>
    * This method was derived from GELLAB-II which was derived from 
    * Numerical Recipes in C and Snedecore and Chochran Statistical Methods.
    * @param n1 # samples in class 1
    * @param  n2 # samples in class 2
    * @param m1 sample mean class 1
    * @param m2 sample mean class 2
    * @param s1 sample std dev class 1
    * @param s2 sample std dev class 2
    * @return false if any of the data is invalid (need >= 2 samples/class)
    * or the beta fct fails.
    * @see MathMAE#nr_betai
    * @see #calcFprobFromVariances
    */
   boolean calcTandPvalues(int n1, int n2, double m1, double m2,
                           double s1, double s2 )
   { /* calcTandPvalues */
     boolean
       varDiffFlag,		/* set if variances are different */
       okF= false,		/* F-test OK */
       okT= false;		/* T-Test OK */
     double
       varpooled,
       v1,
       v2;
     
     if(n1<2 || n2<2 || s1==0.0 || s2==0.0)
     {
       pT= 1.0;		/* TOTAL FAILURE!!! */
       pF= 1.0;
       dF= (n1+n2-2);
       return(false);
     }
     
     v1= s1*s1;
     v2= s2*s2;
     
     /* Always compute the f-statistic and the f-probability. */
     okF= calcFprobFromVariances(n1, n2, v1, v2);  /* sets (f, pF) */
     pF= (0.5 * pF);		/* NOTE: use 1-tailed not 2-tailed
                         * which is returned by calc. */
     
     varDiffFlag= (okF && (pF < 0.05));
     if(varDiffFlag)
       useTest= 'B';
     else useTest= 'T';
     
     switch(useTest)
     {
       case 'B':			/* Behrens-Fisher t-test */
       { /* Estimate t' stat. using Satterthwaite estimate for dF
          * if f-stat <0.05.
          * See Snedicore and Cochran 8th edition pg 97 for formulas.
          */
         double
           vb1= v1/n1,
           vb2= v2/n2,
           vPrime= ((vb1 + vb2) * (vb1 + vb2)) /
                    ((vb1 * vb1) / (n1 - 1.0) + (vb2 * vb2) / (n2 - 1.0));
         
         dF= vPrime;		/* Satterthwaite estimate of df' for NEQ
                         * variance. Round down to nearest integer*/
         t= (m1-m2)/Math.sqrt(vb1 + vb2);
                       /* Behrens-Fisher estimate of t' for NEQ variance */
         break;
       }
       
       case 'T':			/* Student's t-test */
       { /* t-statistic for EQU variance */
         dF= n1 + n2 - 2.0;
         varpooled= ((n1-1)*v1 + (n2-1)*v2) / dF;  /* Pooled variance */
         t= (m1-m2) / Math.sqrt(varpooled * ((1.0/n1) + (1.0/n2)));
         break;
       }
     }
     
     /* Compute probability pT for t-test. */
     pT= MathMAE.nr_betai(0.5*dF, 0.5, (dF / (dF + (t * t))) );
     
     if(!MathMAE.okBetaI || pT>1.0 )
     {
       pT= 1.0;		/* TOTAL FAILURE!!! */
       return(false);
     }
     
     return(true);
   } /* calcTandPvalues */
   
   
   /**
    * calcWMWtestStat() -  calculate WMW statistics from (n1,m1,s1) and (n2,m2,s2).
    * If enabled, do WMW-test for 2 classes.
    * [TODO] - not available yet
    *<P>
    * @param n1 # samples in class 1
    * @param  n2 # samples in class 2
    * @param m1 sample mean class 1
    * @param m2 sample mean class 2
    * @param s1 sample std dev class 1
    * @param s2 sample std dev class 2
    * @return false if any of the data is invalid (need >= 2 samples/class)
    */
   boolean calcWMWtestStat(int n1, int n2, double m1, double m2, 
                           double s1, double s2)
   { /* calcWMWtestStat */
     boolean didWMWtest= false;
     
     return(didWMWtest);
   } /* calcWMWtestStat */
   
   
   /**
    * calcKStestStat() - calculate Kolmogorov-Smirnov ksD, pKS, dFks 
    * from (n1,data1) and (n2,data2). DF= (n1+n2-2).
    * It computes:
    *<pre>
    *    ksD - D statistic
    *    pKS - KS test p-value w/NULL hypoth
    *    dFks - degrees of freedom
    * </pre>
    * This method was derived from GELLAB-II which was derived from 
    * Numerical Recipes in C and Snedecore and Chochran Statistical Methods.
    * @param data1 sample data class 1
    * @param n1 # samples in class 1
    * @param data2 sample data class 2
    * @param n2 # samples in class 2
    * @return false if any of the data is invalid (need >= 2 samples/class)
    */
   boolean calcKStestStat(double data1[], int n1, double data2[], int n2)
   { /* calcKStestStat */
     boolean okKS= false;		        /* KS-test OK */
       
     dFks= n1 + n2 - 2.0;
     
     if(n1<1 || n2<1)
     {
       pKS= 1.0;	                	/* TOTAL FAILURE!!! */
       return(false);
     }
          
     okKS= MathMAE.nr_kstwo(data1, n1, data2, n2);
     pKS= MathMAE.probKS;
     ksD= MathMAE.dKS;
     
     if(!MathMAE.okProbKS || pKS>1.0 || pKS<0.0 )
     {
       pKS= 1.0;		                /* TOTAL FAILURE!!! */
       return(false);
     }
     
     return(true);
   } /* calcKStestStat */
   
   
   /**
    * calcHistStats() - compute and analyze histogram generating statistics.
    * for whatever range of data is given.
    * This computes and returns the results in variables of this instance:
    *   hist[0:nBins-1], medianH, modeH, meanH,
    *   stdDevH, meanAbsDevH, minDataH, maxDataH, deltaBinH
    * @param title for data
    * @param nBins size of hist[]
    * @param data of size [0:nData-1]
    * @param nData size of data array
    * @return nBins if successful.
    * @see #calcHistStats
    */
   int calcHistStats(String title, int nBins, float data[], int nData)
   { /* calcHistStats */
     int n= calcHistStats(title,nBins,data,nData, null /* alloc new hist[] array*/);
     return(n);
   } /* calcHistStats */
   
   
   /**
    * calcHistStats() - compute and analyze a histogram for whatever range of data is given.
    * This computes and returns the results in variables of this instance:
    *<PRE>
    *   hist[0:nBins-1], medianH, modeH, meanH,
    *   stdDevH, meanAbsDevH, minDataH, maxDataH, deltaBinH.
    *</PRE>
    * Note: if hist is int[nBins+1], then it will not be allocated.
    * @param title for data
    * @param nBins size of hist[]
    * @param data of size [0:nData-1]
    * @param nData size of data array
    * @param hist opt. [nBins+1] else null in which case it will allocate
    *        it locally
    * @return nBins if successful else 0.
    */
   int calcHistStats(String title, int nBins, float data[], int nData, int hist[])
   { /* calcHistStats */
     /* [1] Setup parameters for computations */
     if(nData==0)
     {
       this.nBinsH= 0;
       return(0);
     }
     this.title= title;
     
     nBinsH= nBins;
     if(hist==null || hist.length!=nBins)
     {
       this.hist= new int[nBins+1];
       hist= this.hist; /* use the newly allocated hist[] */
     }
     
     minDataH= 100000000.0F;
     maxDataH= -100000000.0F;
     
     int
       hIdx,
       nSum= 0;
     float ri;
     double sumRI= 0.0F;
     
     /* [1] Compute extrema and sum of data */
     for(int i=0;i<nData;i++)
     { /* compute sums of data */
       ri= data[i];
       if(ri<minDataH)
         minDataH= ri;         /* find extrema */
       if(ri>maxDataH)
         maxDataH= ri;
       sumRI += ri;            /* sum of data */
     }
     
     /* [1.1] Compute mean and  deltaBin */
     meanH= (float)(sumRI/nData);
     deltaBinH= nBins/(maxDataH-minDataH); /* # bins/value unit */
     //if(deltaBinH*(maxDataH-minDataH)>nBins)
     //deltaBinH++;                      /* add extra bin */
     meanIdx= (int)((meanH-minDataH)*deltaBinH);  /* value to idx */
     
     /* [2] Compute histogram */
     for(int i=0;i<nData;i++)
     { /* compute histogram of data */
       ri= (data[i]-minDataH);
       hIdx= (int)(ri*deltaBinH);
       if(hIdx>=nBins)
         hIdx= nBins-1;      /* clip it */
       hist[hIdx]++;         /* compute histogram */
     }
     
     /* [3] Compute StdDev of all genes in HP w/o bkgrd  */
     double
       diff,
       aDiff,
       sumAbsDiff= 0.0F,
       sumDiffSq= 0.0F;
     
     for(int i= 0;i<nData;i++)
     { /* compute sum of diff*diff */
       diff= (meanH - data[i]);
       aDiff= (diff>0) ? diff : -diff;
       sumAbsDiff += aDiff;
       sumDiffSq += diff*diff;
     }
     
     meanAbsDevH= (float)(sumAbsDiff/nData);
     stdDevH= (nData<2)
                 ? 0.0F
                 : (float)Math.sqrt((double)sumDiffSq/(nData-1));
     
     /* [4] Compute mode and median from histogram  */
     int
       halfNsum= nData/2,
       hVal,
       sum2= 0;                   /* half sum */
     float maxModeVal= -100000000.0F;
     
     medianIdx= -1;
     modeIdx= -1;
     modeH= 0;
     medianH= 0;
     
     hist[nBins-1]+= hist[nBins];   /* merge overflow if any */
     
     for(hIdx=0;hIdx<nBins;hIdx++)
     { /* find median and mode */
       hVal= hist[hIdx];
       
       if(medianIdx==-1)
       { /* latch the midpoint of histogram */
         sum2 += hVal;
         if(sum2>=halfNsum)
         { /* found midpoint */
           medianIdx= hIdx;
         }
       }
       if(maxModeVal < hVal)
       { /* estimate best peak of histogram */
         modeIdx= hIdx;
         maxModeVal= hVal;
       }
     } /* find median and mode */
     
     /* Compute median and mode in value space */
     medianH= (medianIdx/deltaBinH)+minDataH;
     modeH= (modeIdx/deltaBinH)+minDataH;
     /*
     if(mae.CONSOLE_FLAG)
       mae.fio.logMsgln("ST-CH ms=["+title+"]"+
                       "\n nBins="+nBins+
                       " nData="+nData+
                       " deltaBinH="+Util.cvf2s(deltaBinH,4)+
                       "\n minDataH="+Util.cvf2s(minDataH,3)+
                       " maxDataH="+Util.cvf2s(maxDataH,3)+
                       "\n meanIdx="+meanIdx+
                       " meanH="+Util.cvf2s(meanH,3)+
                       " stdDevH="+Util.cvf2s(stdDevH,3)+
                       " mnAbsDevH="+Util.cvf2s(meanAbsDevH,3)+
                       "\n medianIdx="+medianIdx+
                       " medianH="+Util.cvf2s(medianH,3)+
                       "\n modeIdx="+modeIdx+
                       " modeH="+Util.cvf2s(modeH,3)+
                       " maxModeVal="+Util.cvf2s(maxModeVal,3));
       */
     return(nBins);
   } /* calcHistStats */
   
   
   /**
    * calcNCondFtestStat() - calc. F-test statistics of data[0:nConditions-1][samples]
    * It computes:
    *<pre>
    *    pFnConds - p value
    *    fStatNconds - f statistic
    *    mnSqWithin - mean within class variance
    *    mnSqBetween - mean between class variance
    *    dF1 - degrees of freedom df1
    *    dF2 - degrees of freedom df2
    * </pre>
    * This method was derived from GELLAB-II which was derived from 
    * Numerical Recipes in C, 2nd Edition, pg 619, Sec. 14.2, 
    * and Snedecore and Chochran Statistical Methods.
    *<P>
    * @param data sample data[nConditions][sampleNbrInCondition]
    * @param nData # samples in each [nConditions]
    * @param nConditions # of conditions
    * @return false if any of the data is invalid (need >1 sample/Condition)
    */
   boolean calcNCondFtestStat(float data[][], int nData[], int nConditions)
   { /* calcNCondFtestStat */
     this.nConditions= nConditions; 
     boolean flag= false;	/* test succeed flag */
     int nTot= 0;	/* total # of samples in all class subsets */
     double
       sumGlbMean= 0.0,			/* sum global mean of all data */
       glbMean= 0.0,			  /* global mean of all data */
       sumSQbetween= 0.0,		/* sum Sq between errors */
       sumSQwithin= 0.0;		/* sum Sq within errors */

     /* [1] Calculate the means and variances for each data class */  
     if(nCondAlloc < nConditions)
     { /* allocate arrays if needed */
       condData= new float[nConditions][];
       nCondData= new int[nConditions];
       mean= new double[nConditions];
       variance= new double[nConditions];
       stdDev= new double[nConditions];
       nCondAlloc= nConditions;
     }
      
     /* Worst case */
     pFnConds= 1.0; 
     fStatNconds= 0.0;
     mnSqWithin= 0.0;
     mnSqBetween= 0.0;
     dfWithin= 0.0;
     dfBetween= 0.0;
  
     for(int k=0;k<nConditions;k++)
     { /* create the mean[0:nConditions-1] and variance[0:nConditions-1] arrays */
       /* Also copies the condData[conditionK] and nCondData[conditionK] as well */
       flag= calcMeanAndVariance(data[k],nData[k],k,false);
       if(!flag)
         return(false);     /* bad data */
     }
     
     for(int k=0;k<nConditions;k++)
     { /* Get within class statistics */
       if (nData[k] < 2)
       { /* if fail any one, then fail the F-test */
         return(false);
       }
       
       /* Note: sample variance=(Sum (x-mean)**2)/n =(Sum devSq)/n
        * Therefore, Sum devSq=variance*n.
       */
       nTot += nData[k];
       sumGlbMean += mean[k]*nData[k];
     } /* get within class stat */
     
     /* [1.1] If failed, then just return false. */
     if (nTot==0)
       return(false);		/* bad set - save time and abort test */
     
     /* [2] Compute F-statistic from means, variances and counts */
     glbMean= (nTot>0) ? (sumGlbMean/nTot) : 0.0;
     sumSQbetween= 0.0;
     sumSQwithin= 0.0;
     
     for(int k=0;k<nConditions;k++)
     { /* compute grand totals */
       sumSQbetween += ((mean[k]-glbMean)*(mean[k]-glbMean))*nData[k];
       sumSQwithin  += variance[k]*(nData[k]-1);
     } /* compute grand totals */
     
     /* [3] Compute mean square errors */
     mnSqWithin= ((nTot-nConditions)!=0) ? sumSQwithin/(nTot-nConditions) : 0.0;
     mnSqBetween= ((nConditions-1)!=0) ? sumSQbetween/(nConditions-1) : 0.0;
     
     /* make sure don't divide by zero */
     if(mnSqWithin==0.0)
       return(false);
     
     fStatNconds= (mnSqBetween/mnSqWithin);	/* Calculate SAMPLE F-statistic */
     dfWithin= (nTot - nConditions);
     dfBetween= (nConditions-1);
     
    /* [4] Compute F-test */
     if(fStatNconds<1.0)
     { /* swap */
       double tmp= dfWithin;
       fStatNconds= 1.0/fStatNconds;
       dfWithin= dfBetween;
       dfBetween= tmp;
     } /* swap */
     
     /* See Numerical Recipes in C, 2nd Edition, pg 619, Sec. 14.2 */
     pFnConds= 2.0*MathMAE.nr_betai(0.5*dfBetween, 0.5*dfWithin,
                                    (dfBetween/(dfBetween+dfWithin*fStatNconds)));
     if(pFnConds>1.0)
       pFnConds= 2.0 - pFnConds;
     
     flag= MathMAE.okBetaI;
     
     return(flag);
  } /* calcNCondFtestStat */

} /* end of class Statistics */


