package dev.roanh.convexmerger.ui;

public enum MessageDialog{
	ALREADY_OWNED("Already Claimed", "This object was already claimed by another player."),
	MERGE_INTERSECTS("Invalid Merge", "Your merge intersects other objects on its boundary."),
	NO_TURN("Not Your Turn", "Please wait for the other player(s) to finish their turn.");
	
	private final String title;
	private final String subtitle;
	
	private MessageDialog(String title, String subtitle){
		this.title = title;
		this.subtitle = subtitle;
	}
	
	public String getTitle(){
		return title;
	}
	
	public String getSubtitle(){
		return subtitle;
	}
}
