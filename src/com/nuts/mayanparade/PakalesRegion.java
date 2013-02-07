package com.nuts.mayanparade;

import java.util.List;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class PakalesRegion
{
	@ElementList
	private List<Region> ArrayOfRegion;
	
	public List getRegions()
	{
		return ArrayOfRegion;
	}
}