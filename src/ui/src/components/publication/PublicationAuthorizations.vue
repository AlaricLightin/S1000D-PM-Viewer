<template>
    <v-dialog v-model="dialog"
              v-if="isAdmin"
              ref="main-dialog"
              max-width="500px"
              persistent
              scrollable
    >
        <template v-slot:activator="{ on }">
            <v-btn v-on="on">Права на просмотр</v-btn>
        </template>

        <v-card>
            <v-card-title class="headline">Права на просмотр</v-card-title>
            <v-card-text>
                <v-container>
                    <v-alert v-if="showErrorAlert" type="error">
                        {{errorText}}
                    </v-alert>

                    <v-checkbox v-model="permitAll"
                                label="Открыть доступ для всех"/>
                    <v-divider/>
                    <v-container fluid>
                        <h4>Открыть доступ пользователям</h4>
                        <v-checkbox v-for="user in filteredUsers"
                                    :key="user.username"
                                    :label="user.username"
                                    :value="user.username"
                                    :disabled="permitAll"
                                    v-model="usernameList"
                                    dense
                        >
                        </v-checkbox>
                    </v-container>
                </v-container>
                <v-card-actions>
                    <v-spacer>
                        <slot name="mainButton">
                            <v-btn
                                    text
                                    @click="submitClick"
                                    :disabled="!userDataLoaded || !authorizationDataLoaded"
                            >
                                Установить
                            </v-btn>
                        </slot>
                        <v-btn text @click="dialog = false">Отмена</v-btn>
                    </v-spacer>
                </v-card-actions>
            </v-card-text>
        </v-card>
    </v-dialog>
</template>

<script>
    import {mapGetters, mapState} from "vuex";

    export default {
        name: "PublicationAuthorizations",

        props: {
            publication: Object
        },

        data: () => ({
            dialog: false,
            errorText: '',
            permitAll: false,
            userDataLoaded: false,
            authorizationDataLoaded: false,
            usernameList: [],
        }),

        computed: {
            ...mapState({
                users: state => state.users.all
            }),

            ...mapGetters('authentication', [
                'isAdmin',
                'getGetRequest',
                'getPutRequest'
            ]),

            filteredUsers: function () {
                return this.users.filter(user => user.authorities && !user.authorities.includes('ADMIN'));
            },

            showErrorAlert: function () {
                return this.errorText !== '';
            }
        },

        methods: {
            submitClick: function () {
                // noinspection JSValidateTypes
                this.getPutRequest(`/publication/${this.publication.id}/authorizations`,
                    {
                        permitAll: this.permitAll,
                        usernameList: this.permitAll ? [] : this.usernameList
                    }
                )
                .then(() => this.dialog = false)
                .catch(() => this.errorText = 'Не удалось сохранить изменения.')
            },
        },

        watch: {
            dialog: function (val, oldVal) {
                if(val && !oldVal) {
                    this.errorText = '';
                    this.userDataLoaded = false;
                    this.authorizationDataLoaded = false;

                    this.$store.dispatch('users/load')
                        .then(() => this.userDataLoaded = true)
                        .catch(() => this.errorText = 'Не удалось получить данные с сервера.');

                    // noinspection JSValidateTypes
                    this.getGetRequest(`/publication/${this.publication.id}/authorizations`)
                        .then(r => {
                            this.permitAll = r.data.permitAll;
                            this.usernameList = r.data.usernameList;
                            this.authorizationDataLoaded = true;
                        })
                        .catch(() => this.errorText = 'Не удалось получить данные с сервера.');
                }
            }
        }
    }
</script>

<style scoped>

</style>