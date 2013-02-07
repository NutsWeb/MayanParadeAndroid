package com.nuts.mayanparade;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

@Root
public class SubRegion
{
	@Element
	private int Id;
	
	@Element
	private String Name;
	
	@Element
	private float Longitude;
	
	@Element
	private float Latitude;
	
	public int getId(){return Id;}
	
	public String getName(){return Name;}
	
	public float getLongitude(){return Longitude;}
	
	public float getLatitude(){return Latitude;}
}