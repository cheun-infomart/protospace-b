package in.tech_camp.protospace_b.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService; // 重要
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.repository.UserRepository; // 追加
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
//loadUserByUsernameをカスタマイズ
public class UserAuthenticationService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        UserEntity userEntity = userRepository.findByEmail(email);


        // 【追加】コンソールにDBから取得した中身を表示させる
        System.out.println("--- 認証デバッグ開始 ---");
        System.out.println("検索に使用したメール: " + email);
        System.out.println("DBから取得したEntity: " + userEntity);
        if (userEntity != null) {
            System.out.println("DBから取得したパスワード(ハッシュ値): " + userEntity.getPassword());
        }
        System.out.println("--- 認証デバッグ終了 ---");

        

        if (userEntity == null) {
            throw new UsernameNotFoundException("User not found with email: " + email);
        }
        return new CustomUserDetails(userEntity);
    }
}
