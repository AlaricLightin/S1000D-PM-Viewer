<template>
  <v-app>
    <v-navigation-drawer app clipped>
      <v-list>
        <v-list-item link to="/">
          <v-list-item-content>
            <v-list-item-title>
              Публикации
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>

        <v-list-item v-if="isAdmin" link to="/users">
          <v-list-item-content>
            <v-list-item-title>
              Пользователи
            </v-list-item-title>
          </v-list-item-content>
        </v-list-item>
      </v-list>
    </v-navigation-drawer>

    <v-app-bar app clipped-left>
      <v-toolbar-title>S1000D PM Viewer</v-toolbar-title>
      <v-spacer/>
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
  import Login from "./components/login/Login";
  import {mapGetters} from "vuex";

  export default {
    name: 'App',
    components: {login: Login},
    computed: {
      ...mapGetters('authentication', [
        'isAdmin'
      ])
    },

    created() {
      this.$store.dispatch('authentication/getFromStorage');
    }
  };
</script>
