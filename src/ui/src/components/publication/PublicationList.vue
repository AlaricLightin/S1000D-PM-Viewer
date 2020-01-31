<template>
    <v-list>
        <v-list-item three-line
                     v-for="publication in this.publications"
                     :key="publication.id"
                     @click="$router.push({name: 'Content', params: {id: publication.id}})"
        >
            <v-list-item-content>
                <v-list-item-title>{{`[${publication.code}] ${publication.title}`}}</v-list-item-title>
                <v-list-item-subtitle>{{`Версия: ${publication["issue"]}, язык: ${publication.language},
                    дата создания: ${getDateString(publication["issueDate"])},
                    дата и время загрузки: ${getDateTimeString(publication["loadDateTime"])}`}}</v-list-item-subtitle>
            </v-list-item-content>

            <v-list-item-action>
                <v-btn>Права доступа</v-btn>
                <PublicationDelete :publication="publication"/>
            </v-list-item-action>
        </v-list-item>
    </v-list>
</template>

<script>
    import {mapState} from "vuex";
    import PublicationDelete from "./PublicationDelete";

    export default {
        name: "PublicationList",
        components: {PublicationDelete},
        computed: {
            ...mapState({
                publications: state => state.publications.all
            })
        },

        mounted() {
            this.$store.dispatch('publications/load');
        },

        methods: {
            getDateString(s) {
                let d = new Date(s);
                return d.toLocaleDateString("ru-RU", {year: "numeric", month: "long", day: "numeric"});
            },

            getDateTimeString(s) {
                let d = new Date(s);
                return d.toLocaleDateString("ru-RU",
                    {year: "numeric", month: "long", day: "numeric", hour: "numeric", minute: "numeric"});
            }
        }
    }
</script>

<style scoped>

</style>