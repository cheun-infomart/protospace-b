package in.tech_camp.protospace_b.controller;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.form.CommentForm;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.LikeRepository;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.service.PrototypeService;
import in.tech_camp.protospace_b.validation.ValidationOrder;
import lombok.AllArgsConstructor;

@Controller
@AllArgsConstructor
public class PrototypeController {
  private final PrototypeRepository prototypeRepository;

  private final PrototypeService prototypeService;

  private final LikeRepository likeRepository;
  
  @GetMapping("/")
  public String showPrototypes(Model model, Authentication authentication) {
    List<PrototypeEntity> prototypes = prototypeRepository.findAll();

    // ログイン中のユーザーIDを取得（未ログインならnull）
    Integer currentUserId = null;
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
        currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    }

    // 各プロトタイプに「いいね」情報をセット
    for (PrototypeEntity prototype : prototypes) {
      // いいね総数をセット
      prototype.setLikeCount(likeRepository.countByPrototypeId(prototype.getId()));
      
      // 自分がいいね済みかチェックしてセット
      if (currentUserId != null) {
          int likeCheck = likeRepository.countByUserAndPrototype(currentUserId, prototype.getId());
          prototype.setIsLiked(likeCheck > 0);
      } else {
          prototype.setIsLiked(false);
      }
    }

    model.addAttribute("prototypes", prototypes);
    return "index";
  }

  // プロトタイプ投稿画面表示
  @GetMapping("/prototypes/new")
  public String showPrototypeNew(Model model) {
    model.addAttribute("prototypeForm", new PrototypeForm());
    return "prototypes/new";
  }

  // プロトタイプ投稿保存
  @PostMapping("/prototypes")
  public String createPrototype(@ModelAttribute("prototypeForm") @Validated({
      ValidationOrder.NameSequence.class,
      ValidationOrder.catchCopySequence.class,
      ValidationOrder.conceptSequence.class
  }) PrototypeForm prototypeForm, BindingResult result, Model model, Authentication authentication) {

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

  //プロトタイプ編集
  @PostMapping("/prototypes/{id}/update")
  public String updatePrototype(@ModelAttribute("prototypeForm") @Validated({
      ValidationOrder.NameSequence.class,
      ValidationOrder.catchCopySequence.class,
      ValidationOrder.conceptSequence.class
  }) PrototypeForm prototypeForm, BindingResult result,
      @PathVariable("id") Integer id, Model model) {
    
    if (result.hasErrors()) {
      List<String> errorMessages = result.getAllErrors().stream().map(DefaultMessageSourceResolvable::getDefaultMessage)
          .collect(Collectors.toList());
      model.addAttribute("errorMessages", errorMessages);
      model.addAttribute("prototypeForm", prototypeForm);
      model.addAttribute("id", id);

      PrototypeEntity prototype = prototypeRepository.findById(id);
        
      // いいね・コメント等のデータセット（showPrototypeDetailの内容をコピー、または共通化）
      prototype.setLikeCount(likeRepository.countByPrototypeId(prototype.getId()));
      model.addAttribute("prototype", prototype);
      model.addAttribute("comments", prototype.getComments());
      model.addAttribute("commentForm", new CommentForm());
      return "prototypes/show";
    }

    try {
      prototypeService.updatePrototype(id, prototypeForm);
    } catch (Exception e) {
      // TODO: handle exception
      System.out.println("えらー：" + e);
      return "redirect:/prototypes/" + id ;
    }

    return "redirect:/prototypes/" + id;
  }
  
  //プロトタイプ詳細画面
  @GetMapping("/prototypes/{prototypeId}")
  public String showPrototypeDetail(@PathVariable("prototypeId") Integer prototypeId, Model model, Authentication authentication) {
      PrototypeEntity prototype = prototypeRepository.findById(prototypeId);
      if(prototype == null){
        return "redirect:/";
      }

    Integer currentUserId = null;
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
        currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    }

    // ★ 2. この「1つのprototype」に対していいね情報を直接セットする
    // いいね総数をセット
    prototype.setLikeCount(likeRepository.countByPrototypeId(prototype.getId()));
    
    // 自分がいいね済みかチェック
    if (currentUserId != null) {
        int likeCheck = likeRepository.countByUserAndPrototype(currentUserId, prototype.getId());
        prototype.setIsLiked(likeCheck > 0);
    } else {
        prototype.setIsLiked(false);
    }

      model.addAttribute("prototype", prototype);

      PrototypeForm form = prototypeService.getPrototypeForm(prototypeId);
      model.addAttribute("prototypeForm", form);
      
      model.addAttribute("commentForm", new CommentForm());
      model.addAttribute("comments",prototype.getComments());
      return "prototypes/show";
  }

  // プロトタイプ削除
  @PostMapping("/prototypes/{prototypeId}/delete")
  public String deletePrototype(@PathVariable("prototypeId") Integer prototypeId, Authentication authentication) {
    // ログインしていない場合はログイン画面にリダイレクト
    if (authentication == null || !authentication.isAuthenticated()) {
      return "redirect:/users/login";
    }
    // IDが不正な数値の場合やnullの場合は最初に弾く
    if (prototypeId == null || prototypeId <= 0) {
      return "redirect:/";
    }

    try {
      CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
      prototypeService.deletePrototype(prototypeId, userDetails);
    } catch (Exception e) {
      System.out.println("削除失敗：" + e.getMessage());
      return "redirect:/";
    }
    return "redirect:/";
  }

  @GetMapping("/prototypes/search")
  public String searchPrototypes(@RequestParam("keyword") String keyword, @RequestHeader(value = "X-Requested-With", required = false) String requestedWith, Model model, Authentication authentication) {
    String KatakanaKeyword= prototypeService.convertToKatakana(keyword);
    List<PrototypeEntity> prototypes = prototypeRepository.findByTextContaining(KatakanaKeyword);

    // ログイン中のユーザーIDを取得（未ログインならnull）
    Integer currentUserId = null;
    if (authentication != null && authentication.getPrincipal() instanceof CustomUserDetails) {
        currentUserId = ((CustomUserDetails) authentication.getPrincipal()).getId();
    }

    // 検索結果の各プロトタイプに「いいね」情報をセット
    for (PrototypeEntity prototype : prototypes) {
        // いいね総数をセット
        prototype.setLikeCount(likeRepository.countByPrototypeId(prototype.getId()));
        
        // 自分がいいね済みかチェック
        if (currentUserId != null) {
            int likeCheck = likeRepository.countByUserAndPrototype(currentUserId, prototype.getId());
            prototype.setIsLiked(likeCheck > 0);
        } else {
            prototype.setIsLiked(false);
        }
    }

    model.addAttribute("prototypes", prototypes);
    model.addAttribute("keyword", keyword);

    if("XMLHttpRequest".equals(requestedWith)){
      return "index :: #search-results";
    }
    return "index";
  }
}
