package com.xjq.blog.service;

import com.xjq.blog.NotFoundException;
import com.xjq.blog.dao.BlogRepository;
import com.xjq.blog.po.Blog;
import com.xjq.blog.vo.BlogQuery;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class BlogServiceImplTest {

    @Mock
    private BlogRepository blogRepository;

    @InjectMocks
    private BlogServiceImpl blogService;

    @BeforeEach
    void setUp() {
        openMocks(this);
    }

    @Test
    void testGetBlog() {
        Blog blog = new Blog();
        when(blogRepository.findOne(1L)).thenReturn(blog);  // Mock trả về đối tượng Blog hợp lệ

        Blog result = blogService.getBlog(1L);

        assertNotNull(result);  // Kiểm tra result không null
        verify(blogRepository).findOne(1L);  // Kiểm tra phương thức findOne được gọi
    }


    @Test
    void testListBlog_WithBlogQuery() {
        BlogQuery blogQuery = new BlogQuery();
        blogQuery.setTitle("Test");

        Pageable pageable = PageRequest.of(0, 5);
        Page<Blog> page = new PageImpl<>(Collections.emptyList());

        when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Blog> result = blogService.listBlog(pageable, blogQuery);

        assertNotNull(result);
        verify(blogRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testSaveBlog_New() {
        Blog blog = new Blog();

        when(blogRepository.save(any(Blog.class))).thenReturn(blog);

        Blog result = blogService.saveBlog(blog);

        assertNotNull(result);
        assertEquals(0, result.getViews());
        assertNotNull(result.getCreateTime());
        verify(blogRepository).save(blog);
    }

    @Test
    void testSaveBlog_Update() {
        Blog blog = new Blog();
        blog.setId(1L);

        when(blogRepository.save(any(Blog.class))).thenReturn(blog);

        Blog result = blogService.saveBlog(blog);

        assertNotNull(result);
        assertNotNull(result.getUpdateTime());
        verify(blogRepository).save(blog);
    }

    @Test
    void testListBlog_NoCondition() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Blog> page = new PageImpl<>(Collections.emptyList());

        when(blogRepository.findAll(pageable)).thenReturn(page);

        Page<Blog> result = blogService.listBlog(pageable);

        assertNotNull(result);
        verify(blogRepository).findAll(pageable);
    }

    @Test
    void testListBlog_ByTagId() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Blog> page = new PageImpl<>(Collections.emptyList());

        when(blogRepository.findAll(any(Specification.class), eq(pageable))).thenReturn(page);

        Page<Blog> result = blogService.listBlog(1L, pageable);

        assertNotNull(result);
        verify(blogRepository).findAll(any(Specification.class), eq(pageable));
    }

    @Test
    void testListBlog_ByQueryString() {
        Pageable pageable = PageRequest.of(0, 5);
        Page<Blog> page = new PageImpl<>(Collections.emptyList());

        when(blogRepository.findByQuery(eq("test"), eq(pageable))).thenReturn(page);

        Page<Blog> result = blogService.listBlog("test", pageable);

        assertNotNull(result);
        verify(blogRepository).findByQuery(eq("test"), eq(pageable));
    }

    @Test
    void testGetAndConvert_Success() {
        Blog blog = new Blog();
        blog.setContent("## Title");  // Đảm bảo có nội dung để chuyển đổi

        when(blogRepository.findOne(1L)).thenReturn(blog);  // Mock trả về blog hợp lệ

        Blog result = blogService.getAndConvert(1L);

        assertNotNull(result);
        assertTrue(result.getContent().contains("<h2>"));  // Kiểm tra nội dung đã được chuyển đổi sang HTML
        verify(blogRepository).updateViews(1L);  // Kiểm tra xem views có được cập nhật không
    }


    @Test
    void testGetAndConvert_NotFound() {
        when(blogRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> blogService.getAndConvert(1L));
    }

    @Test
    void testListRecommendBlogTop() {
        List<Blog> blogs = Arrays.asList(new Blog(), new Blog());

        when(blogRepository.findTop(any(Pageable.class))).thenReturn(blogs);

        List<Blog> result = blogService.listRecommendBlogTop(2);

        assertEquals(2, result.size());
        verify(blogRepository).findTop(any(Pageable.class));
    }

    @Test
    void testArchiveBlog() {
        List<String> years = Arrays.asList("2024", "2023");
        when(blogRepository.findGroupYear()).thenReturn(years);
        when(blogRepository.findByYear(anyString())).thenReturn(Collections.singletonList(new Blog()));

        Map<String, List<Blog>> map = blogService.archiveBlog();

        assertEquals(2, map.size());
        verify(blogRepository, times(2)).findByYear(anyString());
    }

    @Test
    void testCountBlog() {
        when(blogRepository.count()).thenReturn(10L);

        Long count = blogService.countBlog();

        assertEquals(10L, count);
        verify(blogRepository).count();
    }

    @Test
    void testUpdateBlog_Success() {
        Blog existBlog = new Blog();  // Blog hiện tại
        Blog updatedBlog = new Blog();  // Blog mới để cập nhật
        updatedBlog.setTitle("New Title");  // Cập nhật title

        when(blogRepository.findOne(1L)).thenReturn(existBlog);  // Mock blog tồn tại
        when(blogRepository.save(any(Blog.class))).thenReturn(existBlog);  // Mock phương thức save

        Blog result = blogService.updateBlog(1L, updatedBlog);

        assertNotNull(result);  // Kiểm tra kết quả không null
        assertEquals("New Title", result.getTitle());  // Kiểm tra title đã được cập nhật
        verify(blogRepository).save(existBlog);  // Kiểm tra phương thức save có được gọi không
    }


    @Test
    void testUpdateBlog_NotFound() {
        when(blogRepository.findById(1L)).thenReturn(java.util.Optional.empty());

        assertThrows(NotFoundException.class, () -> blogService.updateBlog(1L, new Blog()));
    }

    @Test
    void testDeleteBlog() {
        doNothing().when(blogRepository).deleteById(1L);

        blogService.deleteBlog(1L);

        verify(blogRepository).deleteById(1L);
    }
}
