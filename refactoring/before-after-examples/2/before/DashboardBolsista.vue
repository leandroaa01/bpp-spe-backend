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

   async marcarPonto(trigger) {
     triggers.value[trigger] = !triggers.value[trigger];
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

