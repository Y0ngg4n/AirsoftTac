package pro.oblivioncoding.yonggan.airsofttac.Firebase.GameCollection.Chat;

public class ChatMessage {

    private String nickName, email, text;

    public ChatMessage() {
    }

    public ChatMessage(final String nickName, final String email, final String text) {
        this.nickName = nickName;
        this.email = email;
        this.text = text;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(final String nickName) {
        this.nickName = nickName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(final String email) {
        this.email = email;
    }

    public String getText() {
        return text;
    }

    public void setText(final String text) {
        this.text = text;
    }
}
