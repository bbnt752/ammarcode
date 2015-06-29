package uk.gov.birmingham.eRecords.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;

import uk.gov.birmingham.eRecords.ops.ERecordsOperations;
import uk.gov.birmingham.utils.DFCUtils;

public class CallSyncMethodTest {

	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;

	@Before
	public void setUp() throws Exception {
		/*
		 userName = "dmadmin"; 
		 repository = "dmadlpjq"; 
		 password = "dmadm1n";
		 */
		 userName = "dmadmin";
		 repository = "eimabupp";
		 password = "ppadm1n";
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
	public void callSyncMethodForLDAPSync() {

		try {

			assert (session != null);
			String arguments = "-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000395 -method_trace_level 10";
			String methodName = "dm_LDAPSynchronization";

			IDfCollection results = DFCUtils.executeMethodSynchronous(methodName, arguments, session);
			assertNotEquals(null, results);
		} catch (DfException df) {
			assertEquals(null, df);
		}
	}
	
	@Test 
	public void callSyncMethodForUserRename() {
		
		try {
			assert (session != null);
			
			String arguments = "-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000339 -method_trace_level 10";
			String methodName = "dm_UserRename";
			
			IDfCollection results = DFCUtils.executeMethodSynchronous(methodName, arguments, session);
			assertNotEquals(null, results);
		} catch (DfException df) {
			assertEquals(null, df);
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

}
