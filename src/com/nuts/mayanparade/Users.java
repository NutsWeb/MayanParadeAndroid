package com.nuts.mayanparade;

import java.util.List;

import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

@Root
public class Users
{
	@ElementList
	private List<User> ArrayOfUser;
	
	public List<User> getUsers()
	{
		return ArrayOfUser;
	}
}
