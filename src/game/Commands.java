package game;

import java.io.Serializable;

/*
 * The commad object which will be sent over and over again. 
 */

public class Commands implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	public boolean fire = false;
	public boolean endFire = false;
	
	private int direction;
	public final int UP = 1;
	public final int DOWN = 2;
	public final int LEFT = 3;
	public final int RIGHT = 4;
	
	private String cmdString;
	
	public Commands(){
		
	}
	
	
	//setters
	public void setDirection(int direction){
		this.direction = direction;
	}
	
	public void setCmd(String cmd){
		cmdString = cmd;
	}
	
	//Getters
	public int getDirection(){
		return direction;
	}
	
	public String getCmdString(){
		return cmdString;
	}
	

}
