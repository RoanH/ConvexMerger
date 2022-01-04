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
			return (Packet)in.readObject();
		}catch(ClassNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
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
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
