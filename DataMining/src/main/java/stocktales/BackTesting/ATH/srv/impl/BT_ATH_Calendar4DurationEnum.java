package stocktales.BackTesting.ATH.srv.impl;

import java.util.Calendar;

import stocktales.BackTesting.ATH.enums.EnumBTDurations;
import stocktales.BackTesting.ATH.srv.intf.IBT_ATH_Calendar4DurationEnum;
import stocktales.durations.UtilDurations;

public class BT_ATH_Calendar4DurationEnum implements IBT_ATH_Calendar4DurationEnum
{

	@Override
	public Calendar getStartDate4EnumBTDuration(EnumBTDurations durationEnum)
	{
		Calendar startDate = UtilDurations.getTodaysCalendarDateOnly();

		switch (durationEnum)
		{
		case Months3:
			startDate.add(Calendar.MONTH, -3);
			break;

		case Months6:
			startDate.add(Calendar.MONTH, -6);
			break;

		case yr1:
			startDate.add(Calendar.YEAR, -1);
			break;

		case yr2:
			startDate.add(Calendar.YEAR, -2);
			break;

		case yr3:
			startDate.add(Calendar.YEAR, -3);
			break;

		case yr5:
			startDate.add(Calendar.YEAR, -5);
			break;

		case yr7:
			startDate.add(Calendar.YEAR, -7);
			break;

		case yr10:
			startDate.add(Calendar.YEAR, -10);
			break;

		default:
			break;
		}

		return startDate;
	}

}
