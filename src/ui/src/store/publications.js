import axios from 'axios'

const state = {
    all: []
};

const getters = {
};

const mutations = {
    SET_PUBLICATIONS (state, books) {
        state.all = books
    }
};

const actions = {
    load({commit}) {
        axios
            .get('/publication')
            .then(r => r.data)
            .then(books => {
                commit('SET_PUBLICATIONS', books)
            })
    }
};

export default {
    namespaced: true,
    state,
    getters,
    actions,
    mutations
}