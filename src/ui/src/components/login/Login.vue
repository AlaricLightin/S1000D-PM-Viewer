<template>
    <div>
        <template v-if="user">
            <span>Пользователь: {{user.username}}</span>
            <v-btn @click="logoutClick()">Выйти</v-btn>
        </template>

        <template v-else>
            <v-dialog v-model="dialog" max-width="400">
                <template v-slot:activator="{ on }">
                    <v-btn v-on="on">Войти</v-btn>
                </template>
                <v-card>
                    <v-card-title class="headline">Вход в систему</v-card-title>
                    <v-card-text>
                        <v-container>
                            <v-alert v-if="showErrorAlert" type="error">
                                Не удалось войти в систему.
                            </v-alert>

                            <v-row>
                                <v-text-field v-model="username" label="Имя пользователя" required/>
                            </v-row>
                            <v-row>
                                <v-text-field v-model="password" label="Пароль" type="password" required/>
                            </v-row>
                        </v-container>
                    </v-card-text>
                    <v-card-actions>
                        <v-spacer>
                            <v-btn text @click="loginClick()">Войти</v-btn>
                            <v-btn text @click="dialog = false">Отмена</v-btn>
                        </v-spacer>
                    </v-card-actions>
                </v-card>
            </v-dialog>
        </template>
    </div>
</template>

<script>
    import {mapActions, mapState} from "vuex";

    export default {
        name: "Login",

        computed: {
            ...mapState({
                user: state => state.authentication.user
            })
        },

        data() {
            return {
                dialog: false,
                showErrorAlert: false,
                username: '',
                password: ''
            }
        },

        methods: {
            ...mapActions('authentication', [
                'login',
                'logout'
            ]),

            loginClick() {
                this.showErrorAlert = false;
                // noinspection JSValidateTypes
                this.login({username: this.username, password: this.password})
                    .then(() => this.dialog = false)
                    .catch(() => this.showErrorAlert = true);
            },

            logoutClick() {
                // noinspection JSValidateTypes
                this.logout();
            }
        },

        watch: {
            dialog: function(val, oldVal) {
                if (val && !oldVal) {
                    this.showErrorAlert = false;
                    this.password = '';
                }
            }
        }
    }
</script>

<style scoped>

</style>