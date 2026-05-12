package services;

import java.util.List;
import java.util.Random;

import model.domain.Employee;
import model.domain.TechRequest;
import model.domain.TechSupportSpecialist;
import model.domain.User;
import services.events.UserDeleteEvent;
import settings.AppSettings;
import utils.Comparators;

public class TechRequestService extends BaseService<TechRequest>{

    public final UserService userService;
    public TechRequestService(UserService userService) {
        super(TechRequest.class);
        this.userService = userService;
        subscribeToEvents();
    }

    @Override
    public List<TechRequest> getAll() {
        return super.getAll().stream().sorted(Comparators.TECH_REQUEST_BY_STAGE).toList();
    }

    public List<TechRequest> getAllBySpecialist(TechSupportSpecialist specialist){
        return getAll().stream()
                    .filter(req -> req.getSpecialist().getId() == specialist.getId())
                    .toList();
    }

    public List<TechRequest> getAllByEmployee(Employee employee){
        return getAll().stream()
                    .filter(req -> req.getEmployee().getId() == employee.getId())
                    .toList();
    }


    @Override
    public void subscribeToEvents(){
        eventSystem.subscribe(UserDeleteEvent.class, event -> {
            cleanUpUserTechRequestData(event.getUser());
        });

    }

    public void cleanUpUserTechRequestData(User user) {

        if(user instanceof TechSupportSpecialist specialist){
            Random random = new Random();
            List<TechSupportSpecialist> otherSpecialists = userService.getAllByClass(TechSupportSpecialist.class);
            getAllBySpecialist(specialist).forEach(req -> {
                if(!otherSpecialists.isEmpty()){
                    TechSupportSpecialist replacement = otherSpecialists.get(random.nextInt(0, otherSpecialists.size()-1));
                    req.setSpecialist(replacement);
                    return;
                }
                req.setSpecialist(AppSettings.DELETED_TECH_SUPPORT_SPECIALIST);
            });
        }

        if(user instanceof Employee employee){
            getAllByEmployee(employee).forEach(req -> this.delete(req));
        }
    }

}
