package stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.impl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import javax.persistence.EntityManager;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.aspects.SheetName;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCWBMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.interfaces.ISCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.interfaces.ISCDataContainerDAO;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.utilities.implementations.ObjectModelFactory;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service
public class SCDataContainerDAO implements ISCDataContainerDAO
{
	
	private String scCode;
	
	private String from = "from ";
	
	private String space = " ";
	
	private String where = "where SCCode =: lv_scCode";
	
	private String lv_scCode = "lv_scCode";
	
	private scDataContainer scDC;
	
	@Autowired
	private ISCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	EntityManager entityManager;
	
	private SessionFactory sFac;
	
	@Override
	public scDataContainer load(
	        String scCode
	) throws Exception
	{
		scDC = null;
		
		if (wbMdtSrv != null && scCode != null)
		{
			if (scCode.trim().length() >= 3) // Min'm 3 char Scrip Code
			{
				this.scCode = scCode;
				
				SCWBMetadata wbMdt = wbMdtSrv.getWbMetadata();
				if (wbMdt != null)
				{
					ArrayList<SCSheetMetadata> sheetsMdt = wbMdt.getSheetMetadata();
					if (sheetsMdt != null)
					{
						scDC = new scDataContainer();
						for (SCSheetMetadata shMdt : sheetsMdt)
						{
							loadDataforSheet(shMdt, scDC);
						}
					}
				}
				
			}
		}
		
		return scDC;
	}
	
	/**
	 * Load Root Always in Conjunction
	 */
	@Override
	public scDataContainer load(
	        String scCode, String sheetName
	) throws Exception
	{
		scDC = null;
		
		if (wbMdtSrv != null && scCode != null)
		{
			if (scCode.trim().length() >= 3) // Min'm 3 char Scrip Code
			{
				this.scCode = scCode;
				
				SCWBMetadata wbMdt = wbMdtSrv.getWbMetadata();
				if (wbMdt != null)
				{
					ArrayList<SCSheetMetadata> sheetsMdt = wbMdt.getSheetMetadata();
					if (sheetsMdt != null)
					{
						scDC = new scDataContainer();
						Optional<SCSheetMetadata> shMdtO = sheetsMdt.stream()
						        .filter(x -> x.getSheetName().equals(sheetName)).findFirst();
						if (shMdtO.isPresent())
						{
							loadDataforSheet(shMdtO.get(), scDC);
						}
						
						//Also Load Root Container by DEfault if not requested to Distinguish Containers
						if (!sheetName.equals(SheetNames.General))
						{
							
							SCSheetMetadata shMdtRoot = wbMdtSrv.getMetadataforBaseSheet();
							loadDataforSheet(shMdtRoot, scDC);
						}
						
					}
				}
				
			}
		}
		
		return scDC;
	}
	
	public scDataContainer getScDC(
	)
	{
		return scDC;
	}
	
	// G- Get, S- Set
	public Method getMethodfromSCDataContainer(
	        String sheetName, char Type
	)
	{
		
		Method M = null;
		if (sheetName != null)
		{
			Class<?> klass = scDataContainer.class;
			
			final ArrayList<Field> allFields = new ArrayList<Field>(Arrays.asList(klass.getDeclaredFields()));
			for (final Field field : allFields)
			{
				if (field.isAnnotationPresent(SheetName.class))
				{
					SheetName ann = field.getAnnotation(SheetName.class);
					if (ann != null)
					{
						if (ann.Name().equals(sheetName))
						{
							scDataContainer scCont  = new scDataContainer();
							Object_Info     objInfo = new Object_Info(scCont);
							if (objInfo != null)
							{
								
								switch (Type)
								{
									case 'G':
										M = objInfo.Get_Getter_for_FieldName(field.getName());
										break;
									
									case 'S':
										M = objInfo.Get_Setter_for_FieldName(field.getName());
										break;
								}
							}
						}
					}
					
				}
			}
		}
		
		return M;
	}
	
	/**
	 * PRIVATE SECTION
	 * 
	 * @throws Exception
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 */
	@SuppressWarnings("unchecked")
	private void loadDataforSheet(
	        SCSheetMetadata shMdt, scDataContainer scDC
	) throws Exception
	{
		String  qS   = getQueryString(shMdt);
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (sFac != null)
		{
			
			if (sess != null)
			{
				Query Q = sess.createQuery(qS);
				if (Q != null)
				{
					Q.setParameter(lv_scCode, this.scCode);
					List<Object> resList = Q.getResultList();
					if (resList != null)
					{
						if (resList.size() > 0)
						{
							// Get set Method from scDataContainer
							Method M = this.getMethodfromSCDataContainer(shMdt.getSheetName(), 'S');
							if (M != null)
							{
								Object_Info objInfo = null;
								if (shMdt.isCollection())
								{
									objInfo = new Object_Info(shMdt.getAssembly());
									Class     obj_class = objInfo.getCurr_Obj_Class();
									ArrayList objList   = new ArrayList<>();
									for (Object object : resList)
									{
										Object objInstance = ObjectModelFactory.createObjectbyClass(obj_class);
										if (objInstance != null)
										{
											objInstance = object;
											objList.add(objInstance);
										}
									}
									M.invoke(scDC, objList);
								} else
								{
									// Create new Object Instance by assembly from Sheet MetaData
									if (shMdt.isBaseSheet())
									{
										objInfo = new Object_Info(shMdt.getAssembly() + "Q");
									} else
									{
										objInfo = new Object_Info(shMdt.getAssembly());
									}
									// Get the Class of the business object name from Object Info.
									Class  obj_class   = objInfo.getCurr_Obj_Class();
									Object objInstance = ObjectModelFactory.createObjectbyClass(obj_class);
									if (objInstance != null)
									{
										objInstance = resList.get(0);
									}
									M.invoke(scDC, objInstance);
								}
							}
							
						}
					}
				}
			}
		}
		
	}
	
	private String getQueryString(
	        SCSheetMetadata shMdt
	)
	{
		String qS = null;
		if (shMdt.isBaseSheet())
		{
			qS = from + shMdt.getBobjName() + "Q" + space + where;
		} else
		{
			qS = from + shMdt.getBobjName() + space + where;
		}
		
		return qS;
	}
	
}
