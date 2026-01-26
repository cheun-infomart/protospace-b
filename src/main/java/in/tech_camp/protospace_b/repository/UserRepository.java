package in.tech_camp.protospace_b.repository;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Many;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import in.tech_camp.protospace_b.entity.UserEntity;

@Mapper
public interface UserRepository {

  //ビューからデータ取得→エンティティに入れる
  @Insert("INSERT INTO users (name, email, password, profile, department, position, image) VALUES (#{name}, #{email}, #{password}, #{profile}, #{department}, #{position}, #{image})")
  @Options(useGeneratedKeys = true, keyProperty = "id")
  void insert(UserEntity user);

  //すでに使用されているEmailを検索
  @Select("SELECT EXISTS(SELECT 1 FROM users WHERE email = #{email})")
  boolean existsByEmail(String email);
  
  //全データ取得
  @Select("SELECT * FROM users WHERE id = #{id}")
  UserEntity findById(Integer id);

  //必要な情報のみ取得
  @Select("SELECT id, name, image FROM users WHERE id = #{id}")
  UserEntity findUserById(Integer id);

  @Select("SELECT * FROM users WHERE email = #{email}")
  UserEntity findByEmail(String email);

  @Select("SELECT * FROM users WHERE id = #{id}")
    @Results(value = {
        @Result(property = "id", column = "id", id = true),
        @Result(property = "prototypes", column = "id", 
                many = @Many(select = "in.tech_camp.protospace_b.repository.PrototypeRepository.findByUserId"))
    })
    UserEntity findByIdWithProto(Integer id);

  // ユーザー情報更新
  @Update("UPDATE users SET name=#{name}, profile=#{profile}, department=#{department}, position=#{position} WHERE id=#{id}")
  void update(UserEntity user);
  
  @Delete("DELETE FROM users WHERE id = #{id}")
  void deleteById(Integer id);
}

