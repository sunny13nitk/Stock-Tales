package stocktales.scripsEngine.uploadEngine.scripSheetServices.implementations;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.enums.SCEenums;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SheetFieldsMetadata;
import stocktales.scripsEngine.uploadEngine.Metadata.services.implementations.SCWBMetadataSrv;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_GeneralQ;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.DAO.types.scDataContainer;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.interfaces.ISCDataContainerSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn.ISCDBUpdateSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.DBSrv.defn.ScripUpdateContainerPOJO;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCCode_Getter_XLS;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.IXLS_Scrip_Update_Srv;
import stocktales.scripsEngine.uploadEngine.tools.definitions.FldVals;
import stocktales.scripsEngine.uploadEngine.tools.definitions.SheetFldValsHeadersList;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_SingleCard_SheetRawData;
import stocktales.scripsEngine.uploadEngine.tools.definitions.TY_WBRawData;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IHeadersDeltaGetSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IWBRawDataSrv;
import stocktales.scripsEngine.utilities.implementations.ObjectModelFactory;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service("XLS_Scrip_Update_Srv")
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class XLS_Scrip_Update_Srv implements IXLS_Scrip_Update_Srv
{
	
	/*
	 * -------------- AUTOWIRED MEMEBERS ----------------------
	 */
	
	@Autowired
	private IWBRawDataSrv wbRawDataSrv;
	
	@Autowired
	private SCWBMetadataSrv wbMdtSrv;
	
	@Autowired
	private ISCExistsDB_Srv scExistsSrv;
	
	@Autowired
	private ISCCode_Getter_XLS scCodeGetSrv;
	
	@Autowired
	private IHeadersDeltaGetSrv headerDeltaSrv;
	
	@Autowired
	private ISCDBUpdateSrv scDBUpdSrv;
	
	@Autowired
	private ISCDataContainerSrv scDCSrv;
	
	/*
	 * -------------- PRIVATE MEMBERS ----------------------
	 */
	
	private TY_WBRawData wbRawData;
	
	private EN_SC_GeneralQ scRoot;
	
	private Object_Info rootobjinfo;
	
	private scDataContainer scDC;
	
	/*
	 * Interface Implementation
	 */
	
	@Override
	public boolean Update_Scrip_WbContext(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		boolean updated = false;
		
		if (wbCtxt != null)
		{
			
			try
			{
				
				// 1. GEt Scrip Root
				getScRoot(wbCtxt);
				if (this.scRoot != null && scDCSrv != null)
				{
					// 2. Get the RawData
					getWbRawData(wbCtxt);
					
					// 2.5 Get the Existing Scrip Data from DB
					
					scDCSrv.load(scRoot.getSCCode());
					this.scDC = scDCSrv.getScDC();
					
					// 3. Update Root Entity
					updateRootEntity();
					
					// 4. Update Related Entities
					updateRelatedEntities(wbCtxt);
					
					updated = true;
				}
				
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
		return updated;
	}
	
	/*
	 * ------------------------ PRIVATE METHODS -----------------------------
	 */
	
	private void getScRoot(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		if (scExistsSrv != null && scCodeGetSrv != null)
		{
			String scCode = scCodeGetSrv.getSCCode(wbCtxt);
			if (scCode != null)
			{
				this.scRoot = scExistsSrv.Get_ScripExisting_DB(scCode);
			}
			
		}
	}
	
	private void getWbRawData(
	        XSSFWorkbook wbCtxt
	) throws EX_General
	{
		if (wbRawDataSrv != null)
		{
			
			this.wbRawData = wbRawDataSrv.getSheetFldVals(wbCtxt);
			
		}
	}
	
	private void updateRootEntity(
	) throws Exception
	{
		if (scRoot != null)
		{
			boolean                  tobeUpdated = false;
			ScripUpdateContainerPOJO scUpdCont   = new ScripUpdateContainerPOJO();
			
			// GEt Shet Metadata
			SCSheetMetadata shMdt = wbMdtSrv.getMetadataforBaseSheet();
			if (shMdt != null)
			{
				
				// Basics
				
				// GEt Raw Data from WB
				TY_SingleCard_SheetRawData baseSheetRawData = this.wbRawData.getNonCollSheetsRawData().stream()
				        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
				
				if (baseSheetRawData != null)
				{
					
					// Get Object Info for Root
					
					rootobjinfo = new Object_Info(EN_SC_GeneralQ.class.getName());
					if (rootobjinfo != null)
					{
						for (SheetFieldsMetadata fldMdt : shMdt.getFldsMdt())
						{
							// For Non Unmodifiable - i.e. Modifiable Fields
							if (!fldMdt.isUnModifiable())
							
							{
								scUpdCont.setObjInstance(scRoot);
								// Get the value from Raw Data Object
								Object val = baseSheetRawData.getSheetRawData().stream()
								        .filter(y -> y.getAttrName().equals(fldMdt.getObjField())).findFirst().get()
								        .getValue();
								
								Object exisVal = null; // Existing Value initialized as null
								if (val != null)
								{
									// First Scan if the relevant property is changed on the POJO
									
									Method getM = rootobjinfo.Get_Getter_for_FieldName(fldMdt.getObjField());
									if (getM != null)
									{
										// Invoke the POJO Getter for Existing Value
										exisVal = getM.invoke(scRoot);
									}
									
									if (!exisVal.equals(val))
									{
										if (scUpdCont.getUpdFldNames() == null)
										{
											List<String> updFlds = new ArrayList<String>();
											scUpdCont.setUpdFldNames(updFlds);
										}
										// Get the Setter by field Name from POJO
										Method setM = rootobjinfo.Get_Setter_for_FieldName(fldMdt.getObjField());
										if (setM != null)
										{
											// Invoke the POJO Setter with Raw Sheet Data Object Value
											setM.invoke(scRoot, val);
											scUpdCont.getUpdFldNames().add(fldMdt.getObjField());
										}
										tobeUpdated = true;
									}
								}
							}
						}
						
						if (tobeUpdated == true)
						{
							// Update the Root Entity
							scUpdCont.setMode(SCEenums.ModeOperation.UPDATE);
							scUpdCont.setWhrClauseFldName(shMdt.getEntPKey());
							
							if (scDBUpdSrv != null)
							{
								scDBUpdSrv.ProcessSCUpdate(scUpdCont);
							}
						}
					}
					
				}
				
			}
			
		}
		
	}
	
	@SuppressWarnings(
	    { "rawtypes", "unchecked" }
	)
	private void updateRelatedEntities(
	        XSSFWorkbook wbCtxt
	) throws EX_General, Exception
	{
		
		ArrayList                  entList   = null;
		Object                     entity    = null;
		ArrayList<SCSheetMetadata> sheetsMdt = wbMdtSrv.getWbMetadata().getSheetMetadata();
		if (sheetsMdt != null)
		{
			for (SCSheetMetadata shMdt : sheetsMdt)
			{
				if (!shMdt.isBaseSheet())
				{
					// Get the Get Method for Sheet Name for SCData Container Instance
					Method getM = scDCSrv.getMethodfromSCDataContainer(shMdt.getSheetName(), 'G');
					if (getM != null)
					{
						if (shMdt.isCollection())
						{
							entList = (ArrayList) getM.invoke(scDC);
						} else
						{
							Object_Info objInfo     = new Object_Info(shMdt.getAssembly());
							Class       obj_class   = objInfo.getCurr_Obj_Class();
							Object      objInstance = ObjectModelFactory.createObjectbyClass(obj_class);
							if (objInstance != null)
							{
								objInstance = getM.invoke(scDC);
							}
							entList = new ArrayList<>();
							entList.add(objInstance);
						}
						
						if (entList != null)
						{
							if (!shMdt.isUpdHeaderDeltaMode())
							{
								// Update Everything
								updateWODeltaHdrs(shMdt, entList);
							}
							
							else
							{
								// Update using DElta Headers
								// DElta Headers only need to be created - No Updation needed
								XSSFSheet sheetRef = wbCtxt.getSheet(shMdt.getSheetName());
								if (sheetRef != null)
								{
									updateWithDeltaHdrs(sheetRef, entList, shMdt);
								}
								
							}
						}
					}
					
				}
			}
			
		}
		
	}
	
	@SuppressWarnings("rawtypes")
	private void updateWODeltaHdrs(
	        SCSheetMetadata shMdt, ArrayList entList
	) throws Exception
	{
		TY_SingleCard_SheetRawData singleCardRawData = null;
		SheetFldValsHeadersList    collRawData       = null;
		int                        cnt               = 0;
		
		// Get Object Info for Sheet
		Object_Info              obj_Info    = new Object_Info(shMdt.getAssembly());
		boolean                  tobeUpdated = false;
		ScripUpdateContainerPOJO scUpdCont   = new ScripUpdateContainerPOJO();
		
		if (obj_Info != null)
		{
			
			if (!shMdt.isCollection()) // Single Cardinality Relations
			{
				// Get Sheet Raw Data
				singleCardRawData = wbRawData.getNonCollSheetsRawData().stream()
				        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
				if (singleCardRawData != null)
				{
					Object depObj = entList.get(0);
					if (depObj != null)
					{
						scUpdCont.setObjInstance(depObj);
						for (SheetFieldsMetadata fldMdt : shMdt.getFldsMdt())
						{
							// For Non Unmodifiable - i.e. Modifiable Fields
							if (!fldMdt.isUnModifiable())
							{
								// Get the value from Raw Data
								Object val     = singleCardRawData.getSheetRawData().stream()
								        .filter(y -> y.getAttrName().equals(fldMdt.getObjField())).findFirst().get()
								        .getValue();
								Object exisVal = null;                                                              // Existing Value initialized as null
								if (val != null)
								{
									
									// First Scan if the relevant property is changed on the POJO
									
									Method getM = obj_Info.Get_Getter_for_FieldName(fldMdt.getObjField());
									if (getM != null)
									{
										// Invoke the POJO Getter for Existing Value
										exisVal = getM.invoke(depObj);
									}
									
									if (!exisVal.equals(val))
									{
										if (scUpdCont.getUpdFldNames() == null)
										{
											List<String> updFlds = new ArrayList<String>();
											scUpdCont.setUpdFldNames(updFlds);
										}
										// Get the Setter by field Name from POJO
										Method setM = obj_Info.Get_Setter_for_FieldName(fldMdt.getObjField());
										if (setM != null)
										{
											// Invoke the POJO Setter with Raw Sheet Data Object Value
											setM.invoke(depObj, val);
											scUpdCont.getUpdFldNames().add(fldMdt.getObjField());
										}
										tobeUpdated = true;
									}
								}
							}
						}
						
						if (tobeUpdated == true)
						{
							// Update the Root Entity
							scUpdCont.setMode(SCEenums.ModeOperation.UPDATE);
							scUpdCont.setWhrClauseFldName(shMdt.getEntPKey());
							
							if (scDBUpdSrv != null)
							{
								scDBUpdSrv.ProcessSCUpdate(scUpdCont);
							}
						}
					}
				}
			} else // 1..n Cardinality Relations
			{
				cnt = 0;
				// Get Sheet Raw Data
				collRawData = wbRawData.getCollSheetsRawData().stream()
				        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
				if (collRawData != null)
				{
					// For Each Entity in Related Entities
					
					for (Object depObj : entList)
					{
						tobeUpdated = false;
						scUpdCont   = new ScripUpdateContainerPOJO();
						scUpdCont.setObjInstance(depObj);
						for (SheetFieldsMetadata fldMdt : shMdt.getFldsMdt())
						{ // For Non Unmodifiable - i.e. Modifiable  Fields 
							if (!fldMdt.isUnModifiable())
							{
								// Get the value from Raw Data
								Object val = collRawData.getFldValList().stream()
								        .filter(y -> y.getFieldName().equals(fldMdt.getSheetField())).findFirst().get()
								        .getFieldVals().get(cnt);
								if (val != null)
								{
									Object exisVal = null;
									// First Scan if the relevant property is changed on the POJO
									
									Method getM = obj_Info.Get_Getter_for_FieldName(fldMdt.getObjField());
									if (getM != null)
									{
										// Invoke the POJO Getter for Existing Value
										exisVal = getM.invoke(depObj);
									}
									
									if (!exisVal.equals(val))
									{
										if (scUpdCont.getUpdFldNames() == null)
										{
											List<String> updFlds = new ArrayList<String>();
											scUpdCont.setUpdFldNames(updFlds);
										}
										// Get the Setter by field Name from POJO
										Method setM = obj_Info.Get_Setter_for_FieldName(fldMdt.getObjField());
										if (setM != null)
										{
											// Invoke the POJO Setter with Raw Sheet Data Object Value
											setM.invoke(depObj, val);
											scUpdCont.getUpdFldNames().add(fldMdt.getObjField());
										}
										tobeUpdated = true;
									}
								}
							}
						}
						
						if (tobeUpdated == true)
						{
							// Update the Root Entity
							scUpdCont.setMode(SCEenums.ModeOperation.UPDATE);
							scUpdCont.setWhrClauseFldName(shMdt.getEntPKey());
							
							if (scDBUpdSrv != null)
							{
								scDBUpdSrv.ProcessSCUpdate(scUpdCont);
							}
						}
						
						cnt++; // Move to Next Entity Field Values 
					}
					
				}
				
			}
		}
	}
	
	@SuppressWarnings(
	    { "unchecked", "rawtypes" }
	)
	private void updateWithDeltaHdrs(
	        XSSFSheet sheetRef, ArrayList entList, SCSheetMetadata shMdt
	) throws Exception
	{
		SheetFldValsHeadersList  collRawData = null;
		ScripUpdateContainerPOJO scUpdCont   = null;
		
		// Get Object Info for Sheet
		Object_Info obj_Info = new Object_Info(shMdt.getAssembly());
		
		// Get FieldsMetadata
		ArrayList<SheetFieldsMetadata> fldsMdt   = shMdt.getFldsMdt();
		Object                         depPOJO   = null;
		Object                         headerVal = null;
		int                            cnt       = 0;
		
		String headerObjFldName = shMdt.getHeadScanConfig().getObjField();
		
		if (headerDeltaSrv != null)
		{
			
			for (Object deltaHdr : headerDeltaSrv.getHeadersDelta(sheetRef, entList, shMdt))
			{
				// Need to get raw Data for this header
				
				collRawData = wbRawData.getCollSheetsRawData().stream()
				        .filter(x -> x.getSheetName().equals(shMdt.getSheetName())).findFirst().get();
				if (collRawData != null)
				{
					cnt = 0;
					for (Object header : collRawData.getHeaders())
					{
						if (header.equals(deltaHdr))
						{
							headerVal = deltaHdr;
							break; // get out of loop
						}
						cnt++;
					}
					
				}
				
				// One POJO for Each Header - Insert Scenario ( Next/New Year or Quarter Details) 
				Class obj_class = obj_Info.getCurr_Obj_Class();
				depPOJO = ObjectModelFactory.createObjectbyClass(obj_class);
				
				if (depPOJO != null)
				{
					// 1. Set Header Value in POJO
					
					//1.pre - Instantiate SCrip Update Container for this New Insert Entity
					scUpdCont = new ScripUpdateContainerPOJO();
					if (scUpdCont.getUpdFldNames() == null)
					{
						List<String> updFlds = new ArrayList<String>();
						scUpdCont.setUpdFldNames(updFlds);
					}
					
					scUpdCont.setObjInstance(depPOJO);
					
					//First Set the Scrip Code if the Field is present in POJO - To establish FKey Relation
					String baseSheetKeyFldName = wbMdtSrv.getBaseSheetKey().getKeyFieldName();
					if (baseSheetKeyFldName != null)
					{
						Method setterH = obj_Info.Get_Setter_for_FieldName(baseSheetKeyFldName);
						if (setterH != null)
						{
							// 1.b. Invoke the Setter to Set the Header in POJO
							setterH.invoke(depPOJO, scRoot.getSCCode());
							//Update SC Update Container
							scUpdCont.getUpdFldNames().add(baseSheetKeyFldName);
						}
					}
					
					// 1.a. Get Setter by Header Field NAme
					
					Method setterH = obj_Info.Get_Setter_for_FieldName(headerObjFldName);
					if (setterH != null)
					{
						// 1.b. Invoke the Setter to Set the Header in POJO
						setterH.invoke(depPOJO, headerVal);
						//Update SC Update Container
						scUpdCont.getUpdFldNames().add(headerObjFldName);
					}
					
					// Set Each Field Value in POJO
					for (SheetFieldsMetadata fldMdt : fldsMdt)
					{
						// Get Field Values for the Field in question - Currently
						FldVals fldVals = collRawData.getFldValList().stream()
						        .filter(z -> z.getFieldName().equals(fldMdt.getSheetField())).findFirst().get();
						if (fldVals != null)
						{
							Method setterF = obj_Info.Get_Setter_for_FieldName(fldMdt.getObjField());
							if (setterF != null)
							{
								// Exception in case Field is mandatory and is not specified
								if (fldMdt.isMandatory() && fldVals.getFieldVals().get(cnt) == null)
								{
									EX_General egen = new EX_General("ERRNOMANDTVALUE", new Object[]
									{ fldVals.getFieldName(), shMdt.getSheetName() });
									throw egen;
								} else
								{
									// Non Mandatory and Still Null
									if (fldVals.getFieldVals().get(cnt) == null)
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
										scUpdCont.getUpdFldNames().add(fldMdt.getObjField());
									} else
									{
										// Non Mandatory - No Null - Go Ahead and SET
										// 1.b. Invoke the Setter to Set the Header in POJO
										setterF.invoke(depPOJO, fldVals.getFieldVals().get(cnt));
										scUpdCont.getUpdFldNames().add(fldMdt.getObjField());
									}
								}
								
							}
						}
						
					}
					
					// Once POJO Completely Set -Trigger Updates
					
					scUpdCont.setMode(SCEenums.ModeOperation.CREATE);
					scUpdCont.setWhrClauseFldName(shMdt.getEntPKey());
					
					if (scDBUpdSrv != null)
					{
						scDBUpdSrv.ProcessSCUpdate(scUpdCont);
					}
				}
				
			}
			
		}
	}
	
}
