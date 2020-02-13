const state = {
    all: []
};

const getters = {
    getIdx: (state) => (username) => {
        return state.all.findIndex(user => user.username === username);
    },
};

const mutations = {
    SET_USERS (state, users) {
        state.all = users;
    },

    ADD_USER (state, user) {
        state.all.push(user);
    },

    DELETE_USER(state, idx) {
        state.all.splice(idx, 1);
    },
};

const actions = {
    load({commit, rootGetters}) {
        return rootGetters['authentication/getGetRequest']('/user')
            .then(r => r.data)
            .then(users => {
                commit('SET_USERS', users)
            })
    },

    add({commit, rootGetters}, newUser) {
        return rootGetters['authentication/getPostRequest'](
            '/user',
            newUser)
            .then(() => commit('ADD_USER', newUser));
    },

    delete({commit, getters, rootGetters}, username) {
        return rootGetters['authentication/getDeleteRequest'](`/user/${username}`)
            .then(() => {
                const idx = getters.getIdx(username);
                commit('DELETE_USER', idx);
            });
    },
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}