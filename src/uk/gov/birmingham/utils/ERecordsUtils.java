package uk.gov.birmingham.utils;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfLoginInfo;

public class ERecordsUtils implements IERecordsUtils {
	
	IDFCUtils dfcUtils;
	
	ERecordsUtils() {
		dfcUtils = new DFCUtils();
	}

	/**
	 * This utility method executes two queries needed after name change in CYPF
	 * 
	 * @param oldUserName	The old name of the user under question
	 * @param newUserName	The new name of the user
	 * @param session		The DFC session object
	 * 
	 */
	public void executeCYPFUserRenameQueries (String oldUserName, String newUserName, IDfSession session) throws DfException {
		
		String escapedOldUserName = oldUserName.replace("'", "''");
		String escapedNewUserName = newUserName.replace("'", "''");
		
		String dql1 = "execute EXEC_SQL with query ='update dm_sysobject_s set r_creator_name = ''" + escapedNewUserName + "'' where r_creator_name = ''" + escapedOldUserName + "'' '";
		String dql2 = "execute EXEC_SQL with query ='update dm_sysobject_s set r_modifier = ''" + escapedNewUserName + "'' where r_modifier = ''" + escapedOldUserName + "'' '";
		//Now execute the queries
		dfcUtils.executeDQL(dql1, session);
		dfcUtils.executeDQL(dql2, session);
	}

	/**
	 * This utility method resets a dm_user as a unix user (that can do fresh sync with AD)
	 * 
	 * @param userID		The user id to be initialized to this value
	 * @param userName		The userName 
	 */
	public void initializeAsUnixUser(String userID, String userName, IDfSession session) throws DfException {
		//We have to run four dql queries in this case as follows:
		//TODO: Test this method with the user name having apostrophe in it.
		//check if userName contains a quote character, it will need to be escaped.
		String escapedUserName = userName.replace("'", "''");
		boolean txStartedHere = false;
		if (!session.isTransactionActive()) {
			session.beginTrans();
			txStartedHere = true;
		}
		String dql1 = "update dm_user objects set user_global_unique_id = ':" + userID + "' where user_name = '" + escapedUserName + "'";
		String dql2 = "update dm_user objects set user_ldap_dn = '' where user_name = '" + escapedUserName + "'";
		String dql3 = "update dm_user objects set user_source = 'unix only'  where user_name = '" + escapedUserName + "'";		
		String dql4 = "update dm_user objects set user_login_name = '" + userID + "'  where user_name = '" + escapedUserName + "'";
		
		try {
			dfcUtils.executeDQL(dql1, session);
			dfcUtils.executeDQL(dql2, session);
			dfcUtils.executeDQL(dql3, session);
			dfcUtils.executeDQL(dql4, session);
			if (txStartedHere) {
				session.commitTrans();
			}
		}
			finally {
				if ( txStartedHere && session.isTransactionActive())
					session.abortTrans();
			}
				
		}

		
	/**
	 * This utility method is used to rename a user
	 * @param oldUserName	The old user name
	 * @param newUserName	The new name of the user
	 * @param session		The DFC session
	 * @param cypf
	 * 
	 * @return 
	 * @throws DfException 
	 * 
	 */
	public void userRenameSynchronous (String oldUserName, String newUserName, IDfSession session, boolean cypfQueries ) throws DfException {
		//validate the oldUserName object exists in the repository
		
			IDfCollection results = dfcUtils.userRenameSynchronous(oldUserName, newUserName, session);
			//System.out.println("Running the method: dm_UserRename. It may take couple minutes....");
			if (results != null && cypfQueries == true) {
				//This means we have to execute the queries for cypf
				executeCYPFUserRenameQueries(oldUserName, newUserName, session);	
					
			} 
		


		//return results;
	}

}