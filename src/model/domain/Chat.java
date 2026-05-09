package model.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import utils.FieldValidator;

public class Chat extends SerializableModel {

    private static final long serialVersionUID = 1L;

    private User memberOne;
    private User memberTwo;
    private List<Message> messages = new ArrayList<>();

    public Chat(User memberOne, User memberTwo) {
        FieldValidator.requireNonNull(memberOne, "Member one");
        FieldValidator.requireNonNull(memberTwo, "Member two");
        if (memberOne.equals(memberTwo)) {
            throw new IllegalArgumentException("Chat members must be distinct users");
        }
        this.memberOne = memberOne;
        this.memberTwo = memberTwo;
    }

    public User getMemberOne() {
        return memberOne;
    }

    public void setMemberOne(User memberOne) {
        FieldValidator.requireNonNull(memberOne, "Member one");
        this.memberOne = memberOne;
    }

    public User getMemberTwo() {
        return memberTwo;
    }

    public void setMemberTwo(User memberTwo) {
        FieldValidator.requireNonNull(memberTwo, "Member two");
        this.memberTwo = memberTwo;
    }

    public List<Message> getMessages() {
        return List.copyOf(messages);
    }

    public void addMessage(Message message) {
        FieldValidator.requireNonNull(message, "Message");
        this.messages.add(message);
    }

    public boolean removeMessage(int messageId) {
        return messages.removeIf(m -> m.getId() == messageId);
    }

    public boolean removeMessage(Message message) {
        FieldValidator.requireNonNull(message, "Message");
        if (message.getId() != 0) {
            return removeMessage(message.getId());
        }
        return messages.remove(message);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Chat chat = (Chat) o;
        if (id != 0 && chat.getId() != 0) {
            return id == chat.getId();
        }
        return Objects.equals(memberOne, chat.memberOne) && Objects.equals(memberTwo, chat.memberTwo);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(memberOne, memberTwo);
    }

    @Override
    public String toString() {
        return "Chat{" +
                "id=" + id +
                ", memberOne=" + memberOne +
                ", memberTwo=" + memberTwo +
                ", messages=" + messages +
                '}';
    }
}
