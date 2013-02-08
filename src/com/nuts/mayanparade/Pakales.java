package com.nuts.mayanparade;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Pakales
{
	@ElementList
	private List<Pakal> ArrayOfPakal;
	
	public List<Pakal> getPakales()
	{
		return ArrayOfPakal;
	}
}
