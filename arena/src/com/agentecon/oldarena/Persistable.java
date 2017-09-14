package com.agentecon.oldarena;

public class Persistable implements IPersistable {
	
	private int id;

	@Override
	public int getId() {
		return id;
	}

	@Override
	public void setId(int id) {
		assert this.id == 0;
		this.id = id;
		assert this.id != 0;
	}

}
