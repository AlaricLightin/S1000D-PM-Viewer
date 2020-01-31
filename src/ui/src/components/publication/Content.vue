<template>
    <div>
        <v-treeview ref="treeView"
                    open-all
                    :items="items"
        ></v-treeview>
    </div>
</template>

<script>
    import axios from "axios";

    export default {
        name: "Content",

        data: () => ({
            items: [],
        }),

        // watch: {
        //     '$route': 'loadContent',
        // },

        mounted() {
            this.loadContent();
        },

        methods: {
            loadContent() {
                axios
                    .get(`/publication/${this.$route.params.id}/content`)
                    .then(r => {
                        this.items = r.data;
                        this.$refs["treeView"].updateAll(true);
                    }); //TODO обработка ошибок
            }
        }
    }
</script>

<style scoped>

</style>