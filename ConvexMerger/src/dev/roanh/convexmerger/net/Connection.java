package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dev.roanh.convexmerger.net.packet.Packet;

public class Connection{
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	
	protected Connection(Socket socket) throws IOException{
		this.socket = socket;
		out = new ObjectOutputStream(socket.getOutputStream());
		in = new ObjectInputStream(socket.getInputStream());
	}
	
	public Packet readPacket() throws IOException{
		try{
			if(!isClosed()){
				Object data = in.readObject();
				if(data instanceof Packet){
					return (Packet)data;
				}else{
					//bad client
					System.err.println("Aborting bad connection");
					close();
					return null;
				}
			}else{
				return null;
			}
		}catch(ClassNotFoundException e){
			//all classes should be found otherwise the connection is bad
			System.err.println("Aborting bad connection: " + e.getMessage());
			close();
			return null;
		}
	}
	
	public void sendPacket(Packet packet) throws IOException{
		out.writeObject(packet);
		out.flush();
	}
	
	public boolean isClosed(){
		return socket.isClosed();
	}
	
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
