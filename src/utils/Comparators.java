package utils;

import java.util.Comparator;

import model.domain.News;
import model.domain.TechRequest;
import model.enumeration.NewsUrgencyLevel;

public class Comparators {
    public static Comparator<News> getNewsResearchComparator(){
        return ((n1,n2) -> n1.getUrgencyLevel() == NewsUrgencyLevel.RESEARCH ? -1 : 1);
    }

    public static Comparator<TechRequest> getTechRequestComparator(){
        return ((n1,n2) -> Integer.compare(n1.getStatus().getStage(), n2.getStatus().getStage()));
    }
}
