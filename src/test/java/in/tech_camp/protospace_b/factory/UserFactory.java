package in.tech_camp.protospace_b.factory;

import java.util.ArrayList;

import com.github.javafaker.Faker;

import in.tech_camp.protospace_b.entity.UserEntity;
import in.tech_camp.protospace_b.form.UserForm;

public class UserFactory {
    private static final Faker faker = new Faker();

    public static UserEntity createMockUser() {
        UserEntity user = new UserEntity();
        user.setId(1);
        user.setName("TestName");
        user.setProfile("TestProfile");
        user.setDepartment("TestDepartment");
        user.setPosition("TestPosition");
        user.setPrototypes(new ArrayList<>());
        return user;
    }

    public static UserForm createUser(){
        UserForm userForm = new UserForm();
        userForm.setName(faker.name().fullName());
        userForm.setProfile(faker.book().title());
        userForm.setDepartment(faker.dog().breed());
        userForm.setPosition(faker.cat().breed());
        return userForm;
    }
}
