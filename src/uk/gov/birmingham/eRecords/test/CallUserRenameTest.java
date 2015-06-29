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

public class CallUserRenameTest {
	
	private String userName;
	private String password;
	private String repository;
	private IDfSessionManager sMgr = null;
	private IDfSession session = null;


	@Before
	public void setUp() throws Exception {
		
		//Adults QA
		/*
		 userName = "dmadmin"; 
		 repository = "dmadlpjq"; 
		 password = "dmadm1n";
		 */
		 //Adults PP
		 /*
		 userName = "dmadmin";
		 repository = "eimabupp";
		 password = "ppadm1n";
		 */
		 //CYPF PP
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
	
	public void userRenameNormal() {
		assert (session != null);
		boolean userRenamed = false;		
		//String oldName = "adults_hiv_usr3";
		//String newName = "adults_hiv_usr30";
		String oldName = "Cypf Support Staff Usr60";
		String newName = "Cypf Support Staff Usr6";
		
		//String arguments = "-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000339 -method_trace_level 10";
		//String arguments = null;
		try {
			IDfUser oldUser = session.getUser(oldName);
			assertTrue(oldUser != null);
			
			DFCUtils.userRenameSynchronous(oldName, newName, session, repository.startsWith("cypf"));
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
	
	//Scenarios to test
	//TODO: The old user name doesn't exist in the repository
	@Rule
	public ExpectedException exception = ExpectedException.none();
	
	@Test
	@Ignore
	public void userRenameUserDoesntExist() throws DfException  {
		exception.expect(DfException.class);
		
		assert (session != null);
		boolean userRenamed = false;
		String oldName = "Ammaewrer"; //A non-existant name in the repository
		String newName = "Ammar Khalids";
		
		
			DFCUtils.userRenameSynchronous(oldName, newName, session, repository.startsWith("cypf"));			
			//The exception should have been thrown, otherwise fail the test
			fail("The DfException was expected");		
		
	}
}
