/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.corvus.ws.data;

import java.util.Map;
import java.util.HashMap;
import java.math.BigInteger;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import hk.hku.cecid.corvus.util.DateUtil;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.UtilitiesException;

import hk.hku.cecid.piazza.commons.module.ComponentException;

/**
 * A <code>DataFactory</code> imports data from the XML property tree to create different data object used for 
 * sending web service request. 
 * <br/><br/>
 *  
 * Creation Date: 19/3/2007
 * 
 * @author Twinsen Tsang
 * @version 1.0.0
 * @since	Dwarf 10315
 * 
 * @see hk.hku.cecid.piazza.commons.util.PropertyTree
 */
// TODO: Re-factor required after the enhancement of data convertor.
public class DataFactory 
{	
	/** The constants representing the XML Separator **/
	public static final char XML_SEPARATOR = '/';
	
	/** Singleton. */
	public static final DataFactory instance = new DataFactory();	
	
	/*
	 * A set of data convertor for converting object to string.  
	 */
	private Map dataConvertors = new HashMap();
	 	
	/**
	 * Private constructor. Create an instance of <code>DataFactory</code>.
	 */
	private DataFactory()
	{				
		this.dataConvertors.put(String.class, new NoOpDataConvertor());
		this.dataConvertors.put(byte[].class, new ByteArrayDataConvertor());		
	}
	
	/**
	 * Singleton instance.
	 */
	public static DataFactory getInstance(){
		return DataFactory.instance;
	}
	
	public PermitRedownloadData createAS2PermitRedownloadDataFromXML(PropertyTree t){
		return this.createPermitRedownloadDataFromXML(t, PermitRedownloadData.PROTOCOL_AS2);
	}
	
	public PermitRedownloadData createEBMSPermitRedownloadDataFromXML(PropertyTree t){
		return this.createPermitRedownloadDataFromXML(t, PermitRedownloadData.PROTOCOL_EBMS);
	}
	
	private PermitRedownloadData createPermitRedownloadDataFromXML(PropertyTree t, String protocol){
		if (t == null)
			throw new NullPointerException("The property tree is missing.");

		PermitRedownloadData data = new PermitRedownloadData(protocol);
		
		if(protocol.equalsIgnoreCase(PermitRedownloadData.PROTOCOL_AS2)){
			final String[] prefix = {	PermitRedownloadData.AS2_CONFIG_PREFIX, PermitRedownloadData.AS2_PARAM_PREFIX};
			final String[][] keySet = { PermitRedownloadData.CONFIG_KEY_SET,PermitRedownloadData.PARAM_KEY_SET };
			this.loadKVPairDataFromXML(data, t, prefix, keySet);
		}

		if(protocol.equalsIgnoreCase(PermitRedownloadData.PROTOCOL_EBMS)){
			final String[] prefix = {	PermitRedownloadData.EBMS_CONFIG_PREFIX, PermitRedownloadData.EBMS_PARAM_PREFIX};
			final String[][] keySet = { PermitRedownloadData.CONFIG_KEY_SET,PermitRedownloadData.PARAM_KEY_SET };
			this.loadKVPairDataFromXML(data, t, prefix, keySet);
		}
		return data;
	}
	
