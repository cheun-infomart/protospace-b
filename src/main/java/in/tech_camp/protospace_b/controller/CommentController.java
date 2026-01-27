package in.tech_camp.protospace_b.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.CommentEntity;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.form.CommentForm;
import in.tech_camp.protospace_b.repository.CommentRepository;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import lombok.AllArgsConstructor;


@RestController
@AllArgsConstructor
public class CommentController {

  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;
  private final CommentRepository commentRepository;

  @PostMapping("/prototypes/{prototypeId}/comment")
  public ResponseEntity<String> createComment(@PathVariable("prototypeId") Integer prototypeId,
                              @ModelAttribute("commentForm") @Validated(ValidationOrder.textSequence.class) CommentForm commentForm,
                              BindingResult result,
                              @AuthenticationPrincipal CustomUserDetails currentUser) {
  
  PrototypeEntity prototype = prototypeRepository.findById(prototypeId);
  
  if(result.hasErrors()){
    String firstErrorMessage = result.getAllErrors().get(0).getDefaultMessage();
    return ResponseEntity.badRequest().body(firstErrorMessage);
  }

  CommentEntity comment = new CommentEntity();
  comment.setText(commentForm.getText());
  comment.setPrototype(prototype);
  comment.setUser(userRepository.findById(currentUser.getId()));

  try {
    commentRepository.insert(comment);
  } catch (Exception e) {
    return ResponseEntity.internalServerError().body("保存に失敗しました");
  }

  // currentUserから取得
  String userImage = currentUser.getUser().getImage();
  String displayName = currentUser.getUser().getName(); 

  String htmlResponse = String.format(
        "<li class='prototype-show-comment'>" +
        "  <div class='prototype-show-comment-side'>" + 
        "    <a href='/users/%d'>" +
        "      <img src='%s' alt='user-icon' class='user-icon-mini' />" + 
        "    </a>" +
        "  </div>" + 
        "  <div class='prototype-show-comment-content'>" + 
        "    <div class='prototype-show-comment-user-date'>" +
        "      <a href='/users/%d' class='prototype-show-comment-user'>%s</a>" +
        "      <div class='prototype-show-comment-date'>たった今</div>" +
        "    </div>" +
        "    <div class='prototype-show-comment-text-wrapper'>" + 
        "      <span class='prototype-show-comment-text'>%s</span>" + 
        "    </div>" + 
        "  </div>" +
        "</li>",
        currentUser.getId(), 
        userImage,           
        currentUser.getId(),
        displayName,
        commentForm.getText()
    );
    return ResponseEntity.ok(htmlResponse);
  }
}
