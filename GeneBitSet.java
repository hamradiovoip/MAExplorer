/** File: GeneBitSet.java */

/**
 * The class manipulates lists of 64-bit bit sets for performing Boolean set operations.
 * It implements these operations (union, intersection, difference) efficiently.
 * It is also a required data structure in GeneLists and is the default gene list
 * representation. You may convert a GeneBitSet.bitset to an ordered gene
 * list GeneList.mList[]. The notation talks about adding an "item" which is
 * the mid value corresponding to a Gene. The GeneBitSet userBS[0:nUserBS-1] is 
 * the database of all GeneBitSets.
 *<PRE>
 * A GeneBitSet has maxSize and is a (unsigned) long bitData[nWords] 
 * where: 
 *       nWords= (maxSize/64)+1.
 * For an endtry eIdx, it is indexed as ((bitData[wIdx] >>> bitIdx) & 01)
 * where: 
 *       wIdx= eIdx/64 (i.e. eIdx >>> 6),
 *       bitIdx= eIdx - (wIdx*64), (i.e. wIdx<<6) 
 *</PRE>
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
 * @version $Date: 2004/01/13 16:43:41 $   $Revision: 1.11 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 * @see GeneList
 */
 
class GeneBitSet
{
  /** link to global MAExplorer instance */
  private static MAExplorer
    mae;                        
    
  /** 0XFFFFFFFFFFFFFFFF is all 64-bits that is also 1's complement -1 */
  final private static long
    ALL_BITS= -1;               
    
  /** max # user assignable gene bit setst hat can be created */
  final static int
    MAX_USER_BS= 300;           
      
  /** temporary bit set used in set operations */
  private static GeneBitSet
    tempSetBL;                      
  
  /** [1:maxUserBS] user assignable gene bit sets. No set 0. */ 
  static GeneBitSet
    userBS[];                      
  
  /** # user assignable gene bit sets in use */ 
  static int
    nUserBS;                    
  /** max # user assignable gene bit sets */ 
  static int    
    maxUserBS;                  
       
  /** flag: selective debugging enable */ 
  static boolean
    cbsDbug;                   
    
  /* --- Instance of a GeneBitSet --- */
  /** enumeration index 0 to length-1 */
  private int
    bsEnum; 
                        
  /** 64 bit long subset of bits from bitData */
  private int
    bitWord;                                
  /** index of bitWord = bitData[wIdx] */
  private int
    wIdx;                                   
  /** index within a bitWord right to left */
  private int
    bitIdx;                     
  
  /** Associated Master GeneList if any, else null */
  GeneList
    ml; 
                            
  /** if this was assigned from another bitSet */  
  String
    assignedBSname;                    
  /** name of bit set */
  String
    bName;                      
                     
 /** list of nWords bit words allocated [0:maxSize-1] and active [0:highMID-1] */
  long
    bitData[]; 
  
  /** max # 64-bit WORDS alloc'ed in bitData[] */
  int
    maxWords;                   
  /** maximum # ITEMS in bit space allocated */
  int
    maxItems;                   
  /** highest MID in this GeneBitSet */
  int
    highMID;	   	        
  /** # of ITEMS used in this GeneBitSet */
  int
    count;			
  
    

