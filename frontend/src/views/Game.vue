<template>
  <div id="map" ref="map">
    <h2 class="time">{{translateTime()}}</h2>
    <h2 class="length" id="length">3</h2>
    <!-- <button class="button restart-button" @click="restart">重新开始</button> -->
    <button class="button add-button" @click="addMore">加多一条</button>
    <!-- <button class="button increase-button" @click="increaseSpeed">最新加速</button> -->
    <!-- <button class="button decrease-button" @click="decreaseSpeed">最新减速</button> -->
  </div>
</template>

<script>
import Game from '@/static/js/game.js';
import Snake from '../static/js/snake';
export default {
  name: 'HelloWorld',
  data(){
    return {
      websocker:null,
      time:0,
      game:null,
    }
  },
  mounted(){
      let timer = setInterval(()=>{
        this.time++;
      },1000);
      let map = document.getElementById("map");
      this.game = new Game(map,timer);
      this.game.init();
      this.game.bindKey();
  },
  methods:{

    translateTime(){
      let minutes = parseInt(this.time/60);
      let seconds = parseInt(this.time%60);
      return ("0"+minutes).slice(-2) + ":" +  ("0"+seconds).slice(-2) ;
    },
    // increaseSpeed(){
    //   this.game.changeSpeed(-1);
    // },
    // decreaseSpeed(){
    //   this.game.changeSpeed(1);
    // },
    // restart(){
    //   this.game.clearGame();
    //   this.addMore();
    // },
    addMore(){
      this.game.addSnake(new Snake());
      // this.game = null;
      // this.time = 0;
      // let timer = setInterval(()=>{
      //   this.time++;
      // },1000);
      // this.game = new Game(this.$refs.map,timer);
      // this.game.init();
      // this.game.bindKey();
    }
  }
}
</script>

<style scoped>
  .length{
    position: absolute;
    left: 310px;
    top: -80px;
    color: skyblue;
  }
  .time{
    position: absolute;
    left: 50px;
    top: -80px;
    color: skyblue;
  }
  .button{
    position: absolute;
    top:-85px;
    width: 100px;
    height: 40px;
    line-height:40px;
    font-size: 20px;
    text-align: center;
    padding: 0 auto ;
    border-radius: 5px;
    background-color: transparent;
    opacity: 0.9;
    color: rgb(255, 255, 255);
  }
  .restart-button{
    right: 0px;
  }
  .add-button{
    right: 100px;
  }
  .increase-button{
    right:200px;
  }
  .decrease-button{
    right:300px;
  }
  
</style>
