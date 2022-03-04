package stocktales.scripsEngine.uploadEngine.tools.services;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.NoSuchElementException;

import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import stocktales.scripsEngine.uploadEngine.Metadata.definitions.SCSheetMetadata;
import stocktales.scripsEngine.uploadEngine.exceptions.EX_General;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.IHeadersDeltaGetSrv;
import stocktales.scripsEngine.uploadEngine.tools.interfaces.ISheetHeadersSrv;
import stocktales.scripsEngine.utilities.types.Object_Info;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_PROTOTYPE)
public class HeadersDeltaGetSrv implements IHeadersDeltaGetSrv
{
	
	@Autowired
	private ISheetHeadersSrv gethdrsSrv;
	
	@SuppressWarnings(
	    { "static-access", "unchecked" }
	)
	@Override
	public <T> ArrayList<T> getHeadersDelta(
	        XSSFSheet sheetRef, ArrayList<T> sheetEntityList, SCSheetMetadata shtMdt
	) throws EX_General
	{
		ArrayList<T> listHDelta = null;
		
		ArrayList<T> listEnt_A = new ArrayList<T>();
		
		ArrayList<T> listHSheet_B = new ArrayList<T>();
		
		String      hdrFld  = null;
		Object_Info objInfo = null;
		
		if (sheetRef != null && sheetEntityList != null && shtMdt != null)
		{
			if (sheetEntityList.size() > 0)
			{
				// Get Header field Name - Object Name
				hdrFld = shtMdt.getHeadScanConfig().getObjField();
				
				// Get Object Info for Sheet
				try
				{
					objInfo = new Object_Info(shtMdt.getAssembly());
				} catch (Exception e1)
				{
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if (hdrFld != null && objInfo != null)
				{
					// For Each Related Entity
					for (Object entObj : sheetEntityList)
					{
						// Get Getter for Header Field
						Method GetterHFld = objInfo.Get_Getter_for_FieldName(hdrFld);
						if (GetterHFld != null)
						{
							try
							{
								// Get value for the header field
								Object val = GetterHFld.invoke(entObj);
								if (val != null)
								
								{
									listEnt_A.add((T) val);
								}
							} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					}
					
					if (listEnt_A.size() > 0)
					{
						// Now get Headers for Current Sheet from WB Sheet Reference
						
						listHSheet_B = gethdrsSrv.getHeadersbySheet(sheetRef);
						
						if (listHSheet_B.size() > 0)
						{
							listHDelta = new ArrayList<T>();
							// For Each Header in B check the presence in A
							// If not Found add to DElta Headers List
							
							for (T head_elemB : listHSheet_B)
							{
								
								try
								{
									listEnt_A.stream().filter(x -> x.equals(head_elemB)).findFirst().get();
								}
								
								catch (NoSuchElementException e)
								{
									listHDelta.add(head_elemB); // If not Found add to DElta Headers List
								}
								
							}
							
						}
					}
				}
				
			}
			
		}
		
		return listHDelta;
	}
	
}
