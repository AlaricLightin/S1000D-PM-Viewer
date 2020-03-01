const state = {
    all: [],
    needToReload: false
};

const getters = {
    getIdx: (state) => (id) => {
        return state.all.findIndex(p => p.id === id);
    }
};

const mutations = {
    SET_PUBLICATIONS (state, publications) {
        state.all = publications;
        state.needToReload = false;
    },

    ADD_PUBLICATION (state, publication) {
        state.all.push(publication);
    },

    DELETE_PUBLICATION(state, idx) {
        state.all.splice(idx, 1);
    },

    SET_NEED_TO_RELOAD(state) {
        state.needToReload = true;
    }
};

const actions = {
    load({commit, rootGetters}) {
        return rootGetters['authentication/getGetRequest']('/publication')
            .then(r => r.data)
            .then(publications => {
                commit('SET_PUBLICATIONS', publications)
            })
    },

    add({commit, rootGetters}, file){
        let formData = new FormData();
        formData.append('file', file, file.name);
        return rootGetters['authentication/getPostRequest'](
            '/publication',
            formData,
            {
                headers: {
                    'Content-Type': 'multipart/form-data'
                }
            })
            .then(r => r.data)
            .then(publication => commit('ADD_PUBLICATION', publication));
    },

    delete({commit, getters, rootGetters}, id) {
        return rootGetters['authentication/getDeleteRequest'](`/publication/${id}`)
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