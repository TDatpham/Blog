package com.xjq.blog.service;

import com.xjq.blog.NotFoundException;
import com.xjq.blog.repository.BlogRepository;
import com.xjq.blog.model.Blog;
import com.xjq.blog.model.Type;
import com.xjq.blog.model.Tag;
import com.xjq.blog.model.User;
import com.xjq.blog.repository.UserRepository;
import com.xjq.blog.util.MarkdownUtils;
import com.xjq.blog.util.MyBeanUtils;
import com.xjq.blog.vo.BlogQuery;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.*;
import java.util.*;

@Service
public class BlogServiceImpl implements BlogService {

    @Autowired
    private BlogRepository blogRepository;

    @Override
    public Page<Blog> listBlogs(int page, Integer size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        return blogRepository.findAll(pageable);
    }

    @Override
    public Blog getBlog(Long id) {
        return blogRepository.findById(id).orElse(null);
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable, BlogQuery blog) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                if (!"".equals(blog.getTitle()) && blog.getTitle() != null) {
                    predicates.add(cb.like(root.<String>get("title"), "%" + blog.getTitle() + "%"));
                }
                if (blog.getTypeId() != null) {
                    predicates.add(cb.equal(root.<Type>get("type").get("id"), blog.getTypeId()));
                }
                if (blog.isRecommend()) {
                    predicates.add(cb.equal(root.<Boolean>get("recommend"), blog.isRecommend()));
                }
                if (blog.getUserId() != null) {
                    predicates.add(cb.equal(root.get("user").get("id"), blog.getUserId()));
                }
                if (blog.getPublished() != null) {
                    predicates.add(cb.equal(root.get("published"), blog.getPublished()));
                }
                if (blog.getApproved() != null) {
                    predicates.add(cb.equal(root.get("approved"), blog.getApproved()));
                }
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        }, pageable);
    }

    public Blog saveBlog(Blog blog) {
        if (blog.getId() == null) {
            blog.setCreateTime(new Date());
            blog.setUpdateTime(new Date());
            blog.setViews(0);
        } else {
            blog.setUpdateTime(new Date());
        }
        try {
            // Log ngay trước khi lưu blog
            System.out.println("Saving blog: " + blog.toString());
            return blogRepository.save(blog);
        } catch (Exception e) {
            // Log exception
            System.err.println("Error saving blog: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public Page<Blog> listBlog(Pageable pageable) {
        return blogRepository.findAll(pageable);
    }

    @Override
    public Page<Blog> listBlog(Long tagId, Pageable pageable) {
        return blogRepository.findAll(new Specification<Blog>() {
            @Override
            public Predicate toPredicate(Root<Blog> root, CriteriaQuery<?> cq, CriteriaBuilder cb) {
                Join<Blog, Tag> join = root.join("tags");
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(join.get("id"), tagId));
                predicates.add(cb.equal(root.get("published"), true));
                predicates.add(cb.equal(root.get("approved"), true));
                return cb.and(predicates.toArray(new Predicate[0]));
            }
        }, pageable);
    }

    @Override
    public Page<Blog> listBlog(String query, Pageable pageable) {
        return blogRepository.findByQuery(query, pageable);
    }

    // Chuyển từ Markdown sang HTML, cập nhật lượt xem
    @Transactional
    @Override
    public Blog getAndConvert(Long id) {
        Blog blog = blogRepository.findById(id).orElse(null);
        if (blog == null || !blog.isPublished() || !blog.isApproved()) {
            throw new NotFoundException("This article is invalid or not available");
        }
        Blog b = new Blog();
        BeanUtils.copyProperties(blog, b);
        String content = b.getContent();
        b.setContent(MarkdownUtils.markdownToHtmlExtensions(content));
        // view accumulate
        blogRepository.updateViews(id);

        return b;
    }

    @Override
    public List<Blog> listRecommendBlogTop(Integer size) {
        Pageable pageable = PageRequest.of(0, size, Sort.by(Sort.Direction.DESC, "updateTime"));
        return blogRepository.findTop(pageable);
    }

    @Override
    public Map<String, List<Blog>> archiveBlog() {
        List<String> years = blogRepository.findGroupYear();
        Map<String, List<Blog>> map = new HashMap<>();
        for (String year : years) {
            map.put(year, blogRepository.findByYear(year));
        }
        return map;
    }

    @Override
    public Long countBlog() {
        return blogRepository.count();
    }

    @Transactional
    @Override
    public Blog updateBlog(Long id, Blog blog) {
        Blog b = blogRepository.findById(id).orElse(null);
        if (b == null) {
            throw new NotFoundException("This blog is not exist");
        }
        BeanUtils.copyProperties(blog, b, MyBeanUtils.getNullPropertyNames(blog));
        b.setUpdateTime(new Date());
        return blogRepository.save(b);
    }

    @Transactional
    @Override
    public void deleteBlog(Long id) {
        blogRepository.deleteById(id);
    }

    @Override
    public Page<Blog> listPublicBlogs(Pageable pageable) {
        return blogRepository.findByPublishedTrueAndApprovedTrue(pageable);
    }

    @Transactional
    @Override
    public void approveBlog(Long id, boolean approved) {
        blogRepository.updateApproved(id, approved);
    }

    @Autowired
    private UserRepository userRepository;

    @Transactional
    @Override
    public void likeBlog(Long blogId, Long userId) {
        Blog blog = blogRepository.findById(blogId).orElse(null);
        User user = userRepository.findById(userId).orElse(null);

        if (blog != null && user != null) {
            List<User> likedUsers = blog.getLikedUsers();
            if (likedUsers.contains(user)) {
                likedUsers.remove(user);
                blog.setLikes(Math.max(0, blog.getLikes() - 1));
            } else {
                likedUsers.add(user);
                blog.setLikes(blog.getLikes() + 1);
            }
            blogRepository.save(blog);
        }
    }
}
