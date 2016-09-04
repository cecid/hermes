package hk.hku.cecid.edi.sfrm.com;

/**
 * The payload status interface.<br><br>
 * 
 * Creation Date: 13/11/2006
 * 
 * @author Twinsen
 * @version 1.0.0
 * @since	1.0.2
 */
public interface PayloadsState {

	/*
	 * The constant fields for the payloads state. 
	 */
	public int PLS_UPLOADING  = 0;
	public int PLS_PROCESSING = 1;
	public int PLS_PROCESSED  = 2;
	public int PLS_PENDING	  = 3;
	public int PLS_FAILED	  = 4;
}
