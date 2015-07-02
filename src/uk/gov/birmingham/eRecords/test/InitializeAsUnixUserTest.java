package uk.gov.birmingham.eRecords.test;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import uk.gov.birmingham.utils.*;

public class InitializeAsUnixUserTest {

	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;
	private IDFCUtils dfcUtils = null;
	private IERecordsUtils eRecordsUtils = null;

	public InitializeAsUnixUserTest() {
		eRecordsUtils = new ERecordsUtils();
	}
	
	@Before
	public void setUp() throws Exception {
		Properties prop = new Properties();
		InputStream input = null;
		
		try {
			String filename = "credentials.properties";
			input = getClass().getResourceAsStream(filename);
			if (input == null) {
				fail("Sorry unable to find " + filename);
				return;
			}
			prop.load(input);
			
			userName = prop.getProperty("username");
			repository = prop.getProperty("repository");
			password = prop.getProperty("password");
			
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		 sMgr = dfcUtils.getSessionManager(userName, password, repository);
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
			eRecordsUtils.initializeAsUnixUser(userID, userName, session);			
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
	@Ignore
	public void testUserWithQuoteInName() {
		String userName = "Yasmin O'donnell";
		String userID = "BCCAYNOL";

		try {
			IDfUser user = session.getUser(userName);
			assertNotNull(user);
			eRecordsUtils.initializeAsUnixUser(userID, userName, session);			
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