	/**
	 * Create an instance of <code>AS2MessageData</code> from the XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return A new instance of <code>AS2MessageData</code> with data imported from the property tree. 
	 */ 	 
	public AS2MessageData
	createAS2MessageDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		AS2MessageData ret = new AS2MessageData();
		// All key prefix.
		final String[]   prefix = {	AS2MessageData.CONFIG_PREFIX, AS2MessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet = { AS2MessageData.CONFIG_KEY_SET,AS2MessageData.PARAM_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Store an instance of <code>AS2Message<code> to XML.
	 * 
	 * @param d  	The <code>AS2MessageData<code> to store.
	 * @param path  The URL specified the location for storing the data.
	 * 
	 * @throws IOException when storing the data fails.
	 */
	public void 
	storeAS2MessageDataToXML(AS2MessageData d, URL path) throws IOException
	{				
		PropertyTree marshalTree = new PropertyTree();
		// All key prefix.
		final String[]   prefix = {	AS2MessageData.CONFIG_PREFIX, AS2MessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet = { AS2MessageData.CONFIG_KEY_SET,AS2MessageData.PARAM_KEY_SET };
		// Store all key-value pair to XML.
		this.storeKVPairDataToXML(d, marshalTree, path, prefix, keySet, null);		
	}
	
	/**
	 * Create an instance of <code>EBMSMessageData</code> from the XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSMessageData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public EBMSMessageData
	createEBMSMessageDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		EBMSMessageData ret = new EBMSMessageData();		
		// All key prefix.
		final String[]   prefix = {	EBMSMessageData.CONFIG_PREFIX, EBMSMessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet = { EBMSMessageData.CONFIG_KEY_SET,EBMSMessageData.PARAM_KEY_SET };		
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Store an instance of <code>EBMSMessage<code> to XML.
	 * 
	 * @param d  	The <code>AS2MessageData<code> to store.
	 * @param path  The URL specified the location for storing the data.
	 * 
	 * @throws IOException when storing the data fails.
	 */
	public void 
	storeEBMSMessageDataToXML(EBMSMessageData d, URL path) throws IOException
	{				
		PropertyTree marshalTree = new PropertyTree();
		// All key prefix.
		final String[]   prefix = {	EBMSMessageData.CONFIG_PREFIX, EBMSMessageData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet = { EBMSMessageData.CONFIG_KEY_SET,EBMSMessageData.PARAM_KEY_SET};		
		// Store all key-value pair to XML.
		this.storeKVPairDataToXML(d, marshalTree, path, prefix, keySet, null);		
	}
	
	/**
	 * Create an instance of <code>AS2ConfigData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>AS2ConfigData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public AS2ConfigData
	createAS2ConfigDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		AS2ConfigData ret = new AS2ConfigData();
		// All key prefix.
		final String[]   prefix = {	AS2ConfigData.CONFIG_PREFIX, AS2ConfigData.PARAM_PREFIX };
		// All key set 
		final String[][] keySet = { AS2ConfigData.CONFIG_KEY_SET, AS2ConfigData.PARAM_KEY_SET};
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>EBMSConfigData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSConfigData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public EBMSConfigData
	createEBMSConfigDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		EBMSConfigData ret = new EBMSConfigData();
		// All key prefix.
		final String[]   prefix = {	EBMSConfigData.CONFIG_PREFIX, EBMSConfigData.PARAM_PREFIX };
		// All key set 
		final String[][] keySet = { EBMSConfigData.CONFIG_KEY_SET, EBMSConfigData.PARAM_KEY_SET};
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>AS2PartnershipData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>AS2PartnershipData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public AS2PartnershipData 
	createAS2PartnershipFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		AS2PartnershipData ret = new AS2PartnershipData();
		// All key prefix.
		final String[]   prefix =  { AS2PartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet =  { AS2PartnershipData.PARAM_KEY_SET };
		// All key value type set
		final Class [][] typeSet = { AS2PartnershipData.PARAM_CLASS_SET };	
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet, typeSet);
		return ret;
	}
	
	/**
	 * Store the instance of <code>AS2PartnershipData</code> to the XML
	 * specified at <code>path<code>.
	 * 
	 * @param d	The <code>AS2PartnershipData</code> you want to store.
	 * @param path  The URL specified the location for storing the data.
	 * 
	 * @throws IOException when storing the data fails.
	 */ 	 
	public void 
	storeAS2PartnershipFromXML(AS2PartnershipData d, URL path)
		throws IOException 
	{
		PropertyTree marshalTree = new PropertyTree();
		// All key prefix.
		final String[]   prefix =  { AS2PartnershipData.PARAM_PREFIX  };
		// All key set 
		final String[][] keySet =  { AS2PartnershipData.PARAM_KEY_SET };
		// All key value type set
		final Class [][] typeSet = { AS2PartnershipData.PARAM_CLASS_SET };	
		
		// Store all key-value pair to XML.
		this.storeKVPairDataToXML(d, marshalTree, path, prefix, keySet, typeSet);		
	}
	
	/**
	 * 
	 * Create an instance of <code>EBMSPartnershipData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSPartnershipData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public EBMSPartnershipData 
	createEBMSPartnershipFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		EBMSPartnershipData ret = new EBMSPartnershipData();
		// All key prefix.
		final String[]   prefix  = { EBMSPartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet  = { EBMSPartnershipData.PARAM_KEY_SET };
		// All key value type set
		final Class [][] typeSet = { EBMSPartnershipData.PARAM_CLASS_SET };		
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet, typeSet);
		return ret;
	}
	
	/**
	 * Store the instance of <code>EBMSPartnershipData</code> to the XML
	 * specified at <code>path<code>.
	 * 
	 * @param d	The <code>EBMSPartnershipData</code> you want to store.
	 * @param path  The URL specified the location for storing the data.
	 * 
	 * @throws IOException when storing the data fails.
	 */ 	 
	public void 
	storeEBMSPartnershipFromXML(EBMSPartnershipData d, URL path)
		throws IOException 
	{
		PropertyTree marshalTree = new PropertyTree();
		// All key prefix.
		final String[]   prefix  = { EBMSPartnershipData.PARAM_PREFIX};
		// All key set 
		final String[][] keySet  = { EBMSPartnershipData.PARAM_KEY_SET };
		// All key value type set
		final Class [][] typeSet = { EBMSPartnershipData.PARAM_CLASS_SET };	
		
		// Store all key-value pair to XML.
		this.storeKVPairDataToXML(d, marshalTree, path, prefix, keySet, typeSet);		
	}
	
	/**
	 * Create an instance of <code>AS2StatusQueryData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>AS2StatusQueryData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public AS2StatusQueryData
	createAS2StatusQueryDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		AS2StatusQueryData ret = new AS2StatusQueryData();
		// All key prefix
		final String[] 	 prefix = { AS2StatusQueryData.PARAM_PREFIX, AS2StatusQueryData.CONFIG_PREFIX };
		// All key set
		final String[][] keySet = { AS2StatusQueryData.PARAM_KEY_SET, AS2StatusQueryData.CONFIG_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>EBMSStatusQueryData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSStatusQueryData</code> 
	 * 			with data imported from the property tree. 
	 */ 	 
	public EBMSStatusQueryData
	createEBMSStatusQueryDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		EBMSStatusQueryData ret = new EBMSStatusQueryData();
		// All key prefix
		final String[] 	 prefix = { EBMSStatusQueryData.PARAM_PREFIX, EBMSStatusQueryData.CONFIG_PREFIX };
		// All key set
		final String[][] keySet = { EBMSStatusQueryData.PARAM_KEY_SET, EBMSStatusQueryData.CONFIG_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>AS2AdminData</code> from the XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>AS2AdminData</code> with data imported from the property tree. 
	 */ 	 
	public AS2AdminData
	createAS2AdminDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		AS2AdminData ret = new AS2AdminData();
		// All key prefix
		final String[] 	 prefix = { AS2AdminData.PARAM_PREFIX, AS2AdminData.CONFIG_PREFIX };
		// All key set
		final String[][] keySet = { AS2AdminData.PARAM_KEY_SET, AS2AdminData.CONFIG_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>EBMSAdminData</code> from the XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSAdminData</code> with data imported from the property tree. 
	 */ 	 
	public EBMSAdminData
	createEBMSAdminDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		EBMSAdminData ret = new EBMSAdminData();
		// All key prefix
		final String[] 	 prefix = { EBMSAdminData.PARAM_PREFIX, EBMSAdminData.CONFIG_PREFIX };
		// All key set
		final String[][] keySet = { EBMSAdminData.PARAM_KEY_SET, EBMSAdminData.CONFIG_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}	
	
	/**
	 * Create an instance of <code>SFRMStatusQueryData</code>  From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>SFRMStatusQueryData</code> with data imported from the property tree. 
	 */ 	 
	public SFRMStatusQueryData
	createSFRMStatusQueryDataFromXML(PropertyTree t)
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		SFRMStatusQueryData ret = new SFRMStatusQueryData();
		// All key prefix
		final String[] 	 prefix = { SFRMStatusQueryData.PARAM_PREFIX, SFRMStatusQueryData.CONFIG_PREFIX };
		// All key set
		final String[][] keySet = { SFRMStatusQueryData.PARAM_KEY_SET, SFRMStatusQueryData.CONFIG_KEY_SET };
		// Load all key-value pair from XML.
		this.loadKVPairDataFromXML(ret, t, prefix, keySet);
		return ret;
	}
	
	/**
	 * Create an instance of <code>EBMSMessageHistoryRequestData</code> from the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>EBMSMessageHistoryRequestData</code> with data imported from the property tree. 
	 */ 	 
	public EBMSMessageHistoryRequestData
	createEbmsMessageHistoryQueryDataFromXML(PropertyTree t){
		if(t == null)
			throw new NullPointerException("The property tree is missing.");
		
		EBMSMessageHistoryRequestData data = new EBMSMessageHistoryRequestData();
		final String[] 	 config_prefix = { EBMSMessageHistoryRequestData.CONFIG_PREFIX};
		
		final String[][] config_keySet = { EBMSMessageHistoryRequestData.CONFIG_KEY_SET};

		this.loadKVPairDataFromXML(data, t, config_prefix, config_keySet);
		
		
		final String[] 	 prefix = {EBMSMessageHistoryRequestData.CRITERIA_PARAM_PREFIX, 
									EBMSMessageHistoryRequestData.CRITERIA_PARAM_PREFIX};

		final String[][] keySet = { EBMSMessageHistoryRequestData.PARAM_KEY_SET,
									EBMSMessageHistoryRequestData.PARAM_EBMS_KEY_SET};
			
		this.loadKVPairDataFromXML(data, t, prefix, keySet);
		return data;
	}
	
	/**
	 * Create an instance of <code>AS2MessageHistoryRequestData</code> from the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>AS2MessageHistoryRequestData</code> with data imported from the property tree. 
	 */ 	 
	public AS2MessageHistoryRequestData
	createAs2MessageHistoryQueryDataFromXML(PropertyTree t){
		if(t == null)
			throw new NullPointerException("The property tree is missing.");
		
		AS2MessageHistoryRequestData data = new AS2MessageHistoryRequestData();
		final String[] 	 config_prefix = { AS2MessageHistoryRequestData.CONFIG_PREFIX};
		
		final String[][] config_keySet = { AS2MessageHistoryRequestData.CONFIG_KEY_SET};

		this.loadKVPairDataFromXML(data, t, config_prefix, config_keySet);
		
		
		final String[] 	 prefix = {AS2MessageHistoryRequestData.PARAM_PREFIX, 
				AS2MessageHistoryRequestData.PARAM_PREFIX};

		final String[][] keySet = { AS2MessageHistoryRequestData.PARAM_KEY_SET,
									AS2MessageHistoryRequestData.PARAM_AS2_KEY_SET};
			
		this.loadKVPairDataFromXML(data, t, prefix, keySet);
		return data;
	}
	
	/**
	 * Create an instance of <code>MessageStatusRequestData</code> From a 
	 * file written in XML format.
	 * 
	 * @param filename The file to load the message status request data.
	 * @return
	 * 			A new instance of <code>MessageStatusRequestData</code> 
	 * 			with data imported from the file with name <code>filename</code>
	 * @throws ComponentException
	 *			When unable to load the file or invalid file format.			
	 */
	public static MessageStatusRequestData
	createMessageStatusDataFromXML(String filename) throws ComponentException
	{
		if (filename == null)
			throw new NullPointerException("The filename is missing.");
		try{
			PropertyTree t = new PropertyTree(new File(filename).toURI().toURL());
			return createMessageRequestStatusDataFromXML(t);
		}catch(Exception e){
			throw new ComponentException("Unable to create property tree", e);
		}
	}
	
	/**
	 * Create an instance of <code>MessageStatusRequestData</code> From the 
	 * XML property tree.  
	 * 
	 * @param t The property tree to import the data.
	 * @return
	 * 			A new instance of <code>MessageStatusRequestData</code> 
	 * 			with data imported from the property tree. 
	 * @throws UtilitiesException
	 * 			When the data factory is unable to import the data from the property
	 * 			tree.
	 */
	public static MessageStatusRequestData 
	createMessageRequestStatusDataFromXML(PropertyTree t) throws UtilitiesException
	{
		if (t == null)
			throw new NullPointerException("The property tree is missing.");
		// Create return object.
		MessageStatusRequestData ret = new MessageStatusRequestData();
		// variable decl
		int len = MessageStatusRequestData.PARAM_KEY_SET.length; 		
		String [] valueSet = new String[len];				
		// Extract the key param set.
		for (int i = 0; i < len; i++){				
			valueSet[i] = t.getProperty(
				MessageStatusRequestData.PARAM_PREFIX + "/" 
			  + MessageStatusRequestData.PARAM_KEY_SET[i]);
		}		
		// Filling all string-typed param.
		ret.setPartnershipId (valueSet[0]);
		ret.setChannelType	 (valueSet[1]);
		ret.setChannelId	 (valueSet[2]);
		ret.setFolderName	 (valueSet[3]);
		ret.setFileName		 (valueSet[4]);
		ret.setConversationId(valueSet[8]);
		ret.setMessageId     (valueSet[9]);
		ret.setMessageType	 (valueSet[10]);
		ret.setMessageStatus (valueSet[11]);
		ret.setProtocol      (valueSet[12]);
		ret.setLocale        (valueSet[13]);
		// Filling all time-stamp.		
		if (!DataFactory.isNullOrEmpty(valueSet[5]))
			ret.setFromTimestamp(DateUtil.UTC2Calendar(valueSet[5]));
		if (!DataFactory.isNullOrEmpty(valueSet[6]))
			ret.setToTimestamp(DateUtil.UTC2Calendar(valueSet[6]));
			
		// Filling all big integer set.
		if (!DataFactory.isNullOrEmpty(valueSet[7]))
			ret.setNumOfRecords	 (new BigInteger(valueSet[7]));		
		if (!DataFactory.isNullOrEmpty(valueSet[14]))
			ret.setLevelOfDetails(new BigInteger(valueSet[14]));
		if (!DataFactory.isNullOrEmpty(valueSet[15]))	
			ret.setOffset        (new BigInteger(valueSet[15]));
		
		len = MessageStatusRequestData.CONFIG_KEY_SET.length; 		
		valueSet = new String[len];
		// Now Extract the configuration parameter set.
		for (int i = 0; i < len; i++){
			valueSet[i] = t.getProperty(
				MessageStatusRequestData.CONFIG_PREFIX + "/"
			  + MessageStatusRequestData.CONFIG_KEY_SET[i]);
		}
		// Filling
		ret.setWSEndpoint(valueSet[0]);
		ret.setUsername  (valueSet[1]);
		ret.setPassword  (valueSet[2]);
		return ret;
	}
	
	/*
	 * Load the data from XML <code>t</code> into reference <code>d</code> of KVPairData.
	 * The loading schema is based on XPath prefix set <code>prefix<code> plus the 
	 * available <code>key-set</code>.     
	 * 
	 * @param d		 The Data object to be loaded.
	 * @param t		 The XML data source to be loaded into <code>d</code>
	 * @param prefix
	 * 				 The XPath prefix for the key-set.
	 * @param keySet
	 * 				 The XML tag name available in the property tree <code>t</code>
	 */
	private void 
	loadKVPairDataFromXML(KVPairData d, PropertyTree t, String[] prefix, String[][] keySet) 
	{
		if (d == null)
			throw new NullPointerException("Data is missing.");
		int len;
		Map props = d.getProperties();
		// Iterate all key and set the properties.
		for (int i = 0; i < keySet.length; i++){
			len = keySet[i].length;
			for (int j = 0; j < len; j++){
				props.put(keySet[i][j], t.getProperty(prefix[i] + "/" + keySet[i][j]));
			}				
		}					
		// Set back all properties to message data.
		d.setProperties(props);		
	}
	
	/*
	 * Load the data from XML <code>t</code> into reference <code>d</code> of KVPairData.
	 * The loading schema is based on XPath prefix set <code>prefix<code> plus the 
	 * available <code>key-set</code>.     
	 * 
	 * @param d		 The Data object to be loaded.
	 * @param t		 The XML data source to be loaded into <code>d</code>
	 * @param prefix
	 * 				 The XPath prefix for the key-set.
	 * @param keySet
	 * 				 The XML tag name available in the property tree <code>t</code>
	 */
	private void 
	loadKVPairDataFromXML(KVPairData d, PropertyTree t, String[] prefix, String[][] keySet, Class[][] typeSet) 
	{
		// Check the validity of all data.
		DataFactory.checkValidity(d, t, prefix, keySet, typeSet);
		
		Map props = d.getProperties();
		
		int len;
		String dataValue;
		Class  dataValueClass;
		Object convertedValue;
				
		// Iterate all key and set the properties.
		for (int i = 0; i < keySet.length; i++)
		{
			len = keySet[i].length;
			for (int j = 0; j < len; j++)
			{
				dataValue = t.getProperty(prefix[i] + XML_SEPARATOR + keySet[i][j]);
				// Get the data type for this data.
				dataValueClass = this.getDataType(typeSet, i, j);	
				
				DataConvertor dc = (DataConvertor) this.dataConvertors.get(dataValueClass);				
				try
				{
					convertedValue = dc.deserialize(dataValue);
				}
				catch(Exception ex)
				{
					// TODO : do logging.
					convertedValue = dataValue;
				}										
				// Set the converted value to the map in the data.
				props.put(keySet[i][j], convertedValue);
			}				
		}					
		// Push back all properties to the kv-pair data.
		d.setProperties(props);		
	}
	
	/*
	 * Store the data from XML <code>t</code> into reference <code>d</code> of KVPairData.
	 * The loading schema is based on XPath prefix set <code>prefix<code> plus the 
	 * available <code>key-set</code>.     
	 * 
	 * @param d		 The Data object to be loaded.
	 * @param t		 The XML data source to be loaded into <code>d</code>
	 * @param path		
	 * @param prefix
	 * 				 The XPath prefix for the key-set.
	 * @param keySet
	 * 				 The XML tag name available in the property tree <code>t</code>
	 * @throws IOException
	 * 				 When unable to store the data to XML.	 
	 */
	private void
	storeKVPairDataToXML(KVPairData d, PropertyTree t, URL path, String[] prefix, String[][] keySet, Class[][] typeSet)
		throws IOException
	{
		// Check the validity of all data.
		DataFactory.checkValidity(d, t, prefix, keySet, typeSet);
		
		// Get the properties map.
		Map props = d.getProperties();
		
		int len;
		Object dataValue;
		Class  dataValueClass;
		String convertedValue;
		  		
		// Iterate all key and set the properties.
		for (int i = 0; i < keySet.length; i++)
		{
			len = keySet[i].length;
			for (int j = 0; j < len; j++)
			{				
				dataValue = (Object) props.get(keySet[i][j]);
				
				if (dataValue == null) dataValue = "";
				
				// Get the data type for this data.
				dataValueClass = this.getDataType(typeSet, i, j);				
				
				DataConvertor dc = (DataConvertor) this.dataConvertors.get(dataValueClass);				
				try
				{
					convertedValue = dc.serialize(dataValue);
				}
				catch(Exception ex)
				{
					// TODO : do logging.
					convertedValue = dataValue.toString();
				}										
				// Set the converted value to the XML tree.
				t.setProperty(prefix[i] + XML_SEPARATOR + keySet[i][j], convertedValue);				
			}
		}		
		// Now all the data from KVPairData is pushed into property tree, store the tree.
		try
		{		
			if (path == null) 
				path = t.getURL();
			
			t.store(path);					
		}
		catch(ComponentException ce)
		{
			// TODO: it the exception has enough message ?
			throw new IOException("Unable to store data to XML: " + ce.getMessage());
		}
	}
	
	/**
	 * The <code>DataConvertor</code> is a simple data conversion interface for converting data to string or vice-versa.  	
	 */
	private static interface DataConvertor 
	{		
		Class handleClass();
		
		String serialize	(Object dataToConvert) throws Exception;
		
		Object deserialize	(String convertedString) throws Exception;
	}
	
	/**
	 * The <code>ByteArrayDataConvertor</code> is a data convertor converting a byte array to string 
	 * or string back to byte array.   
	 */
	private static class ByteArrayDataConvertor implements DataConvertor 
	{
		public Class handleClass(){ 
			return byte[].class;
		}
		
		public String serialize(Object dataToConvert) throws Exception
		{
			if (dataToConvert instanceof byte[])
				return new String((byte[])dataToConvert, "UTF-8");
			return "";
		}
		
		public Object deserialize(String convertedString)
		{
			if (convertedString != null)
				return convertedString.getBytes();
			return new byte[]{};			
		}
	}
	
	/**
	 * The <code>NoOpDataConvertor</code> is a data convertor which do nothing during conversation.
	 * Just pass-in and return-out the argument.    
	 */
	private static class NoOpDataConvertor implements DataConvertor
	{
		public Class handleClass(){
			return String.class;
		}
		
		public String serialize(Object dataToConvert) throws Exception
		{
			return dataToConvert.toString();
		}
		
		public Object deserialize(String convertedString)
		{
			return convertedString;
		}
	}
	
	/*
	 * Check the validity for the input arguments.
	 * 
	 * @throws NullPointerException
	 * 			If the KVPairData d is null.
	 * 			If the PropertyTree t is null.
	 * 			If the prefix is null.
	 * 			If the keySet is null.
	 * @throws IllegalArgumentException
	 * 			If the length of prefix equal to zero.
	 * 			If the length of keySet equal to zero.
	 * 			If the length of prefix does not match with keySet.
	 */
	private static void 
	checkValidity(KVPairData d, PropertyTree t, String[] prefix, String[][] keySet, Class[][] typeSet)
	{
		if (d == null)
			throw new NullPointerException("Missing KVPairData in the arguments.");
		if (t == null)
			throw new NullPointerException("Missing PropertyTree in the arguments.");		
		if (prefix == null)
			throw new NullPointerException("Missing Prefix in the arguments.");
		if (keySet == null)
			throw new NullPointerException("Missing KeySet in the arguments.");
		/*if (typeSet == null)
			throw new NullPointerException("Missing TypeSet in the arguments.");*/		
		if (prefix.length == 0)
			throw new IllegalArgumentException("No XPath prefix found.");
		if (keySet.length == 0)
			throw new IllegalArgumentException("No XPath key found.");
		if (prefix.length != keySet.length)
			throw new IllegalArgumentException("The length of XPath prefix and key does not match:" + prefix.length + "," + keySet.length);		
	}
		
	/*
	 * Check the input var is null or empty.  
	 * 
	 * @param var The string to test.  
	 * @return true if the input is null or empty.
	 */
	private static boolean isNullOrEmpty(String var)
	{
		return (var == null || "".equals(var));
	}
	
	/*
	 * A Helper method for getting the class type from typeSet at i and j. 
	 * 
	 * It simply return typeSet[i][j] when it exist and not null. Otherwise, it return String.class.  
	 */
	private Class getDataType(Class[][] typeSet, int i, int j)
	{ 		
		if (typeSet != null && i < typeSet.length && j < typeSet[i].length)
		{
			return typeSet[i][j];    
		}
		// Default data type 
		return String.class;
	}
}
