package uk.gov.birmingham.eRecords.test.DFCUtils;

import static org.junit.Assert.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;

import uk.gov.birmingham.eRecords.test.ERecordsUtils.TestInitializeAsUnixUser;
import uk.gov.birmingham.eRecords.utils.*;


public class TestCallMethodSynchronously {

	private String userName;
	private String password;
	private String repository;
	private IDFCUtils idfcutils;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;
	private IDFCUtils dfcUtils = null;

	public TestCallMethodSynchronously() {
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
	public void testValidArgsInput_shouldCallMethodAndWaitForResults() {

		try {

			assert (session != null);
			String arguments = "-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000395 -method_trace_level 10";
			String methodName = "dm_LDAPSynchronization";

			IDfCollection results = idfcutils.callMethodSynchronously(methodName, arguments, session);
			assertNotEquals(null, results);
		} catch (DfException df) {
			fail ("DfException thrown when not expectd");
		}
	}
	
	@Test
	public void testWithoutArgsInput_shouldThrowInvalidArgEx() {
		
	}
	
	@Test
	public void testWithoutMethodNameArgsInput_shouldThrowInvalidArgEx() {
		
	}

	
	@Test
	public void testWithInvalidArgsInput_shouldThrowInvalidArgEx() {
		
	}
	
	
	
			
	}
		
	/*
	
	@Test (expected=DfException.class)
	public void callSyncMethodWithWrongMethodName() {
		
	}
	
	@Test (expected=DfException.class)
	public void callSyncMethodWithIllegalArguments() {
		
	}

	/*
	 * @Test public void test() { fail("Not yet implemented"); }
	 */

	/*
	 * @Test
	 * 
	 * public void testSynchronousMethodExecuteIllegalArguments() {
	 * ERecordsOperations erOperations = new ERecordsOperations(); String msg =
	 * erOperations.executeSynchronousMethod("methodA");
	 * 
	 * 
	 * }
	 */


