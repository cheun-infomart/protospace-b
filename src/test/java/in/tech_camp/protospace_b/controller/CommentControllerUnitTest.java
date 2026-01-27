package in.tech_camp.protospace_b.controller;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import in.tech_camp.protospace_b.config.CustomUserDetails;
import in.tech_camp.protospace_b.entity.PrototypeEntity;
import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.factory.CommentFormFactory;
import in.tech_camp.protospace_b.form.CommentForm;
import in.tech_camp.protospace_b.repository.CommentRepository;
import in.tech_camp.protospace_b.repository.PrototypeRepository;
import in.tech_camp.protospace_b.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class CommentControllerUnitTest {
  @Mock
  private CommentRepository commentRepository;

  @Mock 
  private PrototypeRepository prototypeRepository;

  @Mock
  private UserRepository userRepository;

  @Mock
  private CustomUserDetails currentUser;

  @Mock
  private BindingResult bindingResult;

  @InjectMocks
  private CommentController commentController;

  private Model model;

  @BeforeEach
  public void setUp(){
    model = new ExtendedModelMap();
  }

  @Nested
  class コメント投稿機能{

    @Nested
    class 投稿に成功する場合{

      @Test
      public void textを入力して投稿できた場合投稿した詳細画面に遷移する(){
        
        Integer prototypeId = 1;
        Integer userId = 10;
        String userName = "テストネーム";
        String userImage = "/uploads/test_icon.png"; 

        CommentForm form = CommentFormFactory.createComment(); 
        form.setText("テストコメント");
        when(currentUser.getId()).thenReturn(userId);

        UserEntity user = new UserEntity();
        user.setId(userId);
        user.setName(userName);
        user.setImage(userImage);
        when(currentUser.getUser()).thenReturn(user);

        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(prototypeId);

        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
        when(userRepository.findById(userId)).thenReturn(user);

        ResponseEntity<String> response = commentController.createComment(prototypeId, form, bindingResult, currentUser);
        assertThat(response.getStatusCode(), is(HttpStatus.OK));

        assertThat(response.getBody(), containsString(form.getText()));
        assertThat(response.getBody(), containsString(userName));
        assertThat(response.getBody(), containsString(userImage));
      }
    }

    @Nested
    class 投稿に失敗する場合{

      @Test
      public void textを空にして投稿した場合プロトタイプ詳細ページに留まること(){
        
        Integer prototypeId = 1;

        CommentForm form = CommentFormFactory.createComment(); 
        form.setText("");
        
        when(bindingResult.hasErrors()).thenReturn(true);
        
        ObjectError error = new ObjectError("commentForm", "コメントを入力してください");
        when(bindingResult.getAllErrors()).thenReturn(List.of(error));

        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(prototypeId);

        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);

        ResponseEntity<String> response = commentController.createComment(prototypeId, form, bindingResult, currentUser);
        assertThat(response.getStatusCode(), is(HttpStatus.BAD_REQUEST));
        assertThat(response.getBody(), is("コメントを入力してください"));
      }
    }
  }
}
