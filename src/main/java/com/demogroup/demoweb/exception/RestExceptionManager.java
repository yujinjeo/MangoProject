package com.demogroup.demoweb.exception;

import jakarta.persistence.ElementCollection;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

//restconrollerAdvice 어노테이션은 json 형식으로 response를 반환할 수 있다.
@RestControllerAdvice
public class RestExceptionManager {
    //appexception 핸들러
    @ExceptionHandler
    public ResponseEntity<?> appExceptionHandler(AppException e){
        return ResponseEntity.status(e.getErrorCode().getHttpStatus())
                .body(e.getErrorCode().name()+" "+e.getMessage());
    }

    //@PasswordValid 어노테이션 에러났을 때 핸들러
    /* @Valid 에 대해 설명해보려고 한다.
    * 보통 필드 값에 대해 간단한 확인 (0 이상인지, string 값이 null은 아닌지, True인지 False인지 등)을 일일이 함수 내부에서 if문으로 처리하고
    * 오류 시 매번 AppException을 발생시키는 것은 비효율적이다.
    *
    * @Valid(총괄), @Null, @NotNull, @NotBlank, @NotEmpty, @Size(), @Pattern(), @Min(), @Max(), @Email() (잘 쓰지 않는다), @Positive, @NegativeOrZero 등
    *
    * spring에서는 null여부, 정수의 범위, 문자열 size의 충족 여부, 문자열 패턴 등 필드값이나 타입에 대해 validate 하는 어노테이션을 제공한다.
    * 타입 안에 다양한 종류의 validation 어노테이션을 넣어놓고, 타입 전체를 확인하고 싶다면 @Valid 어노테이션을 해당 필드/매개변수 앞에 넣어
    * 타입 내부의 validation 을 각각 조사한다. 이때, 조사한 @Valid에 해당하는 모든 오류는 MethodArgumentValidException으로 통일되는데,
    * 이 오류로부터 validation만을 위한 custom 오류 확인 필드와 메소드들이 존재한다.
    *
    * 그것은 바로 BindResults!! BindResults 라는 객체는 원래 @Valid 어노테이션이 붙은 매개변수 바로 뒤에 위치하여 오류가 발생할 시
    * 오류가 난 값과 해당 validation 어노테이션에 정의되어 있는 message들을 FieldError라는 객체에 저장하고, 여러 FieldEerror 들을
    * List 형식으로 보관하여, validation 에러가 난 이유와 오류 원인 값을 저장해둔다. 말하자면 오류 원인 저장소인 것이다.
    *
    * BindResults 객체는 getFieldErrors() 메소드를 통해 FieldError 들이 있는 List들을 반환하고,
    * StringBuilder를 통해 error body에 넣을 오류 메세지들을 완성할 수 있다.
    * 각 FieldError는 다음과 같은 3가지의 메소드를 제공하여 에러 메세지를 효과적이고 가시적으로 작성할 수 있게 한다.
    * - getField() : 오류가 발생한 필드명
    * - getDefaultMessage() : 해당 validation 어노테이션에 정의된 오류 메세지
    * - getRejectedValue() : 오류가 난 값
    *
    * 이 메소드들을 통해 더 체계적인 validation 오류 메세지를 작성할 수 있따!!!
    *
    * */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> validAnnotationHandler(MethodArgumentNotValidException e){
        BindingResult bindingResult = e.getBindingResult();
        StringBuilder sb=new StringBuilder();
        for (FieldError error: bindingResult.getFieldErrors()){
            sb.append("[[ ");
            sb.append(error.getField());
            sb.append(" ]] ");
            sb.append(error.getDefaultMessage());
            sb.append(" || 입력된 값 : ");
            sb.append(error.getRejectedValue());
        }

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<String> loginExceptionHandler(AuthenticationException e){
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
    }


}
