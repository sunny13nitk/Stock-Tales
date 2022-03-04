package stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.impl;

import java.lang.reflect.Method;

import javax.persistence.EntityManager;
import javax.persistence.Table;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.enums.SCEenums.ModeOperation;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn.ISCDBUpdateSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn.ScripUpdateContainerPOJO;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service

public class SCDBUpdateSrv implements ISCDBUpdateSrv
{
	
	@Autowired
	EntityManager entityManager;
	
	private SessionFactory sFac;
	
	private ScripUpdateContainerPOJO scUpdCont;
	
	private Object_Info objInfo;
	
	private String queryStr;
	
	private Query query;
	
	private String tableName;
	
	private String lv_prefix = "lv_";
	
	private String EntityPrefix = "EN";
	
	private String TablePrefix = "TB";
	
	@Override
	public void ProcessSCUpdate(
	        ScripUpdateContainerPOJO scUpdContainer
	) throws Exception
	{
		
		this.scUpdCont = scUpdContainer;
		
		initialize();
		
		// Get Object Info
		prepareObjectInfo();
		
		// Prepare Table Name
		getTableName();
		
		// PrepareQuery String
		prepareQueryString();
		
		// Execute the Query
		prepareExecuteQuery();
		
	}
	
	@Override
	@Transactional
	public int prepareExecuteQuery(
	) throws Exception
	{
		int     numrowsAffected = 0;
		Session sess            = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (sFac != null)
		{
			
			/*Session sess;
			try
			{
				sess = sFac.getCurrentSession();
			} catch (HibernateException e)
			{
				sess = sFac.openSession();
			}*/
			
			if (sess != null)
			{
				Transaction txn = sess.beginTransaction();
				
				if (scUpdCont.getMode() == SCEenums.ModeOperation.UPDATE)
				{
					this.query = sess.createQuery(queryStr);
				} else if (scUpdCont.getMode() == SCEenums.ModeOperation.CREATE)
				{
					this.query = sess.createSQLQuery(this.queryStr);
				}
				if (this.query != null)
				{
					setQueryParams();
					numrowsAffected = query.executeUpdate();
					
					txn.commit();
				}
			}
		}
		
		return numrowsAffected;
		
	}
	
	@Override
	public void prepareObjectInfo(
	)
	{
		if (this.scUpdCont.getObjInstance() != null)
		{
			this.objInfo = new Object_Info(scUpdCont.getObjInstance());
			
		}
		
	}
	
	@Override
	public void prepareQueryString(
	)
	{
		switch (scUpdCont.getMode())
		{
			case UPDATE:
				generateUpdateQuery();
				break;
			
			case CREATE:
				generateInsertQuery();
				break;
			
			default:
				break;
		}
		
	}
	
	@Override
	public void setQueryParams(
	) throws Exception
	{
		if (query != null)
		{
			int varLength = this.scUpdCont.getUpdFldNames().size();
			
			for (int i = 1; i <= varLength; i++)
			{
				String fldName = scUpdCont.getUpdFldNames().get(i - 1);
				
				Method getM = objInfo.Get_Getter_for_FieldName(fldName);
				if (getM != null)
				{
					Object val = getM.invoke(scUpdCont.getObjInstance());
					if (val != null)
					{
						query.setParameter(lv_prefix + fldName, val);
					}
				}
				
			}
			
			if (scUpdCont.getMode() == ModeOperation.UPDATE) // Specify Where Clause only in case of Update
			{
				// Now for Where Parameter
				Method getM = objInfo.Get_Getter_for_FieldName(scUpdCont.getWhrClauseFldName());
				if (getM != null)
				{
					Object val = getM.invoke(scUpdCont.getObjInstance());
					if (val != null)
					{
						query.setParameter(lv_prefix + scUpdCont.getWhrClauseFldName(), val);
					}
				}
			}
		}
		
	}
	
	@Override
	public void getTableName(
	)
	{
		
		this.tableName = this.scUpdCont.getObjInstance().getClass().getAnnotation(Table.class).name();
		
	}
	
	/***
	 * PRIVATE METHODS
	 */
	private void generateUpdateQuery(
	)
	{
		String T      = "T";
		String update = "UPDATE";
		String dot    = ".";
		String eqsymb = "=:";
		String where  = "WHERE";
		String space  = " ";
		String comma  = ",";
		
		String iniPart = update + space + this.scUpdCont.getObjInstance().getClass().getSimpleName() + space + T + space
		        + "SET" + space;
		
		String varPart = "";
		
		int varLength = this.scUpdCont.getUpdFldNames().size();
		
		if (varLength > 1)
		{
			
			for (int i = 1; i < varLength; i++)
			{
				String fldName = scUpdCont.getUpdFldNames().get(i - 1);
				varPart += T + dot + fldName + space + eqsymb + lv_prefix + fldName + comma + space;
			}
		}
		
		String fldName = scUpdCont.getUpdFldNames().get(varLength - 1);
		varPart = varPart + T + dot + fldName + space + eqsymb + lv_prefix + fldName + space;
		
		String wherePart = where + space + T + dot + this.scUpdCont.getWhrClauseFldName() + space + eqsymb + lv_prefix
		        + this.scUpdCont.getWhrClauseFldName();
		
		this.queryStr = iniPart + varPart + wherePart;
		
	}
	
	private void generateInsertQuery(
	)
	{
		String brOpen  = " (";
		String brClose = " ) ";
		String insert  = "INSERT INTO ";
		String values  = " VALUES ";
		String eqsymb  = ":";
		String space   = " ";
		String comma   = ",";
		
		String iniPart = insert + this.tableName + brOpen;
		
		String varFNPart = "";
		
		int varLength = this.scUpdCont.getUpdFldNames().size();
		
		if (varLength > 1)
		{
			
			for (int i = 1; i < varLength; i++)
			{
				String fldName = scUpdCont.getUpdFldNames().get(i - 1);
				varFNPart += fldName + comma + space;
			}
		}
		
		String fldName = scUpdCont.getUpdFldNames().get(varLength - 1);
		varFNPart = varFNPart + fldName + brClose;
		
		String fldVPart = "";
		for (int i = 1; i < varLength; i++)
		{
			fldName   = scUpdCont.getUpdFldNames().get(i - 1);
			fldVPart += eqsymb + lv_prefix + fldName + comma + space;
		}
		
		fldName  = scUpdCont.getUpdFldNames().get(varLength - 1);
		fldVPart = fldVPart + eqsymb + lv_prefix + fldName + brClose;
		
		this.queryStr = iniPart + varFNPart + values + brOpen + fldVPart;
	}
	
	private void initialize(
	)
	{
		this.query     = null;
		this.queryStr  = "";
		this.tableName = "";
		this.objInfo   = null;
		
	}
}
