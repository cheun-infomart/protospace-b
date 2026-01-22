package in.tech_camp.protospace_b.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult; // 追加
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping; // 追加
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class UserController {

  private final UserRepository userRepository;
  private final UserService userService;

  //新規登録
  @GetMapping("/users/register")
  public String showRegister(Model model){
    model.addAttribute("userForm", new UserForm());
    return "users/register";
  }

  //新規登録バリデーションチェック
  @PostMapping("/user")
  public String createUser(@ModelAttribute("userForm") 
                           @Validated({ValidationOrder.EmailSequence.class,
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

    //新規登録成功時、ログイン画面に遷移
    return "redirect:users/login";
  }

  //ログイン成功
  @GetMapping("/users/login")
  public String showLogin(){
      return "users/login";
  }

  //ログイン失敗
  @GetMapping("/login")
  public String showLoginWithError(@RequestParam(value = "error") String error, Model model) {
    if (error != null) {
      model.addAttribute("loginError", "メールアドレスまたはパスワードが無効です。");
    }
    return "users/login";
  }

  //詳細ページ
  @GetMapping("/users/{id}")
  public String showUserDetail(@PathVariable("id") Integer id, Model model) {

    UserEntity user = userService.findUserDetail(id);
    if (user != null) {
        model.addAttribute("user", user);
        model.addAttribute("prototypes", user.getPrototypes());
    } 
    return "users/show";
  }
  
  // ユーザー編集画面に移動
  @GetMapping("/users/{id}/edit")
  public String editUserDetail(@PathVariable("id") Integer id, Authentication authentication, RedirectAttributes redirectAttributes, Model model) {
    try {
      UserEntity user = userService.findUser(id);

      UserForm form = userService.getUserForm(id);
      Integer currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();

      if(!user.getId().equals(currentUserId)){
        redirectAttributes.addFlashAttribute("errorMessage", "編集権限がありません");
        return "redirect:/";
      }
      
      model.addAttribute("userForm", form);
      model.addAttribute("id", id);

      return "users/edit";

    } catch (RuntimeException e) {
      redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
      return "redirect:/";
    }
  }

  // ユーザー情報更新
  @PostMapping("/users/{id}/update")
  public String updateUserDetail(@ModelAttribute("userForm")
                                 @Validated({ValidationOrder.NameSequence.class,
                                      ValidationOrder.ProfileSequence.class,
                                      ValidationOrder.DepartmentSequence.class,
                                      ValidationOrder.PositionSequence.class
                                 })UserForm userForm,
                                  BindingResult result,
                                 @PathVariable("id") Integer id,
                                  Model model) {
    if(result.hasErrors()){
      List<String> errorMessages = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);

      model.addAttribute("userForm", userForm);
      model.addAttribute("id", id);

      return "users/edit";
    }

    try {
      userService.updateUser(id, userForm);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      return "redirect:/users/" + id + "/edit";
    }
    return "redirect:/users/" + id;
  } 
}
