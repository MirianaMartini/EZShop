package it.polito.ezshop.data;

import java.util.Date;

public class UserImpl implements User{

    private Integer id;
    private String username;
    private String password;
    private String role;

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String getRole() {
        return role;
    }

    @Override
    public void setRole(String role) {
        this.role = role;
    }

    public UserImpl(Integer id, String username, String password, String role){
        this.username = username;
        this.password = password;
        this.role = role;
        this.id = id;
    }
}
