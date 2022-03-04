package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.lang.reflect.Method;
import java.util.ArrayList;

import javax.persistence.EntityManager;

import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_General;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Quarters;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_Trends;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IXLS_Scrip_Create_Srv;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FldVals;
import stocktales.scripsEngine.uploadEngine.tools.definitions.SheetFldValsHeadersList;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_AttribVal;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_SingleCard_SheetRawData;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_WBRawData;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IWBRawDataSrv;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service("XLS_Scrip_Create_Srv")

@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)

public class XLS_Scrip_Create_Srv implements IXLS_Scrip_Create_Srv
{
	
	/*
	 * -------------- AUTOWIRED MEMEBERS ----------------------
	 */
	
	@Autowired
	EntityManager entityManager;
	
	private SessionFactory sFac;
	
	@Autowired
	private IWBRawDataSrv wbRawDataSrv;
	
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	/*
	 * -------------- PRIVATE MEMBERS ----------------------
	 */
	
	private TY_WBRawData wbRawData;
	
	private EN_SC_General scRoot;
	
	private String addEnt_Prefix = "add_";
	
	private String addEnt_Suffix = "_Entity";
	
	private Object_Info rootobjinfo;
	
	/*
	 * Interface Implementation
	 */
	@Override
	public boolean Create_Scrip_WbContext(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		boolean created = false;
		
		if (wbCtxt != null)
		{
			
			try
			{
				
				// 1. Get the RawData
				getWbRawData(wbCtxt);
				
				// 2. Create Root Entity
				createRootEntity();
				
				// 3. Create Related Entities
				createRelatedEntities();
				
				// 4. Finally Save the Root Entity - With @Transactional Context
				saveScripObject();
				
				// All Executed W/o any Exception
				created = true;
				
				// close the WB Context
				
			}
			
			catch (Exception e)
			{
				if (scRoot == null)
				
				{
					EX_General egen = new EX_General("ERRCRSCROOT", new Object[]
					{ null, e.getMessage() });
					
					throw egen;
				} else
				{
					EX_General egen = new EX_General("ERRCRSCROOT", new Object[]
					{ " ", e.getMessage() });
					
					throw egen;
					
				}
			}
		}
		return created;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	@Transactional
	public void saveScripRoot(
	)
	{
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (scRoot != null && sFac != null)
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
				
				Query<EN_SC_General> qRootIns = sess
				        .createSQLQuery("INSERT INTO TB_SC_General (SCCode, SCName,Sector, UPH, CMP,"
				                + "CurrPE, CurrPB, div_yield, mkt_cap, mcap_to_sales, EPS, PEG, CPS, sgto_capex, num_shares, DERatio, TTMSales ) VALUES "
				                + " (:lv_SCCode, :lv_SCName, :lv_Sector, :lv_UPH, :lv_CMP, :lv_CurrPE, :lv_CurrPB, :lv_DivYield, :lv_MktCap, "
				                + ":lv_MCapToSales, :lv_EPS, :lv_PEG, :lv_CPS, :lv_SGToCapex, :lv_NumShares, :lv_DERatio, :lv_TTMSales)");
				
				if (qRootIns != null)
				{
					qRootIns.setParameter("lv_SCCode", scRoot.getSCCode());
					qRootIns.setParameter("lv_SCName", scRoot.getSCName());
					qRootIns.setParameter("lv_Sector", scRoot.getSector());
					qRootIns.setParameter("lv_UPH", scRoot.getUPH());
					qRootIns.setParameter("lv_CMP", scRoot.getCMP());
					qRootIns.setParameter("lv_CurrPE", scRoot.getCurrPE());
					qRootIns.setParameter("lv_CurrPB", scRoot.getCurrPB());
					qRootIns.setParameter("lv_DivYield", scRoot.getDivYield());
					qRootIns.setParameter("lv_MktCap", scRoot.getMktCap());
					qRootIns.setParameter("lv_MCapToSales", scRoot.getMCapToSales());
					qRootIns.setParameter("lv_EPS", scRoot.getEPS());
					qRootIns.setParameter("lv_PEG", scRoot.getPEG());
					qRootIns.setParameter("lv_CPS", scRoot.getCPS());
					qRootIns.setParameter("lv_SGToCapex", scRoot.getSGToCapex());
					qRootIns.setParameter("lv_NumShares", scRoot.getNumShares());
					qRootIns.setParameter("lv_DERatio", scRoot.getDERatio());
					qRootIns.setParameter("lv_TTMSales", scRoot.getTTMSales());
					
					qRootIns.executeUpdate();
					
					txn.commit();
				}
				
			}
		}
	}
	
	@Override
	@Transactional
	
	public void saveScripObject(
	)
	{
		Session sess = this.entityManager.unwrap(Session.class);
		sFac = sess.getSessionFactory();
		if (scRoot != null && sFac != null)
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
				/*
				 * Root Already Saved - Commit the Other Entities to DB
				 */
				if (scRoot != null)
				{
					sess.save(scRoot.getTenYrEnt());
					sess.save(scRoot.getLast4QEnt());
					for (EN_SC_BalSheet balSheetEnt : scRoot.getBalSheetEntList())
					{
						sess.save(balSheetEnt);
					}
					
					for (EN_SC_Quarters qtrEnt : scRoot.getQtrEntList())
					{
						sess.save(qtrEnt);
					}
					
					for (EN_SC_Trends trendEnt : scRoot.getTrendsEntList())
					{
						sess.save(trendEnt);
					}
				}
			}
		}
	}
	/*
	 * ------------------------ PRIVATE METHODS -----------------------------
	 */
	
	private void getWbRawData(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		if (wbRawDataSrv != null)
		{
			
			this.wbRawData = wbRawDataSrv.getSheetFldVals(wbCtxt);
			
		}
	}
	
	private void createRootEntity(
	) throws Exception
	{
		
		if (wbMdtSrv != null && this.wbRawData != null)
		{
			
			// 1. Create POJO for Root
			this.scRoot = new EN_SC_General();
			TY_SingleCard_SheetRawData rootSheetRawData = null;
			
			rootobjinfo = new Object_Info(EN_SC_General.class.getName());
			
			// 2. Get Root Metadata
			
			SCSheetMetadata rootMdt = wbMdtSrv.getMetadataforBaseSheet();
			if (rootMdt != null)
			{
				// --- 3. Set POJO properties from Sheet Raw Data -----------
				
				// 3.a Get Base Sheet Raw Data
				rootSheetRawData = wbRawData.getNonCollSheetsRawData().stream()
				        .filter(x -> x.getSheetName().equals(rootMdt.getSheetName())).findFirst().get();
				
				if (rootSheetRawData != null)
				{
					// 3.b Loop through Raw data attributes and keep on setting in POJO
					
					for (TY_AttribVal fldVal : rootSheetRawData.getSheetRawData())
					{
						
						// Get the Setter by field Name from POJO
						
						Method setM = rootobjinfo.Get_Setter_for_FieldName(fldVal.getAttrName());
						if (setM != null)
						{
							// Check if the Field is Mandatory from Metadata and no Value is found in Sheet Raw // Data
							SheetFieldsMetadata fldMdt = rootMdt.getFldsMdt().stream()
							        .filter(y -> y.getObjField().equals(fldVal.getAttrName())).findFirst().get();
							if (fldMdt != null)
							{
								if (fldMdt.isMandatory() && fldVal.getValue() == null)
								{
									EX_General egen = new EX_General("ERRNOMANDTVALUE", new Object[]
									{ fldVal.getAttrLabel(), rootMdt.getSheetName() });
									throw egen;
								} // Invoke the POJO Setter with Raw Sheet Data Object Value
								setM.invoke(scRoot, fldVal.getValue());
							}
						}
						
					}
					
				}
				
			}
			saveScripRoot();
			
		}
		
	}
	
	/**
	 * Create RElated Entities to Root
	 * 
	 * @throws Exception
	 * 
	 * @throws EX_InvalidRelationException
	 */
	
	private void createRelatedEntities(
	) throws Exception
	{
		if (wbMdtSrv != null && wbRawData != null && scRoot != null)
		{
			// 1. Get WB Sheet Metadata except Root/Base Sheet
			
			ArrayList<SCSheetMetadata> sheetsMdt = wbMdtSrv.getWbMetadata().getSheetMetadata();
			
			for (SCSheetMetadata shMdt : sheetsMdt)
			{
				if (shMdt.isBaseSheet() != true)
				{
					
					// Get Object Information from Object Factory
					
					Object_Info objInfo = new Object_Info(shMdt.getAssembly());
					// Get the Class of the business object name from Object Info.
					Class obj_class = objInfo.getCurr_Obj_Class();
					
					if (shMdt.isCollection())
					{
						CreateDependantEntity_MultiCard(shMdt, objInfo);
						
					} else
					{
						// Single Cardinality Relations
						
						Object depObj = obj_class.newInstance();
						if (depObj != null)
						{
							CreateDependantEntity_SingCard(depObj, shMdt, objInfo);
						}
					}
				}
				
			}
		}
	}
	
	private void CreateDependantEntity_SingCard(
	        Object depPojo, SCSheetMetadata shMdt, Object_Info objInfo
	) throws EX_General, Exception
	{
		if (scRoot != null && wbRawData != null && shMdt != null && depPojo != null)
		{
			TY_SingleCard_SheetRawData sheetRawData = wbRawData.getNonCollSheetsRawData().stream()
			        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
			
			if (sheetRawData != null)
			{
				// 3.b Loop through Raw data attributes and keep on setting in POJO
				
				for (TY_AttribVal fldVal : sheetRawData.getSheetRawData())
				{
					// Get the Setter by field Name from POJO
					
					Method setM = objInfo.Get_Setter_for_FieldName(fldVal.getAttrName());
					if (setM != null)
					
					{
						// Check if the Field is Mandatory from Metadata and no Value is found in Sheet Raw
						
						SheetFieldsMetadata fldMdt = shMdt.getFldsMdt().stream()
						        .filter(y -> y.getObjField().equals(fldVal.getAttrName())).findFirst().get();
						
						if (fldMdt != null)
						{
							if (fldMdt.isMandatory() && fldVal.getValue() == null)
							{
								EX_General egen = new EX_General("ERRNOMANDTVALUE", new Object[]
								{ fldVal.getAttrLabel(), shMdt.getSheetName() });
								throw egen;
							}
							// Invoke the POJO Setter with Raw Sheet Data Object Value
							setM.invoke(depPojo, fldVal.getValue());
							
						}
					}
					
				}
				
				// Now Add to the the Root Entity Container for Single Commit operation
				String addMethodName = this.addEnt_Prefix + shMdt.getBobjName() + this.addEnt_Suffix;
				
				Method addRootEntM = rootobjinfo.getMethodbyName(addMethodName);
				if (addRootEntM != null)
				{
					addRootEntM.invoke(scRoot, depPojo);
				}
				
			}
			
		}
		
	}
	
	private void CreateDependantEntity_MultiCard(
	        SCSheetMetadata shMdt, Object_Info objInfo
	) throws Exception
	{
		// Get Coll Sheet Raw Data
		SheetFldValsHeadersList colSheetRawData = wbRawData.getCollSheetsRawData().stream()
		        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
		
		// Get FieldsMetadata
		ArrayList<SheetFieldsMetadata> fldsMdt = shMdt.getFldsMdt();
		Object                         depPOJO = null;
		
		if (colSheetRawData != null)
		{
			int header_cnt = 0;
			
			String headerObjFldName = shMdt.getHeadScanConfig().getObjField();
			
			// Looping at headers for Sheet
			
			for (Object header : colSheetRawData.getHeaders())
			{
				// One POJO for Each Header
				
				depPOJO = objInfo.getCurr_Obj_Class().newInstance();
				if (depPOJO != null)
				
				{
					// 1. Set Header Value in POJO
					
					// 1.a. Get Setter by Header Field NAme
					
					Method setterH = objInfo.Get_Setter_for_FieldName(headerObjFldName);
					if (setterH != null)
					{
						// 1.b. Invoke the Setter to Set the Header in POJO
						setterH.invoke(depPOJO, header);
					}
					
					// Set Each Field Value in POJO
					for (SheetFieldsMetadata fldMdt : fldsMdt)
					{
						// Get Field Values for the Field in question - Currently
						FldVals fldVals = colSheetRawData.getFldValList().stream()
						        .filter(z -> z.getFieldName().equals(fldMdt.getSheetField())).findFirst().get();
						if (fldVals != null)
						{
							
							Method setterF = objInfo.Get_Setter_for_FieldName(fldMdt.getObjField());
							if (setterF != null)
							
							{
								// Exception in case Field is mandatory and is not specified
								if (fldMdt.isMandatory() && fldVals.getFieldVals().get(header_cnt) == null)
								{
									EX_General egen = new EX_General("ERRNOMANDTVALUE", new Object[]
									{ fldVals.getFieldName(), shMdt.getSheetName() });
									
									throw egen;
								} else
								{
									// Non Mandatory and Still Null
									if (fldVals.getFieldVals().get(header_cnt) == null)
									{
										Object val = null;
										switch (fldMdt.getDataType())
										{
											case Numerical:
												val = 0;
												break;
											
											case Decimal:
												val = 0;
												break;
											
											case Date:
												val = " ";
												break;
											
											case String:
												val = " ";
												break;
										}
										
										setterF.invoke(depPOJO, val);
									} else
									{
										// Non Mandatory - No Null - Go Ahead and SET
										// 1.b. Invoke the Setter to Set the Header in POJO
										setterF.invoke(depPOJO, fldVals.getFieldVals().get(header_cnt));
									}
								}
								
							}
						}
					}
					
				}
				
				header_cnt++; // Increment for New header to Pick right values from Fld Values List
				
				// Once POJO Completely Set - Associate DEpendeant Entity to Root
				// Now Add to the the Root Entity Container for Single Commit operation
				String addMethodName = this.addEnt_Prefix + shMdt.getBobjName() + this.addEnt_Suffix;
				
				Method addRootEntM = rootobjinfo.getMethodbyName(addMethodName);
				if (addRootEntM != null)
				{
					addRootEntM.invoke(scRoot, depPOJO);
				}
				
				/*
				 * Method addRootEntM = sheetContInfo.getMethodbyName(addMethodName); if (addRootEntM != null) {
				 * addRootEntM.invoke(this.scEntCont, depPOJO); }
				 */
			}
			
		}
		
	}
	
}
