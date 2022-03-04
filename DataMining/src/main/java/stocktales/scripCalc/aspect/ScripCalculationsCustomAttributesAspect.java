package stocktales.scripCalc.aspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.math3.util.Precision;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.scripCalc.annotation.ScripCalcAttrList;
import stocktales.scripCalc.enums.EnumAttrRole;
import stocktales.scripCalc.enums.EnumCalcOperation;
import stocktales.scripCalc.enums.EnumDataType;
import stocktales.scripCalc.intf.IFieldValidator;
import stocktales.scripCalc.intf.IFieldValueRetriever;
import stocktales.scripCalc.intf.IIncrementalDeltaSrv;
import stocktales.scripCalc.intf.IScripCalculations;
import stocktales.scripCalc.pojos.TY_AttrMultiContainer;
import stocktales.scripCalc.pojos.TY_EntAttrValue;
import stocktales.scripCalc.pojos.TY_ScripCalcResult;
import stocktales.scripsEngine.pojos.helperObjs.SheetNames;
import stocktales.scripsEngine.uploadEngine.entities.EN_SC_BalSheet;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.scDataContainer.services.impl.SCDataContainerSrv;
import stocktales.scripsEngine.uploadEngine.scripSheetServices.interfaces.ISCExistsDB_Srv;

@Aspect
@Service
@Slf4j
public class ScripCalculationsCustomAttributesAspect
{
	@Autowired
	private SCDataContainerSrv scDCSrv;
	
	@Autowired
	private IFieldValidator fldValidatorSrv;
	
	@Autowired
	private IFieldValueRetriever fldValRetSrv;
	
	@Autowired
	private IIncrementalDeltaSrv icDeltaSrv;
	
	@Autowired
	private MessageSource msgSrc;
	
	@Autowired
	private ISCExistsDB_Srv scExSrv;
	
	@Around("ScripCalculationsInterface() && getScripCalculatedAttributesResult() && args(scCode)")
	public TY_ScripCalcResult getScripCalculatedAttributesResult(
	        ProceedingJoinPoint pjp, String scCode
	) throws Exception
	{
		
		TY_ScripCalcResult scAttrRes = new TY_ScripCalcResult();
		
		if (pjp.getTarget() != null && scCode != null)
		{
			
			/*
			 * Validate the Scrip
			 */
			
			if (scExSrv != null)
			{
				try
				{
					scExSrv.Is_ScripExisting_DB(scCode);
					
				} catch (EX_General e)
				{
					String msg = msgSrc.getMessage("INVALID_SCRIP", new Object[]
					{ scCode }, Locale.ENGLISH);
					log.debug(msg);
					throw new Exception(msg);
					
				}
			}
			
			if (pjp.getTarget() instanceof IScripCalculations)
			{
				/*
				 * Get All Declared Fields
				 */
				
				Field[] properties = pjp.getTarget().getClass().getFields();
				if (properties != null)
				{
					if (properties.length > 0)
					{
						for (Field field : properties)
						{
							if (field.isAnnotationPresent(ScripCalcAttrList.class))
							{
								Annotation        annotation  = field.getAnnotation(ScripCalcAttrList.class);
								ScripCalcAttrList attrListAnn = (ScripCalcAttrList) annotation;
								if (attrListAnn != null)
								{
									
									processFieldForAnnotationMulti(attrListAnn, scCode, scAttrRes);
									
								}
							}
						}
						
					}
				}
			}
			
		}
		
		return scAttrRes;
	}
	
	/*********************************************************************************************************
	 ***************** POINTCUT DEFINITIONS SECTION **********************************
	 *************************************************************************************************/
	
	/**
	 * Pointcut for Implementing Interface 'IScripCalculations'
	 */
	@Pointcut("target(stocktales.scripCalc.intf.IScripCalculations)")
	public void ScripCalculationsInterface(
	)
	{
		
	}
	
	/**
	 * PointCut for getScripCalculatedAttributesResult
	 */
	@Pointcut("execution( public * *.getScripCalculatedAttributesResult(..))")
	public void getScripCalculatedAttributesResult(
	)
	{
		
	}
	
	/*********************************************************************************************************
	 ***************** PRIVATE SECTION 
	 * @throws Exception **********************************
	 *************************************************************************************************/
	
