<template>
    <div class="">
        <input type="number" v-model="number">
        <button @click="createRoom">创建房间</button>
    </div>
</template>
<script>
import Game from "../static/js/game";

export default {
    data(){
        return {
            number:1,
        }
    },
    methods: {
        createRoom(){
            this.$router.push("/game/"+number);
        }
    },
}
</script>
<style scoped lang="scss">
    
</style>