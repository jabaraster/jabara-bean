/**
 * 
 */
package jabara.bean;

import jabara.bean.annotation.Localized;
import jabara.bean.annotation.Order;
import jabara.general.ArgUtil;
import jabara.general.NotFound;

import java.beans.PropertyDescriptor;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

/**
 * @author jabaraster
 */
public class BeanProperty {
    private static final int DEFAULT_ORDER_INDEX = Integer.MAX_VALUE;

    private final boolean    readOnly;
    private final String     name;
    private final String     localizedName;
    private final Class<?>   type;
    private final int        orderIndex;

    /**
     * @param pBeanType
     * @param pProperty
     */
    public BeanProperty(final Class<?> pBeanType, final PropertyDescriptor pProperty) {
        ArgUtil.checkNull(pBeanType, "pBeanType"); //$NON-NLS-1$
        ArgUtil.checkNull(pProperty, "pProperty"); //$NON-NLS-1$
        this.readOnly = pProperty.getReadMethod() != null && pProperty.getWriteMethod() == null;
        this.name = pProperty.getName();
        this.localizedName = getLocalizedNameS(pBeanType, pProperty.getReadMethod(), pProperty.getWriteMethod(), this.name);
        this.orderIndex = getOrderIndexS(pProperty.getReadMethod(), pProperty.getWriteMethod());
        this.type = pProperty.getPropertyType();
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
     * @return getterのみのプロパティの場合true.
     */
    public boolean isReadOnly() {
        return this.readOnly;
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
