package in.tech_camp.protospace_b.controller;

import org.hamcrest.MatcherAssert;
import static org.hamcrest.MatcherAssert.assertThat;
import org.hamcrest.Matchers;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.service.PrototypeService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PrototypeControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;

  @Mock
  private PrototypeService prototypeService;  

  @Mock
  private BindingResult bindingResult;

  @Mock
  private RedirectAttributes redirectAttributes;

  @Mock
  private MultipartFile multipartFile;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private PrototypeController prototypeController;

  private Model model;

  private PrototypeForm testForm;

  @BeforeEach
  public void setUp(){
    model = new ExtendedModelMap();
    testForm = PrototypeFormFactory.createPrototype();
  }

  @Nested
  class プロトタイプ詳細機能 {

  @Test
  public void 詳細機能にリクエストするとプロトタイプ詳細表示のビューファイルがレスポンスで返ってくる(){
    PrototypeEntity prototype = new PrototypeEntity();
    Integer prototypeId = 1;
    prototype.setId(prototypeId);


    
    when(prototypeRepository.findById(1)).thenReturn(prototype);
    String result = prototypeController.showPrototypeDetail(1, model);
    
    assertThat(result, is("prototypes/show"));
  }

  @Test
  public void 詳細機能にリクエストするとレスポンスにプロトタイプが存在する(){
    PrototypeEntity prototype = new PrototypeEntity();
    Integer prototypeId = 1;
    prototype.setId(prototypeId);
    
    when(prototypeRepository.findById(1)).thenReturn(prototype);
    String result = prototypeController.showPrototypeDetail(1, model);
    
    assertThat(result, is("prototypes/show"));

    assertThat(model.getAttribute("prototype"), is(prototype));
  }

  @Test
  public void 詳細機能にリクエストするときにDBに無いプロトタイプIDを指定されたときにトップページにリダイレクトされる(){
    when(prototypeRepository.findById(1)).thenReturn(null);
    String result = prototypeController.showPrototypeDetail(1, model);
    assertThat(result, is("redirect:/"));
  }
}


  @Nested
  class プロトタイプ新規投稿ページ表示機能 {
    @Test
    public void 新規投稿フォーム表示機能にリクエストすると投稿フォームのビューファイルがレスポンスで返ってくる() {
      Model model = new ExtendedModelMap();

      String result = prototypeController.showPrototypeNew(model);

      MatcherAssert.assertThat(result, Matchers.is("prototypes/new"));
    }
  }

  @Nested
  class 新規投稿保存機能 {

    @Nested
    class 保存に成功する場合 {

      @Test
      public void すべての項目が入力されて保存ボタンが押された場合serviceが呼ばれてトップページにリダイレクトする() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);

        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        Model model = new ExtendedModelMap();
        String result = prototypeController.createPrototype(form, bindingResult, model, authentication);

        // リダイレクト先の検証
        assertThat(result, is("redirect:/"));

        // サービスが呼ばれたことを検証
        // 引数として渡されたPrototypeEntityの内容をチェックする
        ArgumentCaptor<PrototypeEntity> captor = ArgumentCaptor.forClass(PrototypeEntity.class);
        Mockito.verify(prototypeService).createPrototype(captor.capture(), eq(multipartFile), eq(authentication));

        PrototypeEntity savedPrototype = captor.getValue();
        assertThat(savedPrototype.getName(), is(form.getName()));
      }
    }

    @Nested
    class 保存に失敗する場合 {
      @Test
      public void 保存ボタンを押した際にログインしていない場合はログインページにリダイレクトする() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);
        Mockito.when(multipartFile.isEmpty()).thenReturn(false);

        // Serviceが未ログイン例外を投げるように設定
        Mockito.doThrow(new RuntimeException("Unauthenticated"))
              .when(prototypeService).createPrototype(Mockito.any(), Mockito.any(), Mockito.any());

        String result = prototypeController.createPrototype(form, bindingResult, new ExtendedModelMap(), authentication);

        assertThat(result, is("redirect:/users/login"));
      }

      @Test
      public void データベースの保存処理でエラーが発生した場合新規投稿ページを返す() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);

        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        // Serviceが汎用的な例外を投げるように設定
        Mockito.doThrow(new java.io.IOException("画像保存またはDBエラー"))
              .when(prototypeService).createPrototype(Mockito.any(), Mockito.any(), Mockito.any());

        String result = prototypeController.createPrototype(form, bindingResult, new ExtendedModelMap(), authentication);

        assertThat(result, is("prototypes/new"));
      }

      @Test
      public void 画像が空の場合にバリデーションエラーが発生しプロトタイプ新規投稿画面を返す() {
        PrototypeForm form = PrototypeFormFactory.createPrototype();

        // 画像は空にする
        form.setImage(multipartFile);
        Mockito.when(multipartFile.isEmpty()).thenReturn(true);
        
        // BindingResultにエラーがある状態を生成（hasErrorsがtrueを返すように）
        Mockito.when(bindingResult.hasErrors()).thenReturn(true);

        Model model = new ExtendedModelMap();

        String result = prototypeController.createPrototype(form, bindingResult, model, authentication);

        assertThat(result, is("prototypes/new"));
        Mockito.verify(bindingResult).rejectValue(eq("image"), eq("error.image"), anyString());
      }
    }
  }

  @Nested
  class プロトタイプ編集機能 {
    @Test
    public void ゲットメソッドでView画面表示できる() {
    when(prototypeService.getPrototypeForm(1)).thenReturn(testForm);
    Model model = new ExtendedModelMap();

    String result = prototypeController.editPrototype(1, authentication, redirectAttributes, model);
    //修正画面に代わっているか
    assertThat(result, is("prototypes/edit"));
    //modelにフォームの情報が入っているか
    assertThat(model.getAttribute("prototypeForm"), is(testForm));
    }
    @Test
    public void 編集機能が問題なく実行されたか() {
    when(bindingResult.hasErrors()).thenReturn(false);
    Model model = new ExtendedModelMap();
    //updateメソッド実行
    String result = prototypeController.updatePrototype(testForm, bindingResult, 1, model);
    //問題なくupdateされredirectになっているか
    assertThat(result, is("redirect:/prototypes/1"));
    }
  }
}

 