package stocktales.scripsEngine.utilities.interfaces;

public interface IPropertyAwareMessage
	{
		public void setProperty_ID(String propertyID);
		
		public String getProperty_ID();
		
		public Object[] getArguments();
		
		public boolean IS_Exception();
		
	}
