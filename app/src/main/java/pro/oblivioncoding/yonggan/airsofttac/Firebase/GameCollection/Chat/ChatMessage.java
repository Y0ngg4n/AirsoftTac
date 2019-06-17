package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat;

public class ChatMessage {

    private String nickName, email, text;

    public ChatMessage() {
    }

    public ChatMessage(String nickName, String email, String text) {
        this.nickName = nickName;
        this.email = email;
        this.text = text;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
