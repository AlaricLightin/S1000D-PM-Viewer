import Vue from 'vue';
import Vuex from 'vuex';
import axios from 'axios';
import VueAxios from 'vue-axios';
import publications from "./publications";
import authentication from "./authentication";
import users from "./users";

Vue.use(Vuex);
Vue.use(VueAxios, axios);

export default new Vuex.Store({
    modules: {
        publications,
        authentication,
        users
    }
})
