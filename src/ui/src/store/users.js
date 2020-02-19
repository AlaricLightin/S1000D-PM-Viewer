const state = {
    all: []
};

const getters = {
    getIdx: (state) => (username) => {
        return state.all.findIndex(user => user.username === username);
    },

    isUsernameExists: (state) => (username) => {
        return state.all.some(user => user.username === username);
    }
};

const userSortFunction = function (user1, user2) {
    const u1 = user1.username;
    const u2 = user2.username;
    if(u1 < u2) return -1;
    if(u1 > u2) return 1;
    return 0;
};

const mutations = {
    SET_USERS (state, users) {
        state.all = users.sort(userSortFunction);
    },

    ADD_USER (state, user) {
        state.all.push(user);
        state.all = state.all.sort(userSortFunction);
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