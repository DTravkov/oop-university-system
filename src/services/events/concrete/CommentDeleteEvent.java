package services.events.concrete;

import model.domain.Comment;
import services.events.interfaces.CommentEvent;

public record CommentDeleteEvent(Comment comment) implements CommentEvent{

    public Comment getComment(){
        return comment;
    }

}
