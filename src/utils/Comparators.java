package utils;

import java.util.Comparator;

import model.domain.News;
import model.domain.ResearchPaper;
import model.domain.TechRequest;
import model.domain.User;
import model.enumeration.NewsUrgencyLevel;

/**
 * Comparators is a static list of {@link Comparator}s .
 * Used to sort data for application layer
 */
public class Comparators {

    private Comparators() {
    }

    public static final Comparator<News> NEWS_RESEARCH_PRIORITIZED =
            (n1, n2) -> n1.getUrgencyLevel() == NewsUrgencyLevel.RESEARCH ? -1 : 1;

    public static final Comparator<TechRequest> TECH_REQUEST_BY_STAGE =
            (req1, req2) -> Integer.compare(req1.getStatus().getStage(), req2.getStatus().getStage());

    public static final Comparator<ResearchPaper> RESEARCH_PAPER_BY_DATE =
            (rp1, rp2) -> -rp1.getPublishDate().compareTo(rp2.getPublishDate());

    public static final Comparator<ResearchPaper> RESEARCH_PAPER_BY_CITATIONS_DESC =
            (rp1, rp2) -> Integer.compare(rp2.getCitations(), rp1.getCitations());

    public static final Comparator<ResearchPaper> RESEARCH_PAPER_BY_VIEWS_DESC =
            (rp1, rp2) -> Integer.compare(rp2.getViews(), rp1.getViews());

    public static final Comparator<User> USER_BY_FULL_NAME = (u1, u2) -> {
        String fullNameOne = u1.getFullname().toLowerCase().replace(" ", "");
        String fullNameTwo = u2.getFullname().toLowerCase().replace(" ", "");
        return fullNameOne.compareTo(fullNameTwo);
    };
}
