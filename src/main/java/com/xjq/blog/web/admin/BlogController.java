package com.xjq.blog.web.admin;

import com.xjq.blog.po.Blog;
import com.xjq.blog.po.User;
import com.xjq.blog.service.BlogService;
import com.xjq.blog.service.EmailService;
import com.xjq.blog.service.TagService;
import com.xjq.blog.service.TypeService;
import com.xjq.blog.vo.BlogQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/admin")
public class BlogController {

    private static final String INPUT = "admin/blogs-input";
    private static final String LIST = "admin/blogs";
    private static final String REDIRECT_LIST = "redirect:/admin/blogs";

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @Autowired
    private EmailService emailService; // 👈 Thêm dòng này

    @GetMapping("/blogs")
    public String blogs(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                        BlogQuery blog, Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return LIST;
    }

    @PostMapping("/blogs/search")
    public String search(@PageableDefault(size = 8, sort = {"updateTime"}, direction = Sort.Direction.DESC) Pageable pageable,
                         BlogQuery blog, Model model) {
        model.addAttribute("page", blogService.listBlog(pageable, blog));
        return "admin/blogs :: blogList";
    }

    @GetMapping("/blogs/input")
    public String input(Model model) {
        setTypeAndTag(model);
        model.addAttribute("blog", new Blog());
        return INPUT;
    }

    private void setTypeAndTag(Model model) {
        model.addAttribute("types", typeService.listType());
        model.addAttribute("tags", tagService.listTag());
    }

    @GetMapping("/blogs/{id}/input")
    public String editInput(@PathVariable Long id, Model model) {
        setTypeAndTag(model);
        Blog blog = blogService.getBlog(id);
        blog.init();
        model.addAttribute("blog", blog);
        return INPUT;
    }

    @PostMapping("/blogs")
    public String post(Blog blog, RedirectAttributes attributes, HttpSession session) {
        blog.setUser((User) session.getAttribute("user"));
        blog.setType(typeService.getType(blog.getType().getId()));
        blog.setTags(tagService.listTag(blog.getTagIds()));
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

            if (isNew) {
                b = blogService.saveBlog(blog);
                // In ra log để kiểm tra
                System.out.println("Gửi email thông báo bài viết mới.");
                String subject = "📝 Có bài viết mới: " + b.getTitle();
                String content = "Xem bài viết tại: http://localhost:8080/blog/" + b.getId();
                emailService.sendNewPostNotification("phamthanhdat2003vl1@gmail.com", subject, content);
            } else {
                b = blogService.updateBlog(blog.getId(), blog);
            }

        }
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}/delete")
    public String delete(@PathVariable Long id, RedirectAttributes attributes) {
        blogService.deleteBlog(id);
        attributes.addFlashAttribute("message", "Deleted Successfully");
        return REDIRECT_LIST;
    }

    @GetMapping("/blogs/{id}")
    public String showBlog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getBlog(id);
        model.addAttribute("blog", blog);
        return "blog-detail";
    }
}
