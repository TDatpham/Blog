package com.xjq.blog.service;

import com.xjq.blog.repository.CommentRepository;
import com.xjq.blog.model.Comment;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceImplTest {

    @Mock
    private CommentRepository commentRepository;

    @InjectMocks
    private CommentServiceImpl commentService;

    private Comment parentComment;
    private Comment childComment;

    @BeforeEach
    void setUp() {
        parentComment = new Comment();
        parentComment.setId(1L);
        parentComment.setContent("Parent Comment");
        parentComment.setCreateTime(new Date());
        parentComment.setReplyComments(Collections.emptyList());

        childComment = new Comment();
        childComment.setId(2L);
        childComment.setContent("Child Comment");
        childComment.setCreateTime(new Date());
        childComment.setParentComment(parentComment);
        childComment.setReplyComments(Collections.emptyList());
    }

    @Test
    void testListCommentByBlogId() {
        when(commentRepository.findByBlogIdAndParentCommentNull(eq(1L), any(Sort.class)))
                .thenReturn(Arrays.asList(parentComment));

        List<Comment> comments = commentService.listCommentByBlogId(1L);

        assertNotNull(comments);
        assertEquals(1, comments.size());
        assertEquals("Parent Comment", comments.get(0).getContent());

        verify(commentRepository, times(1)).findByBlogIdAndParentCommentNull(eq(1L), any(Sort.class));
    }

    @Test
    void testSaveComment_WithNoParent() {
        Comment newComment = new Comment();
        newComment.setParentComment(new Comment());
        newComment.getParentComment().setId(-1L);

        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        Comment savedComment = commentService.saveComment(newComment);

        assertNotNull(savedComment);
        assertNull(savedComment.getParentComment());
        assertNotNull(savedComment.getCreateTime());

        verify(commentRepository, times(1)).save(any(Comment.class));
    }

    @Test
    void testSaveComment_WithParent() {
        Comment newComment = new Comment();
        Comment parent = new Comment();
        parent.setId(1L);
        newComment.setParentComment(parent);

        when(commentRepository.findOne(1L)).thenReturn(parent);
        when(commentRepository.save(any(Comment.class))).thenReturn(newComment);

        Comment savedComment = commentService.saveComment(newComment);

        assertNotNull(savedComment);
        assertEquals(parent, savedComment.getParentComment());
        assertNotNull(savedComment.getCreateTime());

        verify(commentRepository, times(1)).findOne(1L);
        verify(commentRepository, times(1)).save(any(Comment.class));
    }
}
