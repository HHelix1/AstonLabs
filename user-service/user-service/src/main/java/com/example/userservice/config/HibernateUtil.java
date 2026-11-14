package com.example.userservice.config;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

public final class HibernateUtil {
    private static final SessionFactory SESSION_FACTORY = buildSessionFactory();

    private HibernateUtil() {
    }

    private static SessionFactory buildSessionFactory() {
        try {
            Configuration configuration = new Configuration().configure("hibernate.cfg.xml");

            setIfPresent(configuration, "hibernate.connection.url", "DB_URL");
            setIfPresent(configuration, "hibernate.connection.username", "DB_USER");
            setIfPresent(configuration, "hibernate.connection.password", "DB_PASSWORD");

            return configuration.buildSessionFactory();
        } catch (Throwable exception) {
            System.err.println("Не удалось создать SessionFactory: " + exception.getMessage());
            throw new ExceptionInInitializerError(exception);
        }
    }

    private static void setIfPresent(Configuration configuration, String property, String envName) {
        String value = System.getenv(envName);
        if (value != null && !value.isBlank()) {
            configuration.setProperty(property, value);
        }
    }

    public static SessionFactory getSessionFactory() {
        return SESSION_FACTORY;
    }

    public static void shutdown() {
        if (SESSION_FACTORY != null && !SESSION_FACTORY.isClosed()) {
            SESSION_FACTORY.close();
        }
    }
}
