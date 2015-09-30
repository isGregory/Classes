// Test Server

import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.PrintStream;
import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;


public class tServ {
public static void main (String[] args) throws Exception {
    if (args.length != 2) {
		System.err.println("tServ <host> <port>");
		System.exit(1);
	}
    String host = args[0];
    int port = Integer.parseInt (args[1]);

    ServerSocket serversocket = new ServerSocket();
    serversocket.bind (new InetSocketAddress (host, port));

    Socket socket = serversocket.accept();
	PrintStream out = new PrintStream (socket.getOutputStream(), true);
	Scanner in = new Scanner (socket.getInputStream());
    out.printf ("id 1%n");
    out.printf ("name 0 Burt%n");
    out.printf ("name 1 Duke%n");
    out.printf ("available 0 0 A %n");
    out.printf ("available 0 1 B %n");
    out.printf ("available 0 2 C %n");
    out.printf ("available 0 3 D %n");
    out.printf ("available 1 0 E %n");
    out.printf ("available 1 1 F %n");
    out.printf ("available 1 2 G %n");
    out.printf ("available 1 3 H %n");
    out.printf ("available 2 0 I %n");
    out.printf ("available 2 1 J %n");
    out.printf ("available 2 2 K %n");
    out.printf ("available 2 3 L %n");
    out.printf ("available 3 0 M %n");
    out.printf ("available 3 1 N %n");
    out.printf ("available 3 2 O %n");
    out.printf ("available 3 3 Q %n");

    out.printf ("chosen 2 2%n");
    out.printf ("score 0 84%n");
    out.printf ("score 1 234%n");
    out.printf ("turn 1%n");
	try {
    	while (in.hasNextLine()) {
    		String message = in.nextLine();
			System.out.println( "IN: " + message );
        	Scanner s = new Scanner (message);
    		String op = s.next();
            if ( op.equals( "letter" ) ){
                int row = s.nextInt();
                int col = s.nextInt();
            	out.printf( "chosen %d %d%n", row, col );
            }
		}
	//} catch (IOException exc) {
	} finally {
        try {
           socket.close();
		} catch (IOException exc) {}
    }
}
}
