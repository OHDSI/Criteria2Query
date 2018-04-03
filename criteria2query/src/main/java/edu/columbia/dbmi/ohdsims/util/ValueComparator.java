package edu.columbia.dbmi.ohdsims.util;

import java.util.Comparator;
import java.util.Map;

import edu.columbia.dbmi.ohdsims.pojo.ConceptSet;

public class ValueComparator implements Comparator<Map.Entry<ConceptSet, Integer>>  
{  
    public int compare(Map.Entry<ConceptSet, Integer> mp1, Map.Entry<ConceptSet, Integer> mp2)   
    {  
        return mp2.getValue() - mp1.getValue();  
    }  
}  