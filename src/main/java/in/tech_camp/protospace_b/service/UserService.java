package in.tech_camp.protospace_b.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.UserForm;
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
  public void updateUser(Integer id, UserForm form){
    UserEntity user = userRepository.findById(id);

    user.setName(form.getName());
    user.setProfile(form.getProfile());
    user.setDepartment(form.getDepartment());
    user.setPosition(form.getPosition());

    userRepository.update(user);
  }
}
