package services;

import java.util.List;

import model.domain.Comment;
import model.dto.CommentDTO;
import model.repository.CommentRepository;
import services.events.CommentDeleteEvent;
import services.events.UserDeleteEvent;

public class CommentService extends BaseService<Comment, CommentRepository> {

    private final UserService userService;

    public CommentService(UserService userService) {
        super(CommentRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    @Override
    public Comment create(Comment comment) {
        userService.get(comment.getSenderId());
        return super.create(comment);
    }

    @Override
    public void delete(int commentId){
        Comment toDelete = this.get(commentId);
        eventSystem.publish(new CommentDeleteEvent(toDelete));
        super.delete(commentId);
    }

    public CommentDTO getDTO(int commentId) {
        Comment comment = get(commentId);
        return getDTO(comment);
    }

    public CommentDTO getDTO(Comment comment) {
        return new CommentDTO(comment, userService.getDTO(comment.getSenderId()));
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpUserCommentData(event.getUserId());
        });
    }

    public void cleanUpUserCommentData(int deletedUserId) {
        List<Comment> commentsToDelete = this.getAll()
                .stream()
                .filter(comment -> comment.getSenderId() == deletedUserId)
                .toList();
        commentsToDelete.forEach(this::delete);
    }


}
