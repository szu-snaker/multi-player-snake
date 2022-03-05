<template>
    <div class="">
        <input type="number" v-model="number">
        <button @click="createRoom">创建房间</button>
    </div>
</template>
<script>

export default {
    data(){
        return {
            number:1,
        }
    },
    methods: {
        createRoom(){
            this.$router.push("/game/"+this.number);
        }
    },
}
</script>
<style scoped lang="scss">
    
</style>