package com.demogroup.demoweb.utils.annotation;

import com.demogroup.demoweb.utils.validator.PasswordValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/*
* PasswordValid 어노테이션을 정의하여, password가 유효한 문자열인지를 확인하는
* 것을 메소드 방식 등으로 정의하는 대신 이렇게 어노테이션으로 정의해주면
* 더 깔끔하고, 뭘 하려는지에 대한 기능을 가독성 있게 나타낼 수 있다.
* 자주 사용되는 작은 기능이므로 이렇게 유효성 검사를 위해 어노테이션을 직접 만들어서
* 사용할 수 있다.
*
* 그래서 어노테이션을 생성할 때는, ContraintValidator를 구현한 함수를 지정해서
* 유효성을 검증(@Constraint)하고,
* 필드나 파라미터 등의 위치에서 사용할 수 있게(@Target) 지정해줘야 한다.
* @Retention 어노테이션은 어노테이션을 생성할 때 반드시 시정하는 어노테이션으로,
* 어노테이션이 얼마나 지속되는지 지정해주는 어노테이션이다. 빈과 달리, 어노테이션이라는
* 스프링 기능을 custom해서 추가해준 것이기 때문에 @Documented 되어야 하고,
* 지속 시간도 지정해줘야 한다.
* */
@Documented //javadoc에 저장하여 어노테이션으로 사용할 수 있도록 한다.
@Constraint(validatedBy = PasswordValidator.class) //어떤 class로 검증을 진행할 것인지 지정
@Target({ElementType.FIELD, ElementType.PARAMETER}) //이 어노테이션을 DTO의 필드에 붙일 수 있고, 메소드의 파라이터에 넣을 수도 있다.
@Retention(RetentionPolicy.RUNTIME) //이 어노테이션 관리는 runtime동안 지속
public @interface PasswordValid {
    String message() default "유효하지 않은 비밀번호입니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
