package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dev.roanh.convexmerger.net.packet.Packet;

/**
 * Base connection class for a server client connection.
 * @author Roan
 */
public class Connection{
	/**
	 * The input stream for this connection.
	 */
	private ObjectInputStream in;
	/**
	 * The output stream for this connection.
	 */
	private ObjectOutputStream out;
	/**
	 * The socket for this connection.
	 */
	private Socket socket;
	
	/**
	 * Constructs a new connection from the given socket.
	 * @param socket The socket for the connection.
	 * @throws IOException When some IOException occurs.
	 */
	protected Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	/**
	 * Reads a new packet from the connection. 
	 * @return The read packet or <code>null</code>
	 *         when something unexpected happened or
	 *         the connection is closed.
	 * @throws IOException When some IOException occurs.
	 */
	public Packet readPacket() throws IOException{
		try{
			if(!isClosed()){
				Object data = in.readObject();
				if(data instanceof Packet){
					return (Packet)data;
				}else{
					//bad client
					close();
					return null;
				}
			}else{
				return null;
			}
		}catch(ClassNotFoundException e){
			//all classes should be found otherwise the connection is bad
			close();
			return null;
		}
	}
	
	/**
	 * Sends a new packet over this connection.
	 * @param packet The packet to send.
	 * @throws IOException When some IOException occurs.
	 */
	public void sendPacket(Packet packet) throws IOException{
		out.writeObject(packet);
		out.flush();
	}
	
	/**
	 * Checks if this connection is closed.
	 * @return True if this connection is closed.
	 */
	public boolean isClosed(){
		return socket.isClosed();
	}
	
	/**
	 * Closes this connection.
	 */
	public void close(){
		try{
			in.close();
			out.close();
			socket.close();
		}catch(IOException e){
			//not very relevant, we were disconnecting anyway
		}
	}
}
