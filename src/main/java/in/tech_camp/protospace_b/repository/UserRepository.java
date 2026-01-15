package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import in.tech_camp.protospace_b.entity.UserEntity;

@Mapper
public interface UserRepository {

  //ビューからデータ取得→エンティティに入れる
  @Insert("INSERT INTO users (name, email, password, profile, department, position) VALUES (#{name}, #{email}, #{password}, #{profile}, #{department}, #{position})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(UserEntity user);

  //すでに使用されているEmailを検索
  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existsByEmail(String email);
  
  //データベースから特定のメールアドレスを持つユーザーを探してくる
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);

  
}

