<template>
    <div>
        <template v-if="user">
            <span>Пользователь: {{user.username}}</span>
            <v-btn @click="logoutClick()">Выйти</v-btn>
        </template>

        <custom-dialog ref="main-dialog"
                       v-else
                       max-width="300px"
                       main-button-caption="Войти"
                       form-caption="Вход в систему"
                       v-on:ok-button-click="loginClick"
                       v-on:set-start-state="setStartState"
        >
            <template v-slot:alertText>Не удалось войти в систему.</template>

            <template v-slot:mainComponents>
                <v-row>
                    <v-text-field v-model="username" label="Имя пользователя" required/>
                </v-row>
                <v-row>
                    <v-text-field v-model="password" label="Пароль" type="password" required/>
                </v-row>
            </template>
        </custom-dialog>
    </div>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {mapActions, mapState} from "vuex";

    export default {
        name: "login",
        components: {CustomDialog},

        computed: {
            ...mapState({
                user: state => state.authentication.user
            })
        },

        data() {
            return {
                username: '',
                password: ''
            }
        },

        methods: {
            ...mapActions('authentication', [
                'login',
                'logout'
            ]),

            loginClick: function () {
                let mainDialog = this.$refs['main-dialog'];
                // noinspection JSValidateTypes
                this.login({username: this.username, password: this.password})
                    .catch(() => mainDialog.showAlert());
            },

            logoutClick() {
                // noinspection JSValidateTypes
                this.logout();
            },

            setStartState() {
                this.password = '';
            }
        }
    }
</script>

<style scoped>

</style>