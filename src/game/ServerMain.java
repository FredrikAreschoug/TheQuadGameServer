package game;

import java.io.*;
import java.net.*;
import java.util.ArrayList;

/* 
 * Waiting for connecetinos and seting upp streams to multiable clients.
 * Consist of 3 types threds,
 * 1: main: waiting for conections
 * 2: Handeling the information of each client 
 * 3: brodcases the information of the players. 
 */

public class ServerMain {
	private static int uniqueID = 0;
	private ArrayList<ClientThread> al;
	private int port;
	private boolean keepGoing = true;
	private boolean gameStarted = false;
	private ArrayList<PlayerInfo> allPlayerInfo;
	private GameStats gs = new GameStats();
	private String serverPass = "";
	

	// Constructor
	public ServerMain(int port) {
		this.port = port;
		al = new ArrayList<ClientThread>();
		allPlayerInfo = new ArrayList<PlayerInfo>();

	}

	/*
	 * waiting for connections and setting up threads for each, connection
	 */
	public void start() throws IOException {
		ServerSocket serverSocket = new ServerSocket(port);
		BroadcastTimer timer = new BroadcastTimer(16);
		timer.start();
		while (keepGoing) {
			System.out.println("Waiting for connection on " + port + ".");
			Socket socket = serverSocket.accept();

			if (!keepGoing)
				break;

			ClientThread t = new ClientThread(socket, uniqueID);
			al.add(t);
			t.start();
			uniqueID++;
		}

		// Closes all after waitnig for connections.
		try {
			serverSocket.close();
			for (int i = 0; i < al.size(); i++) {
				ClientThread ct = al.get(i);
				try {
					ct.input.close();
					ct.output.close();
					ct.socket.close();
				} catch (IOException e) {
					System.out.println(e);
				}
			}
		} catch (IOException e) {
			System.out.println("Exception in " + e);
			e.printStackTrace();
		}

	}

	/*
	 * when a admin wants to start a game this method runs
	 * it starts the game whit the game stats.
	 */
	public void startGame(){
		
		for(int i = 0; i < allPlayerInfo.size(); i++){
			allPlayerInfo.get(i).dead = false;
		}
		
		if(gs.team){
			for (int i = 0; i < al.size(); i++) {
				al.get(i).pInfo.setTeam((int)Math.round(Math.random()+1));
				if(al.get(i).pInfo.getTeam() == 1){
					al.get(i).pInfo.setX((int) Math.round(Math.random() * 780 + 10));
					al.get(i).pInfo.setY((int) Math.round(Math.random() * 100 + 490));
					al.get(i).pInfo.setColor(1f, 0f, 0f);
					al.get(i).pInfo.setHealth(gs.getHealth());
				}else{
					al.get(i).pInfo.setX((int) Math.round(Math.random() * 780 + 10));
					al.get(i).pInfo.setY((int) Math.round(Math.random() * 100 + 10));
					al.get(i).pInfo.setColor(0f, 0f, 1f);
					al.get(i).pInfo.setHealth(gs.getHealth());
				}
				System.out.println(al.get(i).pInfo.getTeam());
			}
		}else{
			if(gs.newColors){
				for (int i = 0; i < al.size(); i++) {
					al.get(i).pInfo.setX((int) Math.round(Math.random() * 780 + 10));
					al.get(i).pInfo.setY((int) Math.round(Math.random() * 580 + 10));
					al.get(i).pInfo.setColor((float)Math.random(), (float)Math.random(), (float)Math.random());
					al.get(i).pInfo.setHealth(gs.getHealth());
					gs.newColors = false;
				}
			}else{
				for (int i = 0; i < al.size(); i++) {
					al.get(i).pInfo.setX((int) Math.round(Math.random() * 780 + 10));
					al.get(i).pInfo.setY((int) Math.round(Math.random() * 580 + 10));
					al.get(i).pInfo.setHealth(gs.getHealth());
				}
			}
		}
		
		gameStarted = true;
	}
	
	synchronized void remove(int id) {
		
		al.remove(id);
			

	}

