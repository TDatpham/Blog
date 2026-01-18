package com.xjq.blog.web;

import com.xjq.blog.model.Blog;
import com.xjq.blog.model.Comment;
import com.xjq.blog.model.User;
import com.xjq.blog.service.BlogService;
import com.xjq.blog.service.CommentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

@Controller
public class CommentController {

    @Autowired
    private CommentService commentService;

    @Autowired
    private BlogService blogService;

    @Value("${comment.avatar:/images/default-avatar.png}")
    private String avatar;

    @GetMapping("/comments/{blogId}")
    public String comments(@PathVariable Long blogId, Model model) {
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }

    @PostMapping("/comments")
    public String post(@Valid Comment comment, BindingResult result,
            HttpSession session, RedirectAttributes attributes, Model model) {

        // Validate input
        if (result.hasErrors() || comment.getBlog() == null) {
            attributes.addFlashAttribute("message", "Invalid comment data");
            return "redirect:/";
        }

        // Process comment
        Long blogId = comment.getBlog().getId();
        Blog blog = blogService.getBlog(blogId);
        if (blog == null) {
            attributes.addFlashAttribute("message", "Blog not found");
            return "redirect:/";
        }

        comment.setBlog(blog);
        User user = (User) session.getAttribute("user");

        if (user != null) {
            comment.setAvatar(user.getAvatar());
            comment.setAdminComment(true);
        } else {
            comment.setAvatar(avatar);
        }

        commentService.saveComment(comment);
        model.addAttribute("comments", commentService.listCommentByBlogId(blogId));
        return "blog :: commentList";
    }
}