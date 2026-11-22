
package com.example.userservice.service;

import com.example.userservice.dao.UserDao;
import com.example.userservice.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserDao userDao;

    private UserService userService;

    @BeforeEach
    void setUp() {
        userService = new UserService(userDao);
    }

    @Test
    void createReturnsCreatedUser() {
        User user = new User("Ivan", "ivan@example.com", 25);
        when(userDao.create(user)).thenReturn(user);

        assertSame(user, userService.create(user));
        verify(userDao).create(user);
    }

    @Test
    void findByIdReturnsUserWhenFound() {
        User user = new User("Ivan", "ivan@example.com", 25);
        when(userDao.findById(1L)).thenReturn(Optional.of(user));

        assertEquals(Optional.of(user), userService.findById(1L));
        verify(userDao).findById(1L);
    }

    @Test
    void findByIdReturnsEmptyWhenNotFound() {
        when(userDao.findById(100L)).thenReturn(Optional.empty());

        assertTrue(userService.findById(100L).isEmpty());
        verify(userDao).findById(100L);
    }

    @Test
    void findAllReturnsUsersFromDao() {
        List<User> users = List.of(
                new User("Ivan", "ivan@example.com", 25),
                new User("Anna", "anna@example.com", 30)
        );

        when(userDao.findAll()).thenReturn(users);

        assertSame(users, userService.findAll());
        verify(userDao).findAll();
    }

    @Test
    void updateReturnsTrueWhenUserUpdated() {
        when(userDao.update(
                1L, "Ivan", "ivan@example.com", 26
        )).thenReturn(true);

        assertTrue(userService.update(
                1L, "Ivan", "ivan@example.com", 26
        ));

        verify(userDao).update(
                1L, "Ivan", "ivan@example.com", 26
        );
    }

    @Test
    void updateReturnsFalseWhenUserNotFound() {
        when(userDao.update(
                100L, "Ivan", "ivan@example.com", 26
        )).thenReturn(false);

        assertFalse(userService.update(
                100L, "Ivan", "ivan@example.com", 26
        ));

        verify(userDao).update(
                100L, "Ivan", "ivan@example.com", 26
        );
    }

    @Test
    void deleteReturnsTrueWhenUserDeleted() {
        when(userDao.delete(1L)).thenReturn(true);

        assertTrue(userService.delete(1L));
        verify(userDao).delete(1L);
    }

    @Test
    void deleteReturnsFalseWhenUserNotFound() {
        when(userDao.delete(100L)).thenReturn(false);

        assertFalse(userService.delete(100L));
        verify(userDao).delete(100L);
    }
}
