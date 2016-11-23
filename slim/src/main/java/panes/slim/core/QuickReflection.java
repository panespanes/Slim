package panes.slim.core;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * reflect util.
 * Created by panes.
 */
public final class QuickReflection {
    private static AssertionFailureHandler sFailureHandler;

    public static interface AssertionFailureHandler {
        boolean onAssertionFailure(QrDeclaration.AssertionException assertionException);
    }

    /**
     * handle a reflection error.
     */
    public static abstract class QrDeclaration {

        public static class AssertionException extends Throwable {
            private static final long serialVersionUID = 1;
            private Class<?> mHackedClass;
            private String mHackedFieldName;
            private String mHackedMethodName;

            public AssertionException(String str) {
                super(str);
            }

            public AssertionException(Exception exception) {
                super(exception);
            }

            public String toString() {
                return getCause() != null ? getClass().getName() + ": " + getCause() : super.toString();
            }

            public Class<?> getHackedClass() {
                return this.mHackedClass;
            }

            public void setHackedClass(Class<?> cls) {
                this.mHackedClass = cls;
            }

            public String getHackedMethodName() {
                return this.mHackedMethodName;
            }

            public void setHackedMethodName(String str) {
                this.mHackedMethodName = str;
            }

            public String getHackedFieldName() {
                return this.mHackedFieldName;
            }

            public void setHackedFieldName(String str) {
                this.mHackedFieldName = str;
            }
        }
    }

    /**
     * use this to describe a reflection result.
     * @param <C>
     */
    public static class QrClass<C> {
        protected Class<C> mClass;

        public <T> QrField<C, T> staticField(String str) throws QrDeclaration.AssertionException {
            return new QrField<C, T>(this.mClass, str, 8);
        }

        public <T> QrField<C, T> field(String str) throws QrDeclaration.AssertionException {
            return new QrField(this.mClass, str, 0);
        }

        /**
         * static modifier equals 8
         * @param str
         * @param clsArr
         * @return
         * @throws QrDeclaration.AssertionException
         */
        public QrMethod staticMethod(String str, Class<?>... clsArr) throws QrDeclaration.AssertionException {
            return new QrMethod(this.mClass, str, clsArr, 8);//according to Modifier.toString();
        }

        public QrMethod method(String str, Class<?>... clsArr) throws QrDeclaration.AssertionException {
            return new QrMethod(this.mClass, str, clsArr, 0);
        }

        public QrConstructor constructor(Class<?>... clsArr) throws QrDeclaration.AssertionException {
            return new QrConstructor(this.mClass, clsArr);
        }

        public QrClass(Class<C> cls) {
            this.mClass = cls;
        }

        public Class<C> getMClass() {
            return this.mClass;
        }
    }

    public static class QrConstructor {
        protected Constructor<?> mConstructor;

        QrConstructor(Class<?> cls, Class<?>[] paramsArr) throws QrDeclaration.AssertionException {
            if (cls != null) {
                try {
                    this.mConstructor = cls.getDeclaredConstructor(paramsArr);
                } catch (Exception e) {
                    QrDeclaration.AssertionException assertionException = new QrDeclaration.AssertionException(e);
                    assertionException.setHackedClass(cls);
                    QuickReflection.fail(assertionException);
                }
            }
        }

        public Object getInstance(Object... objArr) throws IllegalArgumentException {
            Object obj = null;
            this.mConstructor.setAccessible(true);
            try {
                obj = this.mConstructor.newInstance(objArr);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return obj;
        }
    }

    public static class QrField<C, T> {
        private final Field mField;

        public QrField<C, T> ofGenericType(Class<?> cls) throws QrDeclaration.AssertionException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField.getType()))) {
                QuickReflection.fail(new QrDeclaration.AssertionException(new ClassCastException(this.mField + " is not of type " + cls)));
            }
            return this;
        }

        public QrField<C, T> ofType(Class<?> cls) throws QrDeclaration.AssertionException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField.getType()))) {
                QuickReflection.fail(new QrDeclaration.AssertionException(new ClassCastException(this.mField + " is not of type " + cls)));
            }
            return this;
        }

        public QrField<C, T> ofType(String str) throws QrDeclaration.AssertionException {
            QrField<C, T> ofType = null;
            try {
                ofType = ofType((Class<T>) Class.forName(str));
            } catch (Exception e) {
                QuickReflection.fail(new QrDeclaration.AssertionException(e));
            }
            return ofType;
        }

        public T get(C c) {
            try {
                return (T) this.mField.get(c);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        public void set(C c, Object obj) {
            try {
                this.mField.set(c, obj);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        QrField(Class<C> cls, String str, int i) throws QrDeclaration.AssertionException {
            Field field = null;
            if (cls == null) {
                this.mField = null;
                return;
            }
            try {
                field = cls.getDeclaredField(str);
                if (i > 0 && (field.getModifiers() & i) != i) {
                    QuickReflection.fail(new QrDeclaration.AssertionException(field + " does not match modifiers: " + i));
                }
                field.setAccessible(true);
            } catch (Exception e) {
                QrDeclaration.AssertionException assertionException = new QrDeclaration.AssertionException(e);
                assertionException.setHackedClass(cls);
                assertionException.setHackedFieldName(str);
                QuickReflection.fail(assertionException);
            } finally {
                this.mField = field;
            }
        }

        public Field getField() {
            return this.mField;
        }
    }

    public static class QrMethod {
        protected final Method mMethod;

        public Object invoke(Object obj, Object... objArr) throws IllegalArgumentException, InvocationTargetException {
            Object obj2 = null;
            try {
                obj2 = this.mMethod.invoke(obj, objArr);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            return obj2;
        }

        QrMethod(Class<?> cls, String str, Class<?>[] clsArr, int i) throws QrDeclaration.AssertionException {
            Method method = null;
            if (cls == null) {
                this.mMethod = null;
                return;
            }
            try {
                method = cls.getDeclaredMethod(str, clsArr);
                if (i > 0 && (method.getModifiers() & i) != i) {
                    QuickReflection.fail(new QrDeclaration.AssertionException(method + " does not match modifiers: " + i));
                }
                method.setAccessible(true);
            } catch (Exception e) {
                QrDeclaration.AssertionException assertionException = new QrDeclaration.AssertionException(e);
                assertionException.setHackedClass(cls);
                assertionException.setHackedMethodName(str);
                QuickReflection.fail(assertionException);
            } finally {
                this.mMethod = method;
            }
        }

        public Method getMethod() {
            return this.mMethod;
        }
    }

    public static <T> QrClass<T> into(Class<T> cls) {
        return new QrClass(cls);
    }

    public static <T> QrClass<T> into(String str) throws QrDeclaration.AssertionException {
        try {
            return new QrClass(Class.forName(str));
        } catch (Exception e) {
            fail(new QrDeclaration.AssertionException(e));
            return new QrClass(null);
        }
    }

    private static void fail(QrDeclaration.AssertionException assertionException) throws QrDeclaration.AssertionException {
        if (sFailureHandler == null || !sFailureHandler.onAssertionFailure(assertionException)) {
            throw assertionException;
        }
    }

    public static void setAssertionFailureHandler(AssertionFailureHandler assertionFailureHandler) {
        sFailureHandler = assertionFailureHandler;
    }

    private QuickReflection() {
    }
}
