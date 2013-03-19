package game;

/*
 * The stats for the upcomming game.
 */

public class GameStats {
	
	public boolean team, adminPassBool;
	public boolean newColors = false;
	private String adminPass;
	private int health = 10;
	
	public GameStats(){
		
	}
	
	public int getHealth(){
		return health;
	}
	
	public void setHealth(int health){
		this.health = health;
	}

}
