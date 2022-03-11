package stocktales.IDS.utility;

import java.util.Arrays;

import stocktales.IDS.pojo.IDS_SMAPreview;

public class SMASortUtility
{
	public static IDS_SMAPreview getSMASortedforIDS(IDS_SMAPreview smaPvwO)
	{
		IDS_SMAPreview smaIDS = smaPvwO;

		double[] smaS = new double[4];
		int i = 0;

		if (smaPvwO.getSMAI1() > 0)
		{
			smaS[i] = smaPvwO.getSMAI1();
			i++;
		}

		if (smaPvwO.getSMAI2() > 0)
		{
			smaS[i] = smaPvwO.getSMAI2();
			i++;
		}

		if (smaPvwO.getSMAI3() > 0)
		{
			smaS[i] = smaPvwO.getSMAI3();
			i++;
		}

		if (smaPvwO.getSMAI4() > 0)
		{
			smaS[i] = smaPvwO.getSMAI4();
			i++;
		}

		Arrays.sort(smaS);

		smaIDS.setSMAI4(smaS[0]);
		smaIDS.setSMAI3(smaS[1]);
		smaIDS.setSMAI2(smaS[2]);
		smaIDS.setSMAI1(smaS[3]);

		return smaIDS;
	}
}
