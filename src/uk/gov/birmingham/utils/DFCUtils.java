package uk.gov.birmingham.utils;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.OutputStream;

import com.documentum.com.DfClientX;
import com.documentum.fc.client.DfObjectNotFoundException;
import com.documentum.fc.client.IDfClient;
import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfFolder;
import com.documentum.fc.client.IDfQuery;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.client.IDfSysObject;
import com.documentum.fc.client.IDfUser;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.DfList;
import com.documentum.fc.common.DfLogger;
import com.documentum.fc.common.IDfId;
import com.documentum.fc.common.IDfList;
import com.documentum.fc.common.IDfLoginInfo;

public class DFCUtils {
	

	/**
	 * This utility method returns IDfSessionManager that can be used to get sessions.
	 * 
	 * @param userName 		The username for login into the repository
	 * @param password 		password of the user	
	 * @param repository 	repository name
	 * @return				returns the session manager
	 * @throws Exception
	 */
	public static IDfSessionManager getSessionManager (String userName, String password, String repository) throws DfException
	{
		// create a client object using a factory method in DfClientX
		DfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();
		// call a factory method to create the session manager
		IDfSessionManager sessionMgr = client.newSessionManager();
		// create an IDfLoginInfo object and set its fields
		IDfLoginInfo loginInfo = clientx.getLoginInfo();
		loginInfo.setUser(userName);
		loginInfo.setPassword(password);
		sessionMgr.setIdentity(repository, loginInfo);
		// set single identity for all docbases
		sessionMgr.setIdentity(IDfSessionManager.ALL_DOCBASES, loginInfo);

		return sessionMgr;
	}
	
	/**
	 * This utility method executes two queries needed after name change in CYPF
	 * 
	 * @param oldUserName	The old name of the user under question
	 * @param newUserName	The new name of the user
	 * @param session		The DFC session object
	 * 
	 */
	public static void executeCYPFUserRenameQueries (String oldUserName, String newUserName, IDfSession session) throws DfException {
		
		String escapedOldUserName = oldUserName.replace("'", "''");
		String escapedNewUserName = newUserName.replace("'", "''");
		
		String dql1 = "execute EXEC_SQL with query ='update dm_sysobject_s set r_creator_name = ''" + escapedNewUserName + "'' where r_creator_name = ''" + escapedOldUserName + "'' '";
		String dql2 = "execute EXEC_SQL with query ='update dm_sysobject_s set r_modifier = ''" + escapedNewUserName + "'' where r_modifier = ''" + escapedOldUserName + "'' '";
		//Now execute the queries
		executeDQL(dql1, session);
		executeDQL(dql2, session);
	}
	
