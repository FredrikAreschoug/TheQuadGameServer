package game;

import java.io.Serializable;

/*
 * All the information of the player.
 */

public class PlayerInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	private final int ID;
	private int x, y, team, direction, health;
	private float red, green, blue;
	private String userName = "anon";
	private String cmd;
	public boolean fire = false;
	public boolean dead = false;
	private String adminPass = "";
	


	/*
	 * Gives a random posision
	 */
	public PlayerInfo(int id) {
		this.ID = id;
		x = (int) Math.round(Math.random() * 780 + 10);
		y = (int) Math.round(Math.random() * 580 + 10);
		red = (float) Math.random();
		green = (float) Math.random();
		blue = (float) Math.random();
		health = 10;
		cmd = "";
	}
	
	// setters
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public void setColor(float red,float green, float blue){
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setDirection(int direction) {
		this.direction = direction;
	}

	public void fire(boolean fire) {
		this.fire = fire;
	}

	public void setTeam(int team) {
		this.team = team;
	}
	public void setHealth(int health){
		this.health = health;
	}
	public void setCmd(String cmd){
		this.cmd = cmd;
	}
	public void setAdminPass(String adminPass){
		this.adminPass = adminPass;
	}

	//changers
	public void changeTeam(){
		if(team == 1){
			team = 2;
		}else{
			team = 1;
		}
	}
	
	public void changeHealth(int denominator){
		health = health + denominator;
	}
	// getters
	public int getID() {
		return ID;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public String getUserName() {
		return userName;
	}

	public float getRed() {
		return red;
	}

	public float getGreen() {
		return green;
	}

	public float getBlue() {
		return blue;
	}

	public int getDirection() {
		return direction;
	}

	public boolean getFire() {
		return fire;
	}

	public int getTeam() {
		return team;
	}
	public int getHealt(){
		return health;
	}
	public String getCmd(){
		return cmd;
	}
	public String getAdminPass(){
		return adminPass;
	}
}
