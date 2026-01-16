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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.service.PrototypeService;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@ActiveProfiles("test")
public class PrototypeControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;

  @Mock
  private PrototypeService prototypeService;  

  @Mock
  private BindingResult bindingResult;

  @Mock
  private MultipartFile multipartFile;

  @Mock
  private Authentication authentication;

  @Mock
  private CustomUserDetails customUserDetails;

  @InjectMocks
  private PrototypeController prototypeController;

  private Model model;

  @BeforeEach
  public void setUp(){
    model = new ExtendedModelMap();
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
  class プロトタイプ削除機能 {

    @Test
    public void 削除リクエストが呼ばれた場合自分の投稿であれば削除されトップページにリダイレクトする() {
      Integer prototypeId = 1;
      Integer userId = 100;
      when(authentication.isAuthenticated()).thenReturn(true);

      PrototypeEntity prototype = new PrototypeEntity();
      prototype.setId(prototypeId);
      in.tech_camp.protospace_b.entity.UserEntity owner = new in.tech_camp.protospace_b.entity.UserEntity();
      owner.setId(userId);
      prototype.setUser(owner);

      // モック設定
      when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
      when(authentication.getPrincipal()).thenReturn(customUserDetails);
      when(customUserDetails.getId()).thenReturn(userId);

      String result = prototypeController.deletePrototype(prototypeId, authentication);

      assertThat(result, is("redirect:/"));
      Mockito.verify(prototypeService).deletePrototype(eq(prototypeId), eq(customUserDetails));
    }

    @Test
    public void 未ログインの場合はログイン画面にリダイレクトされる() {
      // 未ログイン状態を作る（authenticationがfalseを返す）
      Integer prototypeId = 1;
      when(authentication.isAuthenticated()).thenReturn(false);

      String result = prototypeController.deletePrototype(prototypeId, authentication);

      assertThat(result, is("redirect:/users/login"));
      
      // サービスやリポジトリが呼ばれていないことを確認
      Mockito.verifyNoInteractions(prototypeService);
      Mockito.verifyNoInteractions(prototypeRepository);
    }

    @Test
    public void 不正なIDの場合はリダイレクトされる() {
      Integer invalidId = 0;
      when(authentication.isAuthenticated()).thenReturn(true);

      String result = prototypeController.deletePrototype(invalidId, authentication);

      assertThat(result, is("redirect:/"));

      // ガード句で弾かれるためリポジトリもサービスも呼び出していないことを検証
      Mockito.verifyNoInteractions(prototypeRepository);
      Mockito.verifyNoInteractions(prototypeService);
    }

  @Test
  public void 他人の投稿を削除しようとした場合はServiceからのエラーをキャッチしてトップページにリダイレクトする() {
    Integer prototypeId = 1;
    when(authentication.isAuthenticated()).thenReturn(true);
    
    // Serviceは例外を投げる
    Mockito.doThrow(new RuntimeException("削除権限がありません"))
          .when(prototypeService).deletePrototype(eq(prototypeId), any());

    String result = prototypeController.deletePrototype(prototypeId, authentication);

    // コントローラーがcatchブロックに入り、リダイレクトを返しているか検証
    assertThat(result, is("redirect:/"));
    
    // サービス自体は呼ばれたことを確認する（呼ばれた後にユーザー検証するため）
    Mockito.verify(prototypeService).deletePrototype(eq(prototypeId), any());
    }
  }
}
