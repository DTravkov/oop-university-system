package model.domain;

import exceptions.ImmutableFieldChanged;

import java.io.Serializable;

public abstract class SerializableModel implements Serializable {
	protected int id = 0;

	public int getId(){
		return this.id;
	}
	public void setId(int id){
		if(this.id == 0) {
			this.id = id;
			return;
		}
		throw new ImmutableFieldChanged();

	}
	
}
