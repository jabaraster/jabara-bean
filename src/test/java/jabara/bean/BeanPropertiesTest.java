/**
 * 
 */
package jabara.bean;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import jabara.bean.annotation.Hidden;
import jabara.bean.annotation.Localized;
import jabara.bean.annotation.Order;
import jabara.general.Empty;
import jabara.general.ExceptionUtil;
import jabara.general.NotFound;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * @author jabaraster
 */
@RunWith(Enclosed.class)
public class BeanPropertiesTest {

    /**
     * @author jabaraster
     */
    @SuppressWarnings({ "static-method" })
    public static class Other {
        /**
         * @throws NotFound
         */
        @Test
        public void _get_String() throws NotFound {
            final BeanProperties sut = new BeanProperties(XGetterOnly.class);
            assertThat(sut.get("differType").getName(), is("differType")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * @throws NotFound
         */
        @Test
        public void _get_先頭大文字のString() throws NotFound {
            final BeanProperties sut = new BeanProperties(XGetterOnly.class);
            assertThat(sut.get("DifferType").getName(), is("differType")); //$NON-NLS-1$ //$NON-NLS-2$
        }

        /**
         * 
         */
        @SuppressWarnings("nls")
        @Test
        public void _getIndex() {
            final BeanProperties sut = new BeanProperties(XGetterOnly.class);
            assertThat(sut.get(0).getName(), is("hasNoParameter"));
            assertThat(sut.get(1).getName(), is("getterOnly"));
            assertThat(sut.get(2).getName(), is("differType"));
            assertThat(sut.get(3).getName(), is("hidden"));
            assertThat(sut.get(4).getName(), is("notOrderAnnotated"));
        }

        /**
         * 
         */
        @SuppressWarnings({ "boxing" })
        @Test
        public void _GetterOnly() {
            for (final BeanProperty property : new BeanProperties(XGetterOnly.class)) {
                assertThat(property.isReadOnly(), is(true));
            }
        }

        /**
         * 
         */
        @SuppressWarnings("boxing")
        @Test
        public void _size() {
            final BeanProperties sut = new BeanProperties(XGetterOnly.class);
            assertThat(sut.size(), is(5));
        }
    }

    /**
     * @author jabaraster
     */
    public static class ParameterError {
        /**
         * 
         */
        @SuppressWarnings("static-method")
        @Test(expected = IllegalArgumentException.class)
        public void _containsの引数がnull() {
            BeanProperties.getInstance(XGetterOnly.class).contains(null);
        }

        /**
         * 
         */
        @SuppressWarnings("static-method")
        @Test(expected = IllegalArgumentException.class)
        public void _containsの引数が空文字() {
            BeanProperties.getInstance(XGetterOnly.class).contains(Empty.STRING);
        }

        /**
         * 
         */
        @SuppressWarnings("static-method")
        @Test(expected = IllegalArgumentException.class)
        public void _getの引数がnull() {
            BeanProperties.getInstance(XGetterOnly.class).get(null);
        }

        /**
         * 
         */
        @SuppressWarnings("static-method")
        @Test(expected = IllegalArgumentException.class)
        public void _getの引数が空文字() {
            BeanProperties.getInstance(XGetterOnly.class).get(Empty.STRING);
        }
    }

    /**
     * @author jabaraster
     */
    public static class Serializable_ {
        /**
         * 
         */
        @SuppressWarnings({ "static-method" })
        @Test
        public void 直列化可能であることと直列化から復元したときに情報が復元できること() {
            final BeanProperties sut = new BeanProperties(XGetterOnly.class);
            final BeanProperties s = deserialize(serialize(sut));
            assertThat(s, is(sut));
        }

        private static BeanProperties deserialize(final byte[] pData) {
            try {
                final ByteArrayInputStream in = new ByteArrayInputStream(pData);
                final ObjectInputStream objIn = new ObjectInputStream(in);
                return (BeanProperties) objIn.readObject();

            } catch (final Exception e) {
                throw ExceptionUtil.rethrow(e);
            }
        }

        private static byte[] serialize(final BeanProperties pSource) {
            try {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                final ObjectOutputStream objOut = new ObjectOutputStream(out);
                objOut.writeObject(pSource);
                objOut.close();
                out.close();
                return out.toByteArray();

            } catch (final IOException e) {
                throw ExceptionUtil.rethrow(e);
            }
        }
    }

    /**
     * @author jabaraster
     */
    public static class T01 {
        /**
         * 
         */
        @Rule
        public ExpectedException exs = ExpectedException.none();

        /**
         * 
         */
        @SuppressWarnings("nls")
        @Test
        public void _getに存在しないプロパティを指定() {
            final String propertyName = "notExistsProperty";
            this.exs.expect(IllegalArgumentException.class);
            this.exs.expectMessage("no property for '" + propertyName + "' found.");
            BeanProperties.getInstance(XGetterOnly.class).get(propertyName);
        }

        /**
         * 
         */
        @SuppressWarnings({ "static-method", "boxing" })
        @Test
        public void _プロパティの存在判定() {
            final BeanProperties sut = BeanProperties.getInstance(XGetterOnly.class);
            assertThat(sut.contains("DifferType"), is(true)); //$NON-NLS-1$
            assertThat(sut.contains("notExistsProperty"), is(false)); //$NON-NLS-1$
        }
    }

    @SuppressWarnings({ "javadoc", "static-method" })
    @Ignore
    public static class XGetterOnly {
        @Order(50)
        public Integer getDifferType() {
            return null;
        }

        @Order(40)
        @Localized
        public String getGetterOnly() {
            return null;
        }

        @Order(30)
        public Object getHasNoParameter() {
            return null;
        }

        @Order(60)
        @Hidden
        public int getHidden() {
            return 0;
        }

        public long getNotOrderAnnotated() {
            return 0;
        }

        public void setDifferType(@SuppressWarnings("unused") final Long l) {
            //
        }

        public void setHasNoParameter() {
            //
        }
    }
}
