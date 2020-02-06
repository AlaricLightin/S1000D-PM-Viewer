import axios from 'axios'

const state = {
    user: null,
    userToken: null
};

const getConfigWithAuthenticationHeaders = function (config, userToken) {
    if (!config)
        config = {};
    let headers = config.headers ? config.headers : {};
    if (userToken !== null)
        headers['Authorization'] = 'Basic ' + userToken;
    config.headers = headers;
    return config;
};

const hasUserRole = function(user, role) {
    return user && user["authorities"] && user["authorities"].includes(role);
};

// noinspection JSUnusedGlobalSymbols
const getters = {
    getPostRequest: (state) => (requestString, data, config) => {
        if(!data)
            data = {};

        return axios
            .post(
                requestString,
                data,
                getConfigWithAuthenticationHeaders(config, state.userToken));
    },

    getGetRequest: (state) => (requestString, config) => {
        return axios
            .get(
                requestString,
                getConfigWithAuthenticationHeaders(config, state.userToken));
    },

    getDeleteRequest: (state) => (requestString, config) => {
        return axios
            .delete(
                requestString,
                getConfigWithAuthenticationHeaders(config, state.userToken));
    },

    isAdmin: (state) => {
        return hasUserRole(state.user, 'ADMIN');
    },

    isEditor: (state) => {
        return hasUserRole(state.user, 'EDITOR');
    }
};

const mutations = {
    SET_USER(state, user) {
        state.user = user;
    },

    SET_TOKEN(state, token) {
        state.userToken = token;
    },

    LOGOUT(state) {
        state.user = null;
        state.userToken = null;
    }
};

const actions = {
    login({commit, getters}, payload) {
        commit('SET_USER', null);
        commit('SET_TOKEN', btoa(payload.username + ':' + payload.password)); //TODO учесть ограничения на логин и пароль
        return getters.getPostRequest('/login')
            .then(r => r.data)
            .then(user => commit('SET_USER', user));
    },

    logout({commit}) {
        commit('LOGOUT');
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}