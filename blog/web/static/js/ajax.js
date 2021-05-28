function ajax(url,data,fnSucc,fnFaild) {
    var xmlhttp;
    //1.创建xmlhttp对象
    if (window.XMLHttpRequest) {
        xmlhttp = new XMLHttpRequest();
    } else {
        xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
    }
    //2.连接服务器
    xmlhttp.open("POST", url, true);
    xmlhttp.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
    //3.发送请求
    xmlhttp.send(data);
    //4.接收返回
    xmlhttp.onreadystatechange = function () {
        //xmlhttp.readyState  //浏览器和服务器进行到哪一步
        if (xmlhttp.readyState == 4 && xmlhttp.status == 200) { //200等于成功
            fnSucc(xmlhttp.responseText);
        } else {
            if (fnFaild) {
                fnFaild(xmlhttp.status);
            }
        }
    }
}
