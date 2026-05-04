package model.repository;

import java.util.List;

import model.domain.Comment;

public class CommentRepository extends Repository<Comment> {

    private static final CommentRepository INSTANCE = new CommentRepository();

    private CommentRepository() {
        super();
    }

    public static CommentRepository getInstance() {
        return INSTANCE;
    }

}
