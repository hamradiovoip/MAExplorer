/** File: SortMAE.java */

import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowListener;
import java.awt.TextField;
import java.io.*;
import java.util.*;
import java.lang.*;
import java.text.*;

/**
 * The SortMAE class contains a variety of sorting methods.
 * These include: insertion sort, bubble sort and quicksort. They may
 * be applied to various types of arrays of data including
 * string[], int[], float[] data structures. In general, the more efficient
 * quicksort methods may be used to replace bubble sort methods. 
 * Sort times are a problem with large datasets using bubble sorts.
 * The constructor is not used. 
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
 * @version $Date: 2004/01/13 16:46:20 $   $Revision: 1.7 $
 * @see <A HREF="http://maexplorer.sourceforge.net/">MAExplorer Home</A>
 */
 
class SortMAE 
{ /* class SortMAE */

  /* NOTE: there is no constructor... */
  
  /**
   * sortStrArray() - create a sorted String[] array.
   * @param unsortedData array of data to be sorted
   * @return the new sorted string[] array
   * @see #sortStrArray
   */
  static String[] sortStrArray(String unsortedData[])
  { /* sortStrArray */
    if(unsortedData==null)
      return(null);
    else
      return(sortStrArray(unsortedData,unsortedData.length));
  } /* sortStrArray */
  
  
  /**
   * sortStrArray() - create a sorted String[] array with length specified.
   * @param unsortedData array of data to be sorted
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @return the new sorted string[] array
   * @see #bubbleSort
   */
  static String[] sortStrArray(String unsortedData[], int len)
  { /* sortStrArray */
    String sortedData[]= new String[len];
    sortedData= bubbleSort(unsortedData);
    return(sortedData);
  } /* sortStrArray */
  
  
  /**
   * bubbleSort() - Sort String array via bubble sort.
   * @param data array of data to be sorted
   * @return the new sorted string[] array
   */
  static String[] bubbleSort(String data[])
  { /* bubbleSort */
    return(bubbleSort(data, data.length));
  } /* bubbleSort */
  
  
  /**
   * bubbleSort() - Sort String array via bubble sort w/len
   * @param data array of data to be sorted
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @return the new sorted string[] array
   */
  static String[] bubbleSort(String data[], int len)
  { /* bubbleSort */
    if(data==null || len==0)
      return(data);
    
    String
      dataUCJM1,
      dataUCJ,
      tempStr,
      dataUC[]= new String[len];
    int lenMinus1= len-1;
    
    /* convert temp array to upper case  */
    for (int i= 0; i < len; i++)
      dataUC[i]= data[i].toUpperCase();
    
    for(int i= 0; i < len; i++)
    {
      for(int j= lenMinus1; j > i; j--)
      {
        dataUCJM1= dataUC[j-1];
        dataUCJ= dataUC[j];
        
        if(dataUCJM1.compareTo(dataUCJ) > 0)
        { /* exchange */
          tempStr= data[j-1];  /* parallel sort the data */
          data[j-1]= data[j];
          data[j]= tempStr;
          
          dataUC[j-1]= dataUCJ;
          dataUC[j]= dataUCJM1;
        }
      }
    }
    
    return(data);
  } /* bubbleSort */
  
  
  /**
   * bubbleSortIndex() - sort copy of String[0:len-1] data with bubble sort, return index[].
   * Do NOT actually sort the original data[].
   * @param data array of data to be sorted
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @param ascending sort if true
   * @return the index[] of the sorted data
   */
  static int[] bubbleSortIndex(String data[], int len, boolean ascending)
  { /* bubbleSortIndex */
    if(data==null || len==0)
      return(null);
    
    String
      dataUCJM1,
      dataUCJ,
      tempStr,
      dataUC[]= new String[len];
    int
      oldJm1,
      index[]= new int[len],
      j,
      strCmp,
      lenMinus1= (len-1);
    
    /* convert temp array to upper case  */
    for (int i= 0; i < len; i++)
    {
      dataUC[i]= data[i].toUpperCase();
      index[i]= i;
    }
    
    /* Do the sort */
    for(int i= 0; i < len; i++)
    {
      for(j= lenMinus1; j > i; j--)
      {
        dataUCJM1= dataUC[j-1];
        dataUCJ= dataUC[j];
        strCmp= dataUCJM1.compareTo(dataUCJ);
        if((ascending && strCmp > 0) || (!ascending && strCmp < 0))
        { /* exchange */
          oldJm1= index[j-1];  /* parallel sort the index and dataUC */
          index[j-1]= index[j];
          index[j]= oldJm1;
          
          dataUC[j-1]= dataUCJ;
          dataUC[j]= dataUCJM1;
        }
      }
    }
    
    return(index);
  } /* bubbleSortIndex */
  
  
  /**
   * bubbleSortIndex() - sort copy of short[0:len-1] data with bubble sort, return index[].
   * Do NOT actually sort the original data[].
   * @param data array of data to be sorted
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @param ascending sort if true
   * @return the index[] of the sorted data
   */
  static int[] bubbleSortIndex(short data[], int len, boolean ascending)
  { /* bubbleSortIndex */
    if(data==null || len==0)
      return(null);
    
    short
      dataCJM1,
      dataCJ,
      dataC[]= new short[len];
    int
      j,
      strCmp,
      lenMinus1=(len-1),
      oldJm1,
      index[]= new int[len];

    /* copy data and create default cardinal numbering */
    for (int i= 0; i < len; i++)
    {
      dataC[i]= data[i];
      index[i]= i;
    }
    
    /* Do the sort */
    for(int i= 0; i < len; i++)
    {
      for(j= lenMinus1; j > i; j--)
      {
        dataCJM1= dataC[j-1];
        dataCJ= dataC[j];
        if(ascending && (dataCJM1 > dataCJ) ||
           (!ascending && (dataCJM1 < dataCJ)))
        { /* exchange */
          oldJm1= index[j-1];  /* parallel sort index and dataUC */
          index[j-1]= index[j];
          index[j]= oldJm1;
          
          dataC[j-1]= dataCJ;
          dataC[j]= dataCJM1;
        }
      }
    }
    
    return(index);
  } /* bubbleSortIndex */
  
  
  /**
   * bubbleSortIndex() - sort copy of int[0:len-1] data with bubble sort, return index[].
   * @param data array of data to be sorted
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @param ascending sort if true
   * @return the index[] of the sorted data
   */
  static int[] bubbleSortIndex(int data[], int len, boolean ascending)
  { /* bubbleSortIndex */
    if(data==null || len==0)
      return(null);
    
    int
      oldJm1,
      index[]= new int[len],
      j,
      dataCJM1,
      dataCJ,
      lenMinus1= len-1,
      dataC[]= new int[len];
    
    /* copy data and create default cardinal numbering */
    for (int i= 0; i < len; i++)
    {
      dataC[i]= data[i];
      index[i]= i;
    }
    
    /* Do the sort */
    for(int i= 0; i < len; i++)
    {
      for(j= lenMinus1; j > i; j--)
      {
        dataCJM1= dataC[j-1];
        dataCJ= dataC[j];
        if(ascending && (dataCJM1 > dataCJ) ||
           (!ascending && (dataCJM1 < dataCJ)))
        { /* exchange */
          oldJm1= index[j-1];  /* parallel sort index and dataC */
          index[j-1]= index[j];
          index[j]= oldJm1;
          
          dataC[j-1]= dataCJ;
          dataC[j]= dataCJM1;
        }
      }
    }
    
    return(index);
  } /* bubbleSortIndex */
  
  
  /**
   * bubbleSortIndex() - sort copy of float[0:len-1] data with bubble sort, return index[].
   * @param len size of subarray array of data to be sorted [0:len-1]
   * @param ascending sort if true
   * @return the index[] of the sorted data
   */
  static int[] bubbleSortIndex(float data[], int len, boolean ascending)
  { /* bubbleSortIndex */
    if(data==null || len==0)
      return(null);
    
    int
      oldJm1,
      index[]= new int[len],
      j,
      lenMinus1= len-1;
    float
      dataCJM1,
      dataCJ,
      dataC[]= new float[len];
    
    /* copy data and create default cardinal numbering */
    for (int i= 0; i < len; i++)
    {
      dataC[i]= data[i];
      index[i]= i;
    }
    
    /* Do the sort */
    for(int i= 0; i < len; i++)
    {
      for(j= lenMinus1; j > i; j--)
      {
        dataCJM1= dataC[j-1];
        dataCJ= dataC[j];
        if(ascending && (dataCJM1 > dataCJ) ||
        (!ascending && (dataCJM1 < dataCJ)))
        { /* exchange */
          oldJm1= index[j-1];  /* parallel sort index and dataC */
          index[j-1]= index[j];
          index[j]= oldJm1;
          
          dataC[j-1]= dataCJ;
          dataC[j]= dataCJM1;
        }
      }
    }
    
    return(index);
  } /* bubbleSortIndex */
  
  
  /**
   * uniqueInsertionSort() - insertion sort newStr into sList[] if unique.
   * Note: this is faster than the bubble sort after the list is constructed.
   * @param sList array of data to be sorted
   * @param sListUC array of parallel uppercase data to be sorted
   * @param sListLen size of subarray array of data to be sorted [0:sListLen-1]
   * @param newStr new data to insert
   * @return the size of the sorted array with new item inserted
   */
  static int uniqueInsertionSort(String sList[], String sListUC[],
                                 int sListLen, String newStr)
  { /* uniqueInsertionSort */
    int len= sListLen;
    String newStrUC= newStr.toUpperCase();
    
    /* [1] Handle empty list */
    if(len==0)
    {
      sList[len]= newStr;         /* push into empty list */
      sListUC[len++]= newStrUC;
      return(len);
    }
    
    /* [2] Look for replicates and ignore this instance if found */
    String dataIUC;
    int cmp;
    
    for (int i= 0; i < len; i++)
    { /* look for replicates or where to insert newStr*/
      dataIUC= sListUC[i];
      cmp= dataIUC.compareTo(newStrUC);  /* [TODO] BOTTLE NECK!!! */
      if(cmp==0)
        return(len);          /* is EQUAL. newStr is not unique -
         * ignore replicates */
      else if(cmp>0)
      { /* insert newStr before this entry */
        /* Copy sList[i:len-1] to sList[i+1:len] */
        System.arraycopy(sList,i, sList,(i+1), (len-i+1));
        System.arraycopy(sListUC,i, sListUC,(i+1), (len-i+1));
        
        sList[i]= newStr;   /* push into middle of the list */
        sListUC[i]= newStrUC;
        len++;
        return(len);
      }
    } /* look for replicates or where to insert newStr */
    
    /* [3] Insert at the end of the list */
    sList[len]= newStr;   /* push onto end of the list */
    sListUC[len++]= newStrUC;
    return(len);
  } /* uniqueInsertionSort */
  
  
  /**
   * uniqueInsertionSort() - insertion bubble sort s1,s2 into sList1[], sList2[]
   * if s1 is unique.
   * Note: this is faster than the bubble sort after the list is constructed.
   * @param sList1 array of data to be sorted
   * @param sList2 array of data to be sorted
   * @param sListUC array of parallel uppercase data to be sorted
   * @param sListLen size of subarray array of data to be sorted [0:sListLen-1]
   * @param s1 new data to insert
   * @param s2 new data to insert
   * @return the size of the (possibly modified) list.
   */
  static int uniqueInsertionSort(String sList1[], String sList2[],
                                 String sList1UC[], int sListLen,
                                 String s1, String s2)
  { /* uniqueInsertionSort */
    int
      cmp,
      len= sListLen;
    String
      dataIUC,
      s1UC= s1.toUpperCase();
    
    /* [1] Handle empty list */
    if(len==0)
    { /* push into empty list */
      sList1[len]= s1;
      sList2[len]= s2;
      sList1UC[len++]= s1UC;
      return(len);
    }    
    
    /* [2] Look for replicates and ignore this instance if found */
    for (int i= 0; i < len; i++)
    { /* look for replicates or where to insert newStr*/
      dataIUC= sList1UC[i];
      cmp= dataIUC.compareTo(s1UC);  /* [TODO] BOTTLE NECK!!! */
      if(cmp==0)
        return(len);          /* is EQUAL. newStr is not unique -
         * ignore replicates */
      else if(cmp>0)
      { /* insert newStr before this entry */
        /* Copy sList[i:len-1] to sList[i+1:len] */
        System.arraycopy(sList1,i, sList1,(i+1), (len-i+1));
        System.arraycopy(sList2,i, sList2,(i+1), (len-i+1));
        System.arraycopy(sList1UC,i, sList1UC,(i+1), (len-i+1));
        
        sList1[i]= s1;   /* push into middle of the list */
        sList2[i]= s2;
        sList1UC[i]= s1UC;
        len++;
        return(len);
      }
    } /* look for replicates or where to insert newStr */
    
    
    /* [3] Insert at the end of the list */
    sList1[len]= s1;   /* push onto end of the list */
    sList2[len]= s2;
    sList1UC[len++]= s1UC;
    return(len);
  } /* uniqueInsertionSort */
  
  
  /**
   * sortArray() - bubble sort string array either ASCENDING or DESCENDING.
   * This uses a bubble sort.
   * [TODO] extend this to deal with trailing numbers
   * eg. A1, A10, A2 should be sorted as A1, A2, A10.
   * Should be able to use a RuleBasedCollator...
   * @param data array of data to be sorted
   * @param sortAscending direction of sort
   * @return the sorted list.
   */
  static String[] sortArray(String data[], boolean sortAscending)
  { /* sortArray */
    if(data==null || data.length==1)
      return(data);
    
    int
      n,
      strCmp,
      jMin= 0,
      lth= data.length,
      lth1= lth-1;
    String
      dataJ,
      dataJ1;
    Collator col= Collator.getInstance();
    
    for(int i= 0; i<lth-1; i++)
    { /* bubble sort */
      n= jMin;
      for(int j= (lth-2); j>=n; j--)
      { /* compare data[j] with data[j+1] & swap if out of order*/
        dataJ= data[j];
        dataJ1= data[j+1];
        strCmp= col.compare(dataJ, dataJ1);
        if((sortAscending && strCmp >0) || ((!sortAscending && strCmp <0)))
        { /* swap j and j+1 index */
          data[j]= dataJ1;
          data[j+1]= dataJ;
          jMin= j;
        } /* swap j and j+1 index */
      } /* compare data[j] with data[j+1] & swap if out of order*/
    } /* bubble sort */
    
    return(data);
  } /* sortArray */
  
  
  /**
   * uniqueInsert() - insert s1 at end of sList1[] and UC of s1 in sList1UC[]
   * if s1 is not already in sList1[].
   * Note: this is to be used with the quickSortMultLists() for these arrays
   * after they are constructed.
   * @param sList1 array of data to be sorted
   * @param sListUC array of parallel uppercase data to be sorted
   * @param sListLen size of subarray array of data to be sorted [0:sListLen-1]
   * @param newStr sort if true
   * @return the size of the (possibly modified) list.
   */
  static int uniqueInsert(String sList1[], String sList1UC[], int sListLen,
                          String s1, Hashtable ht)
  { /* uniqueInsert */
    int len= sListLen;
    
    /* [1] If s1 is in the s1List[], do not add it again. */
    /*
    for(int i= 0;i<len;i++)
      if(s1.equals(sList1[i]))
        return(len);
    */
    if(ht.containsKey(s1))
      return(len);	             /* exists, so don't add it */
    else
    { /* add (key,value) to hash table*/
      Integer val= new Integer((int)(len+1));
      ht.put(s1,val);
    }
    
    /* [2] Insert at the end of the list */
    sList1[len]= s1;   /* push onto end of the list */
    sList1UC[len++]= s1.toUpperCase();
    
    return(len);
  } /* uniqueInsert */
  
  
  /**
   * uniqueInsert() - insert s1,s2 at end of sList1[], sList2[] lists
   * if s1 is not already in sList1[]. Insert UC of s1 into sList1UC[].
   * Note: this is to be used with the quickSortMultLists() for these arrays
   * after they are constructed.
   * @param sList1 array of data to be sorted
   * @param sList2 array of data to be sorted
   * @param sListUC array of parallel uppercase data to be sorted
   * @param sListLen size of subarray array of data to be sorted [0:sListLen-1]
   * @param s1 data to insert
   * @param s2 data to insert
   * @param ht hashtable to use
   * @return the size of the (possibly modified) list.
   */
  static int uniqueInsert(String sList1[], String sList2[], String sListUC[],
                          int sListLen, String s1, String s2, Hashtable ht)
  { /* uniqueInsert */
    int
    len= sListLen;
    
    /* [1] Handle empty list */
    /*
    for(int i= 0;i<len;i++)
      if(s1.equals(sList1[i]))
        return(len);
    */
    if(ht.containsKey(s1))
      return(len);	             /* exists, so don't add it */
    else
    { /* add (key,value) to hash table*/
      Integer val= new Integer((int)(len+1));
      ht.put(s1,val);
    }
    
    /* [2] Insert at the end of the list */
    sList1[len]= s1;   /* push onto end of the list */
    sList2[len]= s2;
    sListUC[len++]= s1.toUpperCase();
    
    return(len);
  } /* uniqueInsert */
  
  
  /**
   * quickSort() - sort the int[] array.
   * Based on the QuickSort method by James Gosling from Sun's SortDemo applet
   * @param a array of data to sort
   * @param lo0 lower bound of array
   * @param hi0 uppper bound of array
   */
  static void quickSort(int a[], int lo0, int hi0)
  { /* quickSort */
    int
      lo= lo0,
      hi= hi0,
      mid,
      t;
    
    if (hi0 > lo0)
    {  /* need to sort */
      mid= a[(lo0 + hi0)/2];
      while(lo <= hi)
      { /* check if swap within range */
        while((lo < hi0) && (a[lo] < mid) )
          ++lo;
        while((hi > lo0) && (a[hi] > mid) )
          --hi;
        if(lo <= hi)
        {
          t= a[lo];
          a[lo]= a[hi];
          a[hi]= t;
          ++lo;
          --hi;
        }
      } /* check if swap within range */
      
      if(lo0 < hi)
        quickSort(a, lo0, hi);
      if(lo < hi0)
        quickSort(a, lo, hi0);
    } /* need to sort */
  } /* quickSort */
  
  
  /**
   * quickSort() - sort the String[] array.
   * Based on the QuickSort method by James Gosling from Sun's SortDemo applet
   * @param a array of data to sort
   * @param lo0 lower bound of array
   * @param hi0 uppper bound of array
   */
  static void quickSort(String a[], int lo0, int hi0)
  { /* quickSort */
    int
      lo= lo0,
      hi= hi0;
    String
      mid,
      midUC,
      t;
    
    if (hi0 > lo0)
    { /* check if swap within range */
      mid= a[(lo0 + hi0)/2];
      midUC= mid.toUpperCase();
      while(lo <= hi)
      { /* check if swap within range */
        while((lo < hi0) && (a[lo].toUpperCase().compareTo(midUC) < 0) )
          ++lo;
        while((hi > lo0) && (a[hi].toUpperCase().compareTo(midUC) > 0) )
          --hi;
        if(lo <= hi)
        {
          t= a[lo];
          a[lo]= a[hi];
          a[hi]= t;
          ++lo;
          --hi;
        }
      } /* check if swap within range */
      
      if(lo0 < hi)
        quickSort(a, lo0, hi);
      if(lo < hi0)
        quickSort(a, lo, hi0);
    } /* check if swap within range */
  } /* quickSort */
  
  
  /**
   * quickSortMultLists() - sort String lists (a[], a[UC[]) by aUC[].
   * Requires that aUC[] be a[].toUppercase().
   * Based on the QuickSort method by James Gosling from Sun's SortDemo applet
   * @param a array of data to sort
   * @param aUC  upper case version of array a[]
   * @param lo0 lower bound of array
   * @param hi0 uppper bound of array
   */
  static void quickSortMultLists(String a[], String aUC[], int lo0, int hi0)
  { /* quickSortMultLists */
    int
      lo= lo0,
      hi= hi0;
    String
      midUC,
      t;
    
    if (hi0 > lo0)
    { /* need to sort */
      midUC= aUC[(lo0 + hi0)/2];
      
      while(lo <= hi)
      { /* check if swap within range */
        while((lo < hi0) && (aUC[lo].compareTo(midUC) < 0) )
          ++lo;
        while((hi > lo0) && (aUC[hi].compareTo(midUC) > 0) )
          --hi;
        if(lo <= hi)
        { /* swap both lists */
          t= a[lo];          /* swap a[lo] with a[hi] */
          a[lo]= a[hi];
          a[hi]= t;
          
          t= aUC[lo];          /* swap aUC[lo] with aUC[hi] */
          aUC[lo]= aUC[hi];
          aUC[hi]= t;
          
          ++lo;
          --hi;
        } /* swap both lists */
      } /* check if swap within range */
      
      if(lo0 < hi)
        quickSortMultLists(a, aUC, lo0, hi);
      
      if(lo < hi0)
        quickSortMultLists(a, aUC, lo, hi0);
    } /* need to sort */
  } /* quickSortMultLists */
  
  
  /**
   * quickSortMultLists() - sort String lists (a[], b[], aUC[]) by aUC[].
   * Requires that aUC[] be a[].toUppercase().
   * Based on the QuickSort method by James Gosling from Sun's SortDemo applet
   * @param a array of data to sort
   * @param b array of data to sort
   * @param aUC  upper case version of array a[]
   * @param lo0 lower bound of array
   * @param hi0 uppper bound of array
   */
  static void quickSortMultLists(String a[], String b[], String aUC[],
  int lo0, int hi0)
  { /* quickSortMultLists */
    int
      lo= lo0,
      hi= hi0;
    String
      midUC,
      t;
    
    if (hi0 > lo0)
    { /* need to sort */
      midUC= aUC[(lo0 + hi0)/2];
      
      while(lo <= hi)
      { /* check if swap within range */
        while((lo < hi0) &&
        (aUC[lo].compareTo(midUC) < 0) )
          ++lo;          /* find new lo */
        
        while((hi > lo0) &&
        (aUC[hi].compareTo(midUC) > 0) )
          --hi;          /* find new hi */
        
        if(lo <= hi)
        { /* swap both lists */
          t= a[lo];          /* swap a[lo] with a[hi] */
          a[lo]= a[hi];
          a[hi]= t;
          
          t= b[lo];          /* swap b[lo] with b[hi] */
          b[lo]= b[hi];
          b[hi]= t;
          
          t= aUC[lo];        /* swap aUC[lo] with aUC[hi] */
          aUC[lo]= aUC[hi];
          aUC[hi]= t;
          
          ++lo;
          --hi;
        } /* swap both lists */
      } /* check if swap within range */
      
      if(lo0 < hi)
        quickSortMultLists(a, b, aUC, lo0, hi);
      
      if(lo < hi0)
        quickSortMultLists(a, b, aUC, lo, hi0);
    } /* need to sort */
    
  } /* quickSortMultLists */
  
} /* end of class SortMAE */
