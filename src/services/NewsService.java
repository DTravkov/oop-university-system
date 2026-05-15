package services;

import java.util.List;

import model.domain.Comment;
import model.domain.Manager;
import model.domain.News;
import model.enumeration.NewsUrgencyLevel;
import utils.Comparators;
import utils.Logger;

/**
 * NewsService is a concrete service. It implements logic for news and comments: publishing, filters,
 * manager-scoped deletes, and a sorted listing view for the application layer.
 */
public class NewsService extends BaseService<News>{


    public NewsService() {
        super(News.class);
    }

    // CREATE / UPDATE / DELETE

    public void delete(News news, Manager manager) {
        news.deleteBy(manager);
        Logger.log("Deleted news (" + news.getId() + ")");
        repository.delete(news);
    }

    @Override
    public void delete(News news) {
        throw new RuntimeException("Illegal delete method is used. refer to other overload");
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
