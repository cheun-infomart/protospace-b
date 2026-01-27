package in.tech_camp.protospace_b.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.PasswordFindForm;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserService {
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final ImageUrl imageUrl; // 画像保存パス取得用に追加

  // 画像ファイルの保存処理（PrototypeServiceを参考に実装）
  public String saveImage(MultipartFile imageFile) throws IOException {
    if (imageFile == null || imageFile.isEmpty()) {
      return null;
    }

    String uploadDir = imageUrl.getImageUrl();
    String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_"
        + imageFile.getOriginalFilename();
    Path imagePath = Paths.get(uploadDir, fileName);

    if (!Files.exists(imagePath)) {
      Files.createDirectories(imagePath);
    }

    Files.copy(imageFile.getInputStream(), imagePath, StandardCopyOption.REPLACE_EXISTING);
    return "/uploads/" + fileName;
  }

  public void createUserWithEncryptedPassword(UserEntity userEntity, MultipartFile imageFile) throws IOException {

    // 1. 画像の保存とパスのセット
    if (imageFile != null && !imageFile.isEmpty()) {
      String savedImagePath = saveImage(imageFile);
      userEntity.setImage(savedImagePath);
    }
    // 2. パスワードの暗号化
    String encodedPassword = encodePassword(userEntity.getPassword());
    userEntity.setPassword(encodedPassword);
    // 3. DBへ保存
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

  // ユーザー編集用ユーザー情報取得
  public UserEntity findUser(Integer id){
    UserEntity user = userRepository.findById(id);
    return user;
  }

  // 編集画面にDB内の情報を表示
  public UserForm getUserForm(Integer id) {
    UserEntity user = userRepository.findById(id);
    if(user == null){
      throw new RuntimeException("ユーザーが見つかりません");
    }
    UserForm form = new UserForm();
    form.setName(user.getName());
    form.setProfile(user.getProfile());
    form.setDepartment(user.getDepartment());
    form.setPosition(user.getPosition());
    
		return form;
	}

  // 入力情報で更新
  public void updateUser(Integer id, UserForm form, Integer currentUserId){
    UserEntity user = userRepository.findById(id);

    // nullチェック
    if(user == null){
      throw new RuntimeException("編集対象が見つかりません");
    }

    // ログインユーザーと編集対象のユーザーが同一でない
    if (!user.getId().equals(currentUserId)) {
      throw new RuntimeException("編集権限がありません");
    }

    user.setName(form.getName());
    user.setProfile(form.getProfile());
    user.setDepartment(form.getDepartment());
    user.setPosition(form.getPosition());

    userRepository.update(user);
  }
  // 本人確認
  public UserEntity findVerifiedUser(PasswordFindForm form) {
    return userRepository.findByUserInfo(
        form.getEmail(),
        form.getSecurityQuestion(),
        form.getSecurityAnswer());
  }

  // パスワード変更
  @Transactional
  public void updatePassword(String email, String newPassword) {
    String encodedPassword = passwordEncoder.encode(newPassword);

    userRepository.updatePassword(email, encodedPassword);
  }
}
