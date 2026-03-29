package org.isNotNull.springBoardApp.bootstrap;

import org.isNotNull.springBoardApp.domain.*;
import org.isNotNull.springBoardApp.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

/**
 * Seeds demo data aligned with the frontend mock accounts and entities.
 *
 * Example:
 * The first application and demo credentials are available after startup.
 */
@Component
public final class DataSeed implements CommandLineRunner {

    private final TagRepo tags;
    private final CompanyRepo companies;
    private final UserRepo users;
    private final OpportunityRepo opportunities;
    private final ApplicationRepo applications;
    private final FavoriteRepo favorites;
    private final PasswordEncoder encoder;

    public DataSeed(
        final TagRepo tags,
        final CompanyRepo companies,
        final UserRepo users,
        final OpportunityRepo opportunities,
        final ApplicationRepo applications,
        final FavoriteRepo favorites,
        final PasswordEncoder encoder
    ) {
        this.tags = tags;
        this.companies = companies;
        this.users = users;
        this.opportunities = opportunities;
        this.applications = applications;
        this.favorites = favorites;
        this.encoder = encoder;
    }

    @Override
    public void run(final String... arguments) {
        if (this.users.count() > 0L) {
            return;
        }

        final TagEntity python = this.tags.save(new TagEntity("Python", TagCategory.TECHNOLOGY));
        final TagEntity java = this.tags.save(new TagEntity("Java", TagCategory.TECHNOLOGY));
        final TagEntity javascript = this.tags.save(new TagEntity("JavaScript", TagCategory.TECHNOLOGY));
        final TagEntity typescript = this.tags.save(new TagEntity("TypeScript", TagCategory.TECHNOLOGY));
        final TagEntity react = this.tags.save(new TagEntity("React", TagCategory.TECHNOLOGY));
        final TagEntity node = this.tags.save(new TagEntity("Node.js", TagCategory.TECHNOLOGY));
        final TagEntity sql = this.tags.save(new TagEntity("SQL", TagCategory.TECHNOLOGY));
        final TagEntity junior = this.tags.save(new TagEntity("Junior", TagCategory.LEVEL));
        final TagEntity middle = this.tags.save(new TagEntity("Middle", TagCategory.LEVEL));
        final TagEntity full = this.tags.save(new TagEntity("Full-time", TagCategory.EMPLOYMENT));

        final CompanyEntity techno = this.companies.save(new CompanyEntity(
            "ТехноЛаб",
            "7701234567",
            "1177746358590",
            "Москва, ул. Примерная, д. 1",
            "https://technolab.ru",
            null,
            "https://vk.com/technolab\n" +
                    "https://technolab.ru\n" +
                    "+79012312412412",
            "О компании: ТехноЛаб — инновационная IT-компания, занимаемся ПО и AI.",
            true,
            "hr@technolab.ru"
        ));
        final CompanyEntity data = this.companies.save(new CompanyEntity(
            "ДатаТех",
            "7712345678",
            "1177746358591",
            "Москва, ул. Данных, д. 2",
            "https://datatech.ru",
            null,
            "https://vk.com/technolab\n" +
                    "https://datatech.ru\n" +
                    "+7901231231321",
            "О компании: ДатаТех — аналитика данных и ML-решения.",
            true,
            "careers@datatech.ru"
        ));
        final CompanyEntity code = this.companies.save(new CompanyEntity(
            "КодМастер",
            null,
            null,
            "Москва, ул. Образовательная, д. 3",
            "https://codemaster.ru",
            null,
            "https://vk.com/codemaster\n" +
                    "https://codemaster.ru\n" +
                    "+79014981291",
            "О компании: КодМастер — образовательная платформа и разработка веб-приложений.",
            true,
            "jobs@codemaster.ru"
        ));
        final CompanyEntity startup = this.companies.save(new CompanyEntity(
            "СтартапХаб",
            null,
            null,
            "Москва, ул. Стартаповая, д. 4",
            "https://startuphub.ru",
            null,
            "https://vk.com/startuphub\n" +
                    "https://startuphub.ru\n" +
                    "+79014981291",
            "О компании: СтартапХаб — инкубатор стартапов.",
            false,
            "info@startuphub.ru"
        ));

        final UserEntity applicant = new UserEntity(
            "student@example.com",
            "student@example.com",
            "Иван Петров",
            this.encoder.encode("student123"),
            UserRole.APPLICANT
        ).profile(
            "Иван Петров",
            "Петров Иван Сергеевич",
            "МГУ им. М.В. Ломоносова",
            "3 курс",
            "2027",
            List.of("JavaScript", "React", "TypeScript", "Git"),
            List.of("https://github.com/ivanpetrov"),
            "Frontend developer with pet project experience",
            List.of()
        ).privacy(false, true);
        final UserEntity savedApplicant = this.users.save(applicant);

        final UserEntity employer = new UserEntity(
            "employer@technolab.ru",
            "employer@technolab.ru",
            "ТехноЛаб HR",
            this.encoder.encode("employer123"),
            UserRole.EMPLOYER
        ).company(techno.id())
        .profile(
            "ТехноЛаб HR",
            "ТехноЛаб HR",
            null,
            null,
            null,
            List.of(),
            List.of(),
            null,
            List.of()
        )
        .privacy(false, true);
        this.users.save(employer);

        this.users.save(new UserEntity(
            "admin@tramplin.ru",
            "admin@tramplin.ru",
            "Администратор",
            this.encoder.encode("admin123"),
            UserRole.CURATOR
        ));

        final OpportunityEntity first = this.opportunities.save(new OpportunityEntity(
            "Стажировка Junior Frontend Developer",
            "Ищем талантливого студента для стажировки в команде разработки",
            OpportunityType.INTERNSHIP,
            techno.id(),
            WorkFormat.HYBRID,
            "Москва",
            "ул. Ленина, 45",
            55.751244,
            37.618423,
            40000,
            60000,
            "RUB",
            Instant.parse("2026-03-10T10:00:00Z"),
            LocalDate.parse("2026-04-30"),
            null,
            "hr@technolab.ru",
            null,
            "https://technolab.ru/careers",
            List.of(javascript.id(), typescript.id(), react.id(), junior.id(), full.id()),
            OpportunityStatus.ACTIVE,
            "Студент 3-4 курса технического вуза, знание React, TypeScript, Git",
            List.of()
        ));

        this.opportunities.save(new OpportunityEntity(
            "Python Data Scientist",
            "Работа с большими данными, машинное обучение и разработка ML моделей",
            OpportunityType.VACANCY,
            data.id(),
            WorkFormat.REMOTE,
            "Санкт-Петербург",
            null,
            59.934280,
            30.335099,
            120000,
            200000,
            "RUB",
            Instant.parse("2026-03-12T10:00:00Z"),
            LocalDate.parse("2026-05-15"),
            null,
            "careers@datatech.ru",
            null,
            null,
            List.of(python.id(), sql.id(), middle.id(), full.id()),
            OpportunityStatus.ACTIVE,
            "Опыт работы с Python, pandas, scikit-learn, SQL",
            List.of()
        ));

        this.opportunities.save(new OpportunityEntity(
            "Менторская программа Путь в IT",
            "Персональный ментор для студентов младших курсов",
            OpportunityType.MENTORSHIP,
            code.id(),
            WorkFormat.HYBRID,
            "Москва",
            "ул. Тверская, 12",
            55.755814,
            37.617635,
            null,
            null,
            null,
            Instant.parse("2026-03-05T10:00:00Z"),
            LocalDate.parse("2026-04-01"),
            null,
            "jobs@codemaster.ru",
            null,
            null,
            List.of(java.id(), react.id(), node.id()),
            OpportunityStatus.ACTIVE,
            "Студенты 1-2 курса, интерес к веб разработке",
            List.of()
        ));

        this.opportunities.save(new OpportunityEntity(
            "Хакатон Код Будущего",
            "48 часовой хакатон по разработке AI решений",
            OpportunityType.EVENT,
            startup.id(),
            WorkFormat.OFFICE,
            "Казань",
            "Технопарк Идея, корпус 7",
            55.796127,
            49.106414,
            null,
            null,
            null,
            Instant.parse("2026-03-08T10:00:00Z"),
            null,
            LocalDate.parse("2026-04-20"),
            "info@startuphub.ru",
            null,
            "https://startuphub.ru/hackathon",
            List.of(python.id(), javascript.id(), react.id()),
            OpportunityStatus.PLANNED,
            "Студенты и выпускники вузов, команды до 5 человек",
            List.of()
        ));

        this.applications.save(new ApplicationEntity(
            first.id(),
            savedApplicant.id(),
            ApplicationStatus.PENDING,
            Instant.parse("2026-03-15T10:00:00Z"),
            "Здравствуйте! Заинтересован в стажировке. Имею опыт разработки на React."
        ));

        this.favorites.save(new FavoriteEntity(savedApplicant.id(), first.id()));
    }
}
