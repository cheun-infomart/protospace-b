package in.tech_camp.protospace_b.factory;

import com.github.javafaker.Faker;

import in.tech_camp.protospace_b.form.CommentForm;

public class CommentFormFactory {
  private static final Faker faker = new Faker();
  
  public static CommentForm createComment(){
    CommentForm commentForm = new CommentForm();
    commentForm.setText(faker.lorem().sentence());
    return commentForm;
  }
}
