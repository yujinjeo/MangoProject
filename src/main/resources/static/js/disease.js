
//이 함수는 disease/home.html 에서 사진을 업로드 했을 때 사진을 미리보기하는 함수입니다.
function preDisplay(){
    var image=document.getElementById("mango-image");
    var preview=document.getElementById('preview')

    if (image.files && image.files[0]){
        var reader=new FileReader();

        //fileReader가 파일을 읽어 url로 변환하면, 그 url이 preview의 src가 되도록 설정
        reader.onload=function(e){
            preview.src=e.target.result;
        }

        //파일 객체를 읽은 후 data url로 변환한다.
        reader.readAsDataURL(image.files[0])
    }

}