package com.Exam.Backend.Config;

import com.Exam.Backend.Model.Exam.Category;
import com.Exam.Backend.Model.Exam.Question;
import com.Exam.Backend.Model.Exam.Quiz;
import com.Exam.Backend.Model.Exam.quizResult;
import com.Exam.Backend.Model.Role;
import com.Exam.Backend.Model.User;
import com.Exam.Backend.Model.questionPaper.QuestionPaper;
import com.Exam.Backend.Model.questionPaper.parentQuestion;
import com.Exam.Backend.Model.questionPaper.subQuestion;
import com.Exam.Backend.Model.userRole;
import com.Exam.Backend.Repository.categoryRepository;
import com.Exam.Backend.Repository.questionPaper.parentQuestionRepository;
import com.Exam.Backend.Repository.questionPaper.questionPaperRepository;
import com.Exam.Backend.Repository.questionPaper.subQuestionRepository;
import com.Exam.Backend.Repository.questionRepository;
import com.Exam.Backend.Repository.quizRepository;
import com.Exam.Backend.Repository.quizResultRepository;
import com.Exam.Backend.Repository.roleRepository;
import com.Exam.Backend.Repository.userRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class DataSeeder implements ApplicationRunner {

    private final userRepository userRepository;
    private final roleRepository roleRepository;
    private final categoryRepository categoryRepository;
    private final quizRepository quizRepository;
    private final questionRepository questionRepository;
    private final quizResultRepository quizResultRepository;
    private final questionPaperRepository questionPaperRepository;
    private final parentQuestionRepository parentQuestionRepository;
    private final subQuestionRepository subQuestionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(
            userRepository userRepository,
            roleRepository roleRepository,
            categoryRepository categoryRepository,
            quizRepository quizRepository,
            questionRepository questionRepository,
            quizResultRepository quizResultRepository,
            questionPaperRepository questionPaperRepository,
            parentQuestionRepository parentQuestionRepository,
            subQuestionRepository subQuestionRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.categoryRepository = categoryRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.quizResultRepository = quizResultRepository;
        this.questionPaperRepository = questionPaperRepository;
        this.parentQuestionRepository = parentQuestionRepository;
        this.subQuestionRepository = subQuestionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        Role adminRole = getOrCreateRole("ADMIN");
        Role normalRole = getOrCreateRole("NORMAL");

        User admin = upsertUser("admin", "Admin@123", "Super", "Admin", "super.admin@eduquiz.local", "0600000001", adminRole);
        User secondAdmin = upsertUser("admin2", "Admin2@123", "Exam", "Manager", "exam.manager@eduquiz.local", "0600000002", adminRole);
        User student1 = upsertUser("john", "John@123", "John", "Doe", "john.doe@eduquiz.local", "0600000003", normalRole);
        User student2 = upsertUser("sara", "Sara@123", "Sara", "Smith", "sara.smith@eduquiz.local", "0600000004", normalRole);
        User student3 = upsertUser("amine", "Amine@123", "Amine", "Bennani", "amine.bennani@eduquiz.local", "0600000005", normalRole);

        Category javaCategory = getOrCreateCategory(
                "Java",
                "Java and Spring fundamentals, collections, OOP and API basics."
        );
        Category dbCategory = getOrCreateCategory(
                "Database",
                "SQL, relational modeling, normalization, joins and transaction basics."
        );
        Category webCategory = getOrCreateCategory(
                "Web Development",
                "HTTP, REST APIs, frontend integration and secure web application principles."
        );

        Quiz javaQuiz = getOrCreateQuiz(
                "Java Basics Challenge",
                "Quick assessment of Java syntax and core programming concepts.",
                "20",
                "5",
                true,
                javaCategory
        );
        Quiz sqlQuiz = getOrCreateQuiz(
                "SQL Essentials Quiz",
                "Evaluate understanding of SQL querying and relational concepts.",
                "20",
                "5",
                true,
                dbCategory
        );
        Quiz webQuiz = getOrCreateQuiz(
                "Web Fundamentals Check",
                "Assess basics of HTTP, REST and frontend-backend communication.",
                "20",
                "5",
                true,
                webCategory
        );

        seedQuestionsForQuiz(javaQuiz, List.of(
                makeQuestion("What is the default value of an int field in Java?", "0", "null", "1", "undefined", "0"),
                makeQuestion("Which collection does not allow duplicate values?", "List", "Set", "Queue", "Map", "Set"),
                makeQuestion("Which keyword is used to inherit a class?", "implement", "extends", "inherits", "super", "extends"),
                makeQuestion("Which access modifier makes a member visible only inside the same class?", "protected", "public", "private", "default", "private"),
                makeQuestion("What does JVM stand for?", "Java Virtual Machine", "Java Variable Method", "Join Virtual Module", "Java Verified Mode", "Java Virtual Machine")
        ));

        seedQuestionsForQuiz(sqlQuiz, List.of(
                makeQuestion("Which SQL command is used to retrieve data?", "SELECT", "GET", "FETCH", "SHOW", "SELECT"),
                makeQuestion("Which join returns only matching records from both tables?", "LEFT JOIN", "RIGHT JOIN", "INNER JOIN", "FULL JOIN", "INNER JOIN"),
                makeQuestion("Which clause is used to filter grouped results?", "WHERE", "GROUP BY", "HAVING", "ORDER BY", "HAVING"),
                makeQuestion("What is a primary key?", "A nullable identifier", "A unique row identifier", "A foreign table reference", "A sorted index only", "A unique row identifier"),
                makeQuestion("Which normal form removes transitive dependency?", "1NF", "2NF", "3NF", "BCNF", "3NF")
        ));

        seedQuestionsForQuiz(webQuiz, List.of(
                makeQuestion("What HTTP method is typically used to create a resource?", "GET", "POST", "PUT", "DELETE", "POST"),
                makeQuestion("Which status code means Not Found?", "200", "201", "404", "500", "404"),
                makeQuestion("What does REST stand for?", "Remote Execution State Transfer", "Representational State Transfer", "Resource Event Service Template", "Rendered Service Transfer", "Representational State Transfer"),
                makeQuestion("Which header is commonly used to send JWT token?", "Authorization", "X-Auth-Key", "Cookie", "Accept", "Authorization"),
                makeQuestion("What does CORS control?", "Database access", "Cross-origin browser requests", "Code compilation", "Server memory allocation", "Cross-origin browser requests")
        ));

        getOrCreateQuizResult(admin.getUsername(), javaQuiz, 20, 5, 5);
        getOrCreateQuizResult(secondAdmin.getUsername(), sqlQuiz, 18, 5, 4);
        getOrCreateQuizResult(student1.getUsername(), javaQuiz, 16, 5, 4);
        getOrCreateQuizResult(student2.getUsername(), sqlQuiz, 14, 5, 3);
        getOrCreateQuizResult(student3.getUsername(), webQuiz, 18, 5, 4);

        seedQuestionPaperData();
    }

    private Role getOrCreateRole(String roleName) {
        return roleRepository.findAll().stream()
                .filter(role -> roleName.equalsIgnoreCase(role.getRoleName()))
                .findFirst()
                .orElseGet(() -> {
                    Role role = new Role();
                    role.setRoleName(roleName);
                    return roleRepository.save(role);
                });
    }

    private User upsertUser(
            String username,
            String plainPassword,
            String firstName,
            String lastName,
            String email,
            String phone,
            Role role
    ) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            user = new User();
            user.setUsername(username);
            user.setPassword(passwordEncoder.encode(plainPassword));
            user.setEnabled(true);
            user.setProfile("default.png");
        }
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPhone(phone);

        user.getUserRoles().clear();
        userRole link = new userRole();
        link.setUser(user);
        link.setRole(role);
        user.getUserRoles().add(link);
        return userRepository.save(user);
    }

    private Category getOrCreateCategory(String title, String description) {
        return categoryRepository.findAll().stream()
                .filter(category -> title.equalsIgnoreCase(category.getTitle()))
                .findFirst()
                .map(existing -> {
                    existing.setDescription(description);
                    return categoryRepository.save(existing);
                })
                .orElseGet(() -> {
                    Category category = new Category();
                    category.setTitle(title);
                    category.setDescription(description);
                    return categoryRepository.save(category);
                });
    }

    private Quiz getOrCreateQuiz(
            String title,
            String description,
            String maxMarks,
            String numberOfQuestions,
            boolean active,
            Category category
    ) {
        return quizRepository.findAll().stream()
                .filter(quiz -> title.equalsIgnoreCase(quiz.getTitle()))
                .findFirst()
                .map(existing -> {
                    existing.setDescription(description);
                    existing.setMaxMarks(maxMarks);
                    existing.setNumberOfQuestions(numberOfQuestions);
                    existing.setActive(active);
                    existing.setCategory(category);
                    return quizRepository.save(existing);
                })
                .orElseGet(() -> {
                    Quiz quiz = new Quiz();
                    quiz.setTitle(title);
                    quiz.setDescription(description);
                    quiz.setMaxMarks(maxMarks);
                    quiz.setNumberOfQuestions(numberOfQuestions);
                    quiz.setActive(active);
                    quiz.setCategory(category);
                    return quizRepository.save(quiz);
                });
    }

    private Question makeQuestion(String content, String option1, String option2, String option3, String option4, String answer) {
        Question question = new Question();
        question.setContent(content);
        question.setOption1(option1);
        question.setOption2(option2);
        question.setOption3(option3);
        question.setOption4(option4);
        question.setAnswer(answer);
        question.setImage("");
        return question;
    }

    private void seedQuestionsForQuiz(Quiz quiz, List<Question> templateQuestions) {
        List<Question> existing = questionRepository.findByQuiz(quiz);
        if (existing.size() >= templateQuestions.size()) {
            return;
        }

        for (Question question : templateQuestions) {
            question.setQuiz(quiz);
        }
        questionRepository.saveAll(templateQuestions);
    }

    private void getOrCreateQuizResult(String username, Quiz quiz, int marksGot, int attempted, int correctAnswers) {
        if (quizResultRepository.findByQuizAndUsername(quiz, username).isPresent()) {
            return;
        }
        quizResult result = new quizResult();
        result.setQuiz(quiz);
        result.setUsername(username);
        result.setMarksGot(marksGot);
        result.setAttempted(attempted);
        result.setCorrectAnswers(correctAnswers);
        quizResultRepository.save(result);
    }

    private void seedQuestionPaperData() {
        if (questionPaperRepository.count() > 0) {
            return;
        }

        QuestionPaper paper = new QuestionPaper();
        paper.setCollegeName("National School of Technology");
        paper.setInstitutionName("EduQuiz University");
        paper.setDepartment("Computer Science");
        paper.setExamType("Final Exam");
        paper.setSemester("S6");
        paper.setTest("Session 1");
        paper.setSubjectCode("CS-431");
        paper.setFaculty("Dr. Nadia Amrani");
        paper.setTime("2h");
        paper.setNote("Answer all questions and show intermediate reasoning where needed.");
        paper.setMaxMarks(40);
        paper.setCo1("Analyze");
        paper.setCo2("Design");
        paper.setCo3("Implement");
        paper.setCo4("Evaluate");
        paper = questionPaperRepository.save(paper);

        parentQuestion p1 = new parentQuestion();
        p1.setQuestionPaper(paper);
        p1.setMarks(10);
        p1.setCo("CO1");
        p1.setBtl("BTL3");
        p1.setQuestionContent("Explain the main principles of object-oriented programming with examples.");
        p1 = parentQuestionRepository.save(p1);

        parentQuestion p2 = new parentQuestion();
        p2.setQuestionPaper(paper);
        p2.setMarks(10);
        p2.setCo("CO2");
        p2.setBtl("BTL4");
        p2.setQuestionContent("Design a normalized schema for an online quiz platform.");
        p2 = parentQuestionRepository.save(p2);

        Map<parentQuestion, List<subQuestion>> subQuestionMap = new HashMap<>();
        subQuestionMap.put(p1, List.of(
                buildSubQuestion(p1, 5, "CO1", "BTL2", "Define encapsulation and abstraction."),
                buildSubQuestion(p1, 5, "CO1", "BTL3", "Compare inheritance and composition with a practical example.")
        ));
        subQuestionMap.put(p2, List.of(
                buildSubQuestion(p2, 5, "CO2", "BTL3", "Identify entities and relationships for quizzes, questions and results."),
                buildSubQuestion(p2, 5, "CO2", "BTL4", "Propose keys and constraints to preserve data integrity.")
        ));

        for (Map.Entry<parentQuestion, List<subQuestion>> entry : subQuestionMap.entrySet()) {
            subQuestionRepository.saveAll(entry.getValue());
        }
    }

    private subQuestion buildSubQuestion(parentQuestion parent, int marks, String co, String btl, String content) {
        subQuestion child = new subQuestion();
        child.setParentQuestion(parent);
        child.setMarks(marks);
        child.setCo(co);
        child.setBtl(btl);
        child.setQuestionContent(content);
        return child;
    }
}
