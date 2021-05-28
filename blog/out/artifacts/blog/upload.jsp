<%--
  Created by IntelliJ IDEA.
  User: Administrator
  Date: 2021/5/27
  Time: 10:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>Title</title>
    <script src="common/js/jquery-3.2.1.js"></script>
    <script src="common/js/jquery.form.min.js"></script>
    <script src="common/js/layer-3.1/layer.js"></script>
<script>
    $(function (){
       $("#btn1").on("click",function (){
           $("#upload").click();
       })
    })
</script>
    <script>
        $(function (){
             $("#upload").on("change",function(){
                $("#form1").ajaxSubmit({
                     dataType:"text",
                    url:"http://"+document.domain+"/testupload",
                    data:{},
                    success:function(res){
                         var str=eval("("+res+")");
                          if(str.msg==="ok"){
                            $("#imgDiv").css("display","block");
                          var img1=document.getElementById("img1");
                          img1.src=str.imgurl;
                            layer.msg("上传成功");
                         }else{
                            layer.msg(str.msg);
                        }
                    },
                    error:function(err){
                       layer.msg("上传失败,请重试");
                       console.log(err);
                    }
                })
            })
        })
    </script>
</head>
<body>
<body>
<h1>文件上传实例 - 菜鸟教程</h1>
<div><input id="btn1" type="button" value="选择文件" style="width: 80px;height: 50px;background: blue;color: #ffffff"></div>
<div style="display: none">
    <form id="form1" onsubmit="return false" action="" method="post">
    <input id="upload" type="file" name="uploadFile"/>
</form>
</div>
<div id="imgDiv" style="display: none">
    <img id="img1" src="" style="max-width: 500px;max-height:2000px"/>
</div>
</body>
</html>
