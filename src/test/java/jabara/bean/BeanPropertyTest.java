/**
 * 
 */
package jabara.bean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jabara.bean.annotation.Hidden;
import jabara.bean.annotation.Localized;
import jabara.general.ExceptionUtil;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Test;

/**
 * @author jabaraster
 */
public class BeanPropertyTest {

    /**
     * 
     */
    @SuppressWarnings({ "static-method", "boxing" })
    @Test
    public void _getterとsetterを両方備えるプロパティ() {
        final BeanProperty sut = BeanProperties.getInstance(XTestBean.class).get("readWrite"); //$NON-NLS-1$
        assertThat(sut.isReadOnly(), is(false));
        assertThat(sut.getType().equals(String.class), is(true));
    }

    /**
     * 
     */
    @SuppressWarnings({ "boxing", "static-method" })
    @Test
    public void _isHidden() {
        final BeanProperties sut = BeanProperties.getInstance(XTestBean.class);
        assertThat(sut.get("hidden").isHidden(), is(true)); //$NON-NLS-1$
        assertThat(sut.get("readWrite").isHidden(), is(false)); //$NON-NLS-1$
    }

    /**
     * 
     */
    @SuppressWarnings("static-method")
    @Test
    public void _Localizedが付与されていない() {
        final String propertyName = "notAnnotated"; //$NON-NLS-1$
        final BeanProperty sut = BeanProperties.getInstance(XTestBean.class).get(propertyName);
        assertThat(sut.getLocalizedName(), is(propertyName));
    }

    /**
     * 
     */
    @SuppressWarnings("static-method")
    @Test
    public void _Localized名がpropertiesファイルに書かれているケース() {
        final BeanProperty sut = BeanProperties.getInstance(XTestBean.class).get("FromResource"); //$NON-NLS-1$
        assertThat(sut.getLocalizedName(), is("getterのみ")); //$NON-NLS-1$
    }

    /**
     * 
     */
    @SuppressWarnings("static-method")
    @Test
    public void _Localized名がハードコーディングされているケース() {
        final BeanProperty sut = BeanProperties.getInstance(XTestBean.class).get("HardCoding"); //$NON-NLS-1$
        assertThat(sut.getLocalizedName(), is("文字列")); //$NON-NLS-1$
    }

    /**
     * 
     */
    @SuppressWarnings({ "static-method", "boxing" })
    @Test
    public void _serializable() {
        final String propertyName = "notAnnotated"; //$NON-NLS-1$
        final BeanProperty sut = BeanProperties.getInstance(XTestBean.class).get(propertyName);
        final BeanProperty exp = serialize(sut);
        assertThat(sut.equals(exp), is(true));
    }

    /**
     * 
     */
    @SuppressWarnings({ "boxing", "static-method" })
    @Test
    public void _サブクラスでオーバーライドしたboolean型プロパティのアノテーションが有効() {
        final BeanProperty sut = BeanProperties.getInstance(XExTestBean.class).get("boolean"); //$NON-NLS-1$
        assertThat(sut.isHidden(), is(true));
    }

    private static BeanProperty serialize(final BeanProperty pProperty) {
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            final ObjectOutputStream objOut = new ObjectOutputStream(out);

            objOut.writeObject(pProperty);
            objOut.close();

            return (BeanProperty) new ObjectInputStream(new ByteArrayInputStream(out.toByteArray())).readObject();

        } catch (final Exception e) {
            throw ExceptionUtil.rethrow(e);
        }
    }

    @SuppressWarnings("javadoc")
    public static class XExTestBean extends XTestBean {
        @Override
        @Hidden
        public boolean isBoolean() {
            return super.isBoolean();
        }
    }

    @SuppressWarnings({ "static-method", "javadoc" })
    public static class XTestBean {

        @Localized
        public String getFromResource() {
            return null;
        }

        @Localized("文字列")
        public String getHardCoding() {
            return null;
        }

        public int getHidden() {
            return 0;
        }

        public String getNotAnnotated() {
            return null;
        }

        public String getReadWrite() {
            return null;
        }

        public boolean isBoolean() {
            return true;
        }

        @Hidden
        public void setHidden(final int i) {
            System.out.println(i);
        }

        public void setReadWrite(final String s) {
            System.out.println(s);
        }
    }
}
