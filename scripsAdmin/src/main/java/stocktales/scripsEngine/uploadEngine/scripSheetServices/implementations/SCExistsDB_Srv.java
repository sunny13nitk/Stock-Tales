package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.types.ScripSector;

@Service("SCExistsDB_Srv")

public class SCExistsDB_Srv implements ISCExistsDB_Srv
{
	
	// need to inject the session factory
	
	private SessionFactory sFac;
	
	@Autowired
	EntityManager entityManager;
	
	@Override
	@Transactional
	public boolean Is_ScripExisting_DB(
	        String scCode
	) throws EX_General
	{
		boolean scExists = false;
		
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (scCode != null && sFac != null)
		{
			//Session sess = sFac.getCurrentSession();
			if (sess != null)
			{
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ where SCCode =: lv_scCode",
				        EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					qscRoot.setParameter("lv_scCode", scCode);
					
					List<EN_SC_GeneralQ> scrips = qscRoot.getResultList();
					if (scrips != null)
					{
						if (scrips.size() > 0)
						{
							if (scrips.get(0) != null)
							{
								scExists = true;
							}
						}
					}
				}
			}
			
		}
		
		return scExists;
	}
	
	@Override
	@Transactional
	public EN_SC_GeneralQ Get_ScripExisting_DB(
	        String scCode
	) throws EX_General
	{
		EN_SC_GeneralQ scRoot = null;
		Session        sess   = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		
		if (scCode != null && sFac != null)
		{
			//Session sess = sFac.getCurrentSession();
			if (sess != null)
			{
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ where SCCode =: lv_scCode",
				        EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					qscRoot.setParameter("lv_scCode", scCode);
					
					List<EN_SC_GeneralQ> scrips = qscRoot.getResultList();
					if (scrips != null)
					{
						if (scrips.size() > 0)
						{
							if (scrips.get(0) != null)
							{
								scRoot = scrips.get(0);
							}
						}
					}
				}
			}
		}
		
		return scRoot;
	}
	
	@Override
	@Transactional
	public boolean Is_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General
	{
		boolean scExists = false;
		
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		
		if (scDesc != null && sFac != null)
		{
			//Session sess = sFac.getCurrentSession();
			if (sess != null)
			{
				
				String scName = '%' + scDesc;
				
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ where SCName LIKE : scName",
				        EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					qscRoot.setParameter("scName", scName);
					
					List<EN_SC_GeneralQ> scrips = qscRoot.getResultList();
					if (scrips != null)
					{
						if (scrips.size() > 0)
						{
							if (scrips.get(0) != null)
							{
								scExists = true;
							}
						}
					}
				}
			}
			
		}
		
		return scExists;
	}
	
	@Override
	@Transactional
	public EN_SC_GeneralQ Get_ScripExisting_DB_DescSW(
	        String scDesc
	) throws EX_General
	{
		EN_SC_GeneralQ scRoot = null;
		Session        sess   = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (scDesc != null && sFac != null)
		{
			//Session sess = sFac.getCurrentSession();
			if (sess != null)
			{
				
				String scName = '%' + scDesc;
				
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ where SCName LIKE : scName",
				        EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					qscRoot.setParameter("scName", scName);
					
					List<EN_SC_GeneralQ> scrips = qscRoot.getResultList();
					if (scrips != null)
					{
						if (scrips.size() > 0)
						{
							if (scrips.get(0) != null)
							{
								scRoot = scrips.get(0);
							}
						}
					}
				}
			}
			
		}
		
		return scRoot;
	}
	
	@Override
	public List<EN_SC_GeneralQ> getAllScripsHeaders(
	) throws EX_General
	{
		List<EN_SC_GeneralQ> scGQHeaders = null;
		
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (sFac != null)
		{
			if (sess != null)
			{
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ", EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					scGQHeaders = qscRoot.getResultList();
				}
			}
		}
		
		return scGQHeaders;
		
	}
	
	@Override
	public List<String> getAllScripNames(
	) throws EX_General
	{
		List<String>         scNames     = null;
		List<EN_SC_GeneralQ> scGQHeaders = null;
		
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (sFac != null)
		{
			if (sess != null)
			{
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ", EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					scGQHeaders = qscRoot.getResultList();
					
				}
			}
			
			if (scGQHeaders != null)
			{
				scNames = new ArrayList<String>();
				for (EN_SC_GeneralQ en_SC_GeneralQ : scGQHeaders)
				{
					scNames.add(en_SC_GeneralQ.getSCCode());
				}
			}
		}
		return scNames;
	}
	
	@Override
	public List<ScripSector> getAllScripSectors(
	) throws EX_General
	{
		List<ScripSector>    scSectors   = null;
		List<EN_SC_GeneralQ> scGQHeaders = null;
		
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (sFac != null)
		{
			if (sess != null)
			{
				Query<EN_SC_GeneralQ> qscRoot = sess.createQuery("from EN_SC_GeneralQ", EN_SC_GeneralQ.class);
				if (qscRoot != null)
				{
					scGQHeaders = qscRoot.getResultList();
					
				}
			}
			
			if (scGQHeaders != null)
			{
				scSectors = new ArrayList<ScripSector>();
				for (EN_SC_GeneralQ en_SC_GeneralQ : scGQHeaders)
				{
					scSectors.add(new ScripSector(en_SC_GeneralQ.getSCCode(), en_SC_GeneralQ.getSector()));
				}
			}
		}
		return scSectors;
	}
	
	@Override
	public List<String> getAllSectors(
	) throws EX_General
	{
		
		List<String> sectors = null;
		sectors = entityManager.createQuery(
		        "select distinct p.Sector " + "from EN_SC_General p", String.class).getResultList();
		
		return sectors;
	}
	
	@Override
	public String getScripCodeByCodeorDescSW(
	        String searchTerm
	) throws Exception
	{
		
		String         scCode = null;
		EN_SC_GeneralQ scG    = this.Get_ScripExisting_DB(searchTerm);
		
		if (scG == null)
		{
			scG = this.Get_ScripExisting_DB_DescSW(searchTerm);
			
		}
		
		if (scG != null)
		{
			scCode = scG.getSCCode();
		}
		
		return scCode;
	}
	
	@Override
	public List<String> getAllScripNamesforSector(
	        String sector
	) throws EX_General
	{
		List<String> scripinSector = null;
		
		if (sector != null && entityManager != null)
		{
			
			scripinSector = entityManager.createQuery(
			        "SELECT distinct SCCode FROM EN_SC_General where Sector =: lv_sector", String.class)
			        .setParameter("lv_sector", sector).getResultList();
			
		}
		
		return scripinSector;
	}
	
}
