package model.enumeration;

public enum UIMessages {

    TITLE_AUTH("title.auth"),
    TITLE_ADMIN("title.admin"),
    TITLE_USER("title.user"),
    TITLE_ENROLL("title.enroll"),
    TITLE_MSG("title.msg"),

    CHOOSE("menu.choose"),
    EXIT("menu.exit"),

    CHANGE_LANG("auth.change_lang"),
    SIGN_UP("auth.sign_up"),
    LOG_IN("auth.log_in"),
    GOODBYE("auth.goodbye"),
    WELCOME("auth.welcome"),

    LOGIN("input.login"),
    PASSWORD("input.password"),
    ROLE("input.role"),
    USER_ROLE("input.user_role"),
    NAME("input.name"),
    SURNAME("input.surname"),
    TARGET_LOGIN("input.target_login"),
    NEW_NAME("input.new_name"),
    NEW_SURNAME("input.new_surname"),
    NEW_PASSWORD("input.new_password"),
    BAN("input.ban"),
    COURSE_NAME("input.course_name"),
    COURSE_DESC("input.course_desc"),
    COURSE_CREDITS("input.course_credits"),
    COURSE_TYPE("input.course_type"),
    TEACHER_TYPE("input.teacher_type"),
    COURSE_TEACHER_TYPE("input.course_teacher_type"),
    EMPLOYEE_ID("input.employee_id"),
    STUDENT_ID("input.student_id"),
    LECTURER_ID("input.lecturer_id"),
    TEACHER_ID("input.teacher_id"),
    PRACTICE_ID("input.practice_id"),
    ENROLLMENT_ID("input.enrollment_id"),
    POINTS_TO_ADD("input.points_to_add"),
    COURSE_ID("input.course_id"),
    SENDER_ID("input.sender_id"),
    RECEIVER_ID("input.receiver_id"),
    MESSAGE_ID("input.message_id"),
    MESSAGE_CONTENT("input.message_content"),

    COMPLAINT_LEVEL("input.complaint_level"),

    UPDATE_USER("action.update_user"),
    DELETE_USER("action.delete_user"),
    CREATE_ADMIN("action.create_admin"),
    CREATE_STUDENT("action.create_student"),
    CREATE_TEACHER("action.create_teacher"),
    CREATE_MANAGER("action.create_manager"),
    CREATE_SUPPORT("action.create_support"),
    CREATE_COURSE("action.create_course"),
    ENROLL("action.enroll"),
    DROP("action.drop"),
    VIEW_ALL("action.view_all"),
    VIEW_STUDENT("action.view_student"),
    SEND_MSG("action.send_msg"),
    INBOX("action.inbox"),
    DELETE_MSG("action.delete_msg"),

    INVALID_CHOICE("msg.invalid_choice"),
    INPUT_EMPTY("msg.input_empty"),
    INPUT_NUMBER("msg.input_number"),
    INPUT_RANGE("msg.input_range"),
    INPUT_YES_NO("msg.input_yes_no"),
    REGISTERED("msg.registered"),
    CREATED("msg.created"),
    DELETED("msg.deleted"),
    SENT("msg.sent"),

    ERR_OPERATION_NOT_ALLOWED("operation_not_allowed"),
    ERR_ALREADY_EXISTS("already_exists"),
    ERR_DOES_NOT_EXIST("does_not_exist"),
    ERR_INVALID_CREDENTIALS("invalid_credentials"),
    ERR_FIELD_REQUIRED("field_required"),
    ERR_FIELD_POSITIVE("field_positive"),
    ERR_FIELD_NON_NULL("field_non_null"),
    ERR_FIELD_SINGLE_WORD("field_single_word"),
    ERR_FIELD_IN_RANGE("field_in_range"),
    ERR_IMMUTABLE_ID("err.immutable_id");

    private final String key;

    UIMessages(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
