package uk.gov.birmingham.eRecords.utils;

import java.io.PrintWriter;

import com.documentum.fc.client.IDfCollection;
import com.documentum.fc.client.IDfSession;
import com.documentum.fc.client.IDfSessionManager;
import com.documentum.fc.common.DfException;
import com.documentum.fc.common.IDfId;

public interface IDFCUtils {

	public IDfSessionManager getSessionManager (String userName, String password, String repository) throws DfException;
	public IDfSession getDfcSession(IDfSessionManager sMgr, String repository) throws DfException;
	public IDfCollection callMethodSynchronously (String methodName, String arguments, IDfSession session) throws DfException;
	public void userRenameNonSynchronous (String oldUserName, String newUserName, IDfSession session ) throws DfException;
	public IDfCollection userRenameSynchronous (String oldUserName, String newUserName, IDfSession session) throws DfException;
	public IDfSessionManager getSessionManagerTrustedAuth (String userName, String repository) throws DfException;
	public void executeNonSelectQuery(String dql, IDfSession session) throws DfException;
	public IDfId createCabinet(Object o, String cabName, IDfSession session);
	public IDfId createFolder(Object o, String folderName, IDfId parentId, IDfSession session);
	public String getDocbaseSUSer(IDfSession sess, PrintWriter output)throws Exception;
	
	
}
