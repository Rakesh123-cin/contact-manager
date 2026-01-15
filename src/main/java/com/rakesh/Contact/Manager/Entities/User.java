package com.rakesh.Contact.Manager.Entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity(name="user")
@Table(name="users")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class User implements UserDetails{
    @Id
    private String userId;
    @Column(name="username",nullable = false)
    private String name;
    @Column(nullable = false,unique = true)
    private String email;
    private String password;
    @Column(length = 1000)
    private String about;
    @Column(length = 10000)
    private String profilePic;
    private String phoneNumber;

    @Builder.Default
    @Column(name="enabled")
    private boolean enabled=false;
    @Builder.Default
    @Column(name="email_verified")
    private boolean emailVerified=false;
    @Builder.Default
    private boolean phoneNumberVerified=false;

    //SELF, GOOGLE, GITHUB. LINKEDIN, FACEBOOK
    @Enumerated(jakarta.persistence.EnumType.STRING)
    @Builder.Default
    private Providers provider=Providers.SELF;
    private String providerId;

    // Add more fields as per your requirements

    @OneToMany(mappedBy = "user",cascade = jakarta.persistence.CascadeType.ALL,fetch=jakarta.persistence.FetchType.LAZY,orphanRemoval = true)
    @Builder.Default
    private List<Contact> contacts = new ArrayList<>();

    @ElementCollection(fetch=FetchType.EAGER)
    @Builder.Default
    private List<String> roles=new ArrayList<>();

    private String emailVerificationToken;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<SimpleGrantedAuthority> authorities= roles.stream().map(role->new SimpleGrantedAuthority(role)).collect(Collectors.toList());
        return authorities;
    }

    // emailId as username
    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {    
        return this.enabled;
    }

}
