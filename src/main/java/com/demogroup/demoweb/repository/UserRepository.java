package com.demogroup.demoweb.repository;

import com.demogroup.demoweb.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    boolean existsByUsername(String username);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);

    /* @Modifying 어노테이션을 붙여서 이 코드가 update나 delete하는 코드임을 알려준다.
    * 그러면 int 반환 없이 void로 반환할 수 있다.
    * @Modifying을 붙여주지 않으면, 수정 쿼리이므로 executeUpdate()가 작동하는데, 이는 int를 반환하기 때문에 int를 반환하도록 써줘야 한다.
    * ->그러나 무슨 일인지 이 경우 오류가 났다. 왜 그런지는 다시 알아봐야겠다.
    *
    * nativeQuery를 붙여주는 이유는 이 쿼리가 SQL 쿼리라는 걸 알려주기 위해서이다.
    * default인 nativeQuery=False는 객체지향 쿼리인 JPQL이다.
    * ->JPQL을 공부할 필요가 있다.
    *
    * 궅이 @Param을 쓰지 않아도(그건 jpql), sql 쿼리라면 이렇게 매개변수 순서대로 숫자를 (?1, ?2 ...) 대입해 사용할 수 있다.
    * */
    @Modifying
    @Query(value = "update user u set u.name=?1, u.username=?2," +
            "u.nickname=?3, u.email=?4 where u.uid=?5",
    nativeQuery = true)
    void modifyUserData(String name,String username, String nickname, String email, Long uid);

}
