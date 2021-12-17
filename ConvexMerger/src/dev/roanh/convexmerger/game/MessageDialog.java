package dev.roanh.convexmerger.game;

public enum MessageDialog{
	ALREADY_OWNED("Already Claimed", "This object was already claimed by another player."),
	MERGE_INTERSECTS("Invalid Merge", "Your merge intersects other objects on its boundary.");
	
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
