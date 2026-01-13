package in.tech_camp.protospace_b.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import in.tech_camp.protospace_b.repository.UserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class UserController {
  private final UserRepository userRepository;

  @GetMapping("/users/login")
  public String showLogin(){
      return "users/login";
  }
  
   @GetMapping("/login")
  public String showLoginWithError(@RequestParam(value = "error") String error, Model model) {
    if (error != null) {
      model.addAttribute("loginError", "Invalid email or password.");
    }
    return "users/login";
  }
}
