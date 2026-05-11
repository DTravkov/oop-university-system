package model.domain;

import exceptions.ImmutableFieldChanged;

import java.io.Serializable;

public abstract class SerializableModel implements Serializable {

	private static final long serialVersionUID = -2924509934968665706L;

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

	public boolean isNewRecord(){
		return this.id == 0;
	}

	/** Short single-line summary for lists and menus. */
	public abstract String asLine();

	/** Multi-line structured details. */
	public abstract String asTable();


	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		SerializableModel other = (SerializableModel) obj;
		if (id == 0 || other.id == 0) return false;
		return id == other.id;
	}


	@Override
	public int hashCode() {
		if (id != 0) {
			return Integer.hashCode(id);
		}
		return super.hashCode();
	}

	@Override
	public String toString() {
		return getClass().getSimpleName() + "[id=" + id + "]";
	}

}
