package stocktales.scripsEngine.utilities.types;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Objects;

/*
 * Class to have Complete Object Details for an Object Instance In terms of It's Getters/Setters/Properties
  */
public class Object_Info
{
	
	/********************************************** Attributes ****************************************/
	/**
	 * Object Name
	 */
	private String Object_Name;
	
	/**
	 * Current Object Class
	 */
	@SuppressWarnings(
	    "rawtypes"
	)
	private Class Curr_Obj_Class;
	
	/**
	 * List of Getters
	 */
	private ArrayList<Method> Getters;
	
	/**
	 * List of Setters
	 */
	private ArrayList<Method> Setters;
	
	// Methods List
	private ArrayList<Method> Methods;
	
	/**
	 * List of Properties
	 */
	private ArrayList<String> Properties;
	
	/**
	 * Queries
	 */
	
	/**********************************************
	 * Getters and Setters
	 *********************************************/
	public ArrayList<Method> getGetters(
	)
	{
		return Getters;
	}
	
	public ArrayList<Method> getSetters(
	)
	{
		return Setters;
	}
	
	public ArrayList<String> getProperties(
	)
	{
		return Properties;
	}
	
	public void setProperties(
	        ArrayList<String> properties
	)
	{
		Properties = properties;
	}
	
	@SuppressWarnings(
	    "rawtypes"
	)
	public Class getCurr_Obj_Class(
	)
	{
		return Curr_Obj_Class;
	}
	
	@SuppressWarnings(
	    "rawtypes"
	)
	public void setCurr_Obj_Class(
	        Class curr_Obj_Class
	)
	{
		Curr_Obj_Class = curr_Obj_Class;
	}
	
	public String getObject_Name(
	)
	{
		return Object_Name;
	}
	
	/************************************** Constructors ***********************************************/
	// Blank constructor
	public Object_Info(
	)
	{
		this.Getters    = new ArrayList<Method>();
		this.Setters    = new ArrayList<Method>();
		this.Properties = new ArrayList<String>();
	}
	
	// Constructor with Object
	public Object_Info(
	        Object Obj
	)
	{
		if (Obj != null)
		{
			
			{
				@SuppressWarnings(
				    "rawtypes"
				)
				Class myObjClass = Obj.getClass();
				this.Object_Name = myObjClass.getSimpleName();
				this.setCurr_Obj_Class(myObjClass);
				
				this.Getters    = new ArrayList<Method>();
				this.Setters    = new ArrayList<Method>();
				this.Properties = new ArrayList<String>();
				
				// Initialize the Getters and Setters
				
				Initialize_Getters_Setters();
			}
			
		}
		
	}
	
	public Object_Info(
	        String ClassName
	) throws Exception
	{
		
		if (ClassName != null)
		{
			
			{
				
				@SuppressWarnings(
				    "rawtypes"
				)
				Class myObjClass = Class.forName(ClassName);
				this.Object_Name = myObjClass.getSimpleName();
				this.setCurr_Obj_Class(myObjClass);
				
				this.Getters    = new ArrayList<Method>();
				this.Setters    = new ArrayList<Method>();
				this.Properties = new ArrayList<String>();
				
				Initialize_Getters_Setters();
				
			}
		}
		
	}
	
	// Get Getter for Object Property by Name
	public Method Get_Getter_for_FieldName(
	        String fname
	)
	{
		Method method = null;
		
		String Getter_Name = ("get" + fname);
		
		if (this.Getters.size() > 0)
		
		{
			for (Method method1 : Getters)
			{
				if (Objects.equals(method1.getName(), Getter_Name))
				{
					method = method1;
					return method;
				}
			}
		}
		
		else
		{
			// Scan methods using Reflection only if Not found in
			// Compiled List
			Method[] Methods = Curr_Obj_Class.getMethods();
			for (Method method1 : Methods)
			{
				if (Objects.equals(method1.getName(), Getter_Name))
				{
					
					// Method Found
					if (!Void.class.equals(method1.getReturnType()))
					{
						// Return type of method cannot
						// be void
						// since getter is supposed to
						// always
						// return something
						if (method1.getParameterTypes().length == 0)
						{
							method = method1;
							this.Getters.add(method1);
							return method;
						}
					}
				}
			}
			
		}
		
		return method;
	}
	
	// Setter for Object Property by Field Name
	public Method Get_Setter_for_FieldName(
	        String fname
		)
	{
		Method method      = null;
		String Setter_Name = ("set" + fname);
		
		if (this.Setters.size() > 0)
		
		{
			for (Method method1 : Setters)
			{
				if (Objects.equals(method1.getName(), Setter_Name))
				{
					method = method1;
					return method;
				}
			}
		}
		
		else
		{
			// Scan methods using Reflection only if Not found in
			// Compiled List
			Method[] Methods = Curr_Obj_Class.getMethods();
			for (Method method1 : Methods)
			{
				
				// Method Found
				if (Objects.equals(method1.getName(), Setter_Name))
				{
					// A Setter should always take one
					// parameter of the value to be set
					if (method1.getParameterTypes().length == 1)
					{
						method = method1;
						this.Setters.add(method1);
						return method;
					}
				}
			}
			
		}
		
		return method;
	}
	
	// Initialize Getters and Setters - Call in Constructor
	private void Initialize_Getters_Setters(
		)
	{
		this.Getters.clear();
		this.Setters.clear();
		this.Properties.clear();
		
		Method[] Methods = Curr_Obj_Class.getMethods();
		this.Methods = new ArrayList<Method>();
		for (Method method : Methods)
		{
			this.Methods.add(method);
			// Setters
			// Name Starts with set
			if (method.getName().startsWith("set"))
			{
				// Exactly one parameter to be passed to set the
				// member value
				if (method.getParameterTypes().length == 1)
				{
					this.Setters.add(method);
					String prop_name = method.getName().substring(3);
					this.Properties.add(prop_name);
				}
			}
			
			// Getters
			// Name Starts with get or is in case of Boolean suto generated Getter
			if (method.getName().startsWith("get") || method.getName().startsWith("is"))
			{
				// No Parameter passed as intent of getter is to
				// get the value back
				if (method.getParameterTypes().length == 0)
				{
					// Return type in this case should not
					// be void
					if (!void.class.equals(method.getReturnType()))
					{
						this.Getters.add(method);
					}
				}
			}
		}
	}
	
	public Method getMethodbyName(
	        String methodName
		)
	{
		Method retM = null;
		if (methodName != null && this.Methods != null)
		{
			if (Methods.size() > 0)
			{
				retM = this.Methods.stream().filter(x -> x.getName().equals(methodName)).findFirst().get();
			}
		}
		
		return retM;
	}
	
}
