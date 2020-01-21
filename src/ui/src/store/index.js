import Vue from 'vue';
import Vuex from 'vuex';
import publications from "./publications";
import axios from 'axios';
import VueAxios from 'vue-axios';

Vue.use(Vuex);
Vue.use(VueAxios, axios);

export default new Vuex.Store({
    modules: {
        publications
    }
})
