package model.domain;

import java.io.Serializable;

public abstract class SerializableModel implements Serializable {
	protected int id;

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		this.id = id;
	}
	
}
