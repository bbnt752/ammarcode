package uk.gov.birmingham.eRecords.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import uk.gov.birmingham.utils.DFCUtils;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;

import uk.gov.birmingham.eRecords.ops.ERecordsOperations;
import uk.gov.birmingham.utils.DFCUtils;


public class ExecuteNonSelectQueryTest {

	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;

	
	@Before
	public void setUp() throws Exception {
		
		 userName = "dmadmin"; 
		 repository = "dmadlpjq"; 
		 password = "dmadm1n";
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
	public void test() {
		fail("there is nothing to test right now");
		
	}

}
