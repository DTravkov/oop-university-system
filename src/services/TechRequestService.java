package services;

import java.util.List;

import exceptions.OperationNotAllowed;
import model.domain.IMessagable;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import model.dto.TechRequestDTO;
import model.enumeration.TechRequestStatus;
import model.repository.TechRequestRepository;
import services.events.UserDeleteEvent;
import settings.AppSettings;

public class TechRequestService extends BaseService<TechRequest, TechRequestRepository>{

    private final UserService userService;

    public TechRequestService(UserService userService) {
        super(TechRequestRepository.getInstance());
        this.userService = userService;
        subscribeToEvents();
    }

    public TechRequest sendRequest(TechRequest request) {

        User from = userService.get(request.getSenderId());
        User to = userService.get(request.getReceiverId());

        if(from == to){
            throw new OperationNotAllowed(" sending a technical request to yourself");
        }
        if(!(from instanceof IMessagable)){
            throw new OperationNotAllowed(" sending technical requests from " + from.getClass().getSimpleName() + " account");
        }
        if(!(to instanceof TechSupportSpecialist)){
            throw new OperationNotAllowed(" sending technical requests to " + to.getClass().getSimpleName() + " account");
        }

        return super.create(request);
    }

    public void updateRequest(TechRequest updated){
        super.update(updated);
    }

    public List<TechRequest> getAll(){
        return repository.findAll();
    }

    public List<TechRequest> getAllBySpecialistId(int specialistId){
        return repository.findAllBySpecialistId(specialistId);
    }

    public List<TechRequest> getAllBySenderId(int senderId){
        return repository.findAllBySenderId(senderId);
    }

    public List<TechRequest> getAllByStatus(TechRequestStatus status){
        return repository.findAllByStatus(status);
    }

    public TechRequestDTO getDTO(int requestId){
        TechRequest request = get(requestId);
        return new TechRequestDTO(
                request,
                userService.getDTO(request.getSenderId()),
                userService.getDTO(request.getReceiverId())
        );
    }

    public TechRequestDTO getDTO(TechRequest request){
        return new TechRequestDTO(
                request,
                userService.getDTO(request.getSenderId()),
                userService.getDTO(request.getReceiverId())
        );
    }

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, eventData -> {
            cleanUpUserTechRequestData(eventData.getUserId());
        });

    }

    public void cleanUpUserTechRequestData(int deletedUserId) {
        this.getAll().forEach((req) -> {
            if (req.getSenderId() == deletedUserId) {
                req.setSenderId(AppSettings.DELETED_USER_ID);
            }
            if (req.getReceiverId() == deletedUserId) {
                if (req.getStatus() != TechRequestStatus.DONE) {
                    req.setStatus(TechRequestStatus.PENDING);
                }
                req.setReceiverId(AppSettings.DELETED_USER_ID);
            }
        });
        this.saveAll();
    }

}
