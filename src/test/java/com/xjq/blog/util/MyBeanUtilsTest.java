package com.xjq.blog.util;

import org.junit.jupiter.api.Test;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MyBeanUtilsTest {

    static class TestBean {
        private String name;
        private Integer age;
        private String email;

        // Getters và Setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public Integer getAge() { return age; }
        public void setAge(Integer age) { this.age = age; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }

    // Phương thức getNullPropertyNames trong MyBeanUtils
    public static class MyBeanUtils {
        public static String[] getNullPropertyNames(Object source) {
            try {
                PropertyDescriptor[] propertyDescriptors = Introspector.getBeanInfo(source.getClass(), Object.class).getPropertyDescriptors();
                List<String> nullProperties = new ArrayList<>();
                for (PropertyDescriptor pd : propertyDescriptors) {
                    Method readMethod = pd.getReadMethod();
                    if (readMethod != null) {
                        Object value = readMethod.invoke(source);
                        if (value == null) {
                            nullProperties.add(pd.getName());
                        }
                    }
                }
                return nullProperties.toArray(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
                return new String[0];
            }
        }
    }

    @Test
    void testGetNullPropertyNames_AllFieldsNull() {
        TestBean bean = new TestBean(); // Tất cả fields đều null

        String[] nullProperties = MyBeanUtils.getNullPropertyNames(bean);

        assertNotNull(nullProperties);
        assertTrue(nullProperties.length >= 3); // Có ít nhất 3 properties: name, age, email
        assertTrue(contains(nullProperties, "name"));
        assertTrue(contains(nullProperties, "age"));
        assertTrue(contains(nullProperties, "email"));
    }

    @Test
    void testGetNullPropertyNames_SomeFieldsNull() {
        TestBean bean = new TestBean();
        bean.setName("Alice");

        String[] nullProperties = MyBeanUtils.getNullPropertyNames(bean);

        assertNotNull(nullProperties);
        // Chắc chắn "age" và "email" vẫn null
        assertTrue(contains(nullProperties, "age"));
        assertTrue(contains(nullProperties, "email"));
        // "name" đã có giá trị nên không được nằm trong list
        assertFalse(contains(nullProperties, "name"));
    }

    @Test
    void testGetNullPropertyNames_NoNullFields() {
        TestBean bean = new TestBean();
        bean.setName("Alice");
        bean.setAge(30);
        bean.setEmail("alice@example.com");

        String[] nullProperties = MyBeanUtils.getNullPropertyNames(bean);

        // Chỉ còn properties hệ thống (vd: class)
        assertNotNull(nullProperties);
        assertTrue(nullProperties.length >= 1); // Ít nhất 1 (thường là 'class')
        assertFalse(contains(nullProperties, "name"));
        assertFalse(contains(nullProperties, "age"));
        assertFalse(contains(nullProperties, "email"));
    }

    private boolean contains(String[] arr, String target) {
        return Arrays.asList(arr).contains(target);
    }
}
