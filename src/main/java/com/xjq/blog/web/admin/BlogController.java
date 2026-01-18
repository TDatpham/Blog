package com.xjq.blog.web.admin;

import com.xjq.blog.model.Blog;
import com.xjq.blog.model.Comment;
import com.xjq.blog.model.User;
import com.xjq.blog.service.*;
import com.xjq.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;
import java.text.Normalizer;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private NotificationService notificationService;

    // --- Dashboard redirect ---
    @GetMapping("/admin")
    public String adminDashboard() {
        return "admin/blogs"; // hoặc redirect tới /admin/blogs
    }

    // --- Blog list ---
    @GetMapping("/blogs")
    public String blogs(
            @PageableDefault(size = 8, sort = { "updateTime" }, direction = Sort.Direction.DESC) Pageable pageable,
            BlogQuery blog, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && !"ADMIN".equals(user.getRole())) {
            blog.setUserId(user.getId());
        }
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(
            @PageableDefault(size = 8, sort = { "updateTime" }, direction = Sort.Direction.DESC) Pageable pageable,
            BlogQuery blog, Model model, HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user != null && !"ADMIN".equals(user.getRole())) {
            blog.setUserId(user.getId());
        }
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model, HttpSession session) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);

        User user = (User) session.getAttribute("user");
        // Kiểm tra quyền sở hữu
        if (user != null && !"ADMIN".equals(user.getRole()) && !blog.getUser().getId().equals(user.getId())) {
            return "redirect:/admin/blogs"; // Hoặc trang lỗi
        }

        blog.init();
        model.addAttribute("blog", blog);
        return INPUT;
    }

    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        User user = (User) session.getAttribute("user");
        blog.setUser(user);
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));

        if (user != null && "ADMIN".equals(user.getRole())) {
            blog.setApproved(true);
        }

        String slug = toSlug(blog.getTitle());
        blog.setSlug(slug);

        Blog b;
        boolean isNew = (blog.getId() == null);

        if (isNew) {
            b = blogService.saveBlog(blog);
        } else {
            b = blogService.updateBlog(blog.getId(), blog);
        }

        if (b == null) {
            attributes.addFlashAttribute("message", "Operation Failed");
        } else {
            attributes.addFlashAttribute("message", "Operation Succeed");
            if (isNew && b.isPublished() && b.isApproved()) {
                String postUrl = "/blog/" + b.getId();
                notificationService.sendNewPostNotification(b.getTitle(), postUrl);
            }
        }

        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/approve")
    public String approve(@PathVariable Long id, @RequestParam boolean status, RedirectAttributes attributes,
            HttpSession session) {
        User user = (User) session.getAttribute("user");
        if (user == null || !"ADMIN".equals(user.getRole())) {
            attributes.addFlashAttribute("message", "Permission Denied");
            return REDIRECT_LIST;
        }

        blogService.approveBlog(id, status);
        if (status) {
            Blog b = blogService.getBlog(id);
            if (b != null && b.isPublished()) {
                notificationService.sendNewPostNotification(b.getTitle(), "/blog/" + b.getId());
            }
            attributes.addFlashAttribute("message", "Blog Approved and Notification Sent");
        } else {
            attributes.addFlashAttribute("message", "Approved Status Updated");
        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes, HttpSession session) {
        Blog blog = blogService.getBlog(id);
        User user = (User) session.getAttribute("user");

        if (user != null && !"ADMIN".equals(user.getRole()) && !blog.getUser().getId().equals(user.getId())) {
            attributes.addFlashAttribute("message", "You do not have permission to delete this blog");
            return REDIRECT_LIST;
        }

        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "Deleted Successfully");
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}")
    public String showBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlog(id);
        List<Comment> comments = commentService.listCommentByBlogId(id);

        model.addAttribute("blog", blog);
        model.addAttribute("comments", comments);
        String shareUrl = "http://192.168.2.8:8080/blog/" + id + "-" + blog.getSlug();
        model.addAttribute("shareUrl", shareUrl);

        return "blog";
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    private String toSlug(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "")
                .toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-{2,}", "-")
                .replaceAll("^-|-$", "");
    }

    // --- User management ---
    @GetMapping("/users")
    public String listUsers(Model model) {
        List<User> users = userService.findAllUsers();
        model.addAttribute("users", users);
        return "admin/admin";
    }

    @PutMapping("/user/{id}/status")
    @ResponseBody
    public ResponseEntity<?> updateUserStatus(@PathVariable Long id, @RequestParam Boolean enabled) {
        userService.updateUserStatus(id, enabled);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/user/{id}")
    @ResponseBody
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok().build();
    }
}
