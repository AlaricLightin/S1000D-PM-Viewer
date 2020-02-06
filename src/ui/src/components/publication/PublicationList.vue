<template>
    <div>
        <LoadingErrorAlert v-if="loadingError"/>

        <v-list v-if="this.publications.length > 0">
            <v-list-item three-line
                         v-for="publication in this.publications"
                         :key="publication.id"
            >
                <v-list-item-content>
                    <v-list-item-title>{{`[${publication.code}] ${publication.title}`}}</v-list-item-title>
                    <v-list-item-subtitle>{{`Версия: ${publication["issue"]}, язык: ${publication.language},
                        дата создания: ${getDateString(publication["issueDate"])},
                        дата и время загрузки: ${getDateTimeString(publication["loadDateTime"])}`}}
                    </v-list-item-subtitle>
                </v-list-item-content>

                <v-list-item-action>
                    <v-btn @click="$router.push({name: 'Content', params: {id: publication.id}})">Просмотр</v-btn>
                    <v-btn>Права доступа</v-btn>
                    <PublicationDelete :publication="publication"/>
                </v-list-item-action>
            </v-list-item>
        </v-list>
        <template v-else>
            <p>Нет загруженных публикаций.</p>
        </template>

        <PublicationAdd v-bind:disabled="loadingError"/>
    </div>
</template>

<script>
    // TODO сделать древовидную структуру
    import {mapState} from "vuex";
    import PublicationDelete from "./PublicationDelete";
    import PublicationAdd from "./PublicationAdd";
    import LoadingErrorAlert from "../errors/LoadingErrorAlert";

    export default {
        name: "PublicationList",
        components: {LoadingErrorAlert, PublicationDelete, PublicationAdd},
        computed: {
            ...mapState({
                publications: state => state.publications.all
            })
        },

        data: () => ({
            loadingError: false
        }),

        mounted() {
            this.$store.dispatch('publications/load')
                .catch(() => this.loadingError = true);
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