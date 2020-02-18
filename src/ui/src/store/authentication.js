import axios from 'axios'

const USER_PROPERTIES = 'pmViewerUser';

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

    getPutRequest: (state) => (requestString, data, config) => {
        if(!data)
            data = {};

        return axios
            .put(
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

// noinspection JSUnusedGlobalSymbols
const actions = {
    getFromStorage({commit}) {
        let userObject = JSON.parse(localStorage.getItem(USER_PROPERTIES));
        if (userObject) {
            commit('SET_USER', userObject.user);
            commit('SET_TOKEN', userObject.token);
        }
    },

    login({commit, getters}, payload) {
        localStorage.removeItem(USER_PROPERTIES);
        commit('SET_USER', null);
        let token = btoa(payload.username + ':' + payload.password);
        commit('SET_TOKEN', token);
        return getters.getPostRequest('/login')
            .then(r => r.data)
            .then(user => {
                commit('SET_USER', user);
                commit('publications/SET_NEED_TO_RELOAD', null, {root: true});
                localStorage.setItem(USER_PROPERTIES, JSON.stringify({user: user, token: token}));
            });
    },

    logout({commit}) {
        commit('LOGOUT');
        commit('publications/SET_NEED_TO_RELOAD', null, {root: true});
        localStorage.removeItem(USER_PROPERTIES);
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}