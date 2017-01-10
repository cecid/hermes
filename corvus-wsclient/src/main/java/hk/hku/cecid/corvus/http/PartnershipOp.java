package hk.hku.cecid.corvus.http;

/** 
 * The <code>PartnershipOperation</code> is the signature interface for providing a clue 
 * that the implemented class should able to handle add/delete/update operation one or 
 * more kind of partnerships.
 * 
 * @author 	Twinsen Tsang
 * @version 1.0.0
 * @since   JDK5.0, H2O 0908
 */
public interface PartnershipOp 
{
	/** The constant field indicate the adding partnership operation **/
	int ADD 	= 0;
	/** The constant field indicate the deleting partnership operation **/
	int DELETE 	= 1;
	/** The constant field indicate the update partnership operation **/
	int UPDATE 	= 2;
	/** The constant field indicate the total number of partnership operation enumeration. DO NOT USE. **/
	int OP_LEN	= 3;
	
	/**
	 * The interface contract indicate the partnership operation you want to execute to the 
	 * realizing class. The <code>op</code> should be greater than zero and less than {@value #OP_LEN}.
	 * 
	 * @param op The partnership operation type.
	 */
	void setExecuteOperation(int op);
	
	/**
	 * @return Get the partnership operation type for execution.
	 */
	int getExecuteOperation();
}
