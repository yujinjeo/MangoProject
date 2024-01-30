package com.demogroup.demoweb.utils.validator;

import com.demogroup.demoweb.utils.annotation.PasswordValid;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

//커스텀 어노테이션이 어떻게 유효성 검사를 수행할지 정의해놓은 클래스
/*이것은 @PasswordValid 라는 어노테이션의 유효성 검사 기능을 따로 정의해놓은 클래스이다.
즉, 어노테이션의 기능을 정의해놓았다고 보면 된다.
유효성을 검사하는 기능이 되기 위해서는 ConstraintValidator를 구현한 객체여야 한다. 이
객체를 뜯어보면, 제네릭 클래스인데,
ConstraintValidator<A,T> 에서 A는 어노테이션 이름이고, T는 유효성을 검사하고자 하는
객체의 타입이다. 여기서는 검사하고자 하는 password의 타입이 String이므로
String을 써주었다. 여기서는 invalid 라는 함수를 통해 실질적인 유효성을 검사하고 boolean을 리턴한다.
Pattern 이라는 java 내 문자열 메소드를 통해 정의한 password가 pattern에 맞는 문자열인지 여부를 리턴한다.
* */
public class PasswordValidator implements ConstraintValidator<PasswordValid, String> {

    /*java에서 정규표현식(리터럴은 final로 선언된 고정된 값을 의미한다. 상수와 비슷한 것.) 을 정의하는 방식은 다음과 같다.
    . : 문자 1 개
    ? : 앞에 나오는 문자 혹은 그룹('()')이 0개 또는 1개
    * : 앞에 나오는 문자 혹은 그룹이 0개 이상
    + : 앞에 나오는 문자 혹은 그룹이 1개 이상
    //d : 0-9 숫자
    //w : 알파벳 대소문자 + 숫자 + _
    //s : 탭을 허용
    [^] : 해당 문자열 제외
    ^ 로 시작 : 패턴 시작
    $ 로 끝 : 패턴 종료
    {n} : 앞 문자가 딱 그 숫자만큼
    {n,m} : 문자가 n개 이상, m개 이하
    {n, } : 문자가 n개 이상
    (?=$) : 전방탐색. $로 시작하는 문자열을 탐색한다는 뜻인데 $는 포함하지 않는다는 뜻이다. 여기 문자열에 쓴 것처럼 (?=.*[0-9]) 처럼 쓰면,
     적어도 숫자 하나는 포함한다는 의미가 된다.
     적어도 하나를 포함하나는 의미를 사용할 때에는 [0-9]+ 이렇게 하면 편하다.
    () : 괄호는 그룹을 의미한다.
    (?<=com) : 후방탐색. 끝자리가 com으로 끝나는 문자열을 탐색한다는 뜻이다.
    */
    private static final String PASSWORD_PATTERN="^(?=.*[a-zA-Z])(?=.*\\d)(?=.*[@$!_%*?&])[A-Za-z\\d\\w@$!%*?&]{8,16}$";

    @Override
    public void initialize(PasswordValid constraintAnnotation) {

    }

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        return Pattern.matches(PASSWORD_PATTERN,password);
    }
}
