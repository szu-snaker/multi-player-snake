import Resource from "@/static/js/Resource";

import Food from "@/static/js/food.js";
import Snake from "@/static/js/snake.js";

//游戏构造函数
function Game(map, timer = null) {
  this.foods = []; //全局食物
  this.snake = null;
  this.snakes = []; //蛇群
  this.map = map; //游戏地图
  this.timer = timer; //游戏计时
  this.speed = 200; //游戏速度，0.2s刷新一次
  // this.loopTimer = null;
  this.socket = null;
  this.user = null;
  // this.snakeTimer = null; //自己控制的蛇的定时器
  // this.snakeTimers = []; //别人的蛇
  // 两蛇两水果
  for(let i=0;i<2;i++){
    this.foods.push(new Food());
    this.snakes.push(new Snake());
  }
  this.keyBoardListener = (e) => {
    switch (e.keyCode) {
      case 39:
      case 68: {
        if (this.snake.direction != "left" &&this.snake.direction != "right") {
          // this.snake.direction = "right";
          this.sendDir("right");
        }
        break;
      }
      case 37:
      case 65: {
        if (this.snake.direction != "left" &&this.snake.direction != "right") {
          // this.snake.direction = "left";
          this.sendDir("left");
        }
        break;
      }
      case 38:
      case 87: {
        if (this.snake.direction != "down" && this.snake.direction != "up") {
          // this.snake.direction = "up";
          this.sendDir("up");
        }
        break;
      }
      case 40:
      case 83: {
        if (this.snake.direction != "down" && this.snake.direction != "up") {
          // this.snake.direction = "down";
          this.sendDir("down");
        }
      }
    }
  };
}

Game.prototype.sendDir = function(direction){
  this.socket.send(JSON.stringify({
    order:direction
  }))
}



// 初始化游戏
Game.prototype.init = function () {
  // this.snake.refreshSnake(this.map);
  // this.loop(this.food, this.map);
  this.bindKey();
};

// 连接服务器端
Game.prototype.connect = function (user) {
  if (this.socket) {
    this.socket.close();
  }
  this.user = user;
  this.socket = new WebSocket(Resource.SocketBaseUrl + "/" + user);
  this.socket.onopen = () => {
    console.log(user + " is connect");
    this.socket.send(JSON.stringify({ order: "find" }));
  };

  this.socket.onclose = function (e) {
    console.log("socket close", e);
  };
  this.socket.onerror = function (err) {
    console.log("socket error: ", err);
  };
  // 解析并处理对应的数据
  this.socket.onmessage = (data) => {
    data = JSON.parse(data.data);
    console.log(data);
    // 初始化：包含食物，初始化新食物。如果之前存在了，就删掉。
    if (Object.hasOwnProperty.call(data, "food")) {
      for(let i=0;i<data.food.length;i++){
        this.foods[i].initFood(this.map,data.food[i].x,data.food[i].y);
      }
    }
    // 初始化两条蛇
    if (Object.hasOwnProperty.call(data, "buildSnakes")) {
      console.log("initSnake command");
      for(let i=0;i<data.buildSnakes.length;i++){
        this.snakes[i].initBody(data.buildSnakes[i]);
        let isMe = data.buildSnakes[i].userName == this.user;
        this.snakes[i].refreshSnake(this.map,isMe); 
        if(isMe){
          this.snake = this.snakes[i];
        }
      }
      // this.snake.initBody(data.mySnakeInit);
      // this.snake.refreshSnake(this.map,true);

      // let rivalSnake = new Snake();
      // rivalSnake.initBody(data.rivalSnakeInit);
      // this.snakes.push(rivalSnake);
      // this.snakes[0].refreshSnake(this.map);
    }
    // 正常移动
    else if (Object.hasOwnProperty.call(data, "snakes")) {
      // console.log("normal command");
      // this.snake.updateSnake(data.mySnake,true);
      // this.snake.refreshSnake(this.map,true);
      // this.snakes[0].updateSnake(data.rivalSnake);
      // this.snakes[0].refreshSnake(this.map);
      for(let i=0;i<data.snakes.length;i++){
        let isMe = data.snakes[i].userName == this.user;
        this.snakes[i].updateSnake(data.snakes[i],isMe);
        this.snakes[i].refreshSnake(this.map,isMe);
        if(isMe){
          this.snake = this.snakes[i];
        }
      }
    }
    else if(Object.hasOwnProperty.call(data,"gameOver")){
      alert("game is over , you "+data.gameOver);
      this.end();
    }
  };
};

Game.prototype.end = function(){
  this.socket.close();
  this.clearGame();
}


// 新增一条蛇
Game.prototype.addSnake = function (snake) {
  this.snakes.push(snake);
};

// 更新小蛇
// Game.prototype.updateSnake = function (snake, snakes, i = null) {
//   if (snake.move(this.food, this.map, snakes) > 0) {
//     snake.refreshSnake(this.map);
//     return true;
//   } else {
//     snake.removeDiv();
//     snake = null;
//     if (i != null) {
//       this.snakes[i] = this.snakes[this.snakes.length - 1];
//       this.snakes.length--;
//     }
//     return false;
//   }
// };
// 游戏循环
// Game.prototype.loop = function () {
//   this.loopTimer = setInterval(() => {
//     if (this.snakes.length == 0 && this.snake == null) {
//       clearInterval(this.loopTimer);
//       this.loopTimer = null;
//       return false;
//     } else {
//       // 自己的蛇
//       if (this.snake) {
//         this.updateSnake(this.snake, this.snakes);
//       }
//       // 别人的蛇
//       for (let i = 0; i < this.snakes.length; i++) {
//         // console.log("update another snake");
//         let temp = this.snake
//           ? [this.snake]
//           : []
//               .concat(this.snakes.slice(0, i))
//               .concat(this.snakes.slice(i + 1, this.snakes.length));
//         if (this.updateSnake(this.snakes[i], temp, i) == false) {
//           i--;
//         }
//       }
//     }
//   }, 200);
// };

// 绑定键盘事件
Game.prototype.bindKey = function () {
  document.addEventListener("keydown", this.keyBoardListener, false);
};
// // 暂时无用：改变游戏速度
// Game.prototype.changeSpeed = function (flag) {
//   this.speed += flag * 20;
//   if (this.speed < 100 || this.speed > 400) {
//     this.speed -= flag * 20;
//   } else {
//     // 需重设定时器
//   }
// };
// 清除游戏：蛇+水果+键盘绑定监听器+
Game.prototype.clearGame = function () {
  clearInterval(this.timer);
  this.snakes.forEach((ele) => {
    ele.removeDiv();
  });
  for(let i=0;i<this.foods.length;i++){
    this.foods[i].removeDiv();
  }
  document.removeEventListener("keydown", this.keyBoardListener, false);
};

export default Game;
