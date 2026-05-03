package model.repository;

import java.util.List;

import model.domain.*;
import model.enumeration.TechRequestStatus;
import utils.Comparators;

public class TechRequestRepository extends Repository<TechRequest> {

    private static final TechRequestRepository INSTANCE = new TechRequestRepository();

    private TechRequestRepository() {
        super();
    }

    public static TechRequestRepository getInstance() {
        return INSTANCE;
    }

    @Override
    public List<TechRequest> findAll() {
        return super.findAll().stream()
                              .sorted(Comparators.getTechRequestComparator())
                              .toList();
    }

    public List<TechRequest> findAllBySpecialistId(int specialistId) {
        return super.findAll(req -> req.getReceiverId() == specialistId);
    }

    public List<TechRequest> findAllByStatus(TechRequestStatus status) {
        return super.findAll(req -> req.getStatus().getStage() == status.getStage());
    }

}
