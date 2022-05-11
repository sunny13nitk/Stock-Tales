package stocktales.BackTesting.ATH.srv.intf;

import java.util.Calendar;

import stocktales.BackTesting.ATH.enums.EnumBTDurations;

public interface IBT_ATH_Calendar4DurationEnum
{
	public Calendar getStartDate4EnumBTDuration(EnumBTDurations durationEnum);
}
