package utils;

import java.util.Comparator;
import java.util.Date;

import model.domain.News;
import model.domain.ResearchPaper;
import model.domain.TechRequest;
import model.enumeration.NewsUrgencyLevel;

public class Comparators {

    
    public static Comparator<News> NEWS_RESEARCH_PRIORITIZED = ((n1,n2) -> n1.getUrgencyLevel() == NewsUrgencyLevel.RESEARCH ? -1 : 1);
    
    public static Comparator<TechRequest> TECH_REQUEST_BY_STAGE = ((req1,req2) -> Integer.compare(req1.getStatus().getStage(), req2.getStatus().getStage()));

    public static Comparator<ResearchPaper> RESEARCH_PAPER_BY_DATE = ((rp1,rp2) -> rp1.getPublishDate().compareTo(rp2.getPublishDate()));

    public static Comparator<ResearchPaper> RESEARCH_PAPER_BY_CITATIONS_DESC = ((rp1,rp2) -> Integer.compare(rp2.getCitations(), rp1.getCitations()));

}
