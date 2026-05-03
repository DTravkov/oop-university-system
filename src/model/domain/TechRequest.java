package model.domain;

import model.enumeration.TechRequestStatus;

public class TechRequest extends Message {

    private TechRequestStatus status;

    public TechRequest(int senderId, int receiverId, String content) {
        super(senderId, receiverId, content);
        this.status = TechRequestStatus.PENDING;
    }

    public TechRequestStatus getStatus() {
        return status;
    }

    public void setStatus(TechRequestStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "TechRequest [" + ", id=" + id
                + ", from=" + getSenderId() + ", to=" + getReceiverId() 
                + ", content=" + getContent() 
                + "status=" + status + "]";

    }

    

    

}
