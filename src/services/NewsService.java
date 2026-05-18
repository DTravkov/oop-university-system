package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Comment;
import model.domain.Manager;
import model.domain.News;
import model.enumeration.NewsUrgencyLevel;
import utils.Comparators;
import utils.Logger;
import utils.UIText;

/**
 * NewsService is a concrete service. It implements logic for news and comments: publishing, filters,
 * manager-scoped deletes, and a sorted listing view for the application layer.
 */
public class NewsService extends GenericService<News>{


    public NewsService() {
        super(News.class);
    }

    // CREATE / UPDATE / DELETE

    public void delete(News news, Manager manager) {
        news.deleteBy(manager);
        Logger.log("Deleted news (" + news.getId() + ")");
        repository.delete(news);
    }

    /**
     * this method must not be used. it doesnt check that manager owns news.
     * instead, use other override
     */
    @Override
    public void delete(News news) {
        throw new OperationNotAllowed(UIText.ERR_NEWS_DELETE_METHOD);
    }

    public void assignComment(News news, Comment comment) {
        news.addComment(comment);
        Logger.log("Sent comment (" + comment.getId() + ") to news (" + news.getId() + ")");
        this.update(news);
    }
    

    // QUERIES

    public List<News> getNewsByUrgency(NewsUrgencyLevel urgencyLevel) {
        return getAll(n -> n.getUrgencyLevel().equals(urgencyLevel));
    }

    public List<News> getNewsByManager(Manager manager){
        return getAll(n -> n.getPublisher().equals(manager));
    }
    

    @Override
    public List<News> getAll() {
        return super.getAll().stream().sorted(Comparators.NEWS_RESEARCH_PRIORITIZED).toList();
    }



}
