package in.tech_camp.protospace_b.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 追加
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // 追加

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;
import in.tech_camp.protospace_b.repository.LikeRepository;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.Authentication;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;
  private final LikeRepository likeRepository;

  // 新規登録
  @GetMapping("/users/register")
  public String showRegister(Model model) {
    model.addAttribute("userForm", new UserForm());
    return "users/register";
  }

  // 新規登録バリデーションチェック
  @PostMapping("/user")
  public String createUser(@ModelAttribute("userForm") @Validated({ ValidationOrder.EmailSequence.class,
      ValidationOrder.PasswordSequence.class,
      ValidationOrder.NameSequence.class,
      ValidationOrder.ProfileSequence.class,
      ValidationOrder.DepartmentSequence.class,
      ValidationOrder.PositionSequence.class
  }) UserForm userForm,
      BindingResult result,
      Model model) {
    userForm.validatePasswordConfirmation(result);
    if (userRepository.existsByEmail(userForm.getEmail())) {
      result.rejectValue("email", "null", "メールアドレスは既に存在します");
    }

    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.toList());

      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("userForm", userForm);
      return "users/register";
    }

    UserEntity userEntity = new UserEntity();
    userEntity.setName(userForm.getName());
    userEntity.setEmail(userForm.getEmail());
    userEntity.setProfile(userForm.getProfile());
    userEntity.setDepartment(userForm.getDepartment());
    userEntity.setPosition(userForm.getPosition());
    userEntity.setPassword(userForm.getPassword());

    try {
      userService.createUserWithEncryptedPassword(userEntity);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      return "redirect:users/register";
    }

    // 新規登録成功時、ログイン画面に遷移
    return "redirect:users/login";
  }

  // 途中でログイン
  @GetMapping("/users/login")
  public String showLogin(@RequestParam(value = "error", required = false) String error, HttpServletRequest request,
      HttpSession session,
      Model model) {

    // headerにReferer:URLのデータ（以前のセッションURL）を持ってくる
    String referrer = request.getHeader("Referer");

    // refererを持っていてloginページのままの場合refererデータ持ち続ける（ログイン失敗にRefererデータがなくなることを防止）
    if (isPageValid(referrer)) {
      session.setAttribute("prevPage", referrer);
    }
    // フォームエラー表示
    if (error != null) {
      model.addAttribute("loginError", "メールアドレスまたはパスワードが無効です。");
    }
    return "users/login";
  }

  private boolean isPageValid(String ref) {
    return ref != null && !ref.contains("/users/login") && !ref.contains("/users/register");
  }

  // 詳細ページ
  @GetMapping("/users/{id}")
  public String showUserDetail(@PathVariable("id") Integer id, Model model, Authentication authentication) {

    UserEntity user = userService.findUserDetail(id);
    if (user != null) {
        List<PrototypeEntity> prototypes = user.getPrototypes();

        // 1. ログイン中のユーザーIDを取得
        Integer currentUserId = null;
        if (authentication != null && authentication.getPrincipal() instanceof in.tech_camp.protospace_b.config.CustomUserDetails) {
            currentUserId = ((in.tech_camp.protospace_b.config.CustomUserDetails) authentication.getPrincipal()).getId();
        }

        // 2. ユーザーが投稿した各プロトタイプに「いいね」情報をセット
        // ※ PrototypeController等と同様に likeRepository をインジェクションしておく必要があります
        for (in.tech_camp.protospace_b.entity.PrototypeEntity prototype : prototypes) {
            prototype.setLikeCount(likeRepository.countByPrototypeId(prototype.getId()));
            
            if (currentUserId != null) {
                int likeCheck = likeRepository.countByUserAndPrototype(currentUserId, prototype.getId());
                prototype.setIsLiked(likeCheck > 0);
            } else {
                prototype.setIsLiked(false);
            }
        }
        model.addAttribute("user", user);
        model.addAttribute("prototypes", user.getPrototypes());
    } 
    return "users/show";
  }

}
