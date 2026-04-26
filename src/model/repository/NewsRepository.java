package model.repository;

import model.domain.Comment;
import model.domain.News;
import model.enumeration.NewsUrgencyLevel;

import java.util.List;


public class NewsRepository extends Repository<News> {

    private static final NewsRepository INSTANCE = new NewsRepository();

    private NewsRepository() {
        super();
    }

    public static NewsRepository getInstance() {
        return INSTANCE;
    }

    public List<News> findAllByUrgency(NewsUrgencyLevel urgencyLevel){
        return this.findAll(entity -> entity.getUrgencyLevel() == urgencyLevel);
    }

}
