<template>
    <v-list>
        <v-list-item v-for="publication in this.publications" :key="publication.id">
            <v-list-item-title>{{publication.code}}</v-list-item-title>
            <v-btn>Просмотр</v-btn>
            <v-btn>Права доступа</v-btn>
            <PublicationDelete :publication="publication"/>
        </v-list-item>
    </v-list>
</template>

<script>
    import {mapState} from "vuex";
    import PublicationDelete from "./PublicationDelete";

    export default {
        name: "Publications",
        components: {PublicationDelete},
        computed: mapState({
            publications: state => state.publications.all
        }),

        mounted() {
            this.$store.dispatch('publications/load');
        }
    }
</script>

<style scoped>

</style>