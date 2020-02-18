<template>
    <div>
        <loading-error-alert v-if="loadingError"/>

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
                    <v-btn :to="{name: 'Content', params: {id: publication.id}}">Просмотр</v-btn>
                </v-list-item-action>
                <v-list-item-action>
                    <v-btn>Права доступа</v-btn>
                </v-list-item-action>
                <v-list-item-action>
                    <publication-delete :publication="publication"/>
                </v-list-item-action>
            </v-list-item>
        </v-list>
        <template v-else>
            <p>Нет загруженных публикаций.</p>
        </template>

        <publication-add/>
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
                publications: state => state.publications.all,
                needToReload: state => state.publications.needToReload
            })
        },

        data: () => ({
            loadingError: false
        }),

        mounted() {
            this.loadAll();
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
            },

            loadAll() {
                this.$store.dispatch('publications/load')
                    .catch(() => this.loadingError = true);
            }
        },

        watch: {
            needToReload: function (val) {
                if(val)
                    this.loadAll();
            }
        }
    }
</script>

<style scoped>

</style>