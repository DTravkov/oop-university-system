package services.events;

import model.domain.Comment;

public record CommentDeleteEvent(Comment comment) implements Event{

    public int getCommentId(){
        return comment.getId();
    }
    public int getCommentSenderId(){
        return comment.getSenderId();
    }
    public String getCommentContent(){
        return comment.getContent();
    }

}
