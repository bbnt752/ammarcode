package uk.gov.birmingham.eRecords.test;


import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import uk.gov.birmingham.utils.DFCUtils;

import org.junit.After;
import org.junit.Before;
import org.junit.rules.ExpectedException;

import uk.gov.birmingham.utils.DFCUtils;

public class InitializeAsUnixUserTest {

	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;

	@Before
	public void setUp() throws Exception {
		
		 userName = "cypadmin";
		 repository = "cypf_pp";
		 password = "cypadm1n";
		 sMgr = DFCUtils.getSessionManager(userName, password, repository);
		 assertNotNull(sMgr); 
		 System.out.println(sMgr.getLocale());
		 System.out.println(sMgr.getPrincipalName()); 
		 session = sMgr.newSession(repository); 
		 assertNotNull(session);
		 
	}

	@After
	public void tearDown() throws Exception {
		if (session != null)
			sMgr.release(session);
	}

	@Test
	
	public void testUnixSuccess() {
		String userName = "Ammar Khalid";
		String userID = "extaarkd";
		try {
			IDfUser user = session.getUser(userName);
			assertNotNull(user);
			DFCUtils.initializeAsUnixUser(userID, userName, session);			
			user = session.getUser(userName);
			assertEquals("unix only", user.getString("user_source"));
			assertEquals(userID, user.getUserLoginName());
			assertEquals("", user.getString("user_ldap_dn"));
			assertEquals(":" + userID, user.getString("user_global_unique_id"));
			
		} catch (DfException df) {
			fail("There was a DfExceptionwhen it was not expected");
		}
		
	}
	
	@Test
	public void testUserWithQuoteInName() {
		String userName = "Yasmin O'donnell";
		String userID = "BCCAYNOL";

		try {
			IDfUser user = session.getUser(userName);
			assertNotNull(user);
			DFCUtils.initializeAsUnixUser(userID, userName, session);			
			user = session.getUser(userName);
			assertEquals("unix only", user.getString("user_source"));
			assertEquals(userID, user.getUserLoginName());
			assertEquals("", user.getString("user_ldap_dn"));
			assertEquals(":" + userID, user.getString("user_global_unique_id"));
			
		} catch (DfException df) {
			fail("There was a DfExceptionwhen it was not expected");
		}

	}
	


}
