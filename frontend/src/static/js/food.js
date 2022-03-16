import Resource from '@/static/js/Resource';

//食物的初始化
function Food(x,y,width,height,img){
    this.x = x || 0;
    this.y = y || 0;
    this.width = width || 30;
    this.height = height || 30;
    this.img = img || Resource.apple;//默认苹果   
    this.div = null;
}
// 初始化新食物
Food.prototype.initFood = function(map,xx,yy){
    this.removeDiv();
    let myFood = document.createElement("div");
    this.div = myFood;//需要记录食物的div盒子，日后重新生成时需要清空
    myFood.style.width = this.width + "px";
    myFood.style.height = this.height + "px";
    myFood.style.background = `url(${this.img}) no-repeat center`
    myFood.style.backgroundSize = `${this.width}px ${this.height}px`;
    myFood.style.position = "absolute";
    this.x = (xx || Math.floor(Math.random()* (map.offsetWidth/this.width)))  * this.width;
    this.y = (yy || Math.floor(Math.random()* (map.offsetHeight/this.height))) * this.height;
    myFood.style.left = this.x + "px";
    myFood.style.top = this.y +"px";

    map.appendChild(myFood);
    console.log("append food：",this.x,this.y);
}
// 移除食物
Food.prototype.removeDiv = function(){
    if(this.div){//如果先前存在
        this.div.parentNode.removeChild(this.div);
        this.div = null;
    }
}

export default Food