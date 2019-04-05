console.log("Buuya!");
$(document).ready(function(){
 //$("#imgmy").on("onfocus",function(){console.log("it is did ");});
 var textForView=$("#titleImg").attr("titleImgAttr");
 $("#imgmy").attr("title", textForView);
 console.log("value=",textForView);

});


console.log("Buuya2!");