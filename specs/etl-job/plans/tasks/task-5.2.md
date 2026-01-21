# Task: task-5.2 - Configure Testcontainers Base Test

## Context
Base test configuration provides MySQL Testcontainer setup for all integration tests. Required by constraints.md Section 5.1.

## Prerequisites
- [ ] task-5.1 is complete
- [ ] Test CSV files exist

## Instructions
1. Create file `BaseIntegrationTest.java` in `src/test/java/org/example/etl2/batch/`
2. Annotate with:
   - `@SpringBootTest`
   - `@SpringBatchTest`
   - `@Testcontainers`
   - `@ActiveProfiles("test")`
3. Define MySQL container:
   ```java
   @Container
   static MySQLContainer<?> mysql = new MySQLContainer<>("mysql:8.0")
       .withDatabaseName("testdb")
       .withUsername("test")
       .withPassword("test");
   ```
4. Configure dynamic properties:
   ```java
   @DynamicPropertySource
   static void configureProperties(DynamicPropertyRegistry registry) {
       registry.add("spring.datasource.url", mysql::getJdbcUrl);
       registry.add("spring.datasource.username", mysql::getUsername);
       registry.add("spring.datasource.password", mysql::getPassword);
   }
   ```
5. Inject `JobLauncherTestUtils` for job execution
6. Inject `JdbcTemplate` for database verification

## Constraints (from spec)
- MUST: Use Testcontainers with MySQL (constraints.md Section 5.1)
- MUST: Use `@SpringBatchTest` annotation (constraints.md Section 5.1)
- MUST: Use `JobLauncherTestUtils` (constraints.md Section 5.1)
- MUST NOT: Use H2 or in-memory database (constraints.md Section 5.1)
- MUST NOT: Mock database interactions (constraints.md Section 5.1)

## Files to Create/Modify
| File | Action | Purpose |
|------|--------|---------|
| `src/test/java/org/example/etl2/batch/BaseIntegrationTest.java` | CREATE | Base test configuration |

## Acceptance Criteria
- [ ] File exists at specified path
- [ ] Uses `@SpringBootTest` and `@SpringBatchTest`
- [ ] Uses `@Testcontainers` annotation
- [ ] Defines `MySQLContainer` with version 8.0
- [ ] Configures dynamic datasource properties
- [ ] Injects `JobLauncherTestUtils`
- [ ] Compiles without errors

## Rollback
If this task fails validation:
1. Delete `src/test/java/org/example/etl2/batch/BaseIntegrationTest.java`
