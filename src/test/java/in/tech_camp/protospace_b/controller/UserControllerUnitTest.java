package in.tech_camp.protospace_b.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.UserFactory;
import in.tech_camp.protospace_b.form.UserForm;
import in.tech_camp.protospace_b.repository.UserRepository;
import in.tech_camp.protospace_b.service.UserService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUnitTest {

    @Mock
    private UserService userService; 

    @Mock
    private BindingResult bindingResult; 

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

    private UserEntity mockUser;

    @BeforeEach
    public void setUp() {
        mockUser = UserFactory.createMockUser();
    }

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
      
      Model model = new ExtendedModelMap();

      // hasErrors()がtrueを返すように設定（失敗ルートを通すため）
      when(bindingResult.hasErrors()).thenReturn(true);

      // 2. 実行：正しい引数で呼び出す
      String result = userController.createUser(userForm, bindingResult, model);

      // 3. 検証
      assertThat(result, is("users/register"));
    }

    @Test
    public void ユーザー登録中に例外が発生した場合は登録画面にリダイレクトされる() {
      // 準備: バリデーションエラーがない状態を作る
      UserForm form = new UserForm();

      // --- ここを追加 ---
      form.setPassword("password123");
      form.setPasswordConfirmation("password123");

      // BindingResultの mock を用意し、hasErrors() が false を返すようにする
      when(bindingResult.hasErrors()).thenReturn(false);
    
      // any() を使うことで引数の内容に関わらず例外を発生させます
      doThrow(new RuntimeException("DBエラー等")).when(userService).createUserWithEncryptedPassword(any(UserEntity.class));

      // 実行
      Model model = new ExtendedModelMap();
      String result = userController.createUser(form, bindingResult, model);

      // 検証: 返り値がリダイレクト先と一致するか
      assertThat(result, is("redirect:users/register"));
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

    @Test
    public void 詳細ページに移動したら適切なView画面が表示される() {
      //userService実行時、mockUserオブジェクト収得
      when(userService.findUserDetail(mockUser.getId())).thenReturn(mockUser);
      //model定義
      Model model = new ExtendedModelMap();
      //Controller実行とresultにその結果を入れる
      String result = userController.showUserDetail(mockUser.getId(), model);
      // 結果View画面が詳細ページなのか確認
      assertThat(result, is("users/show"));
      // ページにmodelがちゃんと渡されてるか確認
      assertThat(model.getAttribute("user"), is(mockUser));
      assertThat(model.getAttribute("prototypes"), is(mockUser.getPrototypes()));
    }
  
}