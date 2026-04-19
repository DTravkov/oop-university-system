package model.repository;

import model.domain.Enrollment;
import model.domain.Mark;
import model.domain.Student;

import java.util.*;

public class MarkRepository extends Repository<Mark> {

    public MarkRepository() {
        super("marks.ser");
    }

    public HashMap<Student, Mark> getStudentMarks(Collection<Student> students){
        HashMap<Student, Mark> resultSet = new HashMap<>();
        for(Student student : students){

            for(Mark mark : this.data.values()){
                if(mark.getStudent().getId() == student.getId()){
                    resultSet.put(student,mark);
                }
            }

        }
        return resultSet;
    }

}