	private void processFieldForAnnotationMulti(
	        ScripCalcAttrList attrListAnn, String scCode, TY_ScripCalcResult scAttrRes
	) throws Exception
	{
		List<TY_EntAttrValue> entAttrVal = null;
		//1. Instantiate the Container for the Sheet
		
		if (scDCSrv != null)
		{
			scDCSrv.load(scCode, attrListAnn.sheetName());
		}
		
		//2. Switch SheetName
		
		switch (attrListAnn.sheetName())
		{
			case SheetNames.Analysis:
				List<EN_SC_BalSheet> balSheet_EL = scDCSrv.getScDC().getBalSheet_L();
				if (balSheet_EL != null)
				{
					List<String> attrNames = new ArrayList<String>();
					if (attrListAnn.keyAttr() != null)
					{
						if (attrListAnn.keyAttr().trim().length() > 0)
						{
							attrNames.add(attrListAnn.keyAttr());
						}
					}
					
					if (attrListAnn.attr1() != null)
					{
						if (attrListAnn.attr1().trim().length() > 0)
						{
							attrNames.add(attrListAnn.attr1());
						}
					}
					
					if (attrListAnn.attr2() != null)
					{
						if (attrListAnn.attr2().trim().length() > 0)
						{
							attrNames.add(attrListAnn.attr2());
						}
					}
					
					//3. Trigger Validation for Key and Attribute Feilds w.r.t Entity POJO in Question
					if (validateRequestedAttributesonAnnotation(balSheet_EL, attrNames))
					{
						entAttrVal = new ArrayList<TY_EntAttrValue>();
						
						//4. Get the Values For Attributes Configured In Annotation
						
						entAttrVal = getEntityAttributesValues(balSheet_EL, attrNames);
						if (entAttrVal.size() > 0)
						{
							//Perform as per Operation Type Enum 
							int size = entAttrVal.size();
							if (size > 0)
							{
								
								if (attrListAnn.operator() != EnumCalcOperation.IncrementalDelta)
								{
									
									//Prepare Multi- Attribute Container
									double                attr1              = 0, attr2 = 0;
									boolean               trav1              = false, trav2 = false;
									TY_AttrMultiContainer attrMultiContainer = new TY_AttrMultiContainer();
									attrMultiContainer.setAttrName(attrListAnn.label());
									if (attrMultiContainer != null)
									{
										for (TY_EntAttrValue attrVal : entAttrVal)
										{
											if (attrVal.getRole() == EnumAttrRole.Key)
											{
												NameVal nameVal = new NameVal();
												if (attrVal.getDataType() == EnumDataType.Integer)
												{
													nameVal.setName(Integer.toString((int) attrVal.getValue()));
												} else if (attrVal.getDataType() == EnumDataType.Double)
												{
													nameVal.setName(Double.toString((double) attrVal.getValue()));
												} else
												{
													nameVal.setName((String) attrVal.getValue());
												}
												
												attrMultiContainer.getNameVals().add(nameVal);
												
											}
											
											else //Non Key Loop Pass
											{
												switch (attrListAnn.operator())
												{
													case Division:
													case DivisionPercentage:
														if (attrVal.getRole() == EnumAttrRole.Attribute1)
														{
															attr1 = 0;
															if (attrVal.getDataType() == EnumDataType.Integer)
															{
																attr1 = (double) ((Integer) attrVal.getValue())
																        .intValue();
															} else if (attrVal.getDataType() == EnumDataType.Double)
															{
																attr1 = (double) attrVal.getValue();
															}
															trav1 = true;
														}
														
														if (attrVal.getRole() == EnumAttrRole.Attribute2)
														{
															attr2 = 0;
															if (attrVal.getDataType() == EnumDataType.Integer)
															{
																attr2 = (double) ((Integer) attrVal.getValue())
																        .intValue();
															} else if (attrVal.getDataType() == EnumDataType.Double)
															{
																attr2 = (double) attrVal.getValue();
															}
															trav2 = true;
														}
														
														if (trav1 == true && trav2 == true)
														{
															int sizeC = attrMultiContainer.getNameVals().size() - 1;
															if (attr1 != 0 && attr2 != 0)
															{
																
																if (
																    attrListAnn
																            .operator() == EnumCalcOperation.DivisionPercentage
																)
																{
																	attrMultiContainer.getNameVals().get(sizeC)
																	        .setValue(
																	                Precision.round(
																	                        (attr1 / attr2) * 100, 1));
																} else
																{
																	attrMultiContainer.getNameVals().get(sizeC)
																	        .setValue(
																	                Precision.round((attr1 / attr2),
																	                        2));
																}
																
															} else //Non Zero Values- REmove Name Value for That Key 
															{
																attrMultiContainer.getNameVals().remove(sizeC);
															}
															
															/*
															 * RESET VARIABLES
															 */
															attr1 = 0;
															attr2 = 0;
															trav1 = false;
															trav2 = false;
														}
														
														break;
													
													default:
														break;
												}
											}
										}
										scAttrRes.getAttrsMulti().add(attrMultiContainer);
									}
									
								}
								
								else //Handling Incremental Delta
								{
									if (icDeltaSrv != null)
									{
										scAttrRes.getAttrsMulti()
										        .add(icDeltaSrv.getAttrMultiContainer(entAttrVal, attrListAnn));
									}
								}
								
							}
						}
					}
					
				}
				break;
			
			default:
				break;
		}
		
	}
	
	private boolean validateRequestedAttributesonAnnotation(
	        Object instance, List<String> attributeNamesList
	) throws Exception
	{
		boolean isValid = true;
		
		if (fldValidatorSrv != null)
		{
			
			for (String attrName : attributeNamesList)
			{
				if (!fldValidatorSrv.isFieldValidforObject(instance, attrName))
				{
					
					String msg = msgSrc.getMessage("INVALID_ENTITY_FLD", new Object[]
					{ attrName, instance.getClass().getSimpleName() }, Locale.ENGLISH);
					log.debug(msg);
					throw new Exception(msg);
					
				}
			}
		}
		
		return isValid;
	}
	
	private List<TY_EntAttrValue> getEntityAttributesValues(
	        Object instance, List<String> attributeNamesList
	) throws Exception
	{
		List<TY_EntAttrValue> entAttr = new ArrayList<TY_EntAttrValue>();
		
		if (fldValRetSrv != null)
		{
			//the fields have already been validated in above call stack
			entAttr = fldValRetSrv.getFieldValueSemanticsforObject(instance, attributeNamesList, false);
		}
		
		return entAttr;
	}
	
}
