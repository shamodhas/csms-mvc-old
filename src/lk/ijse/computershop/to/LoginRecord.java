package lk.ijse.computershop.to;

import java.time.LocalDateTime;

public class LoginRecord {
    private String loginId;
    private LocalDateTime dateTime;
    private String userId;

    public LoginRecord() {
    }

    public LoginRecord(String loginId, LocalDateTime dateTime, String userId) {
        this.loginId = loginId;
        this.dateTime = dateTime;
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
