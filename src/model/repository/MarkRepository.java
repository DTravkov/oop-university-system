package model.repository;

import model.domain.Course;
import model.domain.Mark;

public class MarkRepository extends MapRepository<Mark> {
    @Override
    protected String getFilePath() {
        return "courses.ser";
    }

    
}