	/*
	 * Sets up a arraylist of all players information Sends all information to
	 * each player
	 */
	public synchronized void broadcastToAll() {

		allPlayerInfo.clear();

		for (int i = 0; i < al.size(); i++) {
			ClientThread ct = al.get(i);
			allPlayerInfo.add(ct.pInfo);
			// ct.pInfo.setX(k++);
			// System.out.println(ct.pInfo.getX());
			// ct.broadcast(ct.pInfo);
		}
		for (int i = 0; i < al.size(); i++) {
			ClientThread ct = al.get(i);

			if (!ct.broadcast(allPlayerInfo)) {

			}

		}
		for (int i = 0; i < al.size(); i++) {
			ClientThread ct = al.get(i);

			ct.pInfo.setCmd("");

		}

	}

	
	/*
	 * Inner class for all the Clients threads Sets up connection for each
	 */
	class ClientThread extends Thread {
		Socket socket;
		ObjectInputStream input;
		ObjectOutputStream output;
		public PlayerInfo pInfo;
		private boolean sent = false;
		int fireTimer;

		/*
		 * Setting up sockets for in and out stream.
		 */
		ClientThread(Socket socket, int id) {
			pInfo = new PlayerInfo(id);
			this.socket = socket;

			try {
				output = new ObjectOutputStream(socket.getOutputStream());
				output.flush();
				input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				System.out.println("Faild in/output stream");
			}

		}
		
		

		/*
		 * Overrides Threads run method First sending a start posision vi
		 * startBrod then waiting for in coming streams of comands
		 * 
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Thread#run()
		 */
		public void run() {
			boolean keepGoing = true;

			startBrod();

			/*
			 * Gets the command and performs the command
			 */
			while (keepGoing) {
				Commands command;
				try {
					command = (Commands) input.readObject();
					// System.out.println(command);
					// System.out.println(pInfo.getX());
				} catch (ClassNotFoundException e) {
					
					System.out.println("sŒ fel");
					e.printStackTrace();
					break;
				} catch (IOException e) {
					
					System.out.println("Player: " + pInfo.getID() + " "
							+ pInfo.getUserName()
							+ " coudent connect or disconected.");
					// e.printStackTrace();
					keepGoing = false;
					break;
				}
				/*
				 * Movement
				 */
				if (command.getDirection() == command.DOWN) {
					pInfo.setDirection(command.DOWN);
					if (colision(pInfo.getX(), pInfo.getY() + 1)) {
						pInfo.setY(pInfo.getY() + 1);
					}
				} else if (command.getDirection() == command.UP) {
					pInfo.setDirection(command.UP);
					if (colision(pInfo.getX(), pInfo.getY() - 1)) {
						pInfo.setY(pInfo.getY() - 1);
					}
				} else if (command.getDirection() == command.RIGHT) {
					pInfo.setDirection(command.RIGHT);
					if (colision(pInfo.getX() + 1, pInfo.getY())) {
						pInfo.setX(pInfo.getX() + 1);
					}
				} else if (command.getDirection() == command.LEFT) {
					pInfo.setDirection(command.LEFT);
					if (colision(pInfo.getX() - 1, pInfo.getY())) {
						pInfo.setX(pInfo.getX() - 1);
					}
					
				}
				/* Fire, and endFire
				 * for the fire and if fire is ended
				 */
				if (!pInfo.fire){
					if (command.fire){
						
						pInfo.fire(true);
						if(gameStarted)
							fireCollision(pInfo.getX(),pInfo.getY());
					}
				}
				if (command.endFire){
					pInfo.fire(false);
				}
				
				/* All the console commands
				 * for all gamestat, start game, adminpass ect.
				 */
				if (!command.getCmdString().equals("")){

					if(command.getCmdString().equals("-START")){
						if(pInfo.getAdminPass().equals(serverPass)){
							startGame();
						}else{
							pInfo.setCmd("wrong password");
						}
					}else if(command.getCmdString().equals("-TEAM 1")){
						if(pInfo.getAdminPass().equals(serverPass)){
							gs.team = true;
							gs.newColors = true;
						}else{
							pInfo.setCmd("wrong password");
						}
					}else if(command.getCmdString().equals("-TEAM 0") ){
						if(pInfo.getAdminPass().equals(serverPass)){
							gs.team = false;
						}else{
							pInfo.setCmd("wrong password");
						}
					}else if(command.getCmdString().startsWith("-HEALTH")){
						if(pInfo.getAdminPass().equals(serverPass)){
							String h = command.getCmdString().replaceFirst("-HEALTH", "");
							int health = 10;
							h = h.trim();
							try{
								health = Integer.parseInt(h);
							}catch(Exception e){
								System.out.println("end health whit just numbers"+ e);
							}
							gs.setHealth(health);
						}else{
							pInfo.setCmd("wrong password");
						}
					}else if(command.getCmdString().startsWith("-NICK")){
						String n = command.getCmdString().replaceFirst("-NICK", "");
						n = n.trim();
						pInfo.setUserName(n);
					}else if(command.getCmdString().startsWith("-CHANGE TEAM")){
						pInfo.changeTeam();
					}else if(command.getCmdString().startsWith("-ADMINPASS")){
						String a = command.getCmdString().replaceFirst("-ADMINPASS", "");
						a = a.trim();
						pInfo.setAdminPass(a);
					}
					else{
						pInfo.setCmd(pInfo.getUserName() + ": " + command.getCmdString());
					}

				}
			}
			close();
			System.out.println("ending Thread.");
			remove(pInfo.getID());
		}

