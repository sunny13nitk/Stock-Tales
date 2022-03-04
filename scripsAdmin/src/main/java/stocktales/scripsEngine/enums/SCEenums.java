package stocktales.scripsEngine.enums;

import java.util.HashMap;
import java.util.Map;

public class SCEenums
{
	// Enumeration for Mode Classification
	
	public enum ModeOperation {
		CREATE(
		        0
		), UPDATE(
		        1
		), NONE(
		        2
		);
		
		private final int value;
		
		ModeOperation(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetModeOperation(
		)
		{
			return value;
		}
	}
	
	// Enumeration for Direction Classification
	
	public enum direction {
		INCREASE(
		        0
		), DECREASE(
		        1
		), NONE(
		        2
		);
		
		private final int value;
		
		direction(
		        int value
		)
		{
			this.value = value;
		}
		
		public int Getdirection(
		)
		{
			return value;
		}
	}
	
	public enum alertType {
		TREND(
		        0
		), PENULTIMATE(
		        1
		), GENERAL(
		        2
		);
		
		private final int value;
		
		alertType(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetalertType(
		)
		{
			return value;
		}
	}
	
	public enum alertMode {
		FAVOR(
		        0
		), AGAINST(
		        1
		);
		
		private final int value;
		
		alertMode(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetalertMode(
		)
		{
			return value;
		}
	}
	
	@SuppressWarnings(
	    "unchecked"
	)
	public enum txnType {
		BUY(
		        0
		), SELL(
		        1
		);
		
		private final int value;
		
		@SuppressWarnings(
		    "rawtypes"
		)
		private static Map map = new HashMap<>();
		
		private txnType(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GettxnType(
		)
		{
			return value;
		}
		
	}
	
	public enum scripSellMode {
		PandL(
		        0
		), FreeShares(
		        1
		), Hybrid(
		        2
		);
		
		private final int value;
		
		scripSellMode(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetscripSellMode(
		)
		{
			return value;
		}
	}
	
	@SuppressWarnings(
	    "unchecked"
	)
	public enum taxType {
		stockTrade(
		        0
		), FDInterest(
		        1
		), RDInterest(
		        2
		), bankACInterest(
		        3
		), others(
		        4
		);
		
		private final int value;
		
		@SuppressWarnings(
		    "rawtypes"
		)
		private static Map map = new HashMap<>();
		
		private taxType(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GettaxType(
		)
		{
			return value;
		}
		
		private int getValue(
		)
		{
			return value;
		}
		
	}
	
	// Enumeration for rowScanType
	
	public enum rowScanType {
		Continous(
		        0
		), Specific(
		        1
		);
		
		private final int value;
		
		rowScanType(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetrowScanType(
		)
		{
			return value;
		}
	}
	
	// Enumeration for rowDataTye
	
	public enum rowDataType {
		String(
		        0
		), Decimal(
		        1
		), Numerical(
		        2
		), Date(
		        3
		);
		
		private final int value;
		
		rowDataType(
		        int value
		)
		{
			this.value = value;
		}
		
		public int GetrowDataType(
		)
		{
			return value;
		}
	}
	
}
