一、点击事件

1、调用函数的方式

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<button onclick="clickFuc()">点我</button>
<script>
    function clickFuc() {
        alert("你点了我");
    }
</script>
</body>
</html>
```

2、通过document访问元素，设置onclick（匿名函数）

```
<body>
<button id="btn1">点我</button>
<script>
    document.getElementById("btn1").onclick=function () {
        alert("hello");
    }
</script>
</body>
</html>
```

二、修改元素属性

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Title</title>
</head>
<body>
<a>百度</a><br/><a>百度</a><br/><a>百度</a><br/><a>百度</a><br/>
<script>
    var link = document.getElementsByTagName('a');
    for(var i = 0; i<link.length;i++){
        link[i].href="http://www.baidu.com";
        link[i].target="_blank";
    }
</script>
</body>
</html>
```

onfocus聚焦  onblur失焦

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>focus</title>
</head>
<body>
<input type="text"/>
</body>

<script>
    window.onload=function () {
        var ein = document.getElementsByTagName("input")[0];
        ein.onfocus=function () {
            console.log("聚焦");
        }
        ein.onblur=function () {
            console.log("失焦");
        }
    }
</script>
</html>
```

鼠标的移入和移出

```
<body>
<img src="img/img1.jpg" id="img" width="200px"/>
<br/>
<span id="span"></span>
<script>
    window.onload=function () {
        document.getElementById('img').onmouseover=function () {
            document.getElementById('span').innerHTML = "this is a beauty";
        }
        document.getElementById('img').onmouseleave=function () {
            document.getElementById('span').innerHTML = "";
        }
    }
</script>
</body>
```

简单级联

```
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>级联</title>
</head>
<body>

      省：<select name="" id="province">
          <option value="">-请选择-</option>
      </select>

      市：<select name="" id="city">
          <option value="">-请选择-</option>
      </select>

<script>

    var provinces = ['江苏','山东','广东'];
    var cities = [
        ['南京','盐城','扬州'],
        ['烟台','青岛','龙口'],
        ['东莞','惠州','衡水']
    ];
    window.onload=function () {
        var prinviceHtml = '<option value="0">'+'-请选择-'+'</option>';
        for(var i=0;i<provinces.length;i++){
          prinviceHtml = prinviceHtml +  '<option value='+i+'>'+provinces[i]+'</option>';
        }
        document.getElementById("province").innerHTML = prinviceHtml;
      
        document.getElementById("province").onchange = function () {
            var cityHtml = '<option>'+'-请选择-'+'</option>';
            var value = document.getElementById("province").value;
            for(var i =0;i<cities[value].length;i++){
                cityHtml = cityHtml+'<option>'+cities[value][i]+'</option>';
            }
            document.getElementById("city").innerHTML = cityHtml;
        }
    }
</script>
</body>
</html>
```

