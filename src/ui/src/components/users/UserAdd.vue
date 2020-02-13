<template>
    <custom-dialog ref="main-dialog"
                   max-width="400px"
                   main-button-caption="Добавить"
                   form-caption="Добавление пользователя"
                   v-on:set-start-state="setStartState"
    >
        <template v-slot:alertText>Не удалось добавить пользователя.</template>

        <template v-slot:mainComponents>
            <v-form ref="form" v-model="valid">
                <v-row>
                    <v-text-field v-model="username"
                                  label="Имя пользователя"
                                  :rules="nameRules"
                                  required/>
                </v-row>
                <v-row>
                    <v-text-field v-model="password"
                                  label="Пароль"
                                  type="password"
                                  :rules="passwordRules"
                                  required/>
                </v-row>
                <v-row>
                    <v-text-field v-model="password2"
                                  label="Повторите пароль"
                                  type="password"
                                  :rules="password2rules"
                                  required/>
                </v-row>
                <v-row>
                    <v-container>
                        <v-checkbox v-model="authorities" label="Администратор" value="ADMIN"/>
                        <v-checkbox v-model="authorities" label="Редактор" value="EDITOR"/>
                    </v-container>
                </v-row>
            </v-form>
        </template>

        <template v-slot:mainButton>
            <v-btn v-bind:loading="loading"
                   @click="addClick">Добавить</v-btn>
        </template>
    </custom-dialog>
</template>

<script>
    import CustomDialog from "../customcomponents/CustomDialog";
    import {validationNameRules, validationPasswordRules} from "../../utils/ValidatorUtils";

    export default {
        name: "UserAdd",
        components: {CustomDialog},

        data() {
            return {
                valid: false,

                username: '',
                password: '',
                password2: '',
                authorities: [],

                loading: false,

                nameRules: validationNameRules(),
                passwordRules: validationPasswordRules(),

                password2rules: [
                    v => v === this.password || 'Пароли не совпадают.'
                ],
            }
        },

        computed: {
        },

        methods: {
            addClick: function () {
                if (!this.$refs["form"].validate())
                    return;

                this.loading = true;
                let mainDialog = this.$refs['main-dialog'];
                this.$store.dispatch('users/add',
                    {
                        username: this.username,
                        password: this.password,
                        authorities: this.authorities
                    })
                    .then(() => {
                        mainDialog.closeDialog();
                    })
                    .catch(() => {
                        this.loading = false;
                        mainDialog.showAlert();
                    });
            },

            setStartState: function () {
                this.$refs["form"].reset();
                this.loading = false;
            },

            // TODO сделать проверку, что такого пользователя нет
            // getNameRules: function () {
            //     let nameRules = validationNameRules();
            //     let usernameExists = this.isUsernameExists;
            //     nameRules.push(
            //         v => !usernameExists(v) || 'Пользователь с таким именем уже существует.'
            //     );
            //     return nameRules;
            // },

            // ...mapGetters('users', [
            //     'isUsernameExists'
            // ])
        }
    }
</script>

<style scoped>

</style>