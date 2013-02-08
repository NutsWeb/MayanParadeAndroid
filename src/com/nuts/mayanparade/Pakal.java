package com.nuts.mayanparade;

import org.simpleframework.xml.Element;

public class Pakal
{
	@Element
	private int Id;
	
	@Element
	private String Name;
	
	@Element
	private float Longitude;
	
	@Element
	private float Latitude;
	
	@Element
	private String Region;
	
	@Element
	private String SubRegion;
	
	@Element
	private ArtistInfo Artist;
	
	public int getId(){return Id;}
	
	public String getName(){return Name;}
	
	public float getLongitude(){return Longitude;}
	
	public float getLatitude(){return Latitude;}
	
	public String getRegion(){return Region;}
	
	public String getSubRegion(){return SubRegion;}
	
	public ArtistInfo getArtist(){return Artist;}
}
