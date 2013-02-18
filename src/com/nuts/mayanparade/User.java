package com.nuts.mayanparade;

import org.simpleframework.xml.Element;

public class User
{
	@Element
	private String Name;
	
	@Element (required=false)
	private String LastName;
	
	@Element
	private String Email;
	
	@Element (required=false)
	private String Facebookid;
	
	public String getName() {return Name;}
	
	public String getLastName() {return LastName;}
	
	public String getEmail() {return Email;}
	
	public String getFacebookid() {return Facebookid;}
	
	public void setName(String name) {Name = name;}
	
	public void setEMail(String email) {Email = email;}
}
