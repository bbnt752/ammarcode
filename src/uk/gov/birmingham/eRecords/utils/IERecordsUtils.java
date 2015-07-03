package uk.gov.birmingham.utils;

import com.documentum.fc.client.IDfSession;
import com.documentum.fc.common.DfException;

public interface IERecordsUtils {

	public void executeCYPFUserRenameQueries (String oldUserName, String newUserName, IDfSession session) throws DfException;
	public void initializeAsUnixUser(String userID, String userName, IDfSession session) throws DfException;
	public void eRecordsUserRenameSynchronous (String oldUserName, String newUserName, IDfSession session, boolean cypfQueries ) throws DfException;
	
}
