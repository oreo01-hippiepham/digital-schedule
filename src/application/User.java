package application;

public class User {
    private String userName;
    private final String email;

    public User (String email)
    {
        this.email = email;
    }

    public String getUserName()
    {
        return userName;
    }

    public String getEmail()
    {
        return email;
    }

    public void setUserName(String userName)
    {
        this.userName = userName;
    }

}
