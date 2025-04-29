package com.xjq.blog.service;

import com.xjq.blog.repository.TypeRepository;
import com.xjq.blog.model.Type;
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
class TypeServiceImplTest {

    @Mock
    private TypeRepository typeRepository;

    @InjectMocks
    private TypeServiceImpl typeService;

    private Type type;

    @BeforeEach
    void setUp() {
        type = new Type();
        type.setId(1L);
        type.setName("Technology");
    }

    @Test
    void testSaveType() {
        when(typeRepository.save(any(Type.class))).thenReturn(type);

        Type savedType = typeService.saveType(type);

        assertNotNull(savedType);
        assertEquals("Technology", savedType.getName());
        verify(typeRepository, times(1)).save(any(Type.class));
    }

    @Test
    void testGetType() {
        when(typeRepository.findOne(1L)).thenReturn(type);

        Type foundType = typeService.getType(1L);

        assertNotNull(foundType);
        assertEquals("Technology", foundType.getName());
        verify(typeRepository, times(1)).findOne(1L);
    }

    @Test
    void testGetTypeByName() {
        when(typeRepository.findByName("Technology")).thenReturn(type);

        Type foundType = typeService.getTypeByName("Technology");

        assertNotNull(foundType);
        assertEquals("Technology", foundType.getName());
        verify(typeRepository, times(1)).findByName("Technology");
    }

    @Test
    void testListTypeWithPageable() {
        Pageable pageable = PageRequest.of(0, 5);
        when(typeRepository.findAll(pageable)).thenReturn(new PageImpl<>(Collections.singletonList(type)));

        Page<Type> typePage = typeService.listType(pageable);

        assertNotNull(typePage);
        assertEquals(1, typePage.getTotalElements());
        verify(typeRepository, times(1)).findAll(pageable);
    }

    @Test
    void testListType() {
        when(typeRepository.findAll()).thenReturn(Arrays.asList(type));

        List<Type> types = typeService.listType();

        assertNotNull(types);
        assertEquals(1, types.size());
        assertEquals("Technology", types.get(0).getName());
        verify(typeRepository, times(1)).findAll();
    }

    @Test
    void testListTypeTop() {
        Pageable pageable = PageRequest.of(0, 5, Sort.by(Sort.Direction.DESC, "blogs.size"));
        when(typeRepository.findTop(pageable)).thenReturn(Arrays.asList(type));

        List<Type> topTypes = typeService.listTypeTop(5);

        assertNotNull(topTypes);
        assertEquals(1, topTypes.size());
        verify(typeRepository, times(1)).findTop(pageable);
    }

    @Test
    void testUpdateType() {
        Type updatedType = new Type();
        updatedType.setName("Science");

        when(typeRepository.findOne(1L)).thenReturn(type);
        when(typeRepository.save(any(Type.class))).thenReturn(updatedType);

        Type result = typeService.updateType(1L, updatedType);

        assertNotNull(result);
        assertEquals("Science", result.getName());
        verify(typeRepository, times(1)).findOne(1L);
        verify(typeRepository, times(1)).save(any(Type.class));
    }

    @Test
    void testUpdateTypeNotFound() {
        Type updatedType = new Type();
        updatedType.setName("Science");

        when(typeRepository.findOne(1L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> typeService.updateType(1L, updatedType));

        verify(typeRepository, times(1)).findOne(1L);
        verify(typeRepository, never()).save(any(Type.class));
    }

    @Test
    void testDeleteType() {
        doNothing().when(typeRepository).deleteById(1L);

        typeService.deleteType(1L);

        verify(typeRepository, times(1)).deleteById(1L);
    }
}
