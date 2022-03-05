import Resource from '@/static/js/Resource';
// 蛇的初始化
function Snake(width,height,direction){
    //每部分宽高
    this.width = width || 30;
    this.height = height || 30;
    this.elements = [];//渲染的 div元素
    //方向
    this.direction = direction || "right";

    // 记住蛇默认的状态，吃完食物的时候就要加一个
    this.body = [
        {
            x:3,
            y:0,
            img:Resource.snake.right ,
        },
        {
            x:2,
            y:0,
            img:Resource.snake.body,
        },
        {
            x:1,
            y:0,
            img:Resource.snake.body,
        },
    ];
}
// 构造新的蛇HTML元素
Snake.prototype.initSnake = function(map){
    
    //循环创建蛇头和蛇身
    this.body.forEach(element => {
        let obj = element;
        let mySnake = document.createElement("div");
        mySnake.style.width = this.width + "px";
        mySnake.style.height = this.height + "px";
        mySnake.style.background = `url(${obj.img}) no-repeat center`
        mySnake.style.backgroundSize = `${this.width}px ${this.height}px`;
        mySnake.style.position = "absolute";
        mySnake.style.left = obj.x * this.width + "px";
        mySnake.style.top = obj.y * this.height +"px";
        map.appendChild(mySnake);

        this.elements.push(mySnake);
    });
}
// 蛇的移动 
Snake.prototype.move = function(food,map){
    // 改变小蛇的身体，让蛇跑起来
    //思路：后一个元素 到 前一个元素
    //蛇头 单独根据方向处理

    this.removeDiv();//去除之前的元素标签，防止重复创建

    for(let i=this.body.length-1;i>0;i--){
        this.body[i].x = this.body[i-1].x;
        this.body[i].y = this.body[i-1].y;
    }
    this.body[0].img =  Resource.snake[`${this.direction}`];
    switch(this.direction){
        case 'right':{
            this.body[0].x ++;
            break;
        }
        case 'left':{
            this.body[0].x --;
            break;
        }
        case 'up':{
            this.body[0].y--;
            break;
        }
        case 'down':{
            this.body[0].y++;
            break;
        }
    }
    if(this.judgeFood(food,map)){//判断是否吃到食物
        this.addBody();
        food.initFood(map);
    }
    if(this.judgeSelf()){//判断是否吃到自己
        return false;
    }
    if(this.judgeWall(map)){//判断是否撞到墙上
        return false;
    }
    return true;
    // console.log("moving",food,map);
}

// 判断是否吃到食物
Snake.prototype.judgeFood = function(food){
    if(this.body[0].x * this.width == food.x && this.body[0].y * this.height == food.y){
        return true;
    }
    return false;
}
// 判断是否吃到自己
Snake.prototype.judgeSelf = function(){
    let head = this.body[0];
    for(let i=4;i<this.body.length;i++){
        let part = this.body[i];
        if(head.x == part.x && head.y == part.y){
            return true;
        }
    }
    return false;
}
// 身体多一截
Snake.prototype.addBody = function(){
    let last = this.body[this.body.length-1];
    this.body.push({
        x:last.x,
        y:last.y,
        img:last.img
    });
    document.getElementById("length").innerHTML = this.body.length;
}

// 判断是否碰到墙壁
// 基于map的宽高来判断是否冲出右下方墙壁
Snake.prototype.judgeWall = function(map){
    if(this.body[0].x * this.width   >= map.offsetWidth ){//右侧墙壁
        console.log(this.body[0].x , map.offsetWidth,"right boom~");
        return true;
    }
    else if( this.body[0].y * this.height >= map.offsetHeight ){//下方墙壁
        console.log(this.body[0].y  , map.offsetWidth," downboom~");
        return true;
    }
    else if(this.body[0].x <0 ){//左侧墙壁
        console.log(this.body[0].x ,"left boom~");
        return true;
    }
    else if(this.body[0].y <0 ){//上方墙壁
        console.log(this.body[0].y ,"up boom~");
        return true;
    }
    return false;
}

// 删除蛇元素
Snake.prototype.removeDiv = function(){
    for(let i =0;i<this.elements.length;i++){
        this.elements[i].parentNode.removeChild(this.elements[i]);
    }
    this.elements.length = 0;
}
export default Snake;