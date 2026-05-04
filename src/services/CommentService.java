package services;

import java.util.List;

import model.domain.Comment;
import model.domain.User;
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
        User author = userService.get(comment.getSenderId());
        return new CommentDTO(comment, author);
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {

            int deletedUserId = event.getUserId();
            List<Comment> list = this.getAll();

            for(Comment comment : list){
                if(comment.getSenderId() == deletedUserId){
                    this.delete(comment.getId());
                }
            }
            
        });
    }
    

}
