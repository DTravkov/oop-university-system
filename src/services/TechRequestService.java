package services;

import java.util.List;
import java.util.Random;

import model.domain.Employee;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import services.events.concrete.UserDeleteEvent;
import utils.Comparators;

/**
 * TechRequestService is a concrete service. It implements logic for IT support tickets: listing by employee or specialist,
 * stable ordering for the UI, and reassigning work when a tech specialist account is removed.
 */
public class TechRequestService extends GenericService<TechRequest>{

    private final UserService userService;

    public TechRequestService(UserService userService) {
        super(TechRequest.class);
        this.userService = userService;
    }

    // QUERIES

    @Override
    public List<TechRequest> getAll() {
        return super.getAll().stream().sorted(Comparators.TECH_REQUEST_BY_STAGE).toList();
    }

    public List<TechRequest> getTechRequestsBySpecialist(TechSupportSpecialist specialist){
        return getAll(req -> req.getSpecialist().getId() == specialist.getId());
    }

    public List<TechRequest> getTechRequestsByEmployee(Employee employee){
        return getAll(req -> req.getEmployee().getId() == employee.getId());
    }


    // EVENT HANDLING

    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> onUserDelete(event.getUser()));

    }

    /**
     * Each time tech specialist is deleted, his tasks are given to other tech specialists that are available.
     */ 
    public void onUserDelete(User user) {

        if(user instanceof TechSupportSpecialist specialist){
            Random random = new Random();

            List<TechSupportSpecialist> otherSpecialists = 
                userService.getUsersByClass(TechSupportSpecialist.class)
                            .stream().filter(s -> !s.isBanned() && !s.isDeleted() && !s.equals(specialist))
                            .toList();

            if(otherSpecialists.isEmpty()){
                getTechRequestsBySpecialist(specialist).forEach(tr -> delete(tr));
                return;
            }
            getTechRequestsBySpecialist(specialist).forEach(req -> {
                TechSupportSpecialist replacement = otherSpecialists.get(random.nextInt(0, otherSpecialists.size()));
                req.setSpecialist(replacement);
            });
            repository.saveAll();
        }

    }

}
