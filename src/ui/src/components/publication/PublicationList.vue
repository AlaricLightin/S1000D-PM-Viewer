<template>
    <div>
        <loading-error-alert v-if="loadingError"/>

        <v-list v-if="publicationTree.length > 0">
            <v-list-item v-for="publication in publicationTree" :key="publication.code">
                <v-list-group prepend-icon="$expand" append-icon="">
                    <template v-slot:activator>
                        <v-list-item-title>{{`[${publication.code}] ${publication.title}`}}</v-list-item-title>
                    </template>
                    
                    <v-list-item 
                            three-line
                            v-for="version in publication.versions" 
                            :key="version.id">
                        <v-list-item-content>
                            <v-list-item-title>{{`${version.title}`}}</v-list-item-title>
                            <v-list-item-subtitle>{{`Версия: ${version["issue"]}, язык: ${version.language},
                                дата создания: ${getDateString(version["issueDate"])},
                                дата и время загрузки: ${getDateTimeString(version["loadDateTime"])}`}}
                            </v-list-item-subtitle>
                        </v-list-item-content>

                        <v-list-item-action>
                            <v-btn :to="{name: 'Content', params: {id: version.id}}">Просмотр</v-btn>
                        </v-list-item-action>
                        <v-list-item-action>
                            <publication-authorizations :publication="version"/>
                        </v-list-item-action>
                        <v-list-item-action>
                            <publication-delete :publication="version"/>
                        </v-list-item-action>
                    </v-list-item>
                </v-list-group>
            </v-list-item>
        </v-list>
        <template v-else>
            <p>Нет загруженных публикаций.</p>
        </template>

        <publication-add/>
    </div>
</template>

<script>
    import {mapState} from "vuex";
    import PublicationDelete from "./PublicationDelete";
    import PublicationAdd from "./PublicationAdd";
    import LoadingErrorAlert from "../errors/LoadingErrorAlert";
    import PublicationAuthorizations from "./PublicationAuthorizations";

    export default {
        name: "PublicationList",
        components: {PublicationAuthorizations, LoadingErrorAlert, PublicationDelete, PublicationAdd},
        computed: {
            ...mapState({
                publications: state => state.publications.all,
                needToReload: state => state.publications.needToReload
            }),

            publicationTree: function () {
                let result = [];
                for(let i = 0; i < this.publications.length; i++) {
                    const version = this.publications[i];
                    const code = version.code;
                    let publication = result[code];
                    if (!publication) {
                        publication = {code: code, title: version.title, versions: []};
                        result.push(publication);
                        result[code] = publication;
                    }
                    publication.versions.push(version);
                }
                return result;
            }
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