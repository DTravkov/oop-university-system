package model.domain;

import java.util.Date;
import java.util.Objects;

public class Comment extends SerializableModel{
	
    private static final long serialVersionUID = 1L;

    private int senderId;
	private final String content;
    private final Date sentDate;

	public Comment(int senderId, String content) {
        this.senderId = senderId;
        this.content = content;
        this.sentDate = new Date();
	}

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public String getContent() {
        return content;
    }

    public Date getSentDate() {
        return sentDate;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Comment other = (Comment) obj;
        if (id != 0 || other.id != 0) {
            return id != 0 && id == other.id;
        }
        return senderId == other.senderId && Objects.equals(content, other.content);
    }

    @Override
    public int hashCode() {
        if (id != 0) {
            return Integer.hashCode(id);
        }
        return Objects.hash(senderId, content);
    }

    @Override
    public String toString() {
        return "Comment[id=" + id + ", senderId=" + senderId + ", content=" + content + ",sentDate=" + sentDate + "]";
    }


    


    

    

    

	
}