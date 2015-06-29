package uk.gov.birmingham.eRecords.ops;

import uk.gov.birmingham.utils.DFCUtils;

import com.documentum.fc.client.DfQuery;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;

public class DFCTest {
	
private String repository = null; 
private String userName = null;
private String password = null;
private final String METHOD_NAME = "dm_LDAPSynchronization";
private final String USER_NAME = "Shazia Begum";
private final String USER_LOGIN = "BCCHSABM";
	
	public static void main(String args[]) {
		DFCTest dfcTest = new DFCTest();
		
		if (args.length == 3) { //Three argument has been passed which is what needed
			dfcTest.setRepository(args[0]);
			dfcTest.setUserName(args[1]);
			dfcTest.setPassword(args[2]);
		} else
		{
			//DfLogger.error(reqEScript.getClass(), "Insufficient number of arguments passed to the program", null, null);
			System.out.println("Insufficient number of arguments passed to the program");
			return;
		}
		dfcTest.execute();
	}
	
	private void execute() {
		IDfSessionManager sMgr = null;
		IDfSession session = null;
		
		
		try {
			sMgr = DFCUtils.getSessionManager(userName, password, repository);
			session = sMgr.getSession(repository);
			
			//IDfSysObject oJob= (IDfSysObject)session.getObjectByQualification("dm_job where object_name = 'dm_ConsistencyChecker'");
			//oJob.setBoolean("run_now",true);
			//oJob.save();
			
			//Make Shazia Begum user the unix user
			this.changeUserToUnix(USER_LOGIN, USER_NAME, session);
			
			// Call the method.
			// *********************************************************************************
			IDfList argList = new DfList();
			IDfList typeList = new DfList();
			IDfList valueList = new DfList();

			argList.appendString("METHOD");
			typeList.appendString("S");
			valueList.appendString(METHOD_NAME);

			argList.appendString("ARGUMENTS");
			typeList.appendString("S");
			valueList.appendString("-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000395 -method_trace_level 10");

			argList.appendString("RUN_AS_SERVER");
			typeList.appendString("B");
			valueList.appendString("T");

			argList.appendString("SAVE_RESULTS");
			typeList.appendString("B");
			valueList.appendString("T");
			System.out.println("Running LDAP Synchronization. It may take couple minutes....");
			IDfCollection results = session.apply(null, "DO_METHOD", argList, typeList, valueList);
			System.out.println("Synchronization process concluded");

			results.next();
			IDfId objId = results.getId("result_doc_id");
			System.out.println(objId.getId());
		
		}
		//Now we want to display the ACL applied to the folder at the given path
		catch (DfException df) {
			System.out.println(df.getMessage());
			df.printStackTrace();
			
			
		} finally {
			if (session != null)
				sMgr.release(session);
		}
		
	}

		public String getRepository() {
			return repository;
		}

		public void setRepository(String repository) {
			this.repository = repository;
		}
		public String getPassword() {
			return password;
		}

		public void setPassword(String password) {
			this.password = password;
		}
		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		private void changeUserToUnix(String userId, String userName, IDfSession sess) throws DfException {
			String dql1 = "update dm_user objects set user_global_unique_id=':" + userId + "' where user_name = '" + userName + "'";
			String dql2 = "update dm_user objects set user_ldap_dn = '' where user_name = '" + userName + "'";
			String dql3 = "update dm_user objects set user_source = 'unix only' where user_name = '" + userName + "'";
			String dql4 = "update dm_user objects set user_login_name = '" + userId + "' where user_name = '" + userName + "'";
			IDfCollection col = null;
			IDfQuery query = null;
			query = new DfQuery();
			query.setDQL(dql1);
			col = query.execute(sess,DfQuery.EXEC_QUERY);
			query.setDQL(dql2);
			col = query.execute(sess,DfQuery.EXEC_QUERY);
			query.setDQL(dql3);
			col = query.execute(sess,DfQuery.EXEC_QUERY);
			query.setDQL(dql4);
			col = query.execute(sess,DfQuery.EXEC_QUERY);
			
		}
		
	}

 
	
