package uk.gov.birmingham.eRecords.test.ERecordsUtils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

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

import uk.gov.birmingham.eRecords.utils.*;

import org.junit.rules.ExpectedException;



public class TestERecordsUserRenameSynchronous {
	
	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;
	private IDFCUtils dfcUtils = null;
	private IERecordsUtils eRecordsUtils = null;

	public TestERecordsUserRenameSynchronous() {
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
				fail("could not load configuration file");
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
	
	public void testValidCYPFUserDetails_shouldRenameAndUpdateObjsOk() {
		assert (session != null);
		boolean userRenamed = false;		
		String oldName = "Cypf Support Staff Usr60";
		String newName = "Cypf Support Staff Usr6";
		
		//String arguments = "-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000339 -method_trace_level 10";
		//String arguments = null;
		try {
			IDfUser oldUser = session.getUser(oldName);
			assertTrue(oldUser != null);
			
			eRecordsUtils.eRecordsUserRenameSynchronous(oldName, newName, session, repository.startsWith("cypf"));
			IDfUser newUser = session.getUser(newName);
			assertNotNull(newUser);
			assertEquals(newUser.getUserName(), newName);
			//TODO: no objects have the old user as r_creator_name or r_modifier
			
			IDfQuery query = new DfQuery();
			query.setDQL("select * from dm_sysobject where r_creator_name = '" + oldUser + "' or r_modifier = '" + oldUser + "'");
			IDfCollection col = query.execute(session, DfQuery.DF_READ_QUERY);
			//The following assertion means there are no results in the collection
			assertTrue(col.next() == false);
			
			
		} catch (DfException df) {
			assertEquals(null, df);
		}
		
	}
	
	@Test
	
	public void testValidAdultsUserDetails_shouldRenameAndUpdateObjsOk() {
		
	}
	
	@Test
	
	public void testValidHousingUserDetails_shouldRenameAndUpdateObjsOk() {
		
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
	
	
	/*
	//Scenarios to test
	//TODO: The old user name doesn't exist in the repository
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	
	
	@Test
	@Ignore
	public void testInvalidUserDetails_shouldThrowDfEx() throws DfException  {
		exception.expect(DfException.class);
		
		assert (session != null);
		boolean userRenamed = false;
		String oldName = "Ammaewrer"; //A non-existant name in the repository
		String newName = "Ammar Khalids";
		
		
		eRecordsUtils.eRecordsUserRenameSynchronous(oldName, newName, session, repository.startsWith("cypf"));			
			//The exception should have been thrown, otherwise fail the test
			fail("The DfException was expected");		
		
	}
	*/
	

}
