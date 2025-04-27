package com.xjq.blog.service;

import com.xjq.blog.dao.TagRepository;
import com.xjq.blog.po.Tag;
import com.xjq.blog.service.TagServiceImpl;
import com.xjq.blog.NotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TagServiceImplTest {

    @Mock
    private TagRepository tagRepository;

    @InjectMocks
    private TagServiceImpl tagService;

    private Tag tag;

    @BeforeEach
    void setUp() {
        tag = new Tag();
        tag.setId(1L);
        tag.setName("Test Tag");
    }

    @Test
    void testSaveTag() {
        when(tagRepository.save(any(Tag.class))).thenReturn(tag);

        Tag savedTag = tagService.saveTag(tag);

        assertNotNull(savedTag);
        assertEquals("Test Tag", savedTag.getName());
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void testGetTag() {
        when(tagRepository.findOne(1L)).thenReturn(tag);

        Tag foundTag = tagService.getTag(1L);

        assertNotNull(foundTag);
        assertEquals("Test Tag", foundTag.getName());
        verify(tagRepository, times(1)).findOne(1L);
    }

    @Test
    void testGetTagByName() {
        when(tagRepository.findByName("Test Tag")).thenReturn(tag);

        Tag foundTag = tagService.getTagByName("Test Tag");

        assertNotNull(foundTag);
        assertEquals("Test Tag", foundTag.getName());
        verify(tagRepository, times(1)).findByName("Test Tag");
    }

    @Test
    void testListTagWithPageable() {
        Pageable pageable = PageRequest.of(0, 5);
        when(tagRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(tag)));

        Page<Tag> tagPage = tagService.listTag(pageable);

        assertNotNull(tagPage);
        assertEquals(1, tagPage.getTotalElements());
        verify(tagRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListTag() {
        when(tagRepository.findAll()).thenReturn(Arrays.asList(tag));

        List<Tag> tags = tagService.listTag();

        assertNotNull(tags);
        assertEquals(1, tags.size());
        assertEquals("Test Tag", tags.get(0).getName());
        verify(tagRepository, times(1)).findAll();
    }

    @Test
    void testListTagByIds() {
        String ids = "1,2,3";
        List<Tag> expectedTags = Arrays.asList(tag, new Tag()); // Example of the returned tags
        when(tagRepository.findAllById(anyList())).thenReturn(expectedTags);

        List<Tag> tags = tagService.listTag(ids);

        assertNotNull(tags);
        assertEquals(2, tags.size());
        verify(tagRepository, times(1)).findAllById(anyList());
    }

    @Test
    void testListTagTop() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "blogs.size"));
        when(tagRepository.findTop(pageable)).thenReturn(Arrays.asList(tag));

        List<Tag> topTags = tagService.listTagTop(5);

        assertNotNull(topTags);
        assertEquals(1, topTags.size());
        verify(tagRepository, times(1)).findTop(pageable);
    }

    @Test
    void testUpdateTag() {
        Tag updatedTag = new Tag();
        updatedTag.setName("Updated Tag");

        when(tagRepository.findOne(1L)).thenReturn(tag);
        when(tagRepository.save(any(Tag.class))).thenReturn(updatedTag);

        Tag result = tagService.updateTag(1L, updatedTag);

        assertNotNull(result);
        assertEquals("Updated Tag", result.getName());
        verify(tagRepository, times(1)).findOne(1L);
        verify(tagRepository, times(1)).save(any(Tag.class));
    }

    @Test
    void testUpdateTagNotFound() {
        Tag updatedTag = new Tag();
        updatedTag.setName("Updated Tag");

        when(tagRepository.findOne(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> tagService.updateTag(1L, updatedTag));

        verify(tagRepository, times(1)).findOne(1L);
        verify(tagRepository, never()).save(any(Tag.class));
    }

    @Test
    void testDeleteTag() {
        doNothing().when(tagRepository).deleteById(1L);

        tagService.deleteTag(1L);

        verify(tagRepository, times(1)).deleteById(1L);
    }
}
