/**
 * 
 */
package jabara.bean;

import jabara.bean.annotation.Hidden;
import jabara.bean.annotation.Localized;
import jabara.bean.annotation.Order;
import jabara.general.ArgUtil;
import jabara.general.NotFound;

import java.beans.PropertyDescriptor;
import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author jabaraster
 */
public class BeanProperty implements Serializable {
    private static final long serialVersionUID    = -5577222431246281031L;

    private static final int  DEFAULT_ORDER_INDEX = Integer.MAX_VALUE;

    private final Class<?>    beanType;
    private final boolean     readOnly;
    private final String      name;
    private final String      localizedName;
    private final Class<?>    type;
    private final int         orderIndex;
    private final boolean     hidden;

    /**
     * @param pBeanType
     * @param pProperty
     */
    BeanProperty(final Class<?> pBeanType, final PropertyDescriptor pProperty) {
        ArgUtil.checkNull(pBeanType, "pBeanType"); //$NON-NLS-1$
        ArgUtil.checkNull(pProperty, "pProperty"); //$NON-NLS-1$

        final Method getter = pProperty.getReadMethod();
        final Method setter = pProperty.getWriteMethod();

        this.beanType = pBeanType;
        this.readOnly = getter != null && setter == null;
        this.name = pProperty.getName();
        this.localizedName = getLocalizedNameS(pBeanType, getter, setter, this.name);
        this.orderIndex = getOrderIndexS(getter, setter);
        this.type = pProperty.getPropertyType();
        this.hidden = getHiddenS(getter, setter);
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
        final BeanProperty other = (BeanProperty) obj;
        if (this.hidden != other.hidden) {
            return false;
        }
        if (this.localizedName == null) {
            if (other.localizedName != null) {
                return false;
            }
        } else if (!this.localizedName.equals(other.localizedName)) {
            return false;
        }
        if (this.name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!this.name.equals(other.name)) {
            return false;
        }
        if (this.orderIndex != other.orderIndex) {
            return false;
        }
        if (this.readOnly != other.readOnly) {
            return false;
        }
        if (this.type == null) {
            if (other.type != null) {
                return false;
            }
        } else if (!this.type.equals(other.type)) {
            return false;
        }
        return true;
    }

    /**
     * @return このプロパティを持っているクラス.
     */
    public Class<?> getBeanType() {
        return this.beanType;
    }

    /**
     * @return 言語環境に即した名称.
     */
    public String getLocalizedName() {
        return this.localizedName;
    }

    /**
     * @return プロパティ名.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @return -
     */
    public int getOrderIndex() {
        return this.orderIndex;
    }

    /**
     * @return プロパティの値の型.
     */
    public Class<?> getType() {
        return this.type;
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (this.hidden ? 1231 : 1237);
        result = prime * result + (this.localizedName == null ? 0 : this.localizedName.hashCode());
        result = prime * result + (this.name == null ? 0 : this.name.hashCode());
        result = prime * result + this.orderIndex;
        result = prime * result + (this.readOnly ? 1231 : 1237);
        result = prime * result + (this.type == null ? 0 : this.type.hashCode());
        return result;
    }

    /**
     * @return {@link Hidden}が付与されていればtrue.
     */
    public boolean isHidden() {
        return this.hidden;
    }

    /**
     * @return getterのみのプロパティの場合true.
     */
    public boolean isReadOnly() {
        return this.readOnly;
    }

    /**
     * @see java.lang.Object#toString()
     */
    @SuppressWarnings("nls")
    @Override
    public String toString() {
        return "BeanProperty [readOnly=" + this.readOnly + ", name=" + this.name + ", localizedName=" + this.localizedName + ", type=" + this.type
                + ", orderIndex=" + this.orderIndex + ", hidden=" + this.hidden + "]";
    }

    private static boolean getHiddenS(final Method pGetter, final Method pSetter) {
        try {
            getMethodsAnnotation(Hidden.class, pGetter, pSetter);
            return true;
        } catch (final NotFound e) {
            return false;
        }
    }

    private static String getLocalizedNameS( //
            final Class<?> pBeanType //
            , final Method pGetter //
            , final Method pSetter //
            , final String pPropertyName) {
        try {
            final Localized ann = getMethodsAnnotation(Localized.class, pGetter, pSetter);
            try {
                return getNameFromResource(pBeanType, pPropertyName);

            } catch (final NotFound e) {
                final String value = ann.value();
                if (value.length() == 0) {
                    return pPropertyName;
                }
                return value;
            }
        } catch (final NotFound e) {
            return pPropertyName;
        }
    }

    private static <A extends Annotation> A getMethodsAnnotation(final Class<A> pType, final Method... pMethods) throws NotFound {
        for (final Method method : pMethods) {
            if (method == null) {
                continue;
            }
            final A ann = method.getAnnotation(pType);
            if (ann != null) {
                return ann;
            }
        }
        throw NotFound.GLOBAL;
    }

    private static String getNameFromResource(final Class<?> pBeanType, final String pKey) throws NotFound {
        try {
            final ResourceBundle resources = ResourceBundle.getBundle(pBeanType.getName());
            return resources.getString(pKey);
        } catch (final MissingResourceException e) {
            throw NotFound.GLOBAL;
        }
    }

    private static int getOrderIndexS(final Method pGetter, final Method pSetter) {
        try {
            final Order order = getMethodsAnnotation(Order.class, pGetter, pSetter);
            return order.value();
        } catch (final NotFound e) {
            return DEFAULT_ORDER_INDEX;
        }
    }
}
