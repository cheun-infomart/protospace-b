package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.UserEntity;

@Mapper
public interface UserRepository {
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  @Select("SELECT id, name FROM users WHERE id = #{id}")
  UserEntity findUserById(Integer id);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);

  @Insert("INSERT INTO users (name, email, password, profile, department, position) VALUES (#{name}, #{email}, #{password}, #{profile}, #{department}, #{position})")
  void insert(UserEntity user);
}

