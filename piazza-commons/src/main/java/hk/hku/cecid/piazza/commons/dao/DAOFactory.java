/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.dao;

import hk.hku.cecid.piazza.commons.module.Component;
import hk.hku.cecid.piazza.commons.util.Instance;
import hk.hku.cecid.piazza.commons.util.PropertyTree;
import hk.hku.cecid.piazza.commons.util.StringUtilities;

import java.net.URL;
import java.util.Properties;

/**
 * The DAO pattern can be made highly flexible by adopting the Abstract Factory
 * and the Factory Method Patterns.
 * <p>
 * The Factory Method pattern can be used to produce a number of DAOs needed by
 * the application when the underlying storage is not subject to change from one
 * implementation to another. When it is subject to change, the Abstract Factory
 * pattern can in turn build on and use the Factory Method Implementation. In
 * this case, it provides an abstract DAO factory object that can construct
 * various types of concrete DAO factories, each factory supporting a different
 * type of persistent storage implementation. Once you obtain the concrete DAO
 * factory for a specific implementation, you use it to produce DAOs supported
 * and implemented in that implementation.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public abstract class DAOFactory extends Component {

    private PropertyTree daoprops = new PropertyTree();

    /**
     * Creates a new instance of DAOFactory.
     */
    protected DAOFactory() {
    }

    /**
     * Initializes the DAO Factory.
     * 
     * @throws DAOException when there is any error in the initialization.
     * @see #initFactory()
     */
    protected void init() throws DAOException {
        String[] daoDescriptors = StringUtilities.tokenize(
                getParameter("config"), ",\t\r\n");

        for (int i = 0; i < daoDescriptors.length; i++) {
            String daoDescriptor = daoDescriptors[i].trim();
            if (!"".equals(daoDescriptor)) {
                URL daoResourceURL = getModule().getResource(daoDescriptor);
                if (daoResourceURL != null) {
                    try {
                        if (!daoprops.append(new PropertyTree(daoResourceURL))) {
                            throw new DAOException("Invalid DAO descriptor");
                        }
                        else {
                            getModule().getLogger().debug(
                                    "DAO descriptor '" + daoDescriptor
                                            + "' loaded successfully");
                        }
                    }
                    catch (Exception e) {
                        throw new DAOException(
                                "Unable to load dao descriptor from URL '"
                                        + daoResourceURL + "'", e);
                    }
                }
                else {
                    throw new DAOException("DAO descriptor '" + daoDescriptor
                            + "' not found");
                }
            }
        }

        initFactory();
    }

    /**
     * Creates a new DAO.
     * 
     * @param inf the DAO interface class.
     * @throws DAOException if there is any problem in looking up the DAO with
     *             the interface specified.
     * @return the DAO retrieved by the interface specified.
     */
    public DAO createDAO(Class inf) throws DAOException {
        if (inf == null) {
            throw new DAOException("DAO interface class cannot be null");
        }
        else
            return createDAO(inf.getName());
    }

    /**
     * Creates a new DAO.
     * 
     * @param daoname the DAO name.
     * @throws DAOException if there is any problem in looking up the DAO with
     *             the name specified.
     * @return the DAO retrieved by the name specified.
     * @see #initDAO(DAO)
     */
    public DAO createDAO(String daoname) throws DAOException {
        try {
            if (daoname == null) {
                throw new DAOException("DAO name cannot be null");
            }

            String propKey = "/dao-config/dao[@name='" + daoname + "']/";

            String daoImpl = daoprops.getProperty(propKey + "class");
            if (daoImpl == null) {
                throw new DAOException("No implementation specified");
            }

            Instance daoInstance = new Instance(daoImpl, getModule()
                    .getClassLoader());
            DAO dao = (DAO) daoInstance.getObject();

            Properties params = daoprops
                    .createProperties(propKey + "parameter");
            Properties daoParams = dao.getParameters();
            if (daoParams != null) {
                daoParams.putAll(params);
            }

            initDAO(dao);

            dao.daoCreated();

            return dao;
        }
        catch (Exception e) {
            throw new DAOException("Error in creating DAO for '" + daoname
                    + "'", e);
        }
    }

    /**
     * Invoked by the init() method for initializing the implementing factory.
     * 
     * @throws DAOException if unable to initialize the factory.
     * @see #init()
     */
    protected abstract void initFactory() throws DAOException;

    /**
     * Invoked by the createDAO() method for initializing the given DAO.
     * 
     * @param dao the DAO.
     * @throws DAOException if unable to initialize the DAO.
     * @see #createDAO(Class)
     * @see #createDAO(String)
     */
    protected abstract void initDAO(DAO dao) throws DAOException;

    /**
     * Creates a new transaction.
     * 
     * @return a new transaction.
     * @throws DAOException if unable to create a new transaction.
     */
    public Transaction createTransaction() throws DAOException {
        throw new DAOException("Transaction not supported");
    }
    
    /**
     * Gets a parameter from the parameters of this DAOFactory.
     * 
     * @param key the parameter key.
     * @return the parameter value associated with the specified key.
     * @throws DAOException if there is no parameter matching the specified key.
     */
    protected String getParameter(String key) throws DAOException {
        if (getParameters() == null) {
            throw new DAOException("No parameters found");
        }

        String param = getParameters().getProperty(key);

        if (param == null) {
            throw new DAOException("Parameter '" + key + "' missing");
        }
        else {
            return param;
        }
    }

    /**
     * Gets a parameter from the parameters of this DAOFactory.
     * 
     * @param key the parameter key.
     * @param def the default value.
     * @return the parameter value associated with the specified key. The
     *         default value will be returned if there is no parameter matching
     *         the specified key.
     */
    protected String getParameter(String key, String def) {
        try {
            return getParameter(key);
        }
        catch (DAOException e) {
            return def;
        }
    }

    /**
     * Creates a DAO Factory.
     * 
     * @param name the DAO Factory name.
     * @param provider the provider of the DAO Factory.
     * @param params the parameters for creating the DAO Factory.
     * @param loader the class loader for loading the factory class.
     * @throws DAOException if there is any error when creating the DAO Factory
     *             using the specified parameters.
     * @return the DAO Factory created by the parameters specified.
     */
    public static DAOFactory createDAOFactory(String name, String provider,
            Properties params, ClassLoader loader) throws DAOException {
        try {
            if (provider == null) {
                throw new DAOException("No provider specified");
            }

            Class factory = Class.forName(provider, true, loader);

            DAOFactory daoFactory = (DAOFactory) factory.newInstance();
            daoFactory.setName(name);
            daoFactory.setParameters(params);
            daoFactory.init();
            return daoFactory;
        }
        catch (Exception e) {
            throw new DAOException(
                    "Error in creating and initializing DAO Factory", e);
        }
    }
}