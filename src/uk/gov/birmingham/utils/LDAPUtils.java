package uk.gov.birmingham.utils;

import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.ldap.LdapContext;

public class LDAPUtils {

	
	/**
	  * Tests if an Active Directory user exists in an Active Directory group. 
	  * @param ctx LDAP Context.
	  * @param dnADGroup distinguishedName of group.
	  * @param dnADUser distinguishedName of user.
	  * @return True if user is member of group.
	  */


	public static boolean isMemberOfADGroup(LdapContext ctx, String dnADGroup, String dnADUser) {
	  try {
	   DirContext lookedContext = (DirContext) (ctx.lookup(dnADGroup));
	   Attribute attrs = lookedContext.getAttributes("").get("member");
	   for (int i = 0; i < attrs.size(); i++) {
	    String foundMember = (String) attrs.get(i);
	    if(foundMember.equals(dnADUser)) {
	     return true;
	    }
	   }
	  } catch (NamingException ex) {
	   String msg = "There has been an error trying to determin a group membership for AD user with distinguishedName: "+dnADUser;
	   System.out.println(msg);
	   ex.printStackTrace();
	  }
	  return false;
	 }
	




	
}