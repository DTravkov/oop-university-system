package services;

import java.util.ArrayList;
import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.Comment;
import model.domain.Manager;
import model.domain.News;
import model.domain.User;
import model.dto.CommentDTO;
import model.dto.NewsDTO;
import model.enumeration.NewsUrgencyLevel;
import model.repository.NewsRepository;
import services.events.CommentDeleteEvent;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Comparators;

public class NewsService extends BaseService<News, NewsRepository>{

    private final UserService userService;
    private final CommentService commentService;

    public NewsService(UserService userService, CommentService commentService) {
        super(NewsRepository.getInstance());
        this.userService = userService;
        this.commentService = commentService;
        subscribeToEvents();
    }

    public void postNews(News news) {
        User publisher = userService.get(news.getPublisherId());
        
        if (!(publisher instanceof Manager)) {
            throw new OperationNotAllowed(" sending messages to/from non-manager account");
        }

        super.create(news);
    }


    public List<Comment> getAllCommentsById(int newsId) {
        List<Comment> list = new ArrayList<>();

        this.get(newsId).getComments()
                        .forEach(commentId -> list.add(commentService.get(commentId)));

        return list;
    }

    public List<News> getAllByUrgency(NewsUrgencyLevel urgencyLevel) {
        return repository.findAllByUrgency(urgencyLevel);
    }

    public NewsDTO getDTO(int newsId) {
        News news = get(newsId);
        return getDTO(news);
    }

    public NewsDTO getDTO(News news) {
        List<CommentDTO> commentDtos = getAllCommentsById(news.getId()).stream()
                .map(commentService::getDTO)
                .toList();
        return new NewsDTO(news, userService.getDTO(news.getPublisherId()), commentDtos);
    }

    public void assignComment(int newsId, int commentId) {
        News news = this.get(newsId);

        news.addComment(commentId);

        this.update(news);
    }

    @Override
    public List<News> getAll() {
        return super.getAll().stream().sorted(Comparators.NEWS_RESEARCH_PRIORITIZED).toList();
    }


    @Override
    public void subscribeToEvents(){
        
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpDeletedPublisherData(event.getUserId());
        });


        eventSystem.subscribe(CommentDeleteEvent.class, event -> {
            cleanUpDeletedCommentData(event.getCommentId());
        });
    }

    public void cleanUpDeletedPublisherData(int deletedUserId) {
        List<News> list = this.getAll();
        for (News news : list) {
            if (news.getPublisherId() == deletedUserId) {
                news.setPublisherId(AppSettings.DELETED_USER_ID);
            }
        }
        this.saveAll();
    }

    public void cleanUpDeletedCommentData(int deletedCommentId) {
        List<News> list = this.getAll();
        for (News news : list) {
            if (news.getComments().contains(Integer.valueOf(deletedCommentId))) {
                news.removeComment(deletedCommentId);
            }
        }
        this.saveAll();
    }
}
