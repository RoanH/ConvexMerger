package dev.roanh.convexmerger.game;

import dev.roanh.convexmerger.ui.MessageDialog;

public class ClaimResult{
	public static final ClaimResult EMPTY = new ClaimResult();
	private MessageDialog message = null;
	private ConvexObject result = null;
	
	private ClaimResult(){
	}
	
	protected ClaimResult(MessageDialog msg){
		message = msg;
	}
	
	protected ClaimResult(ConvexObject obj){
		result = obj;
	}
	
	public boolean hasMessage(){
		return message != null;
	}
	
	public MessageDialog getMessage(){
		return message;
	}
	
	public boolean hasResult(){
		return result != null;
	}
	
	public ConvexObject getResult(){
		return result;
	}
	
	public static final ClaimResult of(MessageDialog msg){
		return new ClaimResult(msg);
	}
	
	public static final ClaimResult of(ConvexObject obj){
		return new ClaimResult(obj);
	}
}
