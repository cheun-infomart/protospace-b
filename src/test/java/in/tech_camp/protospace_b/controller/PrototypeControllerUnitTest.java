package in.tech_camp.protospace_b.controller;

import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.Nested;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class PrototypeControllerUnitTest {
  @Mock
  private PrototypeRepository prototypeRepository;
  
  @Mock
  private UserRepository userRepository;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private MultipartFile multipartFile;

  @Mock
  private ImageUrl imageUrl;

  @Mock
  private Authentication authentication;

  @InjectMocks
  private PrototypeController prototypeController;

  @Nested
  class プロトコル新規投稿ページ表示機能 {
    @Test
    public void 新規投稿フォーム表示機能にリクエストすると投稿フォームのビューファイルがレスポンスで返ってくる() {
      Model model = new ExtendedModelMap();

      String result = prototypeController.showPrototypeNew(model);

      MatcherAssert.assertThat(result, Matchers.is("prototypes/new"));
    }
  }

  @Nested
  class 新規投稿保存機能 {
    @Test
    public void すべての項目が入力されて保存ボタンが押された場合insertメソッドが呼ばれてトップページにリダイレクトする() throws Exception {
      PrototypeForm form = PrototypeFormFactory.createPrototype();
      form.setImage(multipartFile);

      // 画像を準備
      Mockito.when(multipartFile.isEmpty()).thenReturn(false);
      Mockito.when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
      Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.png");

      // id10のユーザーをログインユーザーとする
      UserEntity mockUser = new UserEntity();
      mockUser.setId(10);
      Mockito.when(authentication.isAuthenticated()).thenReturn(true);
      Mockito.when(authentication.getName()).thenReturn("test@test.com");
      Mockito.when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);
      
      // 画像が選択され、エラーも起きていない状態を生成
      Mockito.when(imageUrl.getImageUrl()).thenReturn("src/main/resources/static/uploads");
      Mockito.when(bindingResult.hasErrors()).thenReturn(false);

      Model model = new ExtendedModelMap();
      String result = prototypeController.createPrototype(form, bindingResult, model, authentication);

      assertThat(result, is("redirect:/"));

      // ArgumentCaptorでinsertに渡されたPrototypeEntityを取得する
      ArgumentCaptor<PrototypeEntity> captor = ArgumentCaptor.forClass(PrototypeEntity.class);
      Mockito.verify(prototypeRepository).insert(captor.capture());

      // 取得したプロトタイプエンティティの中身を確認
      PrototypeEntity savedPrototype = captor.getValue();
      assertThat(savedPrototype.getUserId(), is(10)); // IDがログインユーザーのものか確認
      assertThat(savedPrototype.getName(), is(form.getName()));
    }

    @Nested
    class ログインしていない場合 {
      @Test
      public void 保存ボタンを押した際にログインしていない場合はログインページにリダイレクトする() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);

        // 画像が選択されている状態（バリデーションを通過させるため）
        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.png"); // 追加
        Mockito.when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0])); // 追加
        Mockito.when(imageUrl.getImageUrl()).thenReturn(System.getProperty("java.io.tmpdir"));

        // 未ログイン状態を作るためauthenticationがnull、またはisAuthenticatedがfalseを返すように設定
        Mockito.when(authentication.isAuthenticated()).thenReturn(false);

        Model model = new ExtendedModelMap();

        String result = prototypeController.createPrototype(form, bindingResult, model, authentication);

        // ログイン画面へのリダイレクトを確認
        assertThat(result, is("redirect:/users/login"));
        
        // 保存処理（insert）が呼ばれていないことも確認
        Mockito.verify(prototypeRepository, Mockito.never()).insert(Mockito.any());
      }
    }

    @Nested
    class 保存に失敗する場合 {
      @Test
      public void データベースの保存処理でエラーが発生した場合新規投稿ページを返す() throws Exception {
        PrototypeForm form = PrototypeFormFactory.createPrototype();
        form.setImage(multipartFile);

        // 画像のモック設定
        Mockito.when(multipartFile.isEmpty()).thenReturn(false);
        Mockito.when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
        Mockito.when(multipartFile.getOriginalFilename()).thenReturn("test.png");

        // id10のユーザーをログインユーザーとする
        UserEntity mockUser = new UserEntity();
        mockUser.setId(10);
        Mockito.when(authentication.isAuthenticated()).thenReturn(true);
        Mockito.when(authentication.getName()).thenReturn("test@test.com");
        Mockito.when(userRepository.findByEmail("test@test.com")).thenReturn(mockUser);

        Mockito.when(imageUrl.getImageUrl()).thenReturn(System.getProperty("java.io.tmpdir"));
        Mockito.when(bindingResult.hasErrors()).thenReturn(false);

        // insertメソッドが呼ばれたら、例外を投げるように設定
        Mockito.doThrow(new RuntimeException("DBエラーが発生しました"))
              .when(prototypeRepository).insert(Mockito.any());

        Model model = new ExtendedModelMap();

        String result = prototypeController.createPrototype(form, bindingResult, model, authentication);
        // 例外をcatchした場合、新規投稿画面が返されることを確認
        assertThat(result, is("prototypes/new"));
        
        // insertメソッドが少なくとも1回は呼ばれたことを確認
        Mockito.verify(prototypeRepository).insert(Mockito.any());
      }

      @Test
      public void 画像が空の場合にバリデーションエラーが発生しプロトコル新規投稿画面を返す() {
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
}