	/**
	 * This utility method resets a dm_user as a unix user (that can do fresh sync with AD)
	 * 
	 * @param userID		The user id to be initialized to this value
	 * @param userName		The userName 
	 */
	public static void initializeAsUnixUser(String userID, String userName, IDfSession session) throws DfException {
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
			executeDQL(dql1, session);
			executeDQL(dql2, session);
			executeDQL(dql3, session);
			executeDQL(dql4, session);
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
	 * This utility method executes a documentum java method 
	 * 
	 * @param methodName	Method name as defined in the object_name of dm_method object.
	 * @param arguments		 
	 * 
	 * 
	 */
	public static IDfCollection executeMethodSynchronous (String methodName, String arguments, IDfSession session) throws DfException {
		// Call the method.
		// *********************************************************************************
		IDfList argList = new DfList();
		IDfList typeList = new DfList();
		IDfList valueList = new DfList();

		argList.appendString("METHOD");
		typeList.appendString("S");
		valueList.appendString(methodName);

		argList.appendString("ARGUMENTS");
		typeList.appendString("S");
		valueList.appendString(arguments);

		argList.appendString("RUN_AS_SERVER");
		typeList.appendString("B");
		valueList.appendString("T");

		argList.appendString("SAVE_RESULTS");
		typeList.appendString("B");
		valueList.appendString("T");
		System.out.println("Running the method: " + methodName + ". It may take couple minutes....");
		IDfCollection results = session.apply(null, "DO_METHOD", argList, typeList, valueList);
		System.out.println("Method run completed");
		
		return results;
		

	}
	/**
	 * This utility method is used to rename a user
	 * @throws DfException 
	 * 
	 */
	public static void userRenameNonSynchronous (String oldUserName, String newUserName, IDfSession session ) throws DfException {
		IDfUser dctmUser = session.getUser(oldUserName);
		dctmUser.renameUser(newUserName, true, true, false);
		System.out.println("renameUser call method completed");
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
	public static void userRenameSynchronous (String oldUserName, String newUserName, IDfSession session, boolean cypfQueries ) throws DfException {
		//validate the oldUserName object exists in the repository
		
		IDfUser user = null;
		IDfId id = null;
		String sessionUser = null;
		String sessionDocbase = null;
		String jobId = null;
		String arguments = null;
		
		//Escape the old and new user names for quotes
		String escapedOldUserName = oldUserName.replace("'", "''");
		String escapedNewUserName = newUserName.replace("'", "''");
		
		
			user = session.getUser(oldUserName);
			if (user == null) {
				System.out.println("The " + oldUserName + " doesn't exist in the system");
				throw new DfException("The user " + oldUserName +  "doesn't exist in the repository");
				//return false;
			}
			//Get the details about the current session as it will be used in arguments.
			
			IDfLoginInfo loginInfo = session.getLoginInfo();
			sessionUser = loginInfo.getUser();
			sessionDocbase = session.getDocbaseName();
			id = (IDfId) session.getIdByQualification("dm_job where object_name = 'dm_UserRename'");
			jobId = id.toString();
			arguments = "-docbase_name " + sessionDocbase + " -user_name " + sessionUser + " -job_id " + jobId + " -method_trace_level 10";
			//TODO: First see if the oldUserName has any apostrophe in it or not, otherwise method will fail.
			
			//Firstly we need to create a dm_job_request object by constructing the following query
			String jobReqQuery = "create dm_job_request object set object_name='UserRename', set job_name='dm_UserRename', set method_name='dm_UserRename', "
					+ "set request_completed=FALSE, append arguments_keys='OldUserName', append arguments_keys='NewUserName', append arguments_keys='report_only', "
					+ "append arguments_keys='unlock_locked_obj', append arguments_values='" + escapedOldUserName + "', append arguments_values='" + escapedNewUserName + "',"
							+ "append arguments_values='F', append arguments_values='T'";
			//Now execute this query using executeDQL method
			executeDQL(jobReqQuery, session);
			//Now it is time to call the userRename method
			IDfCollection results = executeMethodSynchronous("dm_UserRename", arguments, session);	
			//System.out.println("Running the method: dm_UserRename. It may take couple minutes....");
			if (results != null && cypfQueries == true) {
				//This means we have to execute the queries for cypf
				executeCYPFUserRenameQueries(oldUserName, newUserName, session);	

					
			} 
		


		//return results;
	}
	
	
	 
	
	/**
	 * This utility method returns IDfSessionManager using Trusted Authentication that can be used to get sessions.
	 * 
	 * @param userName 		The username for login into the repository	
	 * @param repository 	repository name
	 * @return				returns the session manager
	 * @throws Exception
	 */
	public static IDfSessionManager getSessionManagerTrustedAuth (String userName, String repository) throws DfException
	{
		// create a client object using a factory method in DfClientX
		DfClientX clientx = new DfClientX();
		IDfClient client = clientx.getLocalClient();
		// call a factory method to create the session manager
		IDfSessionManager sessionMgr = client.newSessionManager();
		// create an IDfLoginInfo object and set its fields
		IDfLoginInfo loginInfo = clientx.getLoginInfo();
		loginInfo.setUser(userName);
		loginInfo.setPassword("");
		loginInfo.setDomain("");
		sessionMgr.setIdentity(repository, loginInfo);
		// set single identity for all docbases
		sessionMgr.setIdentity(IDfSessionManager.ALL_DOCBASES, loginInfo);

		return sessionMgr;
	}
	
	/**
	 * The public method used to execute a single DQL statement. If an exception is encountered, it would be logged
	 * and false would be returned.
	 *
	 * @param dql
	 * @param session
	 * @return		true/false to show if the query was executed successfully or not
	 * 
	 */
	public static void executeDQL(String dql, IDfSession session) throws DfException {
		IDfQuery query = new DfClientX().getQuery();	
		query.setDQL(dql);
		query.execute(session, IDfQuery.DF_EXEC_QUERY);				
				
	}
	
	/**
	 * TODO: modify/enhance to include custom ACL attachment
	 * 
	 * Given a cabinet name and session, the method creates a cabinet
	 * 
	 * @param o				The object reference used for DfLogger statements
	 * @param cabName		The name of the cabinet that needs to be created
	 * @param session		The documentum session.
	 * @return
	 */
	public static IDfId createCabinet(Object o, String cabName, IDfSession session) {
        StringBuffer cabQual = new StringBuffer(32);
        IDfId cabId = null;
        cabQual.append("dm_cabinet where object_name='").append(cabName).append("'");
        try {
	        IDfId id = session.getIdByQualification(cabQual.toString());        
	        if (id.isNull())
	        {
	           //need to create cabinet and it doesn't already exist
	           IDfFolder cab = (IDfFolder) session.newObject("dm_cabinet");
	           cab.setObjectName(cabName);
	           cab.save();
	           logInfo(o, "cabinet created successfully: " + cabName);
	           cabId = cab.getObjectId();
	        }
        } catch (DfException df) {
        	df.printStackTrace();
        } finally {
        	return cabId;
        }
	}
	
	/**
	 * TODO: Modify/enhance to include custom ACL attachment
	 * TODO: Modify/enhance to create folders of other types. In that case, the
	 * 		 type would need to be a parameter to the method itself.
	 * 
	 * The method creates a folder and links it to a cabinet/folder.
	 * 
	 * 
	 * @param o					The object reference used for DfLogger statements
	 * @param folderName		The name of the folder that needs to be created
	 * @param parentId			IDfId of the parent folder/cabinet which needs to be linked to
	 * @param session			dfc session object
	 * @return
	 */
	public static IDfId createFolder(Object o, String folderName, IDfId parentId, IDfSession session) {
		
		IDfFolder parentFldr = null;
		String fldrPath = null;
		IDfId newFldrId = null;
		try {
			//First lets get the folder path to the parentId
			parentFldr = (IDfFolder) session.getObject(parentId);
			fldrPath = parentFldr.getFolderPath(0);
			//System.out.println(fldrPath);
			//Now create the folder object and link it
			IDfSysObject newFolder = (IDfFolder) session.newObject("dm_folder");
			IDfFolder aFolder = session.getFolderByPath(fldrPath + "/" + folderName);
			if (aFolder == null) { //this means folder doesn't already exist with the same name
				logInfo(o, "creating: " + fldrPath + "/" + folderName);
				newFolder.setObjectName(folderName);
				newFolder.link(fldrPath);
				newFolder.save();
				newFldrId = newFolder.getObjectId();
			}
		} catch (DfException df) {
			df.printStackTrace();
		} finally {
			return newFldrId;
		}
	}
	
	/* 
	 * @see getDocbaseSUSer(OutputStream output) throws IOException
	 * Queries the docbase for the install owner
	 * Returns the r_install_owner from the connected server's dm_server_config object
	 *  Throws an IOException if one occurs during the query
	 */		
	public static String getDocbaseSUSer(IDfSession sess, PrintWriter output)throws Exception{
		String strSUser = "";
		//DfExUtils docQuery = new DfExUtils(output); 
		//get the docbase's installation owner: select r_install_owner from dm_server_config
		//strSUser = docQuery.getSingleResultFromQuery(session,"r_install_owner", "dm_server_config" );
		IDfId objIdSUser = sess.getIdByQualification("dm_server_config");
		if (objIdSUser==null) {
			throw new Exception("Error in com.uhb.dctm.portal.server.method.GetReferralsForPatient#getDocbaseSUSer : Cant get Server Config Object");
		}
		IDfSysObject suUserObj = (IDfSysObject) sess.getObject(objIdSUser);
		strSUser = suUserObj.getString("r_install_owner");
		return strSUser;
	}	
	
	private static void logInfo(Object o, String message) {
		DfLogger.info(o, message, null, null);
	}
	public static void logMethod(OutputStream output, String message) throws IOException {
		output.write(message.getBytes());
	}

	
}
