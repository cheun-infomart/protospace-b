package in.tech_camp.protospace_b.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.repository.UserRepository;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PrototypeController {
  private final UserRepository userRepository;
  private final PrototypeRepository prototypeRepository;

  @GetMapping("/prototype/{id}/edit")
  public String editTweet(@PathVariable("id") Integer id, Model model) {
    PrototypeEntity prototype = prototypeRepository.findById(id);

    PrototypeForm prototypeForm = new PrototypeForm();
    
    prototypeForm.setName(prototype.getName());
    prototypeForm.setCatchCopy(prototype.getCatchCopy());
    prototypeForm.setConcept(prototype.getConcept());
    prototypeForm.setImage(prototype.getImage());

    model.addAttribute("prototypeForm", prototypeForm);
    model.addAttribute("id", id);
    
    return "prototype/edit";
  }
  
  @PostMapping("/prototype/{id}/update")
  public String updateTweet(@ModelAttribute("prototypeForm") @Validated PrototypeForm prototypeForm, BindingResult result, @PathVariable("id") Integer id, Model model) {
    //TODO: process POST request
    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage).collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);

      model.addAttribute("prototypeForm", prototypeForm);
      model.addAttribute("id", id);
      return "prototype/edit";
    }

    PrototypeEntity prototype = prototypeRepository.findById(id);
    
    prototype.setName(prototypeForm.getName());
    prototype.setCatchCopy(prototypeForm.getCatchCopy());
    prototype.setConcept(prototypeForm.getConcept());
    prototype.setImage(prototypeForm.getImage());

    try {
      prototypeRepository.update(prototype);
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("えらー：" + e);
      return "redirect:/";
    }
    
    return "redirect:/";
  }
  
}