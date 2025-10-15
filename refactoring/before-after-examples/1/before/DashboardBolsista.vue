<template>
 <Header/>

 <div class="center-container">
   <div class="dashboard-rectangle">
     <h1>Dashboard</h1>
    
     <!--<button @click="marcarPonto">Marcar Ponto</button>-->
     <ul>
       <li v-for="r in registros" :key="r">{{ r }}</li>
       <link rel="stylesheet" href="style.css">
     </ul>

     <button class="button-class" @click="() => changePopup('buttonTrigger')" >
       Marcar Ponto
     </button>
     <Popup v-if="triggers.buttonTrigger"
             :changePopup="() => changePopup('buttonTrigger')">
       <h2>Ponto registrado com sucesso!</h2>
     </Popup>

   </div>
 </div>
</template>

<script>
import axios from 'axios';
import { ref } from 'vue';
import Popup from './items/Popup.vue';
import Header from './items/Header.vue';

export default {
 components: {
   Header, Popup
 },

 setup() {

   const triggers = ref({
     buttonTrigger: false
   })

   const changePopup = (trigger) => {
     triggers.value[trigger] = !triggers.value[trigger];
   }
   return {
     Popup,
     triggers,
     changePopup
   }
 },

 data() {
   return { registros: [] }
 },
 async created() {
   await this.carregarRegistros()
 },
 methods: {
   async carregarRegistros() {
     const token = localStorage.getItem('token')
     const res = await axios.get('http://localhost:8080/api/ponto', {
       headers: { Authorization: `Bearer ${token}` }
     })
     this.registros = res.data.registros
   },
   async marcarPonto() {
     const token = localStorage.getItem('token')
     const res = await axios.post('http://localhost:8080/api/ponto/marcar', {}, {
       headers: { Authorization: `Bearer ${token}` }
     })
     alert(res.data.message)
     await this.carregarRegistros()
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