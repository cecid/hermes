/* 
 * Copyright(c) 2005 Center for E-Commerce Infrastructure Development, The
 * University of Hong Kong (HKU). All Rights Reserved.
 *
 * This software is licensed under the GNU GENERAL PUBLIC LICENSE Version 2.0 [1]
 * 
 * [1] http://www.gnu.org/licenses/old-licenses/gpl-2.0.txt
 */

package hk.hku.cecid.piazza.commons.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Instance represents an object instance. It wraps the actual object instance
 * and provides methods for invoking the object's methods.
 * 
 * @author Hugo Y. K. Lam
 *  
 */
public final class Instance {

    private Class  instanceClass;

    private Object instance;

    /**
     * Creates a new instance of Instance.
     * 
     * @param source an object instance, its class, or its class name.
     * @throws InstanceException if errors occurred in the creation of the
     *             instance.
     */
    public Instance(Object source) throws InstanceException {
        this(source, null);
    }

    /**
     * Creates a new instance of Instance.
     * 
     * @param source an object instance, its class, or its class name.
     * @param loader the class loader for loading the instance's class.
     * @throws InstanceException if errors occurred in the creation of the
     *             instance.
     */
    public Instance(Object source, ClassLoader loader) throws InstanceException {
        this(source, loader, null, null);
    }

    /**
     * Creates a new instance of Instance.
     * 
     * @param source an object instance, its class, or its class name.
     * @param initargTypes the constructor's parameter types.
     * @param initargs the constructor's parameters.
     * @throws InstanceException if errors occurred in the creation of the
     *             instance.
     */
    public Instance(Object source, Class[] initargTypes, Object[] initargs)
            throws InstanceException {
        this(source, null, initargTypes, initargs);
    }

    /**
     * Creates a new instance of Instance.
     * 
     * @param source an object instance, its class, or its class name.
     * @param loader the class loader for loading the instance's class.
     * @param initargTypes the constructor's parameter types.
     * @param initargs the constructor's parameters.
     * @throws InstanceException if errors occurred in the creation of the
     *             instance.
     */
    public Instance(Object source, ClassLoader loader, Class[] initargTypes,
            Object[] initargs) throws InstanceException {
        if (source == null) {
            throw new InstanceException("Cannot create instance from null");
        }
        if (loader == null) {
            loader = this.getClass().getClassLoader();
        }

        try {
            if (source instanceof Class) {
                instanceClass = (Class) source;
            }
            else if (source instanceof String) {
                instanceClass = Class.forName((String) source, true, loader);
            }
            else {
                instanceClass = source.getClass();
                instance = source;
            }

            if (instance == null) {
                Constructor constructor = instanceClass
                        .getConstructor(initargTypes);
                instance = constructor.newInstance(initargs);
            }
        }
        catch (Exception e) {
            throw new InstanceException("Unable to create instance for "
                    + source, e);
        }
    }

    /**
     * Invokes a method in the object that this instance represents.
     * 
     * @param methodName the method name.
     * @return the object returned by the invoked method.
     * @throws InstanceException if the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     */
    public Object invoke(String methodName) throws InstanceException,
            InvocationTargetException {
        return invoke(methodName, null);
    }

    /**
     * Invokes a method in the object that this instance represents.
     * 
     * @param methodName the method name.
     * @param parameters the parameters.
     * @return the object returned by the invoked method.
     * @throws InstanceException if the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     */
    public Object invoke(String methodName, Object[] parameters)
            throws InstanceException, InvocationTargetException {
        return invoke(methodName, null, parameters);
    }

    /**
     * Invokes a method in the object that this instance represents.
     * 
     * @param methodName the method name.
     * @param pt the parameter types.
     * @param parameters the parameters.
     * @return the object returned by the invoked method.
     * @throws InstanceException if the method could not be invoked.
     * @throws InvocationTargetException if the invoked method has thrown an
     *             exception.
     */
    public Object invoke(String methodName, Object[] pt, Object[] parameters)
            throws InstanceException, InvocationTargetException {
        try {
            Class[] parameterTypes = ArrayUtilities
                    .toClasses(pt == null ? parameters : pt);

            Method method = instanceClass.getDeclaredMethod(methodName,
                    parameterTypes);

            return method.invoke(instance, parameters);
        }
        catch (InvocationTargetException e) {
            throw e;
        }
        catch (Exception e) {
            throw new InstanceException("Unable to invoke method", e);
        }
    }

    /**
     * Check if the specified method exists.
     * 
     * @param name the method name.
     * @param pt the parameter types.
     * @return true if the specified method exists.
     */
    public boolean isMethodExist(String name, Object[] pt) {
        try {
            Class[] parameterTypes = ArrayUtilities.toClasses(pt);
            instanceClass.getDeclaredMethod(name, parameterTypes);
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }

    /**
     * Gets the object this instance represents.
     * 
     * @return the object this instance represents.
     */
    public Object getObject() {
        return instance;
    }
}