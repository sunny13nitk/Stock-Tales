package stocktales.healthcheck.model.helperpojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/*
 * Combo Structure that holds
 * - Config Evaluation REsult for a Particular Config at Config Level - Post Process
 * - Bean processing REsult - AS per specified Bean in Config
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class HCComboResult
{
	private HCEvalResult evalResult;
	private HCBeanReturn beanProcResult;
}