		/* Checks if player is hit
		 * and performs the action
		 */
		public void fireCollision(int x, int y){
			
			switch(pInfo.getDirection()){
			case 1:
				y = y-13;
				break;
			case 2:
				y = y+13;
				break;
			case 3:
				x = x-13;
				break;
			case 4:
				x = x+13;
				break;
			}
			
			for(int i = 0; i < allPlayerInfo.size(); i++){
				if(pInfo.getID() != allPlayerInfo.get(i).getID()){
					int xBody = -5;
					while(xBody <= 5){
						if(allPlayerInfo.get(i).getX() + xBody == x){
							int yBody = -5;
							while(yBody <= 5){
								if(allPlayerInfo.get(i).getY() + yBody == y){
									allPlayerInfo.get(i).changeHealth(-1);
									switch(pInfo.getDirection()){
									case 1:
										if(allPlayerInfo.get(i).getY() > 25)
											allPlayerInfo.get(i).setY(allPlayerInfo.get(i).getY()-20);
										else 
											allPlayerInfo.get(i).setY(5);
										break;
									case 2:
										if(allPlayerInfo.get(i).getY() < 575)
											allPlayerInfo.get(i).setY(allPlayerInfo.get(i).getY()+20);
										else
											allPlayerInfo.get(i).setY(595);
										break;
									case 3:
										if(allPlayerInfo.get(i).getX() > 25)
											allPlayerInfo.get(i).setX(allPlayerInfo.get(i).getX()-20);
										else
											allPlayerInfo.get(i).setX(5);
										
										break;
									case 4:
										if(allPlayerInfo.get(i).getX() < 775)
											allPlayerInfo.get(i).setX(allPlayerInfo.get(i).getX()+20);
										else
											allPlayerInfo.get(i).setX(795);
										break;
									}
									if(allPlayerInfo.get(i).getHealt() <= 0){
										allPlayerInfo.get(i).dead = true;
									}
									break;
								}
								yBody++;
							}
						}
						xBody++;
					}
				}
			}
		}
		
		/*
		 * Checks for collision whit walls
		 * Only works whit 800*600
		 */
		public boolean colision(int x, int y) {
			if (x > 800 - 5 || x < 0 + 5 || y > 600 - 5 || y < 0 + 5) {
				return false;
			}
			return true;
		}

		/*
		 * Bordcasts the first information to the client. including generated
		 * posision
		 */
		public void startBrod() {
			try {
				output.writeObject(pInfo);
				output.flush();
			} catch (IOException e) {
				System.out.println("HŠr gick det fel 1" + e);

			}
			sent = true;
		}

		/*
		 * closes connection
		 */
		private void close() {

			sent = false;

			try {
				if (output != null)
					output.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			try {
				if (input != null)
					input.close();
			} catch (Exception e) {
				System.out.println(e);
			}
			try {
				if (socket != null)
					socket.close();
			} catch (Exception e) {
				System.out.println(e);
			}

		}

		/*
		 * bordcast the outGoing Arraylist, includes all the players
		 * information.
		 */
		public boolean broadcast(ArrayList outGoing) {

			if (sent) {
				if (!socket.isConnected()) {
					return false;
				}
				try {
					output.writeObject(outGoing);
					output.reset();
					output.flush();
				} catch (IOException e) {
					System.out.println("HŠr gick det fel 1" + e);
				}
			}
			return true;
		}
	}

	/*
	 * broadcast the information to the player and sleeps after.
	 */
	class BroadcastTimer extends Thread {
		int time;

		BroadcastTimer(int time) {
			this.time = time;
		}

		public void run() {
			while (true) {
				broadcastToAll();
				try {
					Thread.sleep(time);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}

	}

}
