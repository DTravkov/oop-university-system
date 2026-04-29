package model.domain;

import java.util.Comparator;

import model.enumeration.NewsUrgencyLevel;

public class Comparators {
    public static Comparator<News> getNewsResearchComparator(){
        return ((n1,n2) -> n1.getUrgencyLevel() == NewsUrgencyLevel.RESEARCH ? -1 : 1);
    }
}
