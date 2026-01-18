package in.tech_camp.protospace_b.factory;

import java.util.ArrayList;

import in.tech_camp.protospace_b.entity.UserEntity;

public class UserFactory {
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
}
