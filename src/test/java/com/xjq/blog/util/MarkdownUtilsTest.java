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
        // Sửa: kiểm tra tag <h1> hoặc có id attribute
        assertTrue(html.contains("<h1"), "Expected HTML to contain an H1 element");
        assertTrue(html.contains("Hello World"));
    }

    @Test
    void testMarkdownToHtmlExtensions_Link() {
        String markdown = "[imCoding](http://www.lirenmi.cn)";
        String html = MarkdownUtils.markdownToHtmlExtensions(markdown);

        assertNotNull(html);
        assertTrue(html.contains("<a"), "Expected HTML to contain an anchor tag");
        assertTrue(html.contains("target=\"_blank\""), "Expected anchor tag to have target=\"_blank\"");
    }

    @Test
    void testMarkdownToHtmlExtensions_Table() {
        String markdown = "| Header1 | Header2 |\n" +
                "|---------|---------|\n" +
                "| Cell1   | Cell2   |";

        String html = MarkdownUtils.markdownToHtmlExtensions(markdown);

        assertNotNull(html);
        assertTrue(html.contains("<table"), "Expected HTML to contain a table element");
        assertTrue(html.contains("class=\"ui celled table\""), "Expected table to have class=\"ui celled table\"");
    }
}
