package com.xjq.blog.service;

import com.xjq.blog.repository.CommentRepository;
import com.xjq.blog.model.Comment;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class CommentServiceImpl implements CommentService {

    @Autowired
    private CommentRepository commentRepository;

    @Override
    public List<Comment> listCommentByBlogId(Long blogId) {
        // Fetch comments with no parent and sort by creation time
        List<Comment> comments = commentRepository.findByBlogIdAndParentCommentNull(blogId,
                Sort.by(Sort.Order.asc("createTime")));
        return eachComment(comments);
    }

    @Transactional
    @Override
    public Comment saveComment(Comment comment) {
        if (comment.getParentComment() != null && comment.getParentComment().getId() != -1) {
            // Find the parent comment and set it
            comment.setParentComment(commentRepository.findById(comment.getParentComment().getId()).orElse(null));
        } else {
            // If no parent comment, set to null
            comment.setParentComment(null);
        }
        // Set the current timestamp for creation time
        comment.setCreateTime(new Date());
        return commentRepository.save(comment);
    }

    /**
     * Processes the top-level comments and their children recursively.
     * 
     * @param comments List of top-level comments to process.
     * @return List of comments with their nested replies.
     */
    private List<Comment> eachComment(List<Comment> comments) {
        List<Comment> commentsView = new ArrayList<>();
        for (Comment comment : comments) {
            Comment c = new Comment();
            BeanUtils.copyProperties(comment, c);
            commentsView.add(c);
        }
        // Combine replies of all levels into the top-level comments
        combineChildren(commentsView);
        return commentsView;
    }

    /**
     * Combines the children comments into the top-level comments.
     * 
     * @param comments The list of comments to be processed.
     */
    private void combineChildren(List<Comment> comments) {
        for (Comment comment : comments) {
            List<Comment> replies = comment.getReplyComments();
            List<Comment> tempReplys = new ArrayList<>();
            for (Comment reply : replies) {
                // Recursively find all nested replies and add them to tempReplys
                recursively(reply, tempReplys);
            }
            // Set the flattened replies to the top-level comment
            comment.setReplyComments(tempReplys);
        }
    }

    /**
     * Recursively processes replies for each comment, adding them to the provided
     * list.
     * 
     * @param comment    The comment to process.
     * @param tempReplys The list to collect all flattened replies.
     */
    private void recursively(Comment comment, List<Comment> tempReplys) {
        tempReplys.add(comment); // Add the current comment
        if (comment.getReplyComments() != null && !comment.getReplyComments().isEmpty()) {
            List<Comment> replies = comment.getReplyComments();
            for (Comment reply : replies) {
                recursively(reply, tempReplys);
            }
        }
    }
}
