/**
 * 
 */
package stocktales.exceptions;

import stocktales.interfaces.IPropertyAwareMessage;

/**
 * ERR_DUPLANNOT= Annotation - {0} must only be implmented once for Object Class - {1} Annotation specified Twice in a
 * Class while it ought to be only once
 */
@SuppressWarnings("serial")
public class EX_DuplicateAnnotationException extends Exception implements IPropertyAwareMessage
{
	private String Property_ID;
	
	private Object[] Args;
	
	public void setProperty_ID(
	        String propertyID
	)
	{
		this.Property_ID = propertyID;
	}
	
	@Override
	public String getProperty_ID(
	)
	{
		// TODO Auto-generated method stub
		return Property_ID;
	}
	
	@Override
	public Object[] getArguments(
	)
	{
		// TODO Auto-generated method stub
		return Args;
	}
	
	@Override
	public boolean IS_Exception(
	)
	{
		// TODO Auto-generated method stub
		return true;
	}
	
	public EX_DuplicateAnnotationException(
	        Object[] args
	)
	{
		this.Args = args;
		this.setProperty_ID("ERR_DUPLANNOT");
		
	}
}
