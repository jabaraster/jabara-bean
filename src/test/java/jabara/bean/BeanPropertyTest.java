/**
 * 
 */
package jabara.bean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jabara.bean.annotation.Localized;

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

        public String getNotAnnotated() {
            return null;
        }

        public String getReadWrite() {
            return null;
        }

        public void setReadWrite(final String s) {
            System.out.println(s);
        }
    }

}