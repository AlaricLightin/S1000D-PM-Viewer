<template>
  <v-app>
    <v-content>
      <p>{{publications}}</p>
    </v-content>
  </v-app>
</template>

<script>
  import {mapState} from 'vuex';

  export default {
    name: 'App',

    components: {},

    data: () => ({
      //
    }),

    computed: mapState({
      publications: state => state.publications.all
    }),

    mounted() {
      this.$store.dispatch('publications/load');
    }
  };
</script>
