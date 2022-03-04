package stocktales.scripCalc.intf;

import java.util.List;

import stocktales.scripCalc.pojos.TY_EntAttrValue;

public interface IFieldValueRetriever
{
	/**
	 * Get Field Value Semantics for a List /Object Instance for a Scrip RElation
	 * @param instance - List or a Individual Object Relation with Scrip e.g. Balance Sheet Entities, General Entity
	 * @param fieldNames - List of FieldNames for Which Values Need to be REtrived
	 * @param validateFields - The List of Fields Above also need to be validated for their Presence in PoJO(s)/Entities
	 * @return - List<TY_EntAttrValue> 
	 * @throws Exception 
	 */
	public List<TY_EntAttrValue> getFieldValueSemanticsforObject(
	        Object instance, List<String> fieldNames, boolean validateFields
	) throws Exception;
}
