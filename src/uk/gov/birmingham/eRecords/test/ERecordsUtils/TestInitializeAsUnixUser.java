package uk.gov.birmingham.eRecords.test.ERecordsUtils;

import static org.junit.Assert.*;

import java.io.*;

import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;

import uk.gov.birmingham.eRecords.utils.*;

public class TestInitializeAsUnixUser {

	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;
	private IDFCUtils dfcUtils = null;
	private IERecordsUtils eRecordsUtils = null;

	public TestInitializeAsUnixUser() {
		dfcUtils = new DFCUtils();
		eRecordsUtils = new ERecordsUtils();
	}
	
	@Before
	public void setUp() throws Exception {
		Properties prop = new Properties();
		InputStream input = null;
		FileInputStream fileInput = null;
		
		try {
			
			URL location = TestInitializeAsUnixUser.class.getProtectionDomain().getCodeSource().getLocation();
	        System.out.println(location.getFile());
	    
	        File file = new File("config\\credentials.properties");
			fileInput = new FileInputStream(file);
			
			//input = getClass().getResourceAsStream(filename);
			if (fileInput == null) {
				fail("could load configuration file");
				return;
			}
			prop.load(fileInput);
			
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
		 session = dfcUtils.getDfcSession(sMgr, repository); 
		 if (session == null) {
			 fail("Could not get a dfc session");
		 }
		 
	}

	@After
	public void tearDown() throws Exception {
		if (session != null)
			sMgr.release(session);
	}


	
	@Test
	
	public void testValidUserDetails_shouldInitializeSuccessfully() {
		
		
		String userName = "Ammar Khalid";
		String userID = "extaarkd";
		try {
			IDfUser user = session.getUser(userName);
			if (user == null) {
				fail("The user doesn't exist in the repository");
			}
			eRecordsUtils.initializeAsUnixUser(userID, userName, session);			
			user = session.getUser(userName);
			assertEquals("unix only", user.getString("user_source"));
			assertEquals(userID, user.getUserLoginName());
			assertEquals("", user.getString("user_ldap_dn"));
			assertEquals(":" + userID, user.getString("user_global_unique_id"));
			
		} catch (DfException df) {
			fail("There was an Exception thrown and it was not expected");
		}
		
	}
	
	@Test
	
	public void testValidUserDetailsWithQuoteInName_shouldInitializeSuccessfully() {
		String userName = "Yasmin O'donnell";
		String userID = "BCCAYNOL";

		try {
			IDfUser user = session.getUser(userName);
			if (user == null) {
				fail("The user doesn't exist in the repository");
			}
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
	
	public void testNonValidUserDetails_shouldThrowDFEx() {
		
	}
	


}
