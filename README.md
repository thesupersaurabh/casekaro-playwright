# CaseKaro E2E Automation Framework

An enterprise-grade End-to-End (E2E) automation testing framework for the CaseKaro e-commerce platform.

## 🚀 Technology Stack
*   **Java 17**: Core programming language.
*   **Playwright (Java)**: Modern, blazing-fast browser automation library.
*   **Cucumber (BDD)**: Allows writing test scenarios in plain English (Gherkin).
*   **JUnit 4**: Test execution engine.
*   **Maven**: Dependency management and build tool.

## 📖 What This Framework Does (Plain English)
This project is built using **Behavior-Driven Development (BDD)**. This means the tests are designed so that anyone—even non-programmers—can read and understand exactly what is being automated.

When the test runs, the robot does the following:
1. Opens `casekaro.com`.
2. Navigates to the Mobile Covers section and searches for **Apple**.
3. Confirms that only Apple devices appear (and filters out Samsung, OnePlus, etc.).
4. Searches specifically for an **iPhone 16 Pro** case.
5. Adds three different materials (`Hard`, `Soft`, `Glass`) to the cart.
6. Opens the cart and verifies that there are exactly 3 items.
7. Extracts the final price, material, and URL of each item and prints it out.

By reading the `casekaro.feature` file, anyone can verify this exact business logic in plain English.

## 🏗️ Architecture Design (Page Object Model)
This project strictly adheres to the **Page Object Model (POM)** design pattern.
*   **Separation of Concerns**: HTML locators and page-specific logic are encapsulated in `src/test/java/pages/`.
*   **BDD Step Definitions**: English test steps are translated to Java methods in `src/test/java/stepdefinitions/`.
*   **Thread Safety**: Browser instances are isolated using `ThreadLocal` in `PlaywrightFactory.java` to support safe parallel execution.

## 📁 Project Structure
```text
casekaro-playwright/
├── src/test/java/
│   ├── pages/                 # Page Object classes (locators & UI actions)
│   ├── runner/                # JUnit TestRunner configuration
│   ├── stepdefinitions/       # Cucumber step definitions mapping BDD to Java
│   └── utils/                 # PlaywrightFactory (ThreadLocal browser init)
├── src/test/resources/
│   └── features/              # Cucumber .feature files (Gherkin scenarios)
├── Dockerfile                 # Containerized execution environment
└── pom.xml                    # Maven dependencies (Java 17 target)
```

## 🛠️ How to Run Locally

### Prerequisites
*   Java Development Kit (JDK) 17 or higher installed.
*   Maven installed.

### Execution
Open your terminal in the project root folder and run:
```bash
mvn clean test
```

### Viewing the Report
Once the test finishes, Cucumber automatically generates an HTML report.
Open `target/cucumber-reports.html` in your web browser to see the passed/failed steps.

---

## ☁️ Cloudflare Bot Protection Note
CaseKaro utilizes Cloudflare. If you run the tests in "headless" mode (invisible background process), Cloudflare may block the automation with a "Just a moment..." verification page.

To bypass this locally, the framework is currently configured to run in **headed mode**. 
*(See `PlaywrightFactory.java` -> `setHeadless(false)`).*

---

## 🐳 Docker (CI/CD Ready)
This project includes a `Dockerfile` built on the official Microsoft Playwright Java image. This demonstrates how the suite can be integrated into Jenkins, GitHub Actions, or GitLab CI.

**To build the image:**
```bash
docker build -t casekaro-automation .
```

**To run the container:**
```bash
docker run casekaro-automation
```
*(Note: Docker containers run headless by default. If testing against CaseKaro's live production site, the container may encounter the Cloudflare block mentioned above unless a bypass proxy is configured).*
