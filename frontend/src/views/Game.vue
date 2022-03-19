<template>
  <div id="map" ref="map">
    <h2 class="time">{{ translateTime() }}</h2>
    <h2 class="length" id="length">3</h2>
    <button class="button add-button" @click="end">结束游戏</button>
    <button class="button restart-button" @click="start">重新连接</button>
    
    <!-- <button class="button add-button" @click="addMore">加多一条</button> -->
    <!-- <button class="button increase-button" @click="increaseSpeed">最新加速</button> -->
    <!-- <button class="button decrease-button" @click="decreaseSpeed">最新减速</button> -->
  </div>
</template>

<script>
import Game from '@/static/js/game.js';
export default {
  name: 'HelloWorld',
  data() {
    return {
      websocker: null,
      time: 0,
      game: null,
      timer: null,
      map: null
    }
  },
  mounted() {
    this.countTime();
    this.map = document.getElementById("map");
    this.start();
  },
  methods: {
    translateTime() {
      let minutes = parseInt(this.time / 60);
      let seconds = parseInt(this.time % 60);
      return ("0" + minutes).slice(-2) + ":" + ("0" + seconds).slice(-2);
    },
    countTime() {
      if (this.timer) {
        clearInterval(this.timer);
      }
      this.time = 0;
      this.timer = setInterval(() => {
        this.time++;
      }, 1000);
    },
    end() {
      this.time = 0;
      this.game.end();
    },
    start() {
      if (this.game) {
        this.game.end();
      }
      this.countTime();
      this.game = new Game(this.map, this.timer);
      this.game.connect(this.$route.params.id)
      this.game.bindKey();
      console.log("id" + this.$route.params.id);
    },

    /*
        increaseSpeed(){
          this.game.changeSpeed(-1);
        },
        decreaseSpeed(){
          this.game.changeSpeed(1);
        },
        restart(){
          this.game.clearGame();
          this.addMore();
        },
        addMore(){
          this.game.addSnake(new Snake());      
          this.game = null;
          this.time = 0;
          let timer = setInterval(()=>{
            this.time++;
          },1000);
          this.game = new Game(this.$refs.map,timer);
          this.game.init();
          this.game.bindKey();
        }
    */
  }
}
</script>

<style >
.length {
  position: absolute;
  left: 310px;
  top: -80px;
  color: skyblue;
}
.time {
  position: absolute;
  left: 50px;
  top: -80px;
  color: skyblue;
}
.button {
  position: absolute;
  top: -85px;
  width: 100px;
  height: 40px;
  line-height: 40px;
  font-size: 20px;
  text-align: center;
  padding: 0 auto;
  border-radius: 5px;
  background-color: transparent;
  opacity: 0.9;
  color: rgb(255, 255, 255);
}
.restart-button {
  right: 0px;
}
.add-button {
  right: 100px;
}
.increase-button {
  right: 200px;
}
.decrease-button {
  right: 300px;
}


</style>
