package uk.gov.birmingham.eRecords.test.DFCUtils;

import static org.junit.Assert.fail;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;

import uk.gov.birmingham.eRecords.test.ERecordsUtils.TestInitializeAsUnixUser;
import uk.gov.birmingham.eRecords.utils.DFCUtils;
import uk.gov.birmingham.eRecords.utils.IDFCUtils;

public class TestUserRenameSynchronous {

	private String userName;
	private String password;
	private String repository;
	private IDFCUtils idfcutils;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;
	private IDFCUtils dfcUtils = null;

	public TestUserRenameSynchronous() {
		dfcUtils = new DFCUtils();
		
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
		 if (sMgr == null) 
		 {
			 fail("could not retrieve sessionManager");
			 return;
		 }
		 session = sMgr.newSession(repository);
		 if (session == null)
		 {
			 fail("could not retrieve session");
			 return;
		 }

		 
	}

	@After
	public void tearDown() throws Exception {
		if (session != null)
			sMgr.release(session);
	}
	
	@Test
	public void testValidUserDetailsInput_shouldRenameOk() {
		
	}
	
	@Test
	public void testWithoutArgsInput_shouldThrowInvalidArgEx() {
		
	}
	
	@Test
	public void testWithoutOldNameArgsInput_shouldThrowInvalidArgEx() {
		
	}
	
	@Test
	public void testNotValidUser_shouldThrowDfEx() {
		
	}
	
	
	
	
	@Test
	public void testInvalidNewNameInput_shouldThrowDfEx() {
		
	}
	
}
