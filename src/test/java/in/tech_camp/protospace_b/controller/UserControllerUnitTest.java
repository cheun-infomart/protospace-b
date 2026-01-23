package in.tech_camp.protospace_b.controller;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUnitTest {

  @Mock
  private UserService userService;

  @Mock
  private BindingResult bindingResult;

  @Mock
  private UserRepository userRepository;

  @Mock
  private Authentication authentication;

  @Mock
  private HttpServletRequest request;

  @Mock
  private HttpServletResponse response;

  @Mock
  private CustomUserDetails userDetails;

  @InjectMocks
  private UserController userController;

  private UserEntity mockUser;

  @BeforeEach
  public void setUp() {
    mockUser = UserFactory.createMockUser();
  }

  // 新規登録
  @Nested
  class 新規登録 {

    // テスト用の空でない画像ファイルを作成
    private MockMultipartFile createMockImage() {
        return new MockMultipartFile("image", "test.png", "image/png", "test data".getBytes());
    }

    @Test
    public void 新規登録機能にリクエストすると新規登録画面のビューファイルがレスポンスで返ってくる() {
      Model model = new ExtendedModelMap(); // Modelを準備
      String result = userController.showRegister(model);
      assertThat(result, is("users/register"));
    }

    @Test
    public void 新規登録に失敗したユーザーに対してエラーメッセージを表示させる() {
      UserForm userForm = new UserForm();
      // テスト用に値をセット
      userForm.setPassword("password123");
      userForm.setPasswordConfirmation("password123");
      userForm.setImage(new MockMultipartFile("image", new byte[0]));

      Model model = new ExtendedModelMap();

      // hasErrors()がtrueを返すように設定（失敗ルートを通すため）
      when(bindingResult.hasErrors()).thenReturn(true);

      // 2. 実行：正しい引数で呼び出す
      String result = userController.createUser(userForm, bindingResult, model);

      // 3. 検証
      assertThat(result, is("users/register"));
    }

    @Test
    public void ユーザー登録中に例外が発生した場合は登録画面にリダイレクトされる() throws IOException {

      UserForm form = new UserForm();
      // テスト用に値をセット
      form.setPassword("password123");
      form.setPasswordConfirmation("password123");
      form.setImage(createMockImage());

      // BindingResultの mock を用意し、hasErrors() が false を返すようにする
      when(bindingResult.hasErrors()).thenReturn(false);

      // サービスの呼び出し引数を修正（userEntityとMultipartFileの2引数に対応）
      doThrow(new RuntimeException("DBエラー等"))
        .when(userService).createUserWithEncryptedPassword(any(UserEntity.class), any(MultipartFile.class));

      // 実行
      Model model = new ExtendedModelMap();
      String result = userController.createUser(form, bindingResult, model);

      // 検証: 返り値がリダイレクト先と一致するか
      assertThat(result, is("redirect:users/register"));
    }

    // メールアドレスの一意性
    @Test
    public void emailが既に存在する場合に新規登録画面に戻りエラーメッセージが登録される() {
      // 1. 準備：テスト用のフォームデータを作成
      UserForm userForm = new UserForm();
      userForm.setEmail("already@exists.com");
      // パスワード一致チェックを通すために同じ値を設定
      userForm.setPassword("password");
      userForm.setPasswordConfirmation("password");
      userForm.setImage(createMockImage());

      // 2. モックの設定
      when(userRepository.existsByEmail("already@exists.com")).thenReturn(true);

      // rejectValueされた時に hasErrors() が true を返すように設定
      when(bindingResult.hasErrors()).thenReturn(true);

      Model model = new ExtendedModelMap();

      // 3. 実行
      String viewName = userController.createUser(userForm, bindingResult, model);

      // 4. 検証
      assertThat(viewName, is("users/register"));
      verify(bindingResult).rejectValue("email", "null", "メールアドレスは既に存在します");
    }
  }

  @Nested
  class ログイン {
    // ログイン
    @Test
    public void ログイン機能にリクエストするとログイン画面のビューファイルがレスポンスで返ってくる() {
      String result = userController.showLogin();

      assertThat(result, is("users/login"));
    }

    @Test
    public void ログインに失敗したユーザーに対してエラーメッセージを表示させる() {
      // テスト用の空のModelオブジェクトを作成
      Model model = new ExtendedModelMap();
      // URLパラメータ ?error= の値を想定したダミー文字列
      String errorParam = "true";

      // メソッドを呼び出す
      String result = userController.showLoginWithError(errorParam, model);

      // 返り値のビュー名が正しいか確認
      assertThat(result, is("users/login"));

      // Modelの中に "loginError" という名前でメッセージが格納されているか確認
      assertThat(model.getAttribute("loginError"), is("メールアドレスまたはパスワードが無効です。"));
    }
  }

  @Test
  public void 詳細ページに移動したら適切なView画面が表示される() {
    // userService実行時、mockUserオブジェクト収得
    when(userService.findUserDetail(mockUser.getId())).thenReturn(mockUser);
    // model定義
    Model model = new ExtendedModelMap();
    // Controller実行とresultにその結果を入れる
    String result = userController.showUserDetail(mockUser.getId(), model, authentication);
    // 結果View画面が詳細ページなのか確認
    assertThat(result, is("users/show"));
    // ページにmodelがちゃんと渡されてるか確認
    assertThat(model.getAttribute("user"), is(mockUser));
    assertThat(model.getAttribute("prototypes"), is(mockUser.getPrototypes()));
  }

  @Nested
  class ユーザー削除 {
    @Test
    void ユーザー削除成功時successと200変換() {

      when(authentication.isAuthenticated()).thenReturn(true);
      when(authentication.getPrincipal()).thenReturn(userDetails);

      ResponseEntity<String> result = userController.deleteUser(mockUser.getId(), authentication, request, response);

      assertEquals(HttpStatus.OK, result.getStatusCode());
      assertEquals("success", result.getBody());

      verify(userService, times(1)).deleteUser(mockUser.getId(), userDetails);
    }

    @Test
    void ログインしてないと401() {

      Authentication unauthenticated = null;

      ResponseEntity<String> result = userController.deleteUser(mockUser.getId(), unauthenticated, request, response);

      assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
      verify(userService, never()).deleteUser(anyInt(), any());
    }

    @Test
    void 削除処理中例外500() {

      when(authentication.isAuthenticated()).thenReturn(true);
      when(authentication.getPrincipal()).thenReturn(userDetails);
      doThrow(new RuntimeException("DB 削除 エラー")).when(userService).deleteUser(mockUser.getId(), userDetails);
      ResponseEntity<String> result = userController.deleteUser(mockUser.getId(), authentication, request, response);
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
      assertTrue(result.getBody().contains("DB 削除 エラー"));
    }
  }
}
