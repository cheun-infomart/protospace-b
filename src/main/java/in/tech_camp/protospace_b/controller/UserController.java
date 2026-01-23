package in.tech_camp.protospace_b.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 追加
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam; // 追加
import org.springframework.web.bind.annotation.ResponseBody;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.LikeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.AllArgsConstructor; // 追加

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
    model.addAttribute("questions", UserForm.SECURITY_QUESTIONS);
    return "users/register";
  }

  // 新規登録バリデーションチェック
  @PostMapping("/user")
  public String createUser(
      @ModelAttribute("userForm") @Validated({ValidationOrder.EmailSequence.class,
          ValidationOrder.PasswordSequence.class, ValidationOrder.NameSequence.class,
          ValidationOrder.ProfileSequence.class, ValidationOrder.DepartmentSequence.class,
          ValidationOrder.PositionSequence.class}) UserForm userForm,
      BindingResult result, Model model) {


    userForm.validatePasswordConfirmation(result);
    if (userRepository.existsByEmail(userForm.getEmail())) {
      result.rejectValue("email", "null", "メールアドレスは既に存在します");
    }

    if (result.hasErrors()) {
      boolean hasStep1Error = result.hasFieldErrors("email") || result.hasFieldErrors("password")
          || result.hasFieldErrors("name") || result.hasFieldErrors("profile")
          || result.hasFieldErrors("department") || result.hasFieldErrors("position");

      int activeStep = hasStep1Error ? 1 : 2;

      List<String> errorMessages = result.getAllErrors().stream()
          .map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());

      model.addAttribute("activeStep", activeStep);
      model.addAttribute("questions", UserForm.SECURITY_QUESTIONS);
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
  public String showLogin(@RequestParam(value = "error", required = false) String error,
      HttpServletRequest request, HttpSession session, Model model) {

    // headerにReferer:URLのデータ（以前のセッションURL）を持ってくる
    String referrer = request.getHeader("Referer");
    String accept = request.getHeader("Accept");

    // refererを持っていてloginページのままの場合refererデータ持ち続ける（ログイン失敗にRefererデータがなくなることを防止）
    if (isPageValid(referrer) && accept != null && accept.contains("text/html")) {
      session.setAttribute("prevPage", referrer);
    }
    // フォームエラー表示
    if (error != null) {
      model.addAttribute("loginError", "メールアドレスまたはパスワードが無効です。");
    }
    return "users/login";
  }

  private boolean isPageValid(String ref) {
    if (ref == null)
      return false;

    return !ref.contains("/users/login") && !ref.contains("/users/register")
        && !ref.contains("/js/") && !ref.contains("/css/") && !ref.contains("/images/")
        && !ref.contains(".js") && !ref.contains(".css");
  }

  // 詳細ページ
  @GetMapping("/users/{id}")
  public String showUserDetail(@PathVariable("id") Integer id, Model model,
      Authentication authentication) {

    UserEntity user = userService.findUserDetail(id);
    if (user != null) {
      List<PrototypeEntity> prototypes = user.getPrototypes();

      // 1. ログイン中のユーザーIDを取得
      Integer currentUserId = null;
      if (authentication != null && authentication
          .getPrincipal() instanceof in.tech_camp.protospace_b.config.CustomUserDetails) {
        currentUserId =
            ((in.tech_camp.protospace_b.config.CustomUserDetails) authentication.getPrincipal())
                .getId();
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

  // ユーザー削除
  @PostMapping("/users/{id}/delete")
  @ResponseBody
  public ResponseEntity<String> deleteUser(@PathVariable("id") Integer id,
      Authentication authentication, HttpServletRequest request, HttpServletResponse response) {
    // ログインしていない場合はログイン画面にリダイレクト
    if (authentication == null || !authentication.isAuthenticated()) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }

    try {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      userService.deleteUser(id, userDetails);

      SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
      logoutHandler.logout(request, response, authentication);
      return ResponseEntity.ok("success");
    } catch (Exception e) {
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
    }
  }
}
