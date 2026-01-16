package in.tech_camp.protospace_b.controller;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

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

        CommentForm form = CommentFormFactory.createComment(); 

        when(currentUser.getId()).thenReturn(userId);

        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(prototypeId);
        UserEntity user = new UserEntity();
        user.setId(userId);

        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);
        when(userRepository.findById(userId)).thenReturn(user);

        String result = commentController.createComment(prototypeId, form, bindingResult, currentUser, model);
        assertThat(result, is("redirect:/prototypes/" + prototypeId));
      }
    }

    @Nested
    class 投稿に失敗する場合{

      @Test
      public void textを空にして投稿した場合投稿できうにそのページに留まること(){
        
        Integer prototypeId = 1;

        CommentForm form = CommentFormFactory.createComment(); 
        form.setText("");
        when(bindingResult.hasErrors()).thenReturn(true);
    
        PrototypeEntity prototype = new PrototypeEntity();
        prototype.setId(prototypeId);

        when(prototypeRepository.findById(prototypeId)).thenReturn(prototype);

        String result = commentController.createComment(prototypeId, form, bindingResult, currentUser, model);
        assertThat(result, is("prototypes/show"));
      }
    }
  }
}
