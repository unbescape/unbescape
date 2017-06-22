/*
 * =============================================================================
 * 
 *   Copyright (c) 2014-2017, The UNBESCAPE team (http://www.unbescape.org)
 * 
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 * 
 *       http://www.apache.org/licenses/LICENSE-2.0
 * 
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 * 
 * =============================================================================
 */
package org.unbescape;


import java.io.IOException;
import java.io.InputStream;

/**
 * <p>
 *   Utility class for obtaining a correct classloader on which to operate from a
 *   specific class.
 * </p>
 * 
 * @author Daniel Fern&aacute;ndez
 * 
 * @since 1.1.6
 *
 */
final class ClassLoaderUtils {


    private static final ClassLoader classClassLoader;
    private static final ClassLoader systemClassLoader;
    private static final boolean systemClassLoaderAccessibleFromClassClassLoader;


    static {
        classClassLoader = getClassClassLoader(ClassLoaderUtils.class);
        systemClassLoader = getSystemClassLoader();
        systemClassLoaderAccessibleFromClassClassLoader = isKnownClassLoaderAccessibleFrom(systemClassLoader, classClassLoader);
    }




    /**
     * <p>
     *   Obtain a resource by name, throwing an exception if it is not present.
     * </p>
     * <p>
     *   First the <em>context class loader</em> will be used. If this class loader is not
     *   able to locate the resource, then the <em>class class loader</em>
     *   (<tt>ClassLoaderUtils.class.getClassLoader()</tt>) will be used if it is different from
     *   the thread context one. Last, the System class loader will be tried.
     * </p>
     * <p>
     *   This method does never return <tt>null</tt>.
     * </p>
     *
     * @param resourceName the name of the resource to be obtained.
     * @return an input stream on the resource (null never returned).
     * @throws IOException if the resource could not be located.
     *
     */
    static InputStream loadResourceAsStream(final String resourceName) throws IOException {

        final InputStream inputStream = findResourceAsStream(resourceName);
        if (inputStream != null) {
            return inputStream;
        }

        // No way to obtain that resource, so we must raise an IOException
        throw new IOException("Could not locate resource '" + resourceName + "' in the application's class path");

    }


    /**
     * <p>
     *   Try to obtain a resource by name, returning <tt>null</tt> if it could not be located.
     * </p>
     * <p>
     *   This method works very similarly to {@link #loadResourceAsStream(String)} but will just return <tt>null</tt>
     *   if the resource cannot be located by the sequence of class loaders being tried.
     * </p>
     *
     * @param resourceName the name of the resource to be obtained.
     * @return an input stream on the resource, or <tt>null</tt> if it could not be located.
     *
     */
    static InputStream findResourceAsStream(final String resourceName) {

        // First try the context class loader
        final ClassLoader contextClassLoader = getThreadContextClassLoader();
        if (contextClassLoader != null) {
            final InputStream inputStream = contextClassLoader.getResourceAsStream(resourceName);
            if (inputStream != null) {
                return inputStream;
            }
            // Pass-through, there might be other ways of obtaining it
            // note anyway that this is not really normal: the context class loader should be
            // either able to resolve any of our application's resources, or to delegate to a class
            // loader that can do that.
        }

        // The thread context class loader might have already delegated to both the class
        // and system class loaders, in which case it makes little sense to query them too.
        if (!isKnownLeafClassLoader(contextClassLoader)) {

            // The context class loader didn't help, so... maybe the class one?
            if (classClassLoader != null && classClassLoader != contextClassLoader) {
                final InputStream inputStream = classClassLoader.getResourceAsStream(resourceName);
                if (inputStream != null) {
                    return inputStream;
                }
                // Pass-through, maybe the system class loader can do it? - though it would be *really* weird...
            }

            if (!systemClassLoaderAccessibleFromClassClassLoader) {

                // The only class loader we can rely on for not being null is the system one
                if (systemClassLoader != null && systemClassLoader != contextClassLoader && systemClassLoader != classClassLoader) {
                    final InputStream inputStream = systemClassLoader.getResourceAsStream(resourceName);
                    if (inputStream != null) {
                        return inputStream;
                    }
                    // Pass-through, anyway we have a return null after this...
                }

            }

        }

        return null;

    }




    /*
     * This will return the thread context class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getThreadContextClassLoader() {
        try {
            return Thread.currentThread().getContextClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This will return the class class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getClassClassLoader(final Class<?> clazz) {
        try {
            return clazz.getClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This will return the system class loader if it is possible to access it
     * (depending on security restrictions)
     */
    private static ClassLoader getSystemClassLoader() {
        try {
            return ClassLoader.getSystemClassLoader();
        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return null;
        }
    }


    /*
     * This method determines whether it is known that a this class loader is a a child of another one, or equal to it.
     * The "known" part is because SecurityManager could be preventing us from knowing such information.
     */
    private static boolean isKnownClassLoaderAccessibleFrom(final ClassLoader accessibleCL, final ClassLoader fromCL) {

        if (fromCL == null) {
            return false;
        }

        ClassLoader parent = fromCL;

        try {

            while (parent != null && parent != accessibleCL) {
                parent = parent.getParent();
            }

            return (parent != null && parent == accessibleCL);

        } catch (final SecurityException se) {
            // The SecurityManager prevents us from accessing, so just ignore it
            return false;
        }

    }


    /*
     * This method determines whether it is known that a this class loader is a "leaf", in the sense that
     * going up through its hierarchy we are able to find both the class class loader and the system class
     * loader. This is used for determining whether we should be confident on the thread-context class loader
     * delegation mechanism or rather try to perform class/resource resolution manually on the other class loaders.
     */
    private static boolean isKnownLeafClassLoader(final ClassLoader classLoader) {

        if (classLoader == null) {
            return false;
        }

        if (!isKnownClassLoaderAccessibleFrom(classClassLoader, classLoader)) {
            // We cannot access the class class loader from the specified class loader, so this is not a leaf
            return false;
        }

        // Now we know there is a way to reach the class class loader from the argument class loader, so we should
        // base or results on whether there is a way to reach the system class loader from the class class loader.
        return systemClassLoaderAccessibleFromClassClassLoader;

    }



    
    private ClassLoaderUtils() {
        super();
    }
    
    
    
}
