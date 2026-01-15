package in.tech_camp.protospace_b.config;

import java.util.Collection; // 追加
import java.util.Collections; // 追加

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import in.tech_camp.protospace_b.entity.UserEntity;
import lombok.Getter;


public class CustomUserDetails implements UserDetails{
    @Getter // 外部から userEntity を取得したい場合のみ必要
    private final UserEntity user;

    public CustomUserDetails(UserEntity user) {
        this.user = user;
    }

    public String getName() {
        return user.getName();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList();
    }

     public Integer getId() {
      return user.getId();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
