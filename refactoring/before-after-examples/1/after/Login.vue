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
