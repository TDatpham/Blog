package com.xjq.blog.web;

import com.xjq.blog.NotFoundException;
import com.xjq.blog.model.Blog;
import com.xjq.blog.service.BlogService;
import com.xjq.blog.service.TagService;
import com.xjq.blog.service.TypeService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;

@Controller
public class IndexController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private TypeService typeService;

    @Autowired
    private TagService tagService;

    @GetMapping("/")
    public String index(
            @PageableDefault(size = 8, sort = { "updateTime" }, direction = Sort.Direction.DESC) Pageable pageable,
            Model model) {
        model.addAttribute("page", blogService.listPublicBlogs(pageable));
        model.addAttribute("types", typeService.listTypeTop(6));
        model.addAttribute("tags", tagService.listTagTop(10));
        model.addAttribute("recommendBlogs", blogService.listRecommendBlogTop(8));
        return "index";
    }

    @GetMapping("/search")
    public String search(
            @PageableDefault(size = 8, sort = { "updateTime" }, direction = Sort.Direction.DESC) Pageable pageable,
            @RequestParam(required = false) String query,
            Model model) {
        if (query == null || query.trim().isEmpty()) {
            return "redirect:/";
        }
        String safeQuery = "%" + query.trim() + "%";
        model.addAttribute("page", blogService.listBlog(safeQuery, pageable));
        model.addAttribute("query", query);
        return "search";
    }

    @GetMapping("/blog/{id}")
    public String blog(@PathVariable Long id, Model model) {
        Blog blog = blogService.getAndConvert(id);
        if (blog == null) {
            throw new NotFoundException("Blog không tồn tại");
        }
        model.addAttribute("blog", blog);
        return "blog";
    }

    @PostMapping("/blog/like/{id}")
    @ResponseBody
    public String likeBlog(@PathVariable Long id, HttpSession session) {
        com.xjq.blog.model.User user = (com.xjq.blog.model.User) session.getAttribute("user");
        if (user == null) {
            return "Please login to like";
        }
        blogService.likeBlog(id, user.getId());
        Blog blog = blogService.getBlog(id);
        return String.valueOf(blog.getLikes());
    }

    @GetMapping("footer/newblog")
    public String newblogs(Model model) {
        model.addAttribute("newblogs", blogService.listRecommendBlogTop(3));
        return "_fragments :: newblogList";
    }

    @ExceptionHandler(NotFoundException.class)
    public String handleNotFound() {
        return "error/404";
    }
}