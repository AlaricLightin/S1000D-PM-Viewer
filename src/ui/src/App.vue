<template>
  <v-app>
    <v-content>
      <v-container>
        <Publications/>
        <PublicationAdd/>
      </v-container>
    </v-content>
  </v-app>
</template>

<script>
  // TODO написать просмотр
  import Publications from "./components/Publications";
  import PublicationAdd from "./components/PublicationAdd";

  export default {
    name: 'App',

    components: {PublicationAdd, Publications},

    data: () => ({
      //
    }),
  };
</script>
