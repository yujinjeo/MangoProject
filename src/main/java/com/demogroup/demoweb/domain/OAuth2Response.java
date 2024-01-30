package com.demogroup.demoweb.domain;

public interface OAuth2Response {


    //제공자 : naver
    String getProvider();

    //제공자가 발급해주는 아이디 (번호)
    //네이버에서 오는 데이터는 다음과 같다. resultcode=00, message=success, response={id=123123123, name=개발자유미}
    //여기서 id를 말하는 거다.
    String getProviderId();
    String getEmail();
    String getName();
}
