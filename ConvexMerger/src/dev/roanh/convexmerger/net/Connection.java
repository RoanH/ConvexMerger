package dev.roanh.convexmerger.net;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import dev.roanh.convexmerger.net.packet.Packet;

public class Connection{
	protected ObjectInputStream in;
	protected ObjectOutputStream out;
	
	protected Connection(Socket socket) throws IOException{
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
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
}
