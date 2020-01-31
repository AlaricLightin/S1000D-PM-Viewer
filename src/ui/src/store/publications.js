import axios from 'axios'

const state = {
    all: []
};

const getters = {
    getIdx: (state) => (id) => {
        return state.all.findIndex(p => p.id === id);
    }
};

const mutations = {
    SET_PUBLICATIONS (state, publications) {
        state.all = publications;
    },

    ADD_PUBLICATION (state, publication) {
        state.all.push(publication);
    },

    DELETE_PUBLICATION(state, idx) {
        state.all.splice(idx, 1);
    },
};

const actions = {
    load({commit}) {
        return axios
            .get('/publication')
            .then(r => r.data)
            .then(publications => {
                commit('SET_PUBLICATIONS', publications)
            })
    },

    add({commit}, file){
        let formData = new FormData();
        formData.append('file', file, file.name);
        return axios
            .post('publication',
                formData,
                {
                    headers: {
                        'Content-Type': 'multipart/form-data'
                    }
                }
            )
            .then(r => r.data)
            .then(publication => commit('ADD_PUBLICATION', publication));
    },

    delete({commit, getters}, id) {
        return axios.delete('/publication/' + id)
            .then(() => {
                const idx = getters.getIdx(id);
                commit('DELETE_PUBLICATION', idx);
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