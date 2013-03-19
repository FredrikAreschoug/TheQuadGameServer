package game;

import java.io.IOException;

/*
 * Main class
 */

public class StartClass {
	public static void main(String[] args) throws IOException {
		int port = 1501;
		ServerMain server = new ServerMain(port);
		server.start();
	}
}
