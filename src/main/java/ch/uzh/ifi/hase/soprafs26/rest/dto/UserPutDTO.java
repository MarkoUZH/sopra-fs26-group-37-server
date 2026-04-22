package ch.uzh.ifi.hase.soprafs26.rest.dto;

public class UserPutDTO {
    private String username;
    private String password;
    private String name;
    private String language;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name ; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }
}