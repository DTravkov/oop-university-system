package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Manager;
import model.domain.News;
import model.domain.User;
import model.enumeration.NewsUrgencyLevel;
import model.repository.NewsRepository;
import services.events.CommentDeleteEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Comparators;

public class NewsService extends BaseService<News, NewsRepository>{

    private final UserService userService;

    public NewsService(UserService userService) {
        super(NewsRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public void postNews(News news) {
        User publisher = userService.get(news.getPublisherId());
        
        if (!(publisher instanceof Manager)) {
            throw new OperationNotAllowed(" sending messages to/from non-manager account");
        }

        super.create(news);
    }

    public List<News> getAllByUrgency(NewsUrgencyLevel urgencyLevel) {
        return repository.findAllByUrgency(urgencyLevel);

    }

    public void assignComment(int newsId, int commentId) {
        News news = this.get(newsId);

        news.addComment(commentId);

        this.update(news);
    }

    @Override
    public List<News> getAll() {
        return super.getAll().stream().sorted(Comparators.getNewsResearchComparator()).toList();
    }


    @Override
    public void subscribeToEvents(){
        
        eventSystem.subscribe(UserDeleteEvent.class, event -> {

            int deletedUserId = event.getUserId();
            List<News> list = this.getAll();
            for(News news : list){
                if(news.getPublisherId() == deletedUserId){
                    news.setPublisherId(AppSettings.DELETED_USER_ID);
                    this.update(news);
                }
            }
            
        });


        eventSystem.subscribe(CommentDeleteEvent.class, event -> {

            int deletedCommentId = event.getCommentId();
            List<News> list = this.getAll();
            for(News news : list){
                if(news.getComments().contains(Integer.valueOf(deletedCommentId))){
                    news.removeComment(deletedCommentId);
                    this.update(news);
                }
            }

        });
    }
}
