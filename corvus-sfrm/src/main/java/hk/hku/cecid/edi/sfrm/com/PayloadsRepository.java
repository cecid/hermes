package hk.hku.cecid.edi.sfrm.com;

import java.io.File;
import java.util.Collection;
import java.util.Properties;

import hk.hku.cecid.piazza.commons.io.FileSystem;
import hk.hku.cecid.piazza.commons.module.SystemComponent;
/**
 * A Generic repository for collect a set of payloads that 
 * satisfies some criteria.<br><br>
 * 
* <strong>SPA Component Guideline:</strong><br>
 * <ol>
 * 	<li>Add a new parameter with the name is "location".</li>
 *  <li>The value of newly parameter should be the 
 *      absolute repository path.</li>  
 * </ol>
 * 
 * <strong>Example</strong><br>
 * 
 * If the location is C:\corvus\repository\test-repository,
 * <PRE>
 * &lt;component id="test" name="Test Repository"&gt;
 * 	&lt;class&gt;hk.hku.cecid.edi.sfrm.com.XXXXRepository&lt;/class&gt;
 * 	&lt;parameter name="location" value="C:\corvus\repository\test-repository"/&gt;
 * &lt;/component&gt;
 * </PRE> * 
 * Creation Date: 5/10/2006
 * 
 * @author Twinsen
 * @version 1.0.2
 * @since	1.0.0
 */
//public abstract class PayloadsRepository extends Component {
public abstract class PayloadsRepository extends SystemComponent {

	/**
	 * The root of the repository.
	 */
    private File repository;
    
    /**
     * The file system of the repository.
     */
    private FileSystem repositoryFs;

    /** 
     * Constructor.
     */
    public PayloadsRepository() {
        super();
    }
    
    public PayloadsRepository(String repoPath) throws Exception{
    	super();
    	super.init();
    	initRepository(repoPath);
    }
    
    /**
     * Component Initialization.
     * 
     * @throws Exception
     */
    protected void init() throws Exception {
        super.init();
        Properties params = getParameters();
        String location = params.getProperty("location");
        initRepository(location);
    }

    /**
     * Initialize the repository.<br><br>
     * 
     * Create the repository if it does not exist.<br> 
     * @param repository
     */
    protected void initRepository(String repository) {
        initRepository(
                repository==null? new File(System.getProperty("user.dir"), "payload-repository"):
                                  new File(repository)
        );
    }
    
    /**
     * Initialize the repository.<br><br>
     * 
     * Create the repository if it does not exist.<br>
     * 
     * @param repository
     */
    protected void initRepository(File repository) {
        if (!repository.exists())
        	repository.mkdirs();
        this.repository 	= repository;
        this.repositoryFs	= new FileSystem(this.repository);
    }
    
    /**
     * Get the repository.
     * 
     * @return Return the repository.
     */
    public File getRepository() {
        return this.repository;
    }
    
    /**
     * Get the repository system
     * 
     * @return Return the repository.
     */
    public FileSystem getRepositorySystem(){
    	return this.repositoryFs;
    }
    
    /**
     * @return get the repository absolute path.
     */
    public String getRepositoryPath(){
   		return this.repository.getAbsolutePath();
   	}
    
    /**
     * Get a particular payload in the payload repository.
     * 
     * @param name
     * 			The physical file name of the payload.
     * 			
     * @return the payload with the specified name or null
     * 		   if it does not exist.
     */ 
    public NamedPayloads getPayload(String name){
    	File target = new File(this.repository.getAbsolutePath(), name);
    	if (!target.exists())
    		return null;
    	return this.createPayloadsProxy(target);
    }
    
    /**
     * Get a particular payload in the payload repository 
     * by the specified parameters.
     *   
     * @param params
     * 			An array object parameters set for creating the 
     * 			payload.      		
     * @param state
     * 			The current state of that payload.	
     * @return the payload with the specified params or null
     * 		   if it does not exist.
     */
    public abstract NamedPayloads getPayload(Object[] params
    		, int state);
            
    /**
     * @return Get the list of pending payloads in the payload repository. 
     */
    public abstract Collection getPayloads();
    
    /**
     * @return Get the list of processing payloads in the payload repositoy;
     */
    public abstract Collection getProcessingPayloads();
             
    /**
     * Create a customizing payloads for the specified 
     * parameter.
     * 
     * @param params
     * 			An array object parameters set for creating the 
     * 			payload.
     * @param initialState
     * 			The initial state of the payloads, 
     * 			see {@link PayloadsState} for details 
     * @throws Exception 	
     * 			Any kind of exception.
     */
    public abstract NamedPayloads createPayloads(Object[] params,
			int initialState) throws Exception;    
    
    /**
     * Create a customizing payloads for this repository.
     * 
     * @param proxyObj
     * 			The file object for the payloads.
     *  
     * @return a customizing payloads.
     */
    protected abstract NamedPayloads createPayloadsProxy(File proxyObj); 
    
    /**
     * toString method.
     */
    public String 
    toString()
    {
    	StringBuffer ret = new StringBuffer();    	
    	ret	.append("\n")
			.append(this.getClass().getName() + "\n")
			.append("Location : " + this.getRepositoryPath());
		return ret.toString();
    }
}
