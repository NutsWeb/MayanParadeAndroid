package com.nuts.mayanparade;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Region
{
	private int _listInd;
	public int getIndex() {return _listInd;}
	public void setIndex(int index){_listInd = index;}
	
	@Element
	private int Id;
	
	@Element
	private String Name;
	
	@Element
	private float Longitude;
	
	@Element
	private float Latitude;
	
	@ElementList
	private List<SubRegion> SubRegions;
	
	public int getId(){return Id;}
	
	public String getName(){return Name;}
	
	public float getLongitude(){return Longitude;}
	
	public float getLatitude(){return Latitude;}
	
	public List<SubRegion> getSubegions(){return SubRegions;}
}