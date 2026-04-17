package model;

public enum UIMessages {

    // =========================
    // Titles / Menus
    // =========================
    ADMIN_TITLE("admin_title"),
    AUTH_TITLE("auth_title"),

    AUTH_CHANGE_LANG("auth_change_lang"),
    AUTH_SIGNUP("auth_signup"),
    AUTH_LOGIN("auth_login"),
    AUTH_EXIT("auth_exit"),

    ADMIN_UPDATE("admin_update"),
    ADMIN_DELETE("admin_delete"),
    ADMIN_REG_ADMIN("admin_reg_admin"),
    ADMIN_REG_STUDENT("admin_reg_student"),
    ADMIN_REG_COURSE("admin_reg_course"),
    ADMIN_EXIT("admin_exit"),

    ADMIN_PICK_OPTION("admin_pick_option"),

    // =========================
    // Auth Prompts
    // =========================
    AUTH_PROMPT_LOGIN("auth_prompt_login"),
    AUTH_PROMPT_PASS("auth_prompt_pass"),
    AUTH_PROMPT_NAME("auth_prompt_name"),
    AUTH_PROMPT_SURNAME("auth_prompt_surname"),

    // =========================
    // Admin Prompts
    // =========================
    ADMIN_PROMPT_TARGET_LOGIN("admin_prompt_target_login"),
    ADMIN_PROMPT_DELETE("admin_prompt_delete"),

    ADMIN_NEW_NAME("admin_new_name"),
    ADMIN_NEW_SURNAME("admin_new_surname"),
    ADMIN_NEW_PASS("admin_new_pass"),

    ADMIN_BAN_CHOICE("admin_ban_choice"),

    // =========================
    // Course
    // =========================
    ADMIN_COURSE_NAME_PROMPT("admin_course_name_prompt"),
    ADMIN_COURSE_CREDITS_PROMPT("admin_course_credits_prompt"),
    ADMIN_COURSE_TYPE_SELECTION("admin_course_type_selection"),
    ADMIN_COURSE_DESCRIPTION_PROMPT("admin_course_description_prompt"),

    // =========================
    // Messages
    // =========================
    ADMIN_REG_SUCCESS("admin_reg_success"),
    AUTH_ACCESS_GRANTED("auth_access_granted"),
    AUTH_GOODBYE("auth_goodbye"),

    // =========================
    // Errors
    // =========================
    ERROR_APP_INTERNAL("error_app_internal"),
    ERROR_FIELD_VALIDATION("error_field_validation"),

    // =========================
    // Exceptions
    // =========================
    EX_ALREADY_EXISTS("ex_already_exists"),
    EX_DOES_NOT_EXIST("ex_does_not_exist"),
    EX_INVALID_CREDENTIALS("ex_invalid_credentials"),
    EX_FIELD_VALIDATION("ex_field_validation"),
	
	INPUT_EMPTY("input_empty"),
	INPUT_NUMBER_EXPECTED("input_number_expected"),
	INPUT_RANGE_ERROR("input_range_error"),
	INPUT_YES_NO_EXPECTED("input_yes_no_expected");
	
	
	
    private final String key;

    UIMessages(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}