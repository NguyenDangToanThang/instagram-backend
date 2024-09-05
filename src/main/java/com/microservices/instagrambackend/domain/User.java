package com.microservices.instagrambackend.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.microservices.instagrambackend.enums.Role;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Date;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User implements UserDetails {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    @Column(unique = true)
    private String email;
    private String fullname;
    private String password;
    private String avatar;
    private String bio;
    private Role role;
    private Date createdAt;

    @JsonIgnoreProperties
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<Post> posts;

    @JsonIgnoreProperties
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<Comment> comments;

    @JsonIgnoreProperties
    @OneToMany(mappedBy = "user",cascade = CascadeType.REMOVE)
    private List<Like> likes;

    @JsonIgnoreProperties
    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<RefreshToken> refreshTokens;

    @JsonIgnoreProperties
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(role.name()));
    }

    @JsonIgnoreProperties
    @Override
    public String getUsername() {
        return this.email;
    }

    @JsonIgnoreProperties
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnoreProperties
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnoreProperties
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnoreProperties
    @Override
    public boolean isEnabled() {
        return true;
    }
}
