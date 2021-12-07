package it.polito.ezshop.data;

public interface User {

    Integer getId();

    void setId(Integer id);

    String getUsername();

    void setUsername(String username);

    String getPassword();

    void setPassword(String password);

    String getRole();

    void setRole(String role);
}
