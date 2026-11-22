
package com.example.userservice.dao;

import com.example.userservice.entity.User;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class UserDaoIntegrationTest {

    @Container
    private static final PostgreSQLContainer<?> POSTGRES =
            new PostgreSQLContainer<>("postgres:16-alpine")
                    .withDatabaseName("user_service_test")
                    .withUsername("test")
                    .withPassword("test");

    private static SessionFactory sessionFactory;
    private UserDao userDao;

    @BeforeAll
    static void createSessionFactory() {
        Configuration configuration = new Configuration()
                .addAnnotatedClass(User.class)
                .setProperty(
                        "hibernate.connection.driver_class",
                        "org.postgresql.Driver"
                )
                .setProperty(
                        "hibernate.connection.url",
                        POSTGRES.getJdbcUrl()
                )
                .setProperty(
                        "hibernate.connection.username",
                        POSTGRES.getUsername()
                )
                .setProperty(
                        "hibernate.connection.password",
                        POSTGRES.getPassword()
                )
                .setProperty(
                        "hibernate.dialect",
                        "org.hibernate.dialect.PostgreSQLDialect"
                )
                .setProperty(
                        "hibernate.hbm2ddl.auto",
                        "create-drop"
                )
                .setProperty(
                        "hibernate.show_sql",
                        "false"
                );

        sessionFactory = configuration.buildSessionFactory();
    }

    @AfterAll
    static void closeSessionFactory() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }

    @BeforeEach
    void setUp() {
        userDao = new UserDao(sessionFactory);

        try (Session session = sessionFactory.openSession()) {
            session.beginTransaction();
            session.createMutationQuery("delete from User")
                    .executeUpdate();
            session.getTransaction().commit();
        }
    }

    @Test
    void createPersistsUserAndSetsCreatedAt() {
        User created = userDao.create(
                new User("Ivan", "ivan@example.com", 25)
        );

        assertNotNull(created.getId());
        assertNotNull(created.getCreatedAt());

        User loaded = userDao.findById(
                created.getId()
        ).orElseThrow();

        assertEquals("Ivan", loaded.getName());
        assertEquals("ivan@example.com", loaded.getEmail());
        assertEquals(25, loaded.getAge());
    }

    @Test
    void findByIdReturnsEmptyForUnknownId() {
        assertTrue(userDao.findById(999999L).isEmpty());
    }

    @Test
    void findAllReturnsOnlyCurrentTestData() {
        userDao.create(
                new User("Ivan", "ivan@example.com", 25)
        );

        userDao.create(
                new User("Anna", "anna@example.com", 30)
        );

        assertEquals(2, userDao.findAll().size());
    }

    @Test
    void updateChangesExistingUser() {
        User created = userDao.create(
                new User("Ivan", "ivan@example.com", 25)
        );

        assertTrue(userDao.update(
                created.getId(),
                "Ivan Petrov",
                "petrov@example.com",
                26
        ));

        User updated = userDao.findById(
                created.getId()
        ).orElseThrow();

        assertEquals("Ivan Petrov", updated.getName());
        assertEquals("petrov@example.com", updated.getEmail());
        assertEquals(26, updated.getAge());
    }

    @Test
    void updateReturnsFalseForUnknownUser() {
        assertFalse(userDao.update(
                999999L,
                "Nobody",
                "nobody@example.com",
                20
        ));
    }

    @Test
    void deleteRemovesExistingUser() {
        User created = userDao.create(
                new User("Ivan", "ivan@example.com", 25)
        );

        assertTrue(userDao.delete(created.getId()));

        assertTrue(
                userDao.findById(created.getId()).isEmpty()
        );
    }

    @Test
    void deleteReturnsFalseForUnknownUser() {
        assertFalse(userDao.delete(999999L));
    }

    @Test
    void duplicateEmailIsRejected() {
        userDao.create(
                new User("Ivan", "same@example.com", 25)
        );

        assertThrows(
                UserDao.DataAccessException.class,
                () -> userDao.create(
                        new User("Anna", "same@example.com", 30)
                )
        );
    }
}
