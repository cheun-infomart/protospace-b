package in.tech_camp.protospace_b.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;

import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class UserControllerUnitTest {
  @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserController userController;

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
