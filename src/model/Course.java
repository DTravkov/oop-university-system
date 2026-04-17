package model;

import java.io.Serializable;

public class Course implements Serializable, Indexed{
	
    private static final long serialVersionUID = 1L;
    
    private int id;
	private String name;
    private String description;
    private int credits;
    private CourseType type;
    
	public Course(String name, String description, int credits, CourseType type) {
		this.name = name;
		this.description = description;
		this.credits = credits;
		this.type = type;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getCredits() {
		return credits;
	}

	public void setCredits(int credits) {
		this.credits = credits;
	}

	public CourseType getType() {
		return type;
	}

	public void setType(CourseType type) {
		this.type = type;
	}

	@Override
	public void setId(int id) {
		this.id = id;
	}
	@Override
	public int getId() {
		return this.id;
	}
	
	
	
	


}