/**
 * 
 */
package jabara.bean;

import jabara.general.ArgUtil;
import jabara.general.ExceptionUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author jabaraster
 */
public class BeanProperties implements Iterable<BeanProperty> {

    @SuppressWarnings("synthetic-access")
    private static final Comparator<BeanProperty> ORDER_COMPARATOR = new OrderComparator();

    private final List<BeanProperty>              properties       = new ArrayList<BeanProperty>();
    private final Map<String, BeanProperty>       name2Property    = new HashMap<String, BeanProperty>();

    /**
     * @param pBeanType
     */
    public BeanProperties(final Class<?> pBeanType) {
        ArgUtil.checkNull(pBeanType, "pBeanType"); //$NON-NLS-1$
        try {
            for (final PropertyDescriptor property : Introspector.getBeanInfo(pBeanType).getPropertyDescriptors()) {
                if ("class".equals(property.getName())) { //$NON-NLS-1$
                    continue;
                }
                final BeanProperty p = new BeanProperty(pBeanType, property);
                this.properties.add(p);
                this.name2Property.put(property.getName(), p);
            }

            Collections.sort(this.properties, ORDER_COMPARATOR);

        } catch (final IntrospectionException e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    /**
     * @param pPropertyName
     * @return 指定のプロパティが存在するならtrue.
     */
    public boolean contains(final String pPropertyName) {
        ArgUtil.checkNullOrEmpty(pPropertyName, "pPropertyName"); //$NON-NLS-1$
        return this.name2Property.containsKey(normalize(pPropertyName));
    }

    /**
     * @param pIndex
     * @return -
     */
    public BeanProperty get(final int pIndex) {
        return this.properties.get(pIndex);
    }

    /**
     * @param pPropertyName
     * @return -
     */
    public BeanProperty get(final String pPropertyName) {
        ArgUtil.checkNullOrEmpty(pPropertyName, "pPropertyName"); //$NON-NLS-1$

        final String propertyName = normalize(pPropertyName);
        final BeanProperty ret = this.name2Property.get(propertyName);
        if (ret == null) {
            throw new IllegalArgumentException("no property for '" + propertyName + "' found."); //$NON-NLS-1$//$NON-NLS-2$
        }
        return ret;
    }

    /**
     * @see java.lang.Iterable#iterator()
     */
    @Override
    public Iterator<BeanProperty> iterator() {
        return this.properties.iterator();
    }

    /**
     * @return -
     */
    public int size() {
        return this.properties.size();
    }

    /**
     * @param pBeanType
     * @return -
     */
    public static BeanProperties getInstance(final Class<?> pBeanType) {
        ArgUtil.checkNull(pBeanType, "pBeanType"); //$NON-NLS-1$
        return new BeanProperties(pBeanType);
    }

    private static String normalize(final String pPropertyName) {
        if (Character.isUpperCase(pPropertyName.charAt(0))) {
            return Character.toLowerCase(pPropertyName.charAt(0)) + pPropertyName.substring(1);
        }
        return pPropertyName;
    }

    private static class OrderComparator implements Comparator<BeanProperty> {

        @Override
        public int compare(final BeanProperty p0, final BeanProperty p1) {
            return p0.getOrderIndex() - p1.getOrderIndex();
        }

    }

}
