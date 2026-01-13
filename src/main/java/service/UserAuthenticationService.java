package service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;


import in.tech_camp.protospace_b.entity.UserEntity;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
//loadUserByUsernameをカスタマイズ
public class UserAuthenticationService implements CustomUserDetailsService{
  private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);
        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }

        return new CustomUserDetail(userEntity);
    }
}
