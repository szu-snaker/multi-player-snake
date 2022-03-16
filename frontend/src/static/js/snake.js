import Resource from "@/static/js/Resource";
// 蛇的初始化
function Snake(id, xx, yy, width, height, direction) {
  this.id = id || Math.random() * 1000; //id

  this.width = width || 30; //渲染宽度
  this.height = height || 30; //渲染高度
  this.elements = []; //渲染的 div元素

  //方向
  this.direction = direction || "right";

  // 记住蛇默认的状态，吃完食物的时候就要加一个
  this.body = [
    // {
    //   x: xx || 3,
    //   y: yy || 0,
    //   img: Resource.snake.right,
    // },
    // {
    //   x: xx - 1 || 2,
    //   y: yy || 0,
    //   img: Resource.snake.body,
    // },
    // {
    //   x: xx - 2 || 1,
    //   y: yy || 0,
    //   img: Resource.snake.body,
    // },
  ];
}

Snake.prototype.setName = function (map) {
  if (!this.userNameDOM) {
    this.userNameDOM = document.createElement("p");
    this.userNameDOM.classList.add("name");
    this.userNameDOM.style.left = (this.body[0].x - 0.5)* this.width + "px";
    this.userNameDOM.style.top = (this.body[0].y - 0.8) * this.height + "px";
    this.userNameDOM.innerText = this.userName;
    map.appendChild(this.userNameDOM);
  }
};

Snake.prototype.initBody = function (data) {
  let directions = ["up", "right", "down", "left"];
  this.direction = directions[data.direction];
  let head = {
    x: data.snakeBody[0][0],
    y: data.snakeBody[0][1],
    img: Resource.snake[this.direction],
  };
  this.body.push(head);
  for (let i = 1; i < data.snakeBody.length; i++) {
    let tempBody = {
      x: data.snakeBody[i][0],
      y: data.snakeBody[i][1],
      img: Resource.snake.body,
    };
    this.body.push(tempBody);
  }
  this.userName = data.userName;
};
// 刷新新的蛇HTML元素,若长度增加，则清除重构，否则直接移动蛇的元素
Snake.prototype.refreshSnake = function (map, isMe) {
  if (this.elements.length == this.body.length) {
    //单纯移动
    this.refreshSnakeBody();
    return;
  } else if (this.elements.length == this.body.length - 1) {
    //吃到一个新食物
    let obj = this.body[this.body.length - 1];
    let snakeBody = document.createElement("div");
    // snakeBody.style.width = this.width + "px";
    // snakeBody.style.height = this.height + "px";
    // snakeBody.style.backgroundSize = `${this.width}px ${this.height}px`;
    // snakeBody.style.position = "absolute";

    snakeBody.style.background = `url(${obj.img}) no-repeat center`;
    snakeBody.style.left = obj.x * this.width + "px";
    snakeBody.style.top = obj.y * this.height + "px";

    snakeBody.classList.add("snake");
    if (isMe) {
      snakeBody.classList.add("mySnake");
    }
    map.appendChild(snakeBody);
    this.elements.push(snakeBody);

    this.refreshSnakeBody();
    return;
  }
  this.removeDiv(); //去除之前的元素标签，防止重复创建
  //循环创建蛇头和蛇身
  this.body.forEach((element) => {
    let obj = element;
    let snakeBody = document.createElement("div");
    // snakeBody.style.width = this.width + "px";
    // snakeBody.style.height = this.height + "px";
    // snakeBody.style.backgroundSize = `${this.width}px ${this.height}px`;
    // snakeBody.style.position = "absolute";

    snakeBody.style.background = `url(${obj.img}) no-repeat center`;
    snakeBody.style.left = obj.x * this.width + "px";
    snakeBody.style.top = obj.y * this.height + "px";

    snakeBody.classList.add("snake");
    if (isMe) {
      snakeBody.classList.add("mySnake");
    }
    map.appendChild(snakeBody);
    this.elements.push(snakeBody);
  });
  this.setName(map);

};

