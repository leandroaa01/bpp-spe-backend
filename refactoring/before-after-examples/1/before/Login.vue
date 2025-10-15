<template>
<div>
 <Header/>
 <div class="center-container">
   <div class="rectangle">
     <h1 class="secondary">Login</h1>
     <input class="text-box" v-model="username" placeholder="Usuário" />
     <br/><br/>
     <input class="text-box" v-model="password" type="password" placeholder="Senha" />
     <br/><br/>
     <button class="button-class" @click="login">Entrar</button>
     <p v-if="error" style="color:pink; font-style: italic, bold;">{{ error }}</p>
   </div>
 </div>
 <div class="image-container">
   <img class="image" src = "@/assets/img/strawberryicon.png"/>
 </div>
</div>
</template>


<script>
import axios from 'axios'
import Header from './items/Header.vue';

export default {
  components: {
   Header
 },

 data() {
   return {
     username: '',
     password: '',
     error: ''
   }
 },
 methods: {
   async login() {
     try {
       const res = await axios.post('http://localhost:8080/api/login', {
         username: this.username,
         password: this.password
       })
       localStorage.setItem('token', res.data.token)
       this.$router.push('/dashboard')
     } catch (err) {
       this.error = err.response?.data?.error || 'Erro de conexão'
     }
   }
 }
}
</script>

<style>
html, body, #app {
 height: 100%;
 margin: 0;
 background-image: url('@/assets/img/pink-strawberries.png');
 background-size: cover;
 background-position: center;
 min-height: 100vh;
}

.secondary {
 color: pink;
}

.page {
 display: flex;
 flex-direction: column;
 height: 100%
}
.center-container {
 flex: 1;
 display: flex;
 justify-content: center;
 align-items: center;
 text-align: center;
 height: 60vh;
 width: 100%;
 font-family: Arial, Helvetica, sans-serif;
 color: pink;
}

.top-container {
 display: flex;
 justify-content: center;
 padding-top: 20px;
 align-items: flex-start;
 text-align: center;
 height: 15vh;
 font-family: Arial, Helvetica, sans-serif;
 color: pink;
}

.rectangle {
 width: 300px;
 height: 280px;
 background-color: rgb(41, 55, 102);
 border-radius: 25px;
}


.dashboard-rectangle {
  width: 800px;
 height: auto;
 background-color: rgb(41, 55, 102);
 border-radius: 25px;
 padding: 30px;
 max-height: 800px;
}

.menu {
 width: 800px;
 height: 50px;
 background-color: rgb(41, 55, 102);
 border-radius: 25px;
 font-size: 30px;
}

.image {
 height:100px;
 align-content: center;
}

.image-container {
 display: flex;
 justify-content: center;
 align-items: center;
 margin-top: 30px;
}

.text-box {
 background-color: pink;
 padding: 2%;
 border-radius: 20px;
 color: rgb(41, 55, 102);
}

.button-class {
 background-color: pink;
 padding: 10px;
 border-radius: 20px;
 font-weight: bolder;
 margin-left: 40px;
 margin-right: 40px;
}
</style>