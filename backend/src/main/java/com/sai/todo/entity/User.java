package com.sai.todo.entity;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;



import org.springframework.security.core.authority.SimpleGrantedAuthority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.OneToMany;


@Entity
public class User  implements UserDetails{

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;

  @Column(unique = true,nullable = false)
    private String username;
@Column(nullable = false)
    private String password;
    @Column(nullable = false)
    private String role;
    private int tokenVersion=0;

    @OneToMany(
        mappedBy="user",
          fetch = FetchType.LAZY

    )
   private List<Todo> todos;
     @Column(unique=true,nullable=false)
     private String email;



    public User(){

    }

    public User(int id,String username,String password){
        this.id=id;
        this.username=username;
        this.password=password;
    }
    
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id=id;
    }

    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String password){
        this.password=password;
    }
    public String getRole(){
        return role;
    }
    public void setRole(String role){
        this.role=role;
    }
    public List<Todo> getTodos() {
    return todos;
    }
    public String getEmail(){
        return email;
    }
    public void setEmail(String email){
        this.email=email;
    }
    public int getTokenVersion(){
        return tokenVersion;
    }
    public void setTokenVersion(int tokenVersion){
        this.tokenVersion=tokenVersion;
    }
     

    @Override
public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(
        new SimpleGrantedAuthority("ROLE_"+role)
    );
}
}
