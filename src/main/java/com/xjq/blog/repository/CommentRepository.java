package com.xjq.blog.repository;

import com.xjq.blog.model.Comment;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {
    List<Comment> findByBlogIdAndParentCommentNull(Long blogId, Sort sort);

    default Comment findOne(Long id) {
        return (Comment) findById(id).orElse(null);
    }
}
