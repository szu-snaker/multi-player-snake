<template>
    <div class="enter-box">
        <input class="user-input" type="text" v-model.trim="username" placeholder="昵称" />
        <button class="enter-button" @click="createRoom">加入匹配</button>
    </div>
</template>
<script>

export default {
    data() {
        return {
            username: '',
        }
    },
    methods: {
        createRoom() {
            if (this.username != "") {
                this.$router.push("/game/" + this.username);
            }
        }
    },
}
</script>
<style scoped>
.enter-box {
    position: fixed;
    top: 50%;
    left: 30vw;
    width: 30vw;
    height: 14vh;
    border-radius: 10px;
}
.user-input {
    display: block;
    width: 80%;
    height: 5vh;
    margin: 10px auto;
}
.enter-button {
    display: block;
    width: 100px;
    height: 5vh;
    min-height: 30px;
    margin: 0 auto;
}
</style>