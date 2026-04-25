package model.enumeration;

public enum UIMessages {

    MENU_TITLE_AUTH("menu.title_auth"),
    MENU_TITLE_ADMIN("menu.title_admin"),
    MENU_TITLE_USER("menu.title_user"),
    MENU_TITLE_ENROLL("menu.title_enroll"),
    MENU_TITLE_MSG("menu.title_msg"),
    MENU_TITLE_ORG("menu.title_org"),

    MENU_CHOOSE("menu.choose"),
    MENU_EXIT("menu.exit"),

    AUTH_CHANGE_LANG("auth.change_lang"),
    AUTH_SIGN_UP("auth.sign_up"),
    AUTH_LOG_IN("auth.log_in"),
    AUTH_GOODBYE("auth.goodbye"),
    AUTH_WELCOME("auth.welcome"),

    INPUT_LOGIN("input.login"),
    INPUT_PASSWORD("input.password"),
    INPUT_ROLE("input.role"),
    INPUT_USER_ROLE("input.user_role"),
    INPUT_NAME("input.name"),
    INPUT_SURNAME("input.surname"),
    INPUT_TARGET_LOGIN("input.target_login"),
    INPUT_NEW_NAME("input.new_name"),
    INPUT_NEW_SURNAME("input.new_surname"),
    INPUT_NEW_PASSWORD("input.new_password"),
    INPUT_BAN("input.ban"),
    INPUT_COURSE_NAME("input.course_name"),
    INPUT_COURSE_DESC("input.course_desc"),
    INPUT_COURSE_CREDITS("input.course_credits"),
    INPUT_COURSE_TYPE("input.course_type"),
    INPUT_TEACHER_TYPE("input.teacher_type"),
    INPUT_COURSE_TEACHER_TYPE("input.course_teacher_type"),
    INPUT_EMPLOYEE_ID("input.employee_id"),
    INPUT_STUDENT_ID("input.student_id"),
    INPUT_LECTURER_ID("input.lecturer_id"),
    INPUT_TEACHER_ID("input.teacher_id"),
    INPUT_PRACTICE_ID("input.practice_id"),
    INPUT_ENROLLMENT_ID("input.enrollment_id"),
    INPUT_POINTS_TO_ADD("input.points_to_add"),
    INPUT_COURSE_ID("input.course_id"),
    INPUT_ORG_ID("input.org_id"),
    INPUT_SENDER_ID("input.sender_id"),
    INPUT_RECEIVER_ID("input.receiver_id"),
    INPUT_MESSAGE_ID("input.message_id"),
    INPUT_MESSAGE_CONTENT("input.message_content"),

    INPUT_COMPLAINT_LEVEL("input.complaint_level"),

    USER_UPDATE("user.update"),
    USER_DELETE("user.delete"),
    USER_CREATE_ADMIN("user.create_admin"),
    USER_CREATE_STUDENT("user.create_student"),
    USER_CREATE_TEACHER("user.create_teacher"),
    USER_CREATE_MANAGER("user.create_manager"),
    USER_CREATE_SUPPORT("user.create_support"),
    COURSE_CREATE("course.create"),
    ORG_CREATE("org.create"),
    ORG_GET_BY_ID("org.get_by_id"),
    ORG_DELETE("org.delete"),
    ORG_ADD_MEMBER("org.add_member"),
    ORG_REMOVE_MEMBER("org.remove_member"),
    ORG_SET_PRESIDENT("org.set_president"),
    ORG_REMOVE_PRESIDENT("org.remove_president"),
    ENROLL_CREATE("enroll.create"),
    ENROLL_DROP("enroll.drop"),
    MENU_VIEW_ALL("menu.view_all"),
    ENROLL_VIEW_STUDENT("enroll.view_student"),
    MSG_SEND("msg.send"),
    MSG_INBOX("msg.inbox"),
    MSG_DELETE("msg.delete"),

    MSG_INVALID_CHOICE("msg.invalid_choice"),
    MSG_INPUT_EMPTY("msg.input_empty"),
    MSG_INPUT_NUMBER("msg.input_number"),
    MSG_INPUT_RANGE("msg.input_range"),
    MSG_INPUT_YES_NO("msg.input_yes_no"),
    MSG_REGISTERED("msg.registered"),
    MSG_CREATED("msg.created"),
    MSG_DELETED("msg.deleted"),
    MSG_SENT("msg.sent"),
    MSG_ALL_ORGANIZATIONS("msg.all_organizations"),
    MSG_MEMBERS_IDS("msg.members_ids"),
    MSG_STUDENTS_HEADER("msg.students_header"),

    ERR_OPERATION_NOT_ALLOWED("err.operation_not_allowed"),
    ERR_ALREADY_EXISTS("err.already_exists"),
    ERR_DOES_NOT_EXIST("err.does_not_exist"),
    ERR_INVALID_CREDENTIALS("err.invalid_credentials"),
    ERR_FIELD_REQUIRED("err.field_required"),
    ERR_FIELD_POSITIVE("err.field_positive"),
    ERR_FIELD_NON_NULL("err.field_non_null"),
    ERR_FIELD_SINGLE_WORD("err.field_single_word"),
    ERR_FIELD_IN_RANGE("err.field_in_range"),
    ERR_IMMUTABLE_ID("err.immutable_id");

    private final String key;

    UIMessages(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
