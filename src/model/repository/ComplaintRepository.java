package model.repository;


import model.domain.TeacherComplaint;

import java.util.Collection;


public class ComplaintRepository extends Repository<TeacherComplaint> {

    private static final ComplaintRepository INSTANCE = new ComplaintRepository();

    private ComplaintRepository() {
        super();
    }

    public static ComplaintRepository getInstance() {
        return INSTANCE;
    }

    public Collection<TeacherComplaint> findAllByTeacherId(int teacherId){
        return this.data.values().stream()
                .filter(entity -> entity.getSenderId() == teacherId)
                .toList();
    }

    public Collection<TeacherComplaint> findAllByDeanId(int deanId){
        return this.data.values().stream()
                .filter(entity -> entity.getReceiverId() == deanId)
                .toList();
    }

}
