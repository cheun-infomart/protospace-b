package in.tech_camp.protospace_b.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public void createUserWithEncryptedPassword(UserEntity userEntity) {
    String encodedPassword = encodePassword(userEntity.getPassword());
    userEntity.setPassword(encodedPassword);
    userRepository.insert(userEntity);
  }

  private String encodePassword(String password) {
    return passwordEncoder.encode(password);
  }

  public UserEntity findUserDetail(Integer id) {
    UserEntity user = userRepository.findByIdWithProto(id);

    return user;

  }

  public void deleteUser(Integer id, CustomUserDetails userDetails) {
    UserEntity user = userRepository.findById(id);

    // nullチェック（なければ例外を投げる）
    if (user == null) {
      throw new RuntimeException("削除対象が見つかりません");
    }
    // ログインユーザーかつプロトタイプのユーザーかどうかチェックし、異なる場合はエラーを投げる
    if (!user.getId().equals(userDetails.getId())) {
      throw new RuntimeException("削除権限がありません");
    }
    // 削除処理実行
    userRepository.deleteById(id);

  }
}
