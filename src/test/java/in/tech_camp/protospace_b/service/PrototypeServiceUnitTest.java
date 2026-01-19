package in.tech_camp.protospace_b.service;

import java.io.IOException;
import java.nio.file.Path;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import in.tech_camp.protospace_b.ImageUrl;
import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.PrototypeFormFactory;
import in.tech_camp.protospace_b.form.PrototypeForm;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
public class PrototypeServiceUnitTest {
	@Mock
	private PrototypeRepository prototypeRepository;

	@Mock
	private UserRepository userRepository;

	@Mock
	private ImageUrl imageUrl;

	@Mock
	private Authentication authentication;

	@Mock
	private MultipartFile multipartFile;

	@InjectMocks
	private PrototypeService prototypeService;

	private PrototypeEntity prototype;
	private UserEntity user;

	@TempDir
	Path tempDir;

	@BeforeEach
    public void setUp() throws IOException {
			// プロトタイプを作成しセットする
			prototype = new PrototypeEntity();
			prototype.setId(1);
			prototype.setName("テストプロトタイプ");
			prototype.setImage("old_image.png");
			
			// ユーザーを作成しセットする
			user = new UserEntity();
			user.setId(100);
			user.setEmail("test@example.com");

			// 画像処理モックを用意
			lenient().when(multipartFile.isEmpty()).thenReturn(false);
			lenient().when(multipartFile.getOriginalFilename()).thenReturn("test.png");
			lenient().when(multipartFile.getInputStream()).thenReturn(new java.io.ByteArrayInputStream(new byte[0]));
			lenient().when(imageUrl.getImageUrl()).thenReturn(System.getProperty("java.io.tmpdir"));
    }

	@Nested
	class 新規投稿保存機能 {

		@Nested
		class 保存に成功する場合 {

			@Test
			public void 正常に保存される場合はEntityにユーザーがセットされinsertが呼ばれる() throws IOException {
				when(authentication.isAuthenticated()).thenReturn(true);
				when(authentication.getName()).thenReturn("test@example.com");
				when(userRepository.findByEmail("test@example.com")).thenReturn(user);

				// 保存メソッドを実行
				prototypeService.createPrototype(prototype, multipartFile, authentication);

				// 検証
				// EntityにUserオブジェクトがセットされているか
				assertThat(prototype.getUser(), is(user));
				// 画像パスがセットされているか
				assertThat(prototype.getImage(), containsString("/uploads/"));
				// Repositoryのinsertが呼ばれたか
				verify(prototypeRepository, times(1)).insert(prototype);
			}
		}

		@Nested 
		class エラーになる場合 {

			@Test
			public void 未ログインの場合はRuntimeExceptionが発生する() throws IOException {
				// ログイン認証されていない状態を作る
				when(authentication.isAuthenticated()).thenReturn(false);

				// assertThrows は「RuntimeExceptionが発生すること」を保証し、発生した例外を戻り値で返す
				RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
					RuntimeException.class, 
					() -> {
							prototypeService.createPrototype(prototype, multipartFile, authentication);
					}
				);

				// エラーメッセージを検証
				assertThat(exception.getMessage(), is("未ログインです"));

				// 保存処理が呼ばれていないことを確認
				verify(prototypeRepository, never()).insert(any());
			}

			@Test
			public void ログインはしているがユーザーがDBに見つからない場合はRuntimeExceptionが発生する() throws IOException {
				// 認証は通るが、DB検索結果がnullになる状態を作る
				when(authentication.isAuthenticated()).thenReturn(true);
				when(authentication.getName()).thenReturn("ghost@example.com");
				when(userRepository.findByEmail("ghost@example.com")).thenReturn(null);

				// assertThrows は「RuntimeExceptionが発生すること」を保証し、発生した例外を戻り値で返す
				RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
					RuntimeException.class, 
					() -> {
							prototypeService.createPrototype(prototype, multipartFile, authentication);
					}
				);

				// エラーメッセージを検証
				assertThat(exception.getMessage(), is("ユーザーが見つかりません"));

				// 保存処理が呼ばれていないことを確認
				verify(prototypeRepository, never()).insert(any());
			}
		}
	}

	@Nested
  class プロトタイプ削除機能 {

    @Mock
    private CustomUserDetails customUserDetails;

    @Nested
    class 正常に削除される場合 {

      @Test
      public void ログインユーザー自身の投稿であれば削除処理が実行される() {
        Integer prototypeId = 1;
        Integer userId = 100;

        // プロトタイプの所有者をセット
        UserEntity owner = new UserEntity();
        owner.setId(userId);
        prototype.setUser(owner);

        // モックの設定
        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
        when(customUserDetails.getId()).thenReturn(userId); // IDを一致させる

        prototypeService.deletePrototype(prototypeId, customUserDetails);

        // リポジトリのdeleteByIdが1回呼ばれたか検証
        verify(prototypeRepository, times(1)).deleteById(prototypeId);
      }
    }

    @Nested
    class エラーになる場合 {

			@Test
      public void 削除対象のプロトタイプが存在しない場合はRuntimeExceptionが発生する() {
        Integer prototypeId = 1;
        when(prototypeRepository.findById(prototypeId)).thenReturn(null);

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
          RuntimeException.class, 
          () -> {
              prototypeService.deletePrototype(prototypeId, customUserDetails);
          }
        );

        // エラーメッセージの確認
        assertThat(exception.getMessage(), is("削除対象が見つかりません"));
        // 削除処理が呼ばれていないことを確認
        verify(prototypeRepository, never()).deleteById(any());
      }

      @Test
      public void 他人の投稿を削除しようとした場合はRuntimeExceptionが発生する() {
        Integer prototypeId = 1;
        Integer myId = 100;
        Integer otherId = 999;

        // プロトタイプ(ID:1)の所有者は他人(ID:999)にする
        UserEntity otherUser = new UserEntity();
        otherUser.setId(otherId);
        prototype.setUser(otherUser);

        // モックの設定
        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
        when(customUserDetails.getId()).thenReturn(myId); // IDをmyIdの100にする（所有者と不一致）

        RuntimeException exception = org.junit.jupiter.api.Assertions.assertThrows(
          RuntimeException.class, 
          () -> {
              prototypeService.deletePrototype(prototypeId, customUserDetails);
          }
        );

        // エラーメッセージの確認
        assertThat(exception.getMessage(), is("削除権限がありません"));
        // 削除処理が呼ばれていないことを確認
        verify(prototypeRepository, never()).deleteById(any());
      }
    }
  }

	@Nested
  class 編集機能 {
    
    @Test
    public void 画像含めてアップデートできるか() throws IOException {

      PrototypeForm form = PrototypeFormFactory.createPrototype(); 

      when(prototypeRepository.findById(1)).thenReturn(prototype);

      when(imageUrl.getImageUrl()).thenReturn(tempDir.toString());

      prototypeService.updatePrototype(1, form);

      assertThat(prototype.getName(), is(form.getName()));
      assertThat(prototype.getImage(), containsString("test_image.png"));
      verify(prototypeRepository, times(1)).update(prototype);
    }

    @Test
    public void 画像は既存の画像でアップデートできるか() throws IOException {

      PrototypeForm form = PrototypeFormFactory.createPrototype();
      form.setImage(null); 

      when(prototypeRepository.findById(1)).thenReturn(prototype);
    
      prototypeService.updatePrototype(1, form);


      assertThat(prototype.getName(), is(form.getName()));
      assertThat(prototype.getImage(), is("old_image.png")); 
      verify(prototypeRepository, times(1)).update(prototype);
    }
  }
}
