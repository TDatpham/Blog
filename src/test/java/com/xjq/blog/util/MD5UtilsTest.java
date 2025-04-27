// src/test/java/com/xjq/blog/util/MD5UtilsTest.java
package com.xjq.blog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MD5UtilsTest {

    @Test
    void testCodeWithNormalString() {
        String input = "toor";
        String md5Hash = MD5Utils.code(input);

        assertNotNull(md5Hash); // Kết quả không được null
        assertEquals(32, md5Hash.length()); // MD5 dạng hex 32 ký tự
    }

    @Test
    void testCodeWithEmptyString() {
        String input = "";
        String md5Hash = MD5Utils.code(input);

        assertNotNull(md5Hash);
        assertEquals(32, md5Hash.length());
        // MD5 hash của chuỗi rỗng luôn cố định
        assertEquals("d41d8cd98f00b204e9800998ecf8427e", md5Hash);
    }

    @Test
    void testCodeConsistency() {
        String input = "test";
        String hash1 = MD5Utils.code(input);
        String hash2 = MD5Utils.code(input);

        assertEquals(hash1, hash2); // Cùng input phải ra cùng kết quả
    }

    @Test
    void testCodeWithNull() {
        // Hiện tại nếu truyền null sẽ lỗi vì md.update(str.getBytes())
        // --> Nếu muốn test null bạn cần bắt lỗi riêng trong hàm chính.
        // Ở đây mình test luôn exception để biết.
        assertThrows(NullPointerException.class, () -> {
            MD5Utils.code(null);
        });
    }
}
