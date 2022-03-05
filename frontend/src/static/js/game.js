import Food from "@/static/js/food.js";
import Snake from "@/static/js/snake.js";
//游戏构造函数
function Game(map, timer = null) {
  this.food = new Food();
  this.snake = new Snake();
  this.map = map;
  this.timer = timer;
  this.speed = 200;
  this.keyBoardListener = (e) => {
    switch (e.keyCode) {
      case 39:
      case 68: {
        if (this.snake.direction != "left") {
          this.snake.direction = "right";
          console.log("11");
        }
        break;
      }
      case 37:
      case 65: {
        if (this.snake.direction != "right") {
          this.snake.direction = "left";
        }
        break;
      }
      case 38:
      case 87: {
        if (this.snake.direction != "down") {
          this.snake.direction = "up";
        }
        break;
      }
      case 40:
      case 83: {
        if (this.snake.direction != "up") {
          this.snake.direction = "down";
        }
      }
    }
  };
  //  that = this; //此时 that 就是this;
  // this.map.addEventListener("keydown", function (e) {
  //   console.log(e);
  // });
}

// 初始化游戏
Game.prototype.init = function () {
  this.food.initFood(this.map);
  this.snake.initSnake(this.map);
  this.runSnake(this.food, this.map);
};
// 开始运行
Game.prototype.runSnake = function (food, map) {
  this.timeId = setInterval(() => {
    if (this.snake.move(food, map)) {
      this.snake.initSnake(map);
    } else {
      clearInterval(this.timeId);
      clearInterval(this.timer);
      this.clearGame();
    }
  }, this.speed);
};
// 绑定键盘事件
Game.prototype.bindKey = function () {
  document.addEventListener("keydown", this.keyBoardListener, false);
};
// 改变游戏速度
Game.prototype.changeSpeed = function (flag) {
  this.speed += flag * 20;
  if (this.speed < 100 || this.speed > 400) {
    this.speed -= flag * 20;
  } else {
    if(this.timeId){
      clearInterval(this.timeId);
    }
    this.timeId = setInterval(() => {
      if (this.snake.move(this.food, this.map)) {
        this.snake.initSnake(this.map);
      } else {
        clearInterval(this.timeId);
        clearInterval(this.timer);
        this.clearGame();
      }
    }, this.speed);
  }
};
// 清除游戏：蛇+水果+键盘绑定监听器
Game.prototype.clearGame = function () {
  this.snake.removeDiv();
  this.food.removeDiv();
  document.removeEventListener("keydown", this.keyBoardListener, false);
};

export default Game;
