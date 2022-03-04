package stocktales.healthcheck.impl.subSrv;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import stocktales.dataBook.enums.EnumEffect;
import stocktales.dataBook.enums.EnumEmphasis;
import stocktales.healthcheck.annotations.HealthCheckCriteria;
import stocktales.healthcheck.beanSrv.helperPOJO.NameVal;
import stocktales.healthcheck.beanSrv.intf.IScDataContSrv;
import stocktales.healthcheck.enums.EnumDataContainerTitle;
import stocktales.healthcheck.enums.EnumHCCriteria;
import stocktales.healthcheck.intf.IHC_SubSrvRes;
import stocktales.healthcheck.model.helperpojo.HCDataContainer;
import stocktales.healthcheck.model.helperpojo.HCMsg;
import stocktales.healthcheck.model.helperpojo.HCSubSrvRes;

@Service
@HealthCheckCriteria(criteria = EnumHCCriteria.Growth, isApplicableforFinancials = true)
public class RevTrajectorySrv implements IHC_SubSrvRes
{
	@Autowired
	private IScDataContSrv scDCSrc;
	
	@Autowired
	private MessageSource msgSrc;
	
	private final String attrName = "Sales";
	
	private List<HCSubSrvRes> results = new ArrayList<HCSubSrvRes>();
	
	@Override
	public List<HCSubSrvRes> getSubServiceResults(
	        String scCode
	)
	{
		this.results.clear();
		
		try
		{
			
			List<NameVal> nameValCol = scDCSrc.getDeltaListforBalSheet(scCode, attrName);
			if (nameValCol != null)
			{
				if (nameValCol.size() > 0)
				{
					//REsult POJO
					HCSubSrvRes result = new HCSubSrvRes();
					
					HCDataContainer dataCon = new HCDataContainer();
					dataCon.setTitle(EnumDataContainerTitle.SalesTrendsOverall);
					dataCon.setNameValContainer(nameValCol);
					
					result.getContainers().add(dataCon); //Add container to Result POJO
					
					processSalesTrends(result);
					
					this.results.add(result);
					
				}
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return this.results;
	}
	
	private void processSalesTrends(
	        HCSubSrvRes result
	)
	{
		//Populate Another Container For negative Growth Years if Any
		int           totalYrs  = result.getContainers().get(0).getNameValContainer().size();
		List<NameVal> negGrowth = result.getContainers().get(0).getNameValContainer().stream()
		        .filter(x -> x.getValue() < 0).collect(Collectors.toList());
		if (negGrowth != null)
		{
			if (negGrowth.size() > 0)
			{
				//Add Negative Growth Container Too
				result.getContainers().add(new HCDataContainer(EnumDataContainerTitle.NegativeGrowth, negGrowth));
				//3 or More years of negative growth in last 10 years is an Issue
				if (negGrowth.size() >= 3)
				{
					HCMsg msg = new HCMsg();
					msg.setEffect(EnumEffect.Negative);
					msg.setEmphasis(EnumEmphasis.Major);
					msg.setMsg(msgSrc.getMessage("LOWGROWTH_OV", new Object[]
					{ negGrowth.size(), totalYrs + 1 }, Locale.ENGLISH));
					
					result.getMessages().add(msg);
				} else
				{
					HCMsg msg = new HCMsg();
					msg.setEffect(EnumEffect.Monitor);
					msg.setEmphasis(EnumEmphasis.Mild);
					msg.setMsg(msgSrc.getMessage("LOWGROWTH_OV", new Object[]
					{ negGrowth.size(), totalYrs + 1 }, Locale.ENGLISH));
					
					result.getMessages().add(msg);
				}
				
			} else
			{
				HCMsg msg = new HCMsg();
				msg.setEffect(EnumEffect.Positive);
				msg.setEmphasis(EnumEmphasis.Major);
				msg.setMsg(msgSrc.getMessage("GOODGROWTH_OV", new Object[]
				{ totalYrs + 1 }, Locale.ENGLISH));
				
				result.getMessages().add(msg);
			}
			
		}
		
	}
	
}
