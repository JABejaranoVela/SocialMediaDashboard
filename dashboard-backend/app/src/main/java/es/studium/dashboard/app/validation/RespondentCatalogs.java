package es.studium.dashboard.app.validation;

import java.util.Set;

public final class RespondentCatalogs {
    public static final Set<String> GENDERS = Set.of("Male", "Female");
    public static final Set<String> RELATIONSHIPS = Set.of(
            "Single", "Married", "Divorced", "In a relationship");
    public static final Set<String> OCCUPATIONS = Set.of(
            "University Student", "School Student", "Salaried Worker", "Retired", "Unemployed");
    public static final Set<String> SOCIAL_MEDIA_USE = Set.of("Yes", "No");
    public static final Set<String> DAILY_TIMES = Set.of(
            "Less than an Hour",
            "Between 1 and 2 hours",
            "Between 2 and 3 hours",
            "Between 3 and 4 hours",
            "Between 4 and 5 hours",
            "More than 5 hours");
    public static final Set<String> PLATFORMS = Set.of(
            "Facebook", "Twitter", "Instagram", "YouTube", "Discord",
            "Reddit", "Pinterest", "TikTok", "Snapchat");
    public static final Set<String> ORGANIZATIONS = Set.of(
            "Company",
            "Company, Private",
            "Goverment",
            "N/A",
            "Private",
            "School",
            "School, Company",
            "School, N/A",
            "School, Private",
            "School, University",
            "School, University, Private",
            "University",
            "University, Company",
            "University, Company, Goverment",
            "University, Company, Private",
            "University, Goverment",
            "University, Goverment, Private",
            "University, N/A",
            "University, Private");

    private RespondentCatalogs() {
    }
}
