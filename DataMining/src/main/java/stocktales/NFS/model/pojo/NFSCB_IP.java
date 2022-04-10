package stocktales.NFS.model.pojo;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import stocktales.NFS.enums.EnumNFSTxnType;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NFSCB_IP
{
	private EnumNFSTxnType txntype;
	private double amount;
}
