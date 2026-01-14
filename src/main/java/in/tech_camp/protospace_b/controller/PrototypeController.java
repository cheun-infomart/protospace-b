package in.tech_camp.protospace_b.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import lombok.AllArgsConstructor;


@Controller
@AllArgsConstructor
public class PrototypeController {
  private final PrototypeRepository prototypeRepository;
  private final UserRepository userRepository;
  private final ImageUrl imageUrl;

  // プロトタイプ投稿画面表示
  @GetMapping("/prototypes/new")
  public String showPrototypeNew(Model model) {
    model.addAttribute("prototypeForm", new PrototypeForm());
      return "prototypes/new";
  }

  // プロトタイプ投稿保存
  @PostMapping("/prototypes")
  public String createPrototype(@ModelAttribute("prototypeForm") @Validated(ValidationOrder.class) PrototypeForm prototypeForm, BindingResult result, Model model, Authentication authentication) {

    // 画像が無い場合は入力必須のエラーを返す(@NotBlankが使えないのでここで手動設定)
    if (prototypeForm.getImage().isEmpty()) {
        result.rejectValue("image", "error.image", "プロトタイプの画像は必須です");
    }

    // バリデーションエラーがあった場合、新規投稿画面に返す
    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream()
              .map(DefaultMessageSourceResolvable::getDefaultMessage)
              .collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("prototypeForm", prototypeForm);
      return "prototypes/new";
    }

    // エラーが無い場合、フォームからの情報をエンティティにセットする
    PrototypeEntity prototype = new PrototypeEntity();
    prototype.setName(prototypeForm.getName());
    prototype.setCatchCopy(prototypeForm.getCatchCopy());
    prototype.setConcept(prototypeForm.getConcept());

    // 画像保存ロジック
    MultipartFile imageFile = prototypeForm.getImage();
    if (imageFile != null && !imageFile.isEmpty()) {
      try {
        String uploadDir = imageUrl.getImageUrl();
        String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + "_" + imageFile.getOriginalFilename();
        Path imagePath = Paths.get(uploadDir, fileName);
        Files.copy(imageFile.getInputStream(), imagePath);
        prototype.setImage("/uploads/" + fileName);
      } catch (IOException e) {
        System.out.println("エラー：" + e);
        return "redirect:/";
      }
    }

    // ログイン中のユーザー情報を取得してuser_idをセットする
    if (authentication != null && authentication.isAuthenticated()) {
        String email = authentication.getName(); // ログイン時に入力したemailを取得
        UserEntity user = userRepository.findByEmail(email);
        prototype.setUserId(user.getId());
    } else {
      // 万が一ログインしていない場合はログイン画面を返す
      return "redirect:/users/login";
    }

    try {
      prototypeRepository.insert(prototype);
    } catch (Exception e) {
      System.out.println("エラー：" + e);
      return "prototypes/new";
    }
    return "redirect:/";
  }
}
