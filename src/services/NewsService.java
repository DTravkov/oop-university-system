package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Comment;
import model.domain.Manager;
import model.domain.News;
import model.domain.User;
import model.enumeration.NewsUrgencyLevel;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Comparators;
import utils.Logger;

public class NewsService extends BaseService<News>{


    public NewsService() {
        super(News.class);
        subscribeToEvents();
    }

    public void postNews(News news) {
        super.create(news);
    }

    public void deleteNews(News news, Manager manager) {
        if(news.getPublisher().getId() != manager.getId()){
            throw new OperationNotAllowed("deleting other managers' news");
        }
        super.delete(news);
    }

    public void assignComment(News news, Comment comment) {
        news.addComment(comment);
        Logger.log("Assign comment by sender (" + comment.getSender().asLine() + ") to news (" + news.getId() + ")");
        this.update(news);
    }
    

    public List<News> getAllByUrgency(NewsUrgencyLevel urgencyLevel) {
        return getAll().stream()
                    .filter(n -> n.getUrgencyLevel()
                    .equals(urgencyLevel)).toList();
    }
    

    @Override
    public List<News> getAll() {
        return super.getAll().stream().sorted(Comparators.NEWS_RESEARCH_PRIORITIZED).toList();
    }

    @Override
    public void subscribeToEvents(){
        
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            onUserDelete(event.getUser());
        });

    }

    public void onUserDelete(User deletedUser) {
        if(deletedUser instanceof Manager){
            getAll().forEach(news -> {
                if (news.getPublisher().getId() == deletedUser.getId()) {
                    news.setPublisher(AppSettings.DELETED_USER);
                }
            });
        }
        repository.saveAll();
    }

}
