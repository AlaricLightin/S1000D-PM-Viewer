<template>
    <div>
        <template v-if="user">
            <span>Пользователь: {{user.username}}</span>
            <v-btn @click="logoutClick()">Выйти</v-btn>
        </template>

        <custom-dialog v-else
                       ref="main-dialog"
                       max-width="300px"
                       main-button-caption="Войти"
                       form-caption="Вход в систему"
                       v-on:ok-button-click="loginClick"
                       v-on:set-start-state="setStartState"
        >
            <template v-slot:alertText>Не удалось войти в систему.</template>

            <template v-slot:mainComponents>
                <v-form ref="form" v-model="valid">
                    <v-row>
                        <v-text-field
                                v-model="username"
                                label="Имя пользователя"
                                :rules="nameRules"
                                required/>
                    </v-row>
                    <v-row>
                        <v-text-field
                                v-model="password"
                                label="Пароль"
                                type="password"
                                :rules="passwordRules"
                                required/>
                    </v-row>
                </v-form>
            </template>
        </custom-dialog>
    </div>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {mapActions, mapState} from "vuex";
    import {validationNameRules, validationPasswordRules} from "../../utils/ValidatorUtils";

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
                valid: false,

                username: '',
                password: '',

                nameRules: validationNameRules(),
                passwordRules: validationPasswordRules()
            }
        },

        methods: {
            ...mapActions('authentication', [
                'login',
                'logout'
            ]),

            loginClick: function () {
                if (!this.$refs["form"].validate())
                    return;

                let mainDialog = this.$refs['main-dialog'];
                // noinspection JSValidateTypes
                this.login({username: this.username, password: this.password})
                    .catch(() => mainDialog.showAlert());
            },

            logoutClick() {
                // noinspection JSValidateTypes
                this.logout();
                this.$router.push('/');
            },

            setStartState() {
                if(this.$refs["form"])
                    this.$refs["form"].reset();
            }
        }
    }
</script>

<style scoped>
    span {
        margin: 5px;
    }
</style>