package com.demogroup.demoweb.utils;

import com.demogroup.demoweb.domain.dto.UserDTO;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MakeJsonUtil {
    public static JSONObject makeJoinJson(String username, String password) throws ParseException {
        String jsonStr="{\"username\":\""+username+"\", \"password\":\""+password+"\"}";
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);

        return jsonObject;

    }

    public static JSONObject makeModifyJson(UserDTO dto) throws ParseException {
        String jsonStr="{\"name\":\""+dto.getName()+"\", \"nickname\":\""+dto.getNickname()+"\", \"username\":\""
                +dto.getUsername()+"\", \"password\":\""+dto.getPassword()+"\",\"email\":\""+dto.getEmail()+"\", \"role\":\""
                +dto.getRole()+"\"}";
        JSONParser parser = new JSONParser();
        JSONObject jsonObject = (JSONObject) parser.parse(jsonStr);

        return jsonObject;

    }

}
