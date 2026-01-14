package in.tech_camp.protospace_b.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PrototypeController {
  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;

  @GetMapping("/prototypes/{prototypeId}")
  public String showTweetDetail(@PathVariable("prototypeId") Integer prototypeId, Model model) {
      PrototypeEntity prototype = prototypeRepository.findById(prototypeId);
      UserEntity user = userRepository.findById(prototype.getUserId());
      model.addAttribute("prototype", prototype);
      model.addAttribute("user", user);
      return "prototypes/show";
  }
}
