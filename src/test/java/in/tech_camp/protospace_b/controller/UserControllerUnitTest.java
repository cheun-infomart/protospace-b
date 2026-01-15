package in.tech_camp.protospace_b.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUnitTest {
  @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    //新規登録
    @Test
    public void 新規登録機能にリクエストすると新規登録画面のビューファイルがレスポンスで返ってくる() {
        Model model = new ExtendedModelMap(); // Modelを準備
        String result = userController.showRegister(model); 
        assertThat(result, is("users/register"));
    }

    @Test
    public void 新規登録に失敗したユーザーに対してエラーメッセージを表示させる(){
      // 1. 準備
      UserForm userForm = new UserForm();
      // テスト用に値をセット
      userForm.setPassword("password123");
      userForm.setPasswordConfirmation("password123");
      
      BindingResult bindingResult = mock(BindingResult.class);
      Model model = new ExtendedModelMap();

      // hasErrors()がtrueを返すように設定（失敗ルートを通すため）
      when(bindingResult.hasErrors()).thenReturn(true);

      // 2. 実行：正しい引数で呼び出す
      String result = userController.createUser(userForm, bindingResult, model);

      // 3. 検証
      assertThat(result, is("users/register"));
    }

    //メールアドレスの一意性
    @Test
    public void emailが既に存在する場合に新規登録画面に戻りエラーメッセージが登録される() {
        // 1. 準備：テスト用のフォームデータを作成
        UserForm userForm = new UserForm();
        userForm.setEmail("already@exists.com");
        // パスワード一致チェックを通すために同じ値を設定
        userForm.setPassword("password");
        userForm.setPasswordConfirmation("password");

        // 2. モックの設定
        when(userRepository.existsByEmail("already@exists.com")).thenReturn(true);

        // BindingResultのモックを作成（Mockito.mockを使用）
        BindingResult bindingResult = mock(BindingResult.class);
        // rejectValueされた時に hasErrors() が true を返すように設定
        when(bindingResult.hasErrors()).thenReturn(true); 
        
        Model model = new ExtendedModelMap();

        // 3. 実行
        String viewName = userController.createUser(userForm, bindingResult, model);

        // 4. 検証
        assertThat(viewName, is("users/register"));
        verify(bindingResult).rejectValue("email", "null", "メールアドレスは既に存在します");
    }

    //ログイン
    @Test
    public void ログイン機能にリクエストするとログイン画面のビューファイルがレスポンスで返ってくる() {
        String result = userController.showLogin();
        assertThat(result,is("users/login"));
    }

    @Test
    public void ログインに失敗したユーザーに対してエラーメッセージを表示させる(){
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
