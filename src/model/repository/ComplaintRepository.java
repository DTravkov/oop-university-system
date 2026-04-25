package model.repository;


import model.domain.TeacherComplaint;

import java.util.List;


public class ComplaintRepository extends Repository<TeacherComplaint> {

    private static final ComplaintRepository INSTANCE = new ComplaintRepository();

    private ComplaintRepository() {
        super();
    }

    public static ComplaintRepository getInstance() {
        return INSTANCE;
    }

    public List<TeacherComplaint> findAllByTeacherId(int teacherId){
        return this.findAll(entity -> entity.getSenderId() == teacherId);
    }

    public List<TeacherComplaint> findAllByDeanId(int deanId){
        return this.findAll(entity -> entity.getReceiverId() == deanId);
    }

}
