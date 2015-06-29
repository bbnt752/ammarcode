package uk.gov.birmingham.eRecords.ops;

import uk.gov.birmingham.utils.DFCUtils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.IDfList;

public class UserRename {

	private String repository = null;
	private String userName = null;
	private String password = null;
	private final String METHOD_NAME = "dm_UserRename";

	public static void main(String args[]) {
		UserRename dfcTest = new UserRename();

		if (args.length == 3) { // Three argument has been passed which is what
								// needed
			dfcTest.setRepository(args[0]);
			dfcTest.setUserName(args[1]);
			dfcTest.setPassword(args[2]);
		} else {
			// DfLogger.error(reqEScript.getClass(),
			// "Insufficient number of arguments passed to the program", null,
			// null);
			System.out
					.println("Insufficient number of arguments passed to the program");
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


			// Call the method.
			// *********************************************************************************
			IDfList argList = new DfList();
			IDfList typeList = new DfList();
			IDfList valueList = new DfList();

			argList.appendString("METHOD");
			typeList.appendString("S");
			valueList.appendString("dm_UserRename");

			argList.appendString("ARGUMENTS");
			typeList.appendString("S");
			valueList.appendString("-docbase_name eimabupp -user_name dmadmin -job_id 080004bc80000339 -method_trace_level 10");

			argList.appendString("RUN_AS_SERVER");
			typeList.appendString("B");
			valueList.appendString("T");

			argList.appendString("SAVE_RESULTS");
			typeList.appendString("B");
			valueList.appendString("T");

			IDfCollection results = session.apply(null, "DO_METHOD", argList, typeList, valueList);
			System.out.println ("user rename succesfully completed");
			results.next();
			int returnValue = results.getInt("method_return_val");
			String strResultsID = results.getString("results_doc_id");
			System.out.println("returnValue: " + returnValue);
			System.out.println("strResultsID: " + strResultsID);

		}
		// Now we want to display the ACL applied to the folder at the given
		// path
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
}
