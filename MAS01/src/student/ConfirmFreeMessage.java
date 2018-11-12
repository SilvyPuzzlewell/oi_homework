package student;

import mas.agents.Message;

public class ConfirmFreeMessage extends Message{
	private boolean free;

	public boolean isFree() {
		return free;
	}

	public void setFree(boolean free) {
		this.free = free;
	}

	public ConfirmFreeMessage(boolean free) {
		super();
		this.free = free;
	}
	
}
