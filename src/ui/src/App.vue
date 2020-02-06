<template>
  <v-app>
    <v-app-bar app>
      <Login/>
    </v-app-bar>
    <v-content>
      <v-container>
        <router-view/>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
  // TODO написать навигацию

  import Login from "./components/login/Login";

  export default {
    name: 'App',
    components: {Login},
    data: () => ({
      //
    }),
  };
</script>
