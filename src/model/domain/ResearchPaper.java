package model.domain;
import java.util.Date;
import java.util.List;
public class ResearchPaper extends SerializableModel {
    private static final long serialVersionUID = 1L;

    private int views;
    private int citations;
    private Date publishDate;
    private List<Integer> researchers;
    private List<Integer> researchReferences;
    
}