  /**
   * GeneBitSet() - constructor to create a GeneBitSet without a name.
   * @param maE is instance of MAExplorer
   * @param maxItems is the max size of a bit set
   * @param ml is the GeneList instance to associate with the bitset
   * @see #allocGeneBitSet
   */
  GeneBitSet(MAExplorer maE, int maxItems, GeneList ml)
  { /* GeneBitSet */
    mae= maE;
    allocGeneBitSet(maxItems, "", ml);
  } /* GeneBitSet */
  
  
  /**
   * GeneBitSet() - constructor to create a named GeneBitSet.
   * @param maE is instance of MAExplorer
   * @param maxItems is the max size of a bit set
   * @param bName is the bit set name
   * @param ml is the GeneList instance to associate with the bitset
   * @see #allocGeneBitSet
   */
  GeneBitSet(MAExplorer maE, int maxItems, String bName, GeneList ml)
  { /* GeneBitSet */
    mae= maE;
    allocGeneBitSet(maxItems, bName, ml);
  } /* GeneBitSet */
  
  
  /**
   * allocGeneBitSet() - create named GeneBitSet by allocating bit set structures.
   * We packing 64 items (i.e. genes) in a 64 bit (unsigned long) word.
   * @param maxItems is the max size of a bit set
   * @param bName is the bit set name
   * @param ml is the GeneList instance to associate with the bitset
   */
  private void allocGeneBitSet(int maxItems, String bName, GeneList ml)
  { /* allocGeneBitSet */
    this.maxItems= maxItems;
    this.bName= bName;
    this.ml= ml;
    
    assignedBSname= null;     /* set if was assigned from another bitSet */
    highMID= -1;		/* highest MID in this GeneBitSet */
    count= 0;			/* # of ITEMS used in this GeneBitSet */
    maxWords= ((maxItems==0) ? ((mae.mp.maxGenes/64)+1) : ((maxItems/64)+1));
   /*
   if(mae.CONSOLE_FLAG)
      mae.fio.logMsgln("CBS-ACBS bName="+bName+
                       ", maxItems="+maxItems+
                       ", maxWords="+maxWords);
    */
    bitData= new long[maxWords];  /* allocates zeros, so empty */
    count= 0;                     /* empty */
    highMID= -1;
    
    cbsDbug= false;
    
    if(userBS==null)
    { /* allocate user assignable gene bit sets */
      maxUserBS= MAX_USER_BS;   /* max # of user assignable gene bit sets
       * which can be created */
      userBS= new GeneBitSet[maxUserBS+1]; /*i.e. [1:maxUsersBS] */
      nUserBS= 0;
    }
  } /* allocGeneBitSet */
  
  
  /**
   * setItem() - set bit in gene bit set corresponding to item (i.e. MID).
   * It also extends the list length.
   * @param item to be set
   * @return true if set, false if any problems.
   */
  boolean setItem(int item)
  { /* setItem */
    if(item<0 || item>maxItems)
      return(false);                     /* bogus index */
    
    wIdx= (item >>> 6);                  /* item/64 */
    bitIdx= item - (wIdx << 6);          /* i.e. item - wIdx*64 */
    bitData[wIdx] |= (01L << bitIdx);    /* add bit to bit set */
    
    highMID= Math.max(item,highMID);
    count++;
    
    return(true);
  } /* setItems */
  
  
  /**
   * addItem() - add Item (i.e. MID) to the gene bit set.
   * @param item to be added
   * @return true if added, false if any problems.
   */
  boolean addItem(int item)
  { /* addItem */
    if(item<0 || item>maxItems)
      return(false);                 /* bogus index */
    
    wIdx= (item >>> 6);              /* item/64 */
    bitIdx= item - (wIdx << 6);      /* i.e. item - wIdx*64 */
    long bit= (bitData[wIdx] & (01L << bitIdx));
    if(bit!=0L)
      return(false);	               /* already in the list so don't add */
    
    bitData[wIdx] |= (01L << bitIdx); /* add bit to bit set */
    
    highMID= Math.max(highMID,item);
    count++;
      /*
      if(mae.CONSOLE_FLAG)
          mae.fio.logMsgln("CBS-addItem count="+count+" item="+item+
                         " highMID="+highMID+" bit="+bit+" wIdx="+wIdx+
                         " bitData[wIdx]="+bitData[wIdx]+ " bName="+bName);
       */
    return(true);
  } /* addItem */
  
  
  /**
   * rmvItem() - remove Item (i.e. MID) from gene bit set.
   * @param item to be removed
   * @return true if removed it, false if not found.
   * @see #findCountAndhighMID
   */
  boolean rmvItem(int item)
  { /* rmvItem */
    if(item<0 || item>maxItems)
      return(false);                   /* bogus index */
    
    wIdx= (item >>> 6);                /* item/64 */
    bitIdx= item - (wIdx << 6);        /* i.e. item - wIdx*64 */
    long bit= (bitData[wIdx] & (01L << bitIdx));
    if(bit==0L)
      return(false);	                 /* not in bit set */
    
    bitData[wIdx] &= (ALL_BITS - (01L << bitIdx)); /* remove bit */
    
    /* Recompute highMID and count */
    findCountAndhighMID(null);
    
    return(true);
  } /* rmvItem */
  
  
  /**
   * isItemInGeneBitSet() - is an item (i.e. MID) in the GeneBitSet.
   * @param maxItems is the max size of a bit set
   * @return true if item is in the GeneBitSet.
   */
  boolean isItemInGeneBitSet(int item)
  { /* isItemInGeneBitSet */
    if(item<0 || item>maxItems)
      return(false);                     /* bogus index */
    
    wIdx= (item >>> 6);                /* item/64 */
    bitIdx= item - (wIdx << 6);        /* i.e. item - wIdx*64 */
    long bit= (bitData[wIdx] & (01L << bitIdx));
    
    return((bit!=0L));	               /*return status */
  } /* isItemInGeneBitSet */
  
  
  /**
   * clearNull() - clear gene bit set and set entries to null.
   * Return true if successful.
   */
  boolean clearNull()
  { /* clearNull*/
    for(int i=0;i<maxWords;i++)
      bitData[i]= 0;
    count= 0;
    highMID= -1;
    
    return(true);
  } /* clearNull */
  
  
  /**
   * findCountAndhighMID() - find count and highest MID in gene bit set BitData[].
   * If the mList[] is specified, then set mList[0:count-1] with
   * genes from the midStaticCL.mList[] database.
   * @param ml is the GeneList instance to associate with the bitset
   * @return count
   */
  int findCountAndhighMID(GeneList ml)
  { /* findCountAndhighMID */
    int
      i,
      item= -1;
    long
      word,
      bit;
    Gene
      midStaticMlist[]= mae.mp.midStaticCL.mList,
      mList[]= (ml==null) ? null : ml.mList;
    highMID= -1;
    count= 0;
    
    for(int w=0;w<maxWords;w++)
    {
      word= bitData[w];
      bit= 01L;
      
      for(i=0;i<64;i++)
      {
        if((word & bit)!=0L)
        {
          highMID= ++item;  /* save current MID */
          if(mList!=null)
            mList[count]= midStaticMlist[highMID];
          count++;
        }
        else
          item++;
        bit= (bit << 1);
      }
    }
    
    if(ml!=null)
      ml.length= count;
      /*
      if(mae.CONSOLE_FLAG && cbsDbug)
         mae.fio.logMsgln("CBS-findCountAndhighMID count="+count+
                          " highMID="+highMID+
                          " ml.length="+ml.length+
                          " bName="+bName);
       */
    return(count);
  } /* findCountAndhighMID */
  
  
  /**
   * cvtBStoCL() - convert GeneBitSet ml.bitSet to GeneList c1.mList[]
   * (allocate data structures if needed).
   * @param ml is the GeneList instance to convert.
   * @return true if succeed.
   * @see GeneList#clearMlist
   * @see #findCountAndhighMID
   * @see #nextMID
   * @see #resetEnumeration
   */
  boolean cvtBStoCL(GeneList ml)
  { /* cvtBStoCL */
    if(ml==null || ml.mList==null)
      return(false);
    GeneBitSet bs= ml.bitSet;
    
    if(ml.mList==null || ml.maxSize < bs.count)
    { /* (re)allocate */
      ml.mList= new Gene[bs.count];
      ml.maxSize= bs.count;
    }
    
    /* Check bs.bitData[0:bs.maxWords-1] bits
     * to add genes to ml.mList[0:ml.length-1] for all bits set
     */
    Gene
      gene,
      mListMID[]= mae.mp.midStaticCL.mList;
    
    bs.findCountAndhighMID(ml);   /* compute count and highMID*/
    
    ml.length= 0;
    ml.clearMlist();  /* But NOT the bitSet data */
    bs.resetEnumeration();
    
    for(int i=0; i<=bs.count; i++)
    { /* add genes MID's to mList if in bit set */
      int mid= bs.nextMID();
      /*
      if(mae.CONSOLE_FLAG && cbsDbug)
        mae.fio.logMsgln("CBS-cvtBStoCL.1 i="+i+" mid="+mid);
      */
      if(mid==-1)
        continue;
      gene= mListMID[mid];
      if(gene!=null)
      { /* add it to list */
        ml.mList[ml.length++]= gene;
        /*
        if(mae.CONSOLE_FLAG && cbsDbug)
          mae.fio.logMsgln("CBS-cvtBStoCL.2 i="+i+" ml.length="+ml.length+
                           " mid="+mid+" ml.cName="+ml.cName);
         */
      } /* add it to list */
    } /* add genes MID's to mList if in bit set */
    /*
    if(mae.CONSOLE_FLAG && cbsDbug)
      mae.fio.logMsgln("CBS-cvtBStoCL.3 bs.count="+bs.count+
                       " ml.length="+ml.length+=" ml.maxSize="+ml.maxSize+
                       " ml.cName="+ml.cName);
    */
    return(true);
  } /* cvtBStoCL */
  
  
  /**
   * copyBStoCL() - copy GeneBitSet b2Src to GeneList c1Dst.mList[].
   * It also copies b2Src to c1Dst.bitSet.
   * DO NOT rename c1Dst.
   * @param c1Dst is the GeneList destination operand
   * @param b2Src is the GeneBitSet source operand
   * @return true if succeed.
   * @see #copyBStoBS
   * @see #cvtBStoCL
   */
  boolean copyBStoCL(GeneList c1Dst, GeneBitSet b2Src)
  { /* copyBStoCL */
    if(c1Dst==null || c1Dst.mList==null || b2Src==null)
      return(false);
    
    GeneBitSet b1Dst= c1Dst.bitSet;
    
    /* Now, copy BitSet b2Src to c1Dst.bitSet if different bitSets */
    if(b1Dst!=b2Src)
      copyBStoBS(b1Dst, b2Src);
    
    /* Convert ml.bitSet data to new c1Dst.mList[] of genes */
    cvtBStoCL(c1Dst);     /* copy it to mList[] */
    
   /*
   if(mae.CONSOLE_FLAG && cbsDbug)
     mae.fio.logMsgln("CBS-copyBStoCL b2Src.count="+b2Src.count+
                      ", c1Dst.length="+c1Dst.length+
                      ", c1Dst.maxSize="+c1Dst.maxSize+
                      ", c1Dst.cName="+c1Dst.cName);
   */
    return(true);
  } /* copyBStoCL */
  
  
  /**
   * copyCLtoBS() - copy GeneList c2Src to GeneBitSet b1Dst.
   * Do Not rename b1Dst.
   * @param b1Dst is the GeneBitSet destination operand
   * @param c2Src is the GeneList source operand
   * @param useCopyBitArrayFlag copy bit array data
   * @return true if succeed.
   * @see #clearNull
   * @see #addItem
   */
  static boolean copyCLtoBS(GeneBitSet b1Dst, GeneList c2Src,
  boolean useCopyBitArrayFlag)
  { /* copyCLtoBS */
    if(c2Src==null || b1Dst==null)
      return(false);
    
    GeneBitSet b2Src= c2Src.bitSet;
    
    if(b1Dst.maxItems < c2Src.length)
      return(false);
    
    if(useCopyBitArrayFlag)
    { /* copy bit array data */
      /* Copy b1.bitData[0:b1.maxWords-1]
       * to   tempSetBL.bitData[0:b1.maxWords-1]
      */
      b1Dst.count= b2Src.count;
      b1Dst.highMID= b2Src.highMID;
      System.arraycopy((Object) b2Src.bitData, 0,
                       (Object) b1Dst.bitData, 0, b1Dst.maxWords);
    } /* copy bit array data */
    else
    { /* copy by mList[] Gene entries to bits */
      /* Copy c2Src.mList[0:c2Src.length-1] Genes
       * to   b1Dst.bitData[0:b1Dst.maxWords-1] bits
      */
      b1Dst.clearNull();
      Gene gene;
      for(int i=0; i<c2Src.length; i++)
      {
        gene= c2Src.mList[i];
        if(gene!=null)
          b1Dst.addItem(gene.mid);
      }
    } /* copy by mList[] Gene entries to bits */
    
    /*
    if(mae.CONSOLE_FLAG && cbsDbug)
      mae.fio.logMsgln("CBS-copyCLtoBS b1Dst.count="+b1Dst.count+
                       ", c2Src.length="+c2Src.length+
                       ", c2Src.cName="+c2Src.cName+
                       ", b1Dst.bName="+b1Dst.bName);
    */
    return(true);
  } /* copyCLtoBS */
  
  
  /**
   * copyBStoBS() - copy GeneBitSet b2Src to GeneBitSet b1Dst.
   * Do NOT rename b1Dst.
   * @param b1Dst is the GeneBitSet destination operand
   * @param b2Src is the GeneBitSet source operand
   * @return true if succeed.
   */
  static boolean copyBStoBS(GeneBitSet b1Dst, GeneBitSet b2Src)
  { /* copyBStoBS */
    if(b1Dst==null || b2Src==null)
      return(false);
    
    /* Copy b2Src.bitData[0:b2Src.maxWords-1]
     * to   b1Dst.bitData[0:b2Src.maxWords-1]
     */
    System.arraycopy((Object) b2Src.bitData, 0,
                     (Object) b1Dst.bitData, 0, b2Src.maxWords);
    
    b1Dst.count= b2Src.count;
    b1Dst.highMID= b2Src.highMID;
    /*
    if(mae.CONSOLE_FLAG && cbsDbug)
      mae.fio.logMsgln("CBS-copyBStoBS b1Dst.count="+b1Dst.count+
                       " b2Src.count="+b2Src.count+
                       " b1Dst.bName="+b1Dst.bName+
                       " b2Src.bName="+b2Src.bName);
    */
    return(true);
  } /* copyBStoBS */
  
  
  /**
   * resetEnumeration() - reset the gene bit set eumeration count to 0
   */
  void resetEnumeration()
  { /* resetEnumeration */
    bsEnum= 0;               /* bit set eumeration */
  } /* resetEnumeration */
  
  
  /**
   * nextMID() - get next mid in enumeration in [0:highMID] of this gene bit set
   * @return mid if succeed, else -1 if none (at end of the list).
   */
  int nextMID()
  { /* nextMID */
    int 
      mid= -1,
      wIdx;
    boolean existsFlag;
    long bit;
    
    while(bsEnum<=highMID)
    {
      wIdx= (bsEnum >>> 6);
      bitIdx= bsEnum - (wIdx << 6);
      bit= (bitData[wIdx] & (01L << bitIdx));
      existsFlag= (bit!=0L);
    /*
    if(mae.CONSOLE_FLAG && cbsDbug)
      mae.fio.logMsgln("CBS-nextMID bsEnum="+bsEnum+
                       " highMID="+highMID+" wIdx="+wIdx+
                       " bit="+bit+" existsF="+existsFlag+
                       " bitData[wIdx]="+bitData[wIdx]+
                       " bName="+bName);
     */
      if(existsFlag  /* i.e. isItemInGeneBitSet(bsEnum) */)
      {
        mid= bsEnum++;
        return(mid);
      }
      else bsEnum++;
    }
    return(mid);
  } /* nextMID */
  
  
  /**
   * union() - compute union of GeneBitSets sets (b1 & b2) ==> b3.
   * Then, copy the results into b3.
   * Note: b1 or b2 can be the same as b3. It will copy the result AFTER
   * it has been computed.
   * @param b1 is the GeneBitSet first source operand
   * @param b2 is the GeneBitSet second source operand
   * @param b3 is the GeneBitSet destination operand
   * @return true if succeed.
   * @see #findCountAndhighMID
   */
  boolean union(GeneBitSet b1, GeneBitSet b2, GeneBitSet b3)
  { /* union */
    if(b1==null || b2==null && b3==null)
      return(false);
    
    int maxWords3= b1.maxWords;
    
    if(tempSetBL==null)
      tempSetBL= new GeneBitSet(mae, maxWords3, "tempSetBL", null);
    
      /* Copy b1.bitData[0:b1.maxWords-1]
       * to   tempSetBL.bitData[0:b1.maxWords-1]
       */
    tempSetBL.count= b1.count;
    tempSetBL.highMID= b1.highMID;
    System.arraycopy((Object) b1.bitData, 0,
                     (Object) tempSetBL.bitData, 0,maxWords3);
    
    long
      b1BitData[]= b1.bitData,
      b2BitData[]= b2.bitData,
      tempBitData[]= tempSetBL.bitData;
    
    for(int j=0; j<maxWords3; j++)
    { /* add members of b2 which are not in b1 */
      long
        w1= b1BitData[j],
        w2= b2BitData[j],
        w3= (w1 | w2);                    /* does 64-bit UNION */
      tempBitData[j]= w3;
    } /* add members of b2 which are not in b1 */
    
    tempSetBL.findCountAndhighMID(null);
    
    /* Copy tmpSetBL to b3 */
    b3.count= tempSetBL.count;
    b3.highMID= tempSetBL.highMID;
    System.arraycopy((Object) tempSetBL.bitData, 0,
                     (Object) b3.bitData, 0,maxWords3);
    
    return(true);
  } /* union */
  
  
  /**
   * intersection() - intersection of GeneBitSets (b1 | b2) ==> b3.
   * Then, copy the results into b3.
   * Note: b1 or b2 can be the same as b3. It will copy the result AFTER
   * it has been computed.
   * @param b1 is the GeneBitSet first source operand
   * @param b2 is the GeneBitSet second source operand
   * @param b3 is the GeneBitSet destination operand
   * @return true if succeed.
   * @see #findCountAndhighMID
   */
  boolean intersection(GeneBitSet b1, GeneBitSet b2, GeneBitSet b3)
  { /* intersection */
    if(b1==null || b2==null && b3==null)
      return(false);
    
    int maxWords3= b1.maxWords;
    
    if(tempSetBL==null)
      tempSetBL= new GeneBitSet(mae, mae.mp.maxGenes, "tempSetBL", null);
    
    long
      b1BitData[]= b1.bitData,
      b2BitData[]= b2.bitData,
      tempBitData[]= tempSetBL.bitData;
    
    /* Copy b1.bitData[0:b1.maxWords-1]
     * to   tempSetBL.bitData[0:b1.maxWords-1]
     */
    tempSetBL.count= b1.count;
    tempSetBL.highMID= b1.highMID;
    System.arraycopy((Object) tempSetBL.bitData, 0,
                     (Object) b3.bitData, 0,maxWords3);
    
    long w1, w2, w3;
    for(int j=0; j<maxWords3; j++)
    { /* add members which are in both  b1 and b2 */
      w1= b1BitData[j];
      w2= b2BitData[j];
      w3= (w1 & w2);               /* does 64-bit INTERSECTION */
      tempBitData[j]= w3;
    } /* add members which are in both  b1 and b2 */
    
    tempSetBL.findCountAndhighMID(null);
    
    /* Copy tmpSetBL to b3 */
    b3.count= tempSetBL.count;
    b3.highMID= tempSetBL.highMID;
    System.arraycopy((Object) tempSetBL.bitData, 0,
                     (Object) b3.bitData, 0,maxWords3);
    
    return(true);
  } /* intersection */
  
  
  /**
   * difference() - difference of GeneBitSets (b2 - b1)==> b3.
   * That is, subtract items in b1 that are in b2 but DON'T subtract
   * items in b1 that are not in b2 - this is assymetric!
   * The results are copied into b3.
   * Note: b1 or b2 can be the same as b3. It will copy the result AFTER
   * it has been computed.
   * @param b1 is the GeneBitSet first source operand
   * @param b2 is the GeneBitSet second source operand
   * @param b3 is the GeneBitSet destination operand
   * @return true if succeed.
   * @see #findCountAndhighMID
   */
  boolean difference(GeneBitSet b1, GeneBitSet b2, GeneBitSet b3)
  { /* difference */
    if(b1==null || b2==null && b3==null)
      return(false);
    
    int maxWords3= b1.maxWords;
    
    if(tempSetBL==null)
      tempSetBL= new GeneBitSet(mae, maxWords3, "tempSetBL", null);
    
    long
      b1BitData[]= b1.bitData,
      b2BitData[]= b2.bitData,
      tempBitData[]= tempSetBL.bitData;
    
    /* Copy b1.bitData[0:b1.maxWords-1]
     * to   tempSetBL.bitData[0:b1.maxWords-1]
     */
    tempSetBL.count= b1.count;
    tempSetBL.highMID= b1.highMID;
    System.arraycopy((Object) b1.bitData, 0,
                     (Object) tempSetBL.bitData, 0, b1.maxWords);
    
    long
      w1, w2, w3,
      w2Complement;
    for(int j=0; j<maxWords3; j++)
    { /* add members of b2 which are not in b1 */
      w1= b1BitData[j];
      w2= b2BitData[j];
      //w2Complement= - (0X00 | w2);
      w2Complement= -(w2+1);     /* 1's complement */
      w3= (w1 & w2Complement);   /* does  64-bit SET DIFFERENCE */
      tempBitData[j]= w3;
    } /* add members of b2 which are not in b1 */
    
    tempSetBL.findCountAndhighMID(null);
    
    /* Copy tmpSetBL to b3 */
    b3.count= tempSetBL.count;
    b3.highMID= tempSetBL.highMID;
    System.arraycopy((Object) b3.bitData, 0,
                     (Object) tempSetBL.bitData, 0,maxWords3);
    
    
    return(true);
  } /* difference */
  
  
  /**
   * listGeneBitSets() - popup a text window to list the user's gene sets.
   * @param optMsg is optional message to add to front of the report.
   * @see ShowStringPopup
   * @see #getListGeneBitSetsStr
   */
  void listGeneBitSets(String optMsg)
  { /* listGeneBitSets */
    if(optMsg==null)
      optMsg= "";
    String sR= getListGeneBitSetsStr();
    
    ShowStringPopup t= new ShowStringPopup(mae,sR,30,55, /* was 18,80 */
                                           mae.rptFontSize,
                                           optMsg+"User Gene Sets",
                                           0, 0, "UserGeneSets",
                                           PopupRegistry.UNIQUE,
                                           "maeGeneLists.txt");
  } /* listGeneBitSets */
  
  
  /**
   * updateListGeneBitSets() - update existing "UserGeneSets" popup window
   * @see PopupRegistry#lookupShowStringPopupInstance
   * @see ShowStringPopup#updateText
   * @see #getListGeneBitSetsStr
   */
  void updateListGeneBitSets()
  { /* updateListGeneBitSets */
    ShowStringPopup ssp= mae.pur.lookupShowStringPopupInstance("UserGeneSets");
    
    if(ssp==null)
      return;                /* does not exist, so no-op */
    
    String sR= getListGeneBitSetsStr();
    
    ssp.updateText(sR);
  } /* updateListGeneBitSets */
  
  
  /**
   * getListGeneBitSetsStr() - convert list of active GeneBitSets userBS[] to summary print string.
   * @return print string report
   */
  String getListGeneBitSetsStr()
  { /* getListGeneBitSetsStr */
    /*
    if(mae.DEBUG)
      System.out.println("GBS-GLGBS editedCL BS.count=" +
                         mae.gct.editedCL.bitSet.count +
                         " length=" + mae.gct.editedCL.length+
                         " idx="+
                         GeneBitSet.lookupGeneSetByName(mae.gct.editedCL.bitSet.bName,
                         true)+
                         " "+ mae.gct.editedCL.bitSet.bName+
                         "\n (editedCL.bitSet==userBS[11])="+
                         (mae.gct.editedCL.bitSet==userBS[11])+
                         "\n  userBS[11].count="+userBS[11].count+
                         " userBS[11].bName="+userBS[11].bName
                         );
       */
    
    String
      sBS,
      sTitle,
      sR= "User Gene Sets\nSet# |#genes| title\n=======================\n";
    GeneBitSet ubs;
    
    for(int i=1;i<=maxUserBS;i++)
    { /* add all active bit sets */
      ubs= userBS[i];
      if(ubs==null)
        continue;                 /* no set at this slot */
      sTitle= ubs.bName + ((ubs.assignedBSname!=null)
                             ? (" [assigned: "+ubs.assignedBSname+"]")
                             : "");
      sBS= " #" + i + " |" + ubs.count + "| " + sTitle + "\n";
      sR += sBS;
      
      if(i==mae.gct.idxUserFilterGeneSet)
        sR += "--------- User Assignable ----------\n";  /* separate static and dyn */
      if(i==mae.gct.idxNormGeneSet+1)
        sR += "--------- User definable------------\n";  /* separate static and dyn */
    }
    
    return(sR);
  } /* getListGeneBitSetsStr */
  
  
  /**
   * getGBSnames() - return String array of active Gene Bit Set names.
   * @return list of gene bit set names
   */
  String[] getGBSnames()
  { /* getGBSnames */
    GeneBitSet ubs;
    String
      sBuild[]= new String[maxUserBS],
      sR[];
    int k= 0;
    
    for(int i=1;i<=maxUserBS;i++)
    {
      ubs= userBS[i];
      if(ubs==null)
        continue;                 /* no set at this slot */
      sBuild[k++]= ubs.bName;
    }
    
    sR= new String[k];        /* make string of correct length */
    for(int i=0;i<k;i++)
      sR[i]= sBuild[i];
    
    return(sR);
  } /* getGBSnames */
  
  
  /**
   * lookupGeneSetObjByName() - lookup GeneBitSet object by name in userBS[] DB.
   * If it is a numeric string, then return the object, else
   * search the bName's for a match. Do matching ignoring case.
   * @param userSetName of gene bit set
   * @param exactFlag to do an equal() exact match else use startsWith()
   * @return the userBS[1:maxUserBS] object if found, null if not found
   */
  static GeneBitSet lookupGeneSetObjByName(String userSetName, boolean exactFlag)
  { /* lookupGeneSetObjByName */ 
    int bsIdx= lookupGeneSetByName(userSetName, exactFlag);
    if(bsIdx==-1)
      return(null);
    else
      return(userBS[bsIdx]);
  } /* lookupGeneSetObjByName */ 
    
  
  /**
   * lookupGeneSetByName() - lookup index of GeneBitSet in userBS[] database.
   * If it is a numeric string, then return the number, else
   * search the bName's for a match. Do matching ignoring case.
   * @param userSetName of gene bit set
   * @param exactFlag to do an equal() exact match else use startsWith()
   * @return the index CBS in userBS[1:maxUserBS] if found, -1 if not found
   */
  static int lookupGeneSetByName(String userSetName, boolean exactFlag )
  { /* lookupGeneSetByName */
    /* test if it is a number in the proper range */
    int i;
    
    try
    { /* they specified the set by the number of the set */
      i= java.lang.Integer.parseInt(userSetName);
      if(i>0 && i<maxUserBS)
        return(i);
    }
    catch(NumberFormatException e)
    { /* then look up by name */
      int
        foundIt= -1,
        count= 0;
      boolean isExactFlag= false;
      String
        lcUSname= userSetName.toLowerCase(),
        cbsName;
      for(i=1;i<=maxUserBS;i++)
        if(userBS[i]!=null)
        { /* check non-null CBS entries for name match */
          cbsName= userBS[i].bName.toLowerCase();
          isExactFlag= cbsName.equals(lcUSname);
          if(isExactFlag || (!exactFlag && cbsName.startsWith(lcUSname)))
          { /* found it */
            count++;
            foundIt= i;
            if(isExactFlag)
              break;  /* stop search */
          }
        } /* check non-null CBS entries for name match */
      
      if(count==1 || isExactFlag)
        return(foundIt);         /* it is unique */
    } /* then look up by name */
    
    return(-1);             /* failed because either not found
     * or it was not unique. */
  } /* lookupGeneSetByName */
  
  
  /**
   * lookupOrMakeNewGeneBitSet() - lookup or create new GeneBitSet in userBS[].
   * @param userSetName is the name of the existing or new gene bit set
   * @param bs is the optional GeneBitSet to copy or use
   * @return index of userBS[] entry, else -1 if fail.
   * @see Util#showMsg
   * @see #copyBStoBS
   * @see #lookupGeneSetByName
   */
  static int lookupOrMakeNewGeneBitSet(String userSetName, GeneBitSet bs)
  { /* lookupOrMakeNewGeneBitSet */
    if(userSetName==null || userSetName.length()==0)
      return(-1);                           /* failed */
    
    String bsName= (bs!=null && bs.bName!=null) ? bs.bName : "null";
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("GBS-LMNGBS.1 userSetName="+userSetName+
                         " bs.Name="+bsName);
    */
    boolean sameNameFlag= (bs!=null && bs.bName!=null &&
                           userSetName.equals(bs.bName));
    int nFree= lookupGeneSetByName(userSetName,true); /* reuse old name if can*/
    /*
    if(mae.CONSOLE_FLAG)
      System.out.println("GBS-LMNGBS.2 nFree="+nFree+" sameNameFlag="+sameNameFlag);
     */
    
    if(nFree!=-1)
      return(nFree);     /* found it, just return it */
    else
    { /* make it in a free slot */
      if(nUserBS>=maxUserBS)
      {
        Util.showMsg(
         "Sorry, no free gene sets are available. Delete a set and try again.");
        return(-1);
      }
      
      for(int i=1;i<=maxUserBS;i++)
        if(userBS[i]==null)
        { /* find a free slot and create GeneBitSet w/o GeneList */
          nFree= i;
          ++nUserBS;    /* total slots used. Count from 1 not 0 */
          /*
          if(mae.CONSOLE_FLAG)
            System.out.println("GBS-LMNGBS.3 nFree="+nFree+
                               " nUserBS="+nUserBS+
                               " bs.bName="+bsName);
           */
          /* [WIERD] need above debug or this NO-OP for JWS debugger else get
           * wierd null ptr access bug!!!!.
           */
          if(mae.CONSOLE_FLAG)
            System.out.print("");
          
          
          if(sameNameFlag)
          { /* use THIS existing BS */
            /*
            if(mae.CONSOLE_FLAG && mae.NEVER)
              System.out.println("GBS-LMNGBS.4 set userBS[nFree]<==bs.bName="+bsName);
             */
            userBS[nFree]= bs;
            /*
            if(mae.DEBUG)
              System.out.println("GBS-LMNGBS.4.1 userBS[nFree].bName="+ userBS[nFree].bName);
             */
          }
          else
          { /* make a brand new bit set since name is different */
            /*
            if(mae.CONSOLE_FLAG)
              System.out.println("GBS-LMNGBS.5 new bitset nFree="+nFree+
                                 " userSetName="+userSetName);
            */
            userBS[nFree]= new GeneBitSet(mae, mae.mp.maxGenes,
            userSetName, null);
            /*
            if(mae.CONSOLE_FLAG)
              System.out.println("GBS-LMNGBS.6 userBS[nFree].bName="+
                                  userBS[nFree].bName);
             */
            copyBStoBS(userBS[nFree], bs);   /* copy data since
                                              * different named bit sets */
           /*
           if(mae.CONSOLE_FLAG)
             System.out.println("GBS-LMNGBS.7 userBS[nFree].bName="+
                                userBS[nFree].bName);
           */
          }
          /*
          if(mae.CONSOLE_FLAG)
            System.out.println("GBS-LMNGBS.8 nFree="+nFree+
                               " userBS[nFree].bName="+
                               userBS[nFree].bName);
         */
          return(nFree);
        }
    }
    /*
    if(mae.CONSOLE_FLAG)
     System.out.println("GBS-LMNGBS.9 nFree= -1");
    */
    
    return(-1);         /* failed */
  } /* lookupOrMakeNewGeneBitSet */
  
  
  /**
   * assignCLtoUserBS() - assign GeneList ml to new user GeneBitSet userBS[] database.
   * Fail if the GeneBitSet does not exist for that GeneList.
   * Note: when you create a GeneList you ALWAYS make a GeneBitSet
   * but not always the other way around.
   * If the userSetName NEQ ml.cName, then we are creating a new
   * bit set.
   * @param userSetName is the name of the existing or new gene bit set
   * @param ml is the optional GeneList to assign
   * @return the new bit set index for userBS[idx] if successful, else -1.
   * @see Util#showMsg
   * @see #copyBStoBS
   * @see #lookupOrMakeNewGeneBitSet
   */
  int assignCLtoUserBS(String userSetName, GeneList ml)
  { /* assignCLtoUserBS */
    /* Lookup GBS to userBS[nFree] entry */
    int nFree= lookupOrMakeNewGeneBitSet(userSetName,ml.bitSet);
    if(nFree==-1)
      return(-1);
    
    /* Copy bit set ml.bitSet to userBS[nFree] data, but not name*/
    if(!userSetName.equals(ml.bitSet.bName))
    { /* copy bit set from GeneList to new named bit set */
      copyBStoBS(userBS[nFree], ml.bitSet);
    }
    else
      userBS[nFree]= ml.bitSet;     /* NOTE: FORCE  existing instance */
    
    Util.showMsg("Saved Gene Set #"+nFree+" ["+userBS[nFree].bName+"]");
    
   /*
   if(mae.CONSOLE_FLAG)
     mae.fio.logMsgln("CBS-ACLUBS nFree["+userSetName+"]="+nFree);
    */
    
    return(nFree);
  } /* assignCLtoUserBS */
  
  
  /**
   * removeUserBS() - remove user GeneBitSet by name from userBS[] database
   * Do NOT remove the gct.nSpecialGC bit sets at the front of the list.
   * @param userSetName is the name of the existing or new gene bit set
   * @return true if succeed
   * @see Util#showMsg
   * @see #lookupGeneSetByName
   */
  boolean removeUserBS(String userSetName)
  { /* removeUserBS */
    int nRemove= lookupGeneSetByName(userSetName, false);
    
    if(nRemove!=-1)
    { /* valid number */
      if(nRemove<=mae.gct.nSpecialGC)
      {
        Util.showMsg("Can't remove built-in Gene Sets.");
        Util.popupAlertMsg("Can't remove built-in Gene Sets",
                           "Can't remove built-in Gene Sets.\n",
                           4, 60);
        return(false);
      }
      if(userBS[nRemove]==null)
      {
        Util.showMsg("Already removed this Gene Set."); 
        Util.popupAlertMsg("Already removed this Gene Set",
                           "Already removed this Gene Set.\n",
                           4, 60);
        return(false);
      }
      Util.showMsg("Removed gene set #"+ nRemove+" ["+userBS[nRemove].bName+"]");
      userBS[nRemove]= null;   /* 1:nnn */
      nUserBS--;
    }
    else
    {
      Util.showMsg("Can't remove gene set ["+userSetName+"] - check spelling.");   
      Util.popupAlertMsg("Can't remove gene set",
                         "Can't remove gene set ["+userSetName+"] - check spelling.\n",
                         4, 80);
      return(false);
    }
    
    return(true);
  } /* removeUserBS */
  
  
  /**
   * useGeneSetBS() - assign the specified GeneBitSet by name to GeneList ml.
   * @param ml is the GeneList destination operand
   * @param gbsName is the print msg of destination bit set
   * @param userSetNameSrc is the name of the source bit set
   * @return true if succeed
   * @see Util#showMsg
   * @see #copyBStoBS
   * @see #lookupGeneSetByName
   * @see #union
   * @see #intersection
   * @see #difference
   * @see #copyBStoCL
   */
  boolean useGeneSetBS(GeneList mlDest, String gbsName, String userSetNameSrc )
  { /* useGeneSetBS */
    if(mlDest==null)
      return(false);
    String oldCLbsName= mlDest.bitSet.bName;  /* save so preserve name */
    int nUseSrcIdx= lookupGeneSetByName(userSetNameSrc, false);
    
    if(nUseSrcIdx!=-1)
    { /* found it - go assign it */
      GeneBitSet b2Src= userBS[nUseSrcIdx];
      if(mlDest!=null && mlDest.mList!=null)
      { /* Copy both BS & CL */
        int nDstIdx= lookupGeneSetByName(mlDest.cName, true);
        if(nDstIdx==-1)
          return(false);            /*error */
        else
        { /* only copy if it destination bitset exists */
          mlDest.bitSet.assignedBSname= b2Src.bName;
          copyBStoCL(mlDest, b2Src);
          userBS[nDstIdx]= mlDest.bitSet; /* update in case changed */
        }
      }
      else
      { /* just copy BS to BS */
        if(mlDest.bitSet==b2Src)
          return(false);          /* failed */
        mlDest.bitSet.assignedBSname= b2Src.bName;
        copyBStoBS(mlDest.bitSet, b2Src);
      }
      
      mlDest.bitSet.bName= oldCLbsName;  /* restore name*/
      Util.showMsg("Setting 'User "+gbsName+" Gene Set' to #"+
      nUseSrcIdx+" ["+b2Src.bName+"] with "+b2Src.count+" genes");
    } /* found it - go assign it */
    else
    { /* fount it - go assign it */
      Util.showMsg("Can't find Gene Set ["+userSetNameSrc+"] - check spelling.");
      Util.popupAlertMsg("Can't  find Gene Set",
                         "Can't find Gene Set ["+userSetNameSrc+
                         "]\n - check spelling.\n",
                         4, 60);
      return(false);
    }
    
    return(true);
  } /* useGeneSetBS */
  
  
  /**
   * assignCSbinOprToUserBS() - assign GeneBitSet binary Boolean opr to new user GeneBitSet.
   * The value of opr is either "union", "intersection" or "difference".
   * If there is a GeneList associated with the BitSet bs3
   * then update the cs3.mList[0:length-1].
   * @param bsName1 is the name of the first bit setsource operand
   * @param bsName2 is the name of the second bit set source operand
   * @param userSetNameSrc is the name of the destination bit set operand
   * @param opr is the name of the operator:
   *    "union", "intersection" or "difference"
   * @return true if succeed
   * @see Util#showMsg
   * @see #lookupGeneSetByName
   * @see #lookupOrMakeNewGeneBitSet
   */
  boolean assignCSbinOprToUserBS(String bsName1, String bsName2,
                                 String userSetName, String opr)
  { /* assignCSbinOprToUserBS */
    GeneBitSet
      bs1,
      bs2,
      bs3;
    int
      b1= lookupGeneSetByName(bsName1, false),
      b2= lookupGeneSetByName(bsName2, false),
      b3= lookupGeneSetByName(userSetName, false);
    
    if(b1==-1)
    {
      Util.showMsg("Set ["+bsName1+"] doesn't exist");
      return(false);
    }
    else
      bs1= userBS[b1];
    
    if(b2==-1)
    {
      Util.showMsg("Set ["+bsName2+"] doesn't exist");
      return(false);
    }
    else
      bs2= userBS[b2];
    
    if(b3==-1)
    { /* lookup existing set or create a new set with userSetName */
      b3= lookupOrMakeNewGeneBitSet(userSetName,null);
      if(b3==-1)
        return(false);
    }
    bs3= userBS[b3];        /* look up new bit set */
    
    boolean
    flag= false;
    
    /* Do operation (bs1 OPR bs2) ==> bs3 */
    if(opr.equals("union"))
      flag= union(bs1,bs2,bs3);
    else if(opr.equals("intersect"))
      flag= intersection(bs1,bs2,bs3);
    else if(opr.equals("difference"))
      flag= difference(bs1,bs2,bs3);
    
    /* Update associated GeneList for b3 if it exists */
    GeneList cl3= bs3.ml;
    if(cl3!=null && cl3.mList!=null)
      copyBStoCL(cl3, bs3);
    
    return(flag);
  } /* assignCSbinOprToUserBS */
  
  
  /**
   * cleanup() - cleanup global static allocated variables in this class.
   * If statics are added later to this class, then set them to
   * null here.
   */
  void cleanup()
  { /* cleanup */
    tempSetBL= null;
    userBS= null;
    nUserBS= 0;
    maxUserBS= 0;
  } /* cleanup */
  
} /* end of class GeneBitSet*/



