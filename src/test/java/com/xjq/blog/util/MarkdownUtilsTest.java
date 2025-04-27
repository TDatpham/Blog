// src/test/java/com/xjq/blog/util/MarkdownUtilsTest.java
package com.xjq.blog.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class MarkdownUtilsTest {

    @Test
    void testMarkdownToHtml() {
        String markdown = "# Hello World";
        String html = MarkdownUtils.markdownToHtml(markdown);

        assertNotNull(html);
        assertTrue(html.contains("<h1>")); // Kiểm tra có thẻ <h1>
        assertTrue(html.contains("Hello World"));
    }

    @Test
    void testMarkdownToHtmlExtensions_Link() {
        String markdown = "[imCoding](http://www.lirenmi.cn)";
        String html = MarkdownUtils.markdownToHtmlExtensions(markdown);

        assertNotNull(html);
        assertTrue(html.contains("<a"));
        assertTrue(html.contains("target=\"_blank\"")); // Kiểm tra a tag có target _blank
    }

    @Test
    void testMarkdownToHtmlExtensions_Table() {
        String markdown = "| Header1 | Header2 |\n" +
                "|---------|---------|\n" +
                "| Cell1   | Cell2   |";

        String html = MarkdownUtils.markdownToHtmlExtensions(markdown);

        assertNotNull(html);
        assertTrue(html.contains("<table")); // Có table
        assertTrue(html.contains("class=\"ui celled table\"")); // Có class đúng
    }
}
