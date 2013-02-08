package com.nuts.mayanparade;

import org.simpleframework.xml.Element;

public class ArtistInfo
{
	@Element
	private String Name;
	
	@Element
	private String ImgUrl;
	
	@Element
	private String Bio;
	
	@Element
	private String Video;
	
	public String getName(){return Name;}
	
	public String getImage(){return ImgUrl;}
	
	public String getBiography(){return Bio;}
	
	public String getVidoe(){return Video;}
}
