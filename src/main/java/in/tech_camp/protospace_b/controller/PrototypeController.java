package in.tech_camp.protospace_b.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import in.tech_camp.protospace_b.repository.PrototypeRepository;
import lombok.AllArgsConstructor;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.security.core.Authentication;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.service.PrototypeService;
import in.tech_camp.protospace_b.validation.ValidationOrder;

@Controller
@AllArgsConstructor
public class PrototypeController {
  private final PrototypeService prototypeService;
  private final PrototypeRepository prototypeRepository;

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

    // バリデーションエラーがあった場合、新規投稿画面にとどまる
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

    // 投稿保存処理のServiceを呼び出す
    try {
      // 成功したらトップページにリダイレクトする
      prototypeService.createPrototype(prototype, prototypeForm.getImage(), authentication);
      return "redirect:/";
    } catch (RuntimeException e) {
      // ユーザーが見つからないor未ログインの場合はログイン画面に返す
      return "redirect:/users/login";
    } catch (Exception e) {
      // 画像保存エラーやDBエラーなどの場合はその場にとどまる
      System.out.println("保存エラー：" + e);
      return "prototypes/new";
    }
  }

  //プロトタイプ詳細画面への遷移
  @GetMapping("/prototypes/{prototypeId}")
  public String showPrototypeDetail(@PathVariable("prototypeId") Integer prototypeId, Model model) {
      PrototypeEntity prototype = prototypeRepository.findById(prototypeId);
      if(prototype == null){
        return "redirect:/";
      }
      model.addAttribute("prototype", prototype);
      return "prototypes/show";
  }
}
