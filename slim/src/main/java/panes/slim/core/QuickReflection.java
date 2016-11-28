package panes.slim.core;


import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * reflect util.
 * Created by panes.
 * PUBLIC: 1     （0000 0001）
 * PRIVATE: 2    （0000 0010）
 * PROTECTED: 4 （0000 0100）
 * STATIC: 8 （0000 1000）
 * FINAL: 16 （0001 0000）
 * SYNCHRONIZED: 32  （0010 0000）
 * VOLATILE: 64  （0100 0000）
 * TRANSIENT: 128  （1000 0000）
 * NATIVE: 256   （0001  0000 0000）
 * INTERFACE: 512  （0010 0000 0000）
 * ABSTRACT: 1024  （0100 0000 0000）
 * STRICT: 2048  （1000 0000 0000）
 */
public final class QuickReflection {
    private static IQrExceptionHandler mQrHandler;
    private QuickReflection() {
    }
    /**
     * handle a reflection error.
     */
    public static interface IQrExceptionHandler {
        boolean onError(QrException qrException);
    }

    /**
     * use toString() to have a glance at error msg.
     */
    public static class QrException extends Throwable {
        private static final long serialVersionUID = 1;

        public Class<?> getReflectedClass() {
            return mReflectedClass;
        }

        public void setReflectedClass(Class<?> mReflectedClass) {
            this.mReflectedClass = mReflectedClass;
        }

        public String getReflectedFieldName() {
            return mReflectedFieldName;
        }

        public void setReflectedFieldName(String mReflectedFieldName) {
            this.mReflectedFieldName = mReflectedFieldName;
        }

        public String getReflectedMethodName() {
            return mReflectedMethodName;
        }

        public void setReflectedMethodName(String mReflectedMethodName) {
            this.mReflectedMethodName = mReflectedMethodName;
        }

        private Class<?> mReflectedClass;
        private String mReflectedFieldName;
        private String mReflectedMethodName;

        public QrException(String str) {
            super(str);
        }

        public QrException(Exception exception) {
            super(exception);
        }

        public String toString() {
            return getCause() != null ? getClass().getName() + ": " + getCause() : super.toString();
        }

    }

    /**
     * use this to describe a reflection result.
     *
     * @param <C>
     */
    public static class QrClass<C> {
        protected Class<C> mClass;

        public <T> QrField<C, T> staticField(String str) throws QrException {
            return new QrField<C, T>(this.mClass, str, 8);
        }

        public <T> QrField<C, T> field(String str) throws QrException {
            return new QrField(this.mClass, str, 0);
        }

        /**
         * static modifier equals 8
         *
         * @param str
         * @param clsArr
         * @return
         * @throws QrException
         */
        public QrMethod staticMethod(String str, Class<?>... clsArr) throws QrException {
            return new QrMethod(this.mClass, str, clsArr, 8);//according to Modifier.toString();
        }

        /**
         * reflect the specified method.
         *
         * @param str
         * @param clsArr
         * @return
         * @throws QrException
         */
        public QrMethod method(String str, Class<?>... clsArr) throws QrException {
            return new QrMethod(this.mClass, str, clsArr, 0);
        }

        public QrConstructor constructor(Class<?>... clsArr) throws QrException {
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

        QrConstructor(Class<?> cls, Class<?>[] paramsArr) throws QrException {
            if (cls != null) {
                try {
                    this.mConstructor = cls.getDeclaredConstructor(paramsArr);
                } catch (Exception e) {
                    QrException qrException = new QrException(e);
                    qrException.setReflectedClass(cls);
                    QuickReflection.fail(qrException);
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

    /**
     * reflected field
     * @param <C> object belongs to
     * @param <T> field type
     */
    public static class QrField<C, T> {
        private final Field mField;

        public QrField<C, T> ofGenericType(Class<?> cls) throws QrException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField.getType()))) {
                QuickReflection.fail(new QrException(new ClassCastException(this.mField + " is not of type " + cls)));
            }
            return this;
        }

        public QrField<C, T> ofType(Class<?> cls) throws QrException {
            if (!(this.mField == null || cls.isAssignableFrom(this.mField.getType()))) {
                QuickReflection.fail(new QrException(new ClassCastException(this.mField + " is not of type " + cls)));
            }
            return this;
        }

        public QrField<C, T> ofType(String str) throws QrException {
            QrField<C, T> ofType = null;
            try {
                ofType = ofType((Class<T>) Class.forName(str));
            } catch (Exception e) {
                QuickReflection.fail(new QrException(e));
            }
            return ofType;
        }

        /**
         * returns value of C
         * @param c
         * @return
         */
        public T get(C c) {
            try {
                return (T) this.mField.get(c);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return null;
            }
        }

        /**
         * Obj.mField = value
         * @param Obj
         * @param value
         */
        public void set(C Obj, Object value) {
            try {
                this.mField.set(Obj, value);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }

        /**
         *
         * @param cls Object
         * @param str field name
         * @param i modifiers. ignore them if i=0.
         * @throws QrException
         */
        QrField(Class<C> cls, String str, int i) throws QrException {
            Field field = null;
            if (cls == null) {
                this.mField = null;
                return;
            }
            try {
                field = cls.getDeclaredField(str);
                if (i > 0 && (field.getModifiers() & i) != i) {
                    QuickReflection.fail(new QrException(field + " does not match modifiers: " + i));
                }
                field.setAccessible(true);
            } catch (Exception e) {
                QrException qrException = new QrException(e);
                qrException.setReflectedClass(cls);
                qrException.setReflectedFieldName(str);
                QuickReflection.fail(qrException);
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

        /**
         * parse a null cls to clear QrMethod.
         * cls.getDeclaredMethod(str, clsArr);
         *
         * @param cls    Object
         * @param str    function name
         * @param clsArr params
         * @param i      modifiers. ** ignore modifiers if i=0 **
         * @throws QrException
         */
        QrMethod(Class<?> cls, String str, Class<?>[] clsArr, int i) throws QrException {
            Method method = null;
            if (cls == null) {
                this.mMethod = null;
                return;
            }
            try {
                method = cls.getDeclaredMethod(str, clsArr);
                if (i > 0 && (method.getModifiers() & i) != i) { // only if i= 0
                    QuickReflection.fail(new QrException(method + " does not match modifiers which is: " + i));
                }
                method.setAccessible(true);
            } catch (Exception e) {
                QrException qrException = new QrException(e);
                qrException.setReflectedClass(cls);
                qrException.setReflectedFieldName(str);
                QuickReflection.fail(qrException);
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

    public static <T> QrClass<T> into(String str) throws QrException {
        try {
            return new QrClass(Class.forName(str));
        } catch (Exception e) {
            fail(new QrException(e));
            return new QrClass(null);
        }
    }

    private static void fail(QrException qrException) throws QrException {
        if (mQrHandler == null || !mQrHandler.onError(qrException)) {
            throw qrException;
        }
    }

    public static void setAssertionFailureHandler(IQrExceptionHandler assertionFailureHandler) {
        mQrHandler = assertionFailureHandler;
    }


}
