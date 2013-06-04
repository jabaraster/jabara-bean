/**
 * 
 */
package jabara.bean;

import jabara.general.ArgUtil;
import jabara.general.ExceptionUtil;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.io.Serializable;
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
public class BeanProperties implements Iterable<BeanProperty>, Serializable {
    private static final long                     serialVersionUID = -6400975116184225350L;

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
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BeanProperties other = (BeanProperties) obj;
        if (this.name2Property == null) {
            if (other.name2Property != null) {
                return false;
            }
        } else if (!this.name2Property.equals(other.name2Property)) {
            return false;
        }
        if (this.properties == null) {
            if (other.properties != null) {
                return false;
            }
        } else if (!this.properties.equals(other.properties)) {
            return false;
        }
        return true;
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
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.name2Property == null ? 0 : this.name2Property.hashCode());
        result = prime * result + (this.properties == null ? 0 : this.properties.hashCode());
        return result;
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
