package stocktales.scripsEngine.utilities.implementations;

public class ObjectModelFactory
{
	public static <T> T createObjectbyClass(
	        Class<T> cls
	) throws Exception
	{
		Object Obj = null;
		if (cls != null)
		{
			Obj = cls.newInstance();
		}
		
		return (T) Obj;
		
	}
	
}
