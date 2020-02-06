<template>
  <v-app>
    <v-app-bar app>
      <login/>
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
    components: {login: Login},
    data: () => ({
      //
    }),
  };
</script>
