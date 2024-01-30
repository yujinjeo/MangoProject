    const main={
    init : function(){
        const _this=this;

        $('#join_button').on('click',function(){
            _this.join();
        });
    },

    join : function (){
        var formData={
            uid: $("input[name='uid']").val(),
            name : $("input[name='name']").val(),
            nickname: $("input[name='nickname']").val(),
            username: $("input[name='username']").val(),
            password:$("input[name='password']").val(),
            email:$("input[name='email']").val(),
            role:$("input[name='role']").val()
        };

        $.ajax({
            type: "POST",
            url: "/api/user/join",
            dataType:'JSON',
            contentType: 'application/json; charset=utf-8',
            data:JSON.stringify(formData),
            success: function(response){
                console.log("회원가입 성공:",response);
                $.ajax({
                    type:"POST",
                    url: "/user/login",
                    contentType: 'application/json; charset=utf-8',
                    data: JSON.stringify(response),
                    success: function(){
                        console.log("로그인 성공");
                    },
                    error: function(xhr, status, error){
                        console.error("로그인 오류",error);
                    }
                });
            },
            error: function(xhr, status, error){
                console.error("회원가입 오류 : ", status, error);
            }
        });
    }
};
main.init();