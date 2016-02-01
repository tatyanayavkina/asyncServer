package utils;

public class UserCredentials {
    private String name;
    private String password;

    public UserCredentials(String name, String password){
        this.name = name;
        this.password = password;
    }

    public String getName(){
        return name;
    }

    public String getPassword(){
        return password;
    }
}
