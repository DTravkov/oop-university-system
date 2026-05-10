package services.events;

import model.domain.Comment;

public record CommentDeleteEvent(Comment comment) implements Event{

    public Comment getComment(){
        return comment;
    }

}
