package dev.roanh.convexmerger.player;

public class HumanPlayer extends Player{
	private static int ID = 1;

	public HumanPlayer(){
		super(true, "Player " + (ID++));
	}

	@Override
	public boolean executeMove(){
		// TODO Auto-generated method stub
		return true;
	}
}
