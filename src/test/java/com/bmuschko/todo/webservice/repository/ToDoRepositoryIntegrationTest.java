package com.bmuschko.todo.webservice.repository;

import com.bmuschko.todo.webservice.model.ToDoItem;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase.Replace.NONE;

@Testcontainers
@DataJpaTest
@AutoConfigureTestDatabase(replace = NONE)
@ContextConfiguration(initializers = { ToDoRepositoryIntegrationTest.Initializer.class })
public class ToDoRepositoryIntegrationTest {

    @Autowired
    private ToDoRepository repository;

    @Container
    public static PostgreSQLContainer postgreSQLContainer = createDbContainer();

    private static PostgreSQLContainer createDbContainer() {
        return new PostgreSQLContainer("postgres:9.6.10-alpine").
                withUsername("username").
                withPassword("pwd").
                withDatabaseName("todo");
    }

    static class Initializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        @Override
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.datasource.driver-class-name=org.postgresql.Driver",
                    "spring.jpa.generate-ddl=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Test
    public void testCanSaveNewToDoItem() {
        ToDoItem toDoItem = createToDoItem("Buy milk");
        assertNull(toDoItem.getId());
        repository.save(toDoItem);
        assertNotNull(toDoItem.getId());
    }

    private ToDoItem createToDoItem(String name) {
        ToDoItem toDoItem = new ToDoItem();
        toDoItem.setName(name);
        return toDoItem;
    }
}