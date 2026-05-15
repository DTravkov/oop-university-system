package services.events.interfaces;

import model.domain.Comment;


public interface CommentEvent extends Event {
    public Comment getComment();
}