// 刷新蛇HTML元素,前提条件：需要蛇的body长度和elements元素相同
Snake.prototype.refreshSnakeBody = function () {
  //循环移动蛇身
  for (let i = 0; i < this.body.length; i++) {
    let obj = this.body[i];
    let part = this.elements[i];
    part.style.background = `url(${obj.img}) no-repeat center`;
    part.style.left = obj.x * this.width + "px";
    part.style.top = obj.y * this.height + "px";
  }
  // 移动名字
  if (this.userNameDOM) {
    this.userNameDOM.style.left = (this.body[0].x - 0.5)* this.width + "px";
    this.userNameDOM.style.top = (this.body[0].y - 0.8) * this.height + "px";
  }
};

// 根据最新数据更新蛇的数据（暂未考虑方向）
Snake.prototype.updateSnake = function (data, isMe) {
  console.log("update:", data);
  let newImg = null;
  if (data.direction != undefined) {
    console.log("check is Turn ", data.direction);
    let directions = ["up", "right", "down", "left"];
    this.direction = directions[data.direction];
    newImg = Resource.snake[this.direction];
  }
  let newHead = {
    x: data.newHeadX,
    y: data.newHeadY,
    img: newImg || this.body[0].img,
  };
  this.body[0].img = this.body[1].img;
  this.body.unshift(newHead);
  if (!data.eat) {
    this.body.pop();
    if (isMe) {
      document.getElementById("length").innerHTML = this.body.length;
    }
  }
};
/*
// 蛇的移动
Snake.prototype.move = function(food,map,snakes){
    // 改变小蛇的身体，让蛇跑起来
    //思路：后一个元素 到 前一个元素
    //蛇头 单独根据方向处理

    // this.removeDiv();//去除之前的元素标签，防止重复创建

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

    if(this.judgeSelf()){//判断是否吃到自己
        return 0;
    }
    if(this.judgeWall(map)){//判断是否撞到墙上
        return 0;
    }
    if(this.judgeSnakes(snakes)){//判断是否迟到其他的蛇
        return 0;
    }
    if(this.judgeFood(food,map)){//判断是否吃到食物
        this.addBody();
        // food.initFood(map);
        return 2;
    }
    return 1;
}

// 判断是否吃到其他的蛇
Snake.prototype.judgeSnakes = function (snakes) {
  let head = this.body[0];
  // console.log(snakes);
  for (let i = 0; i < snakes.length; i++) {
    for (let j = 0; j < snakes[i].body.length; j++) {
      let part = snakes[i].body[j];
      if (head.x == part.x && head.y == part.y) {
        return true;
      }
    }
  }
  return false;
};

// 判断是否吃到食物
Snake.prototype.judgeFood = function (food) {
  if (
    this.body[0].x * this.width == food.x &&
    this.body[0].y * this.height == food.y
  ) {
    return true;
  }
  return false;
};
// 判断是否吃到自己
Snake.prototype.judgeSelf = function () {
  let head = this.body[0];
  for (let i = 4; i < this.body.length; i++) {
    let part = this.body[i];
    if (head.x == part.x && head.y == part.y) {
      return true;
    }
  }
  return false;
};
// 身体多一截
Snake.prototype.addBody = function () {
  let last = this.body[this.body.length - 1];
  this.body.push({
    x: last.x,
    y: last.y,
    img: last.img,
  });
  document.getElementById("length").innerHTML = this.body.length;
};

// 判断是否碰到墙壁
// 基于map的宽高来判断是否冲出右下方墙壁
Snake.prototype.judgeWall = function (map) {
  if (this.body[0].x * this.width >= map.offsetWidth) {
    //右侧墙壁
    // console.log(this.body[0].x , map.offsetWidth,"right boom~");
    return true;
  } else if (this.body[0].y * this.height >= map.offsetHeight) {
    //下方墙壁
    // console.log(this.body[0].y  , map.offsetWidth," downboom~");
    return true;
  } else if (this.body[0].x < 0) {
    //左侧墙壁
    // console.log(this.body[0].x ,"left boom~");
    return true;
  } else if (this.body[0].y < 0) {
    //上方墙壁
    // console.log(this.body[0].y ,"up boom~");
    return true;
  }
  return false;
};
*/
// 删除蛇元素
Snake.prototype.removeDiv = function () {
  for (let i = 0; i < this.elements.length; i++) {
    this.elements[i].parentNode.removeChild(this.elements[i]);
  }
  this.elements.length = 0;
  if(this.userNameDOM){
    this.userNameDOM.parentNode.removeChild(this.userNameDOM);
    this.userNameDOM = null;
  }
};

export default Snake;
