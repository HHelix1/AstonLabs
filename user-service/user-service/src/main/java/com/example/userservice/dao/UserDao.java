package com.example.userservice.dao;

import com.example.userservice.config.HibernateUtil;
import com.example.userservice.entity.User;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

public class UserDao {
    private static final Logger logger = LoggerFactory.getLogger(UserDao.class);

    public User create(User user) {
        return execute(session -> {
            session.persist(user);
            logger.info("Created user with id={}", user.getId());
            return user;
        });
    }

    public Optional<User> findById(long id) {
        return execute(session -> Optional.ofNullable(session.get(User.class, id)));
    }

    public List<User> findAll() {
        return execute(session -> session.createQuery(
                "from User u order by u.id", User.class).getResultList());
    }

    public boolean update(long id, String name, String email, int age) {
        return execute(session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                return false;
            }
            user.setName(name);
            user.setEmail(email);
            user.setAge(age);
            logger.info("Updated user with id={}", id);
            return true;
        });
    }

    public boolean delete(long id) {
        return execute(session -> {
            User user = session.get(User.class, id);
            if (user == null) {
                return false;
            }
            session.remove(user);
            logger.info("Deleted user with id={}", id);
            return true;
        });
    }

    private <T> T execute(Function<Session, T> action) {
        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            T result = action.apply(session);
            transaction.commit();
            return result;
        } catch (ConstraintViolationException exception) {
            rollback(transaction);
            logger.error("Нарушено ограничение БД", exception);
            throw new DataAccessException("Нарушено ограничение базы данных: " + exception.getSQLException().getMessage(), exception);
        } catch (HibernateException exception) {
            rollback(transaction);
            logger.error("Ошибка Hibernate при выполнении операции", exception);
            throw new DataAccessException("Ошибка Hibernate: " + exception.getMessage(), exception);
        } catch (RuntimeException exception) {
            rollback(transaction);
            logger.error("Ошибка при выполнении операции с БД", exception);
            throw exception;
        }
    }

    private void rollback(Transaction transaction) {
        if (transaction != null && transaction.isActive()) {
            try {
                transaction.rollback();
            } catch (HibernateException exception) {
                logger.error("Не удалось выполнить rollback", exception);
            }
        }
    }

    public static class DataAccessException extends RuntimeException {
        public DataAccessException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}
