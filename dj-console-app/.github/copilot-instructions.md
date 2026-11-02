# GitHub Copilot Instructions

This repository uses the following defaults for all generated code and suggestions:

- Use Java 26 features and modern Java conventions (the parent pom sets `<java.version>26</java.version>`).
- Assume Spring Boot 4 unless the existing code in a specific module clearly requires otherwise.
- Prefer `var` for local variables within methods.
- Use static imports for AssertJ assertions in test classes.
- Follow the existing project style, structure, and naming conventions.
- Keep generated code concise, testable, and compatible with the Maven build in this repository.
- When adding tests, prefer AssertJ and the existing test fixtures and support classes already present in the repository.

