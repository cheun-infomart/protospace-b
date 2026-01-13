package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.UserEntity;

@Mapper
public interface UserRepository {
  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);
}
