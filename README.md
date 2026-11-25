# kubernetes-devops-security

This repo is retired.

Please use [this repo](https://github.com/kodekloudhub/devsecops) for the DevSecOps course.



## **1. Who creates which files?**

Your project has two important types of files:

**Developer – Application source code**

`src/main/java/com/devsecops/`

- `NumericApplication.java`
- `NumericController.java`

The developer implements the business logic and application APIs.

**Developer / QA – Test code**

`src/test/java/com/devsecops/`

\- `NumericApplicationTests.java`

Contains automated tests to verify that the application behaves as expected.

One important distinction: `NumericApplicationTests.java` is not necessarily written only by QA. In many teams, developers write unit tests as part of development, and QA engineers write additional automated tests.

## **2. How does it work in a real company?**

Imagine your team has three people.

| Role            | Responsibility                                    |
| --------------- | ------------------------------------------------- |
| Developer       | Builds Numeric application                        |
| QA engineer     | Validates application functionality               |
| DevOps engineer | Automates build, testing, scanning and deployment |

The workflow looks like this:

**Developer writes application code**

NumericController.java

**Developer pushes code to GitHub**

Feature branch → Pull Request

**QA / Developer writes automated tests**

NumericApplicationTests.java

**Jenkins CI pipeline starts**

**Maven executes tests + JaCoCo measures coverage**

**SonarQube + security scanning**

**Build artifact → Docker image → Kubernetes deployment**

## **3. Example from your Numeric application**

Suppose the developer implements this API:

```
@GetMapping("/compare/{value}")
public String compareToFifty(@PathVariable int value) {

    if (value > 50) {
        return "Greater than 50";
    }

    return "Smaller than or equal to 50";
}
```

The QA engineer needs to validate different scenarios.

| Input | Expected output             |
| ----- | --------------------------- |
| 75    | Greater than 50             |
| 25    | Smaller than or equal to 50 |
| 50    | Smaller than or equal to 50 |
| 100   | Greater than 50             |

QA writes automated test cases to verify these scenarios.

For example:

```
@Test
void testCompareGreaterThanFifty() {

    String response = testRestTemplate.getForObject(
        getBaseUrl() + "/compare/75",
        String.class
    );

    assertEquals("Greater than 50", response);
}
```

When Jenkins runs:

```
mvn clean verify
```

Maven compiles the application, executes the test cases, and JaCoCo measures which code was executed.

If a test fails, Maven reports a failure and Jenkins can stop the pipeline.

## **4. What happens if the developer changes the code?**

Imagine the developer accidentally changes:

```
if (value > 50)
```

to:

```
if (value >= 50)
```

Now the application returns `Greater than 50` for input `50`.

But your existing test expects:

```
Smaller than or equal to 50
```

The test fails.



Pipeline failure

&#x20;Jenkins identifies the regression before deployment, assuming the test stage is configured as a required pipeline stage.



This is one of the biggest advantages of automated testing.

## **5. How the teams collaborate through GitHub**

A typical repository structure is:

```
kubernetes-devops-security/
│
├── src/
│   ├── main/
│   │   ├── java/com/devsecops/
│   │   │   ├── NumericApplication.java
│   │   │   └── NumericController.java
│   │   └── resources/
│   │       └── application.properties
│   │
│   └── test/
│       └── java/com/devsecops/
│           └── NumericApplicationTests.java
│
├── pom.xml
├── Jenkinsfile
├── Dockerfile
└── kubernetes/
```

Developers and QA engineers can contribute to the same Git repository using separate branches and pull requests.

## **6. Important DevOps concept: Who owns testing?**

| Testing type          | Typical owner                |
| --------------------- | ---------------------------- |
| Unit testing          | Developer                    |
| Integration testing   | Developer / QA               |
| API testing           | QA / Developer               |
| Functional testing    | QA                           |
| Regression testing    | QA                           |
| Performance testing   | Performance engineering / QA |
| Security testing      | DevSecOps / Security         |
| Production monitoring | DevOps / SRE                 |

The exact division varies between companies.

## **7. Let's simulate a real company workflow in your lab**

I recommend that we now create separate Git branches to simulate the actual team collaboration:

- `feature/numeric-api` – Developer branch.
- `qa/numeric-tests` – QA branch.
- `main` – Production-ready code.

Then we configure Jenkins to automatically build and test whenever a pull request is created or code is merged.

**This will help you understand how a 3-person development, QA and DevOps team works together in a real CI/CD environment.